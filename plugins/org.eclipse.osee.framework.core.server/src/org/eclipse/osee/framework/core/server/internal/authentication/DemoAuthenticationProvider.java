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
package org.eclipse.osee.framework.core.server.internal.authentication;

import org.eclipse.osee.framework.core.data.IOseeUserInfo;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.server.IAuthenticationProvider;
import org.eclipse.osee.framework.core.server.UserDataStore;

/**
 * @author Roberto E. Escobar
 */
public class DemoAuthenticationProvider implements IAuthenticationProvider {

   private final boolean autoAuthenticate = true;
   private final String DEMO_USER = "Joe Smith";

   @Override
   public IOseeUserInfo asOseeUserId(OseeCredential credential) {
      IOseeUserInfo oseeUserInfo = UserDataStore.getOseeUserFromOseeDb(DEMO_USER);
      return oseeUserInfo != null ? oseeUserInfo : UserDataStore.createUser(true, DEMO_USER, DEMO_USER, "", true);
   }

   @Override
   public boolean authenticate(OseeCredential credential) {
      return autoAuthenticate;
   }

   @Override
   public String getProtocol() {
      return "demo";
   }

}
