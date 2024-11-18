package ui.view.fragment.symmetric;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public interface SymmetricFragment {

    void handleModeChangeListener();

    void handlePaddingChangeListener();

    void saveKey();

    void saveKey(DataOutputStream out);

    void loadKey();

    void loadKey(DataInputStream in);

    void generateKey();

    void display();
    void displayWithAttributes();

    void close();

    void configure();

    int getKeySizeInput();

    boolean validateInput();

    String[] getMode();

    String[] getPadding(String mode);

    String encryptBase64(String plainText);

    String decryptBase64(String cipherText);

    boolean encryptFile(String src, String des);

    boolean decryptFile(String src, String des);
}