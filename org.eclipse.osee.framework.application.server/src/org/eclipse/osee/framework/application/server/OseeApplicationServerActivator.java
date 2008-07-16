/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.application.server;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

public class OseeApplicationServerActivator implements BundleActivator {
   private static final String errorMessage =
         "Error launching application server - did you forget to set the following vmargs ?\n-Dorg.osgi.service.http.port=<port>\n-Dosgi.compatibility.bootdelegation=true\n-Dequinox.ds.debug=true\n-Dosee.application.server.data=<FILE SYSTEM PATH>";

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
    */
   public void start(BundleContext context) throws Exception {
      OseeProperties oseeProperties = OseeProperties.getInstance();

      if (oseeProperties.isLocalApplicationServerRequired() != false) {
         Map<String, Bundle> bundles = new HashMap<String, Bundle>();
         for (Bundle bundle : context.getBundles()) {
            bundles.put(bundle.getSymbolicName(), bundle);
         }
         try {
            String requiredBundles = (String) context.getBundle().getHeaders().get("Require-Bundle");
            launchApplicationServer(requiredBundles, bundles);
         } catch (Exception ex) {
            throw new Exception(errorMessage, ex);
         }
      }
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
    */
   public void stop(BundleContext context) throws Exception {
   }

   private void launchApplicationServer(String requiredBundles, Map<String, Bundle> bundles) throws BundleException {
      Pattern pattern = Pattern.compile("(.*)?;bundle-version=\"(.*)?\"");
      for (String entry : requiredBundles.split(",")) {
         Matcher matcher = pattern.matcher(entry);
         while (matcher.find()) {
            String bundleName = matcher.group(1);
            String requiredVersion = matcher.group(2);
            Bundle bundle = bundles.get(bundleName);
            if (bundle != null && isVersionAllowed(bundle, requiredVersion)) {
               bundle.start();
            }
         }
      }
      String message =
            String.format("Osee Application Server - port: [%s] data: [%s]", System.getProperty(
                  "org.osgi.service.http.port", "-1"), OseeProperties.getInstance().getOseeApplicationServerData());
      System.out.println(message);
   }

   private boolean isVersionAllowed(Bundle bundle, String requiredVersion) {
      return true;
   }
}
