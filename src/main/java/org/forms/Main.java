package org.forms;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        String fileNameExcel = new OpenFile().getSelectedFile().getPath();
//       // String fileNameExcel = "C:/Users/user/Downloads/2024-09-23 Vizual'nyi osmotr bul'dozera ChETRA 1.2.xlsx";
        String mainFolder = "C:/Users/artem/Desktop/Test";
//     //   String mainFolder =  "C:/Users/user/YandexDisk-techr.22/???. ?????/1. ???????????? ??? ???????/5 ?????????";
//        String nameFileWrite = "C:/Users/user/Desktop/????????? ?? v1.2.xlsx";
        FileRead fileRead = new FileRead(fileNameExcel);
        Map<String, String> mapAnswer = fileRead.getMapAnswer();
      //  mapAnswer.forEach((key, value) -> System.out.println(key + " - " + value));
//        System.out.println(mapAnswer.get("Машина"));
//        System.out.println(mapAnswer.get("Дата осмотра"));
//        System.out.println(mapAnswer.get("Серийный номер") + "_" + mapAnswer.get("Хозяйственный номер"));
//        System.out.println();
        Folder folder = new Folder(mapAnswer , mainFolder);
//        FileWrite fileWrite = new FileWrite(nameFileWrite, mapAnswer);
//        fileWrite.writeFile();
        ImageForm imageForm = new ImageForm(mapAnswer , folder.getDirDate());
        mapAnswer = imageForm.getMapAnswer();
        WordFile wordFile = new WordFile(mapAnswer , folder.getDirAutoNumber());
        wordFile.createFile();

    }
}