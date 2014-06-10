/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.rest.client.internal;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import org.eclipse.osee.jaxrs.client.WebClientProvider;
import org.eclipse.osee.ote.rest.client.OTECacheItem;
import org.eclipse.osee.ote.rest.client.OteClient;
import org.eclipse.osee.ote.rest.client.Progress;
import org.eclipse.osee.ote.rest.client.ProgressWithCancel;
import org.eclipse.osee.ote.rest.model.OTEConfiguration;
import org.eclipse.osee.ote.rest.model.OTETestRun;

/**
 * @author Andrew M. Finkbeiner
 */
public class OteClientImpl implements OteClient {

   private ExecutorService executor;
   private WebClientProvider provider;

   public void setWebClientProvider(WebClientProvider provider) {
      this.provider = provider;
   }

   public void start() {
      executor = Executors.newCachedThreadPool(new ThreadFactory() {
         @Override
         public Thread newThread(Runnable arg0) {
            Thread th = new Thread(arg0);
            th.setName("OTE Client " + th.getId());
            th.setDaemon(true);
            return th;
         }
      });
   }

   public void stop() {
      if (executor != null) {
         executor.shutdown();
      }
   }

   @Override
   public Future<Progress> getFile(URI uri, File destination, String filePath, final Progress progress) {
      return executor.submit(new GetOteServerFile(uri, destination, filePath, progress, provider));
   }

   @Override
   public Future<Progress> configureServerEnvironment(URI uri, List<File> jars, final Progress progress) {
      return executor.submit(new ConfigureOteServer(uri, jars, progress, provider));
   }

   @Override
   public Future<Progress> updateServerJarCache(URI uri, String baseJarURL, List<OTECacheItem> jars, Progress progress) {
      return executor.submit(new PrepareOteServerFile(uri, baseJarURL, jars, progress, provider));
   }

   @Override
   public Future<ProgressWithCancel> runTest(URI uri, OTETestRun tests, Progress progress) {
      return executor.submit(new RunTests(uri, tests, progress, provider));
   }

   @Override
   public Future<Progress> configureServerEnvironment(URI uri, OTEConfiguration configuration, Progress progress) {
      return executor.submit(new ConfigureOteServer(uri, configuration, progress, provider));
   }

}
