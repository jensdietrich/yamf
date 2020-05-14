package nz.ac.vuw.yamf.reporting;

import nz.ac.vuw.yamf.MarkingResultRecord;
import java.util.List;

/**
 * Reporter interface.
 * @author jens dietrich
 */
public interface Reporter {
    void generateReport (List<MarkingResultRecord> results);
}
