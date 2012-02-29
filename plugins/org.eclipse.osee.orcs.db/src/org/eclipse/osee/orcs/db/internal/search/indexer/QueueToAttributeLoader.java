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
package org.eclipse.osee.orcs.db.internal.search.indexer;

import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.orcs.data.ReadableAttribute;

/**
 * @author Roberto E. Escobar
 */
public interface QueueToAttributeLoader {

   void loadAttributes(OseeConnection connection, int tagQueueQueryId, Collection<ReadableAttribute<?>> attributeDatas) throws OseeCoreException;

}
