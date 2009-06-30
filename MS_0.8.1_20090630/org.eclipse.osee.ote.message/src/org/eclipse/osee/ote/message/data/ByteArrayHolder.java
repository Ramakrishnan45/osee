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
package org.eclipse.osee.ote.message.data;

/**
 * @author Andrew M. Finkbeiner
 */
public class ByteArrayHolder {
   private byte[] buffer;
   
   public ByteArrayHolder(){
      
   }
   
   public ByteArrayHolder(byte[] buffer){
      this.buffer = buffer;
   }
   
   public void set(byte[] buffer){
      this.buffer = buffer;
   }
   
   public byte[] get(){
      return this.buffer;
   }
}
