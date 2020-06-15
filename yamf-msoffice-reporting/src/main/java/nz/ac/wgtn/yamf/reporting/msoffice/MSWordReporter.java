package nz.ac.wgtn.yamf.reporting.msoffice;

import nz.ac.wgtn.yamf.MarkingResultRecord;
import nz.ac.wgtn.yamf.reporting.Reporter;
import nz.ac.wgtn.yamf.reporting.StackTraceFilters;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

/**
 * Reporter producing MS Word files that are editable.
 * @author jens dietrich
 */
public class MSWordReporter implements Reporter {
    private File file = null;
    private boolean reportFailureAndErrorDetails = false;
    private Predicate<StackTraceElement> stacktraceElementFilter = StackTraceFilters.DEFAULT;

    public MSWordReporter(boolean reportFailureAndErrorDetails,File file) {
        this.file = file;
        this.reportFailureAndErrorDetails = reportFailureAndErrorDetails;
    }

    public MSWordReporter(boolean reportFailureAndErrorDetails,String fileName) {
        this.file = new File(fileName);
        this.reportFailureAndErrorDetails = reportFailureAndErrorDetails;
    }

    public MSWordReporter(File file) {
        this(false,file);
    }

    public MSWordReporter(String fileName) {
        this(false,fileName);
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

    @Override
    public void generateReport(List<MarkingResultRecord> markingResultRecords) {
        XWPFDocument document = new XWPFDocument();
        addTextBlock(document, true, 16, "Marking Report");
        space(document);

        double marks = 0;
        double maxMarks = 0;

        for (MarkingResultRecord record:markingResultRecords) {

            marks = marks + record.getMark();
            maxMarks = maxMarks + record.getMaxMark();
            String colour = (record.isManualMarkingRequired() || record.isAborted()) ? "0000FF" :
                record.isSuccess() ? "000000" : "FF0000";

            addTextBlock(document, true, colour, 12, record.getName() + " -- " + record.getMark() + " / " + record.getMaxMark() + " marks");
            space(document);
            if (record.isManualMarkingRequired()) {
                String message = "#TODO - manual marking is required here.";
                String instructions = record.getManualMarkingInstructions();
                if (instructions!=null && !instructions.isEmpty()) {
                    message = message + " Instructions: \"" + instructions + '\"';
                }
                addTextBlock(document, false, colour, 12, message);
                space(document);
            }
            else {
                String message = record.isAborted() ? "#TODO - the automated checks could not be performed, manual marking is required." : "check result: " + record.getResultStatus();
                addTextBlock(document, false, colour, 12, message);
                space(document);
                if (!record.isSuccess() && record.getThrowable() != null) {
                    // addTextBlock(document, false, colour, 12, "Details:" );
                    space(document);
                    if (record.getThrowable().getMessage()!=null) {
                        for (String messageLine : record.getThrowable().getMessage().split("\n")) {
                            addTextBlock(document, false, colour, 12, messageLine);
                        }
                        space(document);
                    }
                    // stack trace
                    if (this.reportFailureAndErrorDetails) {
                        for (StackTraceElement element : record.getThrowable().getStackTrace()) {
                            if (stacktraceElementFilter.test(element)) {
                                addTextBlock(document, false, colour, 10, element.toString());
                            }
                        }
                    }
                }
            }
            space(document);
        }

        // summary

        addTextBlock(document, true, 14, "Summary");
        space(document);
        addTextBlock(document, false, 12, "" + marks + " / " + maxMarks);


        try (FileOutputStream out = new FileOutputStream(file)) {
            document.write(out);
            document.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void addTextBlock(XWPFDocument document, boolean bold,String colour, int fontSize, String text) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun paragraphRun = paragraph.createRun();
        paragraphRun.setBold(bold);
        paragraphRun.setFontFamily("Courier");
        paragraphRun.setColor(colour);
        paragraphRun.setFontSize(fontSize);
        paragraphRun.setText(text);
    }

    private void addTextBlock(XWPFDocument document, boolean bold, int fontSize, String text) {
        addTextBlock(document,bold,"000000",fontSize,text);
    }

    private void space(XWPFDocument document) {
        document.createParagraph();
    }
}
