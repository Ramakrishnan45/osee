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
package org.eclipse.osee.framework.database.init.internal;

import org.eclipse.osee.framework.core.translation.IDataTranslationService;
import org.eclipse.osee.framework.core.translation.IDataTranslationServiceProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class DatabaseInitActivator implements BundleActivator, IDataTranslationServiceProvider {
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.database.init";

   private static DatabaseInitActivator instance;
   private ServiceTracker serviceTracker;

   @Override
   public void start(BundleContext context) throws Exception {
      DatabaseInitActivator.instance = this;
      serviceTracker = new ServiceTracker(context, IDataTranslationService.class.getName(), null);
      serviceTracker.open(true);
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      if (serviceTracker != null) {
         serviceTracker.close();
      }
   }

   public static DatabaseInitActivator getInstance() {
      return instance;
   }

   @Override
   public IDataTranslationService getTranslationService() {
      return (IDataTranslationService) serviceTracker.getService();
   }
}
