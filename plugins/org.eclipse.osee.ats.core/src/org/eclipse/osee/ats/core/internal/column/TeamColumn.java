/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.internal.column;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.review.IAtsReviewService;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.column.IAtsColumn;
import org.eclipse.osee.ats.core.internal.column.ev.AtsColumnService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class TeamColumn implements IAtsColumn {

   private final IAtsReviewService reviewService;

   public TeamColumn(IAtsReviewService reviewService) {
      this.reviewService = reviewService;
   }

   @Override
   public String getColumnText(IAtsObject atsObject) {
      String result = "";
      try {
         if (atsObject instanceof IAtsTeamWorkflow) {
            result = ((IAtsTeamWorkflow) atsObject).getTeamDefinition().getName();
         } else if (atsObject instanceof IAtsWorkItem) {
            result = getColumnText(((IAtsWorkItem) atsObject).getParentTeamWorkflow());
         }
         if (!Strings.isValid(result) && atsObject instanceof IAtsAbstractReview) {
            IAtsAbstractReview review = (IAtsAbstractReview) atsObject;
            if (reviewService.isStandAloneReview(review)) {
               List<IAtsTeamDefinition> teams = new ArrayList<>();
               for (IAtsActionableItem ai : review.getActionableItems()) {
                  if (ai.getTeamDefinitionInherited() != null) {
                     teams.add(ai.getTeamDefinition());
                  }
               }
               if (!teams.isEmpty()) {
                  result = Collections.toString(", ", teams);
               }
            }
         }
      } catch (OseeCoreException ex) {
         return AtsColumnService.CELL_ERROR_PREFIX + " - " + ex.getLocalizedMessage();
      }
      return result;
   }

}
