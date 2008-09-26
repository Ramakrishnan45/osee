/*
 * Created on Jun 9, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.exception;

/**
 * @author Ryan D. Brooks
 */
public class BranchDoesNotExist extends OseeCoreException {
   private static final long serialVersionUID = -6197324585250025613L;

   /**
    * @param message
    */
   public BranchDoesNotExist(String message) {
      super(message);
   }

   /**
    * @param message
    * @param cause
    */
   public BranchDoesNotExist(String message, Throwable cause) {
      super(message, cause);
   }
}