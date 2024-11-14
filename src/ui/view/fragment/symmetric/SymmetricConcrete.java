package ui.view.fragment.symmetric;

import encryption.common.Algorithm;
import encryption.symmetric.Symmetric;
import encryption.symmetric.SymmetricFactory;
import ui.common.Dimensions;
import ui.view.component.EditText;
import ui.view.component.MaterialCombobox;
import ui.view.component.MaterialLabel;
import utils.FileHelper;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.*;

public class SymmetricConcrete extends JPanel implements SymmetricFragment {

    public static final List<String> algorithmSupported = Arrays.asList("AES", "ARCFOUR", "Blowfish", "DES", "ChaCha20");
    public Symmetric algorithm;
    public String mode, padding;

    public GridBagConstraints gbc;
    public MaterialCombobox<String> modeCbb, paddingCbb;
    public MaterialCombobox<Integer> keySizeCbb;
    public EditText keyEdt;
    public EditText ivSizeEdt;
    public EditText ivEdt;
    private JButton loadKey, genKey, saveKey;

    private String oldPath;

    private MaterialCombobox<String> algorithmCbb;

    public SymmetricConcrete() {
        setLayout(new GridBagLayout());

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(Dimensions.MARGIN_VERTICAL, 0, Dimensions.MARGIN_VERTICAL, Dimensions.MARGIN_HORIZONTAL);

        createModeAndPaddingGUI();
        createKeyGUI();
        createIVGUI();
        createButtonGroup();
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
        JLabel keySizeLabel = new MaterialLabel("Key Size:");
        JLabel keyLabel = new MaterialLabel("Key:");
        keySizeCbb = new MaterialCombobox<>();
        keyEdt = new EditText();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        add(keySizeLabel, gbc);

        gbc.gridx = 1;
        add(keySizeCbb, gbc);

        gbc.gridx = 2;
        gbc.weightx = 1.0;
        add(new JPanel(), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        add(keyLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;
        add(keyEdt, gbc);
    }

    private void createIVGUI() {
        JLabel ivSizeLabel = new MaterialLabel("IV Size:");
        JLabel ivLabel = new MaterialLabel("IV:");
        ivSizeEdt = new EditText();
        ivEdt = new EditText();

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        gbc.gridwidth = 1;
        add(ivSizeLabel, gbc);

        gbc.gridx = 1;
        add(ivSizeEdt, gbc);

        gbc.gridx = 2;
        gbc.weightx = 1.0;
        add(new JPanel(), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        add(ivLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;
        add(ivEdt, gbc);
    }

    private void createButtonGroup() {
        genKey = new JButton("Generate");
        loadKey = new JButton("Load");
        saveKey = new JButton("Save");

        gbc.insets = new Insets(Dimensions.MARGIN_VERTICAL, 0, 0, 0);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, Dimensions.MARGIN_HORIZONTAL, 0));
        btnPanel.add(genKey);
        btnPanel.add(loadKey);
        btnPanel.add(saveKey);

        gbc.gridy = 4;
        add(btnPanel, gbc);

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

    public GridBagConstraints getGbc() {
        return gbc;
    }

    private boolean validateKeyAndIV() {
        String key = keyEdt.getText().trim();
        int ivSize = algorithm.getIVSize(mode);
        String iv = ivEdt.getText().trim();

        if (!key.isEmpty()) {
            keyEdt.hideError();

            if (ivSize != -1 && iv.isEmpty()) {
                ivEdt.error("Vui lòng nhập IV");
                return false;
            }

            ivEdt.hideError();
            return true;
        }

        if (key.isEmpty()) {
            keyEdt.error("Vui lòng nhập khóa");
        }

        if (ivSize != -1 && iv.isEmpty()) {
            ivEdt.error("Vui lòng nhập IV");
        }

        return false;
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
        int ivSize = algorithm.getIVSize(mode);
        setKeySize(keySize);

        if (ivSize != -1) {
            ivSizeEdt.setText(ivSize + "");
            ivSizeEdt.setEnabled(true);
            ivEdt.setEnabled(true);
        } else {
            ivSizeEdt.setEnabled(false);
            ivEdt.setEnabled(false);
        }
    }

    @Override
    public void handlePaddingChangeListener() {
        if (paddingCbb.getSelectedItem() == null)
            return;

        this.padding = paddingCbb.getSelectedItem().toString();
    }

    @Override
    public void saveKey() {
        if (!validateKeyAndIV())
            return;

        FileHelper fileHelper = new FileHelper();
        String key = keyEdt.getText();
        String path = fileHelper.showSaveFile(getRootPane(), oldPath, new String[]{"dat", "txt"});

        if (path != null && !path.isEmpty()) {
            oldPath = path;

            try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(path)))) {
                out.writeUTF(algorithm.getAlgorithm());
                out.writeUTF(mode);
                out.writeUTF(padding);
                out.writeInt(Integer.parseInt(keySizeCbb.getSelectedItem().toString()));
                out.writeUTF(key);

                int ivSize = algorithm.getIVSize(mode);
                if (ivSize != -1) {
                    out.writeInt(ivSize);
                    out.writeUTF(ivEdt.getText());
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
    public void loadKey() {
        FileHelper fileHelper = new FileHelper();
        File file = fileHelper.showOpenFile(getRootPane(), oldPath, new String[]{"dat", "txt"});

        if (file != null && file.exists()) {
            oldPath = file.getAbsolutePath();

            try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
                String algorithmName = in.readUTF();
                String mode = in.readUTF();
                String padding = in.readUTF();
                int keySize = in.readInt();
                String key = in.readUTF();

                if (!algorithmSupported.contains(algorithmName)) {
                    JOptionPane.showMessageDialog(getRootPane(), "Tệp không hợp lệ. Vui lòng thử lại.",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                setAlgorithm(SymmetricFactory.getSymmetric(Algorithm.valueOf(algorithmName)));
                if (algorithmCbb != null) {
                    algorithmCbb.setSelectedItem(algorithmName);
                }

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

                if (Arrays.stream(algorithm.getKeySizeSupported()).noneMatch(v -> v == keySize)) {
                    JOptionPane.showMessageDialog(getRootPane(), "Tệp không hợp lệ. Vui lòng thử lại.",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                keySizeCbb.setSelectedItem(keySize);
                keyEdt.setText(key);

                if (in.available() != 0) {
                    int ivSize = in.readInt();
                    String iv = in.readUTF();

                    if (algorithm.getIVSize(mode) != ivSize) {
                        JOptionPane.showMessageDialog(getRootPane(), "Tệp không hợp lệ. Vui lòng thử lại.",
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    ivSizeEdt.setText(ivSize + "");
                    ivEdt.setText(iv);
                }

                display();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(getRootPane(), "Không thể đọc tệp. Vui lòng thử lại.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void generateKey() {
        try {
            keyEdt.hideError();
            ivEdt.hideError();

            int keySize = Integer.parseInt(keySizeCbb.getSelectedItem().toString());
            algorithm.setKeySize(keySize);
            SecretKey key = algorithm.generateKey();
            keyEdt.setText(Base64.getEncoder().encodeToString(key.getEncoded()));

            int ivSize = algorithm.getIVSize(mode);
            if (ivSize != -1) {
                ivEdt.setText(Base64.getEncoder().encodeToString(algorithm.generateIV().getIV()));
            } else {
                ivEdt.setText("");
            }
        } catch (NoSuchAlgorithmException ex) {

        }
    }

    public void setKeySize(int[] options) {
        keySizeCbb.removeAllItems();
        Arrays.stream(options).forEach(keySizeCbb::addItem);
    }

    public void setAlgorithm(Symmetric algorithm) {
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
            if (!validateKeyAndIV())
                return null;

            configure();
            return algorithm.encryptBase64(plainText);
        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException |
                 InvalidKeyException | InvalidAlgorithmParameterException e) {
            JOptionPane.showMessageDialog(getRootPane(), "Mã hóa thất bại.\nError:\n" + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    @Override
    public String decryptBase64(String cipherText) {
        try {
            if (!validateKeyAndIV())
                return null;

            configure();
            return algorithm.decryptBase64(cipherText);
        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException |
                 InvalidKeyException | InvalidAlgorithmParameterException e) {
            JOptionPane.showMessageDialog(getRootPane(), "Giải mã thất bại.\nError:\n" + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    @Override
    public boolean encryptFile(String src, String des) {
        try {
            if (!validateKeyAndIV())
                return false;

            configure();

            return algorithm.encryptFile(src, des, false);
        } catch (FileNotFoundException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                 InvalidAlgorithmParameterException e) {
            JOptionPane.showMessageDialog(getRootPane(), "Mã hóa thất bại.\nError:\n" + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    public boolean decryptFile(String src, String des) {
        try {
            if (!validateKeyAndIV())
                return false;

            configure();

            return algorithm.decryptFile(src, des);
        } catch (FileNotFoundException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                 InvalidAlgorithmParameterException e) {
            JOptionPane.showMessageDialog(getRootPane(), "Giải mã thất bại.\nError:\n" + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    @Override
    public void display() {
        modeCbb.removeAllItems();
        Arrays.stream(getMode()).forEach(modeCbb::addItem);
    }

    @Override
    public void configure() {
        algorithm.setKeySize(Integer.parseInt(keySizeCbb.getSelectedItem().toString()));
        algorithm.setKey(new SecretKeySpec(Base64.getDecoder().decode(keyEdt.getText()), algorithm.getAlgorithm()));
        algorithm.setMode(modeCbb.getSelectedItem().toString());
        algorithm.setPadding(paddingCbb.getSelectedItem().toString());

        if (algorithm.getIVSize(mode) != -1) {
            algorithm.setIv(new IvParameterSpec(Base64.getDecoder().decode(ivEdt.getText())));
        } else {
            ivEdt.setText("");
            algorithm.setIv(null);
        }
    }

    private <T> boolean exist(T[] values, T item) {
        return Arrays.stream(values).anyMatch(v -> v.equals(item));
    }

    public void setAlgorithmCbb(MaterialCombobox<String> algorithmCbb) {
        this.algorithmCbb = algorithmCbb;
    }
}