package util;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class ImagePanel extends JPanel {
    private Image image;

    public ImagePanel(String path) {
        // Load ảnh từ resources (ví dụ: "/images/login.png")
        URL imgURL = getClass().getResource(path);
        System.out.println("Đang load ảnh: " + path + " | URL = " + imgURL);
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);
            image = icon.getImage();
            // Đặt kích thước panel mặc định = kích thước ảnh
            setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
        } else {
            System.err.println("❌ Không tìm thấy ảnh: " + path);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            // Vẽ ảnh fill theo kích thước panel
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
