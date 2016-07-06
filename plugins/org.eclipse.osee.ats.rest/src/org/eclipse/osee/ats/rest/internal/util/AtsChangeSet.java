/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.util;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.notify.IAtsNotifier;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IExecuteListener;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.RuleEventType;
import org.eclipse.osee.ats.api.workflow.IAttribute;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.core.util.AbstractAtsChangeSet;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.data.ITransaction;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Donald G. Dunne
 */
public class AtsChangeSet extends AbstractAtsChangeSet {

   private TransactionBuilder transaction;

   private final IAttributeResolver attributeResolver;
   private final OrcsApi orcsApi;
   private final IAtsStateFactory stateFactory;
   private final IAtsLogFactory logFactory;
   private final IAtsNotifier notifier;

   private final IAtsServer atsServer;

   public AtsChangeSet(IAtsServer atsServer, IAttributeResolver attributeResolver, OrcsApi orcsApi, IAtsStateFactory stateFactory, IAtsLogFactory logFactory, String comment, IAtsUser user, IAtsNotifier notifier) {
      super(comment, user);
      this.atsServer = atsServer;
      this.attributeResolver = attributeResolver;
      this.orcsApi = orcsApi;
      this.stateFactory = stateFactory;
      this.logFactory = logFactory;
      this.notifier = notifier;
   }

   public TransactionBuilder getTransaction() throws OseeCoreException {
      if (transaction == null) {
         transaction =
            orcsApi.getTransactionFactory().createTransaction(AtsUtilCore.getAtsBranch(), getUser(asUser), comment);
      }
      return transaction;
   }

   private ArtifactReadable getUser(IAtsUser user) {
      if (user.getStoreObject() instanceof ArtifactReadable) {
         return (ArtifactReadable) user.getStoreObject();
      }
      return orcsApi.getQueryFactory().fromBranch(AtsUtilCore.getAtsBranch()).andUuid(
         user.getUuid()).getResults().getExactlyOne();
   }

   @Override
   public ITransaction execute() throws OseeCoreException {
      Conditions.checkNotNull(comment, "comment");
      if (objects.isEmpty() && deleteObjects.isEmpty() && execptionIfEmpty) {
         throw new OseeArgumentException("objects/deleteObjects cannot be empty");
      }
      for (Object obj : objects) {
         if (obj instanceof IAtsWorkItem) {
            IAtsWorkItem workItem = (IAtsWorkItem) obj;
            IAtsStateManager stateMgr = workItem.getStateMgr();
            if (stateMgr.isDirty()) {
               stateFactory.writeToStore(asUser, workItem, this);
            }
            if (workItem.getLog().isDirty()) {
               logFactory.writeToStore(workItem, attributeResolver, this);
            }
         }
      }
      for (Object obj : deleteObjects) {
         if (obj instanceof IAtsWorkItem) {
            ArtifactReadable artifact = getArtifact(obj);
            getTransaction().deleteArtifact(artifact);
         } else {
            throw new OseeArgumentException("AtsChangeSet: Unhandled deleteObject type: " + obj);
         }
      }
      TransactionReadable transactionReadable = getTransaction().commit();
      for (IExecuteListener listener : listeners) {
         listener.changesStored(this);
      }
      notifier.sendNotifications(getNotifications());

      if (!workItemsCreated.isEmpty()) {
         WorkflowRuleRunner runner = new WorkflowRuleRunner(RuleEventType.CreateWorkflow, workItemsCreated, atsServer);
         runner.run();
      }
      return transactionReadable;
   }

   @Override
   public void deleteSoleAttribute(IAtsWorkItem workItem, IAttributeType attributeType) throws OseeCoreException {
      getTransaction().deleteSoleAttribute(getArtifact(workItem), attributeType);
      add(workItem);
   }

   @Override
   public void setSoleAttributeValue(IAtsWorkItem workItem, IAttributeType attributeType, String value) throws OseeCoreException {
      ArtifactReadable artifact = getArtifact(workItem);
      if (!artifact.getSoleAttributeValue(attributeType, "").equals(value)) {
         getTransaction().setSoleAttributeValue(artifact, attributeType, value);
         add(workItem);
      }
   }

   @Override
   public void setSoleAttributeValue(IAtsObject atsObject, IAttributeType attributeType, Object value) throws OseeCoreException {
      getTransaction().setSoleAttributeValue(getArtifact(atsObject), attributeType, value);
      add(atsObject);
   }

   @Override
   public void deleteAttribute(IAtsObject atsObject, IAttributeType attributeType, Object value) throws OseeCoreException {
      getTransaction().deleteAttributesWithValue(getArtifact(atsObject), attributeType, value);
      add(atsObject);
   }

   @Override
   public <T> void setValue(IAtsWorkItem workItem, IAttribute<String> attr, IAttributeType attributeType, T value) throws OseeCoreException {
      ArtifactId artifactId = getArtifact(workItem);
      getTransaction().setAttributeById(artifactId, new AttributeIdWrapper(attr), value);
      add(workItem);
   }

   @Override
   public <T> void deleteAttribute(IAtsWorkItem workItem, IAttribute<T> attr) throws OseeCoreException {
      getTransaction().deleteByAttributeId(getArtifact(workItem), new AttributeIdWrapper(attr));
      add(workItem);
   }

   @Override
   public boolean isAttributeTypeValid(IAtsWorkItem workItem, IAttributeType attributeType) {
      ArtifactReadable artifact = getArtifact(workItem);
      return artifact.getValidAttributeTypes().contains(attributeType);
   }

   @Override
   public void addAttribute(IAtsObject atsObject, IAttributeType attributeType, Object value) throws OseeCoreException {
      ArtifactReadable artifact = getArtifact(atsObject);
      getTransaction().createAttributeFromString(artifact, attributeType, String.valueOf(value));
      add(atsObject);
   }

   @Override
   public ArtifactId createArtifact(IArtifactType artifactType, String name) {
      ArtifactId artifact = getTransaction().createArtifact(artifactType, name);
      add(artifact);
      return artifact;
   }

   @Override
   public void deleteAttributes(IAtsObject atsObject, IAttributeType attributeType) {
      ArtifactReadable artifact = getArtifact(atsObject);
      getTransaction().deleteAttributes(artifact, attributeType);
      add(atsObject);
   }

   @Override
   public ArtifactId createArtifact(IArtifactType artifactType, String name, String guid) {
      ArtifactId artifact = getTransaction().createArtifact(artifactType, name, guid);
      add(artifact);
      return artifact;
   }

   @Override
   public ArtifactId createArtifact(IArtifactType artifactType, String name, String guid, Long uuid) {
      ArtifactId artifact = getTransaction().createArtifact(artifactType, name, guid, uuid);
      add(artifact);
      return artifact;
   }

   @Override
   public void relate(Object object1, IRelationTypeSide relationSide, Object object2) {
      ArtifactId artifact = getArtifact(object1);
      ArtifactId artifact2 = getArtifact(object2);
      if (relationSide.getSide().isSideA()) {
         getTransaction().relate(artifact2, relationSide, artifact);
      } else {
         getTransaction().relate(artifact, relationSide, artifact2);
      }
      add(artifact);
      add(artifact2);
   }

   private ArtifactReadable getArtifact(Object object) {
      ArtifactReadable artifact = null;
      if (object instanceof ArtifactReadable) {
         artifact = (ArtifactReadable) object;
      } else if (object instanceof IAtsObject) {
         artifact = (ArtifactReadable) ((IAtsObject) object).getStoreObject();
      }
      return artifact;
   }

   @Override
   public void unrelateAll(Object object, IRelationTypeSide relationType) {
      ArtifactReadable artifact = getArtifact(object);
      add(artifact);
      for (ArtifactReadable otherArt : artifact.getRelated(relationType)) {
         if (relationType.getSide().isSideA()) {
            getTransaction().unrelate(otherArt, relationType, artifact);
         } else {
            getTransaction().unrelate(artifact, relationType, otherArt);
         }
         add(otherArt);
      }
   }

   @Override
   public void setRelation(Object object1, IRelationTypeSide relationSide, Object object2) {
      setRelations(object1, relationSide, Collections.singleton(object2));
   }

   @Override
   public void setRelations(Object object, IRelationTypeSide relationSide, Collection<? extends Object> objects) {
      ArtifactReadable artifact = getArtifact(object);
      List<ArtifactReadable> artifacts = new LinkedList<>();
      for (Object obj : objects) {
         ArtifactReadable art = getArtifact(obj);
         if (art != null) {
            artifacts.add(art);
         }
      }

      // add all relations that do not exist
      for (Object obj : objects) {
         ArtifactReadable art = getArtifact(obj);
         if (!art.areRelated(relationSide, art)) {
            relate(object, relationSide, obj);
         }
      }
      // unrelate all objects that are not in set
      for (ArtifactReadable art : artifact.getRelated(relationSide)) {
         if (!artifacts.contains(art)) {
            unrelate(artifact, relationSide, art);
         }
      }
   }

   public void unrelate(Object object1, IRelationTypeSide relationType, Object object2) {
      getTransaction().unrelate(getArtifact(object1), relationType, getArtifact(object2));
      add(object1);
   }

   @Override
   public <T> void setAttribute(IAtsWorkItem workItem, int attributeId, T value) {
      ArtifactReadable artifact = getArtifact(workItem);
      boolean found = false;
      for (AttributeReadable<Object> attribute : artifact.getAttributes()) {
         if (attribute.getGammaId() == attributeId) {
            getTransaction().setAttributeById(artifact, attribute, value);
            found = true;
            break;
         }
      }
      if (!found) {
         throw new OseeStateException("Attribute Id %d does not exist on Artifact %s", attributeId, workItem);
      }
      add(workItem);
   }

   @Override
   public void deleteArtifact(ArtifactId artifact) {
      getTransaction().deleteArtifact(artifact);
      add(artifact);
   }

   @Override
   public void setValues(IAtsObject atsObject, IAttributeType attrType, List<String> values) {
      ArtifactReadable artifact = getArtifact(atsObject);
      getTransaction().setAttributesFromStrings(artifact, attrType, values);
      add(artifact);
   }

   @Override
   public <T> void setAttribute(ArtifactId artifact, int attrId, T value) {
      for (AttributeReadable<?> attribute : getArtifact(artifact).getAttributes()) {
         if (attribute.getLocalId() == attrId) {
            getTransaction().setAttributeById(getArtifact(artifact), attribute, value);
         }
      }
   }

   @Override
   public void setSoleAttributeValue(ArtifactId artifact, IAttributeType attrType, String value) {
      ArtifactReadable art = getArtifact(artifact);
      getTransaction().setSoleAttributeValue(art, attrType, value);
      add(art);
   }

   @Override
   public void deleteAttribute(ArtifactId artifact, IAttribute<?> attr) {
      AttributeId attribute = ((ArtifactReadable) artifact).getAttributeById(attr.getId());
      getTransaction().deleteByAttributeId(artifact, attribute);
      add(artifact);
   }

   @Override
   public void unrelate(ArtifactId artifact, IRelationTypeSide relationSide, ArtifactId artifact2) {
      ArtifactReadable art = getArtifact(artifact);
      ArtifactReadable art2 = getArtifact(artifact2);
      if (relationSide.getSide().isSideA()) {
         getTransaction().unrelate(art2, relationSide, art);
      } else {
         getTransaction().unrelate(art, relationSide, art2);
      }
      add(art);
      add(art2);
   }

   @Override
   public void addAttribute(ArtifactId artifact, IAttributeType attrType, Object value) {
      ArtifactReadable art = getArtifact(artifact);
      getTransaction().createAttribute(artifact, attrType, value);
      add(art);
   }

   @Override
   public void setSoleAttributeFromString(ArtifactId artifact, IAttributeType attrType, String value) {
      ArtifactReadable art = getArtifact(artifact);
      getTransaction().setSoleAttributeFromString(artifact, attrType, value);
      add(art);
   }

}