package ui.view.component;

import ui.common.Dimensions;

import javax.swing.*;
import java.awt.*;

public class MaterialLabel extends JPanel {

    public JLabel info;
    public JLabel notify;

    private GridBagConstraints gbc;

    private boolean showNotify;

    public MaterialLabel(String text) {
        info = new JLabel(text);
        info.setPreferredSize(new Dimension(Dimensions.LABEL_WIDTH, info.getPreferredSize().height));

        setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        add(info, gbc);

        notify = new JLabel();
    }

    public void setNotify(String text) {
        if (showNotify) {
            return;
        }

        showNotify = true;
        remove(notify);
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridy = 1;
        notify.setText(text);
        setPreferredSize(new Dimension(getPreferredSize().width, getPreferredSize().height + 20));
        add(notify, gbc);
    }

    public void setNotify(String text, Insets insets) {
        if (showNotify) {
            return;
        }

        showNotify = true;
        remove(notify);
        gbc.insets = insets;
        gbc.gridy = 1;
        notify.setText(text);
        setPreferredSize(new Dimension(getPreferredSize().width, getPreferredSize().height + insets.top + insets.bottom));
        add(notify, gbc);
    }

    public void deleteNotify() {
        if (showNotify) {
            showNotify = false;
            remove(notify);
        }
    }
}
