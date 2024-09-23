package org.forms;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class OpenFile {
    private File selectedFile;

    private void getFile() {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fc.setMultiSelectionEnabled(false);
        fc.setCurrentDirectory(new File("C:/Users/user/Downloads"));
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(new FileNameExtensionFilter
                ("Excel.xlsx", "xlsx"));
        fc.setDialogTitle("Выберете файл для обработки.");
        fc.showOpenDialog(null);
        selectedFile = fc.getSelectedFile();
    }

    public File getSelectedFile() {
        getFile();
        return selectedFile;
    }
}
