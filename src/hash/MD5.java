package hash;

import utils.FileHelper;

import java.io.*;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

    private static final String ALGORITHM = "MD5";
    private static final Integer RADIX = 16;

    public String hash(String plainText) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(ALGORITHM);
        byte[] bytesHash = messageDigest.digest(plainText.getBytes());

        return new BigInteger(1, bytesHash).toString(RADIX);
    }

    public String hashFile(String src) throws IOException, NoSuchAlgorithmException {
        File srcFile = FileHelper.findFile(src);

        MessageDigest messageDigest = MessageDigest.getInstance(ALGORITHM);
        InputStream in = new BufferedInputStream(new FileInputStream(srcFile));
        DigestInputStream dis = new DigestInputStream(in, messageDigest);

        byte[] buffer = new byte[10 * 1024];
        int byteRead;

        do {
            byteRead = dis.read(buffer);
        } while (byteRead != -1);

        return new BigInteger(1, messageDigest.digest()).toString(RADIX);
    }
}