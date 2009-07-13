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
package org.eclipse.osee.framework.ui.skynet.artifact;

import java.net.HttpURLConnection;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.server.HttpResponse;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.httpRequests.BaseArtifactLoopbackCmd;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditorInput;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;

/**
 * @author Roberto E. Escobar
 */
public class OpenArtifactEditorLoopbackCmd extends BaseArtifactLoopbackCmd {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.httpRequests.BaseArtifactLoopbackCmd#isApplicable(java.lang.String)
    */
   @Override
   public boolean isApplicable(String cmd) {
      return cmd != null && cmd.equalsIgnoreCase("open.artifact");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.httpRequests.BaseArtifactLoopbackCmd#process(org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.util.Map, org.eclipse.osee.framework.core.client.server.HttpResponse)
    */
   @Override
   public void process(final Artifact artifact, final Map<String, String> parameters, final HttpResponse httpResponse) {
      if (artifact != null) {
         try {
            boolean hasPermissionToRead = false;
            try {
               hasPermissionToRead = AccessControlManager.checkObjectPermission(artifact, PermissionEnum.READ);
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }

            if (!hasPermissionToRead) {
               httpResponse.outputStandardError(HttpURLConnection.HTTP_UNAUTHORIZED, String.format(
                     "Access denied - User does not have read access to [%s]", artifact));
            } else {
               final MutableBoolean isDone = new MutableBoolean(false);

               Display.getDefault().asyncExec(new Runnable() {
                  public void run() {
                     try {
                        IEditorPart part =
                              AWorkbench.getActivePage().openEditor(new ArtifactEditorInput(artifact),
                                    ArtifactEditor.EDITOR_ID, true);
                        if (part != null) {
                           String html =
                                 AHTML.simplePage(artifact.getName() + " has been opened in OSEE on branch " + artifact.getBranch() + "<br><br>" + "<form><input type=button onClick='window.opener=self;window.close()' value='Close'></form>");
                           httpResponse.getPrintStream().println(html);
                        } else {
                           httpResponse.outputStandardError(HttpURLConnection.HTTP_INTERNAL_ERROR, String.format(
                                 "Unable to open: [%s]", artifact.getName()));
                        }
                     } catch (Exception ex) {
                        OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
                        httpResponse.outputStandardError(HttpURLConnection.HTTP_INTERNAL_ERROR, String.format(
                              "Unable to open: [%s]", artifact.getName()), ex);
                     } finally {
                        isDone.setValue(true);
                     }
                  }
               });

               int count = 1;
               while (!isDone.getValue() && count < 30) {
                  try {
                     Thread.sleep(350);
                  } catch (InterruptedException ex) {
                  }
                  count++;
               }
            }
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            httpResponse.outputStandardError(HttpURLConnection.HTTP_INTERNAL_ERROR, String.format(
                  "Unable to open: [%s]", artifact.getName()), ex);
         }
      } else {
         httpResponse.outputStandardError(HttpURLConnection.HTTP_BAD_REQUEST, "Unable to open null artifact");
      }
   }
}
