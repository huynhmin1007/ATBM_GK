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

public class ChaCha20Fragment extends SymmetricDecorator {

    private ChaCha20 algorithm;
    private EditText counterEdt, nonceEdt;
    private EditText paramSpecEdt;
    private JPanel paramSpecPanel;

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
        MaterialLabel parameterLabel = new MaterialLabel("Parameter");
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
    public void display() {
        concrete.ivPanel.setVisible(false);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 1;
        concrete.add(paramSpecPanel, constraints);
        super.display();
    }

    @Override
    public void close() {
        concrete.ivPanel.setVisible(true);
    }
}