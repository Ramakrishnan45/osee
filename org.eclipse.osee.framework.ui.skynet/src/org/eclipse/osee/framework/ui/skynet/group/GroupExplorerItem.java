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
package org.eclipse.osee.framework.ui.skynet.group;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.eventx.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.eventx.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.eventx.XEventManager;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.ui.plugin.event.Sender.Source;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Donald G. Dunne
 */
public class GroupExplorerItem implements IFrameworkTransactionEventListener {

   private final Artifact artifact;
   private final TreeViewer treeViewer;
   private final GroupExplorerItem parentItem;
   private List<GroupExplorerItem> groupItems;
   private final GroupExplorer groupExplorer;

   public GroupExplorerItem(TreeViewer treeViewer, Artifact artifact, GroupExplorerItem parentItem, GroupExplorer groupExplorer) {
      this.treeViewer = treeViewer;
      this.artifact = artifact;
      this.parentItem = parentItem;
      this.groupExplorer = groupExplorer;
      XEventManager.addListener(this, this);
   }

   public boolean contains(Artifact artifact) {
      for (GroupExplorerItem item : getGroupItems()) {
         if (item.getArtifact() != null && item.getArtifact().equals(artifact)) return true;
      }
      return false;
   }

   /**
    * @param artifact to match with
    * @return UGI that contains artifact
    */
   public GroupExplorerItem getItem(Artifact artifact) {
      if (this.artifact != null && this.artifact.equals(artifact)) return this;
      for (GroupExplorerItem item : getGroupItems()) {
         GroupExplorerItem ugi = item.getItem(artifact);
         if (ugi != null) return ugi;
      }
      return null;
   }

   public void dispose() {
      XEventManager.removeListeners(this);
      if (groupItems != null) for (GroupExplorerItem item : groupItems)
         item.dispose();
   }

   public boolean isUniversalGroup() {
      if (artifact == null || artifact.isDeleted()) return false;
      return artifact.getArtifactTypeName().equals("Universal Group");
   }

   public String getTableArtifactType() {
      return artifact.getArtifactTypeName();
   }

   public String getTableArtifactName() {
      return artifact.getDescriptiveName();
   }

   public String getTableArtifactDescription() {
      return null;
   }

   public Artifact getArtifact() {
      return artifact;
   }

   public List<GroupExplorerItem> getGroupItems() {
      // Light loading; load the first time getChildren is called
      if (groupItems == null) {
         groupItems = new ArrayList<GroupExplorerItem>();
         populateUpdateCategory();
      }
      List<GroupExplorerItem> items = new ArrayList<GroupExplorerItem>();
      if (groupItems != null) items.addAll(groupItems);
      return items;
   }

   /**
    * Populate/Update this category with it's necessary children items
    */
   public void populateUpdateCategory() {
      try {
         for (GroupExplorerItem item : getGroupItems()) {
            removeGroupItem(item);
         }
         for (Artifact art : artifact.getRelatedArtifacts(CoreRelationEnumeration.UNIVERSAL_GROUPING__MEMBERS)) {
            addGroupItem(new GroupExplorerItem(treeViewer, art, this, groupExplorer));
         }
      } catch (SQLException ex) {
         SkynetGuiPlugin.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
   }

   public void addGroupItem(GroupExplorerItem item) {
      groupItems.add(item);
   }

   public void removeGroupItem(GroupExplorerItem item) {
      item.dispose();
      groupItems.remove(item);
   }

   public GroupExplorerItem getParentItem() {
      return parentItem;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IFrameworkTransactionEventListener#handleFrameworkTransactionEvent(org.eclipse.osee.framework.ui.plugin.event.Sender.Source, org.eclipse.osee.framework.skynet.core.eventx.FrameworkTransactionData)
    */
   @Override
   public void handleFrameworkTransactionEvent(Source source, final FrameworkTransactionData transData) {
      if (transData.isHasEvent(artifact)) {
         final GroupExplorerItem tai = this;
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               if (treeViewer == null || treeViewer.getTree().isDisposed() || (artifact != null && artifact.isDeleted())) {
                  dispose();
                  return;
               }

               if (transData.isDeleted(artifact)) {
                  treeViewer.refresh();
                  groupExplorer.restoreSelection();
               } else if (transData.isChanged(artifact)) {
                  treeViewer.update(tai, null);
               } else if (transData.isRelChange(artifact)) {
                  populateUpdateCategory();
                  treeViewer.refresh(tai);
                  groupExplorer.restoreSelection();
               }
            }
         });
      }
   }
}
