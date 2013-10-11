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
package org.eclipse.osee.ats.core.client;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueService;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueServiceProvider;
import org.eclipse.osee.ats.api.query.IAtsQueryService;
import org.eclipse.osee.ats.api.version.IAtsVersionServiceProvider;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemService;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemServiceProvider;
import org.eclipse.osee.ats.core.client.internal.IAtsWorkItemArtifactServiceProvider;
import org.eclipse.osee.ats.core.config.IActionableItemFactory;
import org.eclipse.osee.ats.core.config.IAtsConfigProvider;
import org.eclipse.osee.ats.core.config.ITeamDefinitionFactory;
import org.eclipse.osee.ats.core.config.IVersionFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Donald G. Dunne
 */
public interface IAtsClient extends IAtsWorkItemArtifactServiceProvider, IAtsWorkItemServiceProvider, IAtsConfigProvider, ITeamDefinitionFactory, IActionableItemFactory, IVersionFactory, IAtsQueryService, IAtsVersionServiceProvider, IAtsEarnedValueServiceProvider {

   <T extends IAtsConfigObject> Artifact storeConfigObject(T configObject, SkynetTransaction transaction) throws OseeCoreException;

   <T extends IAtsConfigObject> T getConfigObject(Artifact artifact) throws OseeCoreException;

   Artifact getConfigArtifact(IAtsConfigObject atsConfigObject) throws OseeCoreException;

   /**
    * @return corresponding Artifact or null if not found
    */
   Artifact getArtifact(IAtsObject atsObject) throws OseeCoreException;

   List<Artifact> getConfigArtifacts(Collection<? extends IAtsObject> atsObjects) throws OseeCoreException;

   <T extends IAtsConfigObject> Collection<T> getConfigObjects(Collection<? extends Artifact> artifacts, Class<T> clazz) throws OseeCoreException;

   void invalidateConfigCache();

   void reloadConfigCache() throws OseeCoreException;

   void reloadWorkDefinitionCache() throws OseeCoreException;

   void invalidateWorkDefinitionCache();

   void reloadAllCaches() throws OseeCoreException;

   void invalidateAllCaches();

   IAtsWorkDefinitionAdmin getWorkDefinitionAdmin() throws OseeStateException;

   @Override
   IAtsVersionAdmin getAtsVersionService() throws OseeStateException;

   IAtsUserAdmin getUserAdmin() throws OseeStateException;

   @Override
   IAtsWorkItemService getWorkItemService() throws OseeStateException;

   @Override
   IAtsEarnedValueService getEarnedValueService() throws OseeStateException;

}
