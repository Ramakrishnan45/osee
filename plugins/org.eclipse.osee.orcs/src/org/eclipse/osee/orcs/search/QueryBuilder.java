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
package org.eclipse.osee.orcs.search;

import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.framework.core.data.RelationTypeId;
import org.eclipse.osee.framework.core.data.ValueKind;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public interface QueryBuilder extends ArtifactQueryBuilder<QueryBuilder>, Query {

   ArtifactToken loadArtifactToken();

   List<ArtifactToken> loadArtifactTokens();

   List<ArtifactId> loadArtifactIds();

   List<Map<String, Object>> load();

   /**
    * same as select(attributeType, ValueKind.Value)
    */
   QueryBuilder select(AttributeTypeId attributeType);

   QueryBuilder select(AttributeTypeId attributeType, ValueKind valueKind);

   QueryBuilder select(AttributeTypeId attributeType, ValueKind[] valueKinds);

   /**
    * Follows the relation and does not collect any data (no valueKind), so it is only useful if followed by a select
    * call
    */
   QueryBuilder follow(RelationTypeId relationType, RelationSide side);

   QueryBuilder follow(RelationTypeId relationType, RelationSide side, ValueKind valueKind);

   QueryBuilder follow(RelationTypeId relationType, RelationSide side, ValueKind[] valueKinds);

   /**
    * @param attributeType is used in place of the natural Name attribute to populate the name fields in the returned
    * artifact tokens
    */
   List<ArtifactToken> loadArtifactTokens(AttributeTypeId attributeType);

   List<Map<String, Object>> loadMaps();

   /**
    * @return artifact search results
    */
   ResultSet<ArtifactReadable> getResults();

   ArtifactReadable getArtifact();

   /**
    * @return artifact search results with match locations
    */
   ResultSet<Match<ArtifactReadable, AttributeReadable<?>>> getMatches();

   /**
    * @return ids search results
    */
   ResultSet<? extends ArtifactId> getResultsIds();

   *
   Schedule query**@return
   artifact search results*/

   CancellableCallable<ResultSet<ArtifactReadable>> createSearch();

   /**
    * Schedule query and find matching locations
    *
    * @return artifact search results with match locations
    */
   CancellableCallable<ResultSet<Match<ArtifactReadable, AttributeReadable<?>>>> createSearchWithMatches();

   /**
    * Schedule query and find matching locations
    *
    * @return ids search results
    */
   CancellableCallable<ResultSet<? extends ArtifactId>> createSearchResultsAsIds();

}