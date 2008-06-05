/*
 * Created on May 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.util.Artifacts;

/**
 * Definition of WorkItem. Once created, nothing in this class, or any subclasses, should be modified as these
 * definitions are shared by all instantiations of pages, rules, workflows and widgets.
 * 
 * @author Donald G. Dunne
 */
public abstract class WorkItemDefinition {

   protected final String id;
   protected final String name;
   protected final String parentId;
   protected Object data;
   public static enum WriteType {
      Update, New
   };

   public WorkItemDefinition(String name, String id, String parentId) {
      this.name = name;
      this.id = id;
      this.parentId = parentId;
      if (parentId != null && parentId.equals("")) throw new IllegalArgumentException(
            "parentId must either be null or a valid parent Id.  Invalid for WorkItemDefinition " + id);
      if (this.id == null || this.id.equals("")) throw new IllegalArgumentException("id must be unique and non-null");
   }

   /**
    * Determine if this workItemDefinition is or has a parent definition of pageId. This will walk up the tree of
    * definition inheritance to answer the question
    * 
    * @param pageId
    * @return
    */
   public boolean isInstanceOfPage(String pageId, String... visitedPageIds) throws Exception {
      // Collect all ids already visited
      Set<String> visitedIds = new HashSet<String>();
      for (String visitedId : visitedPageIds)
         visitedIds.add(visitedId);

      // Check for circluar dependency
      if (visitedIds.contains(getId())) throw new IllegalStateException(
            "Circular dependency detected.  Id already visited: " + getId());

      // Check for instanceof 
      if (getId().equals(pageId)) return true;

      // If parentId exists, check if it isInstanceOfPage
      if (getParentId() != null) {
         visitedIds.add(getId());
         WorkItemDefinition workItemDefinition = WorkItemDefinitionFactory.getWorkItemDefinition(getParentId());
         return workItemDefinition.isInstanceOfPage(pageId, visitedIds.toArray(new String[visitedIds.size()]));
      }
      return false;
   }

   public String toString() {
      return "Name: \"" + name + "\"    Id: \"" + id + "\"   " + (parentId != null ? "   Parent: " + parentId : "");
   }

   /**
    * @return the id
    */
   public String getId() {
      return id;
   }

   /**
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * @return the parentId
    */
   public String getParentId() {
      return parentId;
   }

   /**
    * @return the data
    */
   public Object getData() {
      return data;
   }

   /**
    * @param data the data to set
    */
   public void setData(Object data) {
      this.data = data;
   }

   public Artifact toArtifact(WriteType writeType) throws Exception {
      Artifact artifact = null;
      if (writeType == WriteType.Update) {
         artifact =
               Artifacts.getOrCreateArtifact(BranchPersistenceManager.getCommonBranch(), getArtifactTypeName(), getId());
      } else {
         // Ensure doesn't already exist
         if (ArtifactQuery.getArtifactsFromTypeAndName(getArtifactTypeName(), getId(),
               BranchPersistenceManager.getCommonBranch()).size() > 0) {
            throw new IllegalStateException(
                  "WorkItemDefinition artifact creation failed.  \"" + getId() + "\" already exists.");
         }
         // Create new
         artifact = ArtifactTypeManager.addArtifact(getArtifactTypeName(), BranchPersistenceManager.getCommonBranch());
      }
      artifact.setDescriptiveName(getId());
      if (getParentId() != null) artifact.setSoleAttributeValue(
            WorkItemAttributes.WORK_PARENT_ID.getAttributeTypeName(), getParentId());
      artifact.setSoleAttributeValue(WorkItemAttributes.WORK_NAME.getAttributeTypeName(), getName());
      return artifact;
   }

   public abstract String getArtifactTypeName();

}
