/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.search.engines;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.db.internal.proxy.AttributeDataProxyFactory;

/**
 * This loader depends upon query to be sorted by pirmary object id (i.e. artifact id)
 *
 * @author Ryan D. Brooks
 */
public class SelectiveLoader {
   private final List<Map<String, Object>> results = new ArrayList<>(100);
   private final AttributeDataProxyFactory proxyFactory;
   private final String mainIdName;
   private Long previousMainId;
   private Map<String, Object> mainMap;

   SelectiveLoader(AttributeDataProxyFactory proxyFactory, String mainIdName) {
      this.proxyFactory = proxyFactory;
      this.mainIdName = mainIdName;
   }

   public void load(JdbcStatement stmt) {
      Long currentMainId = stmt.getLong(mainIdName);

      if (!currentMainId.equals(previousMainId)) {
         previousMainId = currentMainId;
         mainMap = new HashMap<>();
         results.add(mainMap);
         mainMap.put(mainIdName, currentMainId);
      }
      loadAttribute(stmt);
   }

   private void loadAttribute(JdbcStatement stmt) {
      String attTypeId = stmt.getString("attr_type_id");
      ArrayList<String> attrValues = (ArrayList<String>) mainMap.get(attTypeId);
      if (attrValues == null) {
         attrValues = new ArrayList<>();
         mainMap.put(attTypeId, attrValues);
      }
      DataProxy<?> createProxy = proxyFactory.createProxy(null, stmt.getString("value"), stmt.getString("uri"));
      attrValues.add(createProxy.getStorageString());
   }

   public List<Map<String, Object>> getResults() {
      return results;
   }
}