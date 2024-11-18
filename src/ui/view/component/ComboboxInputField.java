package ui.view.component;

import ui.common.Dimensions;

import javax.swing.*;
import java.awt.*;

public class ComboboxInputField extends JPanel {

    public JLabel label, info, error;
    public MaterialCombobox<String> input;
    public GridBagConstraints gbc;

    public ComboboxInputField(String labelContent) {

        setLayout(new GridBagLayout());

        gbc = new GridBagConstraints();
        gbc.insets = Dimensions.DEFAULT_INSETS;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        label = new JLabel(labelContent);
        label.setPreferredSize(new Dimension(Dimensions.LABEL_WIDTH, label.getPreferredSize().height));
        add(label, gbc);

        input = new MaterialCombobox<String>();
        gbc.gridx = 1;
        add(input, gbc);

        gbc.gridx = 2;
        gbc.weightx = 1.0;
        add(new JPanel(), gbc);

        info = new JLabel(" ");
        info.setFont(new Font(info.getFont().getFontName(), Font.ITALIC, 12));
        info.setForeground(Color.GRAY);
        info.setVisible(false);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.insets = Dimensions.HORIZONTAL_INSETS;
        add(info, gbc);
    }

    public void error() {
        input.putClientProperty("JComponent.outline", Color.RED);
    }

    public void hideError() {
        input.putClientProperty("JComponent.outline", null);
    }

    public void info(String text) {
        info.setText(text);
        info.setVisible(true);
    }

    public String getValue() {
        return input.getSelectedItem().toString();
    }

    public void setValue(String value) {
        input.setSelectedItem(value);
    }

    public void setItems(String[] items) {
        input.setItems(items);
    }
}
