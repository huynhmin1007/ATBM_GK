package encryption.asymmetric;

import encryption.common.Algorithm;

public class ASymmetricFactory {

    private ASymmetricFactory() {}

    public static RSA getASymmetric(Algorithm algorithm) {
        return new RSA();
    }
}
