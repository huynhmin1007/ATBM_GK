package ui.view.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SpinningLoading extends JPanel implements ActionListener {
    private final Timer timer;
    private int angle = 0;
    private int circleSize = 30; // Kích thước mặc định của vòng tròn
    private boolean isRunning = false; // Biến để theo dõi trạng thái hoạt động

    public SpinningLoading() {
        // Thiết lập Timer để xoay hình liên tục với 60 FPS (16.67ms mỗi bước)
        timer = new Timer(1000 / 120, this); // 16.67ms mỗi bước (60 FPS)
    }

    public void start() {
        if (!isRunning) {  // Kiểm tra nếu chưa bắt đầu thì mới bắt đầu
            timer.start();
            isRunning = true;
        }
    }

    public void stop() {
        if (isRunning) {  // Kiểm tra nếu đang chạy thì mới dừng
            timer.stop();
            isRunning = false;
        }
    }

    // Setter để thay đổi kích thước vòng tròn
    public void setCircleSize(int size) {
        this.circleSize = size;
        repaint(); // Cập nhật giao diện sau khi thay đổi kích thước
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Khử răng cưa
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Tính toán tâm và kích thước vòng tròn
        int width = getWidth();
        int height = getHeight();
        int x = (width - circleSize) / 2;
        int y = (height - circleSize) / 2;

        // Vẽ vòng tròn xoay
        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(3)); // Độ dày nét
        g2d.drawArc(x, y, circleSize, circleSize, angle, 270); // Vòng cung 270 độ

        // Xoay góc
        angle += 5;
        angle %= 360;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint(); // Cập nhật giao diện liên tục
    }
}
