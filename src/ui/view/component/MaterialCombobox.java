package ui.view.component;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Vector;

public class MaterialCombobox<T> extends JComboBox<T> {

    public MaterialCombobox(ComboBoxModel<T> aModel) {
        super(aModel);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setMaximumRowCount(5);
        setPreferredSize(new Dimension(140, getPreferredSize().height));
    }

    public MaterialCombobox(T[] items) {
        super(items);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setMaximumRowCount(5);
        setPreferredSize(new Dimension(140, getPreferredSize().height));
    }

    public MaterialCombobox(Vector<T> items) {
        super(items);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setMaximumRowCount(5);
        setPreferredSize(new Dimension(140, getPreferredSize().height));
    }

    public MaterialCombobox() {
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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

    public void setItems(T[] items) {
        removeAllItems();
        Arrays.stream(items).forEach(this::addItem);
    }
}
