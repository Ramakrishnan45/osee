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
package org.eclipse.osee.orcs.core.internal.artifact;

import static org.eclipse.osee.framework.core.enums.DirtyState.APPLICABILITY_ONLY;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.EditState;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.internal.attribute.Attribute;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeFactory;
import org.eclipse.osee.orcs.core.internal.attribute.AttributeManagerImpl;
import org.eclipse.osee.orcs.core.internal.graph.GraphData;
import org.eclipse.osee.orcs.core.internal.relation.order.OrderChange;
import org.eclipse.osee.orcs.data.ArtifactTypes;

public class ArtifactImpl extends AttributeManagerImpl implements Artifact {

   private final ArtifactTypes artifactTypeCache;

   private EditState objectEditState;
   private ArtifactData artifactData;
   private GraphData graph;

   public ArtifactImpl(ArtifactTypes artifactTypeCache, ArtifactData artifactData, AttributeFactory attributeFactory) {
      super(artifactData, attributeFactory);
      this.artifactTypeCache = artifactTypeCache;
      this.artifactData = artifactData;
      this.objectEditState = EditState.NO_CHANGE;
   }

   @Override
   public void setGraph(GraphData graph) {
      this.graph = graph;
   }

   @Override
   public GraphData getGraph() {
      return graph;
   }

   @Override
   public ArtifactData getOrcsData() {
      return artifactData;
   }

   @Override
   public void setOrcsData(ArtifactData data) {
      this.artifactData = data;
      objectEditState = EditState.NO_CHANGE;
   }

   @Override
   public ModificationType getModificationType() {
      return getOrcsData().getModType();
   }

   @Override
   public Integer getLocalId() {
      return getOrcsData().getLocalId();
   }

   @Override
   public TransactionId getLastModifiedTransaction() {
      TransactionId maxTransactionId = getOrcsData().getVersion().getTransactionId();
      for (Attribute<?> attribute : getAllAttributes()) {
         TransactionId tx = attribute.getOrcsData().getVersion().getTransactionId();
         if (maxTransactionId.isOlderThan(tx)) {
            maxTransactionId = tx;
         }
      }
      return maxTransactionId;
   }

   @Override
   public TransactionId getTransaction() {
      return graph.getTransaction();
   }

   @Override
   public BranchId getBranch() {
      return artifactData.getVersion().getBranch();
   }

   @Override
   public IArtifactType getArtifactType() throws OseeCoreException {
      return artifactTypeCache.get(getOrcsData().getTypeUuid());
   }

   @Override
   public void setName(String name) throws OseeCoreException {
      setSoleAttributeFromString(CoreAttributeTypes.Name, name);
   }

   @Override
   public void setArtifactType(IArtifactType artifactType) throws OseeCoreException {
      if (!getArtifactType().equals(artifactType)) {
         getOrcsData().setTypeUuid(artifactType.getGuid());
         objectEditState = EditState.ARTIFACT_TYPE_MODIFIED;
         if (getOrcsData().getVersion().isInStorage()) {
            getOrcsData().setModType(ModificationType.MODIFIED);
         }
      }
   }

   @Override
   public boolean isOfType(ArtifactTypeId... otherTypes) throws OseeCoreException {
      return artifactTypeCache.inheritsFrom(getArtifactType(), otherTypes);
   }

   @Override
   public void setNotDirty() {
      setAttributesNotDirty();
      objectEditState = EditState.NO_CHANGE;
      getOrcsData().setModType(ModificationType.MODIFIED);
   }

   @Override
   public boolean isDirty() {
      return areAttributesDirty() || hasDirtyArtifactType() || isReplaceWithVersion() || APPLICABILITY_ONLY == getOrcsData().getDirtyState();
   }

   private boolean isReplaceWithVersion() {
      return getModificationType() == ModificationType.REPLACED_WITH_VERSION || artifactData.isExistingVersionUsed();
   }

   private boolean hasDirtyArtifactType() {
      return objectEditState.isArtifactTypeChange();
   }

   @Override
   public boolean isAttributeTypeValid(AttributeTypeId attributeType) throws OseeCoreException {
      return artifactTypeCache.isValidAttributeType(getArtifactType(), getBranch(), attributeType);
   }

   @Override
   public Collection<AttributeTypeToken> getValidAttributeTypes() throws OseeCoreException {
      return artifactTypeCache.getAttributeTypes(getArtifactType(), getBranch());
   }

   @Override
   public String getExceptionString() {
      try {
         return String.format("artifact type[%s] guid[%s] on branch[%s]", getArtifactType(), getGuid(), getBranch());
      } catch (OseeCoreException ex) {
         return Lib.exceptionToString(ex);
      }
   }

   @Override
   public void accept(ArtifactVisitor visitor) throws OseeCoreException {
      visitor.visit(this);
      for (Attribute<?> attribute : getAllAttributes()) {
         visitor.visit(attribute);
      }
   }

   @Override
   public void delete() throws OseeCoreException {
      getOrcsData().setModType(ModificationType.DELETED);
      deleteAttributesByArtifact();
   }

   @Override
   public boolean isDeleteAllowed() {
      return !isDeleted();
   }

   @Override
   public void unDelete() throws OseeCoreException {
      getOrcsData().setModType(getOrcsData().getBaseModType());
      unDeleteAttributesByArtifact();
   }

   @Override
   public boolean isAccessible() {
      return !isDeleted();
   }

   @Override
   public String getOrderData() throws OseeCoreException {
      return getSoleAttributeAsString(CoreAttributeTypes.RelationOrder, Strings.emptyString());
   }

   @Override
   public void storeOrderData(OrderChange changeType, String data) throws OseeCoreException {
      if (Strings.isValid(data)) {
         setSoleAttributeFromString(CoreAttributeTypes.RelationOrder, data);
      } else {
         deleteSoleAttribute(CoreAttributeTypes.RelationOrder);
      }
   }

   @Override
   public String toString() {
      try {
         return String.format("artifact [type=[%s] guid=[%s] branch=[%s]]", getArtifactType(), getGuid(), getBranch());
      } catch (OseeCoreException ex) {
         return Lib.exceptionToString(ex);
      }
   }

   @Override
   public Long getId() {
      return Long.valueOf(getLocalId());
   }
}