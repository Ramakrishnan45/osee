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
package org.eclipse.osee.define.traceability.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.enums.IRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractArtifactRelationReport extends AbstractReport {

   private final Set<Artifact> artifactsToCheck;
   private final List<IRelationEnumeration> relationsToCheck;

   public AbstractArtifactRelationReport() {
      super();
      this.artifactsToCheck = new HashSet<Artifact>();
      this.relationsToCheck = new ArrayList<IRelationEnumeration>();
   }

   public void setArtifacts(Collection<Artifact> artifacts) {
      if (artifacts != null) {
         this.artifactsToCheck.addAll(artifacts);
      }
   }

   public void addRelationToCheck(IRelationEnumeration relation) {
      if (relation != null) {
         this.relationsToCheck.add(relation);
      }
   }

   public Artifact[] getArtifactsToCheck() {
      return artifactsToCheck.toArray(new Artifact[artifactsToCheck.size()]);
   }

   public IRelationEnumeration[] getRelationsToCheck() {
      return relationsToCheck.toArray(new IRelationEnumeration[relationsToCheck.size()]);
   }

   public void clear() {
      artifactsToCheck.clear();
      relationsToCheck.clear();
      super.clear();
   }
}
