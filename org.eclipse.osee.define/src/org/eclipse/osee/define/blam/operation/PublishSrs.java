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
package org.eclipse.osee.define.blam.operation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.word.WordTemplateProcessor;

/**
 * We need to create an SRSRenderer that provides the correct templates. Then we'll use it instead of the WordTemplate
 * processor like we do currently.
 * 
 * @author Robert A. Fisher
 */

public class PublishSrs extends AbstractBlam {

   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {

      Artifact srsMasterTemplate =
            ArtifactPersistenceManager.getInstance().getArtifactFromTypeName("Renderer Template", "srsMasterTemplate",
                  BranchPersistenceManager.getInstance().getCommonBranch());
      String masterTemplate = srsMasterTemplate.getSoleAttributeValue(WordAttribute.CONTENT_NAME, "");

      Artifact srsSlaveTemplate =
            ArtifactPersistenceManager.getInstance().getArtifactFromTypeName("Renderer Template", "srsSlaveTemplate",
                  BranchPersistenceManager.getInstance().getCommonBranch());
      String slaveTemplate = srsSlaveTemplate.getSoleAttributeValue(WordAttribute.CONTENT_NAME, "");

      boolean updateParagraphNumber = variableMap.getValue(Boolean.class, "Update Paragraph Numbers");
      WordTemplateProcessor processor = new WordTemplateProcessor(masterTemplate, slaveTemplate);
      processor.setSaveParagraphNumOnArtifact(updateParagraphNumber);
      variableMap.setValue("MasterFileName", "SRS");
      processor.applyTemplate(FileSystemRenderer.ensureRenderFolderExists(PresentationType.PREVIEW), variableMap);
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"Update Paragraph Numbers\" /><XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Branch\" /></xWidgets>";
   }
}