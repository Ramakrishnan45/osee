/*******************************************************************************
 * Copyright(c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.manager.servlet.function;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.data.ChangeReportRequest;
import org.eclipse.osee.framework.core.data.ChangeReportResponse;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.core.services.IOseeBranchServiceProvider;
import org.eclipse.osee.framework.core.services.IOseeDataTranslationProvider;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Jeff C. Phillips
 */
public class ChangeReportFunction {

   public void getChanges(HttpServletRequest req, HttpServletResponse resp, IOseeBranchServiceProvider branchServiceProvider, IOseeDataTranslationProvider dataTransalatorProvider) throws Exception {
      IDataTranslationService service = dataTransalatorProvider.getTranslatorService();
      ChangeReportRequest request = service.convert(req.getInputStream(), CoreTranslatorId.CHANGE_REPORT_REQUEST);
      
      ChangeReportResponse response = new ChangeReportResponse();
      branchServiceProvider.getBranchService().getChanges(new NullProgressMonitor(),
    		  request, response);

      resp.setStatus(HttpServletResponse.SC_ACCEPTED);
      resp.setContentType("text/xml");
      resp.setCharacterEncoding("UTF-8");
      InputStream inputStream =
            service.convertToStream(response, CoreTranslatorId.CHANGE_REPORT_RESPONSE);
      Lib.inputStreamToOutputStream(inputStream, resp.getOutputStream());
   }
}
