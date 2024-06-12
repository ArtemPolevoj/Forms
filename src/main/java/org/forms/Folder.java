package org.forms;

import java.io.File;

public class Folder {
    private  final Report report;
    private final String folder;

    public Folder(Report report, String folder) {
        this.report = report;
        this.folder = folder;
    }
    public String getFolderName(){
        String folderAuto = folder + "/" + report.getAuto();
        String folderAutoNumber = folderAuto + "/" + report.getAuto() + " " + report.getSerialNumber() + "_" + report.getHouseNumber();
        String folderDate = folderAutoNumber + "/" + report.getDate();

        File dirAuto = new File(folderAuto);
        if (!dirAuto.exists()){
            dirAuto.mkdir();
        }
        File dirAutoNumber = new File(folderAutoNumber);
        if (!dirAutoNumber.exists()){
            dirAutoNumber.mkdir();
        }
        File dirDate = new File(folderDate);
        if (!dirDate.exists()){
            dirDate.mkdir();
        }

        System.out.println(folderAuto);
        System.out.println(folderAutoNumber);
        System.out.println(folderDate);

        return folderAutoNumber;
    }
}
