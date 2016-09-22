/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.client.internal.search;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.Identity;
import org.eclipse.osee.orcs.rest.model.search.artifact.Predicate;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchMethod;

/**
 * @author John Misinco
 */
public class PredicateFactoryImpl implements PredicateFactory {

   private final List<String> emptyStringList = Collections.emptyList();

   public static interface RestSearchOptions {

      List<QueryOption> getOptions();

   }

   @Override
   public Predicate createUuidSearch(Collection<String> ids) {
      List<String> strIds = new LinkedList<>(ids);
      return createIdsSearch(SearchMethod.GUIDS, strIds);
   }

   @Override
   public Predicate createLocalIdsSearch(Collection<Integer> ids) {
      List<String> strIds = new LinkedList<>();
      for (Integer value : ids) {
         strIds.add(String.valueOf(value));
      }
      return createIdsSearch(SearchMethod.IDS, strIds);
   }

   @Override
   public Predicate createIdSearch(Collection<? extends Identity<String>> ids) {
      List<String> values = new LinkedList<>();
      for (Identity<String> token : ids) {
         values.add(token.getGuid());
      }
      return createIdsSearch(SearchMethod.GUIDS, values);
   }

   private Predicate createIdsSearch(SearchMethod method, List<String> ids) {
      return new Predicate(method, emptyStringList, ids);
   }

   @Override
   public Predicate createIsOfTypeSearch(Collection<? extends IArtifactType> artifactType) {
      List<String> typeIds = getLongIds(artifactType);
      return new Predicate(SearchMethod.IS_OF_TYPE, emptyStringList, typeIds);
   }

   @Override
   public Predicate createTypeEqualsSearch(Collection<? extends IArtifactType> artifactType) {
      List<String> typeIds = getLongIds(artifactType);
      return new Predicate(SearchMethod.TYPE_EQUALS, emptyStringList, typeIds);
   }

   @Override
   public Predicate createAttributeTypeSearch(Collection<? extends IAttributeType> attributeTypes, String value, QueryOption... options) {
      return createAttributeTypeSearch(attributeTypes, Collections.singleton(value), options);
   }

   @Override
   public Predicate createAttributeTypeSearch(Collection<? extends IAttributeType> attributeTypes, Collection<String> values, QueryOption... options) {
      List<String> typeIds = getLongIds(attributeTypes);
      return new Predicate(SearchMethod.ATTRIBUTE_TYPE, typeIds, new LinkedList<String>(values), options);
   }

   @Override
   public Predicate createAttributeExistsSearch(Collection<? extends IAttributeType> attributeTypes) {
      List<String> typeIds = getLongIds(attributeTypes);
      return new Predicate(SearchMethod.EXISTS_TYPE, Arrays.asList("attrType"), typeIds);
   }

   @Override
   public Predicate createAttributeNotExistsSearch(Collection<? extends IAttributeType> attributeTypes) {
      List<String> typeIds = getLongIds(attributeTypes);
      return new Predicate(SearchMethod.NOT_EXISTS_TYPE, Arrays.asList("attrType"), typeIds);
   }

   @Override
   public Predicate createRelationExistsSearch(Collection<? extends IRelationType> relationTypes) {
      List<String> typeIds = getLongIds(relationTypes);
      return new Predicate(SearchMethod.EXISTS_TYPE, Arrays.asList("relType"), typeIds);
   }

   @Override
   public Predicate createRelationTypeSideExistsSearch(RelationTypeSide relationTypeSide) {
      String side = relationTypeSide.getSide().isSideA() ? "A" : "B";
      return new Predicate(SearchMethod.EXISTS_TYPE, Arrays.asList("relTypeSide", side), getLongIds(relationTypeSide));
   }

   @Override
   public Predicate createRelationTypeSideNotExistsSearch(RelationTypeSide relationTypeSide) {
      String side = relationTypeSide.getSide().isSideA() ? "A" : "B";
      return new Predicate(SearchMethod.NOT_EXISTS_TYPE, Arrays.asList("relTypeSide", side),
         getLongIds(relationTypeSide));
   }

   @Override
   public Predicate createRelationNotExistsSearch(Collection<? extends IRelationType> relationTypes) {
      List<String> typeIds = getLongIds(relationTypes);
      return new Predicate(SearchMethod.NOT_EXISTS_TYPE, Arrays.asList("relType"), typeIds);
   }

   @Override
   public Predicate createRelatedToSearch(RelationTypeSide relationTypeSide, Collection<ArtifactId> ids) {
      List<String> values = new LinkedList<>();
      String side = relationTypeSide.getSide().isSideA() ? "A" : "B";
      for (ArtifactId id : ids) {
         values.add(id.getId().toString());
      }
      return new Predicate(SearchMethod.RELATED_TO, Arrays.asList(side + relationTypeSide.getId().toString()), values);
   }

   private List<String> getLongIds(Collection<? extends Id> types) {
      List<String> toReturn = new LinkedList<>();
      for (Id type : types) {
         Long value = type.getId();
         toReturn.add(String.valueOf(value));
      }
      return toReturn;
   }

   private List<String> getLongIds(Id type) {
      return getLongIds(Collections.singletonList(type));
   }

}
