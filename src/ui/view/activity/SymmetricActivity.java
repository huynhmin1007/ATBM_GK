package ui.view.activity;

import encryption.common.Algorithm;
import encryption.symmetric.SymmetricFactory;
import ui.common.Dimensions;
import ui.view.component.FileLoader;
import ui.view.component.MaterialCombobox;
import ui.view.component.MaterialLabel;
import ui.view.custom.BaseActivity;
import ui.view.fragment.symmetric.AESFragment;
import ui.view.fragment.symmetric.SymmetricConcrete;
import ui.view.fragment.symmetric.SymmetricDecorator;
import utils.FileHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class SymmetricActivity extends BaseActivity {

    private static SymmetricActivity INSTANCE;

    private GridBagConstraints gbc;

    private JPanel contentPane;

    private JPanel inputTextPanel;
    private JTextArea inputTextArea;
    private FileLoader fileLoader;
    private MaterialCombobox<String> algorithmCbb;
    private JTabbedPane resultTabbedPane;
    private JTextArea encryptTextArea, decryptTextArea;

    private AESFragment aesFragment;

    private SymmetricDecorator decorator;
    private SymmetricConcrete symmetricConcrete;

    private String currentOption = "Text";

    public SymmetricActivity() {
        setLayout(new BorderLayout());
        symmetricConcrete = new SymmetricConcrete();
        aesFragment = new AESFragment(symmetricConcrete);
        decorator = aesFragment;
        symmetricConcrete.setAlgorithm(SymmetricFactory.getSymmetric(Algorithm.AES));

        createContentPane();
        createInputPanel();
        createInputTextPanel();
        createInputFilePanel();
        createInputAlgorithmPanel();
        createResultPanel();
        createBottomBar();

        algorithmCbb.setSelectedIndex(0);
        symmetricConcrete.setAlgorithmCbb(algorithmCbb);
        gbc.gridy = 100;
        gbc.weighty = 1.0;
        contentPane.add(new JPanel(), gbc);
    }

    private void createContentPane() {
        contentPane = new JPanel(new GridBagLayout());

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;

        JScrollPane scrollPane = new JScrollPane(contentPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void createInputPanel() {
        gbc.insets = new Insets(Dimensions.MARGIN_VERTICAL, 0, Dimensions.MARGIN_VERTICAL, Dimensions.MARGIN_HORIZONTAL);
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, Dimensions.MARGIN_HORIZONTAL, 0));
        MaterialLabel label = new MaterialLabel("Loại dữ liệu:");
        inputPanel.add(label);

        JRadioButton textOption = new JRadioButton("Text");
        JRadioButton fileOption = new JRadioButton("File");
        textOption.setSelected(true);

        inputPanel.add(textOption);
        inputPanel.add(fileOption);

        ButtonGroup btnGroup = new ButtonGroup();
        btnGroup.add(textOption);
        btnGroup.add(fileOption);

        textOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentOption = "Text";
                fileLoader.setVisible(false);
                inputTextPanel.setVisible(true);
                refreshUI();
            }
        });

        fileOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentOption = "File";
                inputTextPanel.setVisible(false);
                fileLoader.setVisible(true);
                refreshUI();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPane.add(inputPanel, gbc);
    }

    private void createInputTextPanel() {
        inputTextPanel = new JPanel();
        inputTextPanel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        gbc.insets = new Insets(Dimensions.MARGIN_VERTICAL, 0, Dimensions.MARGIN_VERTICAL, Dimensions.MARGIN_HORIZONTAL);
        constraints.insets = new Insets(0, Dimensions.MARGIN_HORIZONTAL, 0, 0);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;

        inputTextPanel.add(new MaterialLabel("Nhập nội dung:"), constraints);

        inputTextArea = new JTextArea(8, 10);
        inputTextArea.setLineWrap(true);
        inputTextArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(inputTextArea);
        scrollPane.setPreferredSize(new Dimension(200, inputTextArea.getPreferredSize().height));

        constraints.gridx = 1;
        constraints.weightx = 1.0;
        inputTextPanel.add(scrollPane, constraints);

        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPane.add(inputTextPanel, gbc);
    }

    private void createInputFilePanel() {
        fileLoader = new FileLoader("File");
        gbc.insets = new Insets(Dimensions.MARGIN_VERTICAL, 0, Dimensions.MARGIN_VERTICAL, Dimensions.MARGIN_HORIZONTAL);
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPane.add(fileLoader, gbc);

        fileLoader.setVisible(false);
        fileLoader.setContainerPopup(this.getParent());
        fileLoader.browserBtn.addActionListener(e -> fileLoader.browseFile(null));
    }

    private void createInputAlgorithmPanel() {
        JPanel algorithmPanel = new JPanel(new GridBagLayout());
        algorithmCbb = new MaterialCombobox<>(SymmetricConcrete.algorithmSupported.toArray(new String[0]));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(0, Dimensions.MARGIN_HORIZONTAL, 0, Dimensions.MARGIN_HORIZONTAL);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;

        algorithmPanel.add(new MaterialLabel("Thuật toán:"), constraints);

        constraints.insets = new Insets(0, 0, 0, Dimensions.MARGIN_HORIZONTAL);
        constraints.gridx = 1;
        constraints.weightx = 1;
        algorithmPanel.add(algorithmCbb, constraints);

        constraints.gridx = 2;
        constraints.weightx = 0;
        MaterialLabel modeLabel = new MaterialLabel("Mode:");
        modeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        algorithmPanel.add(modeLabel, constraints);

        constraints.gridx = 3;
        constraints.weightx = 1;
        algorithmPanel.add(symmetricConcrete.modeCbb, constraints);

        constraints.gridx = 4;
        constraints.weightx = 0;
        MaterialLabel paddingLabel = new MaterialLabel("Padding:");
        paddingLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        algorithmPanel.add(paddingLabel, constraints);

        constraints.gridx = 5;
        constraints.weightx = 1;
        algorithmPanel.add(symmetricConcrete.paddingCbb, constraints);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(Dimensions.MARGIN_VERTICAL, 0, 0, 0);
        contentPane.add(algorithmPanel, gbc);

        algorithmCbb.addActionListener(e -> {
            contentPane.remove(decorator);
            String algorithm = algorithmCbb.getSelectedItem().toString();
            decorator = getDecorator(algorithm);
            addDecorator(decorator);
            refreshUI();
        });
    }

    private SymmetricDecorator getDecorator(String algorithm) {
        return switch (algorithm) {
            case "AES" -> aesFragment;
            default -> aesFragment;
        };
    }

    private void addDecorator(SymmetricDecorator decorator) {
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.gridx = 0;
        gbc.gridy = 3;
        contentPane.add(decorator, gbc);
        decorator.display();
    }

    private void createResultPanel() {
        JPanel resultPanel = new JPanel(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(0, 0, Dimensions.MARGIN_VERTICAL, 0);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;

        JLabel resultLabel = new JLabel("Kết quả:");
        resultLabel.setPreferredSize(new Dimension(Dimensions.LABEL_WIDTH, resultLabel.getPreferredSize().height));
        resultPanel.add(resultLabel, constraints);

        resultPanel.add(resultLabel, constraints);

        encryptTextArea = new JTextArea(8, 10);
        JScrollPane scrollPane = new JScrollPane(encryptTextArea);
        scrollPane.setPreferredSize(new Dimension(200, encryptTextArea.getPreferredSize().height));

        decryptTextArea = new JTextArea(8, 10);
        JScrollPane scrollPane2 = new JScrollPane(decryptTextArea);
        scrollPane2.setPreferredSize(new Dimension(200, decryptTextArea.getPreferredSize().height));

        resultTabbedPane = new JTabbedPane();
        constraints.insets = new Insets(Dimensions.MARGIN_VERTICAL, 0, Dimensions.MARGIN_VERTICAL, 0);
        resultTabbedPane.add("Mã hóa", scrollPane);
        resultTabbedPane.add("Giải mã", scrollPane2);

        constraints.weightx = 1.0;
        constraints.gridy = 1;
        resultPanel.add(resultTabbedPane, constraints);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, Dimensions.MARGIN_HORIZONTAL, 0, Dimensions.MARGIN_HORIZONTAL);
        contentPane.add(resultPanel, gbc);
    }

    private void createBottomBar() {
        JPanel bottomBar = new JPanel();
        add(bottomBar, BorderLayout.SOUTH);

        JButton btnEncrypt = new JButton("Encrypt");
        JButton btnDecrypt = new JButton("Decrypt");

        bottomBar.setLayout(new FlowLayout(FlowLayout.RIGHT, Dimensions.MARGIN_HORIZONTAL, 0));
        bottomBar.add(btnEncrypt);
        bottomBar.add(btnDecrypt);

        btnEncrypt.addActionListener(e -> {
            if ("Text".equals(currentOption)) {
                encryptText();
            } else if ("File".equals(currentOption)) {
                encryptFile();
            }
        });

        btnDecrypt.addActionListener(e -> {
            if ("Text".equals(currentOption)) {
                decryptText();
            } else if ("File".equals(currentOption)) {
                decryptFile();
            }
        });
    }

    private void encryptText() {
        String result = decorator.encryptBase64(inputTextArea.getText());

        if (result != null) {
            encryptTextArea.setText(result);

            String decrypt = decorator.decryptBase64(result);
            decryptTextArea.setText(decrypt);
        } else {
            encryptTextArea.setText("Lỗi mã hóa");
        }

        resultTabbedPane.setSelectedIndex(0);
    }

    private void encryptFile() {
        String srcPath = fileLoader.getPath();
        if (srcPath == null || srcPath.isEmpty()) {
            fileLoader.error("Vui lòng nhập đường dẫn");
            return;
        }

        FileHelper fileHelper = new FileHelper();
        String desPath = fileHelper.showSaveFile(getRootPane(), srcPath, null);

        if (desPath != null && !desPath.isEmpty()) {
            boolean res = decorator.encryptFile(srcPath, desPath);

            if (res) {
                int option = JOptionPane.showOptionDialog(
                        getRootPane(),
                        "Mã hóa thành công!\nFile đã lưu ở: " + desPath,
                        "Encryption Success",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        new Object[]{"OK", "Xem tệp"},
                        "OK"
                );

                if (option == 1) {
                    File file = new File(desPath);
                    try {
                        Desktop.getDesktop().open(file.getParentFile());
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(contentPane,
                                "Không thể mở thư mục chứa file.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    private void decryptText() {
        String result = decorator.decryptBase64(inputTextArea.getText());
        if (result != null) {
            decryptTextArea.setText(result);
            encryptTextArea.setText("");
        } else {
            decryptTextArea.setText("Lỗi giải mã");
        }
        resultTabbedPane.setSelectedIndex(1);
    }

    private void decryptFile() {
        String srcPath = fileLoader.getPath();
        if (srcPath == null || srcPath.isEmpty()) {
            fileLoader.error("Vui lòng nhập đường dẫn");
            return;
        }

        FileHelper fileHelper = new FileHelper();
        String desPath = fileHelper.showSaveFile(getRootPane(), srcPath, null);

        if (desPath != null && !desPath.isEmpty()) {
            boolean res = decorator.decryptFile(srcPath, desPath);

            if (res) {
                int option = JOptionPane.showOptionDialog(
                        getRootPane(),
                        "Giải mã thành công!\nFile đã lưu ở: " + desPath,
                        "Decryption Success",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        new Object[]{"OK", "Xem tệp"},
                        "OK"
                );

                if (option == 1) {
                    File file = new File(desPath);
                    try {
                        Desktop.getDesktop().open(file.getParentFile());
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(contentPane,
                                "Không thể mở thư mục chứa file.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    public static SymmetricActivity getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SymmetricActivity();
        }
        return INSTANCE;
    }

    public void refreshUI() {
        contentPane.revalidate();
        contentPane.repaint();
    }
}