/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.account.admin;

import java.util.Map;

/**
 * @author Roberto E. Escobar
 */
public interface CreateAccountRequest {

   String getUserName();

   boolean isActive();

   String getDisplayName();

   String getEmail();

   Map<String, String> getPreferences();

}
