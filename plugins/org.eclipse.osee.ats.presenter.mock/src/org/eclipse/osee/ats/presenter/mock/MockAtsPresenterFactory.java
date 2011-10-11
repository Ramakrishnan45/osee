/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.presenter.mock;

import org.eclipse.osee.ats.api.components.AtsSearchHeaderComponentInterface;
import org.eclipse.osee.ats.api.search.AtsPresenterFactory;
import org.eclipse.osee.ats.api.search.AtsSearchPresenter;
import org.eclipse.osee.ats.presenter.mock.internal.MockAtsWebSearchPresenter;

/**
 * @author John Misinco
 */
public class MockAtsPresenterFactory implements AtsPresenterFactory<AtsSearchHeaderComponentInterface> {

   @Override
   public AtsSearchPresenter<AtsSearchHeaderComponentInterface> createInstance() {
      return new MockAtsWebSearchPresenter();
   }

}
