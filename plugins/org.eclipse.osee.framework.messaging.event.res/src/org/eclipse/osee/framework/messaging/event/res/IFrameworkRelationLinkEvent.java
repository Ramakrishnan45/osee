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

/**
 * @author Donald G. Dunne
 */
public interface IFrameworkRelationLinkEvent extends IFrameworkEvent {

   public int getArtAId();

   public int getArtATypeId();

   public int getArtBId();

   public int getArtBTypeId();

   public Integer getRelId();

   public Integer getGammaId();

   public int getRelTypeId();

   public int getId();
}
