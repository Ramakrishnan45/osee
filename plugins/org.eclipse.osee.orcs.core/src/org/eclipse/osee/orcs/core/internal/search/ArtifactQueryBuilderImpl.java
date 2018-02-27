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
package org.eclipse.osee.orcs.core.internal.search;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactIds;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeHash;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeTypeNotExists;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelatedTo;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.ArtifactQueryBuilder;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactQueryBuilderImpl<T> implements ArtifactQueryBuilder<T> {

   private final CriteriaFactory criteriaFactory;
   private final QueryData queryData;

   public ArtifactQueryBuilderImpl(CriteriaFactory criteriaFactory, QueryData queryData) {
      this.criteriaFactory = criteriaFactory;
      this.queryData = queryData;
   }

   protected QueryData getQueryData() {
      return queryData;
   }

   private Options getOptions() {
      return queryData.getOptions();
   }

   @Override
   public T includeDeletedAttributes() {
      return includeDeletedAttributes(true);
   }

   @SuppressWarnings("unchecked")
   @Override
   public T includeDeletedAttributes(boolean enabled) {
      OptionsUtil.setIncludeDeletedAttributes(getOptions(), enabled);
      return (T) this;
   }

   @Override
   public boolean areDeletedAttributesIncluded() {
      return OptionsUtil.areDeletedAttributesIncluded(getOptions());
   }

   @Override
   public T includeDeletedRelations() {
      return includeDeletedRelations(true);
   }

   @SuppressWarnings("unchecked")
   @Override
   public T includeDeletedRelations(boolean enabled) {
      OptionsUtil.setIncludeDeletedRelations(getOptions(), enabled);
      return (T) this;
   }

   @Override
   public boolean areDeletedRelationsIncluded() {
      return OptionsUtil.areDeletedRelationsIncluded(getOptions());
   }

   @Override
   public T includeDeletedArtifacts() {
      return includeDeletedArtifacts(true);
   }

   @SuppressWarnings("unchecked")
   @Override
   public T includeDeletedArtifacts(boolean enabled) {
      OptionsUtil.setIncludeDeletedArtifacts(getOptions(), enabled);
      return (T) this;
   }

   @Override
   public boolean areDeletedArtifactsIncluded() {
      return OptionsUtil.areDeletedArtifactsIncluded(getOptions());
   }

   @SuppressWarnings("unchecked")
   @Override
   public T fromTransaction(TransactionId transaction) {
      OptionsUtil.setFromTransaction(getOptions(), transaction);
      return (T) this;
   }

   @Override
   public TransactionId getFromTransaction() {
      return OptionsUtil.getFromTransaction(getOptions());
   }

   @SuppressWarnings("unchecked")
   @Override
   public T headTransaction() {
      OptionsUtil.setHeadTransaction(getOptions());
      return (T) this;
   }

   @Override
   public boolean isHeadTransaction() {
      return OptionsUtil.isHeadTransaction(getOptions());
   }

   @SuppressWarnings("unchecked")
   @Override
   public T excludeDeleted() {
      includeDeletedArtifacts(false);
      return (T) this;
   }

   @Override
   public T andUuid(long id) {
      return andId(ArtifactId.valueOf(id));
   }

   @Override
   public T andId(long id) {
      return andId(ArtifactId.valueOf(id));
   }

   @Override
   public T andId(ArtifactId id) {
      return addAndCheck(queryData, new CriteriaArtifactIds(id));
   }

   @Override
   public T andIds(Collection<? extends ArtifactId> ids) {
      return addAndCheck(queryData, new CriteriaArtifactIds(ids));
   }

   @Override
   public T andUuids(Collection<Long> artifactIds) {
      return andIds(artifactIds.stream().map(id -> ArtifactId.valueOf(id)).collect(Collectors.toList()));
   }

   @Override
   public T andIdsL(Collection<Long> artifactIds) {
      return andIds(artifactIds.stream().map(id -> ArtifactId.valueOf(id)).collect(Collectors.toList()));
   }

   @Override
   public T andGuid(String id) {
      return andGuids(Collections.singleton(id));
   }

   @SuppressWarnings("unchecked")
   @Override
   public T andGuids(Collection<String> ids) {
      Set<String> guids = new HashSet<>();
      Set<String> invalids = new HashSet<>();
      for (String id : ids) {
         if (GUID.isValid(id)) {
            guids.add(id);
         } else {
            invalids.add(id);
         }
      }

      Conditions.checkExpressionFailOnTrue(!invalids.isEmpty(), "Invalid guids detected - %s", invalids);
      if (!guids.isEmpty()) {
         Criteria guidCriteria = criteriaFactory.createArtifactGuidCriteria(guids);
         addAndCheck(getQueryData(), guidCriteria);
      }
      return (T) this;
   }

   @Override
   public T andIsOfType(ArtifactTypeId... artifactType) {
      return andIsOfType(Arrays.asList(artifactType));
   }

   @Override
   public T andIsOfType(Collection<? extends ArtifactTypeId> artifactType) {
      Criteria criteria = criteriaFactory.createArtifactTypeCriteriaWithInheritance(artifactType);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public T andTypeEquals(ArtifactTypeId... artifactType) {
      return andTypeEquals(Arrays.asList(artifactType));
   }

   @Override
   public T andTypeEquals(Collection<? extends ArtifactTypeId> artifactType) {
      Criteria criteria = criteriaFactory.createArtifactTypeCriteria(artifactType);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public T andExists(AttributeTypeId... attributeType) {
      return andExists(Arrays.asList(attributeType));
   }

   @Override
   public T andExists(Collection<AttributeTypeId> attributeTypes) {
      Criteria criteria = criteriaFactory.createExistsCriteria(attributeTypes);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public T andNotExists(AttributeTypeId attributeType) {
      Criteria criteria = criteriaFactory.createNotExistsCriteria(attributeType);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public T andNotExists(AttributeTypeId attributeType, String value) {
      Criteria criteria = new CriteriaAttributeTypeNotExists(attributeType, value);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public T andNotExists(Collection<AttributeTypeId> attributeTypes) {
      Criteria criteria = criteriaFactory.createNotExistsCriteria(attributeTypes);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public T andExists(IRelationType relationType) {
      Criteria criteria = criteriaFactory.createExistsCriteria(relationType);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public T andNotExists(IRelationType relationType) {
      Criteria criteria = criteriaFactory.createNotExistsCriteria(relationType);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public T andNotExists(RelationTypeSide relationTypeSide) {
      Criteria criteria = criteriaFactory.createNotExistsCriteria(relationTypeSide);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public T andExists(RelationTypeSide relationTypeSide) {
      Criteria criteria = criteriaFactory.createExistsCriteria(relationTypeSide);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public T and(AttributeTypeId attributeType, Collection<String> values, QueryOption... options) {
      return and(Collections.singleton(attributeType), values, options);
   }

   @Override
   public T and(AttributeTypeId attributeType, String value, QueryOption... options) {
      return and(Collections.singleton(attributeType), Collections.singleton(value), options);
   }

   @Override
   public T and(Collection<AttributeTypeId> attributeTypes, String value, QueryOption... options) {
      return and(attributeTypes, Collections.singleton(value), options);
   }

   @Override
   public T and(Collection<AttributeTypeId> attributeTypes, Collection<String> value, QueryOption... options) {
      Criteria criteria = criteriaFactory.createAttributeCriteria(attributeTypes, value, options);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public T andAttribute(AttributeTypeId attributeType, String value, QueryOption option, boolean caseSensitive) {
      return addAndCheck(getQueryData(), new CriteriaAttributeHash(attributeType, value, option, caseSensitive));
   }

   @Override
   public T andAttribute(AttributeTypeId attributeType, Collection<String> values, QueryOption option, boolean caseSensitive) {
      return addAndCheck(getQueryData(), new CriteriaAttributeHash(attributeType, values, option, caseSensitive));
   }

   @Override
   public T andAttribute(String value, QueryOption option, boolean caseSensitive) {
      return addAndCheck(getQueryData(), new CriteriaAttributeHash(value, option, caseSensitive));
   }

   @Override
   public T andNameEquals(String artifactName) {
      return and(CoreAttributeTypes.Name, artifactName);
   }

   @Override
   public T andIds(ArtifactId... ids) {
      return andIds(Arrays.asList(ids));
   }

   @Override
   public T andRelatedTo(RelationTypeSide relationTypeSide, ArtifactReadable... artifacts) {
      return andRelatedTo(relationTypeSide, Arrays.asList(artifacts));
   }

   @Override
   public T andRelatedTo(RelationTypeSide relationTypeSide, Collection<? extends ArtifactId> artifacts) {
      return addAndCheck(getQueryData(), new CriteriaRelatedTo(relationTypeSide, artifacts));
   }

   @Override
   public T andRelatedTo(RelationTypeSide relationTypeSide, ArtifactId artifactId) {
      return addAndCheck(getQueryData(), new CriteriaRelatedTo(relationTypeSide, artifactId));
   }

   @SuppressWarnings("unchecked")
   @Override
   public T followRelation(RelationTypeSide relationTypeSide) {
      Criteria criteria = criteriaFactory.createFollowRelationType(relationTypeSide);
      addAndCheck(getQueryData(), criteria);
      queryData.newCriteriaSet();
      return (T) this;
   }

   @SuppressWarnings("unchecked")
   private T addAndCheck(QueryData queryData, Criteria criteria) {
      criteria.checkValid(getOptions());
      queryData.addCriteria(criteria);
      return (T) this;
   }

   @SuppressWarnings("unchecked")
   @Override
   public T andIsHeirarchicalRootArtifact() {
      andId(CoreArtifactTokens.DefaultHierarchyRoot);
      return (T) this;
   }
}