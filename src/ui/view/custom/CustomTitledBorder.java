package ui.view.custom;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class CustomTitledBorder extends TitledBorder {

    private ImageIcon icon;
    private int iconX, iconY;

    public CustomTitledBorder(Border border, String title) {
        super(border, title);
    }

    public CustomTitledBorder(Border border, String title, int titleJustification, int titlePosition) {
        super(border, title, titleJustification, titlePosition);
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        super.paintBorder(c, g, x, y, width, height);

        if (icon != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            FontMetrics fm = g.getFontMetrics(getTitleFont() != null ? getTitleFont() : c.getFont());
            int titleWidth = fm.stringWidth(getTitle());
            int titleHeight = fm.getHeight();

            int titleX = x + 20;

            iconX = titleX + titleWidth + 5;
            iconY = y + (titleHeight - icon.getIconHeight()) / 2;

            g2d.drawImage(icon.getImage(), iconX, iconY, null);
        }
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public int getIconX() {
        return iconX;
    }

    public void setIconX(int iconX) {
        this.iconX = iconX;
    }

    public int getIconY() {
        return iconY;
    }

    public void setIconY(int iconY) {
        this.iconY = iconY;
    }
}