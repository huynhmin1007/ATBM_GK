package ui.view.fragment.symmetric;

import encryption.common.Algorithm;
import encryption.symmetric.DES;
import encryption.symmetric.Symmetric;
import encryption.symmetric.SymmetricFactory;

import javax.swing.*;

public class DESFragment extends SymmetricDecorator {

    private DES algorithm;

    public DESFragment(SymmetricConcrete symmetricConcrete) {
        super(symmetricConcrete);
        algorithm = (DES) SymmetricFactory.getSymmetric(Algorithm.DES);
    }

    @Override
    public Symmetric getAlgorithm() {
        return algorithm;
    }

    @Override
    public void setAlgorithm(Symmetric algorithm) {
        this.algorithm = (DES) algorithm;
    }

    @Override
    public void close() {

    }

    @Override
    public boolean validateInput() {
        boolean check = super.validateInput();

        if (!check)
            JOptionPane.showMessageDialog(getRootPane(), "Please enter all require values.",
                    "Error", JOptionPane.ERROR_MESSAGE);

        return check;
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