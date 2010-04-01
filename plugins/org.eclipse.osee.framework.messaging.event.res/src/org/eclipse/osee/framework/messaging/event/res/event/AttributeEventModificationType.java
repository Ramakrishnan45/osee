/*
 * Created on Mar 31, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.event.res.event;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.enums.ModificationType;

/**
 * This class maps attribute modification types to event guids.<br>
 * <br>
 * TODO Should probably replace Modification Type or integrate guids in, but can't do cause ModificationType is
 * serializeable and can't change till 0.9.5
 * 
 * @author Donald G. Dunne
 */
public class AttributeEventModificationType {

   private final ModificationType modificationType;
   private final String guid;
   private static Map<ModificationType, AttributeEventModificationType> modTypeToEventType =
         new HashMap<ModificationType, AttributeEventModificationType>(15);
   private static Map<String, AttributeEventModificationType> guidToEventType =
         new HashMap<String, AttributeEventModificationType>(15);
   public static AttributeEventModificationType Modified =
         new AttributeEventModificationType(ModificationType.MODIFIED, "AYsmVz6VujxZxW3ByjgA");
   public static AttributeEventModificationType Artifact_Deleted =
         new AttributeEventModificationType(ModificationType.ARTIFACT_DELETED, "AYsmWPvkJyoo4ynjAbgA");
   public static AttributeEventModificationType Deleted =
         new AttributeEventModificationType(ModificationType.DELETED, "AYsmWPxw7mAFrGVGf2AA");
   public static AttributeEventModificationType Introduced =
         new AttributeEventModificationType(ModificationType.INTRODUCED, "AYsmWPzUPCGfOdH5w3wA");
   public static AttributeEventModificationType Merged =
         new AttributeEventModificationType(ModificationType.MERGED, "AYsmWP0Gb1y5V6G9tRwA");
   public static AttributeEventModificationType New =
         new AttributeEventModificationType(ModificationType.NEW, "AYsmWP05uX1Dl6q2pIwA");
   public static AttributeEventModificationType Undeleted =
         new AttributeEventModificationType(ModificationType.UNDELETED, "AYsmWP1q1B2bK1kj0ugA");

   public AttributeEventModificationType(ModificationType modificationType, String guid) {
      this.modificationType = modificationType;
      this.guid = guid;
      modTypeToEventType.put(this.modificationType, this);
      guidToEventType.put(guid, this);
   }

   public static Collection<AttributeEventModificationType> getTypes() {
      return modTypeToEventType.values();
   }

   public static AttributeEventModificationType getType(ModificationType modificationType) {
      return modTypeToEventType.get(modificationType);
   }

   public static AttributeEventModificationType getType(String guid) {
      return guidToEventType.get(guid);
   }

   public ModificationType getModificationType() {
      return modificationType;
   }

   public String getGuid() {
      return guid;
   }
}
