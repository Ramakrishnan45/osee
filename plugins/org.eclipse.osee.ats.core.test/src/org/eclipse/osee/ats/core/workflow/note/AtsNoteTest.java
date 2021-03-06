/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workflow.note;

import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.workflow.note.IAtsWorkItemNotes;
import org.eclipse.osee.ats.api.workflow.note.NoteItem;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Donald G. Dunne
 */
public class AtsNoteTest {

   // @formatter:off
   @Mock private IAtsUserService userService;
   @Mock private IAtsServices services;
   @Mock private IAtsUser Joe;
   // @formatter:on
   List<IAtsUser> assignees = new ArrayList<>();

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);

      when(services.getUserService()).thenReturn(userService);
      when(userService.getUserById("333")).thenReturn(Joe);
      when(Joe.getUserId()).thenReturn("333");
   }

   @Test
   public void testToAndFromStore() throws OseeCoreException {
      Date date = new Date();
      SimpleNoteStore store = new SimpleNoteStore();
      IAtsWorkItemNotes log = new AtsWorkItemNotes(store, services);
      NoteItem item = NoteItemTest.getTestNoteItem(date, Joe);
      log.addNoteItem(item);

      IAtsWorkItemNotes log2 = new AtsWorkItemNotes(store, services);
      Assert.assertEquals(1, log2.getNoteItems().size());
      NoteItem loadItem = log2.getNoteItems().iterator().next();
      NoteItemTest.validate(loadItem, date, Joe);
   }

   public class SimpleNoteStore implements INoteStorageProvider {

      String store = "";

      @Override
      public String getNoteXml() {
         return store;
      }

      @Override
      public Result saveNoteXml(String xml) {
         store = xml;
         return Result.TrueResult;
      }

      @Override
      public String getNoteTitle() {
         return "This is the title";
      }

      @Override
      public String getNoteId() {
         return GUID.create();
      }

      @Override
      public boolean isNoteable() {
         return false;
      }

   }

}
