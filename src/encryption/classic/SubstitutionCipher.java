package encryption.classic;

import java.util.*;
import java.util.stream.Collectors;

public class SubstitutionCipher {

    private String key;
    private static Set<Character> set;

    static {
        set = new HashSet<>();
        for (int i = 'A'; i <= 'Z'; i++) {
            set.add((char) i);
        }
    }

    public String generateKey() {
        List<Character> randList = new ArrayList<>(set);

        Collections.shuffle(randList);

        return randList.stream()
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    public static void main(String[] args) {
        SubstitutionCipher sc = new SubstitutionCipher();
        System.out.println("Generated Key: " + sc.generateKey());
    }
}
