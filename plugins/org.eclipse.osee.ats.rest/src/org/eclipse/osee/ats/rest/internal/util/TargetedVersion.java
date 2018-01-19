package org.eclipse.osee.ats.rest.internal.util;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation on resource methods used to request inclusion of the TargetedVersion field of the parent TeamWorkflow
 *
 * @author Roberto E. Escobar
 */
@Documented
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface TargetedVersion {
   //
}