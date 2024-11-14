package ui.view.fragment.symmetric;

import encryption.symmetric.Symmetric;
import ui.common.Dimensions;

import javax.swing.*;
import java.awt.*;

public abstract class SymmetricDecorator extends JPanel implements SymmetricFragment {

    protected SymmetricConcrete concrete;
    protected GridBagConstraints gbc;

    protected SymmetricDecorator(SymmetricConcrete concrete) {
        this.concrete = concrete;
        setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(Dimensions.MARGIN_VERTICAL, Dimensions.MARGIN_HORIZONTAL, Dimensions.MARGIN_VERTICAL, Dimensions.MARGIN_HORIZONTAL);
        add(concrete, gbc);
    }

    @Override
    public void handleModeChangeListener() {
        concrete.handleModeChangeListener();
    }

    @Override
    public void display() {
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
    }

    @Override
    public boolean decryptFile(String src, String des) {
        return concrete.decryptFile(src, des);
    }

    @Override
    public boolean encryptFile(String src, String des) {
        return concrete.encryptFile(src, des);
    }
}

