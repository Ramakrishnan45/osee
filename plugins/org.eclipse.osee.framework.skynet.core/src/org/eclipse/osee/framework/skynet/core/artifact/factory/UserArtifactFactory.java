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
package org.eclipse.osee.framework.skynet.core.artifact.factory;

import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;

/**
 * @author Donald G. Dunne
 */
public class UserArtifactFactory extends ArtifactFactory {

   public UserArtifactFactory() {
      super(CoreArtifactTypes.User);
   }

   @Override
   public Artifact getArtifactInstance(String guid, Branch branch, IArtifactType artifactType, boolean inDataStore) throws OseeCoreException {
      return new User(guid, branch, artifactType);
   }

   @Override
   public boolean isUserCreationEnabled(IArtifactType artifactType) {
      return false;
   }

}