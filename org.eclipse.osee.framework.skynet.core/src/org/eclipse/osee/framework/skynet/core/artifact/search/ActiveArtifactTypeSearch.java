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
package org.eclipse.osee.framework.skynet.core.artifact.search;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * @author Donald G. Dunne
 */
public class ActiveArtifactTypeSearch {

   private final String artifactTypeName;
   private final Active active;
   private final Branch branch;

   /**
    * Search for given artifactType with active attribute set as specified
    * 
    * @param artifactTypeName
    * @param active
    * @param branch
    */
   public ActiveArtifactTypeSearch(String artifactTypeName, Active active, Branch branch) {
      super();
      this.artifactTypeName = artifactTypeName;
      this.active = active;
      this.branch = branch;
   }

   @SuppressWarnings("unchecked")
   public <A extends Artifact> Set<A> getArtifacts(Class<A> clazz) {
      Set<A> results = new HashSet<A>();

      try {
         Collection<Artifact> arts = null;
         if (active == Active.Both) {
            // Since both, just do a type search
            arts = ArtifactQuery.getArtifactsFromType(artifactTypeName, branch);
         } else {
            arts =
                  ArtifactQuery.getArtifactsFromTypeAndAttribute(artifactTypeName, "ats.Active",
                        active == Active.Active ? "yes" : "no", branch);
         }
         for (Artifact art : arts) {
            results.add((A) art);
         }
      } catch (SQLException ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.toString(), ex);
      }
      return results;
   }
}