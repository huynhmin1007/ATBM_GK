package ui.view.fragment.asymmetric;

import encryption.asymmetric.ASymmetricFactory;
import encryption.asymmetric.RSA;
import encryption.common.Algorithm;
import encryption.symmetric.Symmetric;

import javax.swing.*;

public class RSAFragment extends ASymmetricDecorator {

    private RSA algorithm;

    public RSAFragment(ASymmetricConcrete concrete) {
        super(concrete);
        algorithm = ASymmetricFactory.getASymmetric(Algorithm.RSA);
    }

    @Override
    public RSA getAlgorithm() {
        return algorithm;
    }

    @Override
    public void close() {

    }

    @Override
    public boolean validateInputEncrypt() {
        boolean check = super.validateInputEncrypt();

        if (!check)
            JOptionPane.showMessageDialog(getRootPane(), "Please enter all require values.",
                    "Error", JOptionPane.ERROR_MESSAGE);

        return check;
    }

    @Override
    public boolean validateInputDecrypt() {
        boolean check = super.validateInputDecrypt();

        if (!check)
            JOptionPane.showMessageDialog(getRootPane(), "Please enter all require values.",
                    "Error", JOptionPane.ERROR_MESSAGE);

        return check;
    }

    @Override
    public String encryptBase64(String plainText) {
        if (!validateInputEncrypt()) {
            return null;
        }

        return super.encryptBase64(plainText);
    }

    @Override
    public String decryptBase64(String cipherText) {
        if (!validateInputDecrypt()) {
            return null;
        }

        return super.decryptBase64(cipherText);
    }

    @Override
    public boolean encryptFile(String src, String des, Symmetric symmetric) {
        if (!validateInputEncrypt()) {
            return false;
        }

        return super.encryptFile(src, des, symmetric);
    }

    @Override
    public Symmetric decryptFile(String src, String des) {
        if (!validateInputDecrypt()) {
            return null;
        }

        return super.decryptFile(src, des);
    }
}
