package nz.ac.wgtn.yamf.checks.cha;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Type Hierarchy Tests.
 * @author jens dietrich
 */
public class TestTypeHierachy {

    TypeHierarchy typeHierarchy = null;

    @BeforeEach
    public void setup() throws Exception {
        File testProjectRoot = new File(TestTypeHierachy.class.getResource("/test-project1").getFile());
        Assumptions.assumeTrue(testProjectRoot.exists(),"Test project does not exist: " + testProjectRoot.getAbsolutePath());

        File testTargetFolder = new File(testProjectRoot,"target");
        Assumptions.assumeTrue(testTargetFolder.exists(),"Test project target does not exist (must build with \"maven compile dependency:copy-dependencies\"): " + testProjectRoot.getAbsolutePath());

        File testClassesFolder = new File(testTargetFolder,"classes");
        Assumptions.assumeTrue(testClassesFolder.exists(),"Test project target does not exist (must build with \"maven compile dependency:copy-dependencies\"): " + testProjectRoot.getAbsolutePath());

        File testLibFolder = new File(testTargetFolder,"dependency");
        Assumptions.assumeTrue(testLibFolder.exists(),"Test project target does not exist (must build with \"maven compile dependency:copy-dependencies\"): " + testProjectRoot.getAbsolutePath());

        Set<File> classFiles = Files.walk(Paths.get(testClassesFolder.getAbsolutePath()))
                .filter(path -> path.toFile().getName().endsWith(".class"))
                .map(path -> path.toFile())
                .collect(Collectors.toSet());

        Set<File> dependencies = Files.walk(Paths.get(testLibFolder.getAbsolutePath()))
                .filter(path -> path.toFile().getName().endsWith(".jar"))
                .map(path -> path.toFile())
                .collect(Collectors.toSet());

        typeHierarchy = CHAActions.getTypeHierachy(classFiles,dependencies);

    }

    @AfterEach
    public void tearDown() throws Exception {
        typeHierarchy = null;
    }

    @Test
    public void testAppender1() {
        Assumptions.assumeTrue(typeHierarchy!=null);
        CHAChecks.assertImplements(typeHierarchy,"foo.AppenderImpl1","org.apache.log4j.Appender");
        CHAChecks.assertDoesNotExtend(typeHierarchy,"foo.AppenderImpl1","org.apache.log4j.AppenderSkeleton");
        CHAChecks.assertDoesNotExtend(typeHierarchy,"foo.AppenderImpl1","org.apache.log4j.WriterAppender");
    }

    @Test
    public void testAppender2() {
        Assumptions.assumeTrue(typeHierarchy!=null);
        CHAChecks.assertImplements(typeHierarchy,"foo.AppenderImpl2","org.apache.log4j.Appender");
        CHAChecks.assertExtends(typeHierarchy,"foo.AppenderImpl2","org.apache.log4j.AppenderSkeleton");
        CHAChecks.assertDoesNotExtend(typeHierarchy,"foo.AppenderImpl2","org.apache.log4j.WriterAppender");
    }

    @Test
    public void testAppender3() {
        Assumptions.assumeTrue(typeHierarchy!=null);
        CHAChecks.assertImplements(typeHierarchy,"foo.AppenderImpl3","org.apache.log4j.Appender");
        CHAChecks.assertExtends(typeHierarchy,"foo.AppenderImpl3","org.apache.log4j.AppenderSkeleton");
        CHAChecks.assertExtends(typeHierarchy,"foo.AppenderImpl3","org.apache.log4j.WriterAppender");
    }
}
