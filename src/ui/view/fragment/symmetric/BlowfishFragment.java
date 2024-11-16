package ui.view.fragment.symmetric;

import encryption.common.Algorithm;
import encryption.symmetric.Blowfish;
import encryption.symmetric.Symmetric;
import encryption.symmetric.SymmetricFactory;
import ui.common.Dimensions;
import ui.view.component.EditText;
import ui.view.component.MaterialLabel;

import javax.swing.*;
import java.awt.*;

public class BlowfishFragment extends SymmetricDecorator {

    private Blowfish algorithm;
    private EditText keySizeEdt;
    private JPanel keySizePanel;
    private MaterialLabel keySizeLabel;

    public BlowfishFragment(SymmetricConcrete symmetricConcrete) {
        super(symmetricConcrete);
        algorithm = (Blowfish) SymmetricFactory.getSymmetric(Algorithm.Blowfish);

        keySizeEdt = new EditText();
        keySizeEdt.setInfo("Key size must be a multiple of 8, ranging from 32 to 448");
        keySizeEdt.setNumericOnly();

        keySizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, Dimensions.MARGIN_HORIZONTAL, Dimensions.MARGIN_VERTICAL));
        MaterialLabel keySizeLabel = new MaterialLabel("Key Size:");
        keySizeLabel.setNotify("");
        keySizeLabel.setPreferredSize(new Dimension(keySizeLabel.getPreferredSize().width + Dimensions.MARGIN_HORIZONTAL, keySizeLabel.getPreferredSize().height));

        keySizePanel.add(keySizeLabel);
        keySizePanel.add(keySizeEdt);
    }

    @Override
    public Symmetric getAlgorithm() {
        return algorithm;
    }

    @Override
    public void display() {
        concrete.keySizePanel.setVisible(false);
        concrete.setController(this);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.weightx = 1;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        concrete.keyPanel.add(keySizePanel, constraints);

        super.display();
    }

    @Override
    public void close() {
        concrete.setController(null);
        concrete.keyPanel.remove(keySizePanel);
        concrete.keySizePanel.setVisible(true);
    }

    @Override
    public int getKeySize() {
        if (!validateKeySize())
            return -1;

        return Integer.parseInt(keySizeEdt.getText());
    }

    @Override
    public boolean validateInput() {
        validateKeySize();
        return super.validateInput();
    }

    private boolean validateKeySize() {
        if (keySizeEdt.getText().isEmpty()) {
            keySizeEdt.error("Vui lòng nhập kích thước khóa");
            keySizeLabel.setNotify("", new Insets(20, 0, 20, 0));
            return false;
        }

        keySizeEdt.hideError();

        int keySize = Integer.parseInt(keySizeEdt.getText());

        if (keySize < 32 || keySize > 448 || keySize % 8 != 0) {
            keySizeEdt.infoLabel.setForeground(Color.RED);
            return false;
        } else {
            keySizeEdt.infoLabel.setForeground(Color.GRAY);
        }

        return true;
    }
}