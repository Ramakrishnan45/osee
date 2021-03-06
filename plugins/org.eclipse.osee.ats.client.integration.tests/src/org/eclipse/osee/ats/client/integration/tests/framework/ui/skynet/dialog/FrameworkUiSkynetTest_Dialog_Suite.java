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
package org.eclipse.osee.ats.client.integration.tests.framework.ui.skynet.dialog;

import org.eclipse.osee.ats.client.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Donald G. Dunne
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ //
   EmailWizardTest.class, //
   FilteredCheckboxAttributeTypeDialogTest.class, //
   FilteredTreeArtifactTypeDialogTest.class, //
})
public class FrameworkUiSkynetTest_Dialog_Suite {

   @BeforeClass
   public static void setUp() throws Exception {
      OseeProperties.setIsInTest(true);
      System.out.println("\n\nBegin " + FrameworkUiSkynetTest_Dialog_Suite.class.getSimpleName());
      DemoTestUtil.setUpTest();
   }

   @AfterClass
   public static void tearDown() throws Exception {
      System.out.println("End " + FrameworkUiSkynetTest_Dialog_Suite.class.getSimpleName());
   }
}
