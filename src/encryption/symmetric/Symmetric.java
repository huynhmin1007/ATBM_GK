package encryption.symmetric;

import encryption.asymmetric.RSA;
import encryption.common.Algorithm;
import encryption.common.Mode;
import encryption.common.Padding;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.FileNotFoundException;
import java.io.InputStream;
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

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void setPadding(Padding padding) {
        this.padding = padding;
    }

    public int getKeySize() {
        return this.keySize;
    }

    public SecretKey getKey() {
        return this.key;
    }

    public IvParameterSpec getIv() {
        return this.iv;
    }

    public String getMode() {
        return this.mode.name();
    }

    public String getPadding() {
        return this.padding.name();
    }

    public abstract boolean decryptFile(InputStream is, String des) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException;

    public abstract void saveConfigure(String des, RSA asymmetric, boolean append) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException;

    public abstract InputStream loadConfigure(InputStream is, RSA asymmetric) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException;

    public abstract boolean validateKeySize(int keySize);

    public abstract boolean encryptFile(String src, String des, boolean append) throws FileNotFoundException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException;

    public abstract boolean decryptFile(String src, String des) throws FileNotFoundException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException;
}
