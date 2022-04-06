package test.nz.ac.wgtn.yamf.checks.jbytecode;

import nz.ac.wgtn.yamf.ExpectationChecker;
import nz.ac.wgtn.yamf.checks.jbytecode.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JAnnotationTest {

    private JClass jclazz = null;

    @BeforeEach
    public void setup () throws Exception {
        URL url = JAnnotationTest.class.getResource("/test/nz/ac/wgtn/yamf/checks/jbytecode/SampleAnnotation.class");
        System.out.println(url);
        File file = new File(url.getFile());
        System.out.println(file);
        System.out.println(file.exists());
        System.out.println();
        jclazz = JByteCodeActions.getClass(file, ExpectationChecker.Ignore);
    }

    @AfterEach
    public void tearDown () throws Exception {
        jclazz = null;
    }


    @Test
    public void testMethodAnnotation1 () {
        assertEquals(2,jclazz.getAnnotations().size());
        JAnnotation annotation = jclazz.getAnnotations().get(0);
        assertEquals("java.lang.annotation.Target",annotation.getName());
        assertEquals(1,annotation.getPropertyNames().size());
        assertTrue(annotation.getPropertyNames().contains("value"));
        assertEquals(List.of("java.lang.annotation.ElementType.FIELD","java.lang.annotation.ElementType.METHOD"),annotation.getProperty("value"));
    }

    @Test
    public void testMethodAnnotation2 () {
        assertEquals(2,jclazz.getAnnotations().size());
        JAnnotation annotation = jclazz.getAnnotations().get(1);
        assertEquals("java.lang.annotation.Retention",annotation.getName());
        assertEquals(1,annotation.getPropertyNames().size());
        assertTrue(annotation.getPropertyNames().contains("value"));
        assertEquals("java.lang.annotation.RetentionPolicy.SOURCE",annotation.getProperty("value"));
    }

}
