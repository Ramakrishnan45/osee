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

import java.util.Date;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

public class XDateDam extends XDate implements IAttributeWidget {

   private Artifact artifact;
   private String attributeTypeName;

   public XDateDam(String displayLabel) {
      super(displayLabel);
   }

   @Override
   public String getAttributeType() {
      return attributeTypeName;
   }

   @Override
   public void setAttributeType(Artifact artifact, String attrName) throws OseeCoreException {
      this.artifact = artifact;
      this.attributeTypeName = attrName;
      Date value = artifact.getSoleAttributeValue(attributeTypeName, null);
      if (value != null) {
         super.setDate(value);
      }
   }

   @Override
   public void saveToArtifact() {
      try {
         if (date == null) {
            artifact.deleteSoleAttribute(attributeTypeName);
         } else {
            Date enteredValue = getDate();
            artifact.setSoleAttributeValue(attributeTypeName, enteredValue);
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public Result isDirty() throws OseeCoreException {
      Date enteredValue = getDate();
      Date storedValue = artifact.getSoleAttributeValue(attributeTypeName, null);
      if (enteredValue == null && storedValue == null) {
         return Result.FalseResult;
      }
      if (enteredValue == null && storedValue != null) {
         return new Result(true, attributeTypeName + " is dirty");
      }
      if (enteredValue != null && storedValue == null) {
         return new Result(true, attributeTypeName + " is dirty");
      }
      if (enteredValue.getTime() != storedValue.getTime()) {
         return new Result(true, attributeTypeName + " is dirty");
      }
      return Result.FalseResult;
   }

   @Override
   public void revert() throws OseeCoreException {
      setAttributeType(artifact, attributeTypeName);
   }

   @Override
   public IStatus isValid() {
      IStatus status = super.isValid();
      if (status.isOK()) {
         status = OseeValidator.getInstance().validate(IOseeValidator.SHORT, artifact, attributeTypeName, get());
      }
      return status;
   }
}
