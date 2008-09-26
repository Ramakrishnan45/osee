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
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.osee.framework.ui.plugin.util.EnumStringMultiSelectionDialog;
import org.eclipse.osee.framework.ui.plugin.util.EnumStringSingleSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.DateSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class XPromptChange {

   private final static String VALID_FLOAT_REG_EX = "^[0-9\\.]+$";
   private final static String VALID_INTEGER_REG_EX = "^[0-9]+$";
   private final static String VALID_PERCENT_REG_EX =
         "^(0*100{1,1}\\.?((?<=\\.)0*)?%?$)|(^0*\\d{0,2}\\.?((?<=\\.)\\d*)?%?)$";
   public static enum Option {
      SINGLE_LINE, MULTI_LINE
   };

   public static Date promptChangeDate(String displayName, Date currDate) throws SQLException {
      // prompt that current release is (get from attribute); want to
      // change
      DateSelectionDialog diag =
            new DateSelectionDialog("Select " + displayName, "Select " + displayName,
                  currDate != null ? currDate : null);
      if (diag.open() == 0) {
         return diag.getSelectedDate();
      }
      return null;
   }

   public static EnumStringSingleSelectionDialog promptChangeSingleSelectEnumeration(String displayName, Collection<String> enums, String currSelected) throws SQLException {
      final EnumStringSingleSelectionDialog diag =
            new EnumStringSingleSelectionDialog(displayName, displayName, enums, currSelected);
      if (diag.open() == 0) {
         return diag;
      }
      return null;
   }

   public static EnumStringMultiSelectionDialog promptChangeMultiSelectEnumeration(String displayName, Collection<String> enums, Collection<String> currEnums) throws SQLException {
      final EnumStringMultiSelectionDialog diag = new EnumStringMultiSelectionDialog(displayName, enums, currEnums);
      if (diag.open() == 0) {
         return diag;
      }
      return null;
   }

   public static String promptChangeInteger(String displayName, int currEntry) throws SQLException {
      return promptChangeInteger(displayName, currEntry == 0 ? "" : String.valueOf(currEntry));
   }

   public static String promptChangeInteger(String displayName, String currEntry) throws SQLException {
      return promptChangeString(displayName, currEntry, VALID_INTEGER_REG_EX);
   }

   public static String promptChangePercent(String displayName, String currEntry) throws SQLException {
      return promptChangeString(displayName, currEntry, VALID_PERCENT_REG_EX);
   }

   public static String promptChangeFloat(String displayName, double currEntry) throws SQLException {
      return promptChangeFloat(displayName, currEntry == 0 ? "" : String.valueOf(currEntry));
   }

   public static String promptChangeFloat(String displayName, String currEntry) throws SQLException {
      return promptChangeString(displayName, currEntry, VALID_FLOAT_REG_EX);
   }

   public static String promptChangeString(String displayName, String currEntry, String validationRegEx) throws SQLException {
      return promptChangeString(displayName, currEntry, validationRegEx, Option.SINGLE_LINE);
   }

   public static String promptChangeString(String displayName, String currEntry, String validationRegEx, Option option) throws SQLException {
      EntryDialog ed =
            new EntryDialog(Display.getCurrent().getActiveShell(), "Enter " + displayName, null,
                  "Enter " + displayName, MessageDialog.QUESTION, new String[] {"OK", "Clear", "Cancel"}, 0);
      if (option == Option.MULTI_LINE) ed.setFillVertically(true);
      if (currEntry != null && !currEntry.equals("")) ed.setEntry(currEntry);
      if (validationRegEx != null) ed.setValidationRegularExpression(validationRegEx);
      int result = ed.open();
      if (result == 0)
         return ed.getEntry();
      else if (result == 1) return "";
      return null;
   }

   public static Boolean promptChangeBoolean(String displayName, String toggleMessage, boolean currSelection) throws SQLException {
      MessageDialogWithToggle md =
            new MessageDialogWithToggle(Display.getCurrent().getActiveShell(), displayName, null, displayName,
                  MessageDialog.QUESTION, new String[] {"Ok", "Cancel"}, MessageDialog.OK,
                  (toggleMessage != null ? toggleMessage : displayName), currSelection);
      int result = md.open();
      if (result == 256) {
         return md.getToggleState();
      }
      return null;
   }

}
