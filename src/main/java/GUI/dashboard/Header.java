package GUI.dashboard;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Header extends javax.swing.JPanel {

    private String username;
    private ActionListener logoutListener;
    private Timer clockTimer;

    public Header() {
        initComponents();
        setOpaque(false);
        setupUserProfile();
        startClock();
    }

    private void setupUserProfile() {
        // Set opaque cho panel
        pnlUserProfile.setOpaque(true);
    }

    private void startClock() {
        clockTimer = new Timer(1000, e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            lblDateTime.setText(sdf.format(new Date()));
        });
        clockTimer.start();
    }

    public void setUsername(String username) {
        this.username = username;
        lblUsername.setText(username);
    }

    public void setAvatar(String imagePath) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
            // Scale ảnh về 40x40
            Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            lblAvatar.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            System.out.println("Không tìm thấy avatar: " + imagePath);
        }
    }

    public void setAvatarFromFile(String filePath) {
        try {
            ImageIcon icon = new ImageIcon(filePath);
            Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            lblAvatar.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            System.out.println("Không tìm thấy avatar: " + filePath);
        }
    }

    public String getUsername() {
        return username;
    }

    public void setLogoutListener(ActionListener listener) {
        this.logoutListener = listener;
        btnLogout.addActionListener(listener);
    }

    public void setLogoutIcon(String iconPath) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            btnLogout.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            System.out.println("Không tìm thấy icon đăng xuất: " + iconPath);
        }
    }

    public void setLogoutIconFromFile(String filePath) {
        try {
            ImageIcon icon = new ImageIcon(filePath);
            Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            btnLogout.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            System.out.println("Không tìm thấy icon đăng xuất: " + filePath);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlUserProfile = new javax.swing.JPanel();
        lblAvatar = new javax.swing.JLabel();
        lblUsername = new javax.swing.JLabel();
        btnLogout = new javax.swing.JButton();
        lblDateTime = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        pnlUserProfile.setBackground(new java.awt.Color(255, 255, 255));
        pnlUserProfile.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 10, 4, 10));

        lblAvatar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAvatar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/user.png"))); // NOI18N
        lblAvatar.setPreferredSize(new java.awt.Dimension(40, 40));

        lblUsername.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblUsername.setText("Username");

        btnLogout.setFont(new java.awt.Font("Segoe UI", 0, 12));
        btnLogout.setIcon(new ImageIcon(getClass().getResource("/icon/logout.png")));
        btnLogout.setForeground(new java.awt.Color(255, 255, 255));
        btnLogout.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLogout.setPreferredSize(new java.awt.Dimension(35, 35));

        lblDateTime.setFont(new java.awt.Font("Segoe UI", 0, 14));
        lblDateTime.setForeground(new java.awt.Color(100, 100, 100));
        lblDateTime.setText("dd/MM/yyyy HH:mm:ss");
        lblDateTime.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout pnlUserProfileLayout = new javax.swing.GroupLayout(pnlUserProfile);
        pnlUserProfile.setLayout(pnlUserProfileLayout);
        pnlUserProfileLayout.setHorizontalGroup(
                pnlUserProfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnlUserProfileLayout.createSequentialGroup()
                                .addComponent(lblAvatar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblUsername)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(10, Short.MAX_VALUE))
        );
        pnlUserProfileLayout.setVerticalGroup(
                pnlUserProfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlUserProfileLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pnlUserProfileLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                        .addComponent(lblAvatar, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                        .addComponent(lblUsername, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(lblDateTime, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(pnlUserProfile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(pnlUserProfile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblDateTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    @Override
    protected void paintComponent(Graphics grphcs) {
        Graphics2D g2 = (Graphics2D) grphcs;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        g2.fillRect(0, 0, 25, getHeight());
        g2.fillRect(getWidth() - 25, getHeight() - 25, getWidth(), getHeight());
        super.paintComponent(grphcs);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblAvatar;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JButton btnLogout;
    private javax.swing.JPanel pnlUserProfile;
    private javax.swing.JLabel lblDateTime;
    // End of variables declaration//GEN-END:variables
}
