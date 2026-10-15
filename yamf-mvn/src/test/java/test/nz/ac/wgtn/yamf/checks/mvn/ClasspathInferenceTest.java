package test.nz.ac.wgtn.yamf.checks.mvn;

import nz.ac.wgtn.yamf.OnFailure;
import nz.ac.wgtn.yamf.checks.mvn.MVNActions;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static test.nz.ac.wgtn.yamf.checks.mvn.Utils.getResourceAsFile;

/**x
 * Test classpath inference based on the maven dependency plugin.
 * Note that the expected versions are read from the test project's pom instead of being hard-coded here,
 * so that dependency version bumps (e.g. by dependabot) in the test resources do not break this test.
 * @author jens dietrich
 */
public class ClasspathInferenceTest {

    // matches <artifactId>foo</artifactId> ... <version>1.2.3</version> within a dependency declaration
    private static final Pattern DEPENDENCY = Pattern.compile(
        "<artifactId>\\s*([^<\\s]+)\\s*</artifactId>\\s*<version>\\s*([^<\\s]+)\\s*</version>");

    /**
     * Get the version declared for an artifact in the pom of the test project.
     */
    private static String declaredVersion(File project, String artifactId) throws Exception {
        String pom = Files.readString(new File(project, "pom.xml").toPath());
        Matcher matcher = DEPENDENCY.matcher(pom);
        while (matcher.find()) {
            if (artifactId.equals(matcher.group(1))) {
                return matcher.group(2);
            }
        }
        throw new IllegalStateException("No version declared for " + artifactId + " in " + project);
    }

    private static void assertClasspathContains(String classpath, File project, String artifactId) throws Exception {
        String jar = artifactId + '-' + declaredVersion(project, artifactId) + ".jar";
        assertTrue(classpath.contains(jar), "inferred classpath does not contain " + jar + ": " + classpath);
    }

    @Test
    public void testThisClasspath() throws Exception {
        File project = getResourceAsFile("project-with-dependencies");
        String classpath = MVNActions.inferClasspath(project,false, OnFailure.CARRY_ON);
        // System.out.println(classpath);
        assertClasspathContains(classpath, project, "log4j-api");
        assertClasspathContains(classpath, project, "log4j-core");
        assertClasspathContains(classpath, project, "guava");
    }
}
