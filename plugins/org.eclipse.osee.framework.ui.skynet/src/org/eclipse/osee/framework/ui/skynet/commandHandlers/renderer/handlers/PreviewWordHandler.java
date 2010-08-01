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
package org.eclipse.osee.framework.ui.skynet.commandHandlers.renderer.handlers;

import java.util.logging.Level;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;

/**
 * @author Jeff C. Phillips
 */
public class PreviewWordHandler extends AbstractEditorHandler {

   @Override
   public Object execute(ExecutionEvent event) {
      if (!artifacts.isEmpty()) {
         try {
            WordTemplateRenderer renderer = new WordTemplateRenderer();
            renderer.setOptions(getOptions());
            renderer.open(artifacts, PresentationType.PREVIEW);
            dispose();

         } catch (OseeCoreException ex) {
            OseeLog.log(PreviewWordHandler.class, Level.SEVERE, ex);
         }
      }
      return null;
   }

   /**
    * A subclass may override this method if they would like options to be set on the renderer
    * 
    * @return
    */
   @SuppressWarnings("unused")
   protected VariableMap getOptions() throws OseeArgumentException {
      return null;
   }
}
