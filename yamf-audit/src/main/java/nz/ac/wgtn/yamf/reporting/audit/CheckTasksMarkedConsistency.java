package nz.ac.wgtn.yamf.reporting.audit;

import nz.ac.wgtn.yamf.MarkingResultRecord;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Check whether the names of all marked tasks is consistent across a list of tasks for several submissions.
 * @author jens dietrich
 */
public class CheckTasksMarkedConsistency implements AuditRule {

    @Override
    public List<Result> apply(@Nonnull List<List<MarkingResultRecord>> allResults) {
        List<Result> auditResults = new ArrayList<>();
        if (allResults.size()==0) {
            auditResults.add(new Result(Status.ERROR,"no submissions found"));
        }
        else {
            List<MarkingResultRecord> firstRow = allResults.get(0);
            List<String> taskNames = firstRow.stream().map(record -> record.getName()).collect(Collectors.toList());
            int taskCount = firstRow.size();
            for (int i=1;i<allResults.size();i++) {
                List<MarkingResultRecord> nextRow = allResults.get(i);
                if (nextRow.size()!=taskCount) {
                    auditResults.add(new Result(Status.ERROR,"inconsistent task counts across submissions: " + taskCount + " task(s) marked for submission 1 but " + nextRow.size() + " task(s) marked for submission " + (i+1)));
                }
                else {
                    List<String> names = nextRow.stream().map(record -> record.getName()).collect(Collectors.toList());
                    for (int j = 0; j < names.size(); j++) {
                        if (!taskNames.get(j).equals(names.get(j))) {
                            auditResults.add(new Result(Status.ERROR, "task names for task " + (j + 1) + " mismatch between submissions 1 and " + (i + 1)));
                        }
                    }
                }
            }
        }
        return auditResults;
    }

    @Override
    public String getName() {
        return "check tasks names marked across a set of submissions for consistency";
    }
}
