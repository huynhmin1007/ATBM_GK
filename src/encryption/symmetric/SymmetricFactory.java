package encryption.symmetric;

import encryption.common.Algorithm;

public class SymmetricFactory {

    private SymmetricFactory() {

    }

    public static final Symmetric getSymmetric(Algorithm algorithm) {
        return switch (algorithm) {
            case AES -> new AES();
            default -> null;
        };
    }
}
