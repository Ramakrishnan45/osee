/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.skynet.core.utility.JsonArtifactRepresentation;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link JsonArtifactRepresentation}.
 *
 * @author David W. Miller
 */
public class JsonArtifactTransferTest {
   public static class JsonArtRep {
      ArtifactTypeId artType;
      List<JsonAttrRep> attrs;

      public JsonArtRep() {

      }

      public ArtifactTypeId getArtType() {
         return artType;
      }

      public void setArtType(ArtifactTypeId artType) {
         this.artType = artType;
      }

      public List<JsonAttrRep> getAttrs() {
         return attrs;
      }

      public void setAttrs(List<JsonAttrRep> attrs) {
         this.attrs = attrs;
      }
   }
   public static class JsonAttrRep {
      AttributeTypeId type;
      String value;

      public JsonAttrRep() {

      }

      public AttributeTypeId getType() {
         return type;
      }

      public void setType(AttributeTypeId type) {
         this.type = type;
      }

      public String getValue() {
         return value;
      }

      public void setValue(String value) {
         this.value = value;
      }
   }

   @Test
   public void testConstruction() throws IOException {
      String output = generateJsonOutput();
      parseJsonOutput(output);
   }

   private String generateJsonOutput() {
      String output = null;
      List<JsonArtRep> outputItems = new ArrayList<JsonArtRep>();
      for (int i = 0; i < 5; ++i) {
         JsonArtRep rep = new JsonArtRep();
         rep.setArtType(CoreArtifactTypes.SoftwareDesign);
         List<JsonAttrRep> attrs = new ArrayList<JsonAttrRep>();
         for (int j = 0; j < 5; ++j) {
            JsonAttrRep attr = new JsonAttrRep();
            attr.setType(CoreAttributeTypes.Active);
            attr.setValue(String.format("test %s", j));
            attrs.add(attr);
         }
         rep.setAttrs(attrs);
         outputItems.add(rep);
      }
      ObjectMapper mapper = new ObjectMapper();
      try {
         output = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(outputItems);
         System.out.println(output);
      } catch (IOException ex) {
         //
      }
      return output;
   }

   private void parseJsonOutput(String output) throws IOException {
      ObjectMapper mapper = new ObjectMapper();

      List<JsonArtRep> reqts = mapper.readValue(output, new TypeReference<List<JsonArtRep>>() { //
      });
      Assert.assertTrue(reqts.size() == 5);

   }

}
