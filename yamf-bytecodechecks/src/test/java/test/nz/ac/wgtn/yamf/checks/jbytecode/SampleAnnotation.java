package test.nz.ac.wgtn.yamf.checks.jbytecode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface SampleAnnotation {
}
