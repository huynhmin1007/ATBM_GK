package encryption.classic;

import java.util.Random;

public class Caesar {

    public static final Integer N = 26;

    private int key;

    public int generateKey() {
        return new Random().nextInt(N);
    }

    public void loadKey(int key) {
        this.key = key;
    }

    public String encrypt(String plainText) {
        StringBuilder cipherText = new StringBuilder();

        for(char c : plainText.toCharArray()) {
            char cp = (char) ((c - 'A' + key) % N + 'A');
            cipherText.append(cp);
        }

        return cipherText.toString();
    }

    public String decrypt(String cipherText) {
        StringBuilder plainText = new StringBuilder();

        for(char c : cipherText.toCharArray()) {
            char cp = (char) ((c - 'A' - key) % N + 'A');
            plainText.append(cp);
        }

        return plainText.toString();
    }

    public static void main(String[] args) {
        String plainText = "ABC";

        Caesar caesar = new Caesar();
        caesar.loadKey(9);

        String cipherText = "bzcwvoliqpwkvwvotiu";

        System.out.println(cipherText);
        System.out.println(caesar.decrypt(cipherText));
    }
}
