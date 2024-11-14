package encryption.symmetric;

import encryption.common.Algorithm;
import encryption.common.Mode;
import encryption.common.Padding;
import encryption.utils.StringHelper;
import utils.FileHelper;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class DES {

    public static final Algorithm ALGORITHM_NAME = Algorithm.DES;

    private Mode mode = Mode.CBC;
    private Padding padding = Padding.PKCS5Padding;

    private int keySize;

    private SecretKey key;
    private IvParameterSpec iv;

    private List<String> algorithmsSupported;
    private int[] keySizeSupported;

    public DES() {
        initKeySizeSupported();
        initAlgorithmSupported();
    }

    private void initKeySizeSupported() {
        keySizeSupported = new int[]{128, 192, 256};
    }

    private void initAlgorithmSupported() {
        algorithmsSupported = new ArrayList<>();

        String delimiter = "/";

        algorithmsSupported.add(StringHelper.generateString(delimiter, ALGORITHM_NAME, Mode.CBC, Padding.NoPadding));
        algorithmsSupported.add(StringHelper.generateString(delimiter, ALGORITHM_NAME, Mode.CBC, Padding.ISO10126Padding));
        algorithmsSupported.add(StringHelper.generateString(delimiter, ALGORITHM_NAME, Mode.CBC, Padding.PKCS5Padding));

        algorithmsSupported.add(StringHelper.generateString(delimiter, ALGORITHM_NAME, Mode.CFB, Padding.NoPadding));
        algorithmsSupported.add(StringHelper.generateString(delimiter, ALGORITHM_NAME, Mode.CFB, Padding.ISO10126Padding));
        algorithmsSupported.add(StringHelper.generateString(delimiter, ALGORITHM_NAME, Mode.CFB, Padding.PKCS5Padding));

        algorithmsSupported.add(StringHelper.generateString(delimiter, ALGORITHM_NAME, Mode.CTR, Padding.NoPadding));
        algorithmsSupported.add(StringHelper.generateString(delimiter, ALGORITHM_NAME, Mode.CTS, Padding.NoPadding));

        algorithmsSupported.add(StringHelper.generateString(delimiter, ALGORITHM_NAME, Mode.ECB, Padding.NoPadding));
        algorithmsSupported.add(StringHelper.generateString(delimiter, ALGORITHM_NAME, Mode.ECB, Padding.ISO10126Padding));
        algorithmsSupported.add(StringHelper.generateString(delimiter, ALGORITHM_NAME, Mode.ECB, Padding.PKCS5Padding));

        algorithmsSupported.add(StringHelper.generateString(delimiter, ALGORITHM_NAME, Mode.OFB, Padding.NoPadding));
        algorithmsSupported.add(StringHelper.generateString(delimiter, ALGORITHM_NAME, Mode.OFB, Padding.ISO10126Padding));
        algorithmsSupported.add(StringHelper.generateString(delimiter, ALGORITHM_NAME, Mode.OFB, Padding.PKCS5Padding));

        algorithmsSupported.add(StringHelper.generateString(delimiter, ALGORITHM_NAME, Mode.PCBC, Padding.NoPadding));
        algorithmsSupported.add(StringHelper.generateString(delimiter, ALGORITHM_NAME, Mode.PCBC, Padding.ISO10126Padding));
        algorithmsSupported.add(StringHelper.generateString(delimiter, ALGORITHM_NAME, Mode.PCBC, Padding.PKCS5Padding));
    }

    public void setTransformation(String transformation) {
        String[] strs = transformation.split("/");

        mode = Mode.valueOf(strs[1]);
        padding = Padding.valueOf(strs[2]);
    }

    public SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM_NAME.name());
        keyGenerator.init(keySize);

        key = keyGenerator.generateKey();

        return key;
    }

    public IvParameterSpec generateIV() {
        byte[] ivBytes = new byte[8];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(ivBytes);
        iv = new IvParameterSpec(ivBytes);

        return iv;
    }

    public void loadKeyAndIV(SecretKey key, IvParameterSpec iv) {
        this.key = key;
        this.iv = iv;
    }

    public void loadKey(SecretKey key) {
        this.key = key;
        this.iv = null;
    }

    public byte[] encrypt(byte[] plainText) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        String transformation = ALGORITHM_NAME + "/" + mode + "/" + padding;
        Cipher cipher = initCipher(Cipher.ENCRYPT_MODE);

        return cipher.doFinal(plainText);
    }

    public byte[] encrypt(String plainText) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        return encrypt(plainText.getBytes());
    }

    public String encryptBase64(String plainText) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        return Base64.getEncoder().encodeToString(encrypt(plainText));
    }

    public byte[] decrypt(byte[] cipherText) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        String transformation = ALGORITHM_NAME + "/" + mode + "/" + padding;
        Cipher cipher = initCipher(Cipher.DECRYPT_MODE);

        return cipher.doFinal(cipherText);
    }

    public String decrypt(String cipherText) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return new String(decrypt(cipherText.getBytes()));
    }

    public String decryptBase64(String cipherText) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        return new String(decrypt(Base64.getDecoder().decode(cipherText.getBytes())));
    }

    private Cipher initCipher(int opmode) throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        String transformation = StringHelper.generateString("/", ALGORITHM_NAME, mode, padding);
        Cipher cipher = Cipher.getInstance(transformation);

        if (iv == null) {
            cipher.init(opmode, key);
        } else {
            cipher.init(opmode, key, iv);
        }

        return cipher;
    }

    public boolean encryptFile(String src, String des, boolean append) throws FileNotFoundException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        File srcFile = FileHelper.findFile(src);
        File desFile = new File(des);

        Cipher cipher = initCipher(Cipher.ENCRYPT_MODE);

        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(srcFile));
             BufferedOutputStream bos = new BufferedOutputStream(new CipherOutputStream(new FileOutputStream(desFile, append), cipher))) {

            byte[] bufferBytes = new byte[10 * 1024];
            int bytesRead;

            while ((bytesRead = in.read(bufferBytes)) != -1) {
                bos.write(bufferBytes, 0, bytesRead);
            }
        } catch (IOException e) {
            desFile.delete();
            return false;
        }
        return true;
    }

    public boolean decryptFile(String src, String des) throws FileNotFoundException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        File srcFile = FileHelper.findFile(src);
        File desFile = new File(des);

        Cipher cipher = initCipher(Cipher.DECRYPT_MODE);

        try (BufferedInputStream in = new BufferedInputStream(new CipherInputStream(new FileInputStream(srcFile), cipher));
             BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(desFile))) {
            byte[] bufferBytes = new byte[10 * 1024];
            int bytesRead;

            while ((bytesRead = in.read(bufferBytes)) != -1) {
                out.write(bufferBytes, 0, bytesRead);
            }
        } catch (IOException e) {
            desFile.delete();
            return false;
        }
        return true;
    }

    public void decryptFile(InputStream is, String des) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        File desFile = new File(des);

        Cipher cipher = initCipher(Cipher.DECRYPT_MODE);

        try (BufferedInputStream in = new BufferedInputStream(new CipherInputStream(is, cipher));
             BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(desFile))) {
            byte[] bufferBytes = new byte[10 * 1024];
            int bytesRead;

            while ((bytesRead = in.read(bufferBytes)) != -1) {
                out.write(bufferBytes, 0, bytesRead);
            }
        } catch (IOException e) {
            desFile.delete();
            throw new RuntimeException(e);
        }
    }

    public void setKeySize(int keySize) {
        this.keySize = keySize;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void setPadding(Padding padding) {
        this.padding = padding;
    }

    public List<String> getAlgorithmsSupported() {
        return algorithmsSupported;
    }

    public int[] getKeySizeSupported() {
        return keySizeSupported;
    }
}
