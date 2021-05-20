package nz.ac.wgtn.yamf.reporting.audit;

import com.google.common.base.Preconditions;
import nz.ac.wgtn.yamf.MarkingResultRecord;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Check for dependencies between tasks -- i.e. if  task1 fails (=zero marks) for more than $threshold submissions , then some subsequent  task2
 * always fails. This can point to missing assumptions in task2, leading to penalising the same issue twice.
 * @author jens dietrich
 */
public class CheckDependenciesBetweenTasks implements AuditRule {

    private int threshold = 5;

    public CheckDependenciesBetweenTasks() {
        this.setThreshold(5);
    }

    public CheckDependenciesBetweenTasks(int threshold) {
        this.setThreshold(threshold);
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        Preconditions.checkArgument(threshold>=0);
        this.threshold = threshold;
    }

    @Override
    public List<Issue>  apply(@Nonnull List<List<MarkingResultRecord>> allResults) {
        List<Issue> auditResults = new ArrayList<>();
        if (allResults.size()==0) {
            auditResults.add(new Issue(Status.ERROR,"no submissions found"));
        }
        else {
            int taskCount = allResults.get(0).size();
            List<String> taskNames = allResults.get(0).stream().map(record -> record.getName()).collect(Collectors.toList());

            List<Set<Integer>> failingSubmissionsByTask = new ArrayList<>();
            for (int i = 0; i < taskCount; i++) {
                failingSubmissionsByTask.add(new HashSet<>());
            }

            // collect data
            for (int submissionId = 0; submissionId < allResults.size(); submissionId++) {
                List<MarkingResultRecord> results = allResults.get(submissionId);
                for (int taskId = 0; taskId < results.size(); taskId++) {
                    MarkingResultRecord record = results.get(taskId);
                    if (!record.isManualMarkingRequired() && record.getMark() == 0) {
                        failingSubmissionsByTask.get(taskId).add(submissionId);
                    }
                }
            }

            // detect
            for (int i=0; i<taskCount;i++) {
                Set<Integer> submissionIdsFailingForTask = failingSubmissionsByTask.get(i);
                if (submissionIdsFailingForTask.size() >= threshold) {
                    for (int j = i + 1; j < taskCount; j++) {
                        Set<Integer> submissionIdsFailingForSubsequentTask = failingSubmissionsByTask.get(j);
                        if (submissionIdsFailingForSubsequentTask.containsAll(submissionIdsFailingForTask)) {
                            auditResults.add(new Issue(Status.WARN, "possible dependency detected between tasks \"" + taskNames.get(i) + "\" and \"" + taskNames.get(j) + "\""));
                        }
                    }
                }
            }
        }

        return auditResults;
    }

    @Override
    public String getName() {
        return "check whether more than " + threshold + "% of submissions get full marks for some task";
    }
}
