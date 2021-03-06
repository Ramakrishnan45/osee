package org.eclipse.osee.ats.core.workflow.util;

import java.util.Collection;
import java.util.Map;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IValidatingOperation;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * Duplicate Workflow including all fields and, states.
 *
 * @author Donald G. Dunne
 */
public abstract class AbstractDuplicateWorkflowOperation implements IValidatingOperation {

   protected final Collection<IAtsTeamWorkflow> teamWfs;
   protected final String title;
   protected final IAtsServices services;
   protected Map<IAtsTeamWorkflow, IAtsTeamWorkflow> oldToNewMap;
   protected final IAtsUser asUser;

   public AbstractDuplicateWorkflowOperation(Collection<IAtsTeamWorkflow> teamWfs, String title, IAtsUser asUser, IAtsServices services) {
      this.teamWfs = teamWfs;
      this.title = title;
      this.asUser = asUser;
      this.services = services;
   }

   @Override
   public XResultData validate() {
      XResultData results = new XResultData();
      if (teamWfs.isEmpty()) {
         results.error("Team Workflows can not be empty.");
      }
      if (asUser == null) {
         results.error("AsUser can not be empty.");
      }
      return results;
   }

   /**
    * Return "Copy of"-title if title isn't specified
    */
   protected String getTitle(IAtsWorkItem workItem) {
      if (teamWfs.size() == 1 && Strings.isValid(title)) {
         return AXml.textToXml(title);
      } else {
         if (workItem.isTeamWorkflow()) {
            return AXml.textToXml("Copy of " + workItem.getName());
         } else {
            return workItem.getName();
         }
      }
   }

   public Map<IAtsTeamWorkflow, IAtsTeamWorkflow> getResults() {
      return oldToNewMap;
   }

}