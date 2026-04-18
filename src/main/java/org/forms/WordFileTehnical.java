package org.forms;

import org.apache.poi.xwpf.usermodel.*;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WordFileTehnical extends WordFile {

    public WordFileTehnical(Map<String, String> mapAnswer, String folderName) {
        setMaps(mapAnswer);
        this.folderName = folderName;
    }

    public void createFile() {
        settingFile();

        try (FileOutputStream outputStream = new FileOutputStream(file)) {

            setHeaderFooter();

            document.getDocument().getBody().addNewSectPr().addNewPgMar().setLeft(BigInteger.valueOf(sizePage));
            document.getDocument().getBody().addNewSectPr().addNewPgMar().setRight(BigInteger.valueOf(sizePage));
            document.getDocument().getBody().addNewSectPr().addNewPgMar().setTop(BigInteger.valueOf(sizePage));
            document.getDocument().getBody().addNewSectPr().addNewPgMar().setBottom(BigInteger.valueOf(sizePage));

            setHead("Данные инспекции");
            setInputDataTable();

            setHead("Предварительный осмотр");
            setPreDataTable();

            setHead("Результаты осмотра");
            setResultDataTable();

            document.write(outputStream);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        openFile();
    }

    private void setDataRow(XWPFTable table, String firstText, String secondText, int numberRow) {
        XWPFTableCell cell1 = table.getRow(numberRow).getCell(0);
        XWPFTableCell cell2 = table.getRow(numberRow).getCell(1);
        table.setWidth("100%");
        cell1.setText(firstText);
        cell1.setWidth(sizeCell1);
        cell2.setWidth(sizeCell2);
        cell2.setText(secondText);
        cell2.getParagraphs().getFirst().setAlignment(ParagraphAlignment.CENTER);

        cell1.setColor("FFDAB9");
        cell2.setColor("FFDAB9");

        if (secondText.contains(".jpeg")) {
            insertImage(cell1, cell2, secondText);
        }
    }

    private void setResultDataTable() {
        int countRows = resultData.size();
        List<String> listQuestion = new ArrayList<>(resultData.keySet());
        XWPFTable tableAnswer;
        for (int i = 0; i < countRows; i++) {
            if (i < countRows - 1) {
                if (listQuestion.get(i + 1).contains("Фото")) {
                    tableAnswer = document.createTable(1, 2);
                    setDataRow(tableAnswer, listQuestion.get(i), resultData.get(listQuestion.get(i)), 0);
                } else {
                    tableAnswer = document.createTable(1, 2);
                    setDataRow(tableAnswer, listQuestion.get(i), resultData.get(listQuestion.get(i)), 0);
                    setOffer();
                }
            } else {
                tableAnswer = document.createTable(1, 2);
                setDataRow(tableAnswer, listQuestion.get(i), resultData.get(listQuestion.get(i)), 0);
                setOffer();
            }
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
                    resultData.put(key, value);
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

}
