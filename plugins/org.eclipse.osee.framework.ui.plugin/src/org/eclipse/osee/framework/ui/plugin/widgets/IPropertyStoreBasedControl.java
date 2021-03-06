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
package org.eclipse.osee.framework.ui.plugin.widgets;

import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Roberto E. Escobar
 */
public interface IPropertyStoreBasedControl {

   public Control createControl(Composite parent);

   public void save(IPropertyStore propertyStore);

   public void load(IPropertyStore propertyStore);

   public boolean areSettingsValid();

   public String getErrorMessage();

   public int getPriority();
}
