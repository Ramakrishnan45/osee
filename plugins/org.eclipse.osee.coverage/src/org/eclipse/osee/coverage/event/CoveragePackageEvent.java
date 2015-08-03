//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.09.20 at 02:02:40 PM MST 
//

package org.eclipse.osee.coverage.event;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.OseeLog;

public class CoveragePackageEvent {

   protected CoverageChange _package;
   protected List<CoverageChange> coverages;
   protected String sessionId;

   public CoveragePackageEvent(String coveragePackageName, String coveragePackageGuid, CoverageEventType coverageEventType, String sessionId) {
      _package = new CoverageChange(coveragePackageName, coveragePackageGuid, coverageEventType);
   }

   public CoveragePackageEvent(CoveragePackage coveragePackage, CoverageEventType coverageEventType) {
      try {
         this.sessionId = ClientSessionManager.getSessionId();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Can't get sessionId", ex);
         this.sessionId = GUID.create();
      }
      _package = new CoverageChange(coveragePackage, coverageEventType);
   }

   public CoverageChange getPackage() {
      return _package;
   }

   public void setPackage(CoverageChange value) {
      this._package = value;
   }

   public List<CoverageChange> getCoverages() {
      if (coverages == null) {
         coverages = new ArrayList<CoverageChange>();
      }
      return this.coverages;
   }

   public String getSessionId() {
      return sessionId;
   }

   public void setSessionId(String value) {
      this.sessionId = value;
   }

}