package org.forms;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Folder {
    private final Map<String, String> mapAnswer;
    private final String folder;
    private String folderAutoNumber;
    private String folderDate;

    public Folder(Map<String, String> mapAnswer, String folder) {
        this.mapAnswer = mapAnswer;
        this.folder = folder;
        setDirectory();
    }

    private void setDirectory() {
        String folderAuto = folder + "/" + mapAnswer.get("Машина");
        folderAutoNumber = folderAuto + "/" + mapAnswer.get("Дата осмотра") + " "
                + mapAnswer.get("Серийный номер") + "_" + mapAnswer.get("Хозяйственный номер");
        folderDate = folderAutoNumber + "/" + mapAnswer.get("Дата осмотра");

        File dirAuto = new File(folderAuto);
        if (!dirAuto.exists()) {
            try {
                Files.createDirectory(Path.of(folderAuto));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        File dirAutoNumber = new File(folderAutoNumber);
        if (!dirAutoNumber.exists()) {
            try {
                Files.createDirectory(Path.of(folderAutoNumber));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        File dirDate = new File(folderDate);
        if (!dirDate.exists()) {
            try {
                Files.createDirectory(Path.of(folderDate));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getDirAutoNumber() {
        return folderAutoNumber;
    }

    public String getDirDate() {
        return folderDate;
    }

}
