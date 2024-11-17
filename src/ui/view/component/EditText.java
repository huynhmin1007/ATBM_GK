package ui.view.component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.awt.*;

public class EditText extends JPanel {

    public JTextField textField;
    public JLabel errorLabel;
    public JLabel infoLabel;

    private boolean showError;

    public EditText() {
        textField = new JTextField();
        errorLabel = new JLabel();
        infoLabel = new JLabel();

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        add(textField, gbc);

        gbc.gridy = 1;
        infoLabel.setFont(new Font(infoLabel.getFont().getFontName(), Font.ITALIC, 12));
        infoLabel.setForeground(Color.GRAY);
        infoLabel.setVisible(false);
        add(infoLabel, gbc);

        gbc.gridy = 2;
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font(errorLabel.getFont().getFontName(), Font.PLAIN, 14));
        errorLabel.setVisible(false);
        add(errorLabel, gbc);
    }

    public void setText(String text) {
        textField.setText(text);
    }

    public String getText() {
        return textField.getText();
    }

    public void error(String error) {
        if (!showError) {
            errorLabel.setText(error);
            errorLabel.setVisible(true);
            setPreferredSize(new Dimension(getPreferredSize().width, getPreferredSize().height + errorLabel.getPreferredSize().height));
            refreshUI();
            showError = true;
        }
    }

    private void refreshUI() {
        revalidate();
        repaint();
    }

    public void hideError() {
        if (showError) {
            showError = false;
            errorLabel.setVisible(false);
            refreshUI();
        }
    }

    public void setInfo(String info) {
        infoLabel.setText(info);
        infoLabel.setVisible(true);

        refreshUI();
    }

    public void hideInfo() {
        infoLabel.setVisible(false);

        refreshUI();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textField.setEnabled(enabled);
        if (!enabled) {
        } else {
            textField.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }

    public void setNumericOnly() {
        PlainDocument doc = (PlainDocument) textField.getDocument();
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

    public void setMargin(Insets insets) {
        setBorder(BorderFactory.createCompoundBorder(
                getBorder(),
                new EmptyBorder(insets)
        ));

        Dimension preferredSize = getPreferredSize();
        setPreferredSize(new Dimension(
                preferredSize.width + insets.left + insets.right,
                preferredSize.height + insets.top + insets.bottom
        ));

        textField.setBorder(BorderFactory.createCompoundBorder(
                textField.getBorder(),
                new EmptyBorder(0, insets.left, 0, insets.right)
        ));
    }
}