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
package org.eclipse.osee.disposition.rest.internal.importer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.disposition.model.Discrepancy;
import org.eclipse.osee.disposition.model.DispoAnnotationData;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoItemData;
import org.eclipse.osee.disposition.model.DispoStrings;
import org.eclipse.osee.disposition.rest.internal.DispoConnector;
import org.eclipse.osee.disposition.rest.internal.report.OperationReport;
import org.eclipse.osee.disposition.rest.util.DispoUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Angel Avila
 */
public class AnnotationCopier {

   private final DispoConnector connector;

   public AnnotationCopier(DispoConnector connector) {
      this.connector = connector;
   }

   public List<DispoItem> copyEntireSet(List<DispoItemData> destinationItems, Collection<DispoItem> sourceItems, boolean isCopySet, OperationReport report) {
      List<DispoItem> modifiedItems = new ArrayList<DispoItem>();

      HashMap<String, DispoItemData> nameToDestItems = createNameToItemList(destinationItems);
      for (DispoItem sourceItem : sourceItems) {
         DispoItemData destItem = nameToDestItems.get(sourceItem.getName());

         if (destItem != null) {
            List<DispoAnnotationData> annotationsList = destItem.getAnnotationsList();
            /**
             * If item is PASS don't bother copying over Annotations from Source Item, all annotations are Default
             * Annotations and already created in the Import
             */
            if (!destItem.getStatus().equals(DispoStrings.Item_Pass)) {
               DispoItemData newItem = createNewItemWithCopiedAnnotations(destItem, sourceItem, isCopySet, report);
               if (newItem != null) {
                  if (!Strings.isValid(destItem.getGuid())) {
                     newItem.setGuid(sourceItem.getGuid());
                  } else {
                     newItem.setGuid(destItem.getGuid());
                  }
                  modifiedItems.add(newItem);

                  report.addMessageForItem(destItem.getName(), "$$$$Had %s Dispositions$$$$\n", annotationsList.size());
                  report.addMessageForItem(destItem.getName(), "$$$$Now has %s Dispositions$$$$",
                     newItem.getAnnotationsList().size());
               }
            } else if (!Strings.isValid(destItem.getGuid()) && !sourceItem.getStatus().equals(DispoStrings.Item_Pass)) {
               destItem.setGuid(sourceItem.getGuid());
               modifiedItems.add(destItem);
            }

         }
      }
      return modifiedItems;
   }

   private DispoItemData createNewItemWithCopiedAnnotations(DispoItemData destItem, DispoItem sourceItem, boolean isCopySet, OperationReport report) {
      DispoItemData toReturn;
      boolean isSameDiscrepancies = matchAllDiscrepancies(destItem, sourceItem);
      if (isSameDiscrepancies) {
         toReturn = buildNewItem(destItem, sourceItem, isCopySet, report);
      } else {
         toReturn = null;
         report.addMessageForItem(destItem.getName(),
            "Tried to copy from item id: [%s] but discrepancies were not the same", sourceItem.getGuid());
      }

      return toReturn;
   }

   private DispoItemData buildNewItem(DispoItemData destItem, DispoItem sourceItem, boolean isSkipDestDefaultAnnotations, OperationReport report) {
      boolean isChangesMade = false;
      DispoItemData newItem = new DispoItemData();
      newItem.setDiscrepanciesList(destItem.getDiscrepanciesList());
      List<DispoAnnotationData> newList = new ArrayList<DispoAnnotationData>();
      newList.addAll(destItem.getAnnotationsList());
      newItem.setAnnotationsList(newList);

      List<DispoAnnotationData> newAnnotations = newItem.getAnnotationsList();
      List<DispoAnnotationData> sourceAnnotations = sourceItem.getAnnotationsList();

      Set<String> destDefaultAnntationLocations = getDefaultAnnotations(newItem);

      for (DispoAnnotationData sourceAnnotation : sourceAnnotations) {
         String sourceLocation = sourceAnnotation.getLocationRefs();

         if (DispoUtil.isDefaultAnntoation(sourceAnnotation)) {
            /**
             * This means the source has an annotation that's TEST_UNIT or Exception_Handling, so don't copy it over, we
             * might leave an uncovered discrepancy which is intended and need to log
             */
            if (!destDefaultAnntationLocations.contains(sourceLocation)) {
               report.addMessageForItem(destItem.getName(),
                  "Did not copy annotations for location(s) [%s] because they are default annotations",
                  sourceAnnotation.getLocationRefs());
            }
         } else if (isSkipDestDefaultAnnotations && destDefaultAnntationLocations.contains(sourceLocation)) {
            /**
             * isSkipDestDefault is true when annotation copier is called by a copy set, this means we do not want to
             * copy over source annotations that have the same location as a Dest annotation that's already covered by a
             * Default Annotation This means the destination has an annotation that's TEST_UNIT or Exception_Handling,
             * so don't copy over a manual Disposition
             */
            report.addMessageForItem(
               destItem.getName(),
               "Did not copy annotations for location(s) [%s] because the destination item already has a default annotations at these locations",
               sourceAnnotation.getLocationRefs());
         } else if (newAnnotations.toString().contains(sourceAnnotation.getGuid())) {
            report.addMessageForItem(
               destItem.getName(),
               "Did not copy annotations for location(s) [%s] because the destination item already has the same annotations at these locations [%s]",
               sourceAnnotation.getLocationRefs());
         } else {
            DispoAnnotationData newAnnotation = sourceAnnotation;

            if (destDefaultAnntationLocations.contains(sourceLocation)) {
               /**
                * The discrepancy of this manual disposition is now covered by a Default Annotation so this Manual
                * Annotation is invalid, mark as such by making the location Ref negative, don't bother connecting the
                * annotation
                */
               // Make location ref negative to indicate this 
               String locationRefs = sourceAnnotation.getLocationRefs();
               Integer locationRefAsInt = Integer.valueOf(locationRefs);
               if (locationRefAsInt > 0) {
                  newAnnotation.setLocationRefs(String.valueOf(locationRefAsInt * -1));
               }
               report.addMessageForItem(destItem.getName(),
                  "The annotation was copied over but is no longer needed: [%s]", locationRefs);
            }
            connector.connectAnnotation(newAnnotation, newItem.getDiscrepanciesList());
            isChangesMade = true;
            // Both the source and destination are dispositionable so copy the annotation
            int nextIndex = newAnnotations.size();
            newAnnotation.setIndex(nextIndex);
            newAnnotations.add(nextIndex, newAnnotation);
         }
      }

      if (isChangesMade) {
         newItem.setAnnotationsList(newAnnotations);
         String newStatus = connector.getItemStatus(newItem);
         newItem.setStatus(newStatus);
      } else {
         newItem = null;
      }
      return newItem;
   }

   private Set<String> getDefaultAnnotations(DispoItemData item) {
      Set<String> defaultAnnotationLocations = new HashSet<String>();
      List<DispoAnnotationData> annotations = item.getAnnotationsList();
      if (annotations == null) {
         annotations = new ArrayList<DispoAnnotationData>();
      }
      for (DispoAnnotationData annotation : annotations) {
         if (DispoUtil.isDefaultAnntoation(annotation)) {
            defaultAnnotationLocations.add(annotation.getLocationRefs());
         }
      }

      return defaultAnnotationLocations;
   }

   private HashMap<String, DispoItemData> createNameToItemList(List<DispoItemData> destinationItems) {
      HashMap<String, DispoItemData> nameToItem = new HashMap<String, DispoItemData>();
      for (DispoItemData item : destinationItems) {
         nameToItem.put(item.getName(), item);
      }
      return nameToItem;
   }

   private boolean matchAllDiscrepancies(DispoItemData destItem, DispoItem sourceItem) {
      Map<Integer, String> destLocationToText = generateLocationToTextMap(destItem);
      boolean toReturn = true;

      Map<String, Discrepancy> sourceDiscrepancies = sourceItem.getDiscrepanciesList();
      for (String key : sourceDiscrepancies.keySet()) {
         Discrepancy sourceDiscrepancy = sourceDiscrepancies.get(key);

         int sourceLocation = sourceDiscrepancy.getLocation();
         String destDicrepancyText = destLocationToText.get(sourceLocation);
         if (destDicrepancyText == null) {
            // No Discrepancy with that location in the destination item, return false
            toReturn = false;
            break;
         } else if (sourceDiscrepancy.getText().equals(destDicrepancyText)) {
            continue;
         } else {
            toReturn = false;
            break;
         }

      }
      return toReturn;
   }

   private Map<Integer, String> generateLocationToTextMap(DispoItem item) {
      Map<Integer, String> locationToText = new HashMap<Integer, String>();
      Map<String, Discrepancy> discrepancies = item.getDiscrepanciesList();
      for (String key : discrepancies.keySet()) {
         Discrepancy discrepancy = discrepancies.get(key);
         locationToText.put(discrepancy.getLocation(), discrepancy.getText());
      }

      return locationToText;
   }
}
