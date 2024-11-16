package encryption.symmetric;

import encryption.common.Algorithm;

public class SymmetricFactory {

    private SymmetricFactory() {

    }

    public static final Symmetric getSymmetric(Algorithm algorithm) {
        return switch (algorithm) {
            case AES -> new AES();
            case DES -> new DES();
            case ARCFOUR -> new ARCFOUR();
            case Blowfish -> new Blowfish();
            case ChaCha20 -> new ChaCha20();
            default -> null;
        };
    }
}
