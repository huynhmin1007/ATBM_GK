package ui.view.activity;

import encryption.asymmetric.ASymmetricFactory;
import encryption.common.Algorithm;
import encryption.symmetric.Symmetric;
import encryption.symmetric.SymmetricFactory;
import ui.common.Dimensions;
import ui.view.component.FileLoader;
import ui.view.component.MaterialCombobox;
import ui.view.component.MaterialLabel;
import ui.view.custom.BaseActivity;
import ui.view.custom.CustomTitledBorder;
import ui.view.fragment.asymmetric.ASymmetricConcrete;
import ui.view.fragment.asymmetric.ASymmetricDecorator;
import ui.view.fragment.asymmetric.RSAFragment;
import ui.view.fragment.symmetric.*;
import utils.FileHelper;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

public class ASymmetricActivity extends BaseActivity {

    private static ASymmetricActivity INSTANCE;

    private GridBagConstraints gbc;

    private JPanel contentPane;

    private JPanel inputTextPanel;
    private JTextArea inputTextArea;
    private FileLoader fileLoader;
    private MaterialCombobox<String> algorithmCbb;
    private JPanel resultPanel;
    private JTabbedPane resultTabbedPane;
    private JTextArea encryptTextArea, decryptTextArea;

    private ASymmetricDecorator rsaFragment;
    private ASymmetricDecorator decorator;
    private ASymmetricConcrete asymmetricConcrete;

    private JPanel sAlgorithmInputPanel, symmetricPanel;
    private MaterialCombobox<String> sAlgorithmCbb;
    private SymmetricConcrete symmetricConcrete;
    private SymmetricDecorator aesFragment, desFragment, arcfourFragment, blowfishFragment, chacha20Fragment;
    private SymmetricDecorator sDecorator;

    private String currentOption = "Text";

    public ASymmetricActivity() {
        setLayout(new BorderLayout());

        asymmetricConcrete = new ASymmetricConcrete();
        asymmetricConcrete.setAlgorithm(ASymmetricFactory.getASymmetric(Algorithm.RSA));

        rsaFragment = new RSAFragment(asymmetricConcrete);

        decorator = rsaFragment;

        symmetricConcrete = new SymmetricConcrete();
        symmetricConcrete.setAlgorithm(SymmetricFactory.getSymmetric(Algorithm.AES));
        symmetricConcrete.saveKey.setVisible(false);
        symmetricConcrete.loadKey.setVisible(false);

        aesFragment = new AESFragment(symmetricConcrete);
        desFragment = new DESFragment(symmetricConcrete);
        arcfourFragment = new ARCFOURFragment(symmetricConcrete);
        blowfishFragment = new BlowfishFragment(symmetricConcrete);
        chacha20Fragment = new ChaCha20Fragment(symmetricConcrete);

        sDecorator = aesFragment;

        createContentPane();
        createInputPanel();
        createInputTextPanel();
        createInputFilePanel();
        createInputAlgorithmPanel();
        createInputSymmetricAlgorithmPanel();
        createResultPanel();
        createBottomBar();

        algorithmCbb.setSelectedIndex(0);
        asymmetricConcrete.setAlgorithmCbb(algorithmCbb);

        sAlgorithmCbb.setSelectedIndex(0);
        symmetricConcrete.setAlgorithmCbb(sAlgorithmCbb);

        gbc.gridy = 100;
        gbc.weighty = 1.0;
        contentPane.add(new JPanel(), gbc);
    }

    private void createInputSymmetricAlgorithmPanel() {
        sAlgorithmInputPanel = new JPanel(new GridBagLayout());
        sAlgorithmCbb = new MaterialCombobox<>(SymmetricConcrete.algorithmSupported.toArray(new String[0]));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = Dimensions.DEFAULT_INSETS;

        sAlgorithmInputPanel.add(new MaterialLabel("Thuật toán:"), constraints);
        constraints.gridx = 1;
        constraints.weightx = 1;
        sAlgorithmInputPanel.add(sAlgorithmCbb, constraints);

        constraints.gridx = 2;
        constraints.weightx = 0;
        MaterialLabel modeLabel = new MaterialLabel("Mode:");
        modeLabel.info.setHorizontalAlignment(SwingConstants.RIGHT);
        sAlgorithmInputPanel.add(modeLabel, constraints);

        constraints.gridx = 3;
        constraints.weightx = 1;
        sAlgorithmInputPanel.add(symmetricConcrete.modeCbb, constraints);

        constraints.gridx = 4;
        constraints.weightx = 0;
        MaterialLabel paddingLabel = new MaterialLabel("Padding:");
        paddingLabel.info.setHorizontalAlignment(SwingConstants.RIGHT);
        sAlgorithmInputPanel.add(paddingLabel, constraints);

        constraints.gridx = 5;
        constraints.weightx = 1;
        sAlgorithmInputPanel.add(symmetricConcrete.paddingCbb, constraints);

        symmetricPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints2 = new GridBagConstraints();
        constraints2.fill = GridBagConstraints.HORIZONTAL;
        constraints2.anchor = GridBagConstraints.WEST;
        constraints2.insets = new Insets(Dimensions.MARGIN_VERTICAL * 2, 0, 0, 0);
        constraints2.weightx = 1;

        symmetricPanel.add(sAlgorithmInputPanel, constraints2);

        constraints2.gridy = 1;
        constraints2.insets = Dimensions.ZERO_INSETS;
        symmetricPanel.add(sDecorator, constraints2);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.insets = new Insets(Dimensions.MARGIN_VERTICAL * 3, 0, 0, 0);
        gbc.weightx = 1;
        ImageIcon infoIcon = new ImageIcon(getClass().getClassLoader().getResource("info.png"));

        CustomTitledBorder titledBorder = new CustomTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Chọn thuật toán Mã Hóa File"
        );
        titledBorder.setIcon(infoIcon);
        symmetricPanel.setBorder(titledBorder);
        symmetricPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getX() >= titledBorder.getIconX() &&
                        e.getX() <= titledBorder.getIconX() + infoIcon.getIconWidth() &&
                        e.getY() >= titledBorder.getIconY() &&
                        e.getY() <= titledBorder.getIconY() + infoIcon.getIconHeight()) {
                    JOptionPane.showMessageDialog(getRootPane(),
                            "Quá trình mã hóa bao gồm hai bước:\n" +
                                    "1. Dữ liệu trong file sẽ được mã hóa bằng thuật toán mã hóa đối xứng.\n" +
                                    "2. Khóa mã hóa đối xứng sẽ được mã hóa bằng thuật toán mã hóa bất đối xứng\n" +
                                    "   và được lưu trữ cùng với file.\n\n" +
                                    "Lưu ý: Để giải mã, cần sử dụng khóa bí mật tương ứng của thuật toán mã hóa bất đối xứng.",
                            "Lưu ý",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        symmetricPanel.setVisible(false);
        contentPane.add(symmetricPanel, gbc);

        sAlgorithmCbb.addActionListener(e -> {
            symmetricPanel.remove(sDecorator);
            String algorithm = sAlgorithmCbb.getSelectedItem().toString();
            sDecorator.close();
            sDecorator = getSDecorator(algorithm);
            addDecorator(sDecorator);
            refreshUI();
        });
    }

    private SymmetricDecorator getSDecorator(String algorithm) {
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
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = Dimensions.ZERO_INSETS;
        constraints.gridy = 1;

        symmetricPanel.add(decorator, constraints);
        decorator.display();
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
        MaterialLabel label = new MaterialLabel("Loại dữ liệu:");
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
                symmetricPanel.setVisible(false);
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
                symmetricPanel.setVisible(true);
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
        gbc.insets = new Insets(Dimensions.MARGIN_VERTICAL, 0, Dimensions.MARGIN_VERTICAL, 0);
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPane.add(fileLoader, gbc);

        fileLoader.setVisible(false);
        fileLoader.setContainerPopup(this.getParent());
        fileLoader.browserBtn.addActionListener(e -> fileLoader.browseFile(null));
        gbc.insets = Dimensions.ZERO_INSETS;
    }

    private void createInputAlgorithmPanel() {
        JPanel algorithmPanel = new JPanel(new GridBagLayout());
        algorithmCbb = new MaterialCombobox<>(ASymmetricConcrete.algorithmSupported.toArray(new String[0]));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = Dimensions.DEFAULT_INSETS;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;

        algorithmPanel.add(new MaterialLabel("Thuật toán:"), constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;
        algorithmPanel.add(algorithmCbb, constraints);

        constraints.gridx = 2;
        constraints.weightx = 0;
        MaterialLabel modeLabel = new MaterialLabel("Mode:");
        modeLabel.info.setHorizontalAlignment(SwingConstants.RIGHT);
        algorithmPanel.add(modeLabel, constraints);

        constraints.gridx = 3;
        constraints.weightx = 1;
        algorithmPanel.add(asymmetricConcrete.modeCbb, constraints);

        constraints.gridx = 4;
        constraints.weightx = 0;
        MaterialLabel paddingLabel = new MaterialLabel("Padding:");
        paddingLabel.info.setHorizontalAlignment(SwingConstants.RIGHT);
        algorithmPanel.add(paddingLabel, constraints);

        constraints.gridx = 5;
        constraints.weightx = 1;
        algorithmPanel.add(asymmetricConcrete.paddingCbb, constraints);

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

    private ASymmetricDecorator getDecorator(String algorithm) {
        return switch (algorithm) {
            case "RSA" -> rsaFragment;
            default -> rsaFragment;
        };
    }

    private void addDecorator(ASymmetricDecorator decorator) {
        gbc.insets = Dimensions.ZERO_INSETS;
        gbc.gridx = 0;
        gbc.gridy = 3;
        contentPane.add(decorator, gbc);
        decorator.display();
    }

    private void createResultPanel() {
        resultPanel = new JPanel(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = Dimensions.HORIZONTAL_INSETS;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;

        JLabel resultLabel = new JLabel("Kết quả:");
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
        resultTabbedPane.add("Mã hóa", scrollPane);
        resultTabbedPane.add("Giải mã", scrollPane2);

        constraints.weightx = 1.0;
        constraints.gridy = 1;
        resultPanel.add(resultTabbedPane, constraints);

        gbc.gridy = 4;
        gbc.insets = Dimensions.ZERO_INSETS;
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

            if ((asymmetricConcrete.enPublic.isSelected() && !asymmetricConcrete.privateKeyEdt.getText().trim().isEmpty())
                    || asymmetricConcrete.enPrivate.isSelected() && !asymmetricConcrete.publicKeyEdt.getText().trim().isEmpty()) {
                String decrypt = decorator.decryptBase64(result);
                decryptTextArea.setText(decrypt);
            } else
                decryptTextArea.setText("");
        } else {
            encryptTextArea.setText("Lỗi mã hóa");
        }

        resultTabbedPane.setSelectedIndex(0);
    }

    private void encryptFile() {
        if (!sDecorator.validateInput())
            return;

        sDecorator.configure();
        Symmetric symmetric = sDecorator.getAlgorithm();

        String srcPath = fileLoader.getPath();
        if (srcPath == null || srcPath.isEmpty()) {
            fileLoader.error("Vui lòng nhập đường dẫn");
            return;
        } else
            fileLoader.hideError();

        FileHelper fileHelper = new FileHelper();
        String desPath = fileHelper.showSaveFile(getRootPane(), srcPath, null);

        if (desPath != null && !desPath.isEmpty()) {

            boolean res = decorator.encryptFile(srcPath, desPath, symmetric);

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
        } else
            fileLoader.hideError();

        FileHelper fileHelper = new FileHelper();
        String desPath = fileHelper.showSaveFile(getRootPane(), srcPath, null);

        if (desPath != null && !desPath.isEmpty()) {
            Symmetric symmetric = decorator.decryptFile(srcPath, desPath);
            boolean res = symmetric != null;

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

                getSDecorator(symmetric.getAlgorithm()).setAlgorithm(symmetric);
                sAlgorithmCbb.setSelectedItem(symmetric.getAlgorithm());
                sDecorator.displayWithAttributes();
            }
        }
    }

    public static ASymmetricActivity getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ASymmetricActivity();
        }
        return INSTANCE;
    }

    public void refreshUI() {
        contentPane.revalidate();
        contentPane.repaint();
    }
}