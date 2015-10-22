/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.workitem;

import javax.ws.rs.core.Response;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.task.AtsTaskEndpointApi;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.JaxAtsTasks;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.ats.impl.workitem.CreateTasksOperation;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author Donald G. Dunne
 */
public class AtsTaskEndpointImpl implements AtsTaskEndpointApi {

   private final IAtsServer atsServer;

   public AtsTaskEndpointImpl(IAtsServer atsServer) {
      this.atsServer = atsServer;
   }

   @Override
   public Response create(NewTaskData newTaskData) {
      CreateTasksOperation operation = new CreateTasksOperation(newTaskData, atsServer, new XResultData());
      XResultData results = operation.validate();

      if (results.isErrors()) {
         throw new OseeArgumentException(results.toString());
      }
      operation.run();
      JaxAtsTasks tasks = new JaxAtsTasks();
      tasks.getTasks().addAll(operation.getTasks());
      return Response.ok().entity(tasks).build();
   }

   @Override
   public Response get(long taskUuid) {
      IAtsWorkItem task = atsServer.getQueryService().createQuery().isOfType(IAtsTask.class).andUuids(
         taskUuid).getResults().getOneOrNull();
      if (task == null) {
         throw new OseeArgumentException("No Task found with id %d", taskUuid);
      }
      JaxAtsTask jaxAtsTask = CreateTasksOperation.createNewJaxTask(task.getUuid(), atsServer);
      return Response.ok().entity(jaxAtsTask).build();
   }

   @Override
   public void delete(long taskUuid) {
      IAtsWorkItem task = atsServer.getQueryService().createQuery().isOfType(IAtsTask.class).andUuids(
         taskUuid).getResults().getOneOrNull();
      if (task != null) {
         IAtsChangeSet changes =
            atsServer.getStoreService().createAtsChangeSet("Delete Task", AtsCoreUsers.SYSTEM_USER);
         changes.deleteArtifact(task);
         changes.execute();
      }
   }

}