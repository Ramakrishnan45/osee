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
package org.eclipse.osee.framework.ui.skynet.artifact.editor.sections;

import org.eclipse.osee.framework.ui.skynet.RelationsComposite;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.pages.ArtifactFormPage;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactDragAndDrop;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Roberto E. Escobar
 */
public class RelationsFormSection extends ArtifactEditorFormSection {

   private RelationsComposite relationComposite;
   private boolean sectionCreated = false;

   public RelationsFormSection(AbstractArtifactEditor editor, Composite parent, FormToolkit toolkit, int style) {
      super(editor, parent, toolkit, style);
   }

   @Override
   public void initialize(IManagedForm form) {
      super.initialize(form);
      final FormToolkit toolkit = form.getToolkit();

      final Section section = getSection();
      section.setText("Relations");

      section.setLayout(new GridLayout());
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      // Only load when users selects section
      section.addListener(SWT.Activate, new Listener() {

         @Override
         public void handleEvent(Event e) {
            createSection(section, toolkit);
         }
      });
   }

   protected synchronized void createSection(Section section, FormToolkit toolkit) {
      if (sectionCreated) {
         return;
      }
      Composite sectionBody = toolkit.createComposite(section, toolkit.getBorderStyle());
      sectionBody.setLayout(ALayout.getZeroMarginLayout());
      sectionBody.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      Label dragDropLabel = new Label(sectionBody, SWT.BORDER);
      dragDropLabel.setText("Click here to drag this \"" + getEditorInput().getArtifact().getArtifactTypeName() + "\"");
      GridData gd = new GridData(GridData.FILL_BOTH);
      gd.heightHint = 25;
      dragDropLabel.setLayoutData(gd);
      addDragAndDrop(dragDropLabel);
      toolkit.adapt(dragDropLabel, true, true);

      relationComposite = new RelationsComposite(getEditor(), sectionBody, SWT.NONE, getEditorInput().getArtifact());
      relationComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      section.setClient(sectionBody);
      toolkit.paintBordersFor(section);

      relationComposite.getTreeViewer().getTree().addTreeListener(new TreeListener() {

         @Override
         public void treeCollapsed(TreeEvent e) {
            redrawPage();
         }

         @Override
         public void treeExpanded(TreeEvent e) {
            redrawPage();
         }

         private void redrawPage() {
            Displays.ensureInDisplayThread(new Runnable() {
               @Override
               public void run() {
                  getSection().getParent().layout();
                  getManagedForm().reflow(true);
               }
            });
         }
      });
      sectionCreated = true;
   }

   protected void handleExpandAndCollapse() {
      ((ArtifactFormPage) getEditor().getSelectedPage()).refresh();
   }

   protected void addDragAndDrop(Control dropArea) {
      new ArtifactDragAndDrop(dropArea, getEditorInput().getArtifact(), ArtifactEditor.EDITOR_ID);
   }

   public RelationsComposite getRelationComposite() {
      return relationComposite;
   }

   @Override
   public void refresh() {
      super.refresh();
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (Widgets.isAccessible(relationComposite)) {
               relationComposite.refresh();
            }
         }
      });
   }

   @Override
   public void dispose() {
      if (Widgets.isAccessible(relationComposite)) {
         relationComposite.dispose();
      }
      super.dispose();
   }
}
