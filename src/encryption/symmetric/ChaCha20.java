package encryption.symmetric;

import encryption.common.Algorithm;
import encryption.common.Mode;
import encryption.common.Padding;
import encryption.utils.StringHelper;
import utils.FileHelper;

import javax.crypto.*;
import javax.crypto.spec.ChaCha20ParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;

public class ChaCha20 extends Symmetric {

    private ChaCha20ParameterSpec paramSpec;
    private int counter;

    public ChaCha20() {
        algorithm = Algorithm.ChaCha20;
        mode = Mode.None;
        padding = Padding.NoPadding;
        keySize = 256;
        initAlgorithmSupported();
    }

    private void initAlgorithmSupported() {
        algorithmsSupported = new ArrayList<>();

        String delimiter = "/";

        algorithmsSupported.add(StringHelper.generateString(delimiter, algorithm, Mode.None, Padding.NoPadding));
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

    public ChaCha20ParameterSpec generateParamSpec() {
        byte[] nonce = new byte[12];
        SecureRandom random = new SecureRandom();
        random.nextBytes(nonce);

        paramSpec = new ChaCha20ParameterSpec(nonce, counter);

        return paramSpec;
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

    @Override
    public boolean validateKeySize(int keySize) {
        return keySize == 256;
    }

    private Cipher initCipher(int opmode) throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        String transformation = StringHelper.generateString("/", algorithm, mode, padding);
        Cipher cipher = Cipher.getInstance(transformation);

        cipher.init(opmode, key, paramSpec);

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
        return new int[]{256};
    }

    @Override
    public int getIVSize(String mode) {
        return -1;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getCounter() {
        return counter;
    }

    public void setParamSpec(ChaCha20ParameterSpec paramSpec) {
        this.paramSpec = paramSpec;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        ChaCha20 chaCha20 = new ChaCha20();
        chaCha20.setKeySize(256);
        chaCha20.generateKey();
        chaCha20.setCounter(1);
//        chaCha20.generateParamSpec();
        ChaCha20ParameterSpec param = chaCha20.generateParamSpec();

        String plt = "Đai học Nông Lâm";

        String cpt = chaCha20.encryptBase64(plt);
        String de = chaCha20.decryptBase64(cpt);

        System.out.println(cpt);
        System.out.println(de);

        ByteBuffer buffer = ByteBuffer.allocate(12 + 4);
        buffer.put(param.getNonce());
        buffer.putInt(param.getCounter());
        String encode = Base64.getEncoder().encodeToString(buffer.array());

        byte[] data = Base64.getDecoder().decode(encode);
        if(data.length == 16) {
            ByteBuffer b = ByteBuffer.wrap(data);
            byte[] nonce = new byte[12];
            b.get(nonce);
            int counter = b.getInt();
            ChaCha20ParameterSpec newParam = new ChaCha20ParameterSpec(nonce, counter);

            chaCha20.setParamSpec(newParam);

            System.out.println(chaCha20.decryptBase64(cpt));
        }
    }
}
