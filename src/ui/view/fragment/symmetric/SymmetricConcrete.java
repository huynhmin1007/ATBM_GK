package ui.view.fragment.symmetric;

import encryption.common.Algorithm;
import encryption.common.Mode;
import encryption.common.Padding;
import encryption.symmetric.Symmetric;
import encryption.symmetric.SymmetricFactory;
import ui.common.Dimensions;
import ui.view.component.ComboboxInputField;
import ui.view.component.InputField;
import ui.view.component.MaterialCombobox;
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
    public JButton loadKey, genKey, saveKey;
    public ComboboxInputField keySizeInput;
    public InputField keyInput, ivSizeInput, ivInput;

    public String oldPath;

    public MaterialCombobox<String> algorithmCbb;

    private SymmetricDecorator controller;

    public SymmetricConcrete() {
        setLayout(new GridBagLayout());

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
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
        new DropTarget(keyInput.input, DnDConstants.ACTION_COPY, new DropTargetAdapter() {
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
                                JOptionPane.showMessageDialog(getRootPane(), "Please drop a valid file.", "Invalid Drop", JOptionPane.ERROR_MESSAGE);
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
        keySizeInput = new ComboboxInputField("Key Size:");
        gbc.gridy = 0;
        add(keySizeInput, gbc);

        keyInput = new InputField("Key:");
        keyInput.info("Drag and drop a file here to load the key.");
        gbc.gridy = 1;
        add(keyInput, gbc);
    }

    private void createIVGUI() {
        ivSizeInput = new InputField("IV Size:");
        ivSizeInput.setInput(ivSizeInput.input, 0);
        gbc.gridy = 2;
        add(ivSizeInput, gbc);

        ivInput = new InputField("IV:");
        gbc.gridy = 3;
        add(ivInput, gbc);
    }

    private void createButtonGroup() {
        genKey = new JButton("Generate");
        loadKey = new JButton("Load");
        saveKey = new JButton("Save");

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, Dimensions.MARGIN_HORIZONTAL, 0));
        btnPanel.add(genKey);
        btnPanel.add(loadKey);
        btnPanel.add(saveKey);

        gbc.gridy = 4;
        add(btnPanel, gbc);
    }

    public GridBagConstraints getGbc() {
        return gbc;
    }

    public boolean validateKeyAndIV() {
        int count = 0;

        if (keyInput.getValue().isEmpty()) {
            keyInput.error();
        } else {
            keyInput.hideError();
            count++;
        }

        if (algorithm.getIVSize(mode) != -1) {
            if (ivInput.getValue().isEmpty())
                ivInput.error();
            else count++;
        } else {
            ivInput.hideError();
            count++;
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
        int ivSize = algorithm.getIVSize(mode);
        keySizeInput.setItems(Arrays.stream(keySize).mapToObj(String::valueOf).toArray(String[]::new));

        if (ivSize != -1) {
            ivSizeInput.setValue(ivSize + "");
            ivInput.setEnabled(true);
        } else {
            if (ivInput.getValue().trim().isEmpty()) {
                ivSizeInput.setValue("");
            }
            ivInput.setEnabled(false);
        }
        ivSizeInput.setEnabled(false);
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
                out.writeInt(resolveValue(sf -> sf.getKeySizeInput()));
                out.writeUTF(keyInput.getValue());

                int ivSize = algorithm.getIVSize(mode);
                if (ivSize != -1) {
                    out.writeInt(ivSize);
                    out.writeUTF(ivInput.getValue());
                }

                if (controller != null) {
                    controller.saveKey(out);
                }

                String message = "<html>Saved key successfully!<br>Key saved in: <b>" + path + "</b></html>";
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
                    Desktop.getDesktop().open(new File(path).getParentFile());
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(getRootPane(), "Failed to save the key.",
                        "Error", JOptionPane.ERROR_MESSAGE);
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
                throw new IOException();
            }

            setAlgorithm(SymmetricFactory.getSymmetric(Algorithm.valueOf(algorithmName)));
            if (algorithmCbb != null) {
                algorithmCbb.setSelectedItem(algorithmName);
            }

            String mode = in.readUTF();
            String padding = in.readUTF();

            if (!exist(getMode(), mode)) {
                throw new IOException();
            }

            this.mode = mode;
            modeCbb.setSelectedItem(mode);

            if (!exist(getPadding(mode), padding)) {
                throw new IOException();
            }

            this.padding = padding;
            paddingCbb.setSelectedItem(padding);

            if (controller != null) {
                controller.loadKey(in);
            } else
                loadKey(in);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(getRootPane(), "Failed to load the key.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void loadKey(DataInputStream in) {
        try {
            int keySize = in.readInt();
            String key = in.readUTF();

            if (!algorithm.validateKeySize(keySize)) {
                throw new IOException();
            }

            keySizeInput.setValue(keySize + "");
            keyInput.setValue(key);

            if (in.available() != 0) {
                int ivSize = in.readInt();
                String iv = in.readUTF();

                if (algorithm.getIVSize(mode) != ivSize) {
                    throw new IOException();
                }
                ivSizeInput.setValue(ivSize + "");
                ivInput.setValue(iv);
            } else {
                ivSizeInput.setValue("");
                ivInput.setValue("");
                ivSizeInput.setEnabled(false);
                ivInput.setEnabled(false);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(getRootPane(), "Failed to load the key.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void generateKey() {
        try {
            keyInput.hideError();
            ivInput.hideError();

            int keySize = resolveValue(sf -> sf.getKeySizeInput());
            algorithm.setKeySize(keySize);
            SecretKey key = algorithm.generateKey();
            keyInput.setValue(Base64.getEncoder().encodeToString(key.getEncoded()));

            int ivSize = algorithm.getIVSize(mode);
            if (ivSize != -1) {
                ivInput.setValue(Base64.getEncoder().encodeToString(algorithm.generateIV().getIV()));
            } else {
                ivSizeInput.setValue("");
                ivInput.setValue("");
            }

            if (controller != null) {
                controller.generateKey();
            }
        } catch (NoSuchAlgorithmException ex) {
            keyInput.setValue("");
            int ivSize = algorithm.getIVSize(mode);
            if (ivSize != -1) {
                ivInput.setValue("");
            }
            JOptionPane.showMessageDialog(getRootPane(), "Failed to generate the key.\nError: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
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
            JOptionPane.showMessageDialog(getRootPane(), "Encryption failed.\nError: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(getRootPane(), "Decryption failed.\nError: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(getRootPane(), "Encryption failed.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

            return res;
        } catch (FileNotFoundException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                 InvalidAlgorithmParameterException e) {
            JOptionPane.showMessageDialog(getRootPane(), "Encryption failed.\nError: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(getRootPane(), "Decryption failed.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

            return res;
        } catch (FileNotFoundException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException |
                 InvalidAlgorithmParameterException e) {
            JOptionPane.showMessageDialog(getRootPane(), "Decryption failed.\nError: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
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
        keySizeInput.setValue(algorithm.getKeySize() + "");
        keyInput.setValue(Base64.getEncoder().encodeToString(algorithm.getKey().getEncoded()));

        if (algorithm.getIVSize(algorithm.getMode()) != -1) {
            ivSizeInput.setValue(algorithm.getIVSize(mode) + "");
            ivInput.setValue(Base64.getEncoder().encodeToString(algorithm.getIv().getIV()));
        } else {
            ivSizeInput.setValue("");
            ivInput.setValue("");
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void configure() {
        algorithm.setKeySize(resolveValue(sf -> sf.getKeySizeInput()));
        algorithm.setKey(new SecretKeySpec(Base64.getDecoder().decode(keyInput.getValue()), algorithm.getAlgorithm()));
        algorithm.setMode(Mode.valueOf(modeCbb.getSelectedItem().toString()));
        algorithm.setPadding(Padding.valueOf(paddingCbb.getSelectedItem().toString()));

        if (algorithm.getIVSize(mode) != -1) {
            algorithm.setIv(new IvParameterSpec(Base64.getDecoder().decode(ivInput.getValue())));
        } else {
            ivInput.setValue("");
            algorithm.setIv(null);
        }
    }

    @Override
    public int getKeySizeInput() {
        return Integer.parseInt(keySizeInput.getValue());
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