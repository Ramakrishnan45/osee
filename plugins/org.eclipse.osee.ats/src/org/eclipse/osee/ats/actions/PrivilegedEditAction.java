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

package org.eclipse.osee.ats.actions;

import java.util.Set;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.util.PriviledgedUserManager;
import org.eclipse.osee.ats.util.ReadOnlyHyperlinkListener;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicUser;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class PrivilegedEditAction extends Action {

   private final AbstractWorkflowArtifact sma;
   private final SMAEditor editor;

   public PrivilegedEditAction(AbstractWorkflowArtifact sma, SMAEditor editor) {
      super();
      this.sma = sma;
      this.editor = editor;
      setText("Privileged Edit");
      setToolTipText(getText());
   }

   @Override
   public void run() {
      togglePriviledgedEdit();
   }

   private void togglePriviledgedEdit() {
      try {
         if (sma.isReadOnly()) {
            new ReadOnlyHyperlinkListener(sma).linkActivated(null);
         }
         if (editor.isPriviledgedEditModeEnabled()) {
            if (MessageDialog.openQuestion(Displays.getActiveShell(), "Diable Privileged Edit",
               "Privileged Edit Mode Enabled.\n\nDisable?\n\nNote: (changes will be saved)")) {
               editor.setPriviledgedEditMode(false);
            }
         } else {
            Set<IBasicUser> users = PriviledgedUserManager.getPrivilegedUsers(sma);
            if (AtsUtilCore.isAtsAdmin()) {
               users.add(UserManager.getUser());
            }
            StringBuffer stringBuffer = new StringBuffer();
            for (IBasicUser user : users) {
               stringBuffer.append(user.getName());
               stringBuffer.append("\n");
            }
            String buttons[];
            boolean iAmPrivileged = users.contains(UserManager.getUser());
            if (iAmPrivileged) {
               buttons = new String[] {"Override and Edit", "Cancel"};
            } else {
               buttons = new String[] {"Cancel"};
            }
            MessageDialog dialog =
               new MessageDialog(
                  Displays.getActiveShell(),
                  "Privileged Edit",
                  null,
                  "The following users have the ability to edit this " + sma.getArtifactTypeName() + " in case of emergency.\n\n" + stringBuffer.toString(),
                  MessageDialog.QUESTION, buttons, 0);
            int result = dialog.open();
            if (iAmPrivileged && result == 0) {
               editor.setPriviledgedEditMode(true);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.PRIVILEDGED_EDIT);
   }
}
