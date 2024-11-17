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

    @Override
    public void setAlgorithm(Symmetric algorithm) {
        this.algorithm = (AES) algorithm;
    }

    @Override
    public void close() {

    }

    @Override
    public void display() {
        super.display();
    }
}