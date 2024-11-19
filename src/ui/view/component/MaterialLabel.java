package ui.view.component;

import ui.common.Dimensions;

import javax.swing.*;
import java.awt.*;

public class MaterialLabel extends JLabel {

    public MaterialLabel() {
        setPreferredSize(new Dimension(Dimensions.LABEL_WIDTH, getPreferredSize().height));
    }

    public MaterialLabel(String text) {
        super(text);
        setPreferredSize(new Dimension(Dimensions.LABEL_WIDTH, getPreferredSize().height));
    }

    public MaterialLabel(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
        setPreferredSize(new Dimension(Dimensions.LABEL_WIDTH, getPreferredSize().height));
    }
}
