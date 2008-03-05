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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeDescriptor;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;

/**
 * @author Ryan D. Brooks
 */
public class DeleteExtraneousUnspecifiedAttributes extends AbstractBlam {
   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      List<Artifact> artifacts = variableMap.getArtifacts("Artifacts");
      DynamicAttributeDescriptor attributeDescriptor = variableMap.getAttributeDescriptor("Attribute Type");

      monitor.beginTask("Delete Unspecified " + attributeDescriptor.getName() + " attributes", artifacts.size());

      for (Artifact artifact : artifacts) {
         Collection<Attribute<String>> attributes = artifact.getAttributeManager(attributeDescriptor).getAttributes();

         if (attributes.size() > 1) {
            for (Attribute<String> attribute : attributes) {
               if (attribute.getValue().equals("Unspecified")) {
                  attribute.delete();
               }
            }
         }
         artifact.persistAttributes();
         monitor.worked(1);
      }
      monitor.done();
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XListDropViewer\" displayName=\"Artifacts\" /><XWidget xwidgetType=\"XAttributeTypeListViewer\" displayName=\"Attribute Type\" /></xWidgets>";
   }
}