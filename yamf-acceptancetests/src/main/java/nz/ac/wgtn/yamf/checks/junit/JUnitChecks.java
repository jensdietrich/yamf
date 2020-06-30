package nz.ac.wgtn.yamf.checks.junit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;

/**
 * Junit specific assertions for test results.
 * @author jens dietrich
 */
public class JUnitChecks {

    public static void assertTestsAllSucceed (TestResults testResults) {
        Assertions.assertAll(
            () -> Assertions.assertSame(0,testResults.getTestsFailed(),"Some tests have failed, details:\n" + testResults.getDetails()),
            () -> Assertions.assertSame(0,testResults.getTestsWithErrors(),"Some tests have caused errors, details:\n" + testResults.getDetails()),
            () -> Assertions.assertSame(0,testResults.getTestsSkipped(),"Some tests have been aborted, details:\n" + testResults.getDetails())
        );
    }

    public static void assertSomeTestsHaveBeenExecuted(TestResults testResults) {
        Assertions.assertTrue(0<testResults.getTests());
    }

    public static void assumeSomeTestsHaveBeenExecuted(TestResults testResults,String message) {
        Assumptions.assumeTrue(0<testResults.getTests());
    }

    public static void assertSomeTestsHaveBeenExecutedAndAllSucceeded(TestResults testResults) {
        Assertions.assertAll(
            () -> Assertions.assertTrue(0<testResults.getTests()),
            () -> Assertions.assertSame(0,testResults.getTestsFailed(),"Some tests have failed, details:\n" + testResults.getDetails()),
            () -> Assertions.assertSame(0,testResults.getTestsWithErrors(),"Some tests have caused errors, details:\n" + testResults.getDetails()),
            () -> Assertions.assertSame(0,testResults.getTestsSkipped(),"Some tests have been aborted, details:\n" + testResults.getDetails())
        );
    }

    public static boolean isJunit4InClassPath () {
        try {
            Class.forName("org.junit.Test");
            return true;
        }
        catch (Exception x) {
            return false;
        }
    }

    public static boolean isJunit5InClassPath () {
        try {
            Class.forName("org.junit.jupiter.api.Test");
            return true;
        }
        catch (Exception x) {
            return false;
        }
    }

    public static void assumeJunit4IsInClasspath () {
        Assumptions.assumeTrue(isJunit4InClassPath(),"JUnit4 is not available, classpath must be amended to facilitate testing");
    }

    public static void assumeJunit5IsInClasspath () {
        Assumptions.assumeTrue(isJunit5InClassPath(),"JUnit5 is not available, classpath must be amended to facilitate testing");
    }

}
