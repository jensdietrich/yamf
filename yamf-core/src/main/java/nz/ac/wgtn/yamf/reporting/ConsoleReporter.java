package nz.ac.wgtn.yamf.reporting;

import nz.ac.wgtn.yamf.MarkingResultRecord;

import java.io.File;
import java.io.PrintStream;
import java.util.List;

/**
 * Very simple reporter.
 * @author jens dietrich
 */
public class ConsoleReporter extends AbstractSimpleReporter {

    public ConsoleReporter() {
        super(true);
    }

    public ConsoleReporter(boolean reportFailureAndErrorDetails) {
        super(reportFailureAndErrorDetails);
    }

    @Override
    public void generateReport(File submission, List<MarkingResultRecord> results) {
        PrintStream out = System.out;
        out.println("====== MARKING SCRIPT RESULTS ======");
        export(results,out);
        System.out.println("====================================");
    }
}
