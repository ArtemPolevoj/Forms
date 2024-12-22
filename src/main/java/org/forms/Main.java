package org.forms;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String fileNameExcel = new OpenFile().getSelectedFile().getPath();
       // String fileNameExcel = "C:/Users/user/Downloads/2024-09-23 Vizual'nyi osmotr bul'dozera ChETRA 1.2.xlsx";
        String mainFolder = "C:/Users/user/Desktop/Test";
     //   String mainFolder =  "C:/Users/user/YandexDisk-techr.22/Тех. отдел/1. Документация для сервиса/5 Инспекции";
        String nameFileWrite = "C:/Users/user/Desktop/Инспекции ТР v1.2.xlsx";
        FileRead fileRead = new FileRead(fileNameExcel);
        Map<String, String> mapAnswer = fileRead.getMapAnswer();
        Folder folder = new Folder(mapAnswer , mainFolder);
        FileWrite fileWrite = new FileWrite(nameFileWrite, mapAnswer);
        fileWrite.writeFile();
        ImageForm imageForm = new ImageForm(mapAnswer , folder.getDirDate());
        imageForm.setImageAnswer();
        mapAnswer = imageForm.getMapAnswer();
        WordFile wordFile = new WordFile(mapAnswer , folder.getDirAutoNumber());
        wordFile.createFile();

    }
}