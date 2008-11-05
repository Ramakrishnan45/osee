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
package org.eclipse.osee.ats.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.util.widgets.task.IXTaskViewer;
import org.eclipse.osee.ats.world.search.WorldSearchItem;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.OseeContributionItem;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

/**
 * @author Donald G. Dunne
 */
public class TaskEditor extends AbstractArtifactEditor implements IDirtiableEditor, IXTaskViewer {
   public static final String EDITOR_ID = "org.eclipse.osee.ats.editor.TaskEditor";
   private int taskPageIndex;
   private SMATaskComposite taskComposite;
   private Collection<TaskArtifact> tasks = new HashSet<TaskArtifact>();

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor#doSave(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void doSave(IProgressMonitor monitor) {
      try {
         SkynetTransaction transaction = new SkynetTransaction(BranchManager.getAtsBranch());
         for (TaskArtifact taskArt : tasks) {
            taskArt.saveSMA(transaction);
            }
         transaction.execute();
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
      onDirtied();
   }

   public static void editArtifacts(final TaskEditorInput input, TableLoadOption... tableLoadOptions) {
      Set<TableLoadOption> options = new HashSet<TableLoadOption>();
      options.addAll(Arrays.asList(tableLoadOptions));
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         public void run() {
            IWorkbenchPage page = AWorkbench.getActivePage();
            try {
               page.openEditor(input, EDITOR_ID);
            } catch (PartInitException ex) {
               OSEELog.logException(AtsPlugin.class, ex, true);
            }
         }
      }, options.contains(TableLoadOption.ForcePend));

   }

   public ArrayList<Artifact> getLoadedArtifacts() {
      return taskComposite.getXTask().getXViewer().getLoadedArtifacts();
   }

   @Override
   public boolean isSaveOnCloseNeeded() {
      return isDirty();
   }

   @Override
   public void dispose() {
      for (TaskArtifact taskArt : tasks)
         if (taskArt != null && !taskArt.isDeleted() && taskArt.isSMAEditorDirty().isTrue()) taskArt.revertSMA();
      if (taskComposite != null) {
         taskComposite.dispose();
      }
      super.dispose();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.forms.editor.FormEditor#isDirty()
    */
   @Override
   public boolean isDirty() {
      for (TaskArtifact taskArt : tasks) {
         if (taskArt.isDeleted())
            continue;
         else if (taskArt.isSMAEditorDirty().isTrue()) return true;
      }
      return false;
   }

   @Override
   public String toString() {
      return "TaskEditor";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
    */
   @Override
   protected void addPages() {

      try {
         OseeContributionItem.addTo(this, true);

         IEditorInput editorInput = getEditorInput();
         if (editorInput instanceof TaskEditorInput) {
            TaskEditorInput aei = (TaskEditorInput) editorInput;
            tasks = (aei).getTaskArts();
         } else
            throw new IllegalArgumentException("Editor Input not TaskEditorInput");

         setPartName(((TaskEditorInput) editorInput).getName());

         // Create Tasks tab
         taskComposite = new SMATaskComposite(this, getContainer(), SWT.NONE);
         taskPageIndex = addPage(taskComposite);
         setPageText(taskPageIndex, "Tasks");

         setActivePage(taskPageIndex);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void onDirtied() {
      Displays.ensureInDisplayThread(new Runnable() {

         public void run() {
            firePropertyChange(PROP_DIRTY);
         }
      });
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#getCurrentStateName()
    */
   public String getCurrentStateName() throws OseeCoreException {
      return "";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#getEditor()
    */
   public IDirtiableEditor getEditor() throws OseeCoreException {
      return this;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#getParentSmaMgr()
    */
   public SMAManager getParentSmaMgr() throws OseeCoreException {
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#getTabName()
    */
   public String getTabName() throws OseeCoreException {
      return "Tasks";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#getTaskArtifacts(java.lang.String)
    */
   public Collection<TaskArtifact> getTaskArtifacts(String stateName) throws OseeCoreException {
      return tasks;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#isTaskable()
    */
   public boolean isTaskable() throws OseeCoreException {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.util.widgets.task.IXTaskViewer#isTasksEditable()
    */
   public boolean isTasksEditable() throws OseeCoreException {
      return true;
   }

   public static void loadTable(WorldSearchItem searchItem, TableLoadOption... tableLoadOptions) throws InterruptedException, OseeCoreException {
      Set<TableLoadOption> options = new HashSet<TableLoadOption>();
      options.addAll(Arrays.asList(tableLoadOptions));
      searchItem.setCancelled(false);
      Result result = AtsPlugin.areOSEEServicesAvailable();
      if (result.isFalse()) {
         result.popup();
         return;
      }

      if (searchItem == null) return;

      if (!options.contains(TableLoadOption.NoUI)) searchItem.performUI(SearchType.Search);
      if (searchItem.isCancelled()) return;

      LoadTableJob job = null;
      job = new LoadTableJob(searchItem, SearchType.Search, tableLoadOptions);
      job.setUser(false);
      job.setPriority(Job.LONG);
      job.schedule();
      if (options.contains(TableLoadOption.ForcePend)) job.join();
   }

   private static class LoadTableJob extends Job {

      private final WorldSearchItem searchItem;
      private boolean cancel = false;
      private final SearchType searchType;
      private final TableLoadOption[] tableLoadOptions;

      public LoadTableJob(WorldSearchItem searchItem, SearchType searchType, TableLoadOption... tableLoadOptions) {
         super("Loading \"" + searchItem.getSelectedName(searchType) + "\"...");
         this.searchItem = searchItem;
         this.searchType = searchType;
         this.tableLoadOptions = tableLoadOptions;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
       */
      @Override
      protected IStatus run(IProgressMonitor monitor) {
         cancel = false;
         searchItem.setCancelled(cancel);
         final Collection<Artifact> artifacts;
         try {
            artifacts = searchItem.performSearchGetResults(false, searchType);
            if (artifacts.size() == 0) {
               if (searchItem.isCancelled()) {
                  monitor.done();
                  return Status.CANCEL_STATUS;
               } else {
                  monitor.done();
                  Displays.ensureInDisplayThread(new Runnable() {
                     /* (non-Javadoc)
                      * @see java.lang.Runnable#run()
                      */
                     @Override
                     public void run() {
                        AWorkbench.popup("ERROR", "No Tasks Found for \"" + searchItem.getName() + "\"");
                     }
                  }, true);
               }
               return Status.OK_STATUS;
            }
            List<TaskArtifact> taskArts = new ArrayList<TaskArtifact>();
            for (Artifact artifact : artifacts)
               if (artifact instanceof TaskArtifact) taskArts.add((TaskArtifact) artifact);
            TaskEditorInput input =
                  new TaskEditorInput(
                        "Tasks for \"" + (searchItem.getSelectedName(searchType) != null ? searchItem.getSelectedName(searchType) : "") + "\"",
                        taskArts);
            TaskEditor.editArtifacts(input, tableLoadOptions);

         } catch (final Exception ex) {
            monitor.done();
            return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, "Can't load tasks", ex);
         }
         monitor.done();
         return Status.OK_STATUS;
      }
   }

}
