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
package org.eclipse.osee.framework.database.core;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.database.core.DatabaseJoinAccessor.JoinItem;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public interface IJoinAccessor {

   void store(OseeConnection connection, JoinItem joinItem, int queryId, List<Object[]> dataList) throws OseeCoreException;

   int delete(OseeConnection connection, JoinItem joinItem, int queryId) throws OseeCoreException;

   Collection<Integer> getAllQueryIds(OseeConnection connection, JoinItem joinItem) throws OseeCoreException;
}
