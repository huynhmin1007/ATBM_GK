package ui.view.fragment.asymmetric;

import encryption.asymmetric.RSA;
import encryption.symmetric.Symmetric;
import ui.common.Dimensions;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public abstract class ASymmetricDecorator extends JPanel implements ASymmetricFragment {

    protected ASymmetricConcrete concrete;
    protected GridBagConstraints gbc;

    protected int x = 0, y = 0;

    protected ASymmetricDecorator(ASymmetricConcrete concrete) {
        this.concrete = concrete;
        setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = Dimensions.ZERO_INSETS;
        gbc.weightx = 1.0;
    }

    @Override
    public void handleModeChangeListener() {
        concrete.handleModeChangeListener();
    }

    @Override
    public void display() {
        gbc.gridx = x;
        gbc.gridy = y;
        add(concrete, gbc);
        concrete.setAlgorithm(getAlgorithm());
        concrete.display();
    }

    public abstract RSA getAlgorithm();

    @Override
    public String[] getMode() {
        return concrete.getMode();
    }

    @Override
    public String[] getPadding(String mode) {
        return concrete.getPadding(mode);
    }

    @Override
    public void generateKey() {
        concrete.generateKey();
    }

    @Override
    public void handlePaddingChangeListener() {
        concrete.handlePaddingChangeListener();
    }

    @Override
    public void saveKey() {
        concrete.saveKey();
    }

    @Override
    public void loadKey() {
        concrete.loadKey();
    }

    @Override
    public String decryptBase64(String cipherText) {
        return concrete.decryptBase64(cipherText);
    }

    @Override
    public String encryptBase64(String plainText) {
        return concrete.encryptBase64(plainText);
    }

    @Override
    public Symmetric decryptFile(String src, String des) {
        return concrete.decryptFile(src, des);
    }

    @Override
    public boolean encryptFile(String src, String des, Symmetric symmetric) {
        return concrete.encryptFile(src, des, symmetric);
    }

    @Override
    public int getKeySize() {
        return concrete.getKeySize();
    }

    @Override
    public void loadKey(DataInputStream in) {
        concrete.loadKey(in);
    }

    @Override
    public void saveKey(DataOutputStream out) {
        concrete.saveKey(out);
    }

    @Override
    public boolean validateInputEncrypt() {
        return concrete.validateInputEncrypt();
    }

    @Override
    public boolean validateInputDecrypt() {
        return concrete.validateInputDecrypt();
    }

    @Override
    public void configureDecrypt() {
        concrete.configureDecrypt();
    }

    @Override
    public void configureEncrypt() {
        concrete.configureEncrypt();
    }
}