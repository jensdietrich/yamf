package nz.ac.wgtn.yamf.checks.junit;

import org.junit.jupiter.api.Assertions;

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

    public static void assertSomeTestsHaveBeenExecutedAndAllSucceeded(TestResults testResults) {
        Assertions.assertAll(
            () -> Assertions.assertTrue(0<testResults.getTests()),
            () -> Assertions.assertSame(0,testResults.getTestsFailed(),"Some tests have failed, details:\n" + testResults.getDetails()),
            () -> Assertions.assertSame(0,testResults.getTestsWithErrors(),"Some tests have caused errors, details:\n" + testResults.getDetails()),
            () -> Assertions.assertSame(0,testResults.getTestsSkipped(),"Some tests have been aborted, details:\n" + testResults.getDetails())
        );
    }

}
