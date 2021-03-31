package nz.ac.wgtn.yamf.checks.cha;

import nz.ac.wgtn.yamf.checks.jbytecode.JMethod;
import org.junit.jupiter.api.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Call Graph Tests.
 * @author jens dietrich
 */
public class TestCallGraph {

    CallGraph callGraph = null;

    @BeforeEach
    public void setup() throws Exception {
        File testProjectRoot = new File(TestCallGraph.class.getResource("/test-callgraph").getFile());
        Assumptions.assumeTrue(testProjectRoot.exists(),"Test project does not exist: " + testProjectRoot.getAbsolutePath());

        File testTargetFolder = new File(testProjectRoot,"target");
        Assumptions.assumeTrue(testTargetFolder.exists(),"Test project target does not exist (must build with \"mvn compile dependency:copy-dependencies\"): " + testProjectRoot.getAbsolutePath());

        File testClassesFolder = new File(testTargetFolder,"classes");
        Assumptions.assumeTrue(testClassesFolder.exists(),"Test project target does not exist (must build with \"mvn compile dependency:copy-dependencies\"): " + testProjectRoot.getAbsolutePath());

        File testLibFolder = new File(testTargetFolder,"dependency");
        Assumptions.assumeTrue(testLibFolder.exists(),"Test project target does not exist (must build with \"mvn compile dependency:copy-dependencies\"): " + testProjectRoot.getAbsolutePath());

        Set<File> classFiles = Files.walk(Paths.get(testClassesFolder.getAbsolutePath()))
                .filter(path -> path.toFile().getName().endsWith(".class"))
                .map(path -> path.toFile())
                .collect(Collectors.toSet());

        List<File> dependencies = Files.walk(Paths.get(testLibFolder.getAbsolutePath()))
                .filter(path -> path.toFile().getName().endsWith(".jar"))
                .map(path -> path.toFile())
                .collect(Collectors.toList());

        callGraph = CHAActions.constructCallGraph(classFiles,dependencies);

    }

    @AfterEach
    public void tearDown() throws Exception {
        callGraph = null;
    }

    private JMethod findMethod(String owner, String name, String descr) {
        return callGraph.getVertices().stream()
            .filter(m -> Objects.equals(m.getOwner().getName(),owner))
            .filter(m -> Objects.equals(m.getName(),name))
            .filter(m -> Objects.equals(m.getDescriptor().getRawDescriptor(),descr))
            .findAny()
            .get();
    }

    @Disabled
    @Test
    public void testCallsitesInMyAppenderAppend1() {
        JMethod source = findMethod("foo.MyAppender","append","(Lorg/apache/log4j/spi/LoggingEvent;)V");
        JMethod target = findMethod("foo.MyHelper","write","(Ljava/lang/Object;)V");

        Assertions.assertTrue(callGraph.containsVertex(source));
        Assertions.assertTrue(callGraph.containsVertex(target));
        Assertions.assertTrue(callGraph.getSuccessors(source).contains(target));
    }

    @Disabled
    @Test
    public void testCallsitesInMyAppenderAppend2() {
        JMethod source = findMethod("foo.MyAppender","append","(Lorg/apache/log4j/spi/LoggingEvent;)V");
        JMethod target = findMethod("org.apache.log4j.WriterAppender","append","(Lorg/apache/log4j/spi/LoggingEvent;)V");

        Assertions.assertTrue(callGraph.containsVertex(source));
        Assertions.assertTrue(callGraph.containsVertex(target));
        Assertions.assertTrue(callGraph.getSuccessors(source).contains(target));
    }

    @Disabled
    @Test
    public void testCallsitesInMyHelperWrite() {
        JMethod source = findMethod("foo.MyHelper","write","(Ljava/lang/Object;)V");
        JMethod target = findMethod("java.io.PrintStream","println","(Ljava/lang/String;)V");

        Assertions.assertTrue(callGraph.containsVertex(source));
        Assertions.assertTrue(callGraph.containsVertex(target));
        Assertions.assertTrue(callGraph.getSuccessors(source).contains(target));
    }

}
