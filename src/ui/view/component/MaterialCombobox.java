package ui.view.component;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class MaterialCombobox<T> extends JComboBox<T> {

    public MaterialCombobox(ComboBoxModel<T> aModel) {
        super(aModel);
        setMaximumRowCount(5);
        setPreferredSize(new Dimension(140, getPreferredSize().height));
    }

    public MaterialCombobox(T[] items) {
        super(items);
        setMaximumRowCount(5);
        setPreferredSize(new Dimension(140, getPreferredSize().height));
    }

    public MaterialCombobox(Vector<T> items) {
        super(items);
        setMaximumRowCount(5);
        setPreferredSize(new Dimension(140, getPreferredSize().height));
    }

    public MaterialCombobox() {
        setMaximumRowCount(5);
        setPreferredSize(new Dimension(140, getPreferredSize().height));
    }

    public boolean containsItem(Object anItem) {
        for (int i = 0; i < getItemCount(); i++) {
            if (getItemAt(i).equals(anItem))
                return true;
        }
        return false;
    }
}
