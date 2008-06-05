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
package org.eclipse.osee.ats.navigate;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition.WriteType;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class UpdateAtsWorkItemDefinitions extends XNavigateItemAction {

   /**
    * @param parent
    */
   public UpdateAtsWorkItemDefinitions(XNavigateItem parent) {
      super(parent, "Update Ats WorkItemDefinitions");
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.navigate.ActionNavigateItem#run()
    */
   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(), getName())) return;
      if (!MessageDialog.openConfirm(Display.getCurrent().getActiveShell(), getName(),
            "This could break lots of things, are you SURE?")) return;

      AbstractSkynetTxTemplate newActionTx = new AbstractSkynetTxTemplate(BranchPersistenceManager.getAtsBranch()) {

         @Override
         protected void handleTxWork() throws Exception {
            XResultData xResultData = new XResultData(SkynetGuiPlugin.getLogger());
            AtsWorkDefinitions.importWorkItemDefinitionsIntoDb(WriteType.Update, xResultData,
                  AtsWorkDefinitions.getAtsWorkDefinitions());
            if (xResultData.isEmpty()) {
               xResultData.log("Nothing updated");
            }
            xResultData.report(getName());
         }
      };
      newActionTx.execute();

      AWorkbench.popup("Completed", getName());
   }

}
