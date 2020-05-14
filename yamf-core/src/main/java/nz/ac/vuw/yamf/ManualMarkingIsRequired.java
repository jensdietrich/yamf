package nz.ac.vuw.yamf;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indicating that this question has to be marked manually.
 * The purpose of this annotation is to include this in reports that can then be manually finished.
 * @author jens dietrich
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ManualMarkingIsRequired {
    String instructions() default "";
}
