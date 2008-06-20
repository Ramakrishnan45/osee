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

package org.eclipse.osee.framework.skynet.core.conflict;

import java.sql.SQLException;
import java.util.Set;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;

/**
 * @author Theron Virgin
 */
public class AttributeConflictBuilder extends ConflictBuilder {

   private final String sourceValue;
   private final String destValue;
   private final int attrId;
   private final int attrTypeId;

   /**
    * @param sourceGamma
    * @param destGamma
    * @param artId
    * @param toTransactionId
    * @param fromTransactionId
    * @param artifact
    * @param transactionType
    * @param mergeBranch
    * @param sourceBranch
    * @param destBranch
    * @param sourceValue
    * @param destValue
    * @param sourceContent
    * @param destContent
    * @param attrId
    * @param attrTypeId
    */
   public AttributeConflictBuilder(int sourceGamma, int destGamma, int artId, TransactionId toTransactionId, TransactionId fromTransactionId, ModificationType modType, Branch sourceBranch, Branch destBranch, String sourceValue, String destValue, int attrId, int attrTypeId) {
      super(sourceGamma, destGamma, artId, toTransactionId, fromTransactionId, modType, sourceBranch, destBranch);
      this.sourceValue = sourceValue;
      this.destValue = destValue;
      this.attrId = attrId;
      this.attrTypeId = attrTypeId;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.conflict.ConflictBuilder#getConflict()
    */
   @Override
   public Conflict getConflict(Branch mergeBranch, Set<Integer> artIdSet) throws SQLException, OseeCoreException {
      for (Integer integer : artIdSet) {
         if (integer.intValue() == artId) return null;
      }
      return new AttributeConflict(sourceGamma, destGamma, artId, toTransactionId, fromTransactionId, modType,
            ChangeType.CONFLICTING, sourceValue, destValue, attrId, attrTypeId, mergeBranch, sourceBranch, destBranch);

   }

}
