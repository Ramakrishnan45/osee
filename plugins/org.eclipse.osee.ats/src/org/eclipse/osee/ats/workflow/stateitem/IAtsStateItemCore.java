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
package org.eclipse.osee.ats.workflow.stateitem;

import org.eclipse.osee.ats.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.util.Result;

/**
 * @author Megumi Telles
 */
public interface IAtsStateItemCore {

   public Result workingBranchCreated(AbstractWorkflowArtifact awa);

}