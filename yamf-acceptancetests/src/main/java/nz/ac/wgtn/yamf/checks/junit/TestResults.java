package nz.ac.wgtn.yamf.checks.junit;

import java.util.Objects;

/**
 * Representation of test results.
 * @author jens dietrich
 */
public class TestResults {

    private String consoleOutput = null;
    private String details = null;

    // test counts reported by junit
    private int tests = 0;
    private int testsSkipped = 0;
    private int testsWithErrors = 0;
    private int testsFailed = 0;

    void addToTests(int value) {
        this.tests = this.tests + value;
    }

    void addToTestsSkipped(int value) {
        this.testsSkipped = this.testsSkipped + value;
    }

    void addToTestsFailed(int value) {
        this.testsFailed = this.testsFailed + value;
    }

    void addToTestsWithErrors(int value) {
        this.testsWithErrors = this.testsWithErrors + value;
    }

    public String getConsoleOutput() {
        return consoleOutput;
    }

    public void setConsoleOutput(String consoleOutput) {
        this.consoleOutput = consoleOutput;
    }


    public int getTests() {
        return tests;
    }

    public void setTests(int tests) {
        this.tests = tests;
    }

    public void setTestsSkipped(int testsSkipped) {
        this.testsSkipped = testsSkipped;
    }

    public void setTestsWithErrors(int testsWithErrors) {
        this.testsWithErrors = testsWithErrors;
    }

    public void setTestsFailed(int testsFailed) {
        this.testsFailed = testsFailed;
    }

    public int getTestsSkipped() {
        return testsSkipped;
    }

    public int getTestsWithErrors() {
        return testsWithErrors;
    }

    public int getTestsSucceeded() {
        return this.tests - (this.testsWithErrors + this.testsFailed + this.testsSkipped);
    }

    public int getTestsFailed() {
        return testsFailed;
    }

    public String getDetails() {
        return details==null?"n/a":details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestResults that = (TestResults) o;
        return tests == that.tests &&
                testsSkipped == that.testsSkipped &&
                testsWithErrors == that.testsWithErrors &&
                testsFailed == that.testsFailed &&
                Objects.equals(consoleOutput, that.consoleOutput) &&
                Objects.equals(details, that.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(consoleOutput, details, tests, testsSkipped, testsWithErrors, testsFailed);
    }
}
