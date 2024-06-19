package org.forms;

import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;

public class WordFile {
    private final Report report;
    private final String folderName;
    private XWPFTable table;
    private String sizeCell1 = "70%";
    private String sizeCell2 = "30%";

    public WordFile(Report report, String folderName) {
        this.report = report;
        this.folderName = folderName;

    }

    public void createFile() {
        String textFileName = report.getAuto() + " "
                + report.getSerialNumber() + "_"
                + report.getHouseNumber() +
                ".docx";
        String fileName = folderName + "/Отчет " + textFileName;
      //  String newFileName = folderName + "/Новый отчет " + textFileName;

        File file;
        file = new File(fileName);
//        if (file.exists()) {
//            file = new File(newFileName);
//        }

        Class<?> myClass = Report.class;
        Field[] fields = myClass.getDeclaredFields();
        int numberOfFields = fields.length;
        int countRow = report.getAnswer().size() + numberOfFields + 2;// +2 - plus two row


        XWPFDocument document = new XWPFDocument();
        table = document.createTable(countRow, 2);
        table.setWidth("100%");
        XWPFTableRow inputDateRow = table.getRow(0);
        XWPFTableCell inputDateCell1 = inputDateRow.getCell(0);
        XWPFTableCell inputDateCell2 = inputDateRow.getCell(1);
        inputDateCell1.setColor("BC8F8F");
        inputDateCell1.setText("Данные инспекции");

        XWPFParagraph p1 = inputDateCell1.getParagraphs().getFirst();
        p1.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun r1 = p1.createRun();
        p1.setVerticalAlignment(TextAlignment.CENTER);
        r1.setColor("FFFF00");
        r1.setFontSize(40);
        r1.setBold(true);

        inputDateCell1.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
        inputDateCell2.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);




           p1.addRun(r1);
        for (int i = 1; i <= numberOfFields; i++) {
            XWPFTableCell cell1 = table.getRow(i).getCell(0);
            XWPFTableCell cell2 = table.getRow(i).getCell(1);
            cell1.setWidth(sizeCell1);
            cell2.setWidth(sizeCell2);

            switch (i) {
                case 1 -> {
                    cell1.setText("ID");
                    cell2.setText(report.getId());
                }
                case 2 -> {
                    cell1.setText("Время создания");
                    cell2.setText(report.getDate());
                }
                case 3 -> {
                    cell1.setText("Заказчик");
                    cell2.setText(report.getClient());
                }
                case 4 -> {
                    cell1.setText("Машина");
                    cell2.setText(report.getAuto());
                }
                case 5 -> {
                    cell1.setText("Серийный номер");
                    cell2.setText(report.getSerialNumber());
                }
                case 6 -> {
                    cell1.setText("Хозяйственный номер");
                    cell2.setText(report.getHouseNumber());
                }
                case 7 -> {
                    cell1.setText("Наработка");
                    cell2.setText(report.getWorkHours());
                }
                case 8 -> {
                    cell1.setText("Машина чистая?");
                    cell2.setText(report.getClear());
                }
                case 9 -> {
                    cell1.setText("Исполнитель");
                    cell2.setText(report.getUserReport());
                }
            }
        }

        XWPFTableRow inputResultRow = table.getRow(10);
        XWPFTableCell inputResultCell1 = inputResultRow.getCell(0);
        XWPFTableCell inputResultCell2 = inputResultRow.getCell(1);
        inputResultCell1.setColor("BC8F8F");
        inputResultCell1.setText("Результат инспекции");
        inputResultCell1.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
        inputResultCell2.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);

        int row = 11;
        Map<String, String> tempMap = report.getAnswer();
        for (String key : tempMap.keySet()){
            setDataRow(key, tempMap.get(key), row);
            row++;
        }


            //   table.setRowBandSize(50);


            table.setInsideVBorder(XWPFTable.XWPFBorderType.SINGLE, 10, 0, "000000");
        table.setInsideHBorder(XWPFTable.XWPFBorderType.SINGLE, 10, 0, "000000");

        //   cell2.getParagraphs().getFirst().setVerticalAlignment(TextAlignment.CENTER);


        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            if (file.exists()) file.delete();


            document.write(outputStream);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void setDataRow(String firstText, String secondText, int numberRow) {
        XWPFTableCell cell1 = table.getRow(numberRow).getCell(0);
        XWPFTableCell cell2 = table.getRow(numberRow).getCell(1);
        cell1.setWidth(sizeCell1);
        cell2.setWidth(sizeCell2);
        cell1.setText(firstText);
        cell2.setText(secondText);


        switch (secondText) {
            case "ОК" -> {
                cell1.setColor("90EE90");
                cell2.setColor("90EE90");
            }
            case "Нет осмотра" -> {
                cell1.setColor("ADD8E6");
                cell2.setColor("ADD8E6");
            }
            default -> {
                cell1.setColor("FFDAB9");
                cell2.setColor("FFDAB9");
            }
        }
    }
}
