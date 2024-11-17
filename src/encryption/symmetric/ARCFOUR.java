package encryption.symmetric;

import encryption.asymmetric.RSA;
import encryption.common.Algorithm;
import encryption.common.Mode;
import encryption.common.Padding;
import encryption.utils.StringHelper;
import utils.FileHelper;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;

public class ARCFOUR extends Symmetric {

    public ARCFOUR() {
        algorithm = Algorithm.ARCFOUR;
        initAlgorithmSupported();
    }

    private void initAlgorithmSupported() {
        algorithmsSupported = new ArrayList<>();

        String delimiter = "/";

        algorithmsSupported.add(StringHelper.generateString(delimiter, algorithm, Mode.ECB, Padding.NoPadding));
    }

    @Override
    public SecretKey generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm.name());
        keyGenerator.init(keySize);

        key = keyGenerator.generateKey();

        return key;
    }

    @Override
    public IvParameterSpec generateIV() {
        return null;
    }

    public void loadKey(SecretKey key) {
        this.key = key;
        this.iv = null;
    }

    public byte[] encrypt(byte[] plainText) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
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
        Cipher cipher = initCipher(Cipher.DECRYPT_MODE);

        return cipher.doFinal(cipherText);
    }

    public String decrypt(String cipherText) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return new String(decrypt(cipherText.getBytes()));
    }

    public String decryptBase64(String cipherText) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        return new String(decrypt(Base64.getDecoder().decode(cipherText.getBytes())));
    }

    @Override
    public void saveConfigure(String des, RSA asymmetric, boolean append) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(des, append));
             DataOutputStream out = new DataOutputStream(bos)) {
            out.writeUTF(mode.name());
            out.writeUTF(padding.name());

            byte[] keyBytes = asymmetric.encrypt(key.getEncoded());
            out.writeInt(keySize);
            out.writeInt(keyBytes.length);
            out.write(keyBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InputStream loadConfigure(InputStream is, RSA asymmetric) throws IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        BufferedInputStream bis = new BufferedInputStream(is);
        DataInputStream in = new DataInputStream(bis);
        try {
            mode = Mode.valueOf(in.readUTF());
            padding = Padding.valueOf(in.readUTF());
            keySize = in.readInt();

            int keyLength = in.readInt();
            byte[] keyDecrypt = asymmetric.decrypt(in.readNBytes(keyLength));
            key = new SecretKeySpec(keyDecrypt, algorithm.name());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bis;
    }

    @Override
    public boolean validateKeySize(int keySize) {
        return keySize >= 40 && keySize <= 1024;
    }

    private Cipher initCipher(int opmode) throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        String transformation = StringHelper.generateString("/", algorithm, mode, padding);
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(opmode, key);

        return cipher;
    }

    @Override
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

    @Override
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

    @Override
    public boolean decryptFile(InputStream is, String des) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        File desFile = new File(des);

        Cipher cipher = initCipher(Cipher.DECRYPT_MODE);

        try (BufferedInputStream in = new BufferedInputStream(new CipherInputStream(is, cipher));
             BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(desFile))) {
            byte[] bufferBytes = new byte[10 * 1024];
            int bytesRead;

            while ((bytesRead = in.read(bufferBytes)) != -1) {
                out.write(bufferBytes, 0, bytesRead);
            }

            return true;
        } catch (IOException e) {
            desFile.delete();
            return false;
        }
    }


    @Override
    public int[] getKeySizeSupported() {
        return new int[]{128};
    }

    @Override
    public int getIVSize(String mode) {
        return -1;
    }
}
