package encryption.utils;

import java.util.Arrays;

public class StringHelper {

    public static String generateString(String delimiter, Enum... e) {
        StringBuilder str = new StringBuilder();

        Arrays.stream(e)
                .forEach(v -> {
                    str.append(v.name())
                            .append(delimiter);
                });

        return str.deleteCharAt(str.length() - "/".length()).toString();
    }
}
