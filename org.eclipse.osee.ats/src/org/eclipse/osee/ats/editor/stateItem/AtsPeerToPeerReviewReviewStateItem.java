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
package org.eclipse.osee.ats.editor.stateItem;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.widgets.role.UserRole;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Donald G. Dunne
 */
public class AtsPeerToPeerReviewReviewStateItem extends AtsStateItem {

   public String getId() {
      return "osee.ats.peerToPeerReview.Review";
   }

   @Override
   public void transitioned(SMAManager smaMgr, String fromState, String toState, Collection<User> toAssignees, SkynetTransaction transaction) throws OseeCoreException {
      super.transitioned(smaMgr, fromState, toState, toAssignees, transaction);
      if (!toState.equals(PeerToPeerReviewArtifact.PeerToPeerReviewState.Review.name())) return;
      // Set Assignees to all user roles users
      Set<User> assignees = new HashSet<User>();
      PeerToPeerReviewArtifact peerArt = (PeerToPeerReviewArtifact) smaMgr.getSma();
      for (UserRole uRole : peerArt.getUserRoleManager().getUserRoles()) {
         if (!uRole.isCompleted()) {
            assignees.add(uRole.getUser());
         }
      }
      assignees.addAll(smaMgr.getStateMgr().getAssignees());

      smaMgr.getStateMgr().setAssignees(assignees);
      smaMgr.getSma().persist(transaction);
   }

   public String getDescription() throws OseeCoreException {
      return "AtsPeerToPeerReviewReviewStateItem - assign review state to all members of review as per role in prepare state.";
   }

}
