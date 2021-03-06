/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.enums;

import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.TokenFactory;

public final class CoreArtifactTokens {

   // @formatter:off
   public static ArtifactToken GlobalPreferences = TokenFactory.createArtifactToken(18026, "AAABE8T1j3AA8O7WNsu89A", CoreArtifactTypes.GlobalPreferences.getName(), CoreArtifactTypes.GlobalPreferences);
   public static ArtifactToken XViewerGlobalCustomization = TokenFactory.createArtifactToken(78293, "AAABER+3rLwA8O7WMgtX1g", CoreArtifactTypes.XViewerGlobalCustomization.getName(), CoreArtifactTypes.XViewerGlobalCustomization);
   public static ArtifactToken DefaultHierarchyRoot = TokenFactory.createArtifactToken(197818, "AEslkN+d4hWXjQvnZ1gA", "Default Hierarchy Root", CoreArtifactTypes.RootArtifact);
   public static ArtifactToken UniversalGroupRoot = TokenFactory.createArtifactToken(60807, "AExdLMeOTGhhPY4CyvQA", "Root Artifact", CoreArtifactTypes.UniversalGroup);
   public static ArtifactToken Everyone = TokenFactory.createArtifactToken(48656, "AAABEbn4DKoAaR82FZsL3A", "Everyone", CoreArtifactTypes.UserGroup);
   public static ArtifactToken OseeAdmin = TokenFactory.createArtifactToken(52247, "AAABHaItmnUAG6ZAYlFKag", "OseeAdmin", CoreArtifactTypes.UserGroup);
   public static ArtifactToken OseeAccessAdmin = TokenFactory.createArtifactToken(8033605, "AGXiIJi2qxZnuXEdZVwA", "OseeAccessAdmin", CoreArtifactTypes.UserGroup);
   public static ArtifactToken UserGroups = TokenFactory.createArtifactToken(80920, "AAABGTAGNY8BauB5GajiIQ", "User Groups", CoreArtifactTypes.Folder);
   // @formatter:on

   private CoreArtifactTokens() {
      // Constants
   }
}
