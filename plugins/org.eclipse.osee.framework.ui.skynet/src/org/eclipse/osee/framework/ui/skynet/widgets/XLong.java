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
package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Donald G. Dunne
 */
public class XLong extends XText {
   private int minValue = 0;
   private boolean minValueSet = false;
   private int maxValue = 0;
   private boolean maxValueSet = false;

   public XLong(String displayLabel) {
      super(displayLabel);
   }

   public void setMinValue(int minValue) {
      minValueSet = true;
      this.minValue = minValue;
   }

   public void setMaxValue(int maxValue) {
      maxValueSet = false;
      this.maxValue = maxValue;
   }

   @Override
   public IStatus isValid() {
      if (isRequiredEntry() || super.get().compareTo("") != 0) {
         IStatus result = super.isValid();
         if (!result.isOK()) {
            return result;
         } else if (!this.isLong()) {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Must be an Long");
         } else if (minValueSet && this.getInteger() < minValue) {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Must be >= " + minValue);
         } else if (maxValueSet && this.getInteger() > maxValue) {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Must be <= " + maxValue);
         }
      }
      return Status.OK_STATUS;
   }
}
