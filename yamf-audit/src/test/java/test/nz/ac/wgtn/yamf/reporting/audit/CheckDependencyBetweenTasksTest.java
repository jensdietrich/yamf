package test.nz.ac.wgtn.yamf.reporting.audit;

import nz.ac.wgtn.yamf.reporting.audit.AuditRule;
import nz.ac.wgtn.yamf.reporting.audit.CheckDependenciesBetweenTasks;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static test.nz.ac.wgtn.yamf.reporting.audit.MarkingResultRecordFactory.testData2;

/**
 * @author jens dietrich
 */

public class CheckDependencyBetweenTasksTest {

    @Test
    public void testRuleWithNegativeSensitivity() {
        assertThrows(IllegalArgumentException.class, () -> new CheckDependenciesBetweenTasks(-1));
    }

    @Test
    public void testTask1ToTask3DependencyWithDefaultSensitivity()  {
        List<AuditRule.Issue> auditResults = new CheckDependenciesBetweenTasks().apply(testData2());
        assertSame(1,auditResults.size());
        AuditRule.Issue auditResult = auditResults.get(0);
        assertSame(AuditRule.Status.WARN,auditResult.status);
        assertEquals("possible dependency detected between tasks \"task1\" and \"task3\"",auditResult.details);
    }

    @Test
    public void testTask1ToTask3DependencyWithSensitivity0()  {
        List<AuditRule.Issue> auditResults = new CheckDependenciesBetweenTasks(0).apply(testData2());
        assertSame(1,auditResults.size());
        AuditRule.Issue auditResult = auditResults.get(0);
        assertSame(AuditRule.Status.WARN,auditResult.status);
        assertEquals("possible dependency detected between tasks \"task1\" and \"task3\"",auditResult.details);
    }

    @Test
    public void testTask1ToTask3DependencyWithSensitivity8()  {
        List<AuditRule.Issue> auditResults = new CheckDependenciesBetweenTasks(8).apply(testData2());
        assertTrue(auditResults.isEmpty());
    }
}
