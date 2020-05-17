package nz.ac.wgtn.yamf;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestIdentifier;

import java.util.Objects;

/**
 * The result of marking one particular question and aspect corresponding to a test annotated with @Mark and @Test.
 * This is the information that will be used to generate resports.
 * @author jens dietrich
 */
public class MarkingResultRecord {

    private MarkingTestExecutionListener.AssignedMark mark = null;
    private TestIdentifier testIdentifier = null;
    private TestExecutionResult testExecutionResult = null;

    public MarkingResultRecord(MarkingTestExecutionListener.AssignedMark mark, TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        this.mark = mark;
        this.testIdentifier = testIdentifier;
        this.testExecutionResult = testExecutionResult;
    }

    public double getMark() {
        return testExecutionResult.getStatus()== TestExecutionResult.Status.SUCCESSFUL?mark.marks:0;
    }

    public double getMaxMark() {
        return mark.marks;
    }

    public boolean isManualMarkingRequired() {
        return mark.mustBeMarkedManually;
    }

    // is null if isManualMarkingRequired() return false
    public String getManualMarkingInstructions() {
        return mark.manualMarkingInstructions;
    }

    public String getName() {
        return mark.name;
    }

    public String getTestIdentifier() {
        return testIdentifier.getDisplayName();
    }

    public String getResultStatus() {
        return testExecutionResult.getStatus().name();
    }

    public boolean isSuccess() {
        return this.getResultStatus().equals(TestExecutionResult.Status.SUCCESSFUL.name());
    }

    public boolean isAborted() {
        return this.getResultStatus().equals(TestExecutionResult.Status.ABORTED.name());
    }

    public boolean isFailed() {
        return this.getResultStatus().equals(TestExecutionResult.Status.FAILED.name());
    }

    public boolean hasThrowable() {
        return testExecutionResult.getThrowable().isPresent();
    }

    public Throwable getThrowable() {
        return testExecutionResult.getThrowable().get();
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarkingResultRecord that = (MarkingResultRecord) o;
        return Objects.equals(mark, that.mark) &&
                Objects.equals(testIdentifier, that.testIdentifier) &&
                Objects.equals(testExecutionResult, that.testExecutionResult);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mark, testIdentifier, testExecutionResult);
    }

    @Override
    public String toString() {
        return "MarkingResultRecord{" +
                "mark=" + mark +
                ", testIdentifier=" + testIdentifier +
                ", testExecutionResult=" + testExecutionResult +
                '}';
    }
}
