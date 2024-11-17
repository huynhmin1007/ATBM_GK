package ui.view.fragment.symmetric;

import encryption.common.Algorithm;
import encryption.common.Mode;
import encryption.common.Padding;
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
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.*;
import java.util.function.Function;

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
    public JButton loadKey, genKey, saveKey;
    public JPanel keySizePanel, keyPanel, ivPanel;
    public MaterialLabel ivLabel, keyLabel;

    public String oldPath;

    public MaterialCombobox<String> algorithmCbb;

    private SymmetricDecorator controller;

    public SymmetricConcrete() {
        setLayout(new GridBagLayout());

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = Dimensions.ZERO_INSETS;
        gbc.weightx = 1.0;

        createModeAndPaddingGUI();
        createKeyGUI();
        createIVGUI();
        createButtonGroup();
        addDragAndDrop();

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

    private void addDragAndDrop() {
        new DropTarget(keyEdt.textField, DnDConstants.ACTION_COPY, new DropTargetAdapter() {
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
                                loadKey(droppedFile);
                            } else {
                                JOptionPane.showMessageDialog(getRootPane(), "Vui lòng nhập vào File, không phải Folder.", "Invalid Drop", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void createKeyGUI() {
        MaterialLabel keySizeLabel = new MaterialLabel("Key Size:");
        keyLabel = new MaterialLabel("Key:");
        keySizeCbb = new MaterialCombobox<>();
        keyEdt = new EditText();
        keyLabel.setNotify("");
        keyEdt.setInfo("Kéo và thả File vào đây để load Key");
        keyEdt.setPreferredSize(new Dimension(140, keyEdt.getPreferredSize().height));

        keyPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = Dimensions.DEFAULT_INSETS;

        keySizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, Dimensions.MARGIN_HORIZONTAL, Dimensions.MARGIN_VERTICAL));
        keySizeLabel.setPreferredSize(new Dimension(keySizeLabel.getPreferredSize().width + Dimensions.MARGIN_HORIZONTAL, keySizeLabel.getPreferredSize().height));

        keySizePanel.add(keySizeLabel);
        keySizePanel.add(keySizeCbb);

        constraints.gridx = 0;
        constraints.weightx = 1.0;
        constraints.gridwidth = 2;
        constraints.insets = Dimensions.ZERO_INSETS;
        keyPanel.add(keySizePanel, constraints);

        constraints.gridwidth = 1;
        constraints.insets = Dimensions.DEFAULT_INSETS;
        constraints.gridy = 1;
        constraints.weightx = 0.0;
        keyPanel.add(keyLabel, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1.0;
        keyPanel.add(keyEdt, constraints);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(keyPanel, gbc);
    }

    private void createIVGUI() {
        MaterialLabel ivSizeLabel = new MaterialLabel("IV Size:");
        ivLabel = new MaterialLabel("IV:");
        ivSizeEdt = new EditText();
        ivSizeEdt.setPreferredSize(new Dimension(140, ivSizeEdt.getPreferredSize().height));
        ivEdt = new EditText();
        ivEdt.setPreferredSize(new Dimension(140, keyEdt.getPreferredSize().height));

        ivPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = Dimensions.DEFAULT_INSETS;

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 0.0;
        ivPanel.add(ivSizeLabel, constraints);

        constraints.gridx = 1;
        ivPanel.add(ivSizeEdt, constraints);

        constraints.gridx = 2;
        constraints.weightx = 1.0;
        ivPanel.add(new JPanel(), constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 0.0;
        ivPanel.add(ivLabel, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1.0;
        constraints.gridwidth = 2;
        ivPanel.add(ivEdt, constraints);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(ivPanel, gbc);
    }

    private void createButtonGroup() {
        genKey = new JButton("Generate");
        loadKey = new JButton("Load");
        saveKey = new JButton("Save");

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, Dimensions.MARGIN_HORIZONTAL, 0));
        btnPanel.add(genKey);
        btnPanel.add(loadKey);
        btnPanel.add(saveKey);

        gbc.gridy = 2;
        add(btnPanel, gbc);
    }

    public GridBagConstraints getGbc() {
        return gbc;
    }

    public boolean validateKeyAndIV() {
        String key = keyEdt.getText().trim();
        int ivSize = algorithm.getIVSize(mode);
        String iv = ivEdt.getText().trim();

        if (!key.isEmpty()) {
            keyEdt.hideError();
            keyLabel.deleteNotify();

            if (ivSize != -1 && iv.isEmpty()) {
                ivEdt.error("Vui lòng nhập IV");
                ivLabel.setNotify("");
                return false;
            }

            ivEdt.hideError();
            ivLabel.deleteNotify();
            return true;
        }

        if (key.isEmpty()) {
            keyEdt.error("Vui lòng nhập khóa");
            keyLabel.setNotify("");
        }

        if (ivSize != -1 && iv.isEmpty()) {
            ivEdt.error("Vui lòng nhập IV");
            ivLabel.setNotify("");
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
            ivSizeEdt.setEnabled(false);
            ivEdt.setEnabled(true);
        } else {
            if (ivEdt.getText().trim().isEmpty()) {
                ivSizeEdt.setText("");
            }
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
        String path = fileHelper.showSaveFile(getRootPane(), oldPath, new String[]{"dat", "key"});

        if (path != null && !path.isEmpty()) {
            oldPath = path;

            try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(path)))) {
                out.writeUTF(algorithm.getAlgorithm());
                out.writeUTF(mode);
                out.writeUTF(padding);
                out.writeInt(resolveValue(sf -> sf.getKeySize()));
                out.writeUTF(keyEdt.getText());

                int ivSize = algorithm.getIVSize(mode);
                if (ivSize != -1) {
                    out.writeInt(ivSize);
                    out.writeUTF(ivEdt.getText());
                }

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
            loadKey(file);
        }
    }

    public void loadKey(File file) {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            String algorithmName = in.readUTF();

            if (!algorithmSupported.contains(algorithmName)) {
                JOptionPane.showMessageDialog(getRootPane(), "Tệp không hợp lệ. Vui lòng thử lại.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            setAlgorithm(SymmetricFactory.getSymmetric(Algorithm.valueOf(algorithmName)));
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
            modeCbb.setSelectedItem(mode);

            if (!exist(getPadding(mode), padding)) {
                JOptionPane.showMessageDialog(getRootPane(), "Tệp không hợp lệ. Vui lòng thử lại.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            this.padding = padding;
            paddingCbb.setSelectedItem(padding);

            if (controller != null) {
                controller.loadKey(in);
            } else
                loadKey(in);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(getRootPane(), "Không thể đọc tệp. Vui lòng thử lại.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void loadKey(DataInputStream in) {
        try {
            int keySize = in.readInt();
            String key = in.readUTF();

            if (!algorithm.validateKeySize(keySize)) {
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
            } else {
                ivSizeEdt.setText("");
                ivEdt.setText("");
                ivSizeEdt.setEnabled(false);
                ivEdt.setEnabled(false);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(getRootPane(), "Không thể lưu tệp. Vui lòng thử lại.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void generateKey() {
        try {
            keyEdt.hideError();
            ivEdt.hideError();

            int keySize = resolveValue(sf -> sf.getKeySize());
            algorithm.setKeySize(keySize);
            SecretKey key = algorithm.generateKey();
            keyEdt.setText(Base64.getEncoder().encodeToString(key.getEncoded()));

            int ivSize = algorithm.getIVSize(mode);
            if (ivSize != -1) {
                ivEdt.setText(Base64.getEncoder().encodeToString(algorithm.generateIV().getIV()));
            } else {
                ivSizeEdt.setText("");
                ivEdt.setText("");
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
            if (!resolveValue(sf -> sf.validateInput()))
                return null;

            configure();
            return algorithm.encryptBase64(plainText);
        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException |
                 InvalidKeyException | InvalidAlgorithmParameterException | IllegalArgumentException e) {
            JOptionPane.showMessageDialog(getRootPane(), "Mã hóa thất bại.\nError:\n" + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    @Override
    public String decryptBase64(String cipherText) {
        try {
            if (!resolveValue(sf -> sf.validateInput()))
                return null;

            configure();
            return algorithm.decryptBase64(cipherText);
        } catch (NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | BadPaddingException |
                 InvalidKeyException | InvalidAlgorithmParameterException | IllegalArgumentException e) {
            JOptionPane.showMessageDialog(getRootPane(), "Giải mã thất bại.\nError:\n" + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    @Override
    public boolean encryptFile(String src, String des) {
        try {
            if (!resolveValue(sf -> sf.validateInput()))
                return false;

            configure();
            boolean res = algorithm.encryptFile(src, des, false);

            if (!res) {
                JOptionPane.showMessageDialog(getRootPane(), "Mã hóa thất bại.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

            return res;
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
            if (!resolveValue(sf -> sf.validateInput()))
                return false;

            configure();

            boolean res = algorithm.decryptFile(src, des);

            if (!res) {
                JOptionPane.showMessageDialog(getRootPane(), "Giải mã thất bại.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

            return res;
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
    public void displayWithAttributes() {
        if (algorithm == null)
            return;

        modeCbb.setSelectedItem(algorithm.getMode());
        paddingCbb.setSelectedItem(algorithm.getPadding());
        keySizeCbb.setSelectedItem(algorithm.getKeySize());
        keyEdt.setText(Base64.getEncoder().encodeToString(algorithm.getKey().getEncoded()));

        if (algorithm.getIVSize(algorithm.getMode()) != -1) {
            ivSizeEdt.setText(algorithm.getIVSize(mode) + "");
            ivEdt.setText(Base64.getEncoder().encodeToString(algorithm.getIv().getIV()));
        } else {
            ivSizeEdt.setText("");
            ivEdt.setText("");
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void configure() {
        algorithm.setKeySize(resolveValue(sf -> sf.getKeySize()));
        algorithm.setKey(new SecretKeySpec(Base64.getDecoder().decode(keyEdt.getText()), algorithm.getAlgorithm()));
        algorithm.setMode(Mode.valueOf(modeCbb.getSelectedItem().toString()));
        algorithm.setPadding(Padding.valueOf(paddingCbb.getSelectedItem().toString()));

        if (algorithm.getIVSize(mode) != -1) {
            algorithm.setIv(new IvParameterSpec(Base64.getDecoder().decode(ivEdt.getText())));
        } else {
            ivEdt.setText("");
            algorithm.setIv(null);
        }
    }

    @Override
    public int getKeySize() {
        return Integer.parseInt(keySizeCbb.getSelectedItem().toString());
    }

    @Override
    public boolean validateInput() {
        return validateKeyAndIV();
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

    public <T> T resolveValue(Function<SymmetricFragment, T> function) {
        return function.apply(controller != null ? controller : this);
    }

    public void setController(SymmetricDecorator controller) {
        this.controller = controller;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setPadding(String padding) {
        this.padding = padding;
    }
}