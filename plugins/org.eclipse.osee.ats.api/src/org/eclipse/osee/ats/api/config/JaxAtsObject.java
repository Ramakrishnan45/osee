/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.config;

import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Donald G. Dunne
 */
public class JaxAtsObject extends NamedIdBase {

   /**
    * Do not remove this guid until IDE client no longer needs it for creating config objects. This class is used to
    * serialize all the ATS config objects so the clients don't have to load them.
    */
   protected String guid;
   protected boolean active;
   private String description;

   public JaxAtsObject() {
      this(Id.SENTINEL, "");
   }

   public JaxAtsObject(Long id, String name) {
      super(id, name);
   }

   @Override
   public String toString() {
      return getName();
   }

   public boolean isActive() {
      return active;
   }

   public void setActive(boolean active) {
      this.active = active;
   }

   public boolean matches(JaxAtsObject... identities) {
      for (JaxAtsObject identity : identities) {
         if (equals(identity)) {
            return true;
         }
      }
      return false;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getGuid() {
      return guid;
   }

   public void setGuid(String guid) {
      this.guid = guid;
   }

}
