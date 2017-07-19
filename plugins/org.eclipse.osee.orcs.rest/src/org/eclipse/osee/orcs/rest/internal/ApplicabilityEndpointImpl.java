/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchViewData;
import org.eclipse.osee.framework.core.data.FeatureDefinitionData;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreTupleTypes;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;
import org.eclipse.osee.orcs.search.ApplicabilityQuery;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Donald G. Dunne
 */
public class ApplicabilityEndpointImpl implements ApplicabilityEndpoint {

   private final OrcsApi orcsApi;
   private final BranchId branch;
   private final ApplicabilityQuery applicabilityQuery;
   private final UserId account;

   public ApplicabilityEndpointImpl(OrcsApi orcsApi, BranchId branch, UserId account) {
      this.orcsApi = orcsApi;
      this.branch = branch;
      this.applicabilityQuery = orcsApi.getQueryFactory().applicabilityQuery();
      this.account = account;
   }

   @Override
   public Collection<ApplicabilityToken> getApplicabilityTokens() {
      return applicabilityQuery.getApplicabilityTokens(branch).values();
   }

   @Override
   public List<Pair<ArtifactId, ApplicabilityToken>> getApplicabilityTokens(Collection<? extends ArtifactId> artIds) {
      return applicabilityQuery.getApplicabilityTokens(artIds, branch);
   }

   @Override
   public ApplicabilityToken getApplicabilityToken(ArtifactId artId) {
      return applicabilityQuery.getApplicabilityToken(artId, branch);
   }

   @Override
   public List<ApplicabilityId> getApplicabilitiesReferenced(ArtifactId artifact) {
      return applicabilityQuery.getApplicabilitiesReferenced(artifact, branch);
   }

   @Override
   public List<ApplicabilityToken> getApplicabilityReferenceTokens(ArtifactId artifact) {
      return applicabilityQuery.getApplicabilityReferenceTokens(artifact, branch);
   }

   @Override
   public List<ApplicabilityToken> getViewApplicabilityTokens(ArtifactId view) {
      return applicabilityQuery.getViewApplicabilityTokens(view, branch);
   }

   @Override
   public List<BranchViewData> getViews() {
      return applicabilityQuery.getViews();
   }

   @Override
   public List<FeatureDefinitionData> getFeatureDefinitionData() {
      return applicabilityQuery.getFeatureDefinitionData(branch);
   }

   @Override
   public List<BranchId> getAffectedBranches(Long injectDateMs, Long removalDateMs, List<ApplicabilityId> applicabilityIds) {
      return applicabilityQuery.getAffectedBranches(injectDateMs, removalDateMs, applicabilityIds, branch);
   }

   @Override
   public List<BranchId> getAffectedBranches(TransactionId injectionTx, TransactionId removalTx, List<ApplicabilityId> applicabilityIds) {
      return applicabilityQuery.getAffectedBranches(injectionTx, removalTx, applicabilityIds, branch);
   }

   @Override
   public TransactionToken setApplicability(ApplicabilityId applicId, List<? extends ArtifactId> artifacts) {
      TransactionBuilder tx =
         orcsApi.getTransactionFactory().createTransaction(branch, account, "Set Applicability Ids for Artifacts");
      tx.setApplicability(applicId, artifacts);
      return tx.commit();
   }

   /**
    * TBD: Need to delete tuples that are not in the set. Update this when tx.removeTuple2 is implemented.
    */
   @Override
   public TransactionToken setApplicabilityReference(HashMap<ArtifactId, List<ApplicabilityId>> artToApplMap) {
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, account,
         "Set Reference Applicability Ids for Artifacts");
      tx.setApplicabilityReference(artToApplMap);
      return tx.commit();
   }

   @Override
   public TransactionToken createView(String viewName) {
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, account, "Create Branch View");
      ArtifactId viewArtifact = tx.createView(branch, viewName);
      tx.commit();

      TransactionBuilder tx2 =
         orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, account, "Create Branch View");

      tx2.addTuple2(CoreTupleTypes.BranchView, CoreBranches.COMMON.getId(), viewArtifact.getId());

      return tx2.commit();
   }

   @Override
   public TransactionToken createApplicabilityForView(ArtifactId viewId, String applicability) {
      TransactionBuilder tx =
         orcsApi.getTransactionFactory().createTransaction(branch, account, "Create new applicability");
      tx.createApplicabilityForView(viewId, applicability);
      return tx.commit();
   }

   @Override
   public void createDemoApplicability() {
      TransactionBuilder tx =
         orcsApi.getTransactionFactory().createTransaction(branch, account, "Create Demo Applicability");
      tx.createDemoApplicability();
      tx.commit();

      // set the view on common branch
      List<ArtifactReadable> branchViewArtifacts = orcsApi.getQueryFactory().fromBranch(branch).andTypeEquals(
         CoreArtifactTypes.BranchView).getResults().getList();

      TransactionBuilder tx2 =
         orcsApi.getTransactionFactory().createTransaction(CoreBranches.COMMON, account, "Create Branch View");

      for (ArtifactReadable artifact : branchViewArtifacts) {
         tx2.addTuple2(CoreTupleTypes.BranchView, branch.getId(), artifact.getId());
      }

      tx2.commit();

   }

   @Override
   public void addMissingApplicabilityFromParentBranch() {
      orcsApi.getBranchOps().addMissingApplicabilityFromParentBranch(branch);
   }

   @Override
   public ArtifactId getVersionConfig(ArtifactId version) {
      return applicabilityQuery.getVersionConfig(version, branch);
   }

   @Override
   public String getViewTable() {
      return applicabilityQuery.getViewTable(branch);
   }

}