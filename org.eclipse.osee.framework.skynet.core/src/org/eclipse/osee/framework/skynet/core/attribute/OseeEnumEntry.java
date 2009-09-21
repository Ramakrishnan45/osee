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

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.artifact.AbstractOseeType;
import org.eclipse.osee.framework.skynet.core.types.AbstractOseeCache;
import org.eclipse.osee.framework.skynet.core.types.OseeEnumTypeCache;

/**
 * @author Roberto E. Escobar
 */
public class OseeEnumEntry extends AbstractOseeType implements Comparable<OseeEnumEntry> {
   private int ordinal;

   public OseeEnumEntry(AbstractOseeCache<OseeEnumType> cache, String guid, String name, int ordinal) {
      super(cache, guid, name);
      this.ordinal = ordinal;
   }

   @Override
   protected OseeEnumTypeCache getCache() {
      return (OseeEnumTypeCache) super.getCache();
   }

   public int ordinal() {
      return ordinal;
   }

   public void setOrdinal(int ordinal) {
      updateDirty(this.ordinal, ordinal);
      this.ordinal = ordinal;
   }

   public Pair<String, Integer> asPair() {
      return new Pair<String, Integer>(getName(), ordinal());
   }

   @Override
   protected void updateDirty(Object original, Object other) {
      super.updateDirty(original, other);
      if (isDirty()) {
         OseeEnumType type = null;
         try {
            type = getDeclaringClass();
            if (type != null) {
               type.internalUpdateDirtyEntries(true);
            }
         } catch (OseeCoreException ex) {
            // Do Nothing
         }
      }
   }

   public OseeEnumType getDeclaringClass() throws OseeCoreException {
      return getCache().getEnumType(this);
   }

   @Override
   public boolean equals(Object object) {
      if (object instanceof OseeEnumEntry) {
         OseeEnumEntry other = (OseeEnumEntry) object;
         return super.equals(other) && ordinal == other.ordinal;
      }
      return false;
   }

   @Override
   public int hashCode() {
      final int prime = 37;
      int result = super.hashCode();
      result = prime * result + ordinal;
      return result;
   }

   @Override
   public String toString() {
      return String.format("%s:%s", getName(), ordinal);
   }

   @Override
   public int compareTo(OseeEnumEntry other) {
      return this.ordinal() - other.ordinal();
   }

   @Override
   public void persist() throws OseeCoreException {
      getCache().storeItem(getDeclaringClass());
   }

}
