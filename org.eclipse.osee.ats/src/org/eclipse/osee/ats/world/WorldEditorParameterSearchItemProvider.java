/*
 * Created on Nov 6, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.world;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.world.search.WorldSearchItem;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IDynamicWidgetLayoutListener;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class WorldEditorParameterSearchItemProvider extends WorldEditorProvider implements IWorldEditorParameterProvider {

   private final WorldEditorParameterSearchItem worldParameterSearchItem;
   public static String ENTER_OPTIONS_AND_SELECT_SEARCH = "Enter options and select \"Search\"";
   private boolean firstTime = true;

   public WorldEditorParameterSearchItemProvider(WorldEditorParameterSearchItem worldParameterSearchItem) {
      this(worldParameterSearchItem, null, TableLoadOption.None);
   }

   public WorldEditorParameterSearchItemProvider(WorldEditorParameterSearchItem worldParameterSearchItem, CustomizeData customizeData, TableLoadOption... tableLoadOptions) {
      super(customizeData, tableLoadOptions);
      this.worldParameterSearchItem = worldParameterSearchItem;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldEditorProvider#copy(org.eclipse.osee.ats.world.IWorldEditorProvider)
    */
   @Override
   public IWorldEditorProvider copyProvider() {
      return new WorldEditorParameterSearchItemProvider(
            (WorldEditorParameterSearchItem) worldParameterSearchItem.copy(), customizeData, tableLoadOptions);
   }

   /**
    * @return the worldSearchItem
    */
   public WorldSearchItem getWorldSearchItem() {
      return worldParameterSearchItem;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldEditorProvider#getName()
    */
   @Override
   public String getName() throws OseeCoreException {
      return worldParameterSearchItem.getName();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldEditorProvider#run(org.eclipse.osee.ats.world.WorldEditor)
    */
   @Override
   public void run(WorldEditor worldEditor, SearchType searchType, boolean forcePend) throws OseeCoreException {

      if (firstTime) {
         firstTime = false;
         worldEditor.setTableTitle(ENTER_OPTIONS_AND_SELECT_SEARCH, false);
         return;
      }
      if (worldParameterSearchItem.isCancelled()) return;

      Result result = worldParameterSearchItem.isParameterSelectionValid();
      if (result.isFalse()) {
         result.popup();
         return;
      }

      LoadTableJob job = null;
      job = new LoadTableJob(worldEditor, worldParameterSearchItem, searchType, tableLoadOptions);
      job.setUser(false);
      job.setPriority(Job.LONG);
      job.schedule();
      if (forcePend) {
         try {
            job.join();
         } catch (InterruptedException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         }
      }
   }
   private class LoadTableJob extends Job {

      private final WorldEditorParameterSearchItem worldParameterSearchItem;
      private boolean cancel = false;
      private final SearchType searchType;
      private final WorldEditor worldEditor;
      private final TableLoadOption[] tableLoadOptions;

      public LoadTableJob(WorldEditor worldEditor, WorldEditorParameterSearchItem worldParameterSearchItem, SearchType searchType, TableLoadOption[] tableLoadOptions) throws OseeCoreException {
         super("Loading \"" + worldParameterSearchItem.getSelectedName(searchType) + "\"...");
         this.worldEditor = worldEditor;
         this.worldParameterSearchItem = worldParameterSearchItem;
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

         String selectedName = "";
         try {
            selectedName = worldParameterSearchItem.getSelectedName(searchType);
            worldEditor.setEditorTitle(selectedName != null ? selectedName : worldParameterSearchItem.getName());
            worldEditor.setTableTitle("Loading \"" + (selectedName != null ? selectedName : "") + "\"...", false);
            cancel = false;
            worldParameterSearchItem.setCancelled(cancel);
            final Collection<? extends Artifact> artifacts;
            worldEditor.getWorldComposite().getXViewer().clear();
            artifacts = worldParameterSearchItem.performSearchGetResults(searchType);
            if (artifacts.size() == 0) {
               if (worldParameterSearchItem.isCancelled()) {
                  monitor.done();
                  worldEditor.setTableTitle("CANCELLED - " + selectedName, false);
                  return Status.CANCEL_STATUS;
               } else {
                  monitor.done();
                  worldEditor.setTableTitle("No Results Found - " + selectedName, true);
                  return Status.OK_STATUS;
               }
            }
            worldEditor.getWorldComposite().load(selectedName, artifacts, customizeData, tableLoadOptions);
         } catch (final Exception ex) {
            String str = "Exception occurred. Network may be down.";
            if (ex.getLocalizedMessage() != null && !ex.getLocalizedMessage().equals("")) str +=
                  " => " + ex.getLocalizedMessage();
            worldEditor.getWorldComposite().setTableTitle("Searching Error - " + selectedName, false);
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            monitor.done();
            return new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, str, null);
         }
         monitor.done();
         return Status.OK_STATUS;
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldEditorProvider#getSelectedName(org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType)
    */
   @Override
   public String getSelectedName(SearchType searchType) throws OseeCoreException {
      return worldParameterSearchItem.getSelectedName(searchType);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldEditorParameterProvider#getParameterXWidgetXml()
    */
   @Override
   public String getParameterXWidgetXml() throws OseeCoreException {
      return worldParameterSearchItem.getParameterXWidgetXml();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldEditorParameterProvider#performSearchGetResults(org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType)
    */
   @Override
   public Collection<? extends Artifact> performSearchGetResults(SearchType searchType) throws OseeCoreException {
      return worldParameterSearchItem.performSearchGetResults(searchType);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldEditorParameterProvider#getDynamicWidgetLayoutListener()
    */
   @Override
   public IDynamicWidgetLayoutListener getDynamicWidgetLayoutListener() {
      return worldParameterSearchItem;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IWorldEditorParameterProvider#getWidgetOptions(org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData)
    */
   @Override
   public String[] getWidgetOptions(DynamicXWidgetLayoutData widgetData) {
      return null;
   }

}
