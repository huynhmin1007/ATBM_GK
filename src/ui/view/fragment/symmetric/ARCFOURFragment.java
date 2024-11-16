package ui.view.fragment.symmetric;

import encryption.common.Algorithm;
import encryption.symmetric.ARCFOUR;
import encryption.symmetric.Symmetric;
import encryption.symmetric.SymmetricFactory;
import ui.common.Dimensions;
import ui.view.component.EditText;
import ui.view.component.MaterialLabel;

import javax.swing.*;
import java.awt.*;

public class ARCFOURFragment extends SymmetricDecorator {

    private ARCFOUR algorithm;
    private EditText keySizeEdt;
    private JPanel keySizePanel;
    private MaterialLabel keySizeLabel;

    public ARCFOURFragment(SymmetricConcrete symmetricConcrete) {
        super(symmetricConcrete);
        algorithm = (ARCFOUR) SymmetricFactory.getSymmetric(Algorithm.ARCFOUR);

        keySizeEdt = new EditText();
        keySizeEdt.setInfo("Key size must range between 40 and 1024");
        keySizeEdt.setNumericOnly();

        keySizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, Dimensions.MARGIN_HORIZONTAL, Dimensions.MARGIN_VERTICAL));
        keySizeLabel = new MaterialLabel("Key Size:");
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
        concrete.ivPanel.setVisible(false);

        super.display();
    }

    @Override
    public void close() {
        concrete.setController(null);
        concrete.keyPanel.remove(keySizePanel);
        concrete.keySizePanel.setVisible(true);
        concrete.ivPanel.setVisible(true);
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

        if (keySize < 40 || keySize > 1024) {
            keySizeEdt.infoLabel.setForeground(Color.RED);
            return false;
        } else {
            keySizeEdt.infoLabel.setForeground(Color.GRAY);
        }

        return true;
    }
}