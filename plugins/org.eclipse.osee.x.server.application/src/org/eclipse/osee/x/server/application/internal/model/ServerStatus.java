/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.x.server.application.internal.model;

import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author Donald G. Dunne
 */
public class ServerStatus {

   private final Map<String, String> data = new HashMap<String, String>();

   public void set(StatusKey key, String value) {
      data.put(key.name(), value);
   }

   @JsonIgnore
   public String get(StatusKey key) {
      return data.get(key.name());
   }

   public void add(String keyStr, String value) {
      StatusKey key = StatusKey.Unknown;
      try {
         key = StatusKey.valueOf(keyStr);
      } catch (Exception ex) {
         // do nothing
      }
      if (key == StatusKey.Unknown) {
         String newStr = data.get(StatusKey.Unknown);
         if (newStr == null) {
            newStr = String.format("[%s][%s]", key.name(), value);
         } else {
            newStr += String.format("%s - [%s][%s]", key.name(), value);
         }
         data.put(StatusKey.Unknown.name(), newStr);
      }
      data.put(keyStr, value);
   }

   public Map<String, String> getData() {
      return data;
   }

}
