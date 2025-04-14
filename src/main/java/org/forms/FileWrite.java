package org.forms;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class FileWrite {
    private final String fileName;
    private XSSFSheet sheet;
    private final Map<String, String> mapAnswer;

    public FileWrite(String fileName, Map<String, String> mapAnswer) {
        this.fileName = fileName;
        this.mapAnswer = mapAnswer;
    }

    public void writeFile() {
        Set<String> setMapAnswer = mapAnswer.keySet();
        try (XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(fileName))) {
            sheet = workbook.getSheet("Инспекции машин");
            if (!inspectionExist()) {
                int lastRow = sheet.getLastRowNum();
                XSSFRow rowNew = sheet.createRow(lastRow + 1);
                XSSFRow firstRow = sheet.getRow(0);
                Iterator<Cell> cellIterator = firstRow.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    String celValue = cell.getStringCellValue();
                    if (setMapAnswer.contains(celValue)) {
                        XSSFCell cellNew = rowNew.createCell(cell.getColumnIndex());
                            cellNew.setCellValue(mapAnswer.get(celValue));
                    }
                }
                try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
                    workbook.write(fileOut);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean inspectionExist() {
        Iterator<Cell> cellIterator = sheet.getRow(0).cellIterator();
        String id = "ID";
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            if (cell.getStringCellValue().equals(id)) {
                for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                      Cell temp = sheet.getRow(i).getCell(cell.getColumnIndex());
                    if (temp.getStringCellValue().equals(mapAnswer.get(id))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
