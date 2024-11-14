package encryption.symmetric;

import encryption.common.Algorithm;
import encryption.common.Mode;
import encryption.common.Padding;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.FileNotFoundException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public abstract class Symmetric {

    protected Algorithm algorithm;
    protected List<String> algorithmsSupported;
    protected int keySize;
    protected Mode mode;
    protected Padding padding;
    protected SecretKey key;
    protected IvParameterSpec iv;

    public List<String> getAlgorithmsSupported() {
        return algorithmsSupported;
    }

    public abstract int[] getKeySizeSupported();

    public abstract int getIVSize(String mode);

    public abstract SecretKey generateKey() throws NoSuchAlgorithmException;

    public abstract IvParameterSpec generateIV();

    public abstract String encryptBase64(String plainText) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException;
    public abstract String decryptBase64(String cipherText) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException;

    public void setKeySize(int keySize) {
        this.keySize = keySize;
    }

    public String getAlgorithm() {
        return algorithm.name();
    }

    public void setKey(SecretKey key) {
        this.key = key;
    }

    public void setIv(IvParameterSpec iv) {
        this.iv = iv;
    }

    public void setMode(String mode) {
        this.mode = Mode.valueOf(mode);
    }

    public void setPadding(String padding) {
        this.padding = Padding.valueOf(padding);
    }

    public abstract boolean encryptFile(String src, String des, boolean append) throws FileNotFoundException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException;
    public abstract boolean decryptFile(String src, String des) throws FileNotFoundException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException;
}
