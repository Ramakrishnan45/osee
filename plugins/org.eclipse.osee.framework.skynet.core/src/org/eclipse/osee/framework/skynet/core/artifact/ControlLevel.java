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
package org.eclipse.osee.framework.skynet.core.artifact;

/**
 * Levels of control which an entity in the Skynet system is under.
 * 
 * @author Robert A. Fisher
 */
public enum ControlLevel {
   UNVERSIONED(false),
   VERSIONED(true),
   CHANGE_MANAGED(true);

   private boolean versionControlled;

   private ControlLevel(boolean versionControlled) {
      this.versionControlled = versionControlled;
   }

   /**
    * @return Returns the versionControlled.
    */
   public boolean isVersionControlled() {
      return versionControlled;
   }

}
