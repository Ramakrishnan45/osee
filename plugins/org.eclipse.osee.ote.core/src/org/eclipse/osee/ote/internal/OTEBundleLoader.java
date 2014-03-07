/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.internal;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.xml.XMLStreamWriterUtil;
import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;
import org.eclipse.osee.ote.Configuration;
import org.eclipse.osee.ote.ConfigurationItem;
import org.eclipse.osee.ote.ConfigurationStatus;
import org.eclipse.osee.ote.OTEServerRuntimeCache;
import org.eclipse.osee.ote.OTEStatusCallback;
import org.eclipse.osee.ote.core.OseeURLClassLoader;
import org.eclipse.osee.ote.core.ServiceUtility;
import org.eclipse.osee.ote.core.environment.interfaces.IRuntimeLibraryManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;

public class OTEBundleLoader implements IRuntimeLibraryManager{

   private final Collection<Bundle> installedBundles;
   private final Collection<Bundle> runningBundles;
   private final Map<String, String> bundleNameToMd5Map;
   private final BundleContext context;
   
   private URLClassLoader scriptClassLoader;
   private OTEServerRuntimeCache serverRuntimeCache;

   public void bindOTEServerRuntimeCache(OTEServerRuntimeCache serverRuntimeCache){
      this.serverRuntimeCache = serverRuntimeCache;
   }
   
   public void unbindOTEServerRuntimeCache(OTEServerRuntimeCache serverRuntimeCache){
      this.serverRuntimeCache = serverRuntimeCache;
   }
   
   public OTEBundleLoader() {
      this.context = FrameworkUtil.getBundle(getClass()).getBundleContext();
      this.installedBundles = new LinkedList<Bundle>();
      this.runningBundles = new LinkedList<Bundle>();
      this.bundleNameToMd5Map = new HashMap<String, String>();
   }

   @Override
   public boolean install(Configuration oteConfiguration, OTEStatusCallback<ConfigurationStatus> statusCallback) {
      boolean pass = true;
      for (ConfigurationItem bundleDescription : oteConfiguration.getItems()) {
         String bundleName = bundleDescription.getSymbolicName();
         try {
            boolean exists = false;
            for (Bundle bundle : runningBundles) {
               if (bundle.getSymbolicName().equals(bundleName)) {
                  exists = true;
                  break;
               }
            }
            if (!exists) {
               Bundle bundle = Platform.getBundle(bundleDescription.getSymbolicName());
               if (bundle == null) {
                  Bundle installedBundle;
                  File bundleData = acquireSystemLibraryStream(bundleDescription);
                  installedBundle = context.installBundle("reference:" + bundleData.toURI().toURL().toExternalForm());
                  bundleNameToMd5Map.put(bundleName, bundleDescription.getMd5Digest());
                  installedBundles.add(installedBundle);
               }
            }
            statusCallback.log("installed " + bundleName);
         } catch (Throwable th) {
            statusCallback.error(String.format("Unable to load [%s].", bundleName), th);
            pass = false;
         } finally {
            statusCallback.incrememtUnitsWorked(1);
         }
      }
      return pass;
   }

   @Override
   public boolean acquireBundles(Configuration oteConfiguration, OTEStatusCallback<ConfigurationStatus> statusCallback) {
      boolean pass = true;
      for (ConfigurationItem bundleDescription : oteConfiguration.getItems()) {
         String bundleName = bundleDescription.getSymbolicName();
         File bundleData = null;
         try{
            bundleData = acquireSystemLibraryStream(bundleDescription);
         } catch (Exception e) {
            pass = false;
            statusCallback.error("Failed to aquire bundle from client.", e);
         }
         finally {
            if(bundleData == null){
               pass = false;
               statusCallback.error("Failed to downloaded bundle " + bundleName);
            }
            statusCallback.incrememtUnitsWorked(1);
         }
         statusCallback.log("downloaded " + bundleName);
      }
      return pass;
   }
   
   @Override
   public boolean start(OTEStatusCallback<ConfigurationStatus> statusCallback) {
      boolean pass = true;
      Iterator<Bundle> iter = installedBundles.iterator();
      while (iter.hasNext()) {
         Bundle bundle = iter.next();
         try {
            String entry = bundle.getHeaders().get("Fragment-Host");
            if (entry == null) {
               bundle.start();
            }
            // We got here because bundle.start did not exception
            runningBundles.add(bundle);
            iter.remove();
            statusCallback.log("started " + bundle.getSymbolicName());
         } catch (BundleException ex) {
            pass = false;
            statusCallback.error("Failed to start " + bundle.getSymbolicName(), ex);
         } finally {
            statusCallback.incrememtUnitsWorked(1);
         }
      }
      return pass;
   }

   @Override
   public boolean uninstall(OTEStatusCallback<ConfigurationStatus> statusCallback) {
      boolean result = true;
      for (Bundle bundle : installedBundles) {
         try {
            bundle.uninstall();
         } catch (BundleException ex) {
            result = false;
            statusCallback.error("Failed to uninstall " + bundle.getSymbolicName(), ex);
         }
      }
      installedBundles.clear();

      for (Bundle bundle : runningBundles) {
         try {
            String entry = bundle.getHeaders().get("Fragment-Host");
        	if (entry == null) {
        	   bundle.stop();
        	}
            bundle.uninstall();
         } catch (BundleException ex) {
            result = false;
            statusCallback.error("Failed to stop and uninstall " + bundle.getSymbolicName(), ex);
         }
      }
      runningBundles.clear();
      
      return result;
   }

   private File acquireSystemLibraryStream(ConfigurationItem bundleDescription) throws Exception {
      File file = serverRuntimeCache.get(bundleDescription.getSymbolicName(), bundleDescription.getMd5Digest());
      if (file == null) {
         InputStream servedBundleIn = new URL(bundleDescription.getLocationUrl()).openConnection().getInputStream();
         file = serverRuntimeCache.save(bundleDescription.getSymbolicName(), bundleDescription.getMd5Digest(), servedBundleIn);
         servedBundleIn.close();
      }
      return file;
   }

   public void dispose() {
      bundleNameToMd5Map.clear();
      installedBundles.clear();
      runningBundles.clear();
   }

   @Override
   public boolean installed() {
      return installedBundles.size() > 0 || runningBundles.size() > 0;
   }

   @Override
   public void clearJarCache() {
      serverRuntimeCache.clearJarCache();
   }
   
   @Override
   public void resetScriptLoader(Configuration oteConfiguration, String[] classPaths) throws Exception {
      List<URL> urls = new ArrayList<URL>();
      if(oteConfiguration != null){
         for(ConfigurationItem item:oteConfiguration.getItems()){
            File file = serverRuntimeCache.get(item.getSymbolicName(), item.getMd5Digest());
            if(file != null){
               urls.add(file.toURI().toURL());
            }
         }
      }
      URL[] urlArray = urls.toArray(new URL[urls.size()+classPaths.length]);
      URL[] urlClassPaths = Lib.getUrlFromString(classPaths);
      System.arraycopy(urlClassPaths, 0, urlArray, urls.size(), urlClassPaths.length);
      scriptClassLoader = new OseeURLClassLoader("Script ClassLoader", urlArray, ExportClassLoader.getInstance());
   }

   @Override
   public Class<?> loadFromScriptClassLoader(String path) throws ClassNotFoundException {
      if (scriptClassLoader == null) {
         return loadFromRuntimeLibraryLoader(path);
      }
      Class<?> scriptClass = scriptClassLoader.loadClass(path);
      return scriptClass;
   }

   @Override
   public Class<?> loadFromRuntimeLibraryLoader(String clazz) throws ClassNotFoundException {
      return ExportClassLoader.getInstance().loadClass(clazz);
   }

   @Override
   public void toXml(XMLStreamWriter writer) throws XMLStreamException {
      Bundle[] bundles = ServiceUtility.getContext().getBundles();
      List<String> versions = new ArrayList<String>();
      for(Bundle bundle: bundles ){
         String version = (String) bundle.getHeaders().get("Bundle-Version");
         String implVersion = (String) bundle.getHeaders().get("Implementation-Version");
         if (version != null && implVersion != null) {
            versions.add(String.format("%s_%s_%s", bundle.getSymbolicName(), version, implVersion));
         } else if (version != null){
            versions.add(String.format("%s_%s", bundle.getSymbolicName(), version));
         } else {
            versions.add(String.format("%s", bundle.getSymbolicName()));
         }
      }
      Collections.sort(versions);
      
      writer.writeStartElement("RuntimeVersions");
      for (String bundleVersion : versions) {
         XMLStreamWriterUtil.writeElement(writer, "Version", bundleVersion);
      }
      writer.writeEndElement();
   }

}
