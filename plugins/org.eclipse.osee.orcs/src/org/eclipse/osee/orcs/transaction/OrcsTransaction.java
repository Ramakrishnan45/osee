/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.transaction;

import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;

/**
 * @author Roberto E. Escobar
 */
public interface OrcsTransaction {

   // TODO define better API
   void deleteRelation(IRelationTypeSide relationType, int aArtId, int bArtId);

   Callable<?> build();
}
