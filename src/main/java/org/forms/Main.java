package org.forms;

import java.io.File;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        //  String fileNameExcel = new OpenFile().getSelectedFile().getPath();
        File[] files = new OpenFile().getSelectedFile();
        /* Тестовые пути файлов */
        //      String mainFolder =  "C:/Users/user/Desktop/Test";
         //      String nameFileWrite = "C:/Users/user/Desktop/Test/Инспекции ТР v1.4.xlsx";

        /* Реальные пути файлов*/
        String mainFolder = "C:/Users/user/YandexDisk-techr.22/Тех. отдел/1. Документация для сервиса/5 Инспекции";
        String nameFileWrite = "C:/Users/user/YandexDisk-techr.22/Тех. отдел/2. Внутренние документы/11 Отчеты/Инспекции ТР v1.4.xlsx";

        for (File file : files) {
            String fileNameExcel = file.getPath();
            FileRead fileRead = new FileRead(fileNameExcel);
            Map<String, String> mapAnswer = fileRead.getMapAnswer();
            Folder folder = new Folder(mapAnswer, mainFolder);
            FileWrite fileWrite = new FileWrite(nameFileWrite, mapAnswer);
            fileWrite.writeFile();
            ImageForm imageForm = new ImageForm(mapAnswer, folder.getDirDate());
            mapAnswer = imageForm.getMapAnswer();
            WordFile wordFile;
            if (fileNameExcel.contains("Техническая инспекция")) {
                wordFile = new WordFileTehnical(mapAnswer, folder.getDirAutoNumber());
            } else {
                wordFile = new WordFileUsual(mapAnswer, folder.getDirAutoNumber());
            }

            wordFile.createFile();
        }


    }
}