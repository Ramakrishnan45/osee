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
package org.eclipse.osee.ats.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

/**
 * @author Donald G. Dunne
 */
public class ShowWorkDefinitionAction extends Action {

   public ShowWorkDefinitionAction() {
      super();
      setText("Show Work Definition for Active Workflow");
      setToolTipText(getText());
   }

   @Override
   public void run() {
      IWorkbenchPage page = AWorkbench.getActivePage();
      try {
         page.showView("org.eclipse.ui.views.ContentOutline", null, IWorkbenchPage.VIEW_ACTIVATE);
      } catch (PartInitException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.WORK_DEFINITION);
   }

}
