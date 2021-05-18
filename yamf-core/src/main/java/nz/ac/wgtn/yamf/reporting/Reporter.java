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
}
