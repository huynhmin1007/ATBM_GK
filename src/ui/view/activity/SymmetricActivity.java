package ui.view.activity;

import encryption.common.Algorithm;
import encryption.symmetric.SymmetricFactory;
import ui.common.Dimensions;
import ui.view.component.FileLoader;
import ui.view.component.MaterialCombobox;
import ui.view.component.MaterialLabel;
import ui.view.component.SpinningLoading;
import ui.view.custom.BaseActivity;
import ui.view.fragment.symmetric.*;
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
    private JPanel resultPanel;
    private JTabbedPane resultTabbedPane;
    private JTextArea encryptTextArea, decryptTextArea;

    private SymmetricDecorator aesFragment, desFragment, arcfourFragment, blowfishFragment, chacha20Fragment;

    private SymmetricDecorator decorator;
    private SymmetricConcrete symmetricConcrete;

    private String currentOption = "Text";
    private SpinningLoading loading;

    public SymmetricActivity() {
        setLayout(new BorderLayout());

        symmetricConcrete = new SymmetricConcrete();
        symmetricConcrete.setAlgorithm(SymmetricFactory.getSymmetric(Algorithm.AES));

        aesFragment = new AESFragment(symmetricConcrete);
        desFragment = new DESFragment(symmetricConcrete);
        arcfourFragment = new ARCFOURFragment(symmetricConcrete);
        blowfishFragment = new BlowfishFragment(symmetricConcrete);
        chacha20Fragment = new ChaCha20Fragment(symmetricConcrete);

        decorator = aesFragment;

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
        gbc.insets = Dimensions.ZERO_INSETS;

        JScrollPane scrollPane = new JScrollPane(contentPane);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void createInputPanel() {
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, Dimensions.MARGIN_HORIZONTAL, 0));
        MaterialLabel label = new MaterialLabel("Input type:");
        inputPanel.add(label);

        JRadioButton textOption = new JRadioButton("Text");
        JRadioButton fileOption = new JRadioButton("File");
        textOption.setMargin(new Insets(0, 10, 0, 10));
        fileOption.setMargin(new Insets(0, 10, 0, 0));
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
                resultPanel.setVisible(true);
                refreshUI();
            }
        });

        fileOption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentOption = "File";
                inputTextPanel.setVisible(false);
                fileLoader.setVisible(true);
                resultPanel.setVisible(false);
                refreshUI();
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = Dimensions.VERTICAL_INSETS;
        contentPane.add(inputPanel, gbc);
        gbc.insets = Dimensions.ZERO_INSETS;
    }

    private void createInputTextPanel() {
        inputTextPanel = new JPanel();
        inputTextPanel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = Dimensions.DEFAULT_INSETS;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.NORTHWEST;

        inputTextPanel.add(new MaterialLabel("Input Text:"), constraints);

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
        gbc.insets = Dimensions.ZERO_INSETS;
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPane.add(fileLoader, gbc);

        fileLoader.setVisible(false);
        fileLoader.browserBtn.addActionListener(e -> fileLoader.browseFile(null));
    }

    private void createInputAlgorithmPanel() {
        JPanel algorithmPanel = new JPanel(new GridBagLayout());
        algorithmCbb = new MaterialCombobox<>(SymmetricConcrete.algorithmSupported.toArray(new String[0]));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = Dimensions.DEFAULT_INSETS;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;

        algorithmPanel.add(new MaterialLabel("Algorithm:"), constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;
        algorithmPanel.add(algorithmCbb, constraints);

        constraints.gridx = 2;
        constraints.weightx = 0;
        algorithmPanel.add(new MaterialLabel("Mode:", SwingConstants.RIGHT), constraints);

        constraints.gridx = 3;
        constraints.weightx = 1;
        algorithmPanel.add(symmetricConcrete.modeCbb, constraints);

        constraints.gridx = 4;
        constraints.weightx = 0;
        algorithmPanel.add(new MaterialLabel("Padding:", SwingConstants.RIGHT), constraints);

        constraints.gridx = 5;
        constraints.weightx = 1;
        algorithmPanel.add(symmetricConcrete.paddingCbb, constraints);

        gbc.gridx = 0;
        gbc.gridy = 2;
        contentPane.add(algorithmPanel, gbc);

        algorithmCbb.addActionListener(e -> {
            contentPane.remove(decorator);
            String algorithm = algorithmCbb.getSelectedItem().toString();
            decorator.close();
            decorator = getDecorator(algorithm);
            addDecorator(decorator);
            refreshUI();
        });
    }

    private SymmetricDecorator getDecorator(String algorithm) {
        return switch (algorithm) {
            case "AES" -> aesFragment;
            case "DES" -> desFragment;
            case "ARCFOUR" -> arcfourFragment;
            case "Blowfish" -> blowfishFragment;
            case "ChaCha20" -> chacha20Fragment;
            default -> aesFragment;
        };
    }

    private void addDecorator(SymmetricDecorator decorator) {
        gbc.insets = Dimensions.ZERO_INSETS;
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weighty = 0;
        contentPane.add(decorator, gbc);
        decorator.display();
    }

    private void createResultPanel() {
        resultPanel = new JPanel(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = Dimensions.HORIZONTAL_INSETS;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;

        MaterialLabel resultLabel = new MaterialLabel("Result:");
        resultLabel.setPreferredSize(new Dimension(Dimensions.LABEL_WIDTH, resultLabel.getPreferredSize().height));
        resultPanel.add(resultLabel, constraints);

        resultPanel.add(resultLabel, constraints);

        encryptTextArea = new JTextArea(8, 10);
        encryptTextArea.setEditable(false);
        encryptTextArea.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(encryptTextArea);
        scrollPane.setPreferredSize(new Dimension(200, encryptTextArea.getPreferredSize().height));

        decryptTextArea = new JTextArea(8, 10);
        decryptTextArea.setEditable(false);
        decryptTextArea.setBackground(Color.WHITE);
        JScrollPane scrollPane2 = new JScrollPane(decryptTextArea);
        scrollPane2.setPreferredSize(new Dimension(200, decryptTextArea.getPreferredSize().height));

        resultTabbedPane = new JTabbedPane();
        constraints.insets = new Insets(Dimensions.MARGIN_VERTICAL, Dimensions.MARGIN_HORIZONTAL, 0, Dimensions.MARGIN_HORIZONTAL);
        resultTabbedPane.add("Encryption", scrollPane);
        resultTabbedPane.add("Decryption", scrollPane2);

        constraints.weightx = 1.0;
        constraints.gridy = 1;
        resultPanel.add(resultTabbedPane, constraints);

        gbc.gridy = 4;
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
            encryptTextArea.setText("Encryption failed");
        }

        resultTabbedPane.setSelectedIndex(0);
    }

    private void encryptFile() {
        String srcPath = fileLoader.getPath();
        if (srcPath == null || srcPath.isEmpty()) {
            fileLoader.error();
            return;
        } else
            fileLoader.hideError();

        FileHelper fileHelper = new FileHelper();
        String desPath = fileHelper.showSaveFile(getRootPane(), srcPath, null);

        if (desPath != null && !desPath.isEmpty()) {
            boolean res = decorator.encryptFile(srcPath, desPath);

            if (res) {
                String message = "<html>Encryption successfully!<br>File saved in: <b>" + desPath + "</b></html>";
                int option = JOptionPane.showOptionDialog(
                        getRootPane(),
                        message,
                        "Success",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        new Object[]{"OK", "Check File"},
                        "OK"
                );

                if (option == 1) {
                    File file = new File(desPath);
                    try {
                        Desktop.getDesktop().open(file.getParentFile());
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(contentPane,
                                "Unable to open the File.",
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
            decryptTextArea.setText("Decryption failed");
        }
        resultTabbedPane.setSelectedIndex(1);
    }

    private void decryptFile() {
        String srcPath = fileLoader.getPath();
        if (srcPath == null || srcPath.isEmpty()) {
            fileLoader.error();
            return;
        } else
            fileLoader.hideError();

        FileHelper fileHelper = new FileHelper();
        String desPath = fileHelper.showSaveFile(getRootPane(), srcPath, null);

        if (desPath != null && !desPath.isEmpty()) {
            boolean res = decorator.decryptFile(srcPath, desPath);

            if (res) {
                String message = "<html>Decryption successfully!<br>File saved in: <b>" + desPath + "</b></html>";
                int option = JOptionPane.showOptionDialog(
                        getRootPane(),
                        message,
                        "Success",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        new Object[]{"OK", "Check File"},
                        "OK"
                );

                if (option == 1) {
                    File file = new File(desPath);
                    try {
                        Desktop.getDesktop().open(file.getParentFile());
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(contentPane,
                                "Unable to open the File.",
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