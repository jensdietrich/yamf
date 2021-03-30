package test.nz.ac.wgtn.yamf;

import nz.ac.wgtn.yamf.checks.mvn.MVNActions;
import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static test.nz.ac.wgtn.yamf.Utils.getResourceAsFile;

/**x
 * Test classpath inference based on the maven dependency plugin.
 * @author jens dietrich
 */
public class ClasspathInferenceTest {
    @Test
    public void testThisClasspath() throws Exception {
        File project = getResourceAsFile("project-with-dependencies");
        String classpath = MVNActions.inferClasspath(project,false);
        // System.out.println(classpath);
        assertTrue(classpath.contains("log4j-api-2.13.3.jar"));
        assertTrue(classpath.contains("log4j-core-2.13.3.jar"));
        assertTrue(classpath.contains("guava-27.1-jre.jar"));
    }
}
