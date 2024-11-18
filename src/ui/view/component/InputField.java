package ui.view.component;

import ui.common.Dimensions;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.awt.*;

public class InputField extends JPanel {

    public JLabel label, info, error;
    public JTextField input;
    public GridBagConstraints gbc;

    public InputField(String labelContent) {

        setLayout(new GridBagLayout());

        gbc = new GridBagConstraints();
        gbc.insets = Dimensions.DEFAULT_INSETS;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        label = new JLabel(labelContent);
        label.setPreferredSize(new Dimension(Dimensions.LABEL_WIDTH, label.getPreferredSize().height));
        add(label, gbc);

        input = new JTextField();
        input.setPreferredSize(new Dimension(200, input.getPreferredSize().height));
        gbc.weightx = 1;
        gbc.gridx = 1;
        add(input, gbc);

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

    public void setInput(JTextField input, int weightX) {
        remove(this.input);
        input.setPreferredSize(new Dimension(140, input.getPreferredSize().height));
        this.input = input;
        gbc.weightx = weightX;
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = Dimensions.DEFAULT_INSETS;
        add(this.input, gbc);

        if (weightX == 0) {
            gbc.gridx = 2;
            gbc.weightx = 1;
            add(new JPanel(), gbc);
        }
    }

    public void info(String text) {
        info.setText(text);
        info.setVisible(true);
//        input.putClientProperty("JTextField.placeholderText", text);
    }

    public String getValue() {
        return input.getText();
    }

    public void setValue(String value) {
        input.setText(value);
    }

    public void setNumericOnly() {
        PlainDocument doc = (PlainDocument) input.getDocument();
        doc.setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("\\d*")) { // Chỉ chấp nhận số
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("\\d*")) { // Chỉ chấp nhận số
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        input.setEnabled(enabled);
    }
}
