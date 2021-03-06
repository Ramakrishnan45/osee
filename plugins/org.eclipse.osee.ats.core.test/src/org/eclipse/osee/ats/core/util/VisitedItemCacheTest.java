/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import java.util.Iterator;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Donald G. Dunne
 */
public class VisitedItemCacheTest {

   // @formatter:off
   @Mock private IAtsWorkItem workItem1, workItem2, workItem3;
   // @formatter:on

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);
      when(workItem1.getId()).thenReturn(12L);
      when(workItem2.getId()).thenReturn(13L);
      when(workItem3.getId()).thenReturn(14L);
   }

   @Test
   public void test() {
      VisitedItemCache cache = new VisitedItemCache();
      assertEquals(0, cache.visitedUuids.size());

      cache.addVisited(workItem1);
      assertEquals(1, cache.getReverseVisited().size());
      assertEquals(workItem1.getId(), cache.getReverseVisited().iterator().next().getId());

      cache.addVisited(workItem2);
      cache.addVisited(workItem3);
      assertEquals(3, cache.getReverseVisited().size());
      Iterator<IAtsWorkItem> iterator = cache.getReverseVisited().iterator();
      assertEquals(workItem3.getId(), iterator.next().getId());
      assertEquals(workItem2.getId(), iterator.next().getId());
      assertEquals(workItem1.getId(), iterator.next().getId());

      cache.clearVisited();
      assertEquals(0, cache.getReverseVisited().size());

   }

}
