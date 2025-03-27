package org.forms;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.awt.*;
import java.io.*;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class WordFile {
    private final String folderName;
    private XWPFDocument document;
    private int sizePage = 700;
    private final String sizeCell1 = "60%";
    private final String sizeCell2 = "40%";
    private final Map<String, String> inputData = new LinkedHashMap<>();
    private final Map<String, String> preData = new LinkedHashMap<>();
    private final Map<String, String> resultData = new LinkedHashMap<>();
    private final Map<String, String> defectData = new LinkedHashMap<>();


    public WordFile(Map<String, String> mapAnswer, String folderName) {
        setMaps(mapAnswer);
        this.folderName = folderName;

    }

    public void createFile() {
        String textFileName = inputData.get("Машина") + " "
                + inputData.get("Серийный номер") + "_"
                + inputData.get("Хозяйственный номер") +
                ".docx";
        String fileName = folderName + "/Отчет " + textFileName;
        String newFileName = folderName + "/Отчет новый " + textFileName;

        File file;
        file = new File(fileName);
        if (file.exists()) {
            file = new File(newFileName);
        }

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            document = new XWPFDocument();

            setHeaderFooter();

            document.getDocument().getBody().addNewSectPr().addNewPgMar().setLeft(BigInteger.valueOf(sizePage));
            document.getDocument().getBody().addNewSectPr().addNewPgMar().setRight(BigInteger.valueOf(sizePage));
            document.getDocument().getBody().addNewSectPr().addNewPgMar().setTop(BigInteger.valueOf(sizePage));
            document.getDocument().getBody().addNewSectPr().addNewPgMar().setBottom(BigInteger.valueOf(sizePage));

            setHead("Данные инспекции");
            setInputDataTable();

            setHead("Предварительный осмотр");
            setPreDataTable();

            setHead("Неисправности");
            setDefectDataTable();

            setHead("Без замечаний");
            setResultDataTable();

            document.createParagraph().setPageBreak(true);
            setHead("Приложение 1");
            setApposition();

            document.write(outputStream);

            if (file.exists()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(file);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void setTableBorders(XWPFTable table) {
        table.setInsideVBorder(XWPFTable.XWPFBorderType.SINGLE, 10, 0, "000000");
        table.setInsideHBorder(XWPFTable.XWPFBorderType.SINGLE, 10, 0, "000000");
    }

    private void setHead(String text) {
        String brown = "4B2942";
        String yellow = "F2AF00";
        XWPFTable t = document.createTable(1, 2);
        t.setWidth("100%");
        setTableBorders(t);
        XWPFTableCell cell1 = t.getRow(0).getCell(0);
        XWPFTableCell cell2 = t.getRow(0).getCell(1);
        cell1.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
        cell2.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
        XWPFParagraph paragraph = cell1.getParagraphs().getFirst();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setColor(yellow);
        run.setFontSize(20);
        run.setBold(true);
        cell1.setColor(brown);
    }

    private void setInputDataTable() {
        int countRows = inputData.size();
        XWPFTable tableData = document.createTable(countRows, 2);
        tableData.setWidth("100%");
        setTableBorders(tableData);
        List<String> listQuestion = new ArrayList<>(inputData.keySet());
        for (int i = 0; i < countRows; i++) {
            XWPFTableCell cell1 = tableData.getRow(i).getCell(0);
            XWPFTableCell cell2 = tableData.getRow(i).getCell(1);
            cell1.setWidth(sizeCell1);
            cell2.setWidth(sizeCell2);
            XWPFParagraph paragraph = cell2.getParagraphs().getFirst();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            paragraph.setVerticalAlignment(TextAlignment.CENTER);
            String question = listQuestion.get(i);
            if (question.contains("Общий вид машины")) {
                cell1.setText(question);
                insertImage(cell1, cell2, inputData.get(question));
            } else {
                cell1.setText(question);
                cell2.setText(inputData.get(question));
            }
        }
    }

    private void setDataRow(XWPFTable table, String firstText, String secondText, int numberRow) {
        XWPFTableCell cell1 = table.getRow(numberRow).getCell(0);
        XWPFTableCell cell2 = table.getRow(numberRow).getCell(1);
        cell1.setText(firstText);
        cell1.setWidth(sizeCell1);
        cell2.setWidth(sizeCell2);
        cell2.setText(secondText);
        cell2.getParagraphs().getFirst().setAlignment(ParagraphAlignment.CENTER);
        if (secondText.equals("ОК")) {
            cell1.setColor("90EE90");
            cell2.setColor("90EE90");
        } else if (secondText.equals("Нет осмотра")) {
            cell1.setColor("ADD8E6");
            cell2.setColor("ADD8E6");
        } else {
            cell1.setColor("FFDAB9");
            cell2.setColor("FFDAB9");
        }
        if (secondText.contains(".jpeg")) {
            insertImage(cell1, cell2, secondText);
        }
        if (secondText.contains("Есть замечания")) {
            cell1.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
            cell2.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
            XWPFParagraph paragraph = cell1.getParagraphs().getFirst();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
        }
    }

    private void setHeaderFooter() {

        String logoFileName = "logo.png";
        String singFileName = "sing.png";
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
        XWPFHeaderFooterPolicy headerFooterPolicy = new XWPFHeaderFooterPolicy(document, sectPr);

        XWPFHeader header = headerFooterPolicy.createHeader(XWPFHeaderFooterPolicy.DEFAULT);
        XWPFParagraph paragraph = header.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun run = paragraph.createRun();
        try {
            run.addPicture(new FileInputStream(logoFileName), XWPFDocument.PICTURE_TYPE_PNG, logoFileName, Units.toEMU(140), Units.toEMU(30));
        } catch (InvalidFormatException | IOException e) {
            throw new RuntimeException(e);
        }

        XWPFFooter footer = headerFooterPolicy.createFooter(XWPFHeaderFooterPolicy.DEFAULT);
        paragraph = footer.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        run = paragraph.createRun();
        run.setText("Дата: " + dateFormat.format(new Date()));
        run.setText("                                     Технический специалист     ");
        try {
            run.addPicture(new FileInputStream(singFileName), XWPFDocument.PICTURE_TYPE_PNG, singFileName, Units.toEMU(30), Units.toEMU(30));
        } catch (InvalidFormatException | IOException e) {
            throw new RuntimeException(e);
        }
        run.setText("      Полевой А. В.");
    }

    private void insertImage(XWPFTableCell cell1, XWPFTableCell cell2, String text) {
        cell1.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
        cell2.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);

        XWPFParagraph paragraph = cell1.getParagraphs().getFirst();
        paragraph.setVerticalAlignment(TextAlignment.TOP);
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun run = paragraph.createRun();
        run.addBreak();
        String[] fileName = text.split(", ");
        // настройка размера изображения
        int sizeImage;
        if (fileName.length == 1 ||fileName.length == 2) {
            sizeImage = sizePage;
        } else if (fileName.length < 5) {
            sizeImage = 1000;
        } else {
            sizeImage = 1150;
        }

        int width = (int) ((sizeImage / 1.3) / fileName.length);
        int height = (int) ((sizeImage / 1.8) / fileName.length);
        for (String s : fileName) {
            try {
                run.addPicture(new FileInputStream(s), XWPFDocument.PICTURE_TYPE_JPEG, s,
                        Units.toEMU(width), Units.toEMU(height));
            } catch (InvalidFormatException | IOException e) {
                System.out.println("File image " + s);
                throw new RuntimeException(e);
            }
        }
    }

    private void setResultDataTable() {
        int countRows = resultData.size();
        List<String> listQuestion = new ArrayList<>(resultData.keySet());
        XWPFTable tableAnswer = document.createTable(countRows, 2);
        for (int i = 0; i < countRows; i++) {
            setDataRow(tableAnswer, listQuestion.get(i), resultData.get(listQuestion.get(i)), i);
        }
    }

    private void setPreDataTable() {
        int countRows = preData.size();
        List<String> listQuestion = new ArrayList<>(preData.keySet());
        XWPFTable tableAnswer = document.createTable(countRows, 2);
        for (int i = 0; i < countRows; i++) {
            XWPFTableCell cell1 = tableAnswer.getRow(i).getCell(0);
            XWPFTableCell cell2 = tableAnswer.getRow(i).getCell(1);
            cell1.setWidth(sizeCell1);
            cell2.setWidth(sizeCell2);
            XWPFParagraph paragraph = cell2.getParagraphs().getFirst();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            paragraph.setVerticalAlignment(TextAlignment.CENTER);
            String question = listQuestion.get(i);
            if (question.contains("фото")) {
                cell1.setText(question);
                insertImage(cell1, cell2, preData.get(question));
            } else {
                cell1.setText(question);
                cell2.setText(preData.get(question));
            }
        }
        setOffer(null, null);
    }

    private void setDefectDataTable() {
        int countRows = defectData.size();
        List<String> listQuestion = new ArrayList<>(defectData.keySet());
        int number;
        int oldNumber;
        String recommendation = "";
        String defect = "";
        for (int i = 0; i < countRows; i++) {
            number = Integer.parseInt(listQuestion.get(i).substring(0, 3));
            if (i == 0) {
                XWPFTable tableAnswer = document.createTable(1, 2);
                setDataRow(tableAnswer, listQuestion.get(i), defectData.get(listQuestion.get(i)), 0);
            } else {
                oldNumber = Integer.parseInt(listQuestion.get(i - 1).substring(0, 3));
                if (number == oldNumber) {
                    if (listQuestion.get(i).contains("Неисправность")) {
                        defect = defectData.get(listQuestion.get(i));
                    }
                    if (listQuestion.get(i).contains("Список необходимых з.ч.")) {
                        recommendation = defectData.get(listQuestion.get(i));
                    }
                    XWPFTable tableAnswer = document.createTable(1, 2);
                    setDataRow(tableAnswer, listQuestion.get(i), defectData.get(listQuestion.get(i)), 0);
                } else {
                    setOffer(defect, recommendation);
                    XWPFTable tableAnswer = document.createTable(1, 2);
                    setDataRow(tableAnswer, listQuestion.get(i), defectData.get(listQuestion.get(i)), 0);
                }
            }
        }
        setOffer(defect, recommendation);
    }

    private void setApposition() {
        XWPFRun run = document.createParagraph().createRun();
        run.addBreak();
        try {
            run.addPicture(new FileInputStream("rating.png"), XWPFDocument.PICTURE_TYPE_JPEG,
                    "rating.png", Units.toEMU(sizePage / 1.3), Units.toEMU(sizePage / 1.9));
        } catch (InvalidFormatException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setMaps(Map<String, String> mapAnswer) {
        for (String key : mapAnswer.keySet()) {
            String value = mapAnswer.get(key);
            try {
                int number = Integer.parseInt(key.substring(0, 1));
                if (number == 1) {
                    preData.put(key, value);
                } else {
                    if (key.contains("Неисправность")
                            || key.contains("Список необходимых з.ч.")
                            || key.contains("Фото неисправности")
                            || value.contains("Есть замечания")) {
                        defectData.put(key, value);
                    } else {
                        resultData.put(key, value);
                    }
                }
            } catch (Exception e) {
                if (key.contains("предложения")) {
                    preData.put(key, value);
                } else {
                    inputData.put(key, value);
                }
            }
        }
    }

    private void setOffer(String defect, String recommendation) {
        XWPFTable tableData = document.createTable(3, 2);
        XWPFTableCell cell1 = tableData.getRow(0).getCell(0);
        XWPFTableCell cell2 = tableData.getRow(0).getCell(1);
        cell1.setWidth(sizeCell1);
        cell2.setWidth(sizeCell2);
        cell1.setText("Рекомендации");
        cell2.setText(recommendation);
        XWPFTableCell cell3 = tableData.getRow(1).getCell(0);
        XWPFTableCell cell4 = tableData.getRow(1).getCell(1);
        XWPFTableCell cell5 = tableData.getRow(2).getCell(0);
        cell3.setWidth(sizeCell1);
        cell4.setWidth(sizeCell2);
        XWPFParagraph paragraph = cell3.getParagraphs().getFirst();
        paragraph.setVerticalAlignment(TextAlignment.CENTER);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFParagraph paragraph1 = cell4.getParagraphs().getFirst();
        paragraph1.setVerticalAlignment(TextAlignment.CENTER);
        paragraph1.setAlignment(ParagraphAlignment.CENTER);
        cell3.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        cell3.setText("Возможные последствия отказа");
        cell4.setText("Уровень последствий отказа (приложение 1)");
        cell3.setColor("FFFF00");
        cell4.setColor("FFFF00");
        cell5.setText(defect + " может привезти к ");
        document.createParagraph();
        XWPFRun run = document.createParagraph().createRun();
        run.setText("Коммерческое предложение:");
        run.addBreak();
        run.addBreak();
        run.setText("Согласовано, должность: __________________ Ф.И.О.: _____________________ Дата: __________");
    }
}
