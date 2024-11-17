package encryption.asymmetric;

import encryption.common.Algorithm;
import encryption.symmetric.Symmetric;
import encryption.symmetric.SymmetricFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class RSA {

    private KeyPair keyPair;

    private PublicKey publicKey;
    private PrivateKey privateKey;

    private Algorithm algorithm;
    private List<String> algorithmsSupported;
    private int keySize;
    private String mode;
    private String padding;

    public RSA() {
        algorithm = Algorithm.RSA;
        initAlgorithmSupported();
    }

    private void initAlgorithmSupported() {
        algorithmsSupported = new ArrayList<>();

        algorithmsSupported.add("RSA/ECB/PKCS1Padding");
        algorithmsSupported.add("RSA/ECB/OAEPPadding");
        algorithmsSupported.add("RSA/ECB/OAEPWithMD5AndMGF1Padding");
        algorithmsSupported.add("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
        algorithmsSupported.add("RSA/ECB/OAEPWithSHA-224AndMGF1Padding");
        algorithmsSupported.add("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        algorithmsSupported.add("RSA/ECB/OAEPWithSHA-384AndMGF1Padding");
        algorithmsSupported.add("RSA/ECB/OAEPWithSHA-512AndMGF1Padding");
        algorithmsSupported.add("RSA/ECB/OAEPWithSHA-512/224AndMGF1Padding");
        algorithmsSupported.add("RSA/ECB/OAEPWithSHA-512/256AndMGF1Padding");
    }

    public void generateKey() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(keySize);
        keyPair = generator.generateKeyPair();

        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
    }

    public void loadKey(KeyPair keyPair) {
        this.keyPair = keyPair;
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
    }

    private Cipher initCipher(int opmode) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance(algorithm.name() + "/" + mode + "/" + padding);

        if (opmode == Cipher.ENCRYPT_MODE) {
            cipher.init(opmode, publicKey);
        } else if (opmode == Cipher.DECRYPT_MODE) {
            cipher.init(opmode, privateKey);
        }

        return cipher;
    }

    public byte[] encrypt(byte[] plainText) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = initCipher(Cipher.ENCRYPT_MODE);
        return cipher.doFinal(plainText);
    }

    public byte[] encrypt(String plainText) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = initCipher(Cipher.ENCRYPT_MODE);
        byte[] in = plainText.getBytes(StandardCharsets.UTF_8);

        return cipher.doFinal(in);
    }

    public String encryptBase64(String plainText) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return Base64.getEncoder().encodeToString(encrypt(plainText));
    }

    public byte[] decrypt(byte[] cipherText) throws IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Cipher cipher = initCipher(Cipher.DECRYPT_MODE);

        return cipher.doFinal(cipherText);
    }

    public String decryptBase64(String cipherText) throws IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        byte[] decryptBytes = Base64.getDecoder().decode(cipherText);

        return new String(decrypt(decryptBytes));
    }

    public boolean encryptFile(String src, String des, Symmetric symmetric) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, FileNotFoundException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(des));
             DataOutputStream out = new DataOutputStream(bos)) {
            out.writeUTF(symmetric.getAlgorithm());
        } catch (IOException e) {
            return false;
        }

        symmetric.saveConfigure(des, this, true);
        return symmetric.encryptFile(src, des, true);
    }

    public Symmetric decryptFile(String src, String des) throws FileNotFoundException, IllegalBlockSizeException, NoSuchPaddingException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(src));
             DataInputStream in = new DataInputStream(is)) {
            String algorithmName = in.readUTF();
            Symmetric symmetric = SymmetricFactory.getSymmetric(Algorithm.valueOf(algorithmName));
            return symmetric.decryptFile(symmetric.loadConfigure(is, this), des) ? symmetric : null;
        } catch (IOException e) {
            return null;
        }
    }

    public int[] getKeySizeSupported() {
        return new int[]{1024, 2048, 3072, 4096};
    }

    public boolean validateKeySize(int keySize) {
        return Arrays.stream(getKeySizeSupported()).anyMatch(v -> v == keySize);
    }

    public List<String> getAlgorithmsSupported() {
        return algorithmsSupported;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setPadding(String padding) {
        this.padding = padding;
    }

    public int getKeySize() {
        return keySize;
    }

    public void setKeySize(int keySize) {
        this.keySize = keySize;
    }

    public String getAlgorithm() {
        return algorithm.name();
    }

    public PrivateKey decodePrivateKey(String base64Key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm.name());
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
    }

    public PublicKey decodePublicKey(String base64Key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm.name());
        return keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
    }

    public void setPrivateKey(String privateKey) throws Exception {
        this.privateKey = decodePrivateKey(privateKey);
    }

    public void setPublicKey(String publicKey) throws Exception {
        this.publicKey = decodePublicKey(publicKey);
    }
}
