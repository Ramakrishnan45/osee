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
package org.eclipse.osee.ats.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage.Manipulations;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;

/**
 * @author Donald G. Dunne
 */
public class VersionReportJob extends Job {

   protected final String title;
   protected final VersionArtifact verArt;

   public VersionReportJob(String title, VersionArtifact verArt) {
      super("Creating Release Report");
      this.title = title;
      this.verArt = verArt;
   }

   @Override
   public IStatus run(IProgressMonitor monitor) {
      try {
         final String html = getReleaseReportHtml(title + " - " + XDate.getDateNow(XDate.MMDDYYHHMM), verArt, monitor);
         ResultsEditor.open(new XResultPage(title, html, Manipulations.HTML_MANIPULATIONS));
      } catch (Exception ex) {
         return new Status(IStatus.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.toString(), ex);
      }
      monitor.done();
      return Status.OK_STATUS;
   }

   public static String getReleaseReportHtml(String title, VersionArtifact verArt, IProgressMonitor monitor) throws OseeCoreException {
      if (verArt == null) {
         AWorkbench.popup("ERROR", "Must select product, config and version.");
         return null;
      }
      StringBuilder sb = new StringBuilder();
      sb.append(AHTML.heading(3, title + getReleasedString(verArt), verArt.getName()));
      sb.append(getTeamWorkflowReport(verArt.getTargetedForTeamArtifacts(), null, monitor));
      return sb.toString();
   }

   public static String getFullReleaseReport(TeamDefinitionArtifact teamDef, IProgressMonitor monitor) throws OseeCoreException {
      // Sort by release date and estimated release date
      Map<String, VersionArtifact> dateToVerArt = new HashMap<String, VersionArtifact>();
      for (VersionArtifact verArt : teamDef.getVersionsArtifacts()) {
         if (verArt.getReleaseDate() != null) {
            dateToVerArt.put(verArt.getReleaseDate().getTime() + verArt.getName(), verArt);
         } else if (verArt.getEstimatedReleaseDate() != null) {
            dateToVerArt.put(verArt.getEstimatedReleaseDate().getTime() + verArt.getName(), verArt);
         } else {
            dateToVerArt.put("Un-Released - No Estimated Release " + verArt.getName(), verArt);
         }
      }
      String[] dateSort = dateToVerArt.keySet().toArray(new String[dateToVerArt.size()]);
      Arrays.sort(dateSort);
      // Create hyperlinks reverse sorted
      StringBuffer sb = new StringBuffer();
      sb.append(AHTML.heading(2, teamDef + " Releases"));
      sb.append(AHTML.bold("Report generated by OSEE ATS on " + XDate.getDateNow()) + AHTML.newline(2));
      for (int x = dateSort.length - 1; x >= 0; x--) {
         VersionArtifact verArt = dateToVerArt.get(dateSort[x]);
         if (verArt.isNextVersion() || verArt.isReleased()) {
            sb.append(AHTML.getHyperlink("#" + verArt.getName(),
               verArt.getName() + VersionReportJob.getReleasedString(verArt)) + AHTML.newline());
         } else if (verArt.getEstimatedReleaseDate() != null) {
            sb.append(verArt.getName() + " - Un-Released - Estimated Release Date: " + getDateString(verArt.getEstimatedReleaseDate()) + AHTML.newline());
         } else {
            sb.append(verArt.getName() + " - Un-Released - No Estimated Release Date" + AHTML.newline());
         }
      }
      sb.append(AHTML.addSpace(5));
      int x = 1;
      for (VersionArtifact verArt : teamDef.getVersionsArtifacts()) {
         if (monitor != null) {
            String str = "Processing version " + x++ + "/" + teamDef.getVersionsArtifacts().size();
            monitor.subTask(str);
         }
         if (verArt.isReleased() || verArt.isNextVersion()) {
            String html = VersionReportJob.getReleaseReportHtml(verArt.getName(), verArt, null);
            sb.append(html);
         }
      }
      return sb.toString();
   }

   private static String getDateString(Date date) {
      if (date != null) {
         return XDate.getDateStr(date, XDate.MMDDYY);
      }
      return null;
   }

   public static String getReleasedString(VersionArtifact verArt) throws OseeCoreException {
      String released = "";
      if (verArt.isReleased() && verArt.getSoleAttributeValue(AtsAttributeTypes.ReleaseDate, null) != null) {
         released =
            " - " + "Released: " + getDateString(verArt.getSoleAttributeValue(AtsAttributeTypes.ReleaseDate,
               (Date) null));
      }
      if (verArt.isNextVersion() && verArt.getSoleAttributeValue(AtsAttributeTypes.EstimatedReleaseDate, null) != null) {
         released =
            " - " + "Next Release - Estimated Release Date: " + getDateString(verArt.getSoleAttributeValue(
               AtsAttributeTypes.EstimatedReleaseDate, (Date) null));
      }
      return released;
   }

   public static String getTeamWorkflowReport(Collection<TeamWorkFlowArtifact> teamArts, Integer backgroundColor, IProgressMonitor monitor) throws OseeCoreException {
      StringBuilder sb = new StringBuilder();
      sb.append(AHTML.beginMultiColumnTable(100, 1, backgroundColor));
      sb.append(AHTML.addHeaderRowMultiColumnTable(new String[] {"Type", "Team", "Priority", "Change", "Title", "HRID"}));
      int x = 1;
      Set<TeamDefinitionArtifact> teamDefs = new HashSet<TeamDefinitionArtifact>();
      for (TeamWorkFlowArtifact team : teamArts) {
         teamDefs.add(team.getTeamDefinition());
      }
      for (TeamDefinitionArtifact teamDef : teamDefs) {
         for (TeamWorkFlowArtifact team : teamArts) {
            if (team.getTeamDefinition().equals(teamDef)) {
               String str = "Processing team " + x++ + "/" + teamArts.size();
               if (monitor != null) {
                  monitor.subTask(str);
               }
               System.out.println(str);
               sb.append(AHTML.addRowMultiColumnTable(
                  new String[] {
                     "Action",
                     team.getTeamName(),
                     team.getWorldViewPriority(),
                     team.getWorldViewChangeTypeStr(),
                     team.getName(),
                     team.getHumanReadableId()}, null, (x % 2 == 0 ? null : "#cccccc")));

               for (TaskArtifact taskArt : team.getTaskArtifacts()) {
                  sb.append(AHTML.addRowMultiColumnTable(
                     new String[] {"Task", "", "", "", taskArt.getName(), taskArt.getHumanReadableId()}, null,
                     (x % 2 == 0 ? null : "#cccccc")));
               }
            }
         }
      }
      sb.append(AHTML.endMultiColumnTable());
      return sb.toString();
   }

}
