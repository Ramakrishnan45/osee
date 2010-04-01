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
package org.eclipse.osee.framework.messaging.event.res;

import org.eclipse.osee.framework.messaging.event.res.event.NetworkSender;

/**
 * @author Donald G. Dunne
 */
public interface IFrameworkEvent {
   public NetworkSender getNetworkSender();

   public void toXml(StringBuffer sb);

   public void fromXml(String xml);

   public String getEventGuid();
}
