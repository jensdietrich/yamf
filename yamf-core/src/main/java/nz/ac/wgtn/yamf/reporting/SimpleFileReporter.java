package nz.ac.wgtn.yamf.reporting;

import nz.ac.wgtn.yamf.MarkingResultRecord;

import java.io.*;
import java.util.List;

/**
 * Very simple reporter writing to text files.
 * @author jens dietrich
 */
public class SimpleFileReporter extends AbstractSimpleReporter {

    private File file = null;

    public SimpleFileReporter(boolean reportFailureAndErrorDetails,File file) {
        super(reportFailureAndErrorDetails);
        this.file = file;
    }

    public SimpleFileReporter(boolean reportFailureAndErrorDetails, String fileName) {
        super(reportFailureAndErrorDetails);
        this.file = new File(fileName);
    }

    public File getFile() {
        return file;
    }

    @Override
    public void generateReport(File submission, List<MarkingResultRecord> results) {
        try (PrintStream out = new PrintStream(new FileOutputStream(file))) {
            export(results,out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
