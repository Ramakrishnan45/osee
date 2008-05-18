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
package org.eclipse.osee.framework.skynet.core.attribute;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider;

public final class CompressedContentAttribute extends BinaryAttribute<InputStream> {

   public CompressedContentAttribute(AttributeType attributeType, Artifact artifact) {
      super(attributeType, artifact);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#getValue()
    */
   @Override
   public InputStream getValue() {
      return new ByteArrayInputStream(getAttributeDataProvider().getValueAsBytes());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#setValue(java.lang.Object)
    */
   @Override
   public void subClassSetValue(InputStream value) throws OseeCoreException {
      setValueFromInputStream(value);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.IStreamableAttribute#setValueFromInputStream(java.io.InputStream)
    */
   @Override
   public void setValueFromInputStream(InputStream value) throws OseeCoreException {
      try {
         if (value == null) {
            getAttributeDataProvider().setValue(null);
         } else {
            byte[] data = Lib.inputStreamToBytes(value);
            getAttributeDataProvider().setValue(data);
         }
      } catch (IOException ex) {
         throw new OseeCoreException(ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#setAttributeDataProvider(org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider)
    */
   @Override
   public void setAttributeDataProvider(IAttributeDataProvider attributeDataProvider) {
      super.setAttributeDataProvider(attributeDataProvider);
      attributeDataProvider.setDisplayableString(getAttributeType().getName());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.Attribute#convertStringToValue(java.lang.String)
    */
   @Override
   protected InputStream convertStringToValue(String value) throws OseeCoreException {
      try {
         return Lib.stringToInputStream(value);
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }
}