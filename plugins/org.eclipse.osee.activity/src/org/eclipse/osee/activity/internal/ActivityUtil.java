/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.activity.internal;

import java.util.Map;

/**
 * @author Ryan D. Brooks
 */
public final class ActivityUtil {

   private ActivityUtil() {
      // Utility class
   }

   public static String captureStackTrace(Throwable ex, int linesToCapture) {
      Throwable cause = ex;

      while (cause.getCause() != null) {
         cause = cause.getCause();
      }

      StringBuilder sb = new StringBuilder();
      sb.append(cause.toString() + "\n");
      StackTraceElement stackElements[] = cause.getStackTrace();
      for (int i = 0; i < linesToCapture; i++) {
         sb.append(stackElements[i] + "\n");
      }
      return sb.toString();
   }

   @SuppressWarnings("unchecked")
   public static <T> T get(Map<String, Object> properties, String key, T defaultValue) {
      T value = properties != null ? (T) properties.get(key) : null;
      if (value == null) {
         value = defaultValue;
      }
      return value;
   }
}