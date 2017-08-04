/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.activity.internal;

import static org.eclipse.osee.activity.ActivityConstants.ACTIVITY_LOGGER__CLEANER_EXECUTOR_ID;
import static org.eclipse.osee.activity.ActivityConstants.ACTIVITY_LOGGER__CLEANER_KEEP_DAYS;
import static org.eclipse.osee.activity.ActivityConstants.ACTIVITY_LOGGER__ENABLED;
import static org.eclipse.osee.activity.ActivityConstants.ACTIVITY_LOGGER__EXECUTOR_ID;
import static org.eclipse.osee.activity.ActivityConstants.ACTIVITY_LOGGER__EXECUTOR_POOL_SIZE;
import static org.eclipse.osee.activity.ActivityConstants.ACTIVITY_LOGGER__STACKTRACE_LINE_COUNT;
import static org.eclipse.osee.activity.ActivityConstants.ACTIVITY_LOGGER__WRITE_RATE_IN_MILLIS;
import static org.eclipse.osee.activity.ActivityConstants.DEFAULT_ACTIVITY_LOGGER__CLEANER_KEEP_DAYS;
import static org.eclipse.osee.activity.ActivityConstants.DEFAULT_ACTIVITY_LOGGER__EXECUTOR_POOL_SIZE;
import static org.eclipse.osee.activity.ActivityConstants.DEFAULT_ACTIVITY_LOGGER__STACKTRACE_LINE_COUNT;
import static org.eclipse.osee.activity.ActivityConstants.DEFAULT_ACTIVITY_LOGGER__WRITE_RATE_IN_MILLIS;
import static org.eclipse.osee.activity.internal.ActivityUtil.captureStackTrace;
import static org.eclipse.osee.activity.internal.ActivityUtil.get;
import java.util.Calendar;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.activity.ActivityConstants;
import org.eclipse.osee.activity.ActivityStorage;
import org.eclipse.osee.activity.api.ActivityEntry;
import org.eclipse.osee.activity.api.ActivityEntryId;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.data.ActivityTypeId;
import org.eclipse.osee.framework.core.data.ActivityTypeToken;
import org.eclipse.osee.framework.core.data.CoreActivityTypes;
import org.eclipse.osee.framework.jdk.core.type.DrainingIterator;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcConstants;
import org.eclipse.osee.logger.Log;

/**
 * @author Ryan D. Brooks
 */
public class ActivityLogImpl implements ActivityLog, Callable<Void> {

   public static enum LogEntry {
      ENTRY_ID,
      PARENT_ID,
      TYPE_ID,
      ACCOUNT_ID,
      SERVER_ID,
      CLIENT_ID,
      START_TIME,
      DURATION,
      STATUS,
      MESSAGE_ARGS;

      public static final Long SENTINEL = -1L;

      Long from(Object[] entry) {
         Object obj = entry[ordinal()];
         if (obj instanceof Long) {
            return (Long) obj;
         }
         if (obj instanceof Id) {
            return ((Id) obj).getId();
         }
         throw new OseeArgumentException(
            "LogEntryIndex.from called with agrgument of unsupported type " + obj.getClass());
      }
   };

   private final ConcurrentHashMap<Long, ActivityTypeToken> types = new ConcurrentHashMap<>(30);
   private final ConcurrentHashMap<Long, Object[]> newEntities = new ConcurrentHashMap<>();
   private final ConcurrentHashMap<Long, Object[]> updatedEntities = new ConcurrentHashMap<>();
   private Log logger;
   private ExecutorAdmin executorAdmin;
   private ActivityStorage storage;

   private ActivityMonitorImpl activityMonitor;
   private volatile long freshnessMillis;
   private volatile int exceptionLineCount;
   private volatile int executorPoolSize;
   private volatile long lastFlushTime;
   private volatile int cleanerKeepDays;
   private volatile boolean enabled = ActivityConstants.DEFAULT_ACTIVITY_LOGGER__ENABLED;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setActivityStorage(ActivityStorage storage) {
      this.storage = storage;
   }

   public void setExecutorAdmin(ExecutorAdmin executorAdmin) {
      this.executorAdmin = executorAdmin;
   }

   public void start(Map<String, Object> properties) throws Exception {
      for (ActivityTypeToken type : CoreActivityTypes.getTypes()) {
         types.put(type.getId(), type);
      }
      activityMonitor = new ActivityMonitorImpl();
      update(properties);
   }

   public void stop() {
      flush(true);
      try {
         executorAdmin.shutdown(ACTIVITY_LOGGER__EXECUTOR_ID);
      } catch (Throwable th) {
         logger.error(th, "Error shutting down executor [%s]", ACTIVITY_LOGGER__EXECUTOR_ID);
      }
   }

   @Override
   public ActivityEntry getEntry(ActivityEntryId entryId) {
      return storage.getEntry(entryId);
   }

   public void update(Map<String, Object> properties) {
      //@formatter:off
      freshnessMillis = get(properties, ACTIVITY_LOGGER__WRITE_RATE_IN_MILLIS, DEFAULT_ACTIVITY_LOGGER__WRITE_RATE_IN_MILLIS);
      exceptionLineCount = get(properties, ACTIVITY_LOGGER__STACKTRACE_LINE_COUNT, DEFAULT_ACTIVITY_LOGGER__STACKTRACE_LINE_COUNT);
      int newExecutorPoolSize = get(properties, ACTIVITY_LOGGER__EXECUTOR_POOL_SIZE, DEFAULT_ACTIVITY_LOGGER__EXECUTOR_POOL_SIZE);
      String value = (String)properties.get(ACTIVITY_LOGGER__ENABLED);
      int newCleanerKeepDays = get(properties, ACTIVITY_LOGGER__CLEANER_KEEP_DAYS, DEFAULT_ACTIVITY_LOGGER__CLEANER_KEEP_DAYS);
      if (Strings.isValid(value)) {
         enabled = Boolean.valueOf(value);
      }
      //@formatter:on

      if (newExecutorPoolSize != executorPoolSize) {
         executorPoolSize = newExecutorPoolSize;
         try {
            executorAdmin.shutdown(ACTIVITY_LOGGER__EXECUTOR_ID);
         } catch (Throwable th) {
            logger.error(th, "Error shutting down executor [%s]", ACTIVITY_LOGGER__EXECUTOR_ID);
         } finally {
            try {
               executorAdmin.createFixedPoolExecutor(ACTIVITY_LOGGER__EXECUTOR_ID, executorPoolSize);
            } catch (Throwable th) {
               logger.error(th, "Error creating new executor for [%s]", ACTIVITY_LOGGER__EXECUTOR_ID);
            }
         }
      }
      if (newCleanerKeepDays != cleanerKeepDays) {
         cleanerKeepDays = newCleanerKeepDays;
         setupCleaner();
      }
   }

   private void setupCleaner() {
      Callable<Void> cleaner = new Callable<Void>() {

         @Override
         public Void call() throws Exception {
            storage.cleanEntries(cleanerKeepDays);
            return null;
         }
      };

      // randomly pick a start time around midnight
      Random random = new Random();
      Calendar start = Calendar.getInstance();
      start.set(Calendar.HOUR_OF_DAY, random.nextInt(4));
      start.set(Calendar.MINUTE, random.nextInt(180));
      int day = start.get(Calendar.DAY_OF_YEAR);
      start.set(Calendar.DAY_OF_YEAR, day + 1);

      long startMil = start.getTimeInMillis();
      long curMil = System.currentTimeMillis();
      long startAfter = TimeUnit.MILLISECONDS.toMinutes(startMil - curMil);

      // run once a day
      executorAdmin.shutdown(ACTIVITY_LOGGER__CLEANER_EXECUTOR_ID);
      executorAdmin.scheduleAtFixedRate(ACTIVITY_LOGGER__CLEANER_EXECUTOR_ID, cleaner, startAfter, 60 * 24,
         TimeUnit.MINUTES);
   }

   @Override
   public Long createEntry(ActivityTypeToken type, Object... messageArgs) {
      return createEntry(type, COMPLETE_STATUS, messageArgs);
   }

   @Override
   public Long createUpdateableEntry(ActivityTypeToken type, Object... messageArgs) {
      return createEntry(type, INITIAL_STATUS, messageArgs);
   }

   @Override
   public Long createEntry(ActivityTypeToken type, Integer status, Object... messageArgs) {
      if (enabled) {
         Object[] threadRootEntry = activityMonitor.getThreadRootEntry();
         // Should never have a null rootEntry, but still want to log message with sentinel
         Long entryId = threadRootEntry == null ? LogEntry.SENTINEL : LogEntry.ENTRY_ID.from(threadRootEntry);
         return createEntry(type, entryId, status, messageArgs);
      }
      return 0L;
   }

   @Override
   public Long createEntry(ActivityTypeToken typeId, Long parentId, Integer status, Object... messageArgs) {
      if (enabled) {
         Object[] rootEntry = activityMonitor.getThreadRootEntry();
         // Should never have a null rootEntry, but still want to log message with sentinels
         Long accountId = rootEntry == null ? LogEntry.SENTINEL : LogEntry.ACCOUNT_ID.from(rootEntry);
         Long serverId = rootEntry == null ? LogEntry.SENTINEL : LogEntry.SERVER_ID.from(rootEntry);
         Long clientId = rootEntry == null ? LogEntry.SENTINEL : LogEntry.CLIENT_ID.from(rootEntry);
         Object[] entry =
            createEntry(parentId, typeId, accountId, serverId, clientId, computeDuration(), status, messageArgs);
         return LogEntry.ENTRY_ID.from(entry);
      }
      return 0L;
   }

   @Override
   public Long createEntry(Long accountId, Long clientId, ActivityTypeToken typeId, Long parentId, Integer status, String... messageArgs) {
      Object[] rootEntry = activityMonitor.getThreadRootEntry();
      Long serverId = LogEntry.SERVER_ID.from(rootEntry);
      Object[] entry = createEntry(parentId, typeId, accountId, serverId, clientId, computeDuration(), status,
         (Object[]) messageArgs);
      return LogEntry.ENTRY_ID.from(entry);
   }

   private Object[] createEntry(Long parentId, ActivityTypeToken type, Long accountId, Long serverId, Long clientId, Long duration, Integer status, Object... messageArgs) {
      Object[] entry;
      Long entryId = Lib.generateUuid();
      Long startTime = System.currentTimeMillis();

      String msg;
      String fullMsg = null;
      try {
         String messageFormat = type.getMessageFormat();
         if (Strings.isValid(messageFormat)) {
            fullMsg = String.format(messageFormat, messageArgs);
         } else {
            fullMsg = Collections.toString("\n", messageArgs);
         }

         msg = fullMsg.substring(0, Math.min(fullMsg.length(), JdbcConstants.JDBC__MAX_VARCHAR_LENGTH));
      } catch (Throwable th) {
         msg = th.toString();
         logger.error(th, "Error ActivityLog.createEntry");
      }

      // this is the parent entry so it must be inserted first (because the entry writing is asynchronous
      entry = new Object[] {entryId, parentId, type, accountId, serverId, clientId, startTime, duration, status, msg};
      newEntities.put(entryId, entry);

      if (fullMsg != null && fullMsg.length() > JdbcConstants.JDBC__MAX_VARCHAR_LENGTH) {
         Long parentCursor = entryId;
         for (int i = JdbcConstants.JDBC__MAX_VARCHAR_LENGTH; i < fullMsg.length(); i +=
            JdbcConstants.JDBC__MAX_VARCHAR_LENGTH) {
            Long continueEntryId = Lib.generateUuid();
            Object[] continueEntry = new Object[] {
               continueEntryId,
               parentCursor,
               CoreActivityTypes.MSG_CONTINUATION,
               accountId,
               serverId,
               clientId,
               startTime,
               duration,
               status,
               fullMsg.substring(i, Math.min(fullMsg.length(), i + JdbcConstants.JDBC__MAX_VARCHAR_LENGTH))};
            newEntities.put(continueEntryId, continueEntry);
            parentCursor = continueEntryId;
         }
      }
      flush(false);

      return entry;
   }

   @Override
   public Long createThrowableEntry(ActivityTypeToken type, Throwable throwable) {
      Long entryId = -1L;
      if (enabled) {
         try {
            String message = captureStackTrace(throwable, exceptionLineCount);
            entryId = createEntry(type, ABNORMALLY_ENDED_STATUS, message);
         } catch (Throwable th) {
            logger.error(th, "logging failed in ActivityLogImpl.createThrowableEntry");
         }
      }
      return entryId;
   }

   @Override
   public boolean updateEntry(Long entryId, Integer status) {
      boolean modified = false;
      if (enabled) {
         try {
            if (!updateIfNew(entryId, status)) {
               Object[] data = updatedEntities.get(entryId);
               if (data == null || !(data.length >= LogEntry.STATUS.ordinal())) {
                  addUpdatedEntryToMap(entryId, status);
               } else {
                  data[LogEntry.STATUS.ordinal()] = status;
                  if (!updatedEntities.containsKey(entryId)) {
                     addUpdatedEntryToMap(entryId, status);
                  }
               }
               modified = true;
            }
         } catch (Throwable th) {
            logger.error(th, "Error in ActivityLog.updateEntry");
         }
      }
      return modified;
   }

   private void addUpdatedEntryToMap(Long entryId, Integer status) {
      updatedEntities.put(entryId, new Object[] {status, computeDuration(), entryId});
   }

   private Long computeDuration() {
      long timeOfUpdate = System.currentTimeMillis();
      Object[] rootEntry = activityMonitor.getThreadRootEntry();
      return timeOfUpdate = rootEntry == null ? LogEntry.SENTINEL : timeOfUpdate - LogEntry.START_TIME.from(rootEntry);
   }

   /**
    * If the status has changed for an entry that has not yet been written to the datastore, update in memory and return
    * true if it has not yet been drained and written to the datastore
    */
   private boolean updateIfNew(Long entryId, Integer status) {
      Object[] data = newEntities.get(entryId);
      if (data == null) {
         return false;
      } else {
         data[LogEntry.STATUS.ordinal()] = status;
         data[LogEntry.DURATION.ordinal()] = computeDuration();
         return newEntities.containsKey(entryId);
      }
   }

   @Override
   public Void call() {
      if (enabled) {
         if (!newEntities.isEmpty()) {
            try {
               storage.addEntries(new DrainingIterator<Object[]>(newEntities.values().iterator()));
            } catch (Throwable ex) {
               logger.error(ex, "Exception while storing updates to the activity log");
            }
         }
         if (!updatedEntities.isEmpty()) {
            try {
               storage.updateEntries(new DrainingIterator<Object[]>(updatedEntities.values().iterator()));
            } catch (Throwable ex) {
               logger.error(ex, "Exception while storing updates to the activity log");
            }
         }
      } else {
         newEntities.clear();
         updatedEntities.clear();
      }
      return null;
   }

   private void flush(boolean force) {
      long currentTime = System.currentTimeMillis();
      if (force || currentTime - lastFlushTime > freshnessMillis) {
         try {
            executorAdmin.schedule(ACTIVITY_LOGGER__EXECUTOR_ID, this);
         } catch (Exception ex) {
            logger.error(ex, "Error scheduling activity log callable");
         } finally {
            lastFlushTime = currentTime;
         }
      }
   }

   @Override
   public void completeEntry(Long entryId) {
      updateEntry(entryId, COMPLETE_STATUS);
   }

   @Override
   public void endEntryAbnormally(Long entryId) {
      updateEntry(entryId, ABNORMALLY_ENDED_STATUS);
   }

   @Override
   public void endEntryAbnormally(Long entryId, Integer status) {
      if (status > COMPLETE_STATUS) {
         updateEntry(entryId, status);
      } else {
         endEntryAbnormally(entryId);
      }
   }

   @Override
   public Long createActivityThread(ActivityTypeToken type, Long accountId, Long serverId, Long clientId, Object... messageArgs) {
      return createActivityThread(ActivityConstants.ROOT_ENTRY_ID, type, accountId, serverId, clientId, messageArgs);
   }

   @Override
   public Long createActivityThread(Long parentId, ActivityTypeToken type, Long accountId, Long serverId, Long clientId, Object... messageArgs) {
      Object[] entry = createEntry(parentId, type, accountId, serverId, clientId, 0L, 0, messageArgs);
      activityMonitor.addActivityThread(entry);
      return LogEntry.ENTRY_ID.from(entry);
   }

   @Override
   public ActivityTypeToken getActivityType(ActivityTypeId typeId) {
      ActivityTypeToken type = types.get(typeId);
      if (type == null) {
         type = storage.getActivityType(typeId);
         types.put(type.getId(), type);
      }
      return type;
   }

   @Override
   public boolean isEnabled() {
      return enabled;
   }

   @Override
   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   @Override
   public ActivityTypeToken createIfAbsent(ActivityTypeToken token) {
      ActivityTypeToken type = types.get(token);
      if (type == null) {
         type = storage.createIfAbsent(token);
         types.put(type.getId(), type);
      }
      return type;
   }
}