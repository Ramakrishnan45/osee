/*
 * Created on Sep 14, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.eventx;

import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationModType;
import org.eclipse.osee.framework.ui.plugin.event.Sender;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedRelation;

/**
 * @author Donald G. Dunne
 */
public class XRelationModifiedEvent extends XModifiedEvent {

   protected final Sender sender;
   protected final RelationModType relationModType;
   protected final RelationLink link;
   protected final Branch branch;
   protected final String relationType;
   protected final String relationSide;
   protected final UnloadedRelation unloadedRelation;

   public XRelationModifiedEvent(Sender sender, RelationModType relationModType, RelationLink link, Branch branch, String relationType, String relationSide) {
      this.sender = sender;
      this.relationModType = relationModType;
      this.link = link;
      this.branch = branch;
      this.relationType = relationType;
      this.relationSide = relationSide;
      this.unloadedRelation = null;
   }

   public XRelationModifiedEvent(Sender sender, RelationModType relationModType, UnloadedRelation unloadedRelation) {
      this.sender = sender;
      this.relationModType = relationModType;
      this.unloadedRelation = unloadedRelation;
      this.link = null;
      this.branch = null;
      this.relationType = null;
      this.relationSide = null;
   }

   @Override
   public String toString() {
      return sender.getSource() + " - " + relationModType + " - " + (link != null ? "Loaded - " + link : "Unloaded - " + unloadedRelation);
   }
}
