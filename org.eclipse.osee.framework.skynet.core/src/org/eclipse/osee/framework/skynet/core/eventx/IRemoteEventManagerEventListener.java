/*
 * Created on Sep 13, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.eventx;

import org.eclipse.osee.framework.ui.plugin.event.Sender;

/**
 * @author Donald G. Dunne
 */
public interface IRemoteEventManagerEventListener extends IXEventListener {
   public void handleRemoteEventManagerEvent(Sender sender, RemoteEventModType remoteEventModType);

}
