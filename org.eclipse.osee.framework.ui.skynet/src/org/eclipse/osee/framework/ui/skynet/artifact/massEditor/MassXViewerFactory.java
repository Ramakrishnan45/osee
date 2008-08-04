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
package org.eclipse.osee.framework.ui.skynet.artifact.massEditor;

import java.util.List;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public class MassXViewerFactory extends SkynetXViewerFactory {

   private static String NAMESPACE = "org.eclipse.osee.framework.ui.skynet.massEditor.ArtifactXViewer";
   private CustomizeData custData;
   private String namespace = NAMESPACE;

   /**
    * @param namespace the namespace to set
    */
   public void setNamespace(String namespace) {
      this.namespace = namespace;
   }

   public MassXViewerFactory() {
      super(NAMESPACE);
   }

   @Override
   public XViewerSorter createNewXSorter(XViewer xViewer) {
      return new XViewerSorter(xViewer);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerFactory#getDefaultTableCustomizeData()
    */
   @Override
   public CustomizeData getDefaultTableCustomizeData() {
      if (custData != null) {
         // Return a copy so don't corrupt original columns
         CustomizeData custData = new CustomizeData();
         custData.setNameSpace(custData.getNameSpace());
         custData.getColumnData().setColumns(getColumns());
      }
      return super.getDefaultTableCustomizeData();
   }

   /**
    * @param custData the custData to set
    */
   public void setColumns(List<XViewerColumn> columns) {
      clearColumnRegistration();
      for (XViewerColumn xCol : columns) {
         registerColumn(xCol);
      }
   }

   public void setDefaultCustData(CustomizeData custData) {
      this.custData = custData;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerFactory#getNamespace()
    */
   @Override
   public String getNamespace() {
      return namespace;
   }

}
