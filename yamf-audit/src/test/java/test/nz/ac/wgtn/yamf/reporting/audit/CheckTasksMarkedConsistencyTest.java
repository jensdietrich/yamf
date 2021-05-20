package test.nz.ac.wgtn.yamf.reporting.audit;

import com.google.common.collect.Lists;
import nz.ac.wgtn.yamf.MarkingResultRecord;
import nz.ac.wgtn.yamf.reporting.audit.AuditRule;
import nz.ac.wgtn.yamf.reporting.audit.CheckTasksMarkedConsistency;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static test.nz.ac.wgtn.yamf.reporting.audit.MarkingResultRecordFactory.*;


public class CheckTasksMarkedConsistencyTest {

    @Test
    public void testTasksAreConsistent() throws Exception {
        List<List<MarkingResultRecord>> allResults = Lists.newArrayList(
            Lists.newArrayList(
                create("task1",1,2),
                create("task2",1,2)
            ),
            Lists.newArrayList(
                create("task1",0,2),
                create("task2",0,2)
            )
        );
        List<AuditRule.Result> auditResults = new CheckTasksMarkedConsistency().apply(allResults);
        assertTrue(auditResults.isEmpty());
    }

    @Test
    public void testTaskCountsDontMatch() throws Exception {
        List<List<MarkingResultRecord>> allResults = Lists.newArrayList(
            Lists.newArrayList(
                create("task1",1,2),
                create("task2",1,2)
            ),
            Lists.newArrayList(
                create("task1",1,2)
            )
        );
        List<AuditRule.Result> auditResults = new CheckTasksMarkedConsistency().apply(allResults);
        assertSame(1,auditResults.size());
        AuditRule.Result auditResult = auditResults.get(0);
        assertSame(AuditRule.Status.ERROR,auditResult.status);
        assertEquals("inconsistent task counts across submissions: 2 task(s) marked for submission 1 but 1 task(s) marked for submission 2",auditResult.details);
    }

    @Test
    public void testTaskNamesDontMatch() throws Exception {
        List<List<MarkingResultRecord>> allResults = Lists.newArrayList(
            Lists.newArrayList(
                create("task1",1,2),
                create("task2",1,2)
            ),
            Lists.newArrayList(
                create("task1",1,2),
                create("task3",1,2)
            )
        );
        List<AuditRule.Result> auditResults = new CheckTasksMarkedConsistency().apply(allResults);
        assertSame(1,auditResults.size());
        AuditRule.Result auditResult = auditResults.get(0);
        assertSame(AuditRule.Status.ERROR,auditResult.status);
        assertEquals("task names for task 2 mismatch between submissions 1 and 2",auditResult.details);
    }
}
