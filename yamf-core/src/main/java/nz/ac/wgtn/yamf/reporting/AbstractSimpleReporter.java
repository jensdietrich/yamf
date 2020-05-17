package nz.ac.wgtn.yamf.reporting;

import nz.ac.wgtn.yamf.MarkingResultRecord;

import java.io.PrintStream;
import java.util.List;
import java.util.function.Predicate;

/**
 * Very simple plain file reporter.
 * @author jens dietrich
 */
public abstract class AbstractSimpleReporter implements Reporter {

    protected boolean reportFailureAndErrorDetails = false;
    protected Predicate<StackTraceElement> stacktraceElementFilter = StackTraceFilters.DEFAULT;


    public AbstractSimpleReporter(boolean reportFailureAndErrorDetails) {
        this.reportFailureAndErrorDetails = reportFailureAndErrorDetails;
    }

    public boolean isReportFailureAndErrorDetails() {
        return reportFailureAndErrorDetails;
    }

    public void setReportFailureAndErrorDetails(boolean reportFailureAndErrorDetails) {
        this.reportFailureAndErrorDetails = reportFailureAndErrorDetails;
    }

    public Predicate<StackTraceElement> getStacktraceElementFilter() {
        return stacktraceElementFilter;
    }

    public void setStacktraceElementFilter(Predicate<StackTraceElement> stacktraceElementFilter) {
        this.stacktraceElementFilter = stacktraceElementFilter;
    }

    protected void export(List<MarkingResultRecord> results, PrintStream out) {

        double marks = 0.0;
        double maxMark = 0.0;
        for (MarkingResultRecord record:results) {

            marks = marks + record.getMark();
            maxMark = maxMark + record.getMaxMark();

            out.print(record.getName());
            out.print("\t");
            out.print(record.getMark() + " / " + record.getMaxMark());
            out.print("\t");
            out.print(record.getTestIdentifier());
            out.print("\t");

            if (record.isManualMarkingRequired()) {
                out.print("Manual marking is required !!!");
            }
            else {
                out.print(record.getResultStatus());

                if (reportFailureAndErrorDetails) {
                    if (record.hasThrowable()) {
                        out.println();
                        out.println("\tException detailed message:");
                        out.println(record.getThrowable().getMessage());
                        out.println();
                        out.println("\tException stacktrace:");
                        for (StackTraceElement stackTraceElement : record.getThrowable().getStackTrace()) {
                            if (stacktraceElementFilter.test(stackTraceElement)) {
                                out.println("\t" + stackTraceElement);
                            }
                        }
                    }
                    out.println();
                }
            }

            out.println();
        }
        out.println();
        out.println("SUMMARY: " + marks + " / " + maxMark);

        out.println("====================================");
    }
}
