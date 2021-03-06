/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.ds;

/**
 * @author Angel Avila
 */
public interface KeyValueStore {

   Long putIfAbsent(String value);

   Long getByValue(String value);

   String getByKey(Long key);

   boolean putByKey(Long key, String value);
}