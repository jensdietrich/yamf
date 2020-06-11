package nz.ac.wgtn.yamf.reporting.msoffice;

import nz.ac.wgtn.yamf.MarkingResultRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Reporter producing MS Excel files that are editable.
 * @author jens dietrich
 */
public class MSExcelReporter implements Reporter {
    private File file = null;
    private boolean reportFailureAndErrorDetails = false;
    private Predicate<StackTraceElement> stacktraceElementFilter = StackTraceFilters.DEFAULT;
    private static char[] ROW_NAMES =  "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public MSExcelReporter(boolean reportFailureAndErrorDetails, File file) {
        this.file = file;
        this.reportFailureAndErrorDetails = reportFailureAndErrorDetails;
    }

    public MSExcelReporter(boolean reportFailureAndErrorDetails, String fileName) {
        this.file = new File(fileName);
        this.reportFailureAndErrorDetails = reportFailureAndErrorDetails;
    }

    public MSExcelReporter(File file) {
        this(false,file);
    }

    public MSExcelReporter(String fileName) {
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

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("results");
        int rowCount = 0;

        // row 0 -- header
        String[] titles = new String[]{"task","status","marks","maxMarks","notes"};
        Row row = sheet.createRow(rowCount++);
        addHeaderCells(workbook,row,titles);

        double marks = 0;
        double maxMarks = 0;

        CellStyle styleL = createCellStyle(workbook,HorizontalAlignment.LEFT,false);
        CellStyle styleC = createCellStyle(workbook,HorizontalAlignment.CENTER,false);
        CellStyle styleR = createCellStyle(workbook,HorizontalAlignment.RIGHT,false);

        for (MarkingResultRecord record:markingResultRecords) {
            marks = marks + record.getMark();
            maxMarks = maxMarks + record.getMaxMark();
            int col = 0;

            row = sheet.createRow(rowCount++);
            addCell(row,col++,styleL,record.getName());

            String status = (record.isManualMarkingRequired() || record.isAborted()) ? "todo" :
                record.isSuccess() ? "ok" : "fail";
            addCell(row,col++,styleC,status);

            addCell(row,col++,styleR,record.getMark());
            addCell(row,col++,styleR,record.getMaxMark());
            addCell(row,col++,styleL,record.getManualMarkingInstructions());
        }

        // TODO summary

        styleC = createCellStyle(workbook,HorizontalAlignment.CENTER,true);
        styleR = createCellStyle(workbook,HorizontalAlignment.RIGHT,true);

        row = sheet.createRow(rowCount++);
        int col = 0;

        addCell(row,col++,styleR,"summary");
        addCell(row,col++,styleC,"");

        Cell cell = row.createCell(col++);
        String formula = IntStream.range(2,rowCount).mapToObj(i -> "C"+i).collect(Collectors.joining("+"));
        cell.setCellFormula(formula );  // =SUM(C2:C3)
        cell.setCellStyle(styleR);

        addCell(row,col++,styleC,"");
        addCell(row,col++,styleC,"");


        for (int i=0;i<titles.length;i++) {
            sheet.autoSizeColumn(i);
        }

        // write file
        try (FileOutputStream out = new FileOutputStream(file)) {
            workbook.write(out);
            workbook.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void addCell(Row row, int col, CellStyle style, String value) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void addCell(Row row, int col, CellStyle style, int value) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void addCell(Row row, int col, CellStyle style, double value) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void addHeaderCells(Workbook workbook,Row row,String... headers) {
        int colNum = 0;
        for (String header:headers) {
            Cell cell = row.createCell(colNum++);
            cell.setCellValue(header);
            cell.setCellStyle(createCellStyle(workbook,HorizontalAlignment.CENTER,true));
        }
    }


    private CellStyle createCellStyle(Workbook workbook,HorizontalAlignment hAlign, boolean highlight) {
        CellStyle dataStyle1 = workbook.createCellStyle();
        Font dataFont = workbook.createFont();
        dataFont.setColor(IndexedColors.BLACK.index);
        dataFont.setFontHeightInPoints((short) 12);
        if (highlight) {
            dataFont.setBold(false);
            dataStyle1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            dataStyle1.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        }
        dataStyle1.setFillBackgroundColor(IndexedColors.WHITE.index);
        dataStyle1.setFont(dataFont);
        dataStyle1.setVerticalAlignment(VerticalAlignment.CENTER);
        dataStyle1.setAlignment(hAlign);
        return dataStyle1;
    }
}
