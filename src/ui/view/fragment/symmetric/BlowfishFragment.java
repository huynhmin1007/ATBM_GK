package ui.view.fragment.symmetric;

import encryption.common.Algorithm;
import encryption.symmetric.Blowfish;
import encryption.symmetric.Symmetric;
import encryption.symmetric.SymmetricFactory;
import ui.view.component.InputField;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;

public class BlowfishFragment extends SymmetricDecorator {

    private Blowfish algorithm;
    private InputField keySizeInput;

    public BlowfishFragment(SymmetricConcrete symmetricConcrete) {
        super(symmetricConcrete);
        algorithm = (Blowfish) SymmetricFactory.getSymmetric(Algorithm.Blowfish);

        keySizeInput = new InputField("Key Size:");
        keySizeInput.info("Key size must be a multiple of 8, ranging from 32 to 448\"");
        keySizeInput.setInput(keySizeInput.input, 0);
        keySizeInput.setNumericOnly();
    }

    @Override
    public Symmetric getAlgorithm() {
        return algorithm;
    }

    @Override
    public void setAlgorithm(Symmetric algorithm) {
        this.algorithm = (Blowfish) algorithm;
    }

    @Override
    public void display() {
        concrete.keySizeInput.setVisible(false);
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
        super.displayWithAttributes();
    }

    @Override
    public void close() {
        concrete.remove(keySizeInput);

        concrete.setController(null);
        concrete.keySizeInput.setVisible(true);
    }

    @Override
    public int getKeySizeInput() {
        if (!validateKeySize())
            return -1;

        return Integer.parseInt(keySizeInput.getValue());
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
    public void generateKey() {
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

            if (in.available() != 0) {
                int ivSize = in.readInt();
                String iv = in.readUTF();

                if (algorithm.getIVSize(concrete.mode) != ivSize) {
                    throw new IOException();
                }
                concrete.ivSizeInput.setValue(ivSize + "");
                concrete.ivInput.setEnabled(true);
                concrete.ivInput.setValue(iv);
            } else {
                concrete.ivSizeInput.setValue("");
                concrete.ivInput.setValue("");
                concrete.ivSizeInput.setEnabled(false);
                concrete.ivInput.setEnabled(false);
            }
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