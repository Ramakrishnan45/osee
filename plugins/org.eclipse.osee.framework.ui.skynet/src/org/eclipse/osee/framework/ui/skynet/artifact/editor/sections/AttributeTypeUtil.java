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
package org.eclipse.osee.framework.ui.skynet.artifact.editor.sections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;

/**
 * @author Roberto E. Escobar
 */
public class AttributeTypeUtil {

   private AttributeTypeUtil() {
   }

   public static AttributeType[] getEmptyTypes(Artifact artifact) throws OseeCoreException {
      List<AttributeType> items = new ArrayList<AttributeType>();
      for (AttributeType type : artifact.getAttributeTypes()) {
         if (!CoreAttributeTypes.NAME.equals(type) && artifact.getAttributes(type).isEmpty()) {
            items.add(type);
         }
      }
      Collections.sort(items);
      return items.toArray(new AttributeType[items.size()]);
   }

   public static AttributeType[] getTypesWithData(Artifact artifact) throws OseeCoreException {
      List<AttributeType> items = new ArrayList<AttributeType>();
      AttributeType nameType = null;
      AttributeType annotations = null;

      Set<AttributeType> typesInExistence = new HashSet<AttributeType>();
      List<Attribute<?>> attributeInstances = artifact.getAttributes();
      for (Attribute<?> attribute : attributeInstances) {
         typesInExistence.add(attribute.getAttributeType());
      }
      typesInExistence.addAll(artifact.getAttributeTypes());
      for (AttributeType type : typesInExistence) {
         if (CoreAttributeTypes.NAME.equals(type)) {
            nameType = type;
         } else {
            if (!artifact.getAttributes(type.getName()).isEmpty()) {
               if (CoreAttributeTypes.Annotation.equals(type)) {
                  annotations = type;
               } else {
                  items.add(type);
               }
            }
         }
      }
      Collections.sort(items);
      if (nameType != null) {
         items.add(0, nameType);
      }
      if (annotations != null) {
         items.add(annotations);
      }
      return items.toArray(new AttributeType[items.size()]);
   }

}
