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
import java.util.Arrays;
import java.util.Base64;

public class DES extends Symmetric {

    public DES() {
        algorithm = Algorithm.DES;
        initAlgorithmSupported();
    }

    private void initAlgorithmSupported() {
        algorithmsSupported = new ArrayList<>();

        String delimiter = "/";

        algorithmsSupported.add(StringHelper.generateString(delimiter, algorithm, Mode.CBC, Padding.NoPadding));
        algorithmsSupported.add(StringHelper.generateString(delimiter, algorithm, Mode.CBC, Padding.ISO10126Padding));
        algorithmsSupported.add(StringHelper.generateString(delimiter, algorithm, Mode.CBC, Padding.PKCS5Padding));

        algorithmsSupported.add(StringHelper.generateString(delimiter, algorithm, Mode.CFB, Padding.NoPadding));
        algorithmsSupported.add(StringHelper.generateString(delimiter, algorithm, Mode.CFB, Padding.ISO10126Padding));
        algorithmsSupported.add(StringHelper.generateString(delimiter, algorithm, Mode.CFB, Padding.PKCS5Padding));

        algorithmsSupported.add(StringHelper.generateString(delimiter, algorithm, Mode.CTR, Padding.NoPadding));
        algorithmsSupported.add(StringHelper.generateString(delimiter, algorithm, Mode.CTS, Padding.NoPadding));

        algorithmsSupported.add(StringHelper.generateString(delimiter, algorithm, Mode.ECB, Padding.NoPadding));
        algorithmsSupported.add(StringHelper.generateString(delimiter, algorithm, Mode.ECB, Padding.ISO10126Padding));
        algorithmsSupported.add(StringHelper.generateString(delimiter, algorithm, Mode.ECB, Padding.PKCS5Padding));

        algorithmsSupported.add(StringHelper.generateString(delimiter, algorithm, Mode.OFB, Padding.NoPadding));
        algorithmsSupported.add(StringHelper.generateString(delimiter, algorithm, Mode.OFB, Padding.ISO10126Padding));
        algorithmsSupported.add(StringHelper.generateString(delimiter, algorithm, Mode.OFB, Padding.PKCS5Padding));

        algorithmsSupported.add(StringHelper.generateString(delimiter, algorithm, Mode.PCBC, Padding.NoPadding));
        algorithmsSupported.add(StringHelper.generateString(delimiter, algorithm, Mode.PCBC, Padding.ISO10126Padding));
        algorithmsSupported.add(StringHelper.generateString(delimiter, algorithm, Mode.PCBC, Padding.PKCS5Padding));
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

    private Cipher initCipher(int opmode) throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        String transformation = StringHelper.generateString("/", algorithm, mode, padding);
        Cipher cipher = Cipher.getInstance(transformation);

        if (iv == null) {
            cipher.init(opmode, key);
        } else {
            cipher.init(opmode, key, iv);
        }

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


    @Override
    public int[] getKeySizeSupported() {
        return new int[]{56};
    }

    @Override
    public boolean validateKeySize(int keySize) {
        return Arrays.stream(getKeySizeSupported()).anyMatch(v -> v == keySize);
    }

    @Override
    public int getIVSize(String mode) {
        return switch (mode) {
            case "CBC", "CFB", "CTR", "CTS", "OFB", "PCBC" -> 8;
            default -> -1;
        };
    }
}
