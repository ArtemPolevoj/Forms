package org.forms;


import org.apache.poi.common.usermodel.PictureType;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.wp.usermodel.HeaderFooterType;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class WordFile {
    private final Report report;
    private final String folderName;
    private XWPFDocument document;
    private XWPFTable table;
    private final String sizeCell1 = "70%";
    private final String sizeCell2 = "30%";
    private final int sizePage = 700;

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
        int countRow = report.getAnswer().size() + numberOfFields + 1;// +1 - plus row


        try {
            document = new XWPFDocument();
            setHeaderFooter();

            document.getDocument().getBody().addNewSectPr().addNewPgMar().setLeft(BigInteger.valueOf(sizePage));
            document.getDocument().getBody().addNewSectPr().addNewPgMar().setRight(BigInteger.valueOf(sizePage));
            document.getDocument().getBody().addNewSectPr().addNewPgMar().setTop(BigInteger.valueOf(sizePage));
            document.getDocument().getBody().addNewSectPr().addNewPgMar().setBottom(BigInteger.valueOf(sizePage));

            table = document.createTable(countRow, 2);
            table.setWidth("100%");
            table.setInsideVBorder(XWPFTable.XWPFBorderType.SINGLE, 10, 0, "000000");
            table.setInsideHBorder(XWPFTable.XWPFBorderType.SINGLE, 10, 0, "000000");

            XWPFTableRow inputDateRow = table.getRow(0);
            XWPFTableCell inputDateCell1 = inputDateRow.getCell(0);
            XWPFTableCell inputDateCell2 = inputDateRow.getCell(1);

            String inputData = "Данные инспекции";
            setMergeRow(inputDateCell1, inputDateCell2, inputData);

            for (int i = 1; i <= numberOfFields; i++) {
                XWPFTableCell cell1 = table.getRow(i).getCell(0);
                XWPFTableCell cell2 = table.getRow(i).getCell(1);
                cell1.setWidth(sizeCell1);
                cell2.setWidth(sizeCell2);
                XWPFParagraph paragraph = cell2.getParagraphs().getFirst();
                paragraph.setAlignment(ParagraphAlignment.CENTER);
                paragraph.setVerticalAlignment(TextAlignment.CENTER);

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
            String textResult = "Результат инспекции";
            setMergeRow(inputResultCell1, inputResultCell2, textResult);

            int row = 11;
            Map<String, String> tempMap = report.getAnswer();
            for (String key : tempMap.keySet()) {
                setDataRow(key, tempMap.get(key), row);
                row++;
            }





            FileOutputStream outputStream = new FileOutputStream(file);
            if (file.exists()) file.delete();


            document.write(outputStream);


        } catch (IOException | InvalidFormatException e) {
            throw new RuntimeException(e);
        }

    }

    private void setDataRow(String firstText, String secondText, int numberRow) throws IOException, InvalidFormatException {
        XWPFTableCell cell1 = table.getRow(numberRow).getCell(0);
        XWPFTableCell cell2 = table.getRow(numberRow).getCell(1);

        cell1.setWidth(sizeCell1);
        cell2.setWidth(sizeCell2);
        cell1.setText(firstText);
        cell2.setText(secondText);

        XWPFParagraph paragraph = cell2.getParagraphs().getFirst();
        paragraph.setAlignment(ParagraphAlignment.CENTER);


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
                if (firstText.contains("Общий вид")){
                    cell1.setColor("ADD8E6");
                   }else {
                    cell1.setColor("FFDAB9");
                    cell2.setColor("FFDAB9");
                }
            }
        }
        if (secondText.contains(".jpeg")){
            insertImage(cell1, cell2, secondText);
        }
    }

    private void setMergeRow(XWPFTableCell cell1, XWPFTableCell cell2, String text) {
        cell1.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
        cell2.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
        XWPFParagraph paragraph = cell1.getParagraphs().getFirst();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        paragraph.setVerticalAlignment(TextAlignment.AUTO);
        run.setText(text);
        run.setColor("FFFF00");
        run.setFontSize(20);
        run.setBold(true);
        cell1.setColor("BC8F8F");
    }

    private void setHeaderFooter() throws IOException, InvalidFormatException {

        String logoFileName = "logo.png";
        String singFileName = "sing.png";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
        XWPFHeaderFooterPolicy headerFooterPolicy = new XWPFHeaderFooterPolicy(document, sectPr);

        XWPFHeader header = headerFooterPolicy.createHeader(XWPFHeaderFooterPolicy.DEFAULT);
        XWPFParagraph paragraph = header.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun run = paragraph.createRun();
        run.addPicture(new FileInputStream(logoFileName), XWPFDocument.PICTURE_TYPE_PNG, logoFileName, Units.toEMU(140), Units.toEMU(30));

        XWPFFooter footer = headerFooterPolicy.createFooter(XWPFHeaderFooterPolicy.DEFAULT);
        paragraph = footer.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        run = paragraph.createRun();
        run.setText("Дата: " + dateFormat.format(new Date()));
        run.setText("                                     Технический специалист     ");
        run.addPicture(new FileInputStream(singFileName), XWPFDocument.PICTURE_TYPE_PNG, singFileName, Units.toEMU(30), Units.toEMU(30));
        run.setText("      Полевой А.В.");
    }
    private void insertImage(XWPFTableCell cell1, XWPFTableCell cell2, String text) throws IOException, InvalidFormatException {
        cell1.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
        cell2.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);

        XWPFParagraph paragraph = cell1.getParagraphs().getFirst();
        paragraph.setVerticalAlignment(TextAlignment.TOP);
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun run = paragraph.createRun();
        run.addBreak();

        String[] fileName = text.split(", ");
        int size = (int) ((sizePage/1.3)/ fileName.length);
        for (String s : fileName) {
            run.addPicture(new FileInputStream(s), XWPFDocument.PICTURE_TYPE_JPEG, s, Units.toEMU(size), Units.toEMU(size));
        }
    }
}
