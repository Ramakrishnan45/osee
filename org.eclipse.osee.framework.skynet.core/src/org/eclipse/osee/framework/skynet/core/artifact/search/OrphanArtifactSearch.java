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
package org.eclipse.osee.framework.skynet.core.artifact.search;

import java.sql.SQLException;
import java.util.List;
import org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;

/**
 * @author Jeff C. Phillips
 */
public class OrphanArtifactSearch implements ISearchPrimitive {
   private static final String LABEL = "Orphan Search: ";
   private static final String tables = "osee_define_artifact";
   private static final String sql =
         "osee_define_artifact.art_type_id =? AND art_id NOT in (SELECT t2.art_id FROM osee_define_rel_link t1, osee_define_artifact t2, " + SkynetDatabase.TRANSACTIONS_TABLE + " t4, " + SkynetDatabase.TRANSACTION_DETAIL_TABLE + " t5, (SELECT Max(t1.gamma_id) AS gamma_id, t1.rel_link_id, t3.branch_id FROM " + SkynetDatabase.RELATION_LINK_VERSION_TABLE + " t1, " + SkynetDatabase.TRANSACTIONS_TABLE + " t2, " + SkynetDatabase.TRANSACTION_DETAIL_TABLE + " t3 WHERE t1.gamma_id = t2.gamma_id AND t2.transaction_id = t3.transaction_id AND t3.branch_id = ? GROUP BY t1.rel_link_id, t3.branch_id) t6 WHERE t1.rel_link_type_id =? AND t1.b_art_id = t2.art_id AND t1.gamma_id = t4.gamma_id AND t4.transaction_id = t5.transaction_id AND t1.rel_link_id = t6.rel_link_id AND t5.branch_id = t6.branch_id AND t1.gamma_id = t6.gamma_id AND t1.modification_id <> 3 GROUP BY t2.art_id)";
   private ArtifactSubtypeDescriptor aritfactType;
   private int relationTypeId;

   public OrphanArtifactSearch(ArtifactSubtypeDescriptor aritfactType) throws SQLException {
      this.aritfactType = aritfactType;
      this.relationTypeId = RelationTypeManager.getType("Default Hierarchical").getRelationTypeId();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive#getSql(java.util.List, org.eclipse.osee.framework.skynet.core.artifact.Branch)
    */
   public String getCriteriaSql(List<Object> dataList, Branch branch) {
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(aritfactType.getArtTypeId());
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(branch.getBranchId());
      dataList.add(SQL3DataType.INTEGER);
      dataList.add(relationTypeId);

      return sql;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive#getArtIdColName()
    */
   public String getArtIdColName() {
      return "art_id";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive#getTables(java.util.List)
    */
   public String getTableSql(List<Object> dataList, Branch branch) {
      return tables;
   }

   @Override
   public String toString() {
      return LABEL + aritfactType.getName();
   }

   public static OrphanArtifactSearch getPrimitive(String storageString) {
      storageString = storageString.replace(LABEL, "");
      if (storageString.endsWith(";")) {
         storageString = storageString.substring(0, storageString.length() - 1);
      }
      OrphanArtifactSearch search = null;
      try {
         ArtifactSubtypeDescriptor artifactType =
               ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(storageString);
         search = new OrphanArtifactSearch(artifactType);
      } catch (SQLException ex) {
         new IllegalStateException("Value for " + OrphanArtifactSearch.class.getSimpleName() + " not parsable");
      }
      return search;
   }
}
