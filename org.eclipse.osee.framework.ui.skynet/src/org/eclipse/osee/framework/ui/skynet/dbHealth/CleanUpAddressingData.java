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
package org.eclipse.osee.framework.ui.skynet.dbHealth;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultPage.Manipulations;

/**
 * Identifies and removes addressing from the transaction table that no longer addresses other tables.
 * 
 * @author Theron Virgin
 */
public class CleanUpAddressingData extends DatabaseHealthTask {

   private static final String NOT_ADDRESSESED_GAMMAS =
         "SELECT gamma_id from osee_define_txs MINUS (SELECT gamma_id FROM osee_Define_artifact_version UNION SELECT gamma_id FROM osee_Define_attribute UNION SELECT gamma_id FROM osee_Define_rel_link)";
   private static final String NOT_ADDRESSESED_TRANSACTIONS =
         "SELECT transaction_id from osee_Define_txs MINUS SELECT transaction_id from osee_Define_tx_details";
   private static final String REMOVE_NOT_ADDRESSED_GAMMAS = "DELETE FROM osee_define_txs WHERE gamma_id = ?";
   private static final String REMOVE_NOT_ADDRESSED_TRANSACTIONS =
         "DELETE FROM osee_define_txs WHERE transaction_id = ?";

   private static final String[] COLUMN_HEADER = {"Gamma Id", "Transaction Id"};
   private static final int GAMMA = 0;
   private static final int TRANSACTION = 1;

   private Set<Integer> gammas = null;
   private Set<Integer> transactions = null;

   @Override
   public String getFixTaskName() {
      return "Fix TXS Entries with no Backing Data";
   }

   @Override
   public String getVerifyTaskName() {
      return "Check for TXS Entries with no Backing Data";
   }

   @Override
   public void run(BlamVariableMap variableMap, IProgressMonitor monitor, Operation operation, StringBuilder builder, boolean showDetails) throws Exception {
      boolean fix = operation == Operation.Fix;
      boolean verify = !fix;
      monitor.beginTask(
            fix ? "Deleting TXS Entries with No Backing Data" : "Checking For TXS Entries with No Backing Data", 100);
      if (verify) {
         gammas = null;
         transactions = null;
      }

      if (gammas == null || transactions == null) {
         gammas = new HashSet<Integer>();
         transactions = new HashSet<Integer>();
         ConnectionHandlerStatement chStmt = null;
         ResultSet resultSet = null;
         try {
            chStmt = ConnectionHandler.runPreparedQuery(NOT_ADDRESSESED_GAMMAS);
            resultSet = chStmt.getRset();
            while (resultSet.next()) {
               gammas.add(resultSet.getInt("gamma_id"));
            }
         } finally {
            DbUtil.close(chStmt);
         }
         monitor.worked(25);
         if (monitor.isCanceled()) return;
         try {
            chStmt = ConnectionHandler.runPreparedQuery(NOT_ADDRESSESED_TRANSACTIONS);
            resultSet = chStmt.getRset();
            while (resultSet.next()) {
               transactions.add(resultSet.getInt("transaction_id"));
            }
         } finally {
            DbUtil.close(chStmt);
         }
         monitor.worked(25);
      }
      if (monitor.isCanceled()) return;

      StringBuffer sbFull = new StringBuffer(AHTML.beginMultiColumnTable(100, 1));
      displayData(GAMMA, sbFull, builder, verify, gammas);
      monitor.worked(20);
      displayData(TRANSACTION, sbFull, builder, verify, transactions);
      monitor.worked(20);

      if (monitor.isCanceled()) return;

      if (fix) {
         HashSet<Object[]> insertParameters = new HashSet<Object[]>();
         for (Integer value : gammas) {
            insertParameters.add(new Object[] {value.intValue()});
         }
         ConnectionHandler.runPreparedUpdateBatch(REMOVE_NOT_ADDRESSED_GAMMAS, insertParameters);
         monitor.worked(5);
         insertParameters.clear();
         for (Integer value : transactions) {
            insertParameters.add(new Object[] {value.intValue()});
         }
         ConnectionHandler.runPreparedUpdateBatch(REMOVE_NOT_ADDRESSED_TRANSACTIONS, insertParameters);
         monitor.worked(5);
         gammas = null;
         transactions = null;
      }

      if (showDetails) {
         sbFull.append(AHTML.endMultiColumnTable());
         XResultData rd = new XResultData(SkynetActivator.getLogger());
         rd.addRaw(sbFull.toString());
         rd.report(getVerifyTaskName(), Manipulations.RAW_HTML);
      }
   }

   private void displayData(int x, StringBuffer sbFull, StringBuilder builder, boolean verify, Set<Integer> set) throws SQLException {
      int count = 0;
      sbFull.append(AHTML.addHeaderRowMultiColumnTable(new String[] {COLUMN_HEADER[x]}));
      sbFull.append(AHTML.addRowSpanMultiColumnTable(COLUMN_HEADER[x] + "'s with no backing data", 1));
      for (Integer value : set) {
         count++;
         sbFull.append(AHTML.addRowMultiColumnTable(new String[] {value.toString()}));
      }
      builder.append(verify ? "Found " : "Fixed ");
      builder.append(count);
      builder.append(" ");
      builder.append(COLUMN_HEADER[x]);
      builder.append("'s with no Backing Data\n");
   }
}
