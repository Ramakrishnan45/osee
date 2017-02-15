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
package org.eclipse.osee.client.integration.tests.integration.ui.skynet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.FeatureDefinitionData;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.util.WordCoreUtil;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Morgan Cook
 */
public class WordApplicabilityTest {

   private static final String TEST_INCONSISTENT_TAGS = "support/WordInconsistentApplicabilityTags.xml";
   private static final String TEST_INVALID_TAGS = "support/WordInvalidApplicabilityTags.xml";
   private static final String TEST_VALID_TAGS = "support/WordValidApplicabilityTags.xml";

   private OseeClient oseeClient;

   @Before
   public void setup() {
      oseeClient = ServiceUtil.getOseeClient();
   }

   @Test
   public void testSaveInconsistentApplicabilityTags() throws IOException {
      String content = Lib.fileToString(getClass(), TEST_INCONSISTENT_TAGS);
      HashCollection<String, String> validFeatureValuesForBranch =
         getValidFeatureValuesForBranch(DemoBranches.SAW_Bld_1);
      assertTrue(
         WordCoreUtil.areApplicabilityTagsInvalid(content, DemoBranches.SAW_Bld_1, validFeatureValuesForBranch));
   }

   @Test
   public void testSaveInvalidApplicabilityTags() throws IOException {
      String content = Lib.fileToString(getClass(), TEST_INVALID_TAGS);
      HashCollection<String, String> validFeatureValuesForBranch =
         getValidFeatureValuesForBranch(DemoBranches.SAW_Bld_1);
      assertTrue(
         WordCoreUtil.areApplicabilityTagsInvalid(content, DemoBranches.SAW_Bld_1, validFeatureValuesForBranch));
   }

   @Test
   public void testSaveValidApplicabilityTags() throws IOException {
      String content = Lib.fileToString(getClass(), TEST_VALID_TAGS);
      HashCollection<String, String> validFeatureValuesForBranch =
         getValidFeatureValuesForBranch(DemoBranches.SAW_Bld_1);

      assertFalse(
         WordCoreUtil.areApplicabilityTagsInvalid(content, DemoBranches.SAW_Bld_1, validFeatureValuesForBranch));
   }

   private HashCollection<String, String> getValidFeatureValuesForBranch(BranchId branch) {
      List<FeatureDefinitionData> featureDefinitionData =
         oseeClient.getApplicabilityEndpoint(branch).getFeatureDefinitionData();

      HashCollection<String, String> validFeatureValues = new HashCollection<>();
      for (FeatureDefinitionData feat : featureDefinitionData) {
         validFeatureValues.put(feat.getName(), feat.getValues());
      }

      return validFeatureValues;
   }
}