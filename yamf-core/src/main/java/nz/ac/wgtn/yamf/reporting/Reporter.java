package nz.ac.wgtn.yamf.reporting;

import nz.ac.wgtn.yamf.MarkingResultRecord;

import java.util.List;

/**
 * Reporter interface.
 * @author jens dietrich
 */
public interface Reporter {
    /**
     * Generate the report.
     * @param results
     */
    void generateReport (List<MarkingResultRecord> results);
}
