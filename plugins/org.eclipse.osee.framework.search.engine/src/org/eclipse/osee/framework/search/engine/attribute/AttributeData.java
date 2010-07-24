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
package org.eclipse.osee.framework.search.engine.attribute;

import java.net.URI;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.search.engine.data.IAttributeLocator;

/**
 * @author Roberto E. Escobar
 */
public class AttributeData implements IAttributeLocator {
   private final static String EMPTY_STRING = "";
   private final int artId;
   private final long gammaId;
   private final int branchId;
   private final String value;
   private final String uri;
   private final int attrTypeId;

   protected AttributeData(int artId, long gammaId, int branchId, String value, String uri, int attrTypeId) {
      super();
      this.artId = artId;
      this.gammaId = gammaId;
      this.branchId = branchId;
      this.value = Strings.isValid(value) ? value : EMPTY_STRING;
      this.uri = Strings.isValid(uri) ? uri : EMPTY_STRING;
      this.attrTypeId = attrTypeId;
   }

   protected AttributeData(long gammaId, String value, String uri, int attrTypeId) {
      this(-1, gammaId, -1, value, uri, attrTypeId);
   }

   public int getArtId() {
      return artId;
   }

   public int getId() {
      return branchId;
   }

   @Override
   public long getGammaId() {
      return gammaId;
   }

   public int getAttrTypeId() {
      return attrTypeId;
   }

   public String getStringValue() {
      return this.value;
   }

   public String getUri() {
      return this.uri;
   }

   public boolean isUriValid() {
      boolean toReturn = false;
      try {
         String value = getUri();
         if (Strings.isValid(value)) {
            URI uri = new URI(value);
            if (uri != null) {
               toReturn = true;
            }
         }
      } catch (Exception ex) {
         // DO NOTHING
      }
      return toReturn;
   }

   @Override
   public String toString() {
      return String.format("artId:[%s] branchId:[%d] gammaId:[%s] uri:[%s] attrTypeId:[%s]", getArtId(), getId(),
         getGammaId(), getUri(), getAttrTypeId());
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      }
      if (!(object instanceof AttributeData)) {
         return false;
      }
      AttributeData other = (AttributeData) object;
      return other.getGammaId() == this.getGammaId();
   }

   @Override
   public int hashCode() {
      return (int) (gammaId * 37);
   }
}
