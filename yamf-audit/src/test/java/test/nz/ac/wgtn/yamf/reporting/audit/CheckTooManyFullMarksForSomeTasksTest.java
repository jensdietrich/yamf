package test.nz.ac.wgtn.yamf.reporting.audit;

import nz.ac.wgtn.yamf.reporting.audit.AuditRule;
import nz.ac.wgtn.yamf.reporting.audit.CheckTooManyFullMarksForSomeTasks;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static test.nz.ac.wgtn.yamf.reporting.audit.MarkingResultRecordFactory.testData;


public class CheckTooManyFullMarksForSomeTasksTest {

    @Test
    public void testRuleParameterTooBig() {
        assertThrows(IllegalArgumentException.class, () -> new CheckTooManyFullMarksForSomeTasks(110));
    }

    @Test
    public void testRuleParameterTooSmall() {
        assertThrows(IllegalArgumentException.class, () -> new CheckTooManyFullMarksForSomeTasks(-1));
    }

    @Test
    public void testRuleParameterOK() {
        new CheckTooManyFullMarksForSomeTasks(90);
        assertTrue(true);
    }

    @Test
    public void testWith85PercentSensitivity() {
        List<AuditRule.Result> auditResults = new CheckTooManyFullMarksForSomeTasks(85).apply(testData());
        assertSame(1,auditResults.size());
        AuditRule.Result auditResult = auditResults.get(0);
        assertSame(AuditRule.Status.WARN,auditResult.status);
        assertEquals("full marks allocated for : 9 / 10 submissions for task task2",auditResult.details);
    }

    @Test
    public void testWith90PercentSensitivity() {
        List<AuditRule.Result> auditResults = new CheckTooManyFullMarksForSomeTasks(90).apply(testData());
        assertSame(1,auditResults.size());
        AuditRule.Result auditResult = auditResults.get(0);
        assertSame(AuditRule.Status.WARN,auditResult.status);
        assertEquals("full marks allocated for : 9 / 10 submissions for task task2",auditResult.details);
    }

    @Test
    public void testWith95PercentSensitivity() {
        List<AuditRule.Result> auditResults = new CheckTooManyFullMarksForSomeTasks(95).apply(testData());
        assertTrue(auditResults.isEmpty());
    }

    @Test
    public void testWith100PercentSensitivity() {
        List<AuditRule.Result> auditResults = new CheckTooManyFullMarksForSomeTasks(100).apply(testData());
        assertTrue(auditResults.isEmpty());
    }


}
