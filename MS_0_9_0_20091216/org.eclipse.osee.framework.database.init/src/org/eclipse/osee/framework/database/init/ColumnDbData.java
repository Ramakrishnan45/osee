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
package org.eclipse.osee.framework.database.init;

/**
 * @author Roberto E. Escobar
 */
public class ColumnDbData {
   private String columnName;
   private String columnValue;

   public ColumnDbData(String columnName, String columnValue) {
      this.columnName = columnName;
      this.columnValue = columnValue;
   }

   public String getColumnName() {
      return columnName;
   }

   public String getColumnValue() {
      return columnValue;
   }

   public String toString() {
      return "[" + columnName + ", " + columnValue + "]";
   }
}
