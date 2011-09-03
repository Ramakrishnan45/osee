/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.server.admin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.osee.framework.branch.management.ExportOptions;
import org.eclipse.osee.framework.branch.management.ImportOptions;
import org.eclipse.osee.framework.branch.management.purge.BranchOperation;
import org.eclipse.osee.framework.branch.management.purge.DeletedBranchProvider;
import org.eclipse.osee.framework.branch.management.purge.IBranchOperationFactory;
import org.eclipse.osee.framework.branch.management.purge.IBranchesProvider;
import org.eclipse.osee.framework.branch.management.purge.MultiBranchProvider;
import org.eclipse.osee.framework.branch.management.purge.PurgeBranchOperationFactory;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.BranchFilter;
import org.eclipse.osee.framework.core.operation.CommandInterpreterLogger;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.MutexSchedulingRule;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.server.admin.branch.BranchExportOperation;
import org.eclipse.osee.framework.server.admin.branch.BranchImportOperation;
import org.eclipse.osee.framework.server.admin.branch.ExchangeIntegrityOperation;
import org.eclipse.osee.framework.server.admin.internal.Activator;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

/**
 * @author Roberto E. Escobar
 */
public class BranchManagementCommandProvider implements CommandProvider {
   private final ISchedulingRule branchMutex = new MutexSchedulingRule();

   public Job _export_branch(CommandInterpreter ci) {
      PropertyStore propertyStore = new PropertyStore();
      String exportFileName = null;
      boolean includeArchivedBranches = false;
      boolean excludeBranchIds = false;
      List<Integer> branchIds = new ArrayList<Integer>();
      exportFileName = ci.nextArgument();

      for (String arg = ci.nextArgument(); Strings.isValid(arg); arg = ci.nextArgument()) {
         if (arg.equals("-includeArchivedBranches")) {
            includeArchivedBranches = true;
         } else if (arg.equals("-excludeBranchIds")) {
            excludeBranchIds = true;
         } else if (arg.equals("-compress")) {
            propertyStore.put(ExportOptions.COMPRESS.name(), true);
         } else if (arg.equals("-minTx")) {
            arg = ci.nextArgument();
            if (Strings.isValid(arg)) {
               propertyStore.put(ExportOptions.MIN_TXS.name(), arg);
            }
         } else if (arg.equals("-maxTx")) {
            arg = ci.nextArgument();
            if (Strings.isValid(arg)) {
               propertyStore.put(ExportOptions.MAX_TXS.name(), arg);
            }
         } else {
            branchIds.add(new Integer(arg));
         }
      }

      OperationLogger logger = new CommandInterpreterLogger(ci);
      IOperation op =
         new BranchExportOperation(logger, propertyStore, exportFileName, includeArchivedBranches, branchIds,
            excludeBranchIds);
      return Operations.executeAsJob(op, false, Job.LONG, new JobStatusListener(logger), branchMutex);
   }

   public Job _import_branch(CommandInterpreter ci) {
      PropertyStore propertyStore = new PropertyStore();
      String arg = null;
      int count = 0;

      List<Integer> branchIds = new ArrayList<Integer>();
      List<String> importFiles = new ArrayList<String>();
      do {
         arg = ci.nextArgument();
         if (Strings.isValid(arg)) {
            if (arg.equals("-excludeBaselineTxs")) {
               propertyStore.put(ImportOptions.EXCLUDE_BASELINE_TXS.name(), true);
            } else if (arg.equals("-clean")) {
               propertyStore.put(ImportOptions.CLEAN_BEFORE_IMPORT.name(), true);
            } else if (arg.equals("-allAsRootBranches")) {
               propertyStore.put(ImportOptions.ALL_AS_ROOT_BRANCHES.name(), true);
            } else if (arg.equals("-minTx")) {
               arg = ci.nextArgument();
               if (Strings.isValid(arg)) {
                  propertyStore.put(ImportOptions.MIN_TXS.name(), arg);
               }
               count++;
            } else if (arg.equals("-maxTx")) {
               arg = ci.nextArgument();
               if (Strings.isValid(arg)) {
                  propertyStore.put(ImportOptions.MAX_TXS.name(), arg);
               }
               count++;
            } else if (count == 0 && !arg.startsWith("-")) {
               importFiles.add(arg);
            } else {
               branchIds.add(new Integer(arg));
            }
            count++;
         }
      } while (Strings.isValid(arg));

      final OperationLogger logger = new CommandInterpreterLogger(ci);
      IOperation op = new BranchImportOperation(logger, propertyStore, importFiles, branchIds);
      return Operations.executeAsJob(op, false, Job.LONG, new JobStatusListener(logger), branchMutex);
   }

   public Job _check_exchange(CommandInterpreter ci) throws OseeArgumentException {
      String arg = ci.nextArgument();
      ArrayList<String> importFiles = new ArrayList<String>();
      if (Strings.isValid(arg) && !arg.startsWith("-")) {
         importFiles.add(arg);
      } else {
         throw new OseeArgumentException("File to check was not specified");
      }

      OperationLogger logger = new CommandInterpreterLogger(ci);
      IOperation op = new ExchangeIntegrityOperation(logger, importFiles);
      return Operations.executeAsJob(op, false, Job.LONG, new JobStatusListener(logger), branchMutex);
   }

   public Job _purge_deleted_branches(CommandInterpreter ci) {
      OperationLogger logger = new CommandInterpreterLogger(ci);
      BranchCache branchCache = Activator.getOseeCachingService().getBranchCache();
      IBranchesProvider provider = new DeletedBranchProvider(branchCache);

      return internalPurgeBranch(logger, branchCache, provider);
   }

   public Job _purge_branch(CommandInterpreter ci) throws OseeCoreException {
      OperationLogger logger = new CommandInterpreterLogger(ci);
      String arg = ci.nextArgument();
      boolean recursive = false;
      Set<String> guids = new HashSet<String>();

      while (Strings.isValid(arg)) {
         if (arg.equals("-recursive")) {
            recursive = true;
         } else {
            guids.add(arg);
         }
         arg = ci.nextArgument();
      }

      Set<Branch> branches = new HashSet<Branch>();
      BranchCache branchCache = Activator.getOseeCachingService().getBranchCache();
      for (String guid : guids) {
         branches.add(branchCache.getByGuid(guid));
      }

      BranchFilter filter = new BranchFilter();
      filter.setNegatedBranchTypes(BranchType.BASELINE);

      IBranchesProvider provider = new MultiBranchProvider(recursive, branches, filter);
      return internalPurgeBranch(logger, branchCache, provider);
   }

   private Job internalPurgeBranch(OperationLogger logger, BranchCache branchCache, IBranchesProvider provider) {
      IOseeDatabaseService databaseService = Activator.getOseeDatabaseService();
      IBranchOperationFactory factory = new PurgeBranchOperationFactory(logger, branchCache, databaseService);

      IOperation operation = new BranchOperation(logger, factory, provider);
      return Operations.executeAsJob(operation, false);
   }

   @Override
   public String getHelp() {
      StringBuilder sb = new StringBuilder();
      sb.append("\n---OSEE Branch Commands---\n");
      sb.append("\texport_branch <exchangeFileName> [-compress] [-minTx <value>] [-maxTx <value>] [-includeArchivedBranches] -excludeBranchIds [<branchId>]+ - export a specific set of branches into an exchange zip file.\n");
      sb.append("\timport_branch <exchangeFileName> [-exclude_baseline_txs] [-allAsRootBranches] [-minTx <value>] [-maxTx <value>] [-clean] [<branchId>]+ - import a specific set of branches from an exchange zip file.\n");
      sb.append("\tcheck_exchange <exchangeFileName> - checks an exchange file to ensure data integrity\n");
      sb.append("\tpurge_deleted_branches - permenatly remove all branches that are both archived and deleted \n");
      sb.append("\tpurge_branch <guids...> [-recursive] - removes branches defined by guids, if recursive all its children excluding baseline branches are removed\n");
      return sb.toString();
   }

   private final class JobStatusListener extends JobChangeAdapter {

      private final OperationLogger logger;
      private long startTime;

      public JobStatusListener(OperationLogger logger) {
         super();
         this.logger = logger;
         this.startTime = 0L;
      }

      @Override
      public void aboutToRun(IJobChangeEvent event) {
         super.aboutToRun(event);
         startTime = System.currentTimeMillis();
         logger.logf("Starting [%s]", event.getJob().getName());
      }

      private String toStatus(IStatus status) {
         boolean addDetails = true;
         StringBuilder builder = new StringBuilder();
         switch (status.getSeverity()) {
            case IStatus.OK:
               addDetails = false;
               builder.append("[Ok]");
               break;
            case IStatus.CANCEL:
               addDetails = false;
               builder.append("[Cancelled]");
               break;
            case IStatus.INFO:
               builder.append("[Info]");
               break;
            case IStatus.ERROR:
               builder.append("[Error]");
               break;
            case IStatus.WARNING:
               builder.append("[Warning]");
               break;
            default:
               builder.append("[Unknown]");
               break;
         }

         if (addDetails) {
            builder.append("\n");
            builder.append(status.getMessage());
            builder.append("\n");
            Throwable th = status.getException();
            if (th != null) {
               builder.append(th.getLocalizedMessage());
            }
         }
         return builder.toString();
      }

      @Override
      public void done(IJobChangeEvent event) {
         super.done(event);
         logger.logf("Completed [%s] in [%s] - status:%s", event.getJob().getName(), Lib.getElapseString(startTime),
            toStatus(event.getResult()));
      }
   }
}