package ui.view.custom;

import ui.App;

import javax.swing.*;
import java.awt.*;

public class BaseActivity extends JPanel {

    public BaseActivity() {
        setPreferredSize(new Dimension(App.SCREEN_WIDTH, App.SCREEN_HEIGHT));
    }
}
