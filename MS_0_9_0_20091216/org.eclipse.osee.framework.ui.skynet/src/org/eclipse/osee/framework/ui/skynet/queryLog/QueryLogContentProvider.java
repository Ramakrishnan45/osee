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
package org.eclipse.osee.framework.ui.skynet.queryLog;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.database.sql.QueryLog;
import org.eclipse.osee.framework.database.sql.QueryRecord;

/**
 * @author Robert A. Fisher
 */
public class QueryLogContentProvider implements ITreeContentProvider {

   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof QueryLog) {
         return ((QueryLog) parentElement).getRecords().toArray();
      } else if (parentElement instanceof QueryRecord) {
         QueryRecord record = (QueryRecord) parentElement;

         if (record.getSqlException() != null) {
            return new Object[] {record.getSqlException()};
         }
         return record.getBindVariables();
      } else if (parentElement instanceof Exception) {
         return ((Exception) parentElement).getStackTrace();
      }
      return null;
   }

   // Only needed to support the reveal() method, not implementing yet
   public Object getParent(Object element) {
      return null;
   }

   public boolean hasChildren(Object element) {
      if (element instanceof QueryLog) {
         return ((QueryLog) element).getRecords().size() > 0;
      } else if (element instanceof QueryRecord) {
         QueryRecord record = (QueryRecord) element;
         return record.getBindVariables().length > 0 || record.getSqlException() != null;
      } else if (element instanceof Exception) {
         return ((Exception) element).getStackTrace().length > 0;
      }
      return false;
   }

   public Object[] getElements(Object inputElement) {
      if (inputElement instanceof QueryLog) {
         return getChildren(inputElement);
      }
      throw new IllegalArgumentException("Expect a " + QueryLog.class.getCanonicalName() + " object");
   }

   public void dispose() {
   }

   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
   }
}
