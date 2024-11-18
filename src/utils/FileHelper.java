package utils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class FileHelper {

    public static File findFile(String src) throws FileNotFoundException {
        if (src == null || src.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }

        File srcFile = new File(src);

        if (!srcFile.exists()) {
            throw new FileNotFoundException("File not found: " + src);
        }

        if (!srcFile.isFile()) {
            throw new IllegalArgumentException("Not a valid file: " + src);
        }

        return srcFile;
    }

    public boolean saveFile(Component container, String msg, String text, String des) {
        File file = new File(des);

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(text);

            int option = JOptionPane.showOptionDialog(
                    container,
                    msg,
                    "Success",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    new Object[]{"OK", "Check File"},
                    "OK"
            );

            if (option == 1) {
                Desktop.getDesktop().open(file.getParentFile());
            }

            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(container, "Unable to save the file. Please try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);

            return false;
        }
    }

    public File showOpenFile(Component container, String fileSelected, String[] extensions) {
        if (extensions == null) {
            extensions = new String[0];
        }

        JFileChooser fileChooser = new JFileChooser();
        setFilterExtension(fileChooser, extensions);

        if (fileSelected != null && !fileSelected.trim().isEmpty()) {
            fileChooser.setSelectedFile(new File(fileSelected));
        }

        int result = fileChooser.showOpenDialog(container);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String selectedFilePath = selectedFile.getAbsolutePath();

            if (!selectedFile.exists()) {
                JOptionPane.showMessageDialog(container, "File not Found. Please try again.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            if (extensions.length > 0) {
                boolean hasValidExtension = false;
                for (String ext : extensions) {
                    if (selectedFilePath.endsWith("." + ext)) {
                        hasValidExtension = true;
                        break;
                    }
                }

                if (!hasValidExtension) {
                    JOptionPane.showMessageDialog(container, "The selected file has an invalid format.",
                            "Warning", JOptionPane.WARNING_MESSAGE);
                    return null;
                }
            }

            return selectedFile;
        }

        return null;
    }

    public String showSaveFile(Component container, String fileSelected, String[] extensions) {
        if (extensions == null) {
            extensions = new String[0];
        }

        JFileChooser fileChooser = new JFileChooser();
        setFilterExtension(fileChooser, extensions);

        if (fileSelected != null && !fileSelected.trim().isEmpty()) {
            fileChooser.setSelectedFile(new File(fileSelected));
        }

        int result = fileChooser.showSaveDialog(container);

        if (result == JFileChooser.APPROVE_OPTION) {
            String selectedFilePath = fileChooser.getSelectedFile().getAbsolutePath();

            boolean hasExtension = false;
            for (String ext : extensions) {
                if (selectedFilePath.endsWith("." + ext)) {
                    hasExtension = true;
                    break;
                }
            }

            if (!hasExtension && extensions.length > 0) {
                selectedFilePath += "." + extensions[0];
            }

            return selectedFilePath;
        }

        return null;
    }

    private void setFilterExtension(JFileChooser fileChooser, String[] extensions) {
        if (extensions != null && extensions.length > 0) {
            String description = String.join(", ", extensions) + " files";
            FileNameExtensionFilter filter = new FileNameExtensionFilter(description, extensions);
            fileChooser.setFileFilter(filter);
        }
    }
}