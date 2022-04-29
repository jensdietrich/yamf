package nz.ac.wgtn.yamf.reporting.msoffice;

import nz.ac.wgtn.yamf.Attachment;
import nz.ac.wgtn.yamf.MarkingResultRecord;
import nz.ac.wgtn.yamf.reporting.Reporter;
import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Reporter producing MS Excel files that are editable.
 * @author jens dietrich
 */
public class MSExcelReporter extends AbstractReporter {
    private File file = null;

    private SpreadsheetVersion version = null;

    public MSExcelReporter(String fileName) {
        this.file = new File(fileName);
    }

    public MSExcelReporter(File file) {
        this.file = file;
    }

    @Override
    public void generateReport(File submission, List<MarkingResultRecord> markingResultRecords) {

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("results");
        int rowCount = 0;

        this.version = workbook.getSpreadsheetVersion();

        // row 0 -- header
        String[] titles = new String[]{"task","status","marks","maxMarks","details/logs","notes"};
        Row row = sheet.createRow(rowCount++);
        addHeaderCells(workbook,row,titles);

        double marks = 0;
        double maxMarks = 0;

        CellStyle styleL = createCellStyle(workbook,HorizontalAlignment.LEFT,false);
        CellStyle styleC = createCellStyle(workbook,HorizontalAlignment.CENTER,false);
        CellStyle styleR = createCellStyle(workbook,HorizontalAlignment.RIGHT,false);

        List<Attachment> attachments = new ArrayList<>();
        for (MarkingResultRecord record:markingResultRecords) {
            marks = marks + record.getMark();
            maxMarks = maxMarks + record.getMaxMark();
            int col = 0;

            row = sheet.createRow(rowCount++);
            addCell(row,col++,styleL,record.getName());

            String status = (record.isManualMarkingRequired() || record.isAborted()) ? "manual marking required" :
                record.isSuccess() ? "success" : "fail";
            addCell(row,col++,styleC,status);

            addCell(row,col++,styleR,record.getMark());
            addCell(row,col++,styleR,record.getMaxMark());

            // attachments
            Collection<Attachment>attachmentsOfThisRecord = record.getAttachments();
            List<String> attachmentNames = new ArrayList<>();
            for (Attachment attachment:attachmentsOfThisRecord) {
                attachments.add(attachment);
                attachmentNames.add("details-"+attachments.size());
            }
            String txt = attachmentNames.stream().collect(Collectors.joining(","));
            addCell(row,col++,styleL,txt);

            // notes
            String notes = "";
            if (record.isManualMarkingRequired()) {
                notes = record.getManualMarkingInstructions();
            }
            else if ((record.isFailed() || record.isAborted()) && record.getThrowable()!=null) {
                notes = record.getThrowable().getMessage();
            }
            addCell(row,col++,styleL,notes==null?"":notes);
            // next statement is necessary to retain previous statement in bytecode
            // this might be a case of erroneous compiler optimisation
            System.out.print("");

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

        cell = row.createCell(col++);
        formula = IntStream.range(2,rowCount).mapToObj(i -> "D"+i).collect(Collectors.joining("+"));
        if (formula.trim().length()>0) {
            cell.setCellFormula(formula);  // =SUM(C2:C3)
        }
        else {
            cell.setCellValue("n/a");
        }
        cell.setCellStyle(styleR);

        addCell(row,col++,styleC,"");
        addCell(row,col++,styleC,"");

        for (int i=0;i<titles.length;i++) {
            sheet.autoSizeColumn(i);
        }

        // display details in different sheets
        int attachmentCounter = 1;
        for (Attachment attachment:attachments) {
            XSSFSheet aSheet = workbook.createSheet("details-" + (attachmentCounter++));
            rowCount = 0;
            Row aRow = aSheet.createRow(rowCount++);
            Cell aCell = aRow.createCell(0);
            aCell.setCellValue(attachment.getName());
            aCell.setCellStyle(createCellStyle(workbook,HorizontalAlignment.LEFT,true));
            List<String> lines = loadContent(attachment);
            CellStyle style = createCellStyle(workbook,HorizontalAlignment.LEFT,false);
            for (String line:lines) {
                aRow = aSheet.createRow(rowCount++);
                aCell = aRow.createCell(0);
                aCell.setCellValue(sanitise(line));
                aCell.setCellStyle(style);
            }
            aSheet.autoSizeColumn(0);
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

    private List<String> loadContent(Attachment attachment) {
        return attachment.getContent();
    }

    private void addCell(Row row, int col, CellStyle style, String value) {

        // truncate if necessary in order to avoid IllegalArgumentException later

        value = sanitise(value);
        Cell cell = row.createCell(col);
        if(value.length() > this.version.getMaxTextLength()) {
            value = value.substring(0,this.version.getMaxTextLength() - 16) + " .. (truncated)";
        }
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
