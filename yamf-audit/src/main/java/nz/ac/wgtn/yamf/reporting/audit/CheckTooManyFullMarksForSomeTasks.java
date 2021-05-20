package nz.ac.wgtn.yamf.reporting.audit;

import com.google.common.base.Preconditions;
import nz.ac.wgtn.yamf.MarkingResultRecord;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Go through all marks for a given task, and report if more than a certain percentage of submissions get full marks
 * for this task. If so, report.
 * The threshold must be between 0 and 100 inclusive.
 * @author jens dietrich
 */
public class CheckTooManyFullMarksForSomeTasks implements AuditRule {

    private double threshold = 100;

    public CheckTooManyFullMarksForSomeTasks() {
        this.setThreshold(100);
    }

    public CheckTooManyFullMarksForSomeTasks(double threshold) {
        this.setThreshold(threshold);
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        Preconditions.checkArgument(threshold<=100);
        Preconditions.checkArgument(threshold>=0);
        this.threshold = threshold;
    }

    @Override
    public List<Result>  apply(@Nonnull List<List<MarkingResultRecord>> allResults) {
        List<Result> auditResults = new ArrayList<>();
        if (allResults.size()==0) {
            auditResults.add(new Result(Status.ERROR,"no submissions found"));
        }
        else {
            // init
            List<MarkingResultRecord> firstRow = allResults.get(0);
            List<String> taskNames = firstRow.stream().map(record -> record.getName()).collect(Collectors.toList());
            int taskCount = firstRow.size();
            int[] successCounts = new int[taskCount];
            for (int i=0;i<taskCount;i++) {
                successCounts[i]=0;
            }

            // collect
            for (List<MarkingResultRecord> results:allResults) {
                for (int i=0;i<results.size();i++) {
                    MarkingResultRecord record = results.get(i);
                    if (record.getMark()==record.getMaxMark()) {
                        successCounts[i] = successCounts[i]+1;
                    }
                }
            }

            // detect and report
            for (int i=0;i<successCounts.length;i++) {
                int count = successCounts[i];
                if (100*((double)count)/((double)allResults.size()) >= threshold) {
                    auditResults.add(new Result(Status.WARN,"full marks allocated for " + ": " + count + " / " + allResults.size() + " submissions for task " +  taskNames.get(i)));
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
