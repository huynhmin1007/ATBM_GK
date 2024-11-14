package ui.view.component;

import javax.swing.*;
import java.awt.*;

public class EditText extends JPanel {

    private JTextField textField;
    private JLabel errorLabel;

    public EditText() {
        textField = new JTextField();
        errorLabel = new JLabel();

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        add(textField, gbc);
        gbc.gridy = 1;
        add(errorLabel, gbc);

        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font(errorLabel.getFont().getFontName(), Font.PLAIN, 14));
        errorLabel.setVisible(false);
    }

    public void setText(String text) {
        textField.setText(text);
    }

    public String getText() {
        return textField.getText();
    }

    public void error(String error) {
        errorLabel.setText(error);
        errorLabel.setVisible(true);

        revalidate();
        repaint();
    }

    public void hideError() {
        errorLabel.setVisible(false);

        revalidate();
        repaint();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textField.setEnabled(enabled);
    }
}