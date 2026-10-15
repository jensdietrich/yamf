package nz.ac.wgtn.yamf;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indicating that the *actual* mark is imported through a static function.
 * This function must have no parameters , and return a double.
 * Example: the function may read the mark from a file computed by some other tool.
 * @author jens dietrich
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ImportMark {
    String method() default ""; // the static method
    Class clazz() ; // the class where the static method is declared
    String context() ; // a key that will be passed to the importer to identify the test
}
