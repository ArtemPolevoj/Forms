package org.forms;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class OpenFile {
    private File[] selectedFile;

    private void getFile() {
        JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(true);
        fc.setCurrentDirectory(new File("C:/Users/user/Downloads"));
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.addChoosableFileFilter(new FileNameExtensionFilter
                ("Json.json", "json"));
        fc.setDialogTitle("Выберете файл из списка.");
        fc.showOpenDialog(null);
        selectedFile = fc.getSelectedFiles();
    }

    public File[] getSelectedFile() {
        getFile();
        return selectedFile;
    }
}
