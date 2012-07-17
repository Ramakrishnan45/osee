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
package org.eclipse.osee.framework.database.internal.core;

import java.util.TimerTask;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Ryan D. Brooks
 */
public class StaleConnectionCloser extends TimerTask {
   private final OseeConnectionPoolImpl connectionPool;

   public StaleConnectionCloser(OseeConnectionPoolImpl connectionPool) {
      super();
      this.connectionPool = connectionPool;
   }

   @Override
   public void run() {
      try {
         connectionPool.releaseUneededConnections();
      } catch (OseeCoreException ex) {
         OseeLog.log(StaleConnectionCloser.class, Level.SEVERE, ex);
      }
   }
}