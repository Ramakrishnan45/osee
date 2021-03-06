/*
 * Created on Mar 25, 2014
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event.filter;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.model.event.IBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.artifact.IArtifactTypeProvider;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Donald G. Dunne
 */
public class ArtifactTypeEventFilterTest {

   // @formatter:off
   @Mock private IArtifactTypeProvider typeProvider;
   // @formatter:on

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);
   }

   @Test
   public void testArtifactEventFilters_artifactTypeAndInherited() throws Exception {
      when(typeProvider.getTypeByGuid(CoreArtifactTypes.Requirement.getGuid())).thenReturn(
         CoreArtifactTypes.Requirement);
      ArtifactTypeEventFilter typeFilter = new ArtifactTypeEventFilter(typeProvider, CoreArtifactTypes.Requirement);
      EventBasicGuidArtifact guidArt =
         new EventBasicGuidArtifact(EventModType.Added, COMMON, CoreArtifactTypes.Requirement);
      List<EventBasicGuidArtifact> guidArts = Arrays.asList(guidArt);
      Assert.assertTrue("Should match cause same artifact type", typeFilter.isMatchArtifacts(guidArts));

      // inherited type
      guidArt.setArtTypeGuid(CoreArtifactTypes.SoftwareRequirement);
      when(typeProvider.getTypeByGuid(CoreArtifactTypes.SoftwareRequirement.getGuid())).thenReturn(
         CoreArtifactTypes.SoftwareRequirement);
      when(typeProvider.inheritsFrom(CoreArtifactTypes.SoftwareRequirement, CoreArtifactTypes.Requirement)).thenReturn(
         true);

      Assert.assertTrue("Should match cause SoftwareRequirement is subclass of Requirement",
         typeFilter.isMatchArtifacts(guidArts));

      // not inherited type
      typeFilter = new ArtifactTypeEventFilter(typeProvider, CoreArtifactTypes.SoftwareRequirement);
      guidArt.setArtTypeGuid(CoreArtifactTypes.Requirement);

      Assert.assertFalse("Should NOT match cause Requirement is NOT subclass of Software Requirement",
         typeFilter.isMatchArtifacts(guidArts));
   }

   @Test
   public void testBranchMatch_relationType() throws Exception {
      ArtifactTypeEventFilter typeFilter = new ArtifactTypeEventFilter(typeProvider, CoreArtifactTypes.Requirement);

      when(typeProvider.getTypeByGuid(CoreArtifactTypes.Requirement.getGuid())).thenReturn(
         CoreArtifactTypes.Requirement);
      EventBasicGuidArtifact guidArtA =
         new EventBasicGuidArtifact(EventModType.Added, COMMON, CoreArtifactTypes.Requirement);
      EventBasicGuidArtifact guidArtB =
         new EventBasicGuidArtifact(EventModType.Added, COMMON, CoreArtifactTypes.SoftwareRequirement);

      List<IBasicGuidRelation> relations = new ArrayList<>();
      EventBasicGuidRelation relation = new EventBasicGuidRelation(RelationEventType.Added, BranchId.SENTINEL,
         CoreRelationTypes.SupportingInfo_SupportedBy.getGuid(), 234, 123, 55, guidArtA, 66, guidArtB);
      relations.add(relation);

      // guidArt in relation matches
      Assert.assertTrue(typeFilter.isMatchRelationArtifacts(relations));

      // no art in relation matches
      guidArtA.setArtTypeGuid(CoreArtifactTypes.AccessControlModel);
      guidArtB.setArtTypeGuid(CoreArtifactTypes.Folder);
      Assert.assertFalse(typeFilter.isMatchRelationArtifacts(relations));
   }

   @Test
   public void testBranchMatch_isMatch() throws Exception {
      ArtifactTypeEventFilter typeFilter = new ArtifactTypeEventFilter(typeProvider, CoreArtifactTypes.Requirement);
      Assert.assertTrue(typeFilter.isMatch(COMMON));
      Assert.assertTrue(typeFilter.isMatch(CoreBranches.SYSTEM_ROOT));
   }
}