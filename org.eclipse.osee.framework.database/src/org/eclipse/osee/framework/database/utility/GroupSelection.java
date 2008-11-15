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

package org.eclipse.osee.framework.database.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.database.DatabaseActivator;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;

/**
 * @author Andrew M. Finkbeiner
 */
public class GroupSelection {
   private static final GroupSelection instance = new GroupSelection();
   private final Map<String, List<String>> initGroups = new HashMap<String, List<String>>();

   /**
    * @param initGroups
    */
   private GroupSelection() {
      super();
      populateDbInitChoices();
   }

   public static GroupSelection getInstance() {
      return instance;
   }

   private void populateDbInitChoices() {
      List<IConfigurationElement> elements =
            ExtensionPoints.getExtensionElements(DatabaseActivator.getInstance(), "AddDbInitChoice", "dbInitChoice");

      for (IConfigurationElement element : elements) {
         String choiceClass = element.getAttribute("classname");
         try {
            IAddDbInitChoice choice =
                  (IAddDbInitChoice) Platform.getBundle(element.getContributor().getName()).loadClass(choiceClass).newInstance();
            choice.addDbInitChoice(this);
         } catch (InstantiationException ex) {
            OseeLog.log(DatabaseActivator.class, Level.SEVERE, ex);
         } catch (IllegalAccessException ex) {
            OseeLog.log(DatabaseActivator.class, Level.SEVERE, ex);
         } catch (ClassNotFoundException ex) {
            OseeLog.log(DatabaseActivator.class, Level.SEVERE, ex);
         }
      }
   }

   private void addCommonChoices(List<String> dbInitTasks, boolean bareBones) {
      List<String> initTasks = new ArrayList<String>();
      initTasks.add("org.eclipse.osee.framework.skynet.core.SkynetDbInit");
      dbInitTasks.addAll(0, initTasks);
      dbInitTasks.add("org.eclipse.osee.framework.skynet.core.SkynetDbBranchDataImport");
      dbInitTasks.add("org.eclipse.osee.framework.database.PostDbInitializationProcess");
   }

   public void addChoice(String listName, List<String> dbInitTasks, boolean bareBones) {
      addCommonChoices(dbInitTasks, bareBones);
      initGroups.put(listName, dbInitTasks);
   }

   //   /**
   //    * Call to get DB initialization Tasks from choice made by User
   //    * 
   //    * @return initialization task list
   //    */
   //   public List<String> getDbInitTasks() {
   //      String choice = null;
   //      if (initGroups.keySet().size() == 1) {
   //         String[] keys = initGroups.keySet().toArray(new String[1]);
   //         choice = keys[0];
   //      } else {
   //         List<String> choices = new ArrayList<String>(initGroups.keySet());
   //         Collections.sort(choices);
   //         int selection = -1;
   //         String configChoice = OseeProperties.getDbConfigInitChoice();
   //         if (false != Strings.isValid(configChoice)) {
   //            selection = choices.indexOf(configChoice);
   //         }
   //         if (selection <= -1) {
   //            choice = getSelectionFromUser("Select Init Group To Run.", choices);
   //         }
   //      }
   //      OseeLog.log(DatabaseActivator.class, Level.INFO, String.format("DB Config Choice Selected: [%s]", choice));
   //      return initGroups.get(choice);
   //   }

   public List<String> getChoices() {
      List<String> choices = new ArrayList<String>(initGroups.keySet());
      Collections.sort(choices);
      return choices;
   }

   public List<String> getDbInitTasksByChoiceEntry(String choice) {
      return initGroups.get(choice);
   }

   //   /**
   //    * Call get get DB initialization Tasks from specified taskId
   //    * 
   //    * @param dbInitTaskId
   //    * @return initialization task list
   //    */
   //   public List<String> getDbInitTasks(String dbInitTaskId) {
   //      populateDbInitChoices();
   //      return initGroups.get(dbInitTaskId);
   //   }
}