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
package org.eclipse.osee.ats.workflow.page;

import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TaskStates;
import org.eclipse.osee.ats.workflow.flow.TaskWorkflowDefinition;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions.RuleWorkItemId;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageType;

/**
 * @author Donald G. Dunne
 */
public class AtsTaskInWorkPageDefinition extends WorkPageDefinition {

   public final static String ID = TaskWorkflowDefinition.ID + "." + TaskStates.InWork.getPageName();

   public AtsTaskInWorkPageDefinition(int ordinal) {
      this(TaskStates.InWork.getPageName(), ID, null, ordinal);
   }

   public AtsTaskInWorkPageDefinition(String name, String pageId, String parentId, int ordinal) {
      super(name, pageId, parentId, WorkPageType.Working, ordinal);
      addWorkItem(RuleWorkItemId.atsRequireStateHourSpentPrompt.name());
      addWorkItem("ats.Title");
      addWorkItem(AtsWorkDefinitions.ATS_DESCRIPTION_NOT_REQUIRED_ID);
      addWorkItem(AtsAttributeTypes.Resolution);
      addWorkItem(AtsAttributeTypes.EstimatedHours);
      addWorkItem(AtsAttributeTypes.EstimatedCompletionDate);
      addWorkItem(AtsAttributeTypes.RelatedToState);
      addWorkItem(AtsAttributeTypes.SmaNote);
      addWorkItem(AtsAttributeTypes.Category1);
   }

}
