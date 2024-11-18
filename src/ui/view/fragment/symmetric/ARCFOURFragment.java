package ui.view.fragment.symmetric;

import encryption.common.Algorithm;
import encryption.symmetric.ARCFOUR;
import encryption.symmetric.Symmetric;
import encryption.symmetric.SymmetricFactory;
import ui.view.component.InputField;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Base64;

public class ARCFOURFragment extends SymmetricDecorator {

    private ARCFOUR algorithm;
    private InputField keySizeInput;
    private JPanel emptyPanel;

    public ARCFOURFragment(SymmetricConcrete symmetricConcrete) {
        super(symmetricConcrete);
        algorithm = (ARCFOUR) SymmetricFactory.getSymmetric(Algorithm.ARCFOUR);

        emptyPanel = new JPanel();

        keySizeInput = new InputField("Key Size:");
        keySizeInput.info("Key size must range between 40 and 1024");
        keySizeInput.setInput(keySizeInput.input, 0);
        keySizeInput.setNumericOnly();
    }

    @Override
    public Symmetric getAlgorithm() {
        return algorithm;
    }

    @Override
    public void setAlgorithm(Symmetric algorithm) {
        this.algorithm = (ARCFOUR) algorithm;
    }

    @Override
    public void display() {
        concrete.keySizeInput.setVisible(false);
        concrete.ivSizeInput.setVisible(false);
        concrete.ivInput.setVisible(false);
        concrete.setController(this);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        concrete.add(keySizeInput, constraints);

        super.display();
    }

    @Override
    public void displayWithAttributes() {
        if (algorithm == null)
            return;

        keySizeInput.setValue(algorithm.getKeySize() + "");
        concrete.keyInput.setValue(Base64.getEncoder().encodeToString(algorithm.getKey().getEncoded()));
    }

    @Override
    public void close() {
        concrete.remove(keySizeInput);
//        concrete.remove(emptyPanel);

        concrete.setController(null);
        concrete.keySizeInput.setVisible(true);
        concrete.ivSizeInput.setVisible(true);
        concrete.ivInput.setVisible(true);
    }

    @Override
    public int getKeySizeInput() {
        if (!validateKeySize())
            return -1;

        return Integer.parseInt(keySizeInput.getValue());
    }

    @Override
    public void generateKey() {
    }

    @Override
    public boolean validateInput() {
        boolean check = super.validateInput();
        boolean validateKeySize = validateKeySize();

        if (!check && validateKeySize)
            JOptionPane.showMessageDialog(getRootPane(), "Please enter all require values.",
                    "Error", JOptionPane.ERROR_MESSAGE);

        return check && validateKeySize;
    }

    private boolean validateKeySize() {
        if (keySizeInput.getValue().isEmpty()) {
            keySizeInput.error();
            JOptionPane.showMessageDialog(getRootPane(), "Please enter all require values.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        keySizeInput.hideError();

        int keySize = Integer.parseInt(keySizeInput.getValue());

        if (!algorithm.validateKeySize(keySize)) {
            keySizeInput.info.setForeground(Color.RED);
            JOptionPane.showMessageDialog(getRootPane(), "Please enter a valid key size.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } else {
            keySizeInput.info.setForeground(Color.GRAY);
        }

        return true;
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
            concrete.keyInput.setValue(key);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(getRootPane(), "Failed to load the key.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public String encryptBase64(String plainText) {
        if (!validateInput()) {
            return null;
        }

        return super.encryptBase64(plainText);
    }

    @Override
    public String decryptBase64(String cipherText) {
        if (!validateInput()) {
            return null;
        }

        return super.decryptBase64(cipherText);
    }

    @Override
    public boolean encryptFile(String src, String des) {
        if (!validateInput()) {
            return false;
        }

        return super.encryptFile(src, des);
    }

    @Override
    public boolean decryptFile(String src, String des) {
        if (!validateInput()) {
            return false;
        }

        return super.decryptFile(src, des);
    }
}