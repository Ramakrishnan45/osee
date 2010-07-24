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
package org.eclipse.osee.framework.core.model.test;

import org.eclipse.osee.framework.core.model.test.cache.CacheTestSuite;
import org.eclipse.osee.framework.core.model.test.fields.FieldTestSuite;
import org.eclipse.osee.framework.core.model.test.type.ModelTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({//
CacheTestSuite.class, //
   FieldTestSuite.class, //
   ModelTestSuite.class, //
})
/**
 * @author Roberto E. Escobar
 */
public class AllCoreModelTestSuite {

}
