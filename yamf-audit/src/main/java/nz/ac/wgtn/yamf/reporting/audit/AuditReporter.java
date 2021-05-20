package nz.ac.wgtn.yamf.reporting.audit;

import com.google.common.collect.Lists;
import nz.ac.wgtn.yamf.MarkingResultRecord;
import nz.ac.wgtn.yamf.reporting.Reporter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Reporter that creates a report summarising some patterns found in test results.
 * @author jens dietrich
 */
public class AuditReporter implements Reporter {

    private File file = null;
    private List<List<MarkingResultRecord>> allResults = new ArrayList<>();
    private List<AuditRule> rules = Lists.newArrayList(
        new CheckTasksMarkedConsistency(),
        new CheckTooManyFullMarksForSomeTasks(90),
        new CheckTooManyZeroMarksForSomeTasks(90)
    );

    public AuditReporter(File file) {
        this.file = file;
    }

    @Override
    public void generateReport(File file, List<MarkingResultRecord> list) {
        this.allResults.add(list);
    }

    @Override
    public void finish() {
        try (PrintWriter out = new PrintWriter(new FileWriter(this.file))) {
            for (AuditRule rule:rules) {
                out.println("Applying rule: " + rule.getName());
                out.println();
                List<AuditRule.Result> auditResults = rule.apply(this.allResults);
                for (AuditRule.Result auditResult:auditResults) {
                    out.print("\t");
                    out.print(auditResult.status);
                    out.print(" - ");
                    out.println(auditResult.details);
                }
                out.println();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
