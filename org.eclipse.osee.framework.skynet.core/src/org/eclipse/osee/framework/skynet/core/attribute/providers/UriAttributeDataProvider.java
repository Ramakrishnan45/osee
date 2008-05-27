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
package org.eclipse.osee.framework.skynet.core.attribute.providers;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeResourceProcessor;
import org.eclipse.osee.framework.skynet.core.attribute.utils.AbstractResourceProcessor;
import org.eclipse.osee.framework.skynet.core.attribute.utils.BinaryContentUtils;

/**
 * @author Roberto E. Escobar
 */
public class UriAttributeDataProvider extends AbstractAttributeDataProvider implements ICharacterAttributeDataProvider, IBinaryAttributeDataProvider {

   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(UriAttributeDataProvider.class);
   private DataStore dataStore;
   private String displayable;

   public UriAttributeDataProvider(Attribute<?> attribute) {
      super(attribute);
      AbstractResourceProcessor abstractResourceProcessor = new AttributeResourceProcessor(attribute);
      this.dataStore = new DataStore(abstractResourceProcessor);
      this.displayable = "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.IAttributeDataProvider#getDisplayableString()
    */
   @Override
   public String getDisplayableString() {
      return displayable;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.IAttributeDataProvider#setDisplayableString(java.lang.String)
    */
   @Override
   public void setDisplayableString(String toDisplay) {
      this.displayable = toDisplay;
   }

   public String getInternalFileName() {
      return BinaryContentUtils.generateFileName(getAttribute());
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.IAttributeDataProvider#setValue(ByteBuffer)
    */
   @Override
   public void setValue(ByteBuffer data) {
      try {
         if (!Arrays.equals(dataStore.getContent(), data != null ? data.array() : null)) {
            if (data != null) {
               byte[] compressed;
               try {
                  compressed = Lib.compressFile(Lib.byteBufferToInputStream(data), getInternalFileName());
                  dataStore.setContent(compressed, "zip", "application/zip", "ISO-8859-1");
               } catch (Exception ex) {
                  logger.log(Level.WARNING, "Error compressing data", ex);
               }
            } else {
               String loc = dataStore.getLocator();
               dataStore.clear();
               dataStore.setLocator(loc);
            }
         }
      } catch (Exception ex1) {
         logger.log(Level.SEVERE, ex1.toString(), ex1);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.IAttributeDataProvider#getValueAsBytes()
    */
   @Override
   public ByteBuffer getValueAsBytes() {
      ByteBuffer decompressed = null;
      try {
         byte[] rawData = dataStore.getContent();
         if (rawData != null) {
            decompressed = ByteBuffer.wrap(Lib.decompressBytes(new ByteArrayInputStream(rawData)));
         }
      } catch (Exception ex) {
         logger.log(Level.WARNING, "Error acquiring data. - ", ex);
      }
      return decompressed;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.data.IStringAttributeDataProvider#getValueAsString()
    */
   @Override
   public String getValueAsString() {
      String toReturn = null;
      ByteBuffer data = getValueAsBytes();
      if (data != null) {
         try {
            toReturn = new String(data.array(), "UTF-8");
         } catch (UnsupportedEncodingException ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
         }
      } else {
         toReturn = "";
      }
      return toReturn;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.data.IStringAttributeDataProvider#setValue(java.lang.String)
    */
   @Override
   public void setValue(String value) {
      ByteBuffer toSet = null;
      if (value != null) {
         try {
            toSet = ByteBuffer.wrap(value.getBytes("UTF-8"));
         } catch (UnsupportedEncodingException ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
         }
      }
      setValue(toSet);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.providers.IDataAccessObject#getData()
    */
   @Override
   public Object[] getData() throws Exception {
      return new Object[] {"", dataStore.getLocator()};
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.providers.IDataAccessObject#loadData(java.lang.Object[])
    */
   @Override
   public void loadData(Object... objects) throws Exception {
      if (objects != null && objects.length > 1) {
         dataStore.setLocator((String) objects[1]);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.providers.IDataAccessObject#persist()
    */
   @Override
   public void persist() throws Exception {
      dataStore.persist();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider#purge()
    */
   @Override
   public void purge() throws Exception {
      dataStore.purge();
   }
}
