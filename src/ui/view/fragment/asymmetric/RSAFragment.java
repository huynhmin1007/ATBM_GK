package ui.view.fragment.asymmetric;

import encryption.asymmetric.ASymmetricFactory;
import encryption.asymmetric.RSA;
import encryption.common.Algorithm;

public class RSAFragment extends ASymmetricDecorator {

    private RSA algorithm;

    public RSAFragment(ASymmetricConcrete concrete) {
        super(concrete);
        algorithm = ASymmetricFactory.getASymmetric(Algorithm.RSA);
    }

    @Override
    public RSA getAlgorithm() {
        return algorithm;
    }

    @Override
    public void close() {

    }
}
