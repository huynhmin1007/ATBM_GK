package ui.view.fragment.symmetric;

import encryption.symmetric.Symmetric;
import ui.common.Dimensions;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public abstract class SymmetricDecorator extends JPanel implements SymmetricFragment {

    protected SymmetricConcrete concrete;
    protected GridBagConstraints gbc;

    protected int x = 0, y = 0;

    protected SymmetricDecorator(SymmetricConcrete concrete) {
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

    public abstract Symmetric getAlgorithm();

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
    public void configure() {
        concrete.configure();
        concrete.algorithm.setKeySize(getKeySize());
    }

    @Override
    public boolean decryptFile(String src, String des) {
        return concrete.decryptFile(src, des);
    }

    @Override
    public boolean encryptFile(String src, String des) {
        return concrete.encryptFile(src, des);
    }

    @Override
    public int getKeySize() {
        return concrete.getKeySize();
    }

    @Override
    public boolean validateInput() {
        return concrete.validateInput();
    }

    @Override
    public void loadKey(DataInputStream in) {
        concrete.loadKey(in);
    }

    @Override
    public void saveKey(DataOutputStream out) {
        concrete.saveKey(out);
    }
}