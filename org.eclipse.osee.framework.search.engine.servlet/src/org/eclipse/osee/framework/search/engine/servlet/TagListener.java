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
package org.eclipse.osee.framework.search.engine.servlet;

import org.eclipse.osee.framework.search.engine.TagListenerAdapter;

/**
 * @author Roberto E. Escobar
 */
public class TagListener extends TagListenerAdapter {

   private int queryId;

   public TagListener() {
      this.queryId = -1;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.TagListenerAdapter#onTagQueryIdSubmit(int)
    */
   @Override
   public void onTagQueryIdSubmit(int queryId) {
      this.queryId = queryId;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.ITagListener#onTagQueryIdTagComplete(int, long, long)
    */
   @Override
   synchronized public void onTagQueryIdTagComplete(int queryId, long waitTime, long processingTime) {
      if (this.queryId == queryId) {
         this.notify();
      }
   }

}
