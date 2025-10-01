package GUI;

import util.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RoomManager extends JFrame {

    public RoomManager() {
        setTitle("Quản lý phòng");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        loadRooms();

        setVisible(true);
    }

    private void loadRooms() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT room_id, room_no FROM Room");

            while (rs.next()) {
                int roomId = rs.getInt("room_id");
                String roomName = rs.getString("room_no");

                JButton btn = new JButton("Phòng " + roomName);
                btn.setPreferredSize(new Dimension(100, 50));

                // Gắn sự kiện khi bấm button
                btn.addActionListener(e -> {
                    JOptionPane.showMessageDialog(this,
                            "Bạn chọn phòng: " + roomName + " (ID: " + roomId + ")");
                });

                add(btn);
            }

            rs.close();
            st.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new RoomManager();
    }
}
