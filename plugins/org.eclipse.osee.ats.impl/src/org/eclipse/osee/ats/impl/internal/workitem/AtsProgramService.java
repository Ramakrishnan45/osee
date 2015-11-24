/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.impl.internal.workitem;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.country.IAtsCountry;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.program.IAtsProgramService;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryBuilder;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * @author Donald G. Dunne
 */
public class AtsProgramService implements IAtsProgramService {

   private final IAtsServer atsServer;
   private final Cache<IAtsTeamDefinition, IAtsProgram> cache =
      CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build();

   public AtsProgramService(IAtsServer atsServer) {
      this.atsServer = atsServer;
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition(IAtsProgram atsProgram) {
      IAtsTeamDefinition teamDef = atsProgram.getTeamDefinition();
      if (teamDef == null) {
         ArtifactReadable artifact = (ArtifactReadable) atsProgram.getStoreObject();
         String teamDefGuid = artifact.getSoleAttributeValue(AtsAttributeTypes.TeamDefinition, null);
         if (Strings.isValid(teamDefGuid)) {
            teamDef = (IAtsTeamDefinition) atsServer.getArtifactByGuid(teamDefGuid);
         }
      }
      return teamDef;
   }

   @Override
   public IAtsProgram getProgram(IAtsWorkItem wi) {
      IAtsTeamDefinition teamDefinition = wi.getParentTeamWorkflow().getTeamDefinition();
      IAtsProgram program = cache.getIfPresent(teamDefinition);
      if (program == null) {
         IAtsTeamDefinition topTeamDef = teamDefinition.getTeamDefinitionHoldingVersions();
         QueryBuilder query = atsServer.getQuery();
         query.and(AtsAttributeTypes.TeamDefinition, AtsUtilCore.getGuid(topTeamDef)).andIsOfType(
            AtsArtifactTypes.Program);
         ArtifactReadable programArt = query.getResults().getOneOrNull();
         program = atsServer.getConfigItemFactory().getProgram(programArt);
         cache.put(teamDefinition, program);
      }
      return program;
   }

   @Override
   public IAtsProgram getProgramByGuid(String guid) {
      ArtifactReadable prgArt = (ArtifactReadable) atsServer.getArtifactById(guid);
      return atsServer.getConfigItemFactory().getProgram(prgArt);
   }

   @Override
   public IAtsCountry getCountry(IAtsProgram atsProgram) {
      IAtsCountry country = null;
      ArtifactReadable artifact = ((ArtifactReadable) atsProgram.getStoreObject());
      if (artifact != null) {
         ArtifactReadable countryArt =
            artifact.getRelated(AtsRelationTypes.CountryToProgram_Country).getAtMostOneOrNull();
         if (countryArt != null) {
            country = atsServer.getConfigItemFactory().getCountry(countryArt);
         }
      }
      return country;
   }

   @Override
   public List<IAtsProgram> getPrograms(IAtsCountry atsCountry) {
      List<IAtsProgram> programs = new LinkedList<>();
      ArtifactReadable artifact = ((ArtifactReadable) atsCountry.getStoreObject());
      if (artifact != null) {
         for (ArtifactReadable related : artifact.getRelated(AtsRelationTypes.CountryToProgram_Program)) {
            programs.add(atsServer.getConfigItemFactory().getProgram(related));
         }
      }
      return programs;
   }

}