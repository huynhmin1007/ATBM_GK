package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Test extends JPanel implements ActionListener {
    private final Timer timer;
    private int angle = 0;

    public Test() {
        // Thiết lập Timer để xoay hình liên tục
        timer = new Timer(30, this); // 30ms mỗi bước
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Khử răng cưa
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Vẽ nền (tùy chọn)
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Tính toán tâm và kích thước vòng tròn
        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width, height) / 4; // Kích thước vòng tròn
        int x = (width - size) / 2;
        int y = (height - size) / 2;

        // Vẽ vòng tròn xoay
        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(5));
        g2d.drawArc(x, y, size, size, angle, 270); // Vòng cung 270 độ

        // Xoay góc
        angle += 5;
        angle %= 360;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint(); // Cập nhật giao diện
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Spinning Loading Example");
            Test loadingPanel = new Test();

            frame.add(loadingPanel);
            frame.setSize(400, 400);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
