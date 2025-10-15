package GUI.auth;

import BUS.AuthBUS;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ForgotPasswordGUI extends JDialog {
    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color PANEL_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(224, 224, 224);

    private AuthBUS authBUS;
    private JTextField tfEmail;
    private JPasswordField pfNewPassword;
    private JPasswordField pfConfirmPassword;
    private JButton btnVerifyEmail;
    private JButton btnResetPassword;
    private JButton btnCancel;
    private JPanel passwordPanel;
    private boolean emailVerified = false;

    public ForgotPasswordGUI(Frame parent) {
        super(parent, "Quên mật khẩu", true);
        authBUS = new AuthBUS();
        initComponents();
        setSize(500, 450);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Header Panel with gradient
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(0x1c92d2);
                Color color2 = new Color(0xf2fcfe);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        headerPanel.setPreferredSize(new Dimension(500, 80));
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));

        JLabel headerLabel = new JLabel("Khôi phục mật khẩu");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(new Color(0xFFFF00));
        headerLabel.setIcon(new ImageIcon(getClass().getResource("/images/key.png")));
        headerPanel.add(headerLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setBackground(PANEL_BG);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(30, 40, 30, 40),
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true)
        ));
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Email field
        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblEmail.setIcon(new ImageIcon(getClass().getResource("/images/email.png")));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(lblEmail, gbc);

        tfEmail = new JTextField();
        tfEmail.setPreferredSize(new Dimension(280, 40));
        tfEmail.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tfEmail.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(tfEmail, gbc);

        // Verify button
        btnVerifyEmail = new JButton("Xác thực Email");
        btnVerifyEmail.setPreferredSize(new Dimension(280, 40));
        btnVerifyEmail.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnVerifyEmail.setBackground(PRIMARY_COLOR);
        btnVerifyEmail.setForeground(Color.WHITE);
        btnVerifyEmail.setBorder(BorderFactory.createEmptyBorder());
        btnVerifyEmail.setFocusPainted(false);
        btnVerifyEmail.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVerifyEmail.addActionListener(e -> verifyEmail());
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 10, 15, 10);
        formPanel.add(btnVerifyEmail, gbc);

        // Password panel (initially hidden)
        passwordPanel = new JPanel(new GridBagLayout());
        passwordPanel.setBackground(PANEL_BG);
        passwordPanel.setVisible(false);

        GridBagConstraints gbcPass = new GridBagConstraints();
        gbcPass.insets = new Insets(10, 10, 10, 10);
        gbcPass.fill = GridBagConstraints.HORIZONTAL;

        // New password field
        JLabel lblNewPassword = new JLabel("Mật khẩu mới:");
        lblNewPassword.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNewPassword.setIcon(new ImageIcon(getClass().getResource("/images/locked-computer.png")));
        gbcPass.gridx = 0;
        gbcPass.gridy = 0;
        gbcPass.anchor = GridBagConstraints.WEST;
        passwordPanel.add(lblNewPassword, gbcPass);

        pfNewPassword = new JPasswordField();
        pfNewPassword.setPreferredSize(new Dimension(280, 40));
        pfNewPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pfNewPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        gbcPass.gridx = 1;
        gbcPass.gridy = 0;
        passwordPanel.add(pfNewPassword, gbcPass);

        // Confirm password field
        JLabel lblConfirmPassword = new JLabel("Xác nhận:");
        lblConfirmPassword.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblConfirmPassword.setIcon(new ImageIcon(getClass().getResource("/images/locked-computer.png")));
        gbcPass.gridx = 0;
        gbcPass.gridy = 1;
        passwordPanel.add(lblConfirmPassword, gbcPass);

        pfConfirmPassword = new JPasswordField();
        pfConfirmPassword.setPreferredSize(new Dimension(280, 40));
        pfConfirmPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pfConfirmPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        gbcPass.gridx = 1;
        gbcPass.gridy = 1;
        passwordPanel.add(pfConfirmPassword, gbcPass);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 10, 10);
        formPanel.add(passwordPanel, gbc);

        // Center panel wrapper
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        centerPanel.setOpaque(false);
        centerPanel.add(formPanel);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(PANEL_BG);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));

        btnResetPassword = new JButton("Đặt lại mật khẩu");
        btnResetPassword.setPreferredSize(new Dimension(160, 40));
        btnResetPassword.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnResetPassword.setBackground(SUCCESS_COLOR);
        btnResetPassword.setForeground(Color.WHITE);
        btnResetPassword.setBorder(BorderFactory.createEmptyBorder());
        btnResetPassword.setFocusPainted(false);
        btnResetPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnResetPassword.setEnabled(false);
        btnResetPassword.addActionListener(e -> resetPassword());
        buttonPanel.add(btnResetPassword);

        btnCancel = new JButton("Hủy");
        btnCancel.setPreferredSize(new Dimension(100, 40));
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancel.setBackground(DANGER_COLOR);
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setBorder(BorderFactory.createEmptyBorder());
        btnCancel.setFocusPainted(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(e -> dispose());
        buttonPanel.add(btnCancel);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void verifyEmail() {
        String email = tfEmail.getText().trim();

        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập email!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate email format
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this,
                    "Email không hợp lệ!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String result = authBUS.verifyEmailExists(email);

        if (result.equals("Email hợp lệ")) {
            emailVerified = true;
            tfEmail.setEnabled(false);
            btnVerifyEmail.setEnabled(false);
            passwordPanel.setVisible(true);
            btnResetPassword.setEnabled(true);
            pack();
            JOptionPane.showMessageDialog(this,
                    "Email đã được xác thực! Vui lòng nhập mật khẩu mới.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    result,
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetPassword() {
        if (!emailVerified) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng xác thực email trước!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String newPassword = new String(pfNewPassword.getPassword());
        String confirmPassword = new String(pfConfirmPassword.getPassword());

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đầy đủ thông tin!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (newPassword.length() < 6) {
            JOptionPane.showMessageDialog(this,
                    "Mật khẩu phải có ít nhất 6 ký tự!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "Mật khẩu xác nhận không khớp!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        String email = tfEmail.getText().trim();
        String result = authBUS.resetPassword(email, newPassword);

        if (result.equals("Đặt lại mật khẩu thành công!")) {
            JOptionPane.showMessageDialog(this,
                    result,
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    result,
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
