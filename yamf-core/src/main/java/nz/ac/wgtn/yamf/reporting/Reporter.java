package nz.ac.wgtn.yamf.reporting;

import nz.ac.wgtn.yamf.MarkingResultRecord;

import java.util.List;

/**
 * Reporter interface.
 * @author jens dietrich
 */
public interface Reporter {
    void generateReport (List<MarkingResultRecord> results);
}
