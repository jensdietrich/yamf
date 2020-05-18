package nz.ac.wgtn.yamf.examples.mvn;

import nz.ac.wgtn.yamf.checks.mvn.MVNDependency;
import nz.ac.wgtn.yamf.checks.mvn.MVNActions;
import nz.ac.wgtn.yamf.checks.mvn.MVNChecks;
import nz.ac.wgtn.yamf.ManualMarkingIsRequired;
import nz.ac.wgtn.yamf.Marking;
import nz.ac.wgtn.yamf.commons.IDE;
import org.junit.jupiter.api.*;
import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Example marking scheme for Maven projects.
 * @author jens dietrich
 */
public class MarkingScheme {

    // set by script
    static File submission = null;

    // some static checks on the project structure

    @Test
    @Marking(name="Q1 -- project must contain a valid pom.xml file",marks = 1)
    public void testValidPom () throws Exception {
        File pom = new File(submission,"pom.xml");
        MVNChecks.assertIsPOM(pom);
    }

    @Test
    @Marking(name="Q2 -- project must have valid directory structure with sources in the right places",marks = 2)
    public void testSourceCodeFolderStructure () throws Exception {
        // using junit5s assertAll feature evaluates several assertions even if some fail, and will therefore generate more comprehensive reports
        // if partial marks are to be awarded for some of those asserts, then this question (test) should be split
        Assertions.assertAll(
            () -> MVNChecks.assertHasNonEmptySourceFolder(submission),
            () -> MVNChecks.assertHasNonEmptyTestSourceFolder(submission)
        );
    }

    @Test
    @Marking(name="Q3 -- project must have a source resource folder",marks = 1)
    public void testResourceFolder () throws Exception {
        MVNChecks.assertHasResourceFolder(submission);
    }

    @Test
    @Marking(name="Q4 -- project must compile", marks = 1)
    public void testCompilation () throws Exception {
        MVNActions.compile(submission);
    }

    @Test
    @Marking(name="Q5 -- project must be testable", marks = 1)
    public void testTest () throws Exception {
        MVNActions.test(submission);
    }

    @Test
    @Marking(name="Q6 -- tests must all succeed", marks = 2)
    public void testTestSuccess () throws Exception {
        // need to do this again, unless we accept dependencies on other tests with shared state (the  we must fix the test order)
        // or put this in fixture
        // this will create the /target folder with surefire reports
        MVNActions.test(submission);
        MVNChecks.assertHasNoFailingTests(submission);
    }

    @Test
    @Marking(name="Q7 -- the project must only use junit 4.* as its sole dependency (see https://mvnrepository.com/artifact/junit/junit for dependency details)", marks = 1)
    public void testDependencies () throws Exception {
        MVNActions.test(submission,true);
        File pom = new File(submission,"pom.xml");
        MVNChecks.assertValidDependencies(pom, dependencies -> {
            if (dependencies.size()==1) {
                MVNDependency dependency = dependencies.get(0);
                return dependency.getGroupId().equals("junit") && dependency.getArtifactId().equals("junit") && dependency.getVersion().startsWith("4.");
            }
            return false;
        });
    }

    // illustrates gaps in automated marking -- those will be reported as TODOs in the reports
    @Test
    @ManualMarkingIsRequired(instructions="check comments manually, look for completeness, correctness, spelling and grammar, 0.5 marks for minor gaps or violations")
    @Marking(name="Q8 -- comments should be comprehensive", marks = 1)
    public void testCodeComments () {}

    // illustrates a penalty -- 1 mark will be deducted if IDE mata data is part of the submission
    @Test
    @Marking(name="Q9 -- submissions should not contain IDE project settings", marks = -1)
    public void testForIDEProjectMetaData() {
        Set<String> ideProjectMetaData = IDE.getIDEProjectFiles(submission);
        Assertions.assertTrue(ideProjectMetaData.isEmpty(),"the submission contains IDE project meta data files and folders: " + ideProjectMetaData.stream().collect(Collectors.joining(",")));
    }


}
