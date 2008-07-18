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
package org.eclipse.osee.framework.ui.skynet.widgets.xchange;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.XViewerAttributeSortDataType;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerAttributeFromChangeColumn;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class ChangeXViewerFactory extends SkynetXViewerFactory {

   public static String COLUMN_NAMESPACE = "framework.change.";
   public static final XViewerColumn Name =
         new XViewerColumn(COLUMN_NAMESPACE + "artifactNames", "Artifact name(s)", 250, SWT.LEFT, true,
               SortDataType.String, false);
   public static final XViewerColumn Item_Type =
         new XViewerColumn(COLUMN_NAMESPACE + "itemType", "Item Type", 100, SWT.LEFT, true, SortDataType.String, false);
   public static final XViewerColumn Item_Kind =
         new XViewerColumn(COLUMN_NAMESPACE + "itemKind", "Item Kind", 70, SWT.LEFT, true, SortDataType.String, false);
   public static final XViewerColumn Change_Type =
         new XViewerColumn(COLUMN_NAMESPACE + "changeType", "Change Type", 50, SWT.LEFT, true, SortDataType.String,
               false);
   // TODO Temporary column until dynamic attributes can be added
   public static final XViewerColumn CSCI =
         new XViewerColumn(COLUMN_NAMESPACE + "csci", "CSCI", 50, SWT.LEFT, true, SortDataType.String, false);
   public static final XViewerColumn Is_Value =
         new XViewerColumn(COLUMN_NAMESPACE + "isValue", "Is Value", 150, SWT.LEFT, true, SortDataType.String, false);
   public static final XViewerColumn Was_Value =
         new XViewerColumn(COLUMN_NAMESPACE + "wasValue", "Was Value", 300, SWT.LEFT, true, SortDataType.String, false);

   public static final List<XViewerColumn> columns =
         Arrays.asList(Name, Item_Type, Item_Kind, Change_Type, CSCI, Is_Value, Was_Value);
   public static Map<String, XViewerColumn> idToColumn = null;

   public ChangeXViewerFactory() {
      if (idToColumn == null) {
         idToColumn = new HashMap<String, XViewerColumn>();
         for (XViewerColumn xCol : columns) {
            idToColumn.put(xCol.getId(), xCol);
         }
      }
   }

   public XViewerSorter createNewXSorter(XViewer xViewer) {
      return new XViewerSorter(xViewer);
   }

   public CustomizeData getDefaultTableCustomizeData(XViewer xViewer) {
      CustomizeData custData = new CustomizeData();
      ArrayList<XViewerColumn> cols = new ArrayList<XViewerColumn>();
      for (XViewerColumn xCol : columns) {
         xCol.setXViewer(xViewer);
         cols.add(xCol);
      }
      try {
         // TODO change from getcommonbranch to getBranch(xViewer) when fixed
         for (AttributeType attributeType : AttributeTypeManager.getTypes(BranchPersistenceManager.getCommonBranch())) {
            XViewerAttributeFromChangeColumn newCol =
                  new XViewerAttributeFromChangeColumn(xViewer, attributeType.getName(), attributeType.getName(), 75,
                        75, SWT.LEFT, false, XViewerAttributeSortDataType.get(attributeType));
            newCol.setXViewer(xViewer);
            cols.add(newCol);
         }
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, false);
      }
      custData.getColumnData().setColumns(cols);
      return custData;
   }

   private Branch getBranch(XViewer xViewer) throws OseeCoreException, SQLException {
      Branch branch = ((ChangeXViewer) xViewer).getXChangeViewer().getBranch();
      if (branch == null) {
         TransactionId transId = ((ChangeXViewer) xViewer).getXChangeViewer().getTransactionId();
         if (transId != null) return transId.getBranch();
      }
      return null;
   }

}
