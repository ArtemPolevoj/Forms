package org.forms;

import net.coobird.thumbnailator.Thumbnails;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;

import java.awt.*;
import java.io.*;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public abstract class WordFile {
    protected XWPFDocument document = new XWPFDocument();
    protected String folderName;
    protected int sizePage = 700;
    protected String sizeCell1 = "60%";
    protected String sizeCell2 = "40%";
    protected Map<String, String> inputData = new LinkedHashMap<>();
    protected Map<String, String> preData = new LinkedHashMap<>();
    protected Map<String, String> resultData = new LinkedHashMap<>();
    protected File file;

    public void createFile() {
    }

    protected void setTableBorders(XWPFTable table) {
        table.setInsideVBorder(XWPFTable.XWPFBorderType.SINGLE, 10, 0, "000000");
        table.setInsideHBorder(XWPFTable.XWPFBorderType.SINGLE, 10, 0, "000000");
    }

    protected void setHead(String text) {
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

    protected void setInputDataTable() {
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

    protected void insertImage(XWPFTableCell cell1, XWPFTableCell cell2, String text) {
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
        switch (fileName.length) {
            case 1, 2, 3 -> sizeImage = sizePage;
            case 4 -> sizeImage = 1400;
            default -> sizeImage = 1170;
        }
        int width = (int) ((sizeImage / 1.3) / fileName.length);
        int height = (int) ((sizeImage / 1.8) / fileName.length);
        for (String s : fileName) {
            File originalFile = new File(s);
            try(ByteArrayInputStream compressedImageStream = compressImage(originalFile, width, height);) {

                run.addPicture(
                        compressedImageStream,
                        XWPFDocument.PICTURE_TYPE_JPEG,
                        s,
                        Units.toEMU(width),
                        Units.toEMU(height));

            } catch (InvalidFormatException | IOException e) {
                System.out.println("File image " + s);
                throw new RuntimeException(e);
            }
        }
    }

    protected void setHeaderFooter() {

        String logoFileName = "logo.png";
        String singFileName = "sing.png";
        String id = "ID " + inputData.get("ID");
        String machine = inputData.get("Машина") + "\t "
                + inputData.get("Серийный номер") + "_"
                + inputData.get("Хозяйственный номер");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

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
        run.setText("\t\t\t" + id + "\t\t\t " + machine);

        XWPFFooter footer = headerFooterPolicy.createFooter(XWPFHeaderFooterPolicy.DEFAULT);
        paragraph = footer.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        run = paragraph.createRun();
        run.setText("Дата: " + LocalDateTime.now().format(outputFormatter));
        run.setText("\t\t\tТехнический специалист\t\t");
        try {
            run.addPicture(new FileInputStream(singFileName), XWPFDocument.PICTURE_TYPE_PNG, singFileName, Units.toEMU(30), Units.toEMU(30));
        } catch (InvalidFormatException | IOException e) {
            throw new RuntimeException(e);
        }
        run.setText("\t\tПолевой А. В.");
    }

    protected void setOffer() {
        XWPFTable tableData = document.createTable(1, 2);
        XWPFTableCell cell1 = tableData.getRow(0).getCell(0);
        XWPFTableCell cell2 = tableData.getRow(0).getCell(1);
        cell1.setWidth(sizeCell1);
        cell2.setWidth(sizeCell2);
        cell1.setText("Рекомендации");
        document.createParagraph();
        XWPFRun run = document.createParagraph().createRun();
        run.setText("Предполагаемая стоимость:");
        run.addBreak();
        run.addBreak();
        run.setText("Согласовано, должность: __________________ Ф.И.О.: _____________________ Дата: __________");
    }

    protected void setPreDataTable() {
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
        setOffer();
    }

    protected void settingFile(boolean technical) {
        String fileName = "";
        String newFileName = "";
        String textFileName = inputData.get("Машина") + " "
                + inputData.get("Серийный номер") + "_"
                + inputData.get("Хозяйственный номер") +
                ".docx";
        if (technical) {
            fileName = folderName + "/Отчет технический " + textFileName;
            newFileName = folderName + "/Отчет новый технический " + textFileName;
        } else {
            fileName = folderName + "/Отчет " + textFileName;
            newFileName = folderName + "/Отчет новый " + textFileName;
        }


        file = new File(fileName);
        if (file.exists()) {
            file = new File(newFileName);
        }
    }

    protected void openFile() {
        if (file.exists()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.open(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected void settingDocument() {
        document.getDocument().getBody().addNewSectPr().addNewPgMar().setLeft(BigInteger.valueOf(sizePage));
        document.getDocument().getBody().addNewSectPr().addNewPgMar().setRight(BigInteger.valueOf(sizePage));
        document.getDocument().getBody().addNewSectPr().addNewPgMar().setTop(BigInteger.valueOf(sizePage));
        document.getDocument().getBody().addNewSectPr().addNewPgMar().setBottom(BigInteger.valueOf(sizePage));
    }

    private float getCompressionLevel(long fileSize) {
        long sizeInKb = fileSize / 1024;

        if (sizeInKb <= 1024) {
            return 0.99f;
        } else if (sizeInKb <= 2048) {
            return 0.95f;
        } else {
            return 0.9f;
        }
    }

    private ByteArrayInputStream compressImage(File imageFile, int targetWidth, int targetHeight)  {

        long fileSizeInBytes = imageFile.length();
        float compressionLevel = getCompressionLevel(fileSizeInBytes);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Thumbnails.of(imageFile)
                    .size(targetWidth, targetHeight)
                    .outputFormat("jpg")
                    .outputQuality(compressionLevel)
                    .toOutputStream(baos);

            return new ByteArrayInputStream(baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}