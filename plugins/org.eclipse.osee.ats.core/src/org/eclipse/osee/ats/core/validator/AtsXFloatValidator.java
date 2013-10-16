/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.validator;

import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWidgetDefinition;
import org.eclipse.osee.ats.api.workdef.WidgetResult;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class AtsXFloatValidator extends AtsXWidgetValidator {

   @Override
   public WidgetResult validateTransition(IValueProvider provider, IAtsWidgetDefinition widgetDef, IAtsStateDefinition fromStateDef, IAtsStateDefinition toStateDef) throws OseeCoreException {
      WidgetResult result = WidgetResult.Valid;
      if ("XFloatDam".equals(widgetDef.getXWidgetName())) {
         result = validateWidgetIsRequired(provider, widgetDef, fromStateDef, toStateDef);
         if (!result.isValid()) {
            return result;
         }
         result = isValidFloat(provider, widgetDef);
         if (!result.isValid()) {
            return result;
         }
      }
      return result;
   }
}
