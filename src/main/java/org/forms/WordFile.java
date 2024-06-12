package org.forms;

import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class WordFile {
    private final Report report;
    private final String folderName;
    public WordFile (Report report, String folderName){
        this.report = report;
        this.folderName = folderName;

    }
    public void createFile(){


//        File file = new File(folderName);
//        XWPFDocument document = new XWPFDocument();
//        XWPFTable table1 = document.createTable(1, 2);
//        table1.setWidth("100%");
//        XWPFTableRow row = table1.getRow(0);
//
//        XWPFTableCell t1r1cell1 = row.getCell(0);
//
//        XWPFTableCell t1r1cell2 = row.getCell(1);
//
//        t1r1cell1.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
//        t1r1cell2.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
//
//
//
//        XWPFParagraph p1 = table1.getRow(0).getCell(0).getParagraphs().getFirst();
//        p1.setAlignment(ParagraphAlignment.CENTER);
//
//        XWPFRun r1 = p1.createRun();
//        p1.setVerticalAlignment(TextAlignment.CENTER);
//        r1.setColor("FFFF00");
//        r1.setText("¬ходные данные");
//        r1.setFontSize(40);
//        r1.setBold(true);
//
//
//
//        //   p1.addRun(r1);
//
//
//        t1r1cell1.setColor("BC8F8F");
//        //  row.setHeight(100);
//        //table1.setInsideVBorder(XWPFTable.XWPFBorderType.DASHED, 10, 20, "00FF00");
//
//
//        XWPFTable table = document.createTable(2, 2);
//        table.setWidth("100%");
//
//        XWPFTableRow row1 = table.getRow(0);
//        XWPFTableCell r1Cell1 = row1.getCell(0);
//        r1Cell1.setWidth("70%");
//        XWPFTableCell r1Cell2 = row1.getCell(1);
//        r1Cell2.setWidth("30%");
//
//        r1Cell1.setText("r1Cell1");
//        //   table.setRowBandSize(50);
//        r1Cell2.setText("r1Cell2");
//        XWPFTableCell r2Cell1 = table.getRow(1).getCell(0);
//        XWPFTableCell r2Cell2 = table.getRow(1).getCell(1);
//        r2Cell1.setText("r2Cell1");
//        r2Cell2.setText("r2Cell2");
//
//
//        table.setInsideVBorder(XWPFTable.XWPFBorderType.DASHED, 20, 30, "00FF00");
//        table.setInsideHBorder(XWPFTable.XWPFBorderType.DASHED, 10, 20, "00FF00");
//        XWPFTableRow row2 = table.createRow();
//        XWPFTableCell cell1 = row2.getCell(0);
//        XWPFTableCell cell2 = row2.getCell(1);
//        cell1.setText("cell1");
//
//        cell2.setText("cell2");
//        cell2.getParagraphs().getFirst().setVerticalAlignment(TextAlignment.CENTER);
//        cell1.setColor("FFFFE0");
//        cell2.setColor("E6E6FA");
//
//
//        try (FileOutputStream outputStream = new FileOutputStream(file)) {
//            if (file.exists()) file.delete();
//
//
//            document.write(outputStream);
//
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

    }
}
