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
package org.eclipse.osee.framework.db.connection.core.query;

import java.sql.SQLException;
import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.Activator;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.core.RsetProcessor;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Robert A. Fisher
 */
public class Query {

   /**
    * Builds a collection of items from an SQL statement from the basic DBConnection.
    * 
    * @param collection The collection to add the objects to.
    * @param sql The SQL statement to use to acquire a ResultSet.
    * @param processor The RsetProcessor used for providing and validating items.
    * @param <A> The type of object being placed into the collection.
    */
   @Deprecated
   // all code that uses this is also Deprecated
   public static <A extends Object> void acquireCollection(Collection<A> collection, RsetProcessor<A> processor, String sql, Object... data) throws OseeDataStoreException {
      A item;
      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt = ConnectionHandler.runPreparedQuery(sql, data);
         while (chStmt.next()) {
            try {
               item = processor.process(chStmt.getRset());
               if (processor.validate(item)) collection.add(item);
            } catch (IllegalStateException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, "Encountered Exception when trying to acquire a collection.",
                     ex);
            }
         }
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      } finally {
         ConnectionHandler.close(chStmt);
      }
   }
}