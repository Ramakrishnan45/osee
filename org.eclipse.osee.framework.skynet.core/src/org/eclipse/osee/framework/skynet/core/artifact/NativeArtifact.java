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
package org.eclipse.osee.framework.skynet.core.artifact;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.swt.graphics.Image;

/**
 * Artifact type used to indicate that this artifact is to be rendered by a passing its binary data to a native program
 * for editing.
 * 
 * @author Ryan D. Brooks
 */
public class NativeArtifact extends Artifact {
   public static final String CONTENT_NAME = "Native Content";

   /**
    * @param parentFactory
    * @param guid
    * @param branch
    */
   public NativeArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactType artifactType) {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
   }

   @Override
   public Image getImage() {
      try {
         Image image = SkynetActivator.getInstance().getImageForProgram(getFileExtension());
         if (image != null) {
            return image;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
      }
      return super.getImage();
   }

   public String getFileName() throws OseeCoreException {
      return getDescriptiveName() + "." + getFileExtension();
   }

   public String getFileExtension() throws OseeCoreException {
      return getSoleAttributeValue("Extension", "");
   }

   public InputStream getNativeContent() throws OseeCoreException {
      return getSoleAttributeValue(CONTENT_NAME);
   }

   public void setNativeContent(File importFile) throws OseeCoreException, FileNotFoundException {
      setNativeContent(new FileInputStream(importFile));
   }

   public void setNativeContent(InputStream inputStream) throws OseeCoreException {
      setSoleAttributeValue(CONTENT_NAME, inputStream);
   }
}