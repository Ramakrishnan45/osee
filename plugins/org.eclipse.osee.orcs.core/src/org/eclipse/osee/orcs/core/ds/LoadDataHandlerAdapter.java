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
package org.eclipse.osee.orcs.core.ds;

import java.util.Map;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public class LoadDataHandlerAdapter implements LoadDataHandler {

   @Override
   public void onLoadStart() throws OseeCoreException {
      //
   }

   @Override
   public void onLoadDescription(LoadDescription data) throws OseeCoreException {
      //
   }

   @Override
   public void onData(BranchData data) throws OseeCoreException {
      //
   }

   @Override
   public void onData(TxOrcsData data) throws OseeCoreException {
      //
   }

   @Override
   public void onData(ArtifactData data) throws OseeCoreException {
      //
   }

   @Override
   public void onData(AttributeData data) throws OseeCoreException {
      //
   }

   @Override
   public void onData(RelationData data) throws OseeCoreException {
      //
   }

   @Override
   public void onData(AttributeData data, MatchLocation match) throws OseeCoreException {
      //
   }

   @Override
   public void onDynamicData(Map<String, Object> data) throws OseeCoreException {
      //
   }

   @Override
   public void onLoadEnd() throws OseeCoreException {
      //
   }
}
