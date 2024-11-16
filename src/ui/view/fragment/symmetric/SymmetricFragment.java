package ui.view.fragment.symmetric;

import java.io.File;

public interface SymmetricFragment {

    void handleModeChangeListener();

    void handlePaddingChangeListener();

    void saveKey();

    void loadKey();

    void generateKey();

    void display();

    void close();

    void configure();

    int getKeySize();

    boolean validateInput();

    String[] getMode();

    String[] getPadding(String mode);

    String encryptBase64(String plainText);

    String decryptBase64(String cipherText);

    boolean encryptFile(String src, String des);

    boolean decryptFile(String src, String des);
}