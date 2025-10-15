package GUI.operation;

import GUI.operation.housekeeping.HousekeepingTask;
import GUI.operation.maintenance.MaintenanceTicket;

import javax.swing.*;
import java.awt.*;

public class OperationManager extends JFrame {

    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color PANEL_BG = Color.WHITE;

    public OperationManager() {
        setTitle("Quản lý vận hành");
        setSize(1200, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        getContentPane().setBackground(BACKGROUND_COLOR);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setBackground(PANEL_BG);
        tabbedPane.setForeground(PRIMARY_COLOR);

        HousekeepingTask housekeepingPanel = new HousekeepingTask();
        tabbedPane.addTab("Quản lý nhiệm vụ dọn dẹp", housekeepingPanel);

        MaintenanceTicket maintenancePanel = new MaintenanceTicket();
        tabbedPane.addTab("Quản lý phiếu bảo trì", maintenancePanel);

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

        SwingUtilities.invokeLater(() -> new OperationManager());
    }
}
