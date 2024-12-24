package org.forms;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileRead {
    private final String fileName;
    private Sheet sheet;
    private final Map<String, String> mapAnswer = new LinkedHashMap<>();

    public FileRead(String fileName) {
        this.fileName = fileName;
        setMapAnswer();
    }

    public Map<String, String> getMapAnswer() {
        return new LinkedHashMap<>(mapAnswer);
    }

    private void setMapAnswer() {
        try (XSSFWorkbook workbook = new XSSFWorkbook(
                new FileInputStream(fileName))) {

            sheet = workbook.getSheet("Sheet");
            for (Row row : sheet) {
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    if (!getTextTempCell(cell).isEmpty()) {
                        setAnswer(cell);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getTextTempCell(Cell cell) {
        int colGet = cell.getColumnIndex();
        int rowGet = cell.getRowIndex();
        try {
            return sheet.getRow(rowGet + 1).getCell(colGet).getStringCellValue();
        } catch (NullPointerException e) {
            return "";
        }
    }

    private void setAnswer(Cell cell) {
        String temp = cell.toString();
        String key;
        String newValue;
        if (temp.contains("[")) {
            key = temp.substring(3, temp.length() - 4);

        } else if (temp.contains("#")) {
            key = temp.substring(3);
        } else {
            key = temp;
        }
        String value = getTextTempCell(cell);
        if (value.contains("#")) {
            value = value.substring(3);
        }
        if (value.contains("http")) {
            String[] arr = value.split(", ");
            StringBuilder files = new StringBuilder();


            for (int i = 0; i < arr.length; i++) {
                int beginIndex = arr[i].indexOf("baket%2F") + 8;
                if (i == 0) {
                    files.append(arr[i].substring(beginIndex));
                } else {
                    files.append(", ")
                            .append(arr[i].substring(beginIndex));
                }
            }
            value = String.valueOf(files);
        }

        if (mapAnswer.containsKey(key)) {
            String tempValue = mapAnswer.get(key);
            newValue = tempValue + ", " + value;
            mapAnswer.put(key, newValue);
        } else {
            if (key.contains(MachineData.DATE.getData())) {
                mapAnswer.put(key, parsDate(value));
            } else {
                mapAnswer.put(key, value);
            }
        }
    }

    private String parsDate(String inputDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = inputFormat.parse(inputDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}