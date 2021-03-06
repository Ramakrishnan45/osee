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
package org.eclipse.osee.framework.jdk.core.type;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class JaxRsUuidNamed {

   private String name;
   private long uuid;

   public JaxRsUuidNamed() {
      this("", 0L);
   }

   public JaxRsUuidNamed(String name, Long uuid) {
      this.uuid = uuid;
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public long getUuid() {
      return uuid;
   }

   public void setUuid(long uuid) {
      this.uuid = uuid;
   }

}
