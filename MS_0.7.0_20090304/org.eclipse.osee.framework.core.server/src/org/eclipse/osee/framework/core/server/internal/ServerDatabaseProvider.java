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
package org.eclipse.osee.framework.core.server.internal;

import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.DatabaseInfoManager;
import org.eclipse.osee.framework.db.connection.IApplicationDatabaseInfoProvider;
import org.eclipse.osee.framework.db.connection.IDatabaseInfo;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.logging.BaseStatus;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class ServerDatabaseProvider implements IApplicationDatabaseInfoProvider {

   private static final String NAME = "Server Data Source";

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IApplicationDatabaseInfoProvider#getDatabaseInfo()
    */
   @Override
   public IDatabaseInfo getDatabaseInfo() throws OseeDataStoreException {
      IDatabaseInfo databaseInfo = null;
      databaseInfo = DatabaseInfoManager.getDefault();
      OseeLog.reportStatus(new BaseStatus(NAME, Level.INFO, "%s [%s as %s]", databaseInfo.getDriver(),
            databaseInfo.getDatabaseName(), databaseInfo.getDatabaseLoginName()));
      return databaseInfo;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.IApplicationDatabaseInfoProvider#getPriority()
    */
   @Override
   public int getPriority() {
      return Integer.MAX_VALUE;
   }

}
