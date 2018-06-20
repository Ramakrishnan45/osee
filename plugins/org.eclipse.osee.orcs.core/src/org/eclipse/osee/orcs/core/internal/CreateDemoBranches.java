/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal;

import static org.eclipse.osee.framework.core.data.ApplicabilityToken.BASE;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTokens.DefaultHierarchyRoot;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTokens.ProductLineFolder;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.DemoBranches.CIS_Bld_1;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreTupleTypes;
import org.eclipse.osee.framework.core.enums.DemoSubsystems;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.Requirements;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

/**
 * @author Ryan D. Brooks
 */
public class CreateDemoBranches {
   private final OrcsApi orcsApi;
   private final TransactionFactory txFactory;
   private final QueryBuilder query;

   public CreateDemoBranches(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
      txFactory = orcsApi.getTransactionFactory();
      query = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON);
   }

   public void populate() {
      UserId account = DemoUsers.Joe_Smith;
      TransactionBuilder tx = txFactory.createTransaction(COMMON, account, "Create Demo Users");
      CreateSystemBranches.createUsers(tx, DemoUsers.values(), query);
      tx.commit();

      createDemoProgramBranch(SAW_Bld_1, account);
      createPlConfig(SAW_Bld_1, account);

      createDemoProgramBranch(CIS_Bld_1, account);
   }

   private void configureFeature(TransactionBuilder tx, String featureName, ArtifactId[] products, String... featureValues) {
      for (int i = 0; i < products.length; i++) {
         tx.addTuple2(CoreTupleTypes.ViewApplicability, products[i], featureName + " = " + featureValues[i]);
      }
   }

   private void configureProducts(TransactionBuilder tx, ArtifactToken[] products) {
      for (int i = 0; i < products.length; i++) {
         tx.addTuple2(CoreTupleTypes.ViewApplicability, products[i], "Config = " + products[i].getName());
         tx.addTuple2(CoreTupleTypes.ViewApplicability, products[i], BASE.getName());
      }
   }

   private void createPlConfig(BranchId branch, UserId account) {

      TransactionBuilder tx0 =
         txFactory.createTransaction(branch, SystemUser.OseeSystem, "Create Product Line folders");
      tx0.createArtifact(DefaultHierarchyRoot, ProductLineFolder);
      tx0.createArtifact(ProductLineFolder, CoreArtifactTokens.ProductsFolder);
      tx0.commit();

      TransactionBuilder tx = txFactory.createTransaction(branch, SystemUser.OseeSystem, "Configure Product Line");

      ArtifactToken productA = tx.createView(branch, "Product A");
      ArtifactToken productB = tx.createView(branch, "Product B");
      ArtifactToken productC = tx.createView(branch, "Product C");
      ArtifactToken productD = tx.createView(branch, "Product D");
      ArtifactToken[] products = new ArtifactToken[] {productA, productB, productC, productD};

      configureProducts(tx, products);

      configureFeature(tx, "LAD", products, "Excluded", "Included", "Excluded", "Included");
      configureFeature(tx, "JSOW", products, "Included", "Included", "Included", "Excluded");
      configureFeature(tx, "IRST", products, "Excluded", "Included", "Included", "Excluded");
      configureFeature(tx, "EA-18G", products, "Included", "Included", "Included", "Excluded");

      ArtifactId featureDefinition = tx.createArtifact(CoreArtifactTokens.ProductsFolder,
         CoreArtifactTypes.FeatureDefinition, "Feature Definition");

      String featureDefJson = "[{" + "\"name\": \"LAD\"," + //
         "\"type\": \"single\"," + //
         "\"values\": [\"Included\", \"Excluded\"]," + //
         "\"defaultValue\": \"Included\"," + //
         "\"description\": \"Large Area Display\"" + //
         "}, {" + //
         "\"name\": \"JSOW\"," + //
         "\"type\": \"single\"," + //
         "\"values\": [\"Included\", \"Excluded\"]," + //
         "\"defaultValue\": \"Included\"," + //
         "\"description\": \"Joint Stand-Off Weapon\"" + //
         "},{" + //
         "\"name\": \"EA-18G\"," + //
         "\"type\": \"single\"," + //
         "\"values\": [\"Included\", \"Excluded\"]," + //
         "\"defaultValue\": \"Included\"," + //
         "\"description\": \"Electronic Attack Growler\"" + //
         "},{" + //
         "\"name\": \"IRST\"," + //
         "\"type\": \"single\"," + //
         "\"values\": [\"Included\", \"Excluded\"]," + //
         "\"defaultValue\": \"Included\"," + //
         "\"description\": \"Infra-Red Search and Track (sensor)\"" + //
         "}" + //
         "]";

      tx.createAttribute(featureDefinition, CoreAttributeTypes.GeneralStringData, featureDefJson);

      tx.commit();
   }

   public void createDemoProgramBranch(IOseeBranch branch, UserId account) {
      OrcsBranch branchOps = orcsApi.getBranchOps();

      branchOps.createTopLevelBranch(branch, account);
      branchOps.setBranchPermission(DemoUsers.Joe_Smith, branch, PermissionEnum.FULLACCESS);

      TransactionBuilder tx = txFactory.createTransaction(branch, account, "Create Demo Program data");

      ArtifactId sawProduct =
         tx.createArtifact(DefaultHierarchyRoot, CoreArtifactTypes.Component, "SAW Product Decomposition");

      for (String subsystem : DemoSubsystems.getSubsystems()) {
         tx.createArtifact(sawProduct, CoreArtifactTypes.Component, subsystem);
      }

      for (String name : new String[] {
         Requirements.SYSTEM_REQUIREMENTS,
         Requirements.SUBSYSTEM_REQUIREMENTS,
         Requirements.SOFTWARE_REQUIREMENTS,
         Requirements.HARDWARE_REQUIREMENTS,
         "Verification Tests",
         "Validation Tests",
         "Integration Tests",
         "Applicability Tests"}) {
         tx.createArtifact(DefaultHierarchyRoot, CoreArtifactTypes.Folder, name);
      }

      tx.commit();
   }
}