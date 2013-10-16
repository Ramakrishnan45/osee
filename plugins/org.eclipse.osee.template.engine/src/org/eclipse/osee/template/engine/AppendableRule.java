/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.template.engine;

import java.io.IOException;
import org.eclipse.osee.framework.jdk.core.type.Named;

/**
 * @author Ryan D. Brooks
 */
public abstract class AppendableRule implements Named {
   private final String ruleName;

   public AppendableRule(String ruleName) {
      this.ruleName = ruleName;
   }

   @Override
   public String getName() {
      return ruleName;
   }

   public abstract void applyTo(Appendable appendable) throws IOException;
}