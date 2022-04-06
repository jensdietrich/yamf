package nz.ac.wgtn.yamf.examples.acceptancetests;

import nz.ac.wgtn.yamf.ExpectationChecker;
import nz.ac.wgtn.yamf.checks.junit.JUnitActions;
import nz.ac.wgtn.yamf.checks.junit.JUnitVersion;
import nz.ac.wgtn.yamf.checks.junit.TestResults;
import nz.ac.wgtn.yamf.checks.mvn.MVNActions;
import nz.ac.wgtn.yamf.Marking;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.File;

/**
 * Example marking scheme for Maven projects that uses acceptance tests.
 * The project under test uses Maven.
 * @author jens dietrich
 */
public class MarkingScheme {

    // injected by the marking script
    static File submission = null;

    // the acceptance tests
    static File acceptanceTestProjectFolder = null;

    static File junitRunner = null;

    @BeforeAll
    public static void prepareAcceptanceTests () throws Exception {
        acceptanceTestProjectFolder = new File("yamf-examples/examples/acceptancetests/reference-solution-with-tests");
        assert acceptanceTestProjectFolder.exists();

        junitRunner = new File(JUnitActions.class.getResource("/junit-platform-console-standalone-1.6.2.jar").getFile());
        assert  junitRunner.exists();
    }

    @Test
    @Marking(name="Q1 -- run simple acceptance tests",marks=5.0)
    public void runSimpleAcceptanceTests () throws Exception {
        TestResults results = MVNActions.acceptanceTestMvnProject(junitRunner,"acceptancetests.TestCalculatorSimple", submission,acceptanceTestProjectFolder,true, JUnitVersion.JUNIT5, ExpectationChecker.AssumeTrue);
        Assumptions.assumeTrue(results.getTests() == 3);
        Assertions.assertSame(3,results.getTestsSucceeded(),"not all tests succeeded, details:\n" + results.getDetails());
    }

    @Test
    @Marking(name="Q2 -- run advanced acceptance tests to check overflow handing",marks=5.0)
    public void runAdvancedAcceptanceTests () throws Exception {
        TestResults results = MVNActions.acceptanceTestMvnProject(junitRunner,"acceptancetests.TestCalculatorOverflow", submission,acceptanceTestProjectFolder,true,JUnitVersion.JUNIT5, ExpectationChecker.AssumeTrue);
        Assumptions.assumeTrue(results.getTests() == 1);
        Assertions.assertSame(1,results.getTestsSucceeded(),"not all tests succeeded, details:\n" + results.getDetails());
    }
}
