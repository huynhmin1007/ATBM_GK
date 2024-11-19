package ui.view.fragment.asymmetric;

import encryption.asymmetric.ASymmetricFactory;
import encryption.asymmetric.RSA;
import encryption.common.Algorithm;
import encryption.symmetric.Symmetric;
import ui.common.Dimensions;
import ui.view.component.ComboboxInputField;
import ui.view.component.InputField;
import ui.view.component.MaterialCombobox;
import utils.FileHelper;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
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
    public JButton loadKey, genKey, saveKey;
    public JRadioButton enPrivate, enPublic;
    public InputField privateKeyEdt, publicKeyEdt;
    public ComboboxInputField keySizeCbb;

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

        gbc.weighty = 1;
        gbc.gridy = 100;
        add(new JPanel(), gbc);
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
        keySizeCbb = new ComboboxInputField("Key Size:");
        gbc.gridy = 0;
        add(keySizeCbb, gbc);

        JLabel enTypeLabel = new JLabel("Encryption Key:");
        JPanel enTypePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, Dimensions.MARGIN_HORIZONTAL, Dimensions.MARGIN_VERTICAL + 5));
        enTypeLabel.setPreferredSize(new Dimension(enTypeLabel.getPreferredSize().width + Dimensions.MARGIN_HORIZONTAL, enTypeLabel.getPreferredSize().height));
        enTypePanel.add(enTypeLabel);

        enPublic = new JRadioButton("Public Key");
        enTypePanel.add(enPublic);
        enPublic.setMargin(new Insets(0, 0, 0, 3 * Dimensions.MARGIN_HORIZONTAL));
        enPublic.setSelected(true);

        enPrivate = new JRadioButton("Private Key");
        enTypePanel.add(enPrivate);

        ButtonGroup btnGroup = new ButtonGroup();
        btnGroup.add(enPrivate);
        btnGroup.add(enPublic);

        gbc.gridy = 1;
        add(enTypePanel, gbc);

        privateKeyEdt = new InputField("Private Key:");
        privateKeyEdt.info("Drag and drop a file here to load the key.");
        gbc.gridy = 2;
        add(privateKeyEdt, gbc);

        publicKeyEdt = new InputField("Public Key:");
        publicKeyEdt.info("Drag and drop a file here to load the key.");
        gbc.gridy = 3;
        add(publicKeyEdt, gbc);
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

    public boolean validateKey() {
        String privateKey = privateKeyEdt.getValue().trim();
        String publicKey = publicKeyEdt.getValue().trim();

        int count = 0;

        if (!privateKey.isEmpty()) {
            privateKeyEdt.hideError();
            count++;
        } else {
            privateKeyEdt.error();
        }

        if (!publicKey.isEmpty()) {
            publicKeyEdt.hideError();
            count++;
        } else {
            publicKeyEdt.error();
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
        String path = fileHelper.showSaveFile(getRootPane(), oldPath, new String[]{"dat", "key"});

        if (path != null && !path.isEmpty()) {
            oldPath = path;

            try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(path)))) {
                out.writeUTF(algorithm.getAlgorithm());
                out.writeUTF(mode);
                out.writeUTF(padding);
                out.writeInt(resolveValue(sf -> sf.getKeySize()));
                out.writeUTF(privateKeyEdt.getValue());
                out.writeUTF(publicKeyEdt.getValue());

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

    private void addDragAndDrop() {
        new DropTarget(privateKeyEdt.input, DnDConstants.ACTION_COPY, new DropTargetAdapter() {
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

        new DropTarget(publicKeyEdt.input, DnDConstants.ACTION_COPY, new DropTargetAdapter() {
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

    public void loadKey(File file) {
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
            JOptionPane.showMessageDialog(getRootPane(), "Failed to load the key.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void loadKey() {
        FileHelper fileHelper = new FileHelper();
        File file = fileHelper.showOpenFile(getRootPane(), oldPath, new String[]{"dat", "key"});

        if (file != null && file.exists()) {
            oldPath = file.getAbsolutePath();
            loadKey(file);
        }
    }

    @Override
    public void loadKey(DataInputStream in) {
        try {
            int keySize = in.readInt();
            String privateKey = in.readUTF();
            String publicKey = in.readUTF();

            if (!algorithm.validateKeySize(keySize)) {
                throw new IOException();
            }

            keySizeCbb.setValue(keySize + "");
            privateKeyEdt.setValue(privateKey);
            publicKeyEdt.setValue(publicKey);
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
                privateKeyEdt.setValue(Base64.getEncoder().encodeToString(privateKey.getEncoded()));
                publicKeyEdt.setValue(Base64.getEncoder().encodeToString(publicKey.getEncoded()));
            } else {
                privateKeyEdt.setValue(Base64.getEncoder().encodeToString(publicKey.getEncoded()));
                publicKeyEdt.setValue(Base64.getEncoder().encodeToString(privateKey.getEncoded()));
            }

            if (controller != null) {
                controller.generateKey();
            }
        } catch (NoSuchAlgorithmException ex) {
            privateKeyEdt.setValue("");
            publicKeyEdt.setValue("");
            JOptionPane.showMessageDialog(getRootPane(), "Failed to generate the key.\nError: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setKeySize(int[] options) {
        keySizeCbb.setItems(Arrays.stream(options).mapToObj(String::valueOf).toArray(String[]::new));
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
            JOptionPane.showMessageDialog(getRootPane(), "Encryption failed.\nError: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(getRootPane(), "Decryption failed.\nError: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(getRootPane(), "Encryption failed.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

            return res;
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | FileNotFoundException |
                 NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            JOptionPane.showMessageDialog(getRootPane(), "Encryption failed.\nError: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(getRootPane(), "Decryption failed.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }

            return res;
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | FileNotFoundException |
                 NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            JOptionPane.showMessageDialog(getRootPane(), "Decryption failed.\nError: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
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
                algorithm.setPublicKey(privateKeyEdt.getValue());
            } else
                algorithm.setPublicKey(publicKeyEdt.getValue());

            algorithm.setMode(modeCbb.getSelectedItem().toString());
            algorithm.setPadding(paddingCbb.getSelectedItem().toString());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(getRootPane(), (enPrivate.isSelected() ? "Private Key" : "Public Key") + " is not valid.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void configureDecrypt() {
        try {
            if (enPrivate.isSelected()) {
                algorithm.setPrivateKey(publicKeyEdt.getValue());
            } else
                algorithm.setPrivateKey(privateKeyEdt.getValue());

            algorithm.setMode(modeCbb.getSelectedItem().toString());
            algorithm.setPadding(paddingCbb.getSelectedItem().toString());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(getRootPane(), (enPrivate.isSelected() ? "Public Key" : "Private Key") + " is not valid.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public int getKeySize() {
        return Integer.parseInt(keySizeCbb.getValue());
    }

    @Override
    public boolean validateInputEncrypt() {
        if (enPrivate.isSelected()) {
            publicKeyEdt.hideError();

            String privateKey = privateKeyEdt.getValue().trim();

            if (!privateKey.isEmpty()) {
                privateKeyEdt.hideError();
                return true;
            }

            privateKeyEdt.error();
            return false;
        }

        privateKeyEdt.hideError();

        String publicKey = publicKeyEdt.getValue().trim();

        if (!publicKey.isEmpty()) {
            publicKeyEdt.hideError();
            return true;
        }

        publicKeyEdt.error();
        return false;
    }

    @Override
    public boolean validateInputDecrypt() {
        if (enPrivate.isSelected()) {
            privateKeyEdt.hideError();

            String publicKey = publicKeyEdt.getValue().trim();

            if (!publicKey.isEmpty()) {
                publicKeyEdt.hideError();
                return true;
            }

            publicKeyEdt.error();
            return false;
        }

        publicKeyEdt.hideError();

        String privateKey = privateKeyEdt.getValue().trim();

        if (!privateKey.isEmpty()) {
            privateKeyEdt.hideError();
            return true;
        }

        privateKeyEdt.error();
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

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setPadding(String padding) {
        this.padding = padding;
    }
}