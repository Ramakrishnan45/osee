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
package org.eclipse.osee.ats.hyper;

import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSArtifact;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.config.BulkLoadAtsCache;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.ats.AtsOpenOption;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener2;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ActionHyperView extends HyperView implements IPartListener, IActionable, IFrameworkTransactionEventListener, IPerspectiveListener2 {

   public static String VIEW_ID = "org.eclipse.osee.ats.hyper.ActionHyperView";
   private static String HELP_CONTEXT_ID = "atsActionView";
   private static ActionHyperItem topAHI;
   private static ATSArtifact currentArtifact;

   public ActionHyperView() {
      super();
      PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(this);
      PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPerspectiveListener(this);
      setNodeColor(whiteColor);
      setCenterColor(whiteColor);
      setVerticalSelection(45);
   }

   @Override
   public void createPartControl(Composite top) {
      if (!DbConnectionExceptionComposite.dbConnectionIsOk(top)) {
         PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().removePartListener(this);
         PlatformUI.getWorkbench().getActiveWorkbenchWindow().removePerspectiveListener(this);
         return;
      }
      BulkLoadAtsCache.run(false);
      super.createPartControl(top);
      OseeAts.addBugToViewToolbar(this, this, AtsPlugin.getInstance(), VIEW_ID, "SkyWalker");
      AtsPlugin.getInstance().setHelp(top, HELP_CONTEXT_ID);
      AtsPlugin.getInstance().setHelp(composite, HELP_CONTEXT_ID);
      OseeEventManager.addListener(this);
   }

   public static ActionHyperView getArtifactHyperView() {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      try {
         IViewReference ivr = page.findViewReference(ActionHyperView.VIEW_ID);
         if (ivr != null && page.isPartVisible(ivr.getPart(false))) return (ActionHyperView) page.showView(ActionHyperView.VIEW_ID);
      } catch (PartInitException e1) {
         MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Launch Error",
               "Couldn't Get OSEE Hyper View " + e1.getMessage());
      }
      return null;
   }

   @Override
   public boolean provideBackForwardActions() {
      return false;
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      super.dispose();
   }

   @Override
   public void handleItemDoubleClick(HyperViewItem hvi) {
      Artifact art = ((ActionHyperItem) hvi).getArtifact();
      if (art == null) {
         ((ActionHyperItem) hvi).handleDoubleClick(hvi);

      } else if (art.isDeleted()) {
         AWorkbench.popup("ERROR", "Artifact has been deleted");
         return;
      } else {
         AtsLib.openAtsAction(art, AtsOpenOption.OpenOneOrPopupSelect);
      }
   }

   private boolean reviewsCreated = false;
   private boolean tasksReviewsCreated = false;

   @Override
   public void display() {
      try {
         getContainer().setCursor(new Cursor(null, SWT.NONE));
         if (currentArtifact == null || currentArtifact.isDeleted()) {
            return;
         }
         reviewsCreated = false;
         tasksReviewsCreated = false;
         ATSArtifact topArt = getTopArtifact(currentArtifact);
         if (topArt == null || topArt.isDeleted()) return;
         topAHI = new ActionHyperItem(topArt);
         if (topArt instanceof ActionArtifact) {
            for (TeamWorkFlowArtifact team : ((ActionArtifact) topArt).getTeamWorkFlowArtifacts()) {
               ActionHyperItem teamAHI = new ActionHyperItem(team);
               teamAHI.setRelationToolTip("Team");
               topAHI.addBottom(teamAHI);
               for (ReviewSMArtifact rev : team.getSmaMgr().getReviewManager().getReviews()) {
                  reviewsCreated = true;
                  ActionHyperItem reviewAHI = new ActionHyperItem(rev);
                  reviewAHI.setRelationToolTip("Review");
                  teamAHI.addBottom(reviewAHI);
                  addTasksAHIs(reviewAHI, rev);
               }
               addTasksAHIs(teamAHI, team);
            }
         }
         if (topArt instanceof ReviewSMArtifact) {
            addTasksAHIs(topAHI, topArt);
         }

         if (activeEditorIsActionEditor()) {
            topAHI.calculateCurrent(currentArtifact);
         }
         if (tasksReviewsCreated)
            setVerticalSelection(50);
         else if (reviewsCreated)
            setVerticalSelection(47);
         else
            setVerticalSelection(45);
         create(topAHI);
      } catch (Exception ex) {
         clear();
      }
   }

   private void addTasksAHIs(ActionHyperItem parentAHI, ATSArtifact artifact) throws OseeCoreException {
      if (!(artifact instanceof StateMachineArtifact)) return;
      if (((StateMachineArtifact) artifact).getSmaMgr().getTaskMgr().getTaskArtifacts().size() > 0) {
         if (artifact instanceof ReviewSMArtifact) tasksReviewsCreated = true;
         parentAHI.addBottom(new TasksActionHyperItem(
               ((StateMachineArtifact) artifact).getSmaMgr().getTaskMgr().getTaskArtifacts()));
      }
   }

   public ATSArtifact getTopArtifact(ATSArtifact art) throws OseeCoreException {
      ATSArtifact artifact = art;
      if (artifact instanceof TaskArtifact) {
         artifact = ((TaskArtifact) artifact).getParentSMA();
      }
      if (artifact instanceof TeamWorkFlowArtifact) {
         artifact = ((TeamWorkFlowArtifact) artifact).getParentActionArtifact();
      }
      if (artifact instanceof ReviewSMArtifact) {
         if (((ReviewSMArtifact) artifact).getParentActionArtifact() != null) {
            artifact = ((ReviewSMArtifact) artifact).getParentActionArtifact();
         }
      }
      if (artifact == null) OseeLog.log(AtsPlugin.class, Level.SEVERE, "Unknown parent " + art.getHumanReadableId());
      return artifact;
   }

   public boolean activeEditorIsActionEditor() {
      IWorkbenchPage page = getSite().getWorkbenchWindow().getActivePage();
      if (page == null) return false;
      IEditorPart editorPart = page.getActiveEditor();
      boolean result = (editorPart != null && (editorPart instanceof SMAEditor));
      return result;
   }

   public void processWindowActivated() {
      if (!this.getSite().getPage().isPartVisible(this)) return;
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      if (page != null) {
         IEditorPart editor = page.getActiveEditor();
         if (editor != null && (editor instanceof SMAEditor)) {
            currentArtifact = ((SMAEditor) editor).getSmaMgr().getSma();
            display();
         } else
            super.clear();
      }
   }

   public void processWindowDeActivated(IWorkbenchPart part) {
      processWindowActivated();
   }

   @Override
   public void partActivated(IWorkbenchPart part) {
      processWindowActivated();
   }

   @Override
   public void partBroughtToTop(IWorkbenchPart part) {
      processWindowActivated();
   }

   @Override
   public void partClosed(IWorkbenchPart part) {
      if (part.equals(this))
         dispose();
      else
         processWindowActivated();
   }

   @Override
   public void partDeactivated(IWorkbenchPart part) {
      processWindowActivated();
   }

   @Override
   public void partOpened(IWorkbenchPart part) {
      processWindowActivated();
   }

   public String getActionDescription() {
      if (currentArtifact != null && currentArtifact.isDeleted()) return String.format("Current Artifact - %s - %s",
            currentArtifact.getGuid(), currentArtifact.getDescriptiveName());
      return "";
   }

   @Override
   protected void clear() {
      super.clear();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IPerspectiveListener#perspectiveActivated(org.eclipse.ui.IWorkbenchPage,
    *      org.eclipse.ui.IPerspectiveDescriptor)
    */
   public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
      processWindowActivated();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IPerspectiveListener#perspectiveChanged(org.eclipse.ui.IWorkbenchPage,
    *      org.eclipse.ui.IPerspectiveDescriptor, java.lang.String)
    */
   public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {
      processWindowActivated();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IPerspectiveListener2#perspectiveChanged(org.eclipse.ui.IWorkbenchPage,
    *      org.eclipse.ui.IPerspectiveDescriptor, org.eclipse.ui.IWorkbenchPartReference,
    *      java.lang.String)
    */
   public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, IWorkbenchPartReference partRef, String changeId) {
      processWindowActivated();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IFrameworkTransactionEventListener#handleFrameworkTransactionEvent(org.eclipse.osee.framework.ui.plugin.event.Sender.Source, org.eclipse.osee.framework.skynet.core.eventx.FrameworkTransactionData)
    */
   @Override
   public void handleFrameworkTransactionEvent(Sender sender, FrameworkTransactionData transData) throws OseeCoreException {
      if (currentArtifact == null) return;
      if (transData.branchId != AtsPlugin.getAtsBranch().getBranchId()) return;
      if (transData.isDeleted(currentArtifact)) {
         Displays.ensureInDisplayThread(new Runnable() {
            /* (non-Javadoc)
             * @see java.lang.Runnable#run()
             */
            @Override
            public void run() {
               clear();
            }
         });
      }
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            display();
         }
      });
   }
}
