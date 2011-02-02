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
package org.eclipse.osee.framework.ui.skynet.render;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionDelta;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.change.ArtifactDelta;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.swt.Displays;

public final class RenderingUtil {
   private static final Random generator = new Random();

   private static IFolder workingFolder;
   private static IFolder compareFolder;
   private static IFolder previewFolder;
   private static boolean arePopupsAllowed = true;
   private static final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");

   public static void setPopupsAllowed(boolean popupsAllowed) {
      arePopupsAllowed = popupsAllowed;
   }

   private static final String FILENAME_WARNING_MESSAGE =
      "\n\nis approaching a large size which may cause the opening application to error. " + "\nSuggest moving your workspace to avoid potential errors. ";
   private static final int FILENAME_LIMIT = 215;

   private static boolean showAgain = true;

   public static String getAssociatedArtifactName(List<Change> changes) throws OseeCoreException {
      Change change = changes.get(0);
      return getName(change.getTxDelta());
   }

   public static String getAssociatedArtifactName(Collection<ArtifactDelta> artifactDeltas) throws OseeCoreException {
      if (artifactDeltas.isEmpty()) {
         return "";
      }
      ArtifactDelta artifactDelta = artifactDeltas.iterator().next();
      return getName(artifactDelta.getTxDelta());
   }

   private static String getName(TransactionDelta txDelta) throws OseeCoreException {
      Artifact associatedArtifact = BranchManager.getAssociatedArtifact(txDelta);
      String name = associatedArtifact == null ? "" : associatedArtifact.getName();
      return name.substring(0, Math.min(name.length(), 15));
   }

   public static boolean ensureFilenameLimit(IFile file) {
      boolean withinLimit = true;
      if (Lib.isWindows()) {
         String absPath = file.getLocation().toFile().getAbsolutePath();
         if (absPath.length() > FILENAME_LIMIT) {
            final String warningMessage = "Your filename: \n\n" + absPath + FILENAME_WARNING_MESSAGE;
            // need to warn user that their filename size is large and may cause the program (Word, Excel, PPT) to error
            if (showAgain && arePopupsAllowed()) {
               //display warning once per session

               Displays.pendInDisplayThread(new Runnable() {
                  @Override
                  public void run() {
                     MessageDialog.openWarning(Displays.getActiveShell(), "Filename Size Warning", warningMessage);
                  }
               });

               showAgain = false;
            }
            //log the warning every time
            OseeLog.log(SkynetGuiPlugin.class, Level.WARNING, warningMessage);
            withinLimit = false;
         }
      }
      return withinLimit;
   }

   public static boolean arePopupsAllowed() {
      return arePopupsAllowed;
   }

   public static IFile getRenderFile(FileSystemRenderer renderer, List<Artifact> artifacts, IOseeBranch branch, PresentationType presentationType) throws OseeCoreException {
      Artifact artifact = artifacts.isEmpty() ? null : artifacts.get(0);
      String extension = renderer.getAssociatedExtension(artifact);
      return getRenderFile(renderer, artifacts, branch, presentationType, null, "." + extension);
   }

   public static IFile getRenderFile(IRenderer renderer, List<Artifact> artifacts, IOseeBranch branch, PresentationType presentationType, String pathPrefix, String extension) throws OseeCoreException {
      String mainName = getNameFromArtifacts(artifacts, presentationType);
      return getRenderFile(renderer, branch, presentationType, pathPrefix, mainName, extension);
   }

   public static String getRenderPath(IRenderer renderer, List<Artifact> artifacts, IOseeBranch branch, PresentationType presentationType, String pathPrefix, String extension) throws OseeCoreException {
      return getRenderFile(renderer, artifacts, branch, presentationType, pathPrefix, extension).getLocation().toOSString();
   }

   public static String getRenderPath(IRenderer renderer, IOseeBranch branch, PresentationType presentationType, String pathPrefix, String mainName, String extension) throws OseeCoreException {
      return getRenderFile(renderer, branch, presentationType, pathPrefix, mainName, extension).getLocation().toOSString();
   }

   public static IFile getRenderFile(IRenderer renderer, IOseeBranch branch, PresentationType presentationType, String pathPrefix, String mainName, String extension) throws OseeCoreException {
      String subFolder = toFileName(branch);
      String fileNamePrefix = null;
      if (Strings.isValid(pathPrefix)) {
         int index = pathPrefix.lastIndexOf("/");
         if (index != -1) {
            subFolder = pathPrefix.substring(0, index);
         }
         fileNamePrefix = pathPrefix.substring(index + 1);
      }

      String fileName = constructFilename(mainName, fileNamePrefix, extension);
      return getRenderFile(renderer, subFolder, fileName, presentationType);
   }

   private static String getNameFromArtifacts(List<Artifact> artifacts, PresentationType presentationType) {
      StringBuilder name = new StringBuilder(128);
      Artifact artifact = null;
      if (!artifacts.isEmpty()) {
         artifact = artifacts.iterator().next();
         if (artifacts.size() == 1) {
            name.append(artifact.getSafeName());
            name.append("_");
            if (artifact.isHistorical() || presentationType == PresentationType.DIFF) {
               name.append(artifact.getTransactionNumber());
               name.append("_");
            }
         } else {
            name.append(artifacts.size());
            name.append("artifacts_");
         }
      }
      return name.toString();
   }

   public static IFile getRenderFile(IRenderer renderer, String subFolder, String fileName, PresentationType presentationType) throws OseeCoreException {
      try {
         IFolder baseFolder = ensureRenderFolderExists(presentationType);
         IFolder renderFolder = baseFolder.getFolder(subFolder);
         if (!renderFolder.exists()) {
            renderFolder.create(true, true, null);
         }
         IFile file = renderFolder.getFile(fileName);
         renderer.setOption(IRenderer.RESULT_PATH_RETURN, file.getLocation().toOSString());
         return file;
      } catch (CoreException ex) {
         OseeExceptions.wrapAndThrow(ex);
         return null; // unreachable since wrapAndThrow() always throws an exception
      }
   }

   private static String constructFilename(String mainName, String fileNamePrefix, String extension) {
      StringBuilder name = new StringBuilder(128);
      if (Strings.isValid(fileNamePrefix)) {
         name.append(fileNamePrefix);
         name.append("_");
      }

      name.append(mainName);
      name.append("_");
      name.append(dateFormat.format(new Date()));
      name.append("-");
      name.append(generator.nextInt(99) + 1);
      name.append(extension);
      return name.toString();
   }

   public static String toFileName(IOseeBranch branch) throws OseeCoreException {
      return encode(Branch.getShortName(branch));
   }

   private static String encode(String name) throws OseeCoreException {
      String toReturn = null;
      try {
         toReturn = URLEncoder.encode(name, "UTF-8");
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return toReturn;
   }

   public static IFolder ensureRenderFolderExists(PresentationType presentationType) throws OseeCoreException {
      IFolder toReturn = null;
      switch (presentationType) {
         case MERGE:
         case DIFF:
            compareFolder = getOrCreateFolder(compareFolder, ".compare");
            toReturn = compareFolder;
            break;
         case SPECIALIZED_EDIT:
            workingFolder = getOrCreateFolder(workingFolder, ".working");
            toReturn = workingFolder;
            break;
         case PREVIEW:
            previewFolder = getOrCreateFolder(previewFolder, ".preview");
            toReturn = previewFolder;
            break;
         default:
            throw new OseeArgumentException("Unexpected presentation type: %s", presentationType);
      }
      return toReturn;
   }

   private static IFolder getOrCreateFolder(IFolder folder, String name) throws OseeCoreException {
      IFolder toCheck = folder;
      if (toCheck == null || !toCheck.exists()) {
         toCheck = OseeData.getFolder(name);
      }
      return toCheck;
   }
}