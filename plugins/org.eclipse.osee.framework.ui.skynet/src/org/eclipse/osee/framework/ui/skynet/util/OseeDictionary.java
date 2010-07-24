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
package org.eclipse.osee.framework.ui.skynet.util;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.swt.styledText.IDictionary;
import org.osgi.framework.Bundle;

/**
 * Dictionary provided by OSEE that includes all dictionarys through the OseeDictionary extension point.
 * 
 * @author Donald G. Dunne
 */
public class OseeDictionary extends AbstractOperation implements IDictionary {

   private static Set<IOseeDictionary> dictionaries = new HashSet<IOseeDictionary>();
   private static OseeDictionary instance = new OseeDictionary();
   private static boolean isLoadingDictionary = false;
   private static boolean loaded = false;

   public static OseeDictionary getInstance() {
      return instance;
   }

   public static void load() {
      if (loaded || isLoadingDictionary) {
         return;
      }
      Operations.executeAsJob(new OseeDictionary(), false);
   }

   public OseeDictionary() {
      super("Loading Osee Dictionary", SkynetGuiPlugin.PLUGIN_ID);
   }

   @Override
   public boolean isWord(String word) {
      // Just return true till dictionary loaded
      if (!loaded && isLoadingDictionary) {
         return true;
      }
      // If not loaded, kickoff operation to load and return true
      if (!loaded) {
         Operations.executeAsJob(new OseeDictionary(), false);
         return true;
      }
      //       System.out.println("Lookup => \""+word+"\"");
      String cleanWord = getCleanWord(word);
      if (cleanWord.equals("") || cleanWord.length() == 1) {
         return true;
      }
      for (IOseeDictionary dict : dictionaries) {
         if (dict.isWord(cleanWord)) {
            return true;
         }
      }
      return false;
   }
   // Remove any junky characters and check for acronyms and other known
   // non-word type stuff. Return valid word to check in dictionary OR
   // "" if there is no word in this string
   // eg now) = now
   // a..b = ""
   // SQA = ""
   // NEon = ""
   private static Pattern pattern = Pattern.compile("^[a-zA-Z]{1}[a-z]+$");

   public String getCleanWord(String w) {
      // Single character is a valid word
      if (w.length() == 1) {
         return w;
      }

      // First, remove any non-word characters before and after string
      // eg. end. (now) it!
      w = w.replaceAll("^\\W+", "");
      w = w.replaceAll("\\W+$", "");
      w = w.replaceAll("'s$", ""); // Get rid of 's at end of word

      // If any non-alphabetic characters still in string, not a word
      // If string not either all lowercase or first letter capitalized, not a
      // word
      Matcher m = pattern.matcher(w);
      if (!m.find()) {
         return "";
      }
      return w.toLowerCase();
   }

   private synchronized void ensureLoaded() {
      if (!loaded && !isLoadingDictionary) {
         getIDictionaries();
         OseeLog.log(SkynetGuiPlugin.class, Level.INFO, "Loading Osee Dictionary");
      }
   }

   private static void getIDictionaries() {
      if (loaded) {
         return;
      }
      isLoadingDictionary = true;
      if (!Platform.isRunning()) {
         return;
      }
      IExtensionPoint point = null;
      try {
         point =
            Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.framework.ui.skynet.OseeDictionary");
      } catch (NullPointerException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, "Can't access OseeDictionary extension point", ex);
         return;
      }
      if (point == null) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, "Can't access OseeDictionary extension point");
         return;
      }
      IExtension[] extensions = point.getExtensions();
      for (IExtension extension : extensions) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         String classname = null;
         String bundleName = null;
         for (IConfigurationElement el : elements) {
            if (el.getName().equals("OseeDictionary")) {
               classname = el.getAttribute("classname");
               bundleName = el.getContributor().getName();
               if (classname != null && bundleName != null) {
                  Bundle bundle = Platform.getBundle(bundleName);
                  try {
                     Class<?> taskClass = bundle.loadClass(classname);
                     Object obj = taskClass.newInstance();
                     dictionaries.add((IOseeDictionary) obj);
                  } catch (Exception ex) {
                     OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP,
                        "Error loading OseeDictionary extension", ex);
                  }
               }

            }
         }
      }
      loaded = true;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      ensureLoaded();
   }
}
