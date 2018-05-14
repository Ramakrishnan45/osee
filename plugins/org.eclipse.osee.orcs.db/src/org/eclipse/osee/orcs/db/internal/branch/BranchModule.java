/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.branch;

import java.net.URI;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.executor.ExecutorAdmin;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.SystemPreferences;
import org.eclipse.osee.orcs.core.ds.BranchDataStore;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.data.ArchiveOperation;
import org.eclipse.osee.orcs.data.CreateBranchData;
import org.eclipse.osee.orcs.db.internal.IdentityManager;
import org.eclipse.osee.orcs.db.internal.callable.AbstractDatastoreTxCallable;
import org.eclipse.osee.orcs.db.internal.callable.ArchiveUnarchiveBranchCallable;
import org.eclipse.osee.orcs.db.internal.callable.BranchCopyTxCallable;
import org.eclipse.osee.orcs.db.internal.callable.ChangeBranchFieldCallable;
import org.eclipse.osee.orcs.db.internal.callable.CheckBranchExchangeIntegrityCallable;
import org.eclipse.osee.orcs.db.internal.callable.CommitBranchDatabaseCallable;
import org.eclipse.osee.orcs.db.internal.callable.CompareDatabaseCallable;
import org.eclipse.osee.orcs.db.internal.callable.CompositeDatastoreTxCallable;
import org.eclipse.osee.orcs.db.internal.callable.CreateBranchDatabaseTxCallable;
import org.eclipse.osee.orcs.db.internal.callable.ExportBranchDatabaseCallable;
import org.eclipse.osee.orcs.db.internal.callable.ImportBranchDatabaseCallable;
import org.eclipse.osee.orcs.db.internal.callable.PurgeBranchDatabaseCallable;
import org.eclipse.osee.orcs.db.internal.change.MissingChangeItemFactory;
import org.eclipse.osee.orcs.db.internal.change.MissingChangeItemFactoryImpl;
import org.eclipse.osee.orcs.db.internal.exchange.ExportItemFactory;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;
import org.eclipse.osee.orcs.search.ApplicabilityQuery;

/**
 * @author Roberto E. Escobar
 */
public class BranchModule {
   private final String INSERT_INTO_BRANCH_ACL =
      "INSERT INTO OSEE_BRANCH_ACL (permission_id, privilege_entity_id, branch_id) VALUES (?, ?, ?)";
   private final String UPDATE_BRANCH_ACL =
      "UPDATE OSEE_BRANCH_ACL SET permission_id = ? WHERE privilege_entity_id = ? AND branch_id = ?";
   private final String GET_BRANCH_PERMISSION =
      "SELECT permission_id FROM osee_branch_acl WHERE privilege_entity_id = ? AND branch_id = ?";

   private static final String COPY_APPLIC =
      "INSERT INTO osee_txs (branch_id, gamma_id, transaction_id, tx_current, mod_type, app_id)\n" + "with cte as (select branch_id as chid, baseline_transaction_id as chtx, parent_branch_id as pid from osee_branch where branch_id = ?)\n" + "select chid, txsP.gamma_id, chtx, tx_current, mod_type, app_id from cte, osee_tuple2 t2, osee_txs txsP where tuple_type = 2 and t2.gamma_id = txsP.gamma_id and txsP.branch_id = pid and txsP.tx_current =1 and not exists (select 1 from osee_txs txsC where txsC.branch_id = chid and txsC.gamma_id = txsP.gamma_id)";

   private final Log logger;
   private final JdbcClient jdbcClient;
   private final SqlJoinFactory joinFactory;
   private final IdentityManager idManager;
   private final SystemPreferences preferences;
   private final ExecutorAdmin executorAdmin;
   private final IResourceManager resourceManager;

   public BranchModule(Log logger, JdbcClient jdbcClient, SqlJoinFactory joinFactory, IdentityManager idManager, SystemPreferences preferences, ExecutorAdmin executorAdmin, IResourceManager resourceManager) {
      super();
      this.logger = logger;
      this.jdbcClient = jdbcClient;
      this.joinFactory = joinFactory;
      this.idManager = idManager;
      this.preferences = preferences;
      this.executorAdmin = executorAdmin;
      this.resourceManager = resourceManager;
   }

   public BranchDataStore createBranchDataStore(final DataLoaderFactory dataLoaderFactory) {
      final MissingChangeItemFactory missingChangeItemFactory = new MissingChangeItemFactoryImpl(dataLoaderFactory);
      return new BranchDataStore() {
         @Override
         public void addMissingApplicabilityFromParentBranch(BranchId branch) {
            jdbcClient.runPreparedUpdate(COPY_APPLIC, branch);
         }

         @Override
         public void createBranch(CreateBranchData branchData) {
            jdbcClient.runTransaction(
               new CreateBranchDatabaseTxCallable(jdbcClient, idManager, branchData, OseeCodeVersion.getVersionId()));
         }

         @Override
         public void createBranchCopyTx(CreateBranchData branchData) {
            jdbcClient.runTransaction(new BranchCopyTxCallable(jdbcClient, idManager, branchData,
               OseeCodeVersion.getVersionId()));
         }

         @Override
         public Callable<TransactionId> commitBranch(OrcsSession session, ArtifactId committer, Branch source, TransactionToken sourceHead, Branch destination, TransactionToken destinationHead, ApplicabilityQuery applicQuery) {
            return new CommitBranchDatabaseCallable(logger, session, jdbcClient, joinFactory, idManager, committer,
               source, sourceHead, destination, destinationHead, missingChangeItemFactory, applicQuery);
         }

         @Override
         public Callable<Void> purgeBranch(OrcsSession session, Branch toDelete) {
            return new PurgeBranchDatabaseCallable(logger, session, jdbcClient, toDelete);
         }

         @Override
         public Callable<List<ChangeItem>> compareBranch(OrcsSession session, TransactionToken sourceTx, TransactionToken destinationTx, ApplicabilityQuery applicQuery) {
            return new CompareDatabaseCallable(logger, session, jdbcClient, joinFactory, sourceTx, destinationTx,
               missingChangeItemFactory, applicQuery);
         }

         @Override
         public Callable<URI> exportBranch(OrcsSession session, OrcsTypes orcsTypes, List<? extends BranchId> branches, PropertyStore options, String exportName) {
            ExportItemFactory factory =
               new ExportItemFactory(logger, preferences, jdbcClient, resourceManager, orcsTypes);
            return new ExportBranchDatabaseCallable(session, factory, joinFactory, preferences, executorAdmin, branches,
               options, exportName);
         }

         @Override
         public Callable<URI> importBranch(OrcsSession session, OrcsTypes orcsTypes, URI fileToImport, List<? extends BranchId> branches, PropertyStore options) {
            ImportBranchDatabaseCallable callable = new ImportBranchDatabaseCallable(logger, session, jdbcClient,
               preferences, resourceManager, idManager, orcsTypes, fileToImport, branches, options);
            return callable;
         }

         @Override
         public Callable<URI> checkBranchExchangeIntegrity(OrcsSession session, URI fileToCheck) {
            return new CheckBranchExchangeIntegrityCallable(logger, session, jdbcClient, preferences, resourceManager,
               fileToCheck);
         }

         @Override
         public Callable<Void> changeBranchState(OrcsSession session, BranchId branch, BranchState branchState) {
            return ChangeBranchFieldCallable.newBranchState(logger, session, jdbcClient, branch, branchState);
         }

         @Override
         public Callable<Void> changeBranchType(OrcsSession session, BranchId branch, BranchType branchType) {
            return ChangeBranchFieldCallable.newBranchType(logger, session, jdbcClient, branch, branchType);
         }

         @Override
         public Callable<Void> changeBranchName(OrcsSession session, BranchId branch, String branchName) {
            return ChangeBranchFieldCallable.newBranchName(logger, session, jdbcClient, branch, branchName);
         }

         @Override
         public Callable<Void> changeBranchAssociatedArt(OrcsSession session, BranchId branch, ArtifactId assocArt) {
            return ChangeBranchFieldCallable.newAssocArtId(logger, session, jdbcClient, branch, assocArt);
         }

         @Override
         public Callable<Void> archiveUnArchiveBranch(OrcsSession session, BranchId branch, ArchiveOperation op) {
            return new ArchiveUnarchiveBranchCallable(logger, session, jdbcClient, branch, op);
         }

         @Override
         public Callable<Void> deleteBranch(OrcsSession session, BranchId branch) {
            AbstractDatastoreTxCallable<?> deleteBranch =
               (AbstractDatastoreTxCallable<?>) changeBranchState(session, branch, BranchState.DELETED);
            AbstractDatastoreTxCallable<?> archiveBranch =
               (AbstractDatastoreTxCallable<?>) archiveUnArchiveBranch(session, branch, ArchiveOperation.ARCHIVE);
            CompositeDatastoreTxCallable composite =
               new CompositeDatastoreTxCallable(logger, session, jdbcClient, deleteBranch, archiveBranch);
            return composite;
         }

         @Override
         public void setBranchPermission(ArtifactId subject, BranchId branch, PermissionEnum permission) {
            int existingPermission = jdbcClient.fetch(-1, GET_BRANCH_PERMISSION, subject, branch);
            String sql = existingPermission == -1 ? INSERT_INTO_BRANCH_ACL : UPDATE_BRANCH_ACL;
            jdbcClient.runPreparedUpdate(sql, permission.getPermId(), subject, branch);
         }

      };
   }
}
