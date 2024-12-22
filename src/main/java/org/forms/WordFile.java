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
    private final Map<String, String> mapAnswer;
    private final String folderName;
    private XWPFDocument document;
    private final String sizeCell1 = "70%";
    private final String sizeCell2 = "30%";
    private final int sizePage = 700;
    private final List<String> listQuestion;
    private final int countFirstRow = 9;
    private final List<String> listFailure = new ArrayList<>();

    public WordFile(Map<String, String> mapAnswer, String folderName) {
        this.mapAnswer = mapAnswer;
        this.folderName = folderName;
        listQuestion = new ArrayList<>(mapAnswer.keySet());
    }

    public void createFile() {
        String textFileName = mapAnswer.get(MachineData.AUTO.getData()) + " "
                + mapAnswer.get(MachineData.SERIAL_NUMBER.getData()) + "_"
                + mapAnswer.get(MachineData.HOUSE_NUMBER.getData()) +
                ".docx";
        String fileName = folderName + "/Отчет " + textFileName;
        String newFileName = folderName + "/Новый отчет " + textFileName;

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
            setTableData();

            setHead("Результат инспекции");
            setTableAnswer();

            document.createParagraph().setPageBreak(true);
            setHead("Рекомендации");
            setRecommendation();

            setHead("Возможные последствия отказа");
            setTableConsequencesFailure();

            document.createParagraph().setPageBreak(true);
            setHead("Коммерческое предложение");
            setCommercialOffer();

            document.createParagraph().setPageBreak(true);
            setHead("Приложение 1");
            setApposition();

            document.write(outputStream);

            if (file.exists()) {
                Desktop desktop = Desktop.getDesktop();
                desktop.open(file);
            }

        } catch (IOException e) {
            System.out.println("Не удалось создать файл");
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

    private void setTableData() {
        XWPFTable tableData = document.createTable(countFirstRow, 2);
        tableData.setWidth("100%");
        setTableBorders(tableData);

        for (int i = 0; i < countFirstRow; i++) {
            XWPFTableCell cell1 = tableData.getRow(i).getCell(0);
            XWPFTableCell cell2 = tableData.getRow(i).getCell(1);
            cell1.setWidth(sizeCell1);
            cell2.setWidth(sizeCell2);
            XWPFParagraph paragraph = cell2.getParagraphs().getFirst();
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            paragraph.setVerticalAlignment(TextAlignment.CENTER);

            String question = listQuestion.get(i);
            cell1.setText(question);
            cell2.setText(mapAnswer.get(question));

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

        switch (secondText) {
            case "ОК", "Ok", "ok", "Ок", "Да", "Норма", "Нет", "В норме" -> {
                cell1.setColor("90EE90");
                cell2.setColor("90EE90");
            }
            case "Нет осмотра" -> {
                cell1.setColor("ADD8E6");
                cell2.setColor("ADD8E6");
            }
            default -> {
                if (firstText.contains("Общий вид")) {
                    cell1.setColor("ADD8E6");
                } else {
                    cell1.setColor("FFDAB9");
                    cell2.setColor("FFDAB9");
                }
            }
        }
        if (secondText.contains(".jpeg")) {
            insertImage(cell1, cell2, secondText);
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
            System.out.println("Не удалось вставить логотип");
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
            System.out.println("Не удалось вставить подпись");
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
        int width = (int) ((sizePage / 1.3) / fileName.length);
        int height = (int) ((sizePage / 1.8) / fileName.length);
        for (String s : fileName) {
            try {
                run.addPicture(new FileInputStream(s), XWPFDocument.PICTURE_TYPE_JPEG, s,
                        Units.toEMU(width), Units.toEMU(height));
            } catch (InvalidFormatException | IOException e) {
                System.out.println("Не удалось вставить фото: " + s);
                throw new RuntimeException(e);
            }
        }
    }


    private void setRecommendation() {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        for (String question : listQuestion) {
            if (question.contains(MachineData.FAULT.getData())){
                String answer = mapAnswer.get(question);
                listFailure.add(answer);
                if (answer.contains("отсутствует")) {
                    answer = answer.replace("отсутствует ", "");
                    answer = answer.replace("Отсутствует ", "");
                    run.setText("Установить " + answer + ".");
                } else {
                    run.setText("Устранить " + answer.toLowerCase() + ".");
                }
                run.addBreak();
            }
        }
    }

    private void setTableConsequencesFailure() {
        document.createParagraph().createRun();
        int countRows = listFailure.size() + 1;
        XWPFTable tableConsequencesFailure = document.createTable(countRows, 2);
        tableConsequencesFailure.setWidth("100%");
        setTableBorders(tableConsequencesFailure);
        XWPFTableCell cell1 = tableConsequencesFailure.getRow(0).getCell(0);
        XWPFTableCell cell2 = tableConsequencesFailure.getRow(0).getCell(1);
        cell1.setWidth(sizeCell1);
        cell1.setText("Возможные последствия отказа");
        cell1.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);
        cell1.getParagraphs().getFirst().setAlignment(ParagraphAlignment.CENTER);
        cell2.setWidth(sizeCell2);
        cell2.setText("Уровень последствий отказа (приложение 1)");
        int row = 1;
        for (String failure : listFailure) {
                  tableConsequencesFailure.getRow(row++).getCell(0).setText(failure + " может привезти к ");
        }
    }

    private void setTableAnswer() {
        int countRows = listQuestion.size() - countFirstRow;
        XWPFTable tableAnswer = document.createTable(countRows, 2);
        int row = 0;
        for (int i = countFirstRow; i < listQuestion.size(); i++) {
            setDataRow(tableAnswer, listQuestion.get(i), mapAnswer.get(listQuestion.get(i)), row++);
        }
    }

    private void setCommercialOffer() {
        XWPFParagraph pLeft = document.createParagraph();
        pLeft.setAlignment(ParagraphAlignment.RIGHT);
        XWPFRun rLeft = pLeft.createRun();
        rLeft.setText("185013, г. Петрозаводск");
        rLeft.addBreak();
        rLeft.setText("пр. Лесной, 49Б");
        rLeft.addBreak();
        rLeft.setText("ИНН 1001262650");
        rLeft.addBreak();
        rLeft.setText("КПП 100101001");
        rLeft.addBreak();
        rLeft.setText("f.shestovec@mail.ru");
        rLeft.addBreak();
        rLeft.setText("+7 911 426 02 00");
        rLeft.addBreak();

        XWPFRun run = document.createParagraph().createRun();
        run.setText("Предлагаем вам профессиональную техническую инспекцию," +
                " которая поможет выявить и устранить возможные проблемы и повысить безопасность и эффективность" +
                " работы вашей техники.");
        run.addBreak();
        run.addBreak();
        run.addBreak();
        run.setText("Предполагаемое время работы -    ч.ч.");
        run.addBreak();
        run.setText("Цены и сроки поставки действительны 10 дней со дня подачи.");
        run.addBreak();
        run.setText("Согласовано, должность: __________________ Ф.И.О.: _____________________  Дата: __________");
        run.addBreak();
        run.setText("Мы уверены, что наша техническая инспекция поможет вам повысить безопасность и эффективность" +
                " работы вашей техники, а также сэкономить время и деньги. ");
        run.addBreak();
        run.setText("Обращайтесь к нам и убедитесь в этом сами!");


    }

    private void setApposition() {
        XWPFRun run = document.createParagraph().createRun();
        run.addBreak();
        try {
            run.addPicture(new FileInputStream("rating.png"), XWPFDocument.PICTURE_TYPE_JPEG,
                    "rating.png", Units.toEMU(sizePage / 1.3), Units.toEMU(sizePage / 1.9));
        } catch (InvalidFormatException | IOException e) {
            System.out.println("Не удалось вставить таблицу рейтенгов");
        }
    }
}
