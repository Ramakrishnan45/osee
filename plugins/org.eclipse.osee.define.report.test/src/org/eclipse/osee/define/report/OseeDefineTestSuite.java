/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.report;

import org.eclipse.osee.define.report.internal.DefineReportInternalTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author David W. Miller
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({DefineReportInternalTestSuite.class, WordUpdateEndpointImplTest.class})
public class OseeDefineTestSuite {
   // Test Suite
}
