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
package org.eclipse.osee.framework.skynet.core.test.event;

import java.util.HashSet;
import java.util.Set;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event2.artifact.EventChangeTypeBasicGuidArtifact;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class EventChangeTypeBasicGuidArtifactTest {

   @Test
   public void testEquals() throws OseeCoreException {
      EventChangeTypeBasicGuidArtifact eventArt1 =
         new EventChangeTypeBasicGuidArtifact(GUID.create(), GUID.create(), GUID.create(), GUID.create());
      EventChangeTypeBasicGuidArtifact eventArt2 =
         new EventChangeTypeBasicGuidArtifact(eventArt1.getBranchGuid(), eventArt1.getFromArtTypeGuid(),
            eventArt1.getArtTypeGuid(), eventArt1.getGuid());

      Assert.assertEquals(eventArt1.hashCode(), eventArt2.hashCode());
      Assert.assertEquals(eventArt1, eventArt2);

      eventArt2 =
         new EventChangeTypeBasicGuidArtifact(eventArt1.getBranchGuid(), eventArt1.getFromArtTypeGuid(),
            eventArt1.getArtTypeGuid(), eventArt1.getGuid());

      Assert.assertNotSame(eventArt1, eventArt2);

      eventArt2 =
         new EventChangeTypeBasicGuidArtifact(GUID.create(), eventArt1.getFromArtTypeGuid(),
            eventArt1.getArtTypeGuid(), eventArt1.getGuid());

      Assert.assertNotSame(eventArt1, eventArt2);

      eventArt2 =
         new EventChangeTypeBasicGuidArtifact(eventArt1.getBranchGuid(), GUID.create(), eventArt1.getArtTypeGuid(),
            eventArt1.getGuid());

      Assert.assertNotSame(eventArt1, eventArt2);

      eventArt2 =
         new EventChangeTypeBasicGuidArtifact(eventArt1.getBranchGuid(), eventArt1.getFromArtTypeGuid(),
            eventArt1.getArtTypeGuid(), GUID.create());

      Assert.assertNotSame(eventArt1, eventArt2);

      Set<EventBasicGuidArtifact> toAdd = new HashSet<EventBasicGuidArtifact>();
      toAdd.add(eventArt2);
      toAdd.add(eventArt1);
      Assert.assertEquals(2, toAdd.size());

      toAdd.add(eventArt1);
      Assert.assertEquals(2, toAdd.size());

      Set<EventBasicGuidArtifact> eventArts = new HashSet<EventBasicGuidArtifact>();
      eventArts.add(eventArt2);
      eventArts.addAll(toAdd);
      Assert.assertEquals(2, toAdd.size());

   }
}
