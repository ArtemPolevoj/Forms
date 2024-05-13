package org.forms;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

public class ReaderFile {
    private String fileName;

    public void reader(String fileName) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(
                new FileInputStream(fileName))) {

            Sheet sheet = workbook.getSheet("Таблица");
//int lastRowNumRows = sheet.getLastRowNum();
//
//            for (int i = 0; i < lastRowNumRows; i++) {
//                Iterator<Cell> cellIterator = sheet.getRow(i).cellIterator();
//                while (cellIterator.hasNext()) {
//                    Cell cell = cellIterator.next();
//
//                    System.out.print(cell.toString());
//                    System.out.print(" | ");
//                }
//                System.out.println();
//            }
            int rowNumber = 0;
            int columnNumber = 0;
            for (Row row : sheet) {

                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    if (cell.toString().equals("1214.0")) {
                        rowNumber = cell.getRowIndex();
                    }
                    if (cell.toString().equals("Заказчик")) {
                        columnNumber = cell.getColumnIndex();
                    }

                }
            }
            System.out.println(rowNumber + ", " + columnNumber);
            Cell cell1 = sheet.getRow(rowNumber).getCell(columnNumber);
            System.out.println(cell1.toString());


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}