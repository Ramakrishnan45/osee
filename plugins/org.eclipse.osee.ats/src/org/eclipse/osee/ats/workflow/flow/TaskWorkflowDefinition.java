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
package org.eclipse.osee.ats.workflow.flow;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.artifact.TaskStates;
import org.eclipse.osee.ats.util.TeamState;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.ats.workflow.page.AtsCancelledWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsCompletedWorkPageDefinition;
import org.eclipse.osee.ats.workflow.page.AtsTaskInWorkPageDefinition;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageType;

/**
 * @author Donald G. Dunne
 */
public class TaskWorkflowDefinition extends WorkFlowDefinition {

   public final static String ID = "osee.ats.taskWorkflow";

   public TaskWorkflowDefinition() {
      this(ID, ID);
      startPageId = TaskStates.InWork.getPageName();
   }

   public TaskWorkflowDefinition(Artifact artifact) throws OseeCoreException {
      super(artifact);
      throw new OseeStateException("This constructor should never be used.");
   }

   public void config(WriteType writeType, XResultData xResultData) throws OseeCoreException {
      AtsWorkDefinitions.importWorkItemDefinitionsIntoDb(writeType, xResultData, getAtsWorkDefinitions());
   }

   public static List<WorkItemDefinition> getAtsWorkDefinitions() {
      List<WorkItemDefinition> workItems = new ArrayList<WorkItemDefinition>();

      // Add Task Page and Workflow Definition
      workItems.add(new AtsTaskInWorkPageDefinition(1));
      workItems.add(new WorkPageDefinition(TeamState.Completed.getPageName(),
         ID + "." + TaskStates.Completed.getPageName(), AtsCompletedWorkPageDefinition.ID, WorkPageType.Completed, 2));
      workItems.add(new WorkPageDefinition(TeamState.Cancelled.getPageName(),
         ID + "." + TaskStates.Cancelled.getPageName(), AtsCancelledWorkPageDefinition.ID, WorkPageType.Cancelled, 3));
      workItems.add(new TaskWorkflowDefinition());

      return workItems;
   }

   public TaskWorkflowDefinition(String name, String id) {
      super(name, id, null);
      addPageTransition(TaskStates.InWork.getPageName(), TaskStates.Completed.getPageName(),
         TransitionType.ToPageAsDefault);

      // Add return transitions
      addPageTransition(TaskStates.Completed.getPageName(), TaskStates.InWork.getPageName(),
         TransitionType.ToPageAsReturn);

      // Add cancelled transitions
      addPageTransitionToPageAndReturn(TaskStates.InWork.getPageName(), TaskStates.Cancelled.getPageName());
   }

}
