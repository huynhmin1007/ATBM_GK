package ui.view.fragment.symmetric;

import encryption.common.Algorithm;
import encryption.symmetric.AES;
import encryption.symmetric.Symmetric;
import encryption.symmetric.SymmetricFactory;

public class AESFragment extends SymmetricDecorator {

    private AES algorithm;

    public AESFragment(SymmetricConcrete symmetricConcrete) {
        super(symmetricConcrete);
        algorithm = (AES) SymmetricFactory.getSymmetric(Algorithm.AES);
    }

    @Override
    public Symmetric getAlgorithm() {
        return algorithm;
    }
}