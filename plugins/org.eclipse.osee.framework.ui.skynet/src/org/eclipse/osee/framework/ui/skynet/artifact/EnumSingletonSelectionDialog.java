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
package org.eclipse.osee.framework.ui.skynet.artifact;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.plugin.util.StringLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * @author Donald G. Dunne
 */
public class EnumSingletonSelectionDialog extends ListDialog {

   private boolean isRemoveAllAllowed = true;
   private boolean removeAllSelected = false;

   public EnumSingletonSelectionDialog(AttributeTypeId attributeType, Collection<? extends Artifact> artifacts) {
      super(Displays.getActiveShell());
      Set<String> options;
      try {
         options = AttributeTypeManager.getEnumerationValues(attributeType);
         isRemoveAllAllowed = AttributeTypeManager.checkIfRemovalAllowed(attributeType, artifacts);

      } catch (OseeCoreException ex) {
         options = new HashSet<>();
         options.add(ex.getLocalizedMessage());
      }
      setInput(options);
      setTitle("Select Option (Singleton)");
      setMessage("Select option or Remove All.");
      setContentProvider(new ArrayContentProvider());
      setLabelProvider(new StringLabelProvider());
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control control = super.createDialogArea(container);

      Composite composite = new Composite(container, SWT.None);
      composite.setLayout(new GridLayout(2, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

      final XCheckBox checkBox = new XCheckBox("Remove All");
      checkBox.setEditable(isRemoveAllAllowed);
      checkBox.setVerticalLabel(false);
      checkBox.createWidgets(composite, 2);
      checkBox.addXModifiedListener(new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {
            removeAllSelected = checkBox.isChecked();
         }
      });

      return control;
   }

   public boolean isRemoveAllSelected() {
      if (!isRemoveAllAllowed) {
         return false;
      } else {
         return removeAllSelected;
      }
   }

   public String getSelectedOption() {
      if (getResult().length == 0) {
         return "";
      }
      return (String) getResult()[0];
   }
}
