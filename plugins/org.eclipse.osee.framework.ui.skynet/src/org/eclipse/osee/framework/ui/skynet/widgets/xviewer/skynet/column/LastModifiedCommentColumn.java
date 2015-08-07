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
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column;

import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class LastModifiedCommentColumn extends XViewerValueColumn {

   public static LastModifiedCommentColumn instance = new LastModifiedCommentColumn();

   public static LastModifiedCommentColumn getInstance() {
      return instance;
   }

   public LastModifiedCommentColumn() {
      super("framework.lastModComment", "Last Modified Comment", 100, SWT.LEFT, false, SortDataType.String, false,
         "Retrieves transaction comment of last attribute update of this artifact.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public LastModifiedCommentColumn copy() {
      LastModifiedCommentColumn newXCol = new LastModifiedCommentColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof Artifact) {
            return ((Artifact) element).getTransactionRecord().getComment();
         } else if (element instanceof Change) {
            return ((Change) element).getChangeArtifact().getTransactionRecord().getComment();
         }
      } catch (OseeCoreException ex) {
         return XViewerCells.getCellExceptionString(ex);
      }
      return "";
   }

}