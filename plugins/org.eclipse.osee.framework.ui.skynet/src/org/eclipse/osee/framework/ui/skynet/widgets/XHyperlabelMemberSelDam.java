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
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

public class XHyperlabelMemberSelDam extends XHyperlabelMemberSelection implements IAttributeWidget {

   private Artifact artifact;
   private String attributeTypeName;

   public XHyperlabelMemberSelDam(String displayLabel) {
      super(displayLabel);
   }

   @Override
   public String getAttributeType() {
      return attributeTypeName;
   }

   @Override
   public void setAttributeType(Artifact artifact, String attrName) {
      this.artifact = artifact;
      this.attributeTypeName = attrName;

      super.setSelectedUsers(getUsers());
   }

   public Set<User> getUsers() {
      Set<User> users = new HashSet<User>();
      try {
         Matcher m =
            Pattern.compile("<userId>(.*?)</userId>").matcher(artifact.getSoleAttributeValue(attributeTypeName, ""));
         while (m.find()) {
            users.add(UserManager.getUserByUserId(m.group(1)));
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }

      return users;
   }

   @Override
   public void saveToArtifact() {
      try {
         String selectedStrValue = getSelectedStringValue();
         if (selectedStrValue == null || selectedStrValue.equals("")) {
            artifact.deleteSoleAttribute(attributeTypeName);
         } else {
            artifact.setSoleAttributeValue(attributeTypeName, selectedStrValue);
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public String getSelectedStringValue() throws OseeCoreException {
      StringBuffer sb = new StringBuffer();
      for (User user : getSelectedUsers()) {
         sb.append(AXml.addTagData("userId", user.getUserId()));
      }
      return sb.toString();
   }

   @Override
   public Result isDirty() throws OseeCoreException {
      try {
         String enteredValue = getSelectedStringValue();
         String storedValue = artifact.getSoleAttributeValue(attributeTypeName);
         if (!enteredValue.equals(storedValue)) {
            return new Result(true, attributeTypeName + " is dirty");
         }
      } catch (AttributeDoesNotExist ex) {
         if (!artifact.getSoleAttributeValue(attributeTypeName, "").equals("")) {
            return new Result(true, attributeTypeName + " is dirty");
         }
      } catch (NumberFormatException ex) {
         // do nothing
      }
      return Result.FalseResult;
   }

   @Override
   public void revert() throws OseeCoreException {
      setAttributeType(artifact, attributeTypeName);
   }
}
