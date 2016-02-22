/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.internal;

import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.rest.DispoConstants;
import org.eclipse.osee.framework.jdk.core.type.BaseIdentity;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Angel Avila
 */
public class DispoSetArtifact extends BaseIdentity<String> implements DispoSet {

   private final ArtifactReadable artifact;

   public DispoSetArtifact(ArtifactReadable artifact) {
      super(artifact.getGuid());
      this.artifact = artifact;
   }

   @Override
   public String getName() {
      return artifact.getName();
   }

   @Override
   public String getImportPath() {
      return artifact.getSoleAttributeAsString(DispoConstants.ImportPath);
   }

   @Override
   public JSONArray getNotesList() {
      String notesJson = artifact.getSoleAttributeAsString(DispoConstants.DispoNotesJson, "[]");
      try {
         return new JSONArray(notesJson);
      } catch (JSONException ex) {
         throw new OseeCoreException("Could not parse Notes Json", ex);
      }
   }

   @Override
   public JSONObject getOperationSummary() {
      String operationSummaryJson = artifact.getSoleAttributeAsString(DispoConstants.OperationSummary, "{}");
      try {
         JSONObject toReturn = new JSONObject(operationSummaryJson);
         return toReturn;
      } catch (JSONException ex) {
         throw new OseeCoreException("Could not parse Operation Sumary Json", ex);
      }
   }

   @Override
   public String toString() {
      return getName();
   }

   @Override
   public String getImportState() {
      return artifact.getSoleAttributeAsString(DispoConstants.ImportState, "None");
   }

   @Override
   public String getDispoType() {
      return artifact.getSoleAttributeAsString(DispoConstants.DispoType, "");
   }
}
