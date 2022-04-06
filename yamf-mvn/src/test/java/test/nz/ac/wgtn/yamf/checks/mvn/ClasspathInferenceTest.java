package test.nz.ac.wgtn.yamf.checks.mvn;

import nz.ac.wgtn.yamf.ExpectationChecker;
import nz.ac.wgtn.yamf.checks.mvn.MVNActions;
import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static test.nz.ac.wgtn.yamf.checks.mvn.Utils.getResourceAsFile;

/**x
 * Test classpath inference based on the maven dependency plugin.
 * @author jens dietrich
 */
public class ClasspathInferenceTest {
    @Test
    public void testThisClasspath() throws Exception {
        File project = getResourceAsFile("project-with-dependencies");
        String classpath = MVNActions.inferClasspath(project,false, ExpectationChecker.Ignore);
        // System.out.println(classpath);
        assertTrue(classpath.contains("log4j-api-2.17.1.jar"));
        assertTrue(classpath.contains("log4j-core-2.17.1.jar"));
        assertTrue(classpath.contains("guava-29.0-jre.jar"));
    }
}
