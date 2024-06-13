package org.forms;

import java.io.File;

public class Folder {
    private  final Report report;
    private final String folder;
    private String folderAutoNumber;
    private String folderDate;

    public Folder(Report report, String folder) {
        this.report = report;
        this.folder = folder;
        setDirectory();
    }
    private void setDirectory(){
        String folderAuto = folder + "/" + report.getAuto();
        folderAutoNumber = folderAuto + "/" + report.getAuto() + " " + report.getSerialNumber() + "_" + report.getHouseNumber();
        folderDate = folderAutoNumber + "/" + report.getDate();

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
    }

    public String getDirAutoNumber() {
        return folderAutoNumber;
    }

    public String getDirDate() {
        return folderDate;
    }
}
