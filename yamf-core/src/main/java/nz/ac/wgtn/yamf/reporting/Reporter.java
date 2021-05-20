package nz.ac.wgtn.yamf.reporting;

import nz.ac.wgtn.yamf.MarkingResultRecord;

import java.io.File;
import java.util.List;

/**
 * Reporter interface.
 * @author jens dietrich
 */
public interface Reporter {
    /**
     * Generate the report.
     * @param submission
     * @param results
     */
    void generateReport (File submission, List<MarkingResultRecord> results);

    /**
     * Called on each reporter after all results where processed.
     * A typical use case is to accumulate all results using a single reporter
     * when generateReport is called, and the write the file at the end.
     */
    default void finish(){}
}
