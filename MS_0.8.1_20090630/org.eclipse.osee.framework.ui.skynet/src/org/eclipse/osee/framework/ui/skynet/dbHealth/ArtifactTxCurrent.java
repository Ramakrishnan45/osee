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

import java.util.HashSet;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.AHTML;

/**
 * @author Theron Virgin
 */
public class ArtifactTxCurrent extends DatabaseHealthOperation {
   private HashSet<LocalTxData> multipleSet = null;
   private HashSet<Pair<Integer, Integer>> noneSet = null;

   public ArtifactTxCurrent() {
      super("Tx_Current Artifact Errors");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthOperation#doHealthCheck(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected void doHealthCheck(IProgressMonitor monitor) throws Exception {
      String[] columnHeaders = new String[] {"Count", "Art id", "Branch id"};

      if (isShowDetailsEnabled()) {
         appendToDetails(AHTML.beginMultiColumnTable(100, 1));
         appendToDetails(AHTML.addHeaderRowMultiColumnTable(columnHeaders));
         appendToDetails(AHTML.addRowSpanMultiColumnTable("Artifacts with no tx_current set", columnHeaders.length));
      }
      monitor.worked(calculateWork(0.10));

      if (!isFixOperationEnabled() || noneSet == null) {
         noneSet = HealthHelper.getNoTxCurrentSet("art_id", "osee_artifact_version", getSummary(), " Artifacts");
      }
      checkForCancelledStatus(monitor);
      monitor.worked(calculateWork(0.10));

      if (isShowDetailsEnabled()) {
         HealthHelper.dumpDataNone(getDetailedReport(), noneSet);
         columnHeaders = new String[] {"Count", "Art id", "Branch id", "Num TX_Currents"};
         appendToDetails(AHTML.addHeaderRowMultiColumnTable(columnHeaders));
         appendToDetails(AHTML.addRowSpanMultiColumnTable("Artifacts with multiple tx_currents set",
               columnHeaders.length));
      }
      checkForCancelledStatus(monitor);
      monitor.worked(calculateWork(0.10));

      if (!isFixOperationEnabled() || multipleSet == null) {
         //Multiple TX Currents Set
         multipleSet =
               HealthHelper.getMultipleTxCurrentSet("art_id", "osee_artifact_version", getSummary(), " Artifacts");
      }
      checkForCancelledStatus(monitor);
      monitor.worked(calculateWork(0.10));

      if (isShowDetailsEnabled()) {
         HealthHelper.dumpDataMultiple(getDetailedReport(), multipleSet);
      }

      checkForCancelledStatus(monitor);
      monitor.worked(calculateWork(0.10));

      int multipleTxCurrentCount = multipleSet != null ? multipleSet.size() : 0;
      int noTxCurrentCount = noneSet != null ? noneSet.size() : 0;
      setItemsToFix(noTxCurrentCount + multipleTxCurrentCount);

      if (isFixOperationEnabled()) {
         /** Duplicate TX_current Cleanup **/
         monitor.subTask("Cleaning up multiple Tx_currents");
         HealthHelper.cleanMultipleTxCurrent("art_id", "osee_artifact_version", getSummary(), multipleSet);
         monitor.worked(calculateWork(0.25));
         monitor.subTask("Cleaning up no Tx_currents");
         HealthHelper.cleanNoTxCurrent("art_id", "osee_artifact_version", getSummary(), noneSet);
         multipleSet = null;
         noneSet = null;
      } else {
         monitor.worked(calculateWork(0.25));
      }
      monitor.worked(calculateWork(0.20));

      if (isShowDetailsEnabled()) {
         appendToDetails(AHTML.endMultiColumnTable());
      }
      monitor.worked(calculateWork(0.05));
   }
}
