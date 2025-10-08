package GUI.account;

import javax.swing.*;
import java.awt.*;

public class AccountManager extends JFrame {

    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color PANEL_BG = Color.WHITE;

    public AccountManager() {
        setTitle("Quản lý tài khoản và vai trò");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        getContentPane().setBackground(BACKGROUND_COLOR);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setBackground(PANEL_BG);
        tabbedPane.setForeground(PRIMARY_COLOR);

        Role rolePanel = new Role();
        tabbedPane.addTab("Quản lý vai trò", rolePanel);

        UserAccount userAccountPanel = new UserAccount();
        tabbedPane.addTab("Quản lý tài khoản", userAccountPanel);

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

        SwingUtilities.invokeLater(() -> new AccountManager());
    }
}