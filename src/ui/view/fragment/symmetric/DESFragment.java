package ui.view.fragment.symmetric;

import encryption.common.Algorithm;
import encryption.symmetric.DES;
import encryption.symmetric.Symmetric;
import encryption.symmetric.SymmetricFactory;

public class DESFragment extends SymmetricDecorator {

    private DES algorithm;

    public DESFragment(SymmetricConcrete symmetricConcrete) {
        super(symmetricConcrete);
        algorithm = (DES) SymmetricFactory.getSymmetric(Algorithm.DES);
    }

    @Override
    public Symmetric getAlgorithm() {
        return algorithm;
    }

    @Override
    public void close() {

    }
}