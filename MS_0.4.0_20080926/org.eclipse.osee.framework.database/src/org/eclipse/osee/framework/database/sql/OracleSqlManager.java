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
package org.eclipse.osee.framework.database.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.database.data.ColumnMetadata;
import org.eclipse.osee.framework.database.data.IndexElement;
import org.eclipse.osee.framework.database.data.TableElement;
import org.eclipse.osee.framework.database.data.TableElement.ColumnFields;
import org.eclipse.osee.framework.database.sql.datatype.SqlDataType;
import org.eclipse.osee.framework.jdk.core.util.StringFormat;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;

/**
 * @author Roberto E. Escobar
 */
public class OracleSqlManager extends SqlManager {

   public OracleSqlManager(SqlDataType sqlDataType) {
      super(ConfigUtil.getConfigFactory().getLogger(OracleSqlManager.class), sqlDataType);
   }

   protected String handleColumnCreationSection(Connection connection, Map<String, ColumnMetadata> columns) throws SQLException {
      List<String> lines = new ArrayList<String>();
      Set<String> keys = columns.keySet();
      for (String key : keys) {
         Map<ColumnFields, String> column = columns.get(key).getColumnFields();
         lines.add(columnDataToSQL(column));
      }
      String toExecute = StringFormat.listToValueSeparatedString(lines, ",\n");
      return toExecute;
   }

   public void createTable(Connection connection, TableElement tableDef) throws SQLException, Exception {
      StringBuilder toExecute = new StringBuilder();
      toExecute.append(SqlManager.CREATE_STRING + " TABLE " + formatQuotedString(tableDef.getFullyQualifiedTableName(),
            "\\.") + " ( \n");
      toExecute.append(handleColumnCreationSection(connection, tableDef.getColumns()));
      toExecute.append(handleConstraintCreationSection(tableDef.getConstraints(), tableDef.getFullyQualifiedTableName()));
      toExecute.append(handleConstraintCreationSection(tableDef.getForeignKeyConstraints(),
            tableDef.getFullyQualifiedTableName()));
      toExecute.append(" \n)");
      toExecute.append(" tablespace ");
      toExecute.append(tableDef.getTablespace());
      toExecute.append("\n");
      logger.log(Level.INFO, "Creating Table: [ " + tableDef.getFullyQualifiedTableName() + "]");
      executeStatement(connection, toExecute.toString());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.database.sql.SqlManager#createIndexPostProcess(java.lang.String)
    */
   @Override
   protected String createIndexPostProcess(IndexElement indexElement, String original) {
      StringBuilder buffer = new StringBuilder(original);
      buffer.append(" tablespace ");
      buffer.append(indexElement.getTablespace());
      return buffer.toString();
   }

   @Override
   public void dropTable(Connection connection, TableElement tableDef) throws SQLException, Exception {
      StringBuilder toExecute = new StringBuilder();
      toExecute.append(SqlManager.DROP_STRING + " TABLE " + formatQuotedString(tableDef.getFullyQualifiedTableName(),
            "\\."));
      logger.log(Level.INFO, "Dropping Table: [ " + tableDef.getFullyQualifiedTableName() + "]");
      executeStatement(connection, toExecute.toString());
   }
}
