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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.core.util.SubscribeManager;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.util.SubscribeManagerUI;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class SubscribedAction extends Action {

   private final ISelectedAtsArtifacts selectedAtsArtifacts;

   public SubscribedAction(ISelectedAtsArtifacts selectedAtsArtifacts) {
      super();
      this.selectedAtsArtifacts = selectedAtsArtifacts;
      updateName();
   }

   private void updateName() {
      String title = "Subscribed";
      try {
         if (getSelectedSubscribableArts().size() == 1) {
            title =
               SubscribeManager.amISubscribed(getSelectedSubscribableArts().iterator().next()) ? "Remove Subscribed" : "Add as Subscribed";
         } else {
            title = "Toggle Subscribed";
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      setText(title);
      setToolTipText(title);
   }

   public Collection<AbstractWorkflowArtifact> getSelectedSubscribableArts() throws OseeCoreException {
      List<AbstractWorkflowArtifact> favoritableArts = new ArrayList<AbstractWorkflowArtifact>();
      for (Artifact art : selectedAtsArtifacts.getSelectedSMAArtifacts()) {
         if (art instanceof AbstractWorkflowArtifact) {
            favoritableArts.add((AbstractWorkflowArtifact) art);
         }
      }
      return favoritableArts;
   }

   @Override
   public void run() {
      try {
         new SubscribeManagerUI(getSelectedSubscribableArts()).toggleSubscribe();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.SUBSCRIBED);
   }

   public void updateEnablement() {
      try {
         setEnabled(getSelectedSubscribableArts().size() > 0);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         setEnabled(false);
      }
      updateName();
   }

}
