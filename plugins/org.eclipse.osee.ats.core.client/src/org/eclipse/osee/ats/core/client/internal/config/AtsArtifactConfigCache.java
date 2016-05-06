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
package org.eclipse.osee.ats.core.client.internal.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.core.client.search.AtsArtifactQuery;
import org.eclipse.osee.ats.core.config.AtsConfigCache;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class AtsArtifactConfigCache extends AtsConfigCache {

   public List<Artifact> getArtifacts(Collection<? extends IAtsObject> atsObjects) throws OseeCoreException {
      List<Artifact> artifacts = new LinkedList<>();
      List<Long> uuids = new ArrayList<>();
      for (IAtsObject atsObject : atsObjects) {
         Artifact artifact = getArtifact(atsObject);
         if (artifact != null) {
            artifacts.add(artifact);
         } else {
            uuids.add(atsObject.getUuid());
         }
      }
      artifacts.addAll(AtsArtifactQuery.getArtifactListFromIds(uuids));
      return artifacts;
   }

   public <A extends IAtsConfigObject> Collection<A> getConfigObjects(Collection<? extends Artifact> artifacts, Class<A> clazz) {
      List<A> objects = new ArrayList<>();
      for (Artifact art : artifacts) {
         objects.addAll(getByTag(art.getGuid(), clazz));
      }
      return objects;
   }

   public Artifact getArtifact(IAtsObject atsObject) throws OseeCoreException {
      Conditions.checkNotNull(atsObject, "atsConfigObject");
      Artifact artifact = (Artifact) atsObject.getStoreObject();
      if (artifact == null) {
         try {
            artifact = AtsArtifactQuery.getArtifactFromId(atsObject.getUuid());
         } catch (ArtifactDoesNotExist ex) {
            // do nothing
         }
      }
      return artifact;
   }

}