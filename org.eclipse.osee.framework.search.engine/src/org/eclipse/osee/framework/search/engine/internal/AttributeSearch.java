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
package org.eclipse.osee.framework.search.engine.internal;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.search.engine.Options;
import org.eclipse.osee.framework.search.engine.utility.IRowProcessor;
import org.eclipse.osee.framework.search.engine.utility.ITagCollector;

/**
 * @author Roberto E. Escobar
 */
public final class AttributeSearch implements ITagCollector {
   private List<Long> tags;
   private String searchString;
   private Options options;

   public AttributeSearch(String searchString, Options options) {
      this.tags = new ArrayList<Long>();
      this.searchString = searchString;
      this.options = options;
   }

   public Set<AttributeVersion> findMatches() throws Exception {
      final Set<AttributeVersion> toReturn = new HashSet<AttributeVersion>();
      try {
         TagProcessor.collectFromString(searchString, this);

         for (Long codedTag : tags) {
            SearchTagDb.executeQuery(SearchTagDb.getQuery(options), new IRowProcessor() {
               @Override
               public void processRow(ResultSet resultSet) throws Exception {
                  toReturn.add(new AttributeVersion(resultSet));
               }
            }, SQL3DataType.BIGINT, codedTag);
         }
      } finally {
         tags.clear();
         tags = null;
      }
      return toReturn;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.utility.ITagCollector#addTag(java.lang.Long)
    */
   @Override
   public void addTag(Long codedTag) {
      tags.add(codedTag);
   }
}
