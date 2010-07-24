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
package org.eclipse.osee.framework.core.server;

import org.eclipse.osee.framework.core.server.internal.InternalOseeHttpServlet;

/**
 * @author Roberto E. Escobar
 */
public abstract class OseeHttpServlet extends InternalOseeHttpServlet {

   private static final long serialVersionUID = -4747761442607851113L;
   //
   //	@Override
   //	protected void checkAccessControl(HttpServletRequest request) throws OseeCoreException {
   //		String sessionId = request.getParameter("sessionId");
   //		String interaction =
   //					String.format("%s %s %s", request.getMethod(), request.getRequestURI(), request.getQueryString());
   //		ServerActivator.getSessionManager().updateSessionActivity(sessionId, interaction);
   //	}
   //
   //	public boolean isInitializing(HttpServletRequest request) throws OseeCoreException {
   //		String sessionId = request.getParameter("sessionId");
   //		ISession session = ServerActivator.getSessionManager().getSessionById(sessionId);
   //		String userId = session.getUserId();
   //		return SystemUser.BootStrap.getUserID().equals(userId);
   //	}
}
