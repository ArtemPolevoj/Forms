package org.forms;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class FileRead {
    private final String fileName;
    private final Map<String, String> mapAnswer = new LinkedHashMap<>();

    public FileRead(String fileName) {
        this.fileName = fileName;
        setMapAnswer();
    }

    public Map<String, String> getMapAnswer() {
        return new LinkedHashMap<>(mapAnswer);
    }

    private void setMapAnswer() {
        File file = new File(fileName);
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(file);
            String jsonString = rootNode.toString();
            List<List<String[]>> data = mapper.readValue(jsonString, new TypeReference<>() {
            });

            for (String[] entry : data.getFirst()) {
                if (!entry[1].isEmpty()) {
                    String key = entry[0].replaceAll("#", "").trim();
                    String value = entry[1].replaceAll("#", "").trim();
                    if (key.contains("Машина (другое)")) {
                        key = "Машина";
                    }
                    if (key.contains("[")) {
                        key = key.substring(0, key.length() - 4);
                    }
                    if (value.contains("http")) {
                        String[] arr = value.split(", ");
                        StringBuilder files = new StringBuilder();
                        for (int i = 0; i < arr.length; i++) {
                            int beginIndex = arr[i].indexOf("baket%2F") + 8;
                            if (i == 0) {
                                files.append(arr[i].substring(beginIndex));
                            } else {
                                files.append(", ").append(arr[i].substring(beginIndex));
                            }
                        }
                        value = String.valueOf(files);
                    }
                    if (key.contains("Время создания")) {
                        key = "Дата осмотра";
                        value = parsDate(value);
                    }
                    if (mapAnswer.containsKey(key) && !key.equals("Машина")) {
                        value = mapAnswer.get(key) + ", " + value;
                        mapAnswer.put(key, value);
                    } else {
                        mapAnswer.put(key, value);
                    }

                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String parsDate(String inputDate) {
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