// File: GUI/room/RoomManagerPanel.java
package GUI.room;

import javax.swing.*;
import java.awt.*;

public class RoomManager extends JPanel {

    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color PANEL_BG = Color.WHITE;

    public RoomManager() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

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
    }
}