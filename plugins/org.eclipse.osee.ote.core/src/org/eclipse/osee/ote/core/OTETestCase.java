package org.eclipse.osee.ote.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface OTETestCase {
   int order() default Integer.MAX_VALUE;
   String[] traceability() default {};
}