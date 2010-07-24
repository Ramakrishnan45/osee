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
package org.eclipse.osee.framework.ui.admin;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Jeff C. Phillips
 */
public class AdminAction implements IWorkbenchWindowActionDelegate {

   @Override
   public void run(IAction proxyAction) {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      try {
         page.showView(AdminView.VIEW_ID);
      } catch (PartInitException e1) {
         MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Launch Error",
            "Couldn't Launch Admin View " + e1.getMessage());
      }
   }

   // IActionDelegate method
   @Override
   public void selectionChanged(IAction proxyAction, ISelection selection) {

   }

   // IWorkbenchWindowActionDelegate method
   @Override
   public void init(IWorkbenchWindow window) {
   }

   // IWorkbenchWindowActionDelegate method
   @Override
   public void dispose() {
      // nothing to do
   }
}