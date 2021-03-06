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
package org.eclipse.osee.framework.skynet.core.relation.sorters;

import java.util.Comparator;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Andrew M. Finkbeiner
 */
class UserDefinedOrderComparator extends AbstractUserDefinedOrderComparator implements Comparator<ArtifactToken> {

   public UserDefinedOrderComparator(List<String> guidOrder) {
      super(guidOrder);
   }

   @Override
   public int compare(ArtifactToken artifact1, ArtifactToken artifact2) {
      Integer val1 = value.get(artifact1.getGuid());
      Integer val2 = value.get(artifact2.getGuid());
      return compareIntegers(val1, val2);
   }
}
