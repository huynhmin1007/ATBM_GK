package encryption.asymmetric;

import encryption.symmetric.AES;
import utils.FileHelper;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

public class RSA {

    public static final Integer KEY_SIZE = 2048;
    public static final String TRANSFORMATION = "RSA/ECB/PKCS1Padding";

    private KeyPair keyPair;

    private PublicKey publicKey;
    private PrivateKey privateKey;

    public void generateKey() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(KEY_SIZE);
        keyPair = generator.generateKeyPair();

        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
    }

    public void loadKey(KeyPair keyPair) {
        this.keyPair = keyPair;
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
    }

    public byte[] encrypt(byte[] plainText) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        return cipher.doFinal(plainText);
    }

    public byte[] encrypt(String plainText) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] in = plainText.getBytes(StandardCharsets.UTF_8);

        return cipher.doFinal(in);
    }

    public String encryptBase64(String plainText) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return Base64.getEncoder().encodeToString(encrypt(plainText));
    }

    public byte[] decrypt(byte[] cipherText) throws IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return cipher.doFinal(cipherText);
    }

    public String decryptBase64(String cipherText) throws IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        byte[] decryptBytes = Base64.getDecoder().decode(cipherText);

        return new String(decrypt(decryptBytes));
    }

    public boolean encryptFile(String src, String des) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, FileNotFoundException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        AES aes = new AES();
        SecretKey key = aes.generateKey();
        IvParameterSpec iv = aes.generateIV();

        saveKeyAndIV(key, iv, des);

        return aes.encryptFile(src, des, true);
    }

    public boolean decryptFile(String src, String des) throws FileNotFoundException {
        File srcFile = FileHelper.findFile(src);
        AES aes = new AES();

        try (InputStream in = new BufferedInputStream(new FileInputStream(srcFile));
             DataInputStream dataInputStream = new DataInputStream(in)) {
            int keyLength = dataInputStream.readInt();
            int ivLength = dataInputStream.readInt();

            byte[] keyDecrypt = decrypt(dataInputStream.readNBytes(keyLength));
            byte[] ivDecrypt = decrypt(dataInputStream.readNBytes(ivLength));

            SecretKey key = new SecretKeySpec(keyDecrypt, "AES");
            IvParameterSpec iv = new IvParameterSpec(ivDecrypt);

            aes.loadKeyAndIV(key, iv);

            return aes.decryptFile(src, des);
        } catch (IOException | IllegalBlockSizeException | BadPaddingException | NoSuchPaddingException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveKeyAndIV(SecretKey key, IvParameterSpec iv, String des) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(des));
             DataOutputStream out = new DataOutputStream(bos)) {
            byte[] keyBytes = encrypt(key.getEncoded());
            byte[] ivBytes = encrypt(iv.getIV());

            out.writeInt(keyBytes.length);
            out.writeInt(ivBytes.length);

            out.write(keyBytes);
            out.write(ivBytes);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void savePrivateKey(PrivateKey privateKey, String des) {
        try (FileOutputStream fos = new FileOutputStream(des);
             ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(fos))) {
            out.writeObject(privateKey);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void savePublicKey(PublicKey publicKey, String des) {
        try (FileOutputStream fos = new FileOutputStream(des);
             ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(fos))) {
            out.writeObject(publicKey);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveKeyPair(KeyPair keyPair, String des) {
        try (FileOutputStream fos = new FileOutputStream(des);
             ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(fos))) {
            out.writeObject(keyPair);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadKeyPair(String src) throws FileNotFoundException {
        File file = FileHelper.findFile(src);

        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(fis))) {
            loadKey((KeyPair) in.readObject());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadPrivateKey(String src) throws FileNotFoundException {
        File file = FileHelper.findFile(src);

        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(fis))) {
            this.privateKey = (PrivateKey) in.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadPublicKey(String src) throws FileNotFoundException {
        File file = FileHelper.findFile(src);

        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(fis))) {
            this.publicKey = (PublicKey) in.readObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }
}
