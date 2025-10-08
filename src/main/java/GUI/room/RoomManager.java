package GUI.room;

import javax.swing.*;
import java.awt.*;

public class RoomManager extends JFrame {

    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color PANEL_BG = Color.WHITE;

    public RoomManager() {
        setTitle("Quản lý phòng");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        getContentPane().setBackground(BACKGROUND_COLOR);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setBackground(PANEL_BG);
        tabbedPane.setForeground(PRIMARY_COLOR);

        Room roomPanel = new Room();
        tabbedPane.addTab("Quản lý phòng", roomPanel);

        RoomType roomType = new RoomType();
        tabbedPane.addTab("Quản lý loại phòng", roomType);

        Amenity amenity = new Amenity();
        tabbedPane.addTab("Quản lý tiện nghi", amenity);

        add(tabbedPane, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new RoomManager());
    }
}
