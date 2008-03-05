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

import java.sql.SQLException;
import java.util.Arrays;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.WordArtifact;

/**
 * @author Ryan D. Brooks
 */
public class WordArtifactFactory extends ArtifactFactory<WordArtifact> {
   private static WordArtifactFactory factory = null;
   private static String[] WholeArtifactMatches =
         new String[] {"Checklist (WordML)", "Guideline", "How To", "Roadmap", "Template (WordML)",
               "Test Procedure WML", "Work Instruction", "Work Sheet (WordML)"};

   private WordArtifactFactory(int factoryId) {
      super(factoryId);
   }

   public static WordArtifactFactory getInstance(int factoryId) {
      if (factory == null) {
         factory = new WordArtifactFactory(factoryId);
      }
      return factory;
   }

   public static WordArtifactFactory getInstance() {
      return factory;
   }

   @Override
   public WordArtifact getNewArtifact(String guid, String humandReadableId, String factoryKey, Branch branch) throws SQLException {
      WordArtifact artifact = new WordArtifact(this, guid, humandReadableId, branch);
      artifact.setWholeWordArtifact(Arrays.binarySearch(WholeArtifactMatches, factoryKey) >= 0);
      return artifact;
   }
}