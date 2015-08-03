//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.09.20 at 02:02:40 PM MST 
//

package org.eclipse.osee.coverage.event;

import org.eclipse.osee.coverage.model.ICoverage;

public class CoverageChange {

   protected String name;
   protected String guid;
   protected CoverageEventType coverageEventType;

   public CoverageChange(ICoverage coverage, CoverageEventType coverageEventType) {
      this(coverage.getName(), coverage.getGuid(), coverageEventType);
   }

   public CoverageChange(String name, String guid, CoverageEventType coverageEventType) {
      super();
      this.name = name;
      this.guid = guid;
      this.coverageEventType = coverageEventType;
   }

   public String getName() {
      return name;
   }

   public void setName(String value) {
      this.name = value;
   }

   public String getGuid() {
      return guid;
   }

   public void setGuid(String value) {
      this.guid = value;
   }

   public CoverageEventType getEventType() {
      return coverageEventType;
   }

   public void setEventType(CoverageEventType coverageEventType) {
      this.coverageEventType = coverageEventType;
   }

   @Override
   public String toString() {
      return String.format("[%s - %s - %s]", this.coverageEventType, this.guid, this.name);
   }
}