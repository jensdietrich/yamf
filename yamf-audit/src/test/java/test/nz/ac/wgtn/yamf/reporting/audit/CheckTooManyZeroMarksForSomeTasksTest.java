package test.nz.ac.wgtn.yamf.reporting.audit;

import nz.ac.wgtn.yamf.reporting.audit.AuditRule;
import nz.ac.wgtn.yamf.reporting.audit.CheckTooManyZeroMarksForSomeTasks;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static test.nz.ac.wgtn.yamf.reporting.audit.MarkingResultRecordFactory.testData;


public class CheckTooManyZeroMarksForSomeTasksTest {

    @Test
    public void testRuleParameterTooBig() {
        assertThrows(IllegalArgumentException.class, () -> new CheckTooManyZeroMarksForSomeTasks(110));
    }

    @Test
    public void testRuleParameterTooSmall() {
        assertThrows(IllegalArgumentException.class, () -> new CheckTooManyZeroMarksForSomeTasks(-1));
    }

    @Test
    public void testRuleParameterOK() {
        new CheckTooManyZeroMarksForSomeTasks(10);
        assertTrue(true);
    }

    @Test
    public void testWith85PercentSensitivity() {
        List<AuditRule.Result> auditResults = new CheckTooManyZeroMarksForSomeTasks(85).apply(testData());
        assertSame(1,auditResults.size());
        AuditRule.Result auditResult = auditResults.get(0);
        assertSame(AuditRule.Status.WARN,auditResult.status);
        assertEquals("zero marks allocated for : 9 / 10 submissions for task task1",auditResult.details);
    }

    @Test
    public void testWith90PercentSensitivity() {
        List<AuditRule.Result> auditResults = new CheckTooManyZeroMarksForSomeTasks(90).apply(testData());
        assertSame(1,auditResults.size());
        AuditRule.Result auditResult = auditResults.get(0);
        assertSame(AuditRule.Status.WARN,auditResult.status);
        assertEquals("zero marks allocated for : 9 / 10 submissions for task task1",auditResult.details);
    }

    @Test
    public void testWith95PercentSensitivity() {
        List<AuditRule.Result> auditResults = new CheckTooManyZeroMarksForSomeTasks(95).apply(testData());
        assertTrue(auditResults.isEmpty());
    }

    @Test
    public void testWith100PercentSensitivity() {
        List<AuditRule.Result> auditResults = new CheckTooManyZeroMarksForSomeTasks(100).apply(testData());
        assertTrue(auditResults.isEmpty());
    }


}
