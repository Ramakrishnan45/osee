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

package org.eclipse.osee.ats;

import java.util.logging.Level;
import org.eclipse.osee.ats.util.AtsNotifyUsers;
import org.eclipse.osee.ats.util.AtsPreSaveCacheRemoteEventHandler;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.OseeGroup;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.dbinit.SkynetDbInit;
import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;
import org.eclipse.swt.graphics.Color;

/**
 * The main plugin class to be used in the desktop.
 * 
 * @author Donald G. Dunne
 */
public class AtsPlugin extends OseeUiActivator {
   private static AtsPlugin pluginInstance;
   public static ActionDebug debug = new ActionDebug(false, "AtsPlugin");
   public static final String PLUGIN_ID = "org.eclipse.osee.ats";
   private static boolean emailEnabled = true;
   public static Color ACTIVE_COLOR = new Color(null, 206, 212, 241);
   private static OseeGroup atsAdminGroup = null;
   private static boolean goalEnabled = false;

   /**
    * The constructor.
    */
   public AtsPlugin() {
      super();
      pluginInstance = this;
      AtsPreSaveCacheRemoteEventHandler.getInstance();
      AtsNotifyUsers.getInstance();
   }

   public static boolean isEmailEnabled() {
      return emailEnabled;
   }

   public static void setEmailEnabled(boolean enabled) {
      if (!SkynetDbInit.isDbInit()) System.out.println("Email " + (enabled ? "Enabled" : "Disabled"));
      emailEnabled = enabled;
   }

   public static boolean isProductionDb() throws OseeCoreException {
      return ClientSessionManager.isProductionDataStore();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jdk.core.util.plugin.OseePlugin#getPluginName()
    */
   @Override
   protected String getPluginName() {
      return PLUGIN_ID;
   }

   /**
    * Returns the shared instance.
    */
   public static AtsPlugin getInstance() {
      return pluginInstance;
   }

   public static boolean isAtsAdmin() {
      try {
         return getAtsAdminGroup().isCurrentUserMember();
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return false;
      }
   }

   public static OseeGroup getAtsAdminGroup() {
      if (atsAdminGroup == null) {
         atsAdminGroup = new OseeGroup("AtsAdmin");
      }
      return atsAdminGroup;
   }

   public static Branch getAtsBranch() throws OseeCoreException {
      return BranchManager.getCommonBranch();
   }

   /**
    * @return the enableGoal
    */
   public static boolean isGoalEnabled() {
      return goalEnabled;
   }

}
