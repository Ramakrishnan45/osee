/*
 * Created on Apr 10, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.define.traceability.jobs;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.osee.define.DefinePlugin;
import org.eclipse.osee.define.traceability.operations.FindTraceUnitFromResource;
import org.eclipse.osee.framework.plugin.core.util.IExceptionableRunnable;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.skynet.branch.BranchSelectionDialog;
import org.eclipse.ui.progress.UIJob;

/**
 * @author Roberto E. Escobar
 */
public class FindTraceUnitJob extends Job {
   private final IResource[] resources;

   public FindTraceUnitJob(String name, IResource... resources) {
      super(name);
      if (resources != null) {
         this.resources = resources;
      } else {
         this.resources = new IResource[0];
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected IStatus run(IProgressMonitor monitor) {
      if (resources != null && resources.length > 0) {
         FetchBranchJob job = new FetchBranchJob(getName());
         Jobs.startJob(job, true, new JobChangeAdapter() {

            @Override
            public void done(IJobChangeEvent event) {
               FetchBranchJob fetcherJob = (FetchBranchJob) event.getJob();
               final Branch branch = fetcherJob.getSelectedBranch();
               if (branch != null) {
                  IExceptionableRunnable runnable = new IExceptionableRunnable() {

                     @Override
                     public IStatus run(IProgressMonitor monitor) throws Exception {
                        if (branch != null) {
                           FindTraceUnitFromResource.search(branch, resources);
                        }
                        return Status.OK_STATUS;
                     }
                  };
                  Jobs.run(getName(), runnable, DefinePlugin.class, DefinePlugin.PLUGIN_ID);
               }
            }
         });
      }
      return Status.OK_STATUS;
   }
   private final class FetchBranchJob extends UIJob {
      private Branch branch;

      public FetchBranchJob(String name) {
         super(name);
         branch = null;
      }

      public IStatus runInUIThread(IProgressMonitor monitor) {
         branch = BranchSelectionDialog.getBranchFromUser();
         return Status.OK_STATUS;
      }

      public Branch getSelectedBranch() {
         return branch;
      }
   }
}
