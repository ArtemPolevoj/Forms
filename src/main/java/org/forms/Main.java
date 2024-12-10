package org.forms;


public class Main {
    public static void main(String[] args) {
        String fileNameExcel = new OpenFile().getSelectedFile().getPath();
       // String fileNameExcel = "C:/Users/user/Downloads/2024-09-23 Vizual'nyi osmotr bul'dozera ChETRA 1.2.xlsx";
       // String mainFolder = "C:/Users/user/Desktop/Test";
        String mainFolder =  "C:/Users/user/YandexDisk-techr.22/Тех. отдел/1. Документация для сервиса/5 Инспекции";
        FileRead fileRead = new FileRead(fileNameExcel);
        Report report = fileRead.getReport();
        Folder folder = new Folder(report, mainFolder);
        ImageForm imageForm = new ImageForm(report, folder.getDirDate());
        imageForm.setImageAnswer();
        WordFile wordFile = new WordFile(report, folder.getDirAutoNumber());
        wordFile.createFile();
    }
}