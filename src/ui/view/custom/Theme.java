package ui.view.custom;

import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;
import java.awt.*;

public class Theme {

    public void setup() {
        FlatIntelliJLaf.setup();

        UIManager.put("Button.arc", 10);
        UIManager.put("Button.margin", new Insets(10, 20, 10, 20));

        UIManager.put("Component.arc", 10);

        UIManager.put("ProgressBar.arc", 10);

        UIManager.put("TextComponent.arc", 10);

        UIManager.put("TextField.margin", new Insets(8, 8, 8, 8));

        UIManager.put("ComboBox.padding", new Insets(8, 8, 8, 8));

        UIManager.put("TabbedPane.selectedBackground", Color.white);
    }
}
