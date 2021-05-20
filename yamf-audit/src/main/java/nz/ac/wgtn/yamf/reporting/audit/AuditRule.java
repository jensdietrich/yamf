package nz.ac.wgtn.yamf.reporting.audit;

import nz.ac.wgtn.yamf.MarkingResultRecord;
import javax.annotation.Nonnull;
import java.util.List;

/**
 * Audit rule abstraction.
 * @author jens dietrich
 */
public interface AuditRule {
    enum Status {ERROR, WARN}
    class Result {
        public Result(Status status,String details) {
            this.status = status;
            this.details = details;
        }
        public String details = null;
        public Status status = null;
    }

    String getName();

    List<Result> apply (@Nonnull List<List<MarkingResultRecord>> allResults);

}
