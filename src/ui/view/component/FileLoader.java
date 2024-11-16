package ui.view.component;

import utils.FileHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

public class FileLoader extends JPanel {

    private MaterialLabel label;
    private EditText fileEdt;
    public JButton browserBtn;

    private GridBagConstraints gbc;

    public static final int MARGIN_HORIZONTAL = 10;

    private Component container;

    public FileLoader(String labelName) {
        setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, MARGIN_HORIZONTAL, 0, 0);
        gbc.anchor = GridBagConstraints.WEST;

        label = new MaterialLabel(labelName);
        add(label, gbc);

        fileEdt = new EditText();
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        add(fileEdt, gbc);

        browserBtn = new JButton("Browse");
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        add(browserBtn, gbc);

        container = this;
    }

    public void setLabel(String text) {
        label.info.setText(text);
    }

    public JLabel getLabel() {
        return label.info;
    }

    /**
     * Lấy đường dẫn File save, tạo File mới nếu không tồn tại
     *
     * @param extensions
     * @return
     */
    public void browseSaveFile(String[] extensions) {
        FileHelper fileHelper = new FileHelper();

        String filePath = fileHelper.showSaveFile(container, fileEdt.getText(), extensions);
        fileEdt.setText(filePath != null ? filePath : fileEdt.getText());
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
        File file = fileHelper.showOpenFile(container, fileEdt.getText(), extensions);
        fileEdt.setText(file != null ? file.getAbsolutePath() : fileEdt.getText());
        hideError();
    }

    public void setContainerPopup(Component container) {
        this.container = container;
    }

    public String getPath() {
        return fileEdt.getText();
    }

    public void error(String error) {
        fileEdt.error(error);
        label.setNotify("");
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(0, MARGIN_HORIZONTAL, 25, 0);
        add(browserBtn, gbc);
    }

    public void hideError() {
        fileEdt.hideError();
        label.deleteNotify();

        gbc.gridx = 2;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(0, MARGIN_HORIZONTAL, 0, 0);
        remove(browserBtn);
        add(browserBtn, gbc);
    }
}