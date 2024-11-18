package ui;

//import ui.view.activity.ASymmetricActivity;
import ui.view.activity.HashActivity;
import ui.view.activity.SymmetricActivity;
import ui.view.custom.Theme;

import javax.swing.*;
import java.awt.*;

public class App extends JFrame {

    public static final Integer SCREEN_WIDTH = 1080;
    public static final Integer SCREEN_HEIGHT = 720;

    private JTabbedPane tabbedPane;

    public App() {
        new Theme().setup();

        setTitle("Mã hóa");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));

        tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Mã hóa đối xứng", createTabWithPadding(SymmetricActivity.getInstance()));
//        tabbedPane.addTab("Mã hóa bất đối xứng", createTabWithPadding(ASymmetricActivity.getInstance()));
        tabbedPane.addTab("Hash", createTabWithPadding(HashActivity.getInstance()));

        add(tabbedPane);

        pack();
        setLocationRelativeTo(null);
    }

    private JPanel createTabWithPadding(JPanel activityPanel) {
        JPanel paddedPanel = new JPanel();
        paddedPanel.setLayout(new BorderLayout());

        paddedPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        paddedPanel.add(activityPanel, BorderLayout.CENTER);

        return paddedPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new App().setVisible(true);
        });
    }
}