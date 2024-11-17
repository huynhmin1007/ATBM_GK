package ui.view.fragment.asymmetric;

import encryption.symmetric.Symmetric;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public interface ASymmetricFragment {
    void handleModeChangeListener();

    void handlePaddingChangeListener();

    void saveKey();

    void saveKey(DataOutputStream out);

    void loadKey();

    void loadKey(DataInputStream in);

    void generateKey();

    void display();

    void close();

    void configureEncrypt();
    void configureDecrypt();

    int getKeySize();

    boolean validateInputEncrypt();
    boolean validateInputDecrypt();

    String[] getMode();

    String[] getPadding(String mode);

    String encryptBase64(String plainText);

    String decryptBase64(String cipherText);

    boolean encryptFile(String src, String des, Symmetric symmetric);

    Symmetric decryptFile(String src, String des);
}
