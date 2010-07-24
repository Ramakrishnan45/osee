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
package org.eclipse.osee.ats.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.artifact.TaskableStateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.XListDropViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class ImportTasksFromSimpleList extends AbstractBlam {

   public static String ASSIGNEES = "Assignees";
   public static String TASK_IMPORT_TITLES = "Task Import Titles";
   public static String TEAM_WORKFLOW = "Team Workflow (drop here)";
   private TaskableStateMachineArtifact taskableStateMachineArtifact;

   @Override
   public String getName() {
      return "Import Tasks From Simple List";
   }

   @Override
   public void runOperation(final VariableMap variableMap, IProgressMonitor monitor) throws OseeCoreException {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               List<Artifact> artifacts = variableMap.getArtifacts(TEAM_WORKFLOW);
               final List<User> assignees = Collections.castAll(variableMap.getArtifacts(ASSIGNEES));
               final List<String> titles = new ArrayList<String>();
               for (String title : variableMap.getString(TASK_IMPORT_TITLES).split("\n")) {
                  title = title.replaceAll("\r", "");
                  if (!title.equals("")) {
                     titles.add(title);
                  }
               }

               if (artifacts.isEmpty()) {
                  AWorkbench.popup("ERROR", "Must drag in Team Workflow to add tasks.");
                  return;
               }
               if (artifacts.size() > 1) {
                  AWorkbench.popup("ERROR", "Only drag ONE Team Workflow.");
                  return;
               }
               Artifact artifact = artifacts.iterator().next();
               if (!(artifact instanceof TeamWorkFlowArtifact)) {
                  AWorkbench.popup("ERROR", "Artifact MUST be Team Workflow");
                  return;
               }
               if (titles == null || titles.isEmpty()) {
                  AWorkbench.popup("ERROR", "Must enter title(s).");
                  return;
               }
               try {
                  final TeamWorkFlowArtifact teamArt = (TeamWorkFlowArtifact) artifact;
                  SkynetTransaction transaction =
                     new SkynetTransaction(AtsUtil.getAtsBranch(), "Import Tasks from Simple List");
                  teamArt.createTasks(titles, assignees, transaction);
                  teamArt.persist(transaction);
                  transaction.execute();
               } catch (Exception ex) {
                  OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
                  return;
               }

               SMAEditor.editArtifact(artifact);
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         };
      });
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
      super.widgetCreated(xWidget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
      if (xWidget.getLabel().equals(TEAM_WORKFLOW) && taskableStateMachineArtifact != null) {
         XListDropViewer viewer = (XListDropViewer) xWidget;
         viewer.setInput(Arrays.asList(taskableStateMachineArtifact));
      }
   }

   @Override
   public String getXWidgetsXml() {
      StringBuffer buffer = new StringBuffer("<xWidgets>");
      buffer.append("<XWidget xwidgetType=\"XListDropViewer\" displayName=\"" + TEAM_WORKFLOW + "\" />");
      buffer.append("<XWidget xwidgetType=\"XText\" fill=\"Vertically\" displayName=\"" + TASK_IMPORT_TITLES + "\" />");
      buffer.append("<XWidget xwidgetType=\"XMembersList\" displayName=\"" + ASSIGNEES + "\" />");
      buffer.append("</xWidgets>");
      return buffer.toString();
   }

   @Override
   public String getDescriptionUsage() {
      return "Import tasks from spreadsheet into given Team Workflow";
   }

   /**
    * @return the TaskableStateMachineArtifact
    */
   public TaskableStateMachineArtifact getTaskableStateMachineArtifact() {
      return taskableStateMachineArtifact;
   }

   /**
    * @param defaultTeamWorkflowArtifact the defaultTeamWorkflowArtifact to set
    */
   public void setTaskableStateMachineArtifact(TaskableStateMachineArtifact taskableStateMachineArtifact) {
      this.taskableStateMachineArtifact = taskableStateMachineArtifact;
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("ATS");
   }
}