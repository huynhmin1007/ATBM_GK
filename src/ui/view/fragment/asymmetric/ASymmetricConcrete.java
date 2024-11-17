package ui.view.fragment.asymmetric;

import encryption.asymmetric.ASymmetricFactory;
import encryption.asymmetric.RSA;
import encryption.common.Algorithm;
import encryption.symmetric.Symmetric;
import ui.common.Dimensions;
import ui.view.component.EditText;
import ui.view.component.MaterialCombobox;
import ui.view.component.MaterialLabel;
import utils.FileHelper;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.security.*;
import java.util.List;
import java.util.*;
import java.util.function.Function;

public class ASymmetricConcrete extends JPanel implements ASymmetricFragment {

    public static final List<String> algorithmSupported = Arrays.asList("RSA");
    public RSA algorithm;
    public String mode, padding;

    public GridBagConstraints gbc;
    public MaterialCombobox<String> modeCbb, paddingCbb;
    public MaterialCombobox<Integer> keySizeCbb;
    public EditText privateKeyEdt, publicKeyEdt;
    public MaterialLabel privateKeyLabel, publicKeyLabel;
    public JButton loadKey, genKey, saveKey;
    public JRadioButton enPrivate, enPublic;

    public String oldPath;

    public MaterialCombobox<String> algorithmCbb;

    private ASymmetricDecorator controller;

    public ASymmetricConcrete() {
        setLayout(new GridBagLayout());

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = Dimensions.ZERO_INSETS;
        gbc.weightx = 1.0;

        createModeAndPaddingGUI();
        createKeyGUI();
        createButtonGroup();

        genKey.addActionListener(e -> {
            generateKey();
        });

        saveKey.addActionListener(e -> {
            saveKey();
        });

        loadKey.addActionListener(e -> {
            loadKey();
        });
    }

    private void createModeAndPaddingGUI() {
        modeCbb = new MaterialCombobox<>();
        paddingCbb = new MaterialCombobox<>();

        modeCbb.addActionListener(e -> {
            handleModeChangeListener();
        });

        paddingCbb.addActionListener(e -> {
            handlePaddingChangeListener();
        });
    }

    private void createKeyGUI() {
        MaterialLabel keySizeLabel = new MaterialLabel("Key Size:");
        keySizeCbb = new MaterialCombobox<>();
        privateKeyLabel = new MaterialLabel("Private Key:");
        privateKeyEdt = new EditText();
        privateKeyEdt.setPreferredSize(new Dimension(140, privateKeyEdt.getPreferredSize().height));

        publicKeyLabel = new MaterialLabel("Public Key:");
        publicKeyEdt = new EditText();
        publicKeyEdt.setPreferredSize(new Dimension(140, publicKeyEdt.getPreferredSize().height));

        enPrivate = new JRadioButton("Private Key");
        enPublic = new JRadioButton("Public Key");
        MaterialLabel enTypeLabel = new MaterialLabel("Encryption Key:");
        JPanel enTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, Dimensions.MARGIN_HORIZONTAL, Dimensions.MARGIN_VERTICAL + 5));
        enTypeLabel.setPreferredSize(new Dimension(enTypeLabel.getPreferredSize().width + Dimensions.MARGIN_HORIZONTAL, enTypeLabel.getPreferredSize().height));

        enTypePanel.add(enTypeLabel);

        enTypePanel.add(enPublic);
        enPublic.setMargin(new Insets(0, 0, 0, 3 * Dimensions.MARGIN_HORIZONTAL));
        enTypePanel.add(enPrivate);
        enPublic.setSelected(true);

        ButtonGroup btnGroup = new ButtonGroup();
        btnGroup.add(enPrivate);
        btnGroup.add(enPublic);

        JPanel keyPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = Dimensions.DEFAULT_INSETS;

        JPanel keySizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, Dimensions.MARGIN_HORIZONTAL, Dimensions.MARGIN_VERTICAL));
        keySizeLabel.setPreferredSize(new Dimension(keySizeLabel.getPreferredSize().width + Dimensions.MARGIN_HORIZONTAL, keySizeLabel.getPreferredSize().height));

        keySizePanel.add(keySizeLabel);
        keySizePanel.add(keySizeCbb);

        constraints.gridx = 0;
        constraints.weightx = 1.0;
        constraints.gridwidth = 2;
        constraints.insets = Dimensions.ZERO_INSETS;
        keyPanel.add(keySizePanel, constraints);

        constraints.gridy = 1;
        keyPanel.add(enTypePanel, constraints);

        constraints.gridwidth = 1;
        constraints.insets = Dimensions.DEFAULT_INSETS;
        constraints.gridy = 2;
        constraints.weightx = 0.0;
        keyPanel.add(privateKeyLabel, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1.0;
        keyPanel.add(privateKeyEdt, constraints);

        constraints.gridwidth = 1;
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.weightx = 0.0;
        keyPanel.add(publicKeyLabel, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1.0;
        keyPanel.add(publicKeyEdt, constraints);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(keyPanel, gbc);
    }

    private void createButtonGroup() {
        genKey = new JButton("Generate");
        loadKey = new JButton("Load");
        saveKey = new JButton("Save");

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, Dimensions.MARGIN_HORIZONTAL, 0));
        btnPanel.add(genKey);
        btnPanel.add(loadKey);
        btnPanel.add(saveKey);

        gbc.gridy = 1;
        add(btnPanel, gbc);
    }

    public GridBagConstraints getGbc() {
        return gbc;
    }

    public boolean validateKey() {
        String privateKey = privateKeyEdt.getText().trim();
        String publicKey = publicKeyEdt.getText().trim();

        int count = 0;

        if (!privateKey.isEmpty()) {
            privateKeyEdt.hideError();
            privateKeyLabel.deleteNotify();
            count++;
        }

        if (!publicKey.isEmpty()) {
            publicKeyEdt.hideError();
            publicKeyLabel.deleteNotify();
            count++;
        }

        if (privateKey.isEmpty()) {
            privateKeyEdt.error("Vui lòng nhập khóa");
            privateKeyLabel.setNotify("");
        }

        if (publicKey.isEmpty()) {
            publicKeyEdt.error("Vui lòng nhập khóa");
            publicKeyLabel.setNotify("");
        }

        return count == 2;
    }

    @Override
    public void handleModeChangeListener() {
        if (modeCbb.getSelectedItem() == null)
            return;

        String mode = modeCbb.getSelectedItem().toString();
        this.mode = mode;

        paddingCbb.removeAllItems();
        Arrays.stream(getPadding(mode))
                .forEach(paddingCbb::addItem);

        int[] keySize = algorithm.getKeySizeSupported();
        setKeySize(keySize);
    }

    @Override
    public void handlePaddingChangeListener() {
        if (paddingCbb.getSelectedItem() == null)
            return;

        this.padding = paddingCbb.getSelectedItem().toString();
    }

    @Override
    public void saveKey() {
        if (!validateKey())
            return;

        FileHelper fileHelper = new FileHelper();
        String path = fileHelper.showSaveFile(getRootPane(), oldPath, new String[]{"dat", "txt"});

        if (path != null && !path.isEmpty()) {
            oldPath = path;

            try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(path)))) {
                out.writeUTF(algorithm.getAlgorithm());
                out.writeUTF(mode);
                out.writeUTF(padding);
                out.writeInt(resolveValue(sf -> sf.getKeySize()));
                out.writeUTF(privateKeyEdt.getText());
                out.writeUTF(publicKeyEdt.getText());

                if (controller != null) {
                    controller.saveKey(out);
                }

                int option = JOptionPane.showOptionDialog(
                        getRootPane(),
                        "Lưu khóa thành công",
                        "Lưu thành công",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        new Object[]{"OK", "Xem tệp"},
                        "OK"
                );

                if (option == 1) {
                    Desktop.getDesktop().open(new File(path).getParentFile());
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(getRootPane(), "Không thể lưu tệp. Vui lòng thử lại.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void saveKey(DataOutputStream out) {
    }

    @Override
    public void loadKey() {
        FileHelper fileHelper = new FileHelper();
        File file = fileHelper.showOpenFile(getRootPane(), oldPath, new String[]{"dat", "txt"});

        if (file != null && file.exists()) {
            oldPath = file.getAbsolutePath();

            try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
                String algorithmName = in.readUTF();

                if (!algorithmSupported.contains(algorithmName)) {
                    JOptionPane.showMessageDialog(getRootPane(), "Tệp không hợp lệ. Vui lòng thử lại.",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                setAlgorithm(ASymmetricFactory.getASymmetric(Algorithm.valueOf(algorithmName)));
                if (algorithmCbb != null) {
                    algorithmCbb.setSelectedItem(algorithmName);
                }

                String mode = in.readUTF();
                String padding = in.readUTF();


                if (!exist(getMode(), mode)) {
                    JOptionPane.showMessageDialog(getRootPane(), "Tệp không hợp lệ. Vui lòng thử lại.",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                this.mode = mode;

                if (!exist(getPadding(mode), padding)) {
                    JOptionPane.showMessageDialog(getRootPane(), "Tệp không hợp lệ. Vui lòng thử lại.",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                this.padding = padding;

                if (controller != null) {
                    controller.loadKey(in);
                } else
                    loadKey(in);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(getRootPane(), "Không thể đọc tệp. Vui lòng thử lại.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void loadKey(DataInputStream in) {
        try {
            int keySize = in.readInt();
            String privateKey = in.readUTF();
            String publicKey = in.readUTF();

            if (!algorithm.validateKeySize(keySize)) {
                JOptionPane.showMessageDialog(getRootPane(), "Tệp không hợp lệ. Vui lòng thử lại.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            keySizeCbb.setSelectedItem(keySize);
            privateKeyEdt.setText(privateKey);
            publicKeyEdt.setText(publicKey);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(getRootPane(), "Không thể lưu tệp. Vui lòng thử lại.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void generateKey() {
        try {
            privateKeyEdt.hideError();
            publicKeyEdt.hideError();

            int keySize = resolveValue(sf -> sf.getKeySize());
            algorithm.setKeySize(keySize);
            algorithm.generateKey();
            PrivateKey privateKey = algorithm.getPrivateKey();
            PublicKey publicKey = algorithm.getPublicKey();

            if (enPublic.isSelected()) {
                privateKeyEdt.setText(Base64.getEncoder().encodeToString(privateKey.getEncoded()));
                publicKeyEdt.setText(Base64.getEncoder().encodeToString(publicKey.getEncoded()));
            } else {
                privateKeyEdt.setText(Base64.getEncoder().encodeToString(publicKey.getEncoded()));
                publicKeyEdt.setText(Base64.getEncoder().encodeToString(privateKey.getEncoded()));
            }

            if (controller != null) {
                controller.generateKey();
            }
        } catch (NoSuchAlgorithmException ex) {

        }
    }

    public void setKeySize(int[] options) {
        keySizeCbb.removeAllItems();
        Arrays.stream(options).forEach(keySizeCbb::addItem);
    }

    public void setAlgorithm(RSA algorithm) {
        this.algorithm = algorithm;
    }

    @Override
    public String[] getMode() {
        Set<String> set = new TreeSet<>();

        algorithm.getAlgorithmsSupported().forEach(a -> {
            set.add(a.split("/")[1]);
        });

        return set.toArray(new String[0]);
    }

    @Override
    public String[] getPadding(String mode) {
        Set<String> set = new TreeSet<>();
        algorithm.getAlgorithmsSupported().forEach(a -> {
            String[] attrs = a.split("/");
            if (attrs[1].equals(mode))
                set.add(attrs[2]);
        });

        return set.toArray(new String[0]);
    }

    @Override
    public String encryptBase64(String plainText) {
        try {
            if (!resolveValue(sf -> sf.validateInputEncrypt()))
                return null;

            configureEncrypt();
            return algorithm.encryptBase64(plainText);
        } catch (IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException |
                 InvalidKeyException | IllegalArgumentException e) {
            JOptionPane.showMessageDialog(getRootPane(), "Mã hóa thất bại.\nError:\n" + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    @Override
    public String decryptBase64(String cipherText) {
        try {
            if (!resolveValue(sf -> sf.validateInputDecrypt()))
                return null;

            configureDecrypt();
            return algorithm.decryptBase64(cipherText);
        } catch (IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException | NoSuchAlgorithmException |
                 InvalidKeyException | IllegalArgumentException e) {
            JOptionPane.showMessageDialog(getRootPane(), "Giải mã thất bại.\nError:\n" + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    @Override
    public boolean encryptFile(String src, String des, Symmetric symmetric) {
        try {
            if (!resolveValue(sf -> sf.validateInputEncrypt()))
                return false;

            configureEncrypt();
            boolean res = algorithm.encryptFile(src, des, symmetric);

            if (!res) {
                JOptionPane.showMessageDialog(getRootPane(), "Mã hóa thất bại.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

            return res;
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | FileNotFoundException |
                 NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            JOptionPane.showMessageDialog(getRootPane(), "Mã hóa thất bại.\nError:\n" + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    public Symmetric decryptFile(String src, String des) {
        try {
            if (!resolveValue(sf -> sf.validateInputDecrypt()))
                return null;

            configureDecrypt();
            Symmetric res = algorithm.decryptFile(src, des);

            if (res == null) {
                JOptionPane.showMessageDialog(getRootPane(), "Giải mã thất bại.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

            return res;
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | FileNotFoundException |
                 NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            JOptionPane.showMessageDialog(getRootPane(), "Giải mã thất bại.\nError:\n" + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    @Override
    public void display() {
        modeCbb.removeAllItems();
        Arrays.stream(getMode()).forEach(modeCbb::addItem);
    }

    @Override
    public void close() {

    }

    @Override
    public void configureEncrypt() {
        try {
            if (enPrivate.isSelected()) {
                algorithm.setPublicKey(privateKeyEdt.getText());
            } else
                algorithm.setPublicKey(publicKeyEdt.getText());

            algorithm.setMode(modeCbb.getSelectedItem().toString());
            algorithm.setPadding(paddingCbb.getSelectedItem().toString());
        } catch (Exception e) {

        }
    }

    @Override
    public void configureDecrypt() {
        try {
            if (enPrivate.isSelected()) {
                algorithm.setPrivateKey(publicKeyEdt.getText());
            } else
                algorithm.setPrivateKey(privateKeyEdt.getText());

            algorithm.setMode(modeCbb.getSelectedItem().toString());
            algorithm.setPadding(paddingCbb.getSelectedItem().toString());
        } catch (Exception e) {
        }
    }

    @Override
    public int getKeySize() {
        return Integer.parseInt(keySizeCbb.getSelectedItem().toString());
    }

    @Override
    public boolean validateInputEncrypt() {
        if (enPrivate.isSelected()) {
            publicKeyEdt.hideError();
            publicKeyLabel.deleteNotify();

            String privateKey = privateKeyEdt.getText().trim();

            if (!privateKey.isEmpty()) {
                privateKeyEdt.hideError();
                privateKeyLabel.deleteNotify();
                return true;
            }

            privateKeyEdt.error("Vui lòng nhập khóa");
            privateKeyLabel.setNotify("");
            return false;
        }

        privateKeyEdt.hideError();
        privateKeyLabel.deleteNotify();

        String publicKey = publicKeyEdt.getText().trim();

        if (!publicKey.isEmpty()) {
            publicKeyEdt.hideError();
            publicKeyLabel.deleteNotify();
            return true;
        }

        publicKeyEdt.error("Vui lòng nhập khóa");
        publicKeyLabel.setNotify("");
        return false;
    }

    @Override
    public boolean validateInputDecrypt() {
        if (enPrivate.isSelected()) {
            privateKeyEdt.hideError();
            privateKeyLabel.deleteNotify();

            String publicKey = publicKeyEdt.getText().trim();

            if (!publicKey.isEmpty()) {
                publicKeyEdt.hideError();
                publicKeyLabel.deleteNotify();
                return true;
            }

            publicKeyEdt.error("Vui lòng nhập khóa");
            publicKeyLabel.setNotify("");
            return false;
        }

        publicKeyEdt.hideError();
        publicKeyLabel.deleteNotify();

        String privateKey = privateKeyEdt.getText().trim();

        if (!privateKey.isEmpty()) {
            privateKeyEdt.hideError();
            privateKeyLabel.deleteNotify();
            return true;
        }

        privateKeyEdt.error("Vui lòng nhập khóa");
        privateKeyLabel.setNotify("");
        return false;
    }

    public <T> boolean exist(T[] values, T item) {
        return Arrays.stream(values).anyMatch(v -> v.equals(item));
    }

    public void setAlgorithmCbb(MaterialCombobox<String> algorithmCbb) {
        this.algorithmCbb = algorithmCbb;
    }

    public void refreshUI() {
        revalidate();
        repaint();
    }

    public <T> T resolveValue(Function<ASymmetricFragment, T> function) {
        return function.apply(controller != null ? controller : this);
    }

    public void setController(ASymmetricDecorator controller) {
        this.controller = controller;
    }
}