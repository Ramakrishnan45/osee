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
package org.eclipse.osee.orcs.core.ds.criteria;

import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author Roberto E. Escobar
 */
public class CriteriaTxGetPrior extends Criteria implements TxCriteria {

   private final TransactionId txId;

   public CriteriaTxGetPrior(TransactionId txId) {
      super();
      this.txId = txId;
   }

   public TransactionId getTxId() {
      return txId;
   }

   @Override
   public void checkValid(Options options) throws OseeCoreException {
      super.checkValid(options);
      Conditions.checkExpressionFailOnTrue(txId.isInvalid(), "TxId [%s] is invalid. Must be >= 0", txId);
   }

   @Override
   public String toString() {
      return "CriteriaTxGetPrior [txId=" + txId + "]";
   }

}
