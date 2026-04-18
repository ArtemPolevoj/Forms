package org.forms;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class WordFileUsual extends WordFile {
    private final Map<String, String> defectData = new LinkedHashMap<>();

    public WordFileUsual(Map<String, String> mapAnswer, String folderName) {
        setMaps(mapAnswer);
        this.folderName = folderName;

    }

    public void createFile() {
        settingFile(false);

        try (FileOutputStream outputStream = new FileOutputStream(file)) {

            setHeaderFooter();

            settingDocument();

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

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        openFile();
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

    private void setResultDataTable() {
        int countRows = resultData.size();
        List<String> listQuestion = new ArrayList<>(resultData.keySet());
        XWPFTable tableAnswer = document.createTable(countRows, 2);
        for (int i = 0; i < countRows; i++) {
            setDataRow(tableAnswer, listQuestion.get(i), resultData.get(listQuestion.get(i)), i);
        }
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
        run.setText("Предполагаемая стоимость:");
        run.addBreak();
        run.addBreak();
        run.setText("Согласовано, должность: __________________ Ф.И.О.: _____________________ Дата: __________");
    }
}
