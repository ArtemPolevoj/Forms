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
    private Report report;
    private Sheet sheet;
    private final Map<String, String> answer = new LinkedHashMap<>();

    public FileRead(String fileName) {
        this.fileName = fileName;
    }

    public Report getReport() {
        setReport();
        report.setAnswer(answer);
        return report;
    }

    private void setReport() {
        report = new Report();
        try (XSSFWorkbook workbook = new XSSFWorkbook(
                new FileInputStream(fileName))) {

            sheet = workbook.getSheet("Sheet");
            for (Row row : sheet) {

                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    switch (cell.toString()) {
                        case "ID" -> report.setId(getTextTempCell(cell));
                        case "Время создания" -> report.setDate(parsDate(getTextTempCell(cell)));
                        case "## Заказчик" -> report.setClient(getTextTempCell(cell));
                        case "## Машина" -> report.setAuto(getTextTempCell(cell));
                        case "## Серийный номер" -> report.setSerialNumber(getTextTempCell(cell));
                        case "## Хозяйственный номер" -> report.setHouseNumber(getTextTempCell(cell));
                        case "## Наработка" -> report.setWorkHours(getTextTempCell(cell));
                        case "## Машина чистая?" -> report.setClear(getTextTempCell(cell));
                        case "## Исполнитель" -> report.setUserReport(getTextTempCell(cell));
                        case "## 601 Номера устранённых неисправностей" -> {}
                        case "## Оставить пожелание по использованию формы" -> {}
                        case "## Пожелания" -> {}
                        default -> {
                            if (!getTextTempCell(cell).isEmpty()) {
                                setAnswer(cell);
                            }
                        }
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
            return sheet.getRow(rowGet + 1).getCell(colGet).toString();
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
        } else {
            key = temp.substring(3);
        }
        String value = getTextTempCell(cell);
        if (value.contains("#")) {
            value = value.substring(3);
        }
        if (answer.containsKey(key)) {
            String tempValue = answer.get(key);
            if (tempValue.contains(",")){
                newValue = tempValue + " " + value;
            }else {
                newValue = tempValue + ", " + value;
            }
            answer.put(key, newValue);
        } else {
            answer.put(key, value);
        }
    }
    private String parsDate(String inputDate){
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date date;
        try {
            date = inputFormat.parse(inputDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}