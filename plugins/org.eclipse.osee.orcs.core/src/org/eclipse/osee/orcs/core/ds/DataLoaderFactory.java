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
package org.eclipse.osee.orcs.core.ds;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public interface DataLoaderFactory {

   DataLoader fromQueryContext(QueryContext queryContext) throws OseeCoreException;

   DataLoader fromBranchAndArtifactIds(String sessionId, IOseeBranch branch, Collection<Integer> artifactIds) throws OseeCoreException;

   DataLoader fromBranchAndArtifactIds(String sessionId, IOseeBranch branch, int... artifactIds) throws OseeCoreException;

}
