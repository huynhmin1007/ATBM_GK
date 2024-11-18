package ui.view.fragment.symmetric;

import encryption.common.Algorithm;
import encryption.symmetric.ChaCha20;
import encryption.symmetric.Symmetric;
import encryption.symmetric.SymmetricFactory;
import ui.common.Dimensions;
import ui.view.component.InputField;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ChaCha20Fragment extends SymmetricDecorator {

    private ChaCha20 algorithm;
    private InputField counterInput, nonceInput, paramSpecInput;

    private JPanel counterAndNoncePanel;

    public ChaCha20Fragment(SymmetricConcrete symmetricConcrete) {
        super(symmetricConcrete);

        algorithm = (ChaCha20) SymmetricFactory.getSymmetric(Algorithm.ChaCha20);

        counterInput = new InputField("Counter:");
        counterInput.info("Counter must be an Integer number");
        counterInput.setValue("0");
        counterInput.setNumericOnly();

        nonceInput = new InputField("Nonce:");
        nonceInput.setValue("12");
        nonceInput.info(" ");
        nonceInput.label.setHorizontalAlignment(SwingConstants.RIGHT);
        nonceInput.setEnabled(false);

        paramSpecInput = new InputField("Parameter:");

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = Dimensions.DEFAULT_INSETS;
        setLayout(new GridBagLayout());

        counterAndNoncePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        counterAndNoncePanel.add(counterInput);
        counterAndNoncePanel.add(nonceInput);

        add(counterAndNoncePanel, constraints);

        constraints.gridy = 1;
        constraints.weightx = 1;
        add(counterAndNoncePanel, constraints);
    }

    @Override
    public Symmetric getAlgorithm() {
        return algorithm;
    }

    @Override
    public void setAlgorithm(Symmetric algorithm) {
        this.algorithm = (ChaCha20) algorithm;
    }

    @Override
    public void display() {
        concrete.ivSizeInput.setVisible(false);
        concrete.ivInput.setVisible(false);
        concrete.setController(this);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        concrete.add(counterAndNoncePanel, constraints);

        constraints.gridy = 3;
        concrete.add(paramSpecInput, constraints);

        super.display();
    }

    @Override
    public void close() {
        concrete.setController(null);
        concrete.ivSizeInput.setVisible(true);
        concrete.ivInput.setVisible(true);
        concrete.remove(counterAndNoncePanel);
        concrete.remove(paramSpecInput);
    }

    @Override
    public void configure() {
        super.configure();
        algorithm.setParamSpec(Base64.getDecoder().decode(paramSpecInput.getValue().getBytes()));
        algorithm.setCounter(Integer.parseInt(counterInput.getValue()));
    }

    @Override
    public void displayWithAttributes() {
        super.displayWithAttributes();
        counterInput.setValue(algorithm.getCounter() + "");
        paramSpecInput.setValue(algorithm.getParamSpec());
        nonceInput.setValue(algorithm.getNonce() + "");
    }

    @Override
    public void generateKey() {
        if (counterInput.getValue().isEmpty()) {
            counterInput.setValue("0");
        }
        paramSpecInput.hideError();

        algorithm.setCounter(Integer.parseInt(counterInput.getValue()));
        algorithm.generateParamSpec();

        paramSpecInput.setValue(algorithm.getParamSpec());
    }

    @Override
    public boolean validateInput() {
        boolean check = super.validateInput();
        boolean validateKeySize = validateParamSpec();

        if (!check && validateKeySize)
            JOptionPane.showMessageDialog(getRootPane(), "Please enter all require values.",
                    "Error", JOptionPane.ERROR_MESSAGE);

        return check && validateKeySize;
    }

    private boolean validateParamSpec() {
        if (paramSpecInput.getValue().isEmpty()) {
            paramSpecInput.error();
            JOptionPane.showMessageDialog(getRootPane(), "Please enter all require values.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        paramSpecInput.hideError();

        return true;
    }

    @Override
    public void saveKey(DataOutputStream out) {
        if (!validateInput())
            return;

        int counter = Integer.parseInt(counterInput.getValue());
        int nonce = Integer.parseInt(nonceInput.getValue());
        String parameter = paramSpecInput.getValue();

        try {
            out.writeInt(counter);
            out.writeInt(nonce);
            out.writeUTF(parameter);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(getRootPane(), "Failed to save the key.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void loadKey(DataInputStream in) {
        try {
            int keySize = in.readInt();
            String key = in.readUTF();
            int counter = in.readInt();
            int nonce = in.readInt();
            String parameter = in.readUTF();

            if (!algorithm.validateKeySize(keySize)) {
                throw new IOException();
            }

            concrete.keySizeInput.setValue(keySize + "");
            concrete.keyInput.setValue(key);

            counterInput.setValue(counter + "");
            nonceInput.setValue(nonce + "");
            paramSpecInput.setValue(parameter);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(getRootPane(), "Failed to load the key.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public String encryptBase64(String plainText) {
        if (!validateInput()) {
            return null;
        }

        return super.encryptBase64(plainText);
    }

    @Override
    public String decryptBase64(String cipherText) {
        if (!validateInput()) {
            return null;
        }

        return super.decryptBase64(cipherText);
    }

    @Override
    public boolean encryptFile(String src, String des) {
        if (!validateInput()) {
            return false;
        }

        return super.encryptFile(src, des);
    }

    @Override
    public boolean decryptFile(String src, String des) {
        if (!validateInput()) {
            return false;
        }

        return super.decryptFile(src, des);
    }
}