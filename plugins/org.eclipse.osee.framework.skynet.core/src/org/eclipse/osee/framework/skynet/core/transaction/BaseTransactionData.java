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
package org.eclipse.osee.framework.skynet.core.transaction;

import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.database.core.OseeSql;
import org.eclipse.osee.framework.skynet.core.event2.ArtifactEvent;

/**
 * @author Jeff C. Phillips
 * @author Roberto E. Escobar
 */
public abstract class BaseTransactionData {
   private static final String INSERT_INTO_TRANSACTION_TABLE =
      "INSERT INTO osee_txs (transaction_id, gamma_id, mod_type, tx_current, branch_id) VALUES (?, ?, ?, ?, ?)";

   private static final int PRIME_NUMBER = 37;
   private final int itemId;
   private ModificationType modificationType;
   private Integer gammaId;

   public BaseTransactionData(int itemId, ModificationType modificationType) {
      this.modificationType = modificationType;
      this.itemId = itemId;
   }

   protected boolean useExistingBackingData() {
      switch (modificationType) {
         case ARTIFACT_DELETED:
         case DELETED:
         case INTRODUCED:
            return true;
      }
      return false;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof BaseTransactionData) {
         BaseTransactionData data = (BaseTransactionData) obj;
         return data.itemId == this.itemId && data.getClass().equals(this.getClass());
      }
      return false;
   }

   @Override
   public int hashCode() {
      return itemId * PRIME_NUMBER * this.getClass().hashCode();
   }

   protected void addInsertToBatch(SkynetTransaction transaction) throws OseeCoreException {
      internalAddInsertToBatch(transaction, Integer.MAX_VALUE, INSERT_INTO_TRANSACTION_TABLE,
         transaction.getTransactionNumber(), getGammaId(), getModificationType().getValue(),
         TxChange.getCurrent(getModificationType()).getValue(), transaction.getBranch().getId());
   }

   protected final int getItemId() {
      return itemId;
   }

   protected final ModificationType getModificationType() {
      return modificationType;
   }

   protected final int getGammaId() throws OseeCoreException {
      if (gammaId == null) {
         gammaId = createGammaId();
      }
      return gammaId;
   }

   final void setModificationType(ModificationType modificationType) {
      this.modificationType = modificationType;
   }

   protected abstract OseeSql getSelectTxNotCurrentSql();

   /**
    * Should be called by child classes during their implementation of addInsertToBatch.
    */
   protected final void internalAddInsertToBatch(SkynetTransaction transaction, int insertPriority, String insertSql, Object... data) {
      transaction.internalAddInsertToBatch(insertPriority, insertSql, data);
   }

   /**
    * Should not be called by application. This should only be called once after the transaction has been committed.
    */
   protected abstract void internalUpdate(TransactionRecord transactionId) throws OseeCoreException;

   /**
    * Should not be called by application. This should only be called once after the transaction has been committed.
    */
   protected abstract void internalClearDirtyState();

   /**
    * Should not be called by application. This should only be called once if there was an error committing the
    * transaction.
    */
   protected abstract void internalOnRollBack() throws OseeCoreException;

   /**
    * Should not be called by application. This method will be called by the base class when required;
    */
   protected abstract int createGammaId() throws OseeCoreException;

   /**
    * Should not be called by application. This should only be called once after the transaction has been committed.
    * 
    * @param artifactEvent TODO
    */
   protected abstract void internalAddToEvents(ArtifactEvent artifactEvent) throws OseeCoreException;
}
