package ui.view.component;

import ui.common.Dimensions;
import utils.FileHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.util.List;

public class FileLoader extends JPanel {

    public InputField inputField;
    public JButton browserBtn;

    public FileLoader(String labelName) {

        inputField = new InputField(labelName);
        inputField.info("Drag and drop a file here");
        browserBtn = new JButton("Browse");

        new DropTarget(inputField.input, DnDConstants.ACTION_COPY, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent event) {
                try {
                    event.acceptDrop(DnDConstants.ACTION_COPY);

                    Object droppedData = event.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

                    if (droppedData instanceof List) {
                        List<File> fileList = (List<File>) droppedData;
                        if (!fileList.isEmpty()) {
                            File droppedFile = fileList.get(0);

                            if (droppedFile.isFile()) {
                                inputField.setValue(droppedFile.getAbsolutePath());
                            } else {
                                JOptionPane.showMessageDialog(getRootPane(), "Please drop a valid file.", "Invalid Drop", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = Dimensions.DEFAULT_INSETS;
        gbc.gridx = 2;
        gbc.gridy = 0;
        inputField.add(browserBtn, gbc);

        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.insets = Dimensions.ZERO_INSETS;
        add(inputField, gbc);
    }

    /**
     * Lấy đường dẫn File save, tạo File mới nếu không tồn tại
     *
     * @param extensions
     * @return
     */
    public void browseSaveFile(String[] extensions) {
        FileHelper fileHelper = new FileHelper();

        String filePath = fileHelper.showSaveFile(getRootPane(), inputField.getValue(), extensions);
        inputField.setValue(filePath != null ? filePath : inputField.getValue());
        hideError();
    }

    /**
     * Lấy đường dẫn của File đã tồn tại
     *
     * @param extensions
     * @return
     */
    public void browseFile(String[] extensions) {
        FileHelper fileHelper = new FileHelper();
        File file = fileHelper.showOpenFile(getRootPane(), inputField.getValue(), extensions);
        inputField.setValue(file != null ? file.getAbsolutePath() : inputField.getValue());
        hideError();
    }

    public String getPath() {
        return inputField.getValue();
    }

    public void error() {
        inputField.error();
        JOptionPane.showMessageDialog(getRootPane(), "Please enter a File.",
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void hideError() {
        inputField.hideError();
    }
}