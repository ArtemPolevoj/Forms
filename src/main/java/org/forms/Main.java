package org.forms;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String fileNameExcel = "C:/Users/artem/Downloads/2024-06-12 Vizual'nyi osmotr bul'dozera ChETRA 1.2.xlsx";
        String mainFolder = "C:/Users/artem/Desktop/Test";
        FileRead fileRead = new FileRead(fileNameExcel);
        Report report = fileRead.getReport();
        Folder folder = new Folder(report, mainFolder);
        ImageForm imageForm = new ImageForm(report, folder.getDirDate());
        imageForm.setImageAnswer();
        WordFile wordFile = new WordFile(report, folder.getDirAutoNumber());
        wordFile.createFile();
        //      System.out.println(report);
//        Map<String, String> map = report.getAnswer();
//        map.forEach((key, value) -> System.out.println(key + " - " + value));
    }
}