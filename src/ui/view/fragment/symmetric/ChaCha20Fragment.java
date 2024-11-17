package ui.view.fragment.symmetric;

import encryption.common.Algorithm;
import encryption.symmetric.ChaCha20;
import encryption.symmetric.Symmetric;
import encryption.symmetric.SymmetricFactory;
import ui.common.Dimensions;
import ui.view.component.EditText;
import ui.view.component.MaterialLabel;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ChaCha20Fragment extends SymmetricDecorator {

    private ChaCha20 algorithm;
    private EditText counterEdt, nonceEdt;
    private EditText paramSpecEdt;
    private JPanel paramSpecPanel;
    private MaterialLabel parameterLabel;

    public ChaCha20Fragment(SymmetricConcrete symmetricConcrete) {
        super(symmetricConcrete);

        algorithm = (ChaCha20) SymmetricFactory.getSymmetric(Algorithm.ChaCha20);

        counterEdt = new EditText();
        counterEdt.setInfo("Counter must be an Integer number");
        counterEdt.setNumericOnly();

        nonceEdt = new EditText();
        nonceEdt.setText("12");
        nonceEdt.setEnabled(false);
        nonceEdt.setPreferredSize(new Dimension(140, nonceEdt.getPreferredSize().height));

        paramSpecEdt = new EditText();

        paramSpecPanel = new JPanel(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = Dimensions.DEFAULT_INSETS;

        MaterialLabel counterLabel = new MaterialLabel("Counter:");
        counterLabel.setNotify("");
        paramSpecPanel.add(counterLabel, constraints);

        constraints.gridx = 1;
        counterEdt.setText("0");
        paramSpecPanel.add(counterEdt, constraints);

        MaterialLabel nonceLabel = new MaterialLabel("Nonce:");
        nonceLabel.setNotify("", new Insets(15, 0, 10, 0));
        nonceLabel.info.setHorizontalAlignment(SwingConstants.RIGHT);
        constraints.gridx = 2;
        paramSpecPanel.add(nonceLabel, constraints);

        constraints.gridx = 3;
        nonceEdt.setMargin(new Insets(0, 0, 20, 0));
        paramSpecPanel.add(nonceEdt, constraints);

        constraints.gridx = 4;
        constraints.weightx = 1;
        paramSpecPanel.add(new JPanel(), constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.weightx = 0;
        parameterLabel = new MaterialLabel("Parameter");
        paramSpecPanel.add(parameterLabel, constraints);

        constraints.gridx = 1;
        constraints.weightx = 1;
        constraints.gridwidth = 4;
        paramSpecPanel.add(paramSpecEdt, constraints);
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
        concrete.ivPanel.setVisible(false);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 1;
        concrete.add(paramSpecPanel, constraints);
        concrete.setController(this);
        super.display();
    }

    @Override
    public void configure() {
        super.configure();
        algorithm.setParamSpec(Base64.getDecoder().decode(paramSpecEdt.getText().getBytes()));
        algorithm.setCounter(Integer.parseInt(counterEdt.getText()));
    }

    @Override
    public void displayWithAttributes() {
        super.displayWithAttributes();
        counterEdt.setText(algorithm.getCounter() + "");
        paramSpecEdt.setText(algorithm.getParamSpec());
        nonceEdt.setText(algorithm.getNonce() + "");
    }

    @Override
    public void close() {
        concrete.ivPanel.setVisible(true);
        concrete.remove(paramSpecPanel);
        concrete.setController(null);
    }

    @Override
    public void generateKey() {
        if (counterEdt.getText().isEmpty()) {
            counterEdt.setText("0");
        }
        paramSpecEdt.hideError();
        parameterLabel.deleteNotify();

        algorithm.setCounter(Integer.parseInt(counterEdt.getText()));
        algorithm.generateParamSpec();

        paramSpecEdt.setText(algorithm.getParamSpec());
    }

    @Override
    public boolean validateInput() {
        validateParamSpec();
        return super.validateInput();
    }

    private boolean validateParamSpec() {
        if (paramSpecEdt.getText().isEmpty()) {
            paramSpecEdt.error("Vui lòng nhập Parameter");
            parameterLabel.setNotify("");
            return false;
        }

        paramSpecEdt.hideError();
        parameterLabel.deleteNotify();

        return true;
    }

    @Override
    public void saveKey(DataOutputStream out) {
        if (!validateInput())
            return;

        int counter = Integer.parseInt(counterEdt.getText());
        int nonce = Integer.parseInt(nonceEdt.getText());
        String parameter = paramSpecEdt.getText();

        try {
            out.writeInt(counter);
            out.writeInt(nonce);
            out.writeUTF(parameter);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(getRootPane(), "Không thể lưu tệp. Vui lòng thử lại.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(getRootPane(), "Tệp không hợp lệ. Vui lòng thử lại.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            concrete.keySizeCbb.setSelectedItem(keySize);
            concrete.keyEdt.setText(key);

            counterEdt.setText(counter + "");
            nonceEdt.setText(nonce + "");
            paramSpecEdt.setText(parameter);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(getRootPane(), "Không thể lưu tệp. Vui lòng thử lại.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}