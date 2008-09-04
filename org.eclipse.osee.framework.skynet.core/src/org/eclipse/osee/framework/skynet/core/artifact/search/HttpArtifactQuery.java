/*
 * Created on Sep 3, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.artifact.search;

import java.io.ByteArrayOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.db.connection.core.JoinUtility;
import org.eclipse.osee.framework.jdk.core.type.ObjectPair;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.jdk.core.util.OseeApplicationServerContext;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoad;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.ISearchConfirmer;
import org.eclipse.osee.framework.skynet.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.skynet.core.linking.HttpUrlBuilder;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Roberto E. Escobar
 */
public class HttpArtifactQuery {
   private String queryString;
   private boolean nameOnly;
   private boolean includeDeleted;
   private Branch branch;

   public HttpArtifactQuery(String queryString, boolean nameOnly, boolean includeDeleted, Branch branch) {
      this.branch = branch;
      this.includeDeleted = includeDeleted;
      this.nameOnly = nameOnly;
      this.queryString = queryString;
   }

   private String getSearchUrl() throws OseeDataStoreException {
      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("query", queryString);
      parameters.put("branchId", Integer.toString(branch.getBranchId()));
      if (includeDeleted) {
         parameters.put("include deleted", Boolean.toString(includeDeleted));
      }
      if (nameOnly) {
         parameters.put("name only", Boolean.toString(nameOnly));
      }
      return HttpUrlBuilder.getInstance().getOsgiServletServiceUrl(OseeApplicationServerContext.SEARCH_CONTEXT,
            parameters);
   }

   public List<Artifact> getArtifacts(ArtifactLoad loadLevel, ISearchConfirmer confirmer, boolean reload, boolean historical) throws Exception {
      List<Artifact> toReturn = null;
      ObjectPair<Integer, Integer> queryIdAndSize = executeSearch(getSearchUrl());
      if (queryIdAndSize != null && queryIdAndSize.object2 > 0) {
         try {
            toReturn =
                  ArtifactLoader.loadArtifactsFromQueryId(queryIdAndSize.object1, loadLevel, confirmer,
                        queryIdAndSize.object2, reload, historical);
         } finally {
            JoinUtility.deleteQuery(JoinUtility.JoinItem.ARTIFACT, queryIdAndSize.object1.intValue());
         }
      }
      if (toReturn == null) {
         toReturn = java.util.Collections.emptyList();
      }
      return toReturn;
   }

   private ObjectPair<Integer, Integer> executeSearch(String searchUrl) throws Exception {
      ObjectPair<Integer, Integer> toReturn = null;
      Result result = SkynetActivator.areOSEEServicesAvailable();
      if (result.isTrue()) {
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         AcquireResult httpRequestResult = HttpProcessor.acquire(new URL(searchUrl), outputStream);
         if (httpRequestResult.wasSuccessful()) {
            String queryIdString = outputStream.toString("UTF-8");
            if (Strings.isValid(queryIdString)) {
               String[] entries = queryIdString.split(",\\s*");
               if (entries.length >= 2) {
                  toReturn = new ObjectPair<Integer, Integer>(new Integer(entries[0]), new Integer(entries[1]));
               }
            }
         } else if (httpRequestResult.getCode() != HttpURLConnection.HTTP_NO_CONTENT) {
            throw new Exception(String.format("Search error due to bad request: url[%s] status code: [%s]", searchUrl,
                  httpRequestResult.getCode()));
         }
      } else {
         throw new Exception(String.format("Unable to perform search: %s", result.getText()));
      }
      return toReturn;
   }
}
