package nz.ac.wgtn.yamf.reporting;


import nz.ac.wgtn.yamf.MarkingResultRecord;

import java.io.*;
import java.util.List;
import java.util.function.Function;

/**
 * Reporter that creates a CSV file with summaries over multiple projects marked.
 * It is useful to produce this in addition to individual files in order to see patterns
 * that could point to particular issues in marking scripts.
 * @author jens dietrich
 */
public class CSVSummaryReporter implements Reporter  {

    public static final char CSV_SEP = '\t';
    private boolean initialized = false;
    private File file = null;
    private Function<String,String> name2headerFunction = name -> name;

    public CSVSummaryReporter(File file) {
        this.file = file;
    }

    public CSVSummaryReporter(File file, Function<String, String> name2headerFunction) {
        this.file = file;
        this.name2headerFunction = name2headerFunction;
    }

    @Override
    public void generateReport(File submission, List<MarkingResultRecord> results) {
        try (PrintStream out = new PrintStream(new FileOutputStream(file,initialized))) {
            if (!initialized) {
                // write header
                out.print("submission");
                for (MarkingResultRecord record:results) {
                    out.print(CSV_SEP);
                    out.print(name2headerFunction.apply(record.getName()));
                }
                out.println();

                out.print("<max marks>");
                for (MarkingResultRecord record:results) {
                    out.print(CSV_SEP);
                    out.print(record.getMaxMark());
                }
                out.println();

                initialized = true;
            }

            // data row
            out.print(submission.getName());
            for (MarkingResultRecord record:results) {
                out.print(CSV_SEP);
                out.print(record.getMark());
            }
            out.println();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
