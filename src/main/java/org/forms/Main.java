package org.forms;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String fileNameExcel = new OpenFile().getSelectedFile().getPath();
        // Тестовые пути файлов
        String mainFolder =  "C:/Users/artem/Desktop/Test";

      //  String nameFileWrite = "C:/Users/artem/YandexDisk-techr.22/Тех. отдел/1. Документация для сервиса/Сервис Технические Решения.xlsx";
        String nameFileWrite = "C:/Users/artem/Desktop/Test/Тех. отдел/1. Документация для сервиса/Сервис Технические Решения.xlsx";
        // Реальные пути файлов
     //   String mainFolder =  "C:/Users/user/YandexDisk-techr.22/Тех. отдел/1. Документация для сервиса/5 Инспекции";
      //  String nameFileWrite = "C:/Users/user/YandexDisk-techr.22/Тех. отдел/2. Внутренние документы/11 Отчеты/Инспекции ТР v1.4.xlsx";
        FileRead fileRead = new FileRead(fileNameExcel);
        Map<String, String> mapAnswer = fileRead.getMapAnswer();
        Folder folder = new Folder(mapAnswer , mainFolder);
        FileWrite fileWrite = new FileWrite(nameFileWrite, mapAnswer);
        fileWrite.writeFile();
        ImageForm imageForm = new ImageForm(mapAnswer , folder.getDirDate());
        mapAnswer = imageForm.getMapAnswer();
        WordFile wordFile;
        if (fileNameExcel.contains("Техническая инспекция")){
            wordFile = new WordFileTehnical(mapAnswer , folder.getDirAutoNumber());
        }else {
            wordFile = new WordFileUsual(mapAnswer , folder.getDirAutoNumber());
        }

        wordFile.createFile();

    }
}