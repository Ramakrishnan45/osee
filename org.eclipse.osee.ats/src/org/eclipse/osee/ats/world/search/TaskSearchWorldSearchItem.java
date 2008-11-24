/*
 * Created on Nov 20, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.world.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact.VersionReleaseType;
import org.eclipse.osee.ats.task.TaskEditorParameterSearchItem;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.widgets.XHyperlabelTeamDefinitionSelection;
import org.eclipse.osee.ats.world.search.TeamWorldSearchItem.ReleasedOption;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlabelGroupSelection;
import org.eclipse.osee.framework.ui.skynet.widgets.XMembersCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class TaskSearchWorldSearchItem extends TaskEditorParameterSearchItem {

   private XMembersCombo assigneeCombo;
   private XCheckBox includeCompletedCancelledCheckbox;
   private XHyperlabelTeamDefinitionSelection teamCombo = null;
   private XHyperlabelGroupSelection groupWidget = null;
   private XCombo versionCombo = null;

   /**
    * @param worldSearchItem
    */
   public TaskSearchWorldSearchItem(WorldSearchItem worldSearchItem) {
      super(worldSearchItem);
   }

   /**
    * @param name
    * @param loadView
    */
   public TaskSearchWorldSearchItem() {
      super("Task Search");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.task.TaskEditorParameterSearchItem#getParameterXWidgetXml()
    */
   @Override
   public String getParameterXWidgetXml() throws OseeCoreException {
      return "<xWidgets>" +
      //
      "<XWidget xwidgetType=\"XHyperlabelTeamDefinitionSelection\" displayName=\"Team Definitions(s)\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget xwidgetType=\"XCombo()\" beginComposite=\"8\" displayName=\"Version\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget xwidgetType=\"XHyperlabelGroupSelection\" displayName=\"Group(s)\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget xwidgetType=\"XMembersCombo\" displayName=\"Assignee\" horizontalLabel=\"true\"/>" +
      //
      "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Include Completed/Cancelled\" defaultValue=\"false\" labelAfter=\"true\" horizontalLabel=\"true\"/>" +
      //
      "</xWidgets>";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.task.TaskEditorParameterSearchItem#getTaskEditorTaskArtifacts()
    */
   @Override
   public Collection<? extends Artifact> getTaskEditorTaskArtifacts() throws OseeCoreException {
      List<Artifact> workflows = new ArrayList<Artifact>();
      Collection<TeamDefinitionArtifact> teamDefs = getSelectedTeamDefinitions();
      VersionArtifact verArt = getSelectedVersionArtifact();
      Collection<Artifact> groups = getSelectedGroups();
      User user = getSelectedUser();

      // If only user selected, handle that case separately
      if (verArt == null && teamDefs.size() == 0 && user != null) {
         return handleOnlyUserSelected();
      } // If version specified, get workflows from targeted relation
      if (verArt != null) {
         for (Artifact art : verArt.getRelatedArtifacts(AtsRelation.TeamWorkflowTargetedForVersion_Workflow)) {
            if (teamDefs.size() == 0) {
               workflows.add(art);
            }
            // Filter by team def if specified
            else if (teamDefs.contains((((TeamWorkFlowArtifact) art).getTeamDefinition()))) {
               workflows.add(art);
            }
         }
      }
      // Else, get workflows from teamdefs
      else if (teamDefs.size() > 0) {
         TeamWorldSearchItem teamWorldSearchItem =
               new TeamWorldSearchItem("", teamDefs, true, false, false, null, null, ReleasedOption.UnReleased);
         workflows.addAll(teamWorldSearchItem.performSearchGetResults(false, SearchType.Search));
      } else if (groups.size() > 0) {
         Set<TaskArtifact> taskArts = new HashSet<TaskArtifact>();
         for (Artifact groupArt : groups) {
            for (Artifact art : groupArt.getRelatedArtifacts(CoreRelationEnumeration.UNIVERSAL_GROUPING__MEMBERS)) {
               if (art instanceof TaskArtifact) {
                  taskArts.add((TaskArtifact) art);
               } else if (art instanceof StateMachineArtifact) {
                  taskArts.addAll(((StateMachineArtifact) art).getSmaMgr().getTaskMgr().getTaskArtifacts());
               }
            }
         }
         return filterByCompletedAndSelectedUser(taskArts);
      }

      // Bulk load tasks related to workflows
      Collection<Artifact> artifacts = RelationManager.getRelatedArtifacts(workflows, 1, AtsRelation.SmaToTask_Task);

      // Apply the remaining criteria
      return filterByCompletedAndSelectedUser(artifacts);
   }

   private Collection<TaskArtifact> handleOnlyUserSelected() throws OseeCoreException {
      return filterByCompletedAndSelectedUser(getUserAssignedTaskArtifacts());
   }

   private Collection<TaskArtifact> getUserAssignedTaskArtifacts() throws OseeCoreException {
      Set<TaskArtifact> tasks = new HashSet<TaskArtifact>();
      for (Artifact art : getSelectedUser().getRelatedArtifacts(CoreRelationEnumeration.Users_Artifact)) {
         if (art instanceof TaskArtifact) {
            tasks.add((TaskArtifact) art);
         }
      }
      return tasks;
   }

   private Collection<TaskArtifact> filterByCompletedAndSelectedUser(Collection<? extends Artifact> artifacts) throws OseeCoreException {
      Set<TaskArtifact> tasks = new HashSet<TaskArtifact>();
      for (Artifact art : artifacts) {
         TaskArtifact taskArt = (TaskArtifact) art;
         // If not include completed and task is such, skip this task
         if (!isIncludeCompletedCancelledCheckbox() && taskArt.getSmaMgr().isCancelledOrCompleted()) {
            continue;
         }
         // If include completed and task is such and user not implementer, skip this task
         if (isIncludeCompletedCancelledCheckbox() && taskArt.getSmaMgr().isCancelledOrCompleted() && getSelectedUser() != null && taskArt.getImplementers().contains(
               getSelectedUser())) {
            tasks.add(taskArt);
            continue;
         }
         // If user is selected and not user is assigned, skip this task
         else if (getSelectedUser() != null && !taskArt.getSmaMgr().getStateMgr().getAssignees().contains(
               getSelectedUser())) {
            continue;
         }
         tasks.add(taskArt);
      }
      return tasks;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.task.TaskEditorParameterSearchItem#isParameterSelectionValid()
    */
   @Override
   public Result isParameterSelectionValid() throws OseeCoreException {
      if (getSelectedUser() != null && isIncludeCompletedCancelledCheckbox() && getSelectedVersionArtifact() == null && getSelectedTeamDefinitions().size() == 0) {
         // This case is unsupported  and should be filtered out prior to this point
         throw new OseeArgumentException("Unsupported User and Include Completed selected.");
      }

      // If only user selected, handle that case separately
      if (getSelectedVersionArtifact() == null && getSelectedTeamDefinitions().size() == 0 && getSelectedUser() != null) {
         return Result.TrueResult;
      }

      if (getSelectedGroups().size() > 0 && (getSelectedVersionArtifact() != null || getSelectedTeamDefinitions().size() > 0)) {
         // This case is unsupported  and should be filtered out prior to this point
         throw new OseeArgumentException("Unsupported Groups selection with Version or Team(s).");
      }
      return Result.TrueResult;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.task.ITaskEditorProvider#getTableLoadOptions()
    */
   @Override
   public Collection<TableLoadOption> getTableLoadOptions() throws OseeCoreException {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.task.ITaskEditorProvider#getTaskEditorLabel(org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType)
    */
   @Override
   public String getTaskEditorLabel(SearchType searchType) throws OseeCoreException {
      StringBuffer sb = new StringBuffer();
      Collection<TeamDefinitionArtifact> teamDefs = getSelectedTeamDefinitions();
      if (teamDefs.size() > 0) {
         sb.append(" - Teams: " + org.eclipse.osee.framework.jdk.core.util.Collections.toString(",", teamDefs));
      }
      if (getSelectedVersionArtifact() != null) {
         sb.append(" - Version: " + getSelectedVersionArtifact());
      }
      if (getSelectedGroups().size() > 0) {
         sb.append(" - Groups: " + Collections.toString(",", getSelectedGroups()));
      }
      if (getSelectedUser() != null) {
         sb.append(" - Assignee: " + getSelectedUser());
      }
      if (isIncludeCompletedCancelledCheckbox()) {
         sb.append(" - Include Completed/Cancelled");
      }
      return "Tasks" + sb.toString();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.workflow.IDynamicWidgetLayoutListener#createXWidgetLayoutData(org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData, org.eclipse.osee.framework.ui.skynet.widgets.XWidget, org.eclipse.ui.forms.widgets.FormToolkit, org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener, boolean)
    */
   @Override
   public void createXWidgetLayoutData(DynamicXWidgetLayoutData layoutData, XWidget widget, FormToolkit toolkit, Artifact art, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.workflow.IDynamicWidgetLayoutListener#widgetCreating(org.eclipse.osee.framework.ui.skynet.widgets.XWidget, org.eclipse.ui.forms.widgets.FormToolkit, org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout, org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener, boolean)
    */
   @Override
   public void widgetCreating(XWidget widget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#widgetCreated(org.eclipse.osee.framework.ui.skynet.widgets.XWidget, org.eclipse.ui.forms.widgets.FormToolkit, org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout, org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener, boolean)
    */
   @Override
   public void widgetCreated(XWidget widget, FormToolkit toolkit, Artifact art, DynamicXWidgetLayout dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
      if (widget.getLabel().equals("Group(s)")) {
         groupWidget = (XHyperlabelGroupSelection) widget;
      }
      if (widget.getLabel().equals("Assignee")) {
         assigneeCombo = (XMembersCombo) widget;
      }
      if (widget.getLabel().equals("Include Completed/Cancelled")) {
         includeCompletedCancelledCheckbox = (XCheckBox) widget;
      }
      if (widget.getLabel().equals("Version")) {
         versionCombo = (XCombo) widget;
         versionCombo.getComboBox().setVisibleItemCount(25);
         widget.getLabelWidget().setToolTipText("Select Team to populate Version list");
      }
      if (widget.getLabel().equals("Team Definitions(s)")) {
         teamCombo = (XHyperlabelTeamDefinitionSelection) widget;
         teamCombo.addXModifiedListener(new XModifiedListener() {
            @Override
            public void widgetModified(XWidget widget) {
               if (versionCombo != null) {
                  try {
                     Collection<TeamDefinitionArtifact> teamDefArts = getSelectedTeamDefinitions();
                     if (teamDefArts.size() == 0) {
                        versionCombo.setDataStrings(new String[] {});
                        return;
                     }
                     TeamDefinitionArtifact teamDefHoldingVersions =
                           teamDefArts.iterator().next().getTeamDefinitionHoldingVersions();
                     if (teamDefHoldingVersions == null) {
                        versionCombo.setDataStrings(new String[] {});
                        return;
                     }
                     Collection<String> names =
                           Artifacts.artNames(teamDefHoldingVersions.getVersionsArtifacts(VersionReleaseType.Both));
                     if (names.size() == 0) {
                        versionCombo.setDataStrings(new String[] {});
                        return;
                     }
                     versionCombo.setDataStrings(names.toArray(new String[names.size()]));
                  } catch (Exception ex) {
                     OSEELog.logException(AtsPlugin.class, ex, true);
                  }
               }
            }
         });
      }
   }

   private User getSelectedUser() {
      if (assigneeCombo == null) return null;
      return assigneeCombo.getUser();
   }

   private boolean isIncludeCompletedCancelledCheckbox() {
      if (includeCompletedCancelledCheckbox == null) return false;
      return includeCompletedCancelledCheckbox.isSelected();
   }

   private VersionArtifact getSelectedVersionArtifact() throws OseeCoreException {
      if (versionCombo == null) return null;
      String versionStr = versionCombo.get();
      if (versionStr == null || versionStr.equals("")) return null;
      Collection<TeamDefinitionArtifact> teamDefs = getSelectedTeamDefinitions();
      if (teamDefs.size() > 0) {
         TeamDefinitionArtifact teamDefHoldingVersions = teamDefs.iterator().next().getTeamDefinitionHoldingVersions();
         if (teamDefHoldingVersions == null) return null;
         for (VersionArtifact versionArtifact : teamDefHoldingVersions.getVersionsArtifacts(VersionReleaseType.Both)) {
            if (versionArtifact.getDescriptiveName().equals(versionStr)) {
               return versionArtifact;
            }
         }
      }
      return null;
   }

   private Collection<TeamDefinitionArtifact> getSelectedTeamDefinitions() throws OseeCoreException {
      return teamCombo.getSelectedTeamDefintions();
   }

   private Collection<Artifact> getSelectedGroups() throws OseeCoreException {
      return groupWidget.getSelectedGroups();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.search.WorldSearchItem#copy()
    */
   @Override
   public TaskSearchWorldSearchItem copy() {
      return new TaskSearchWorldSearchItem(this);
   }

}
