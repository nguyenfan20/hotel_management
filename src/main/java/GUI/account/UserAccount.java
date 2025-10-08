package GUI.account;

import BUS.UserAccountBUS;
import BUS.RoleBUS;
import DTO.UserAccountDTO;
import DTO.RoleDTO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class UserAccount extends JPanel {
    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color PANEL_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(224, 224, 224);
    private static final Color TEXT_COLOR = new Color(33, 33, 33);

    private JScrollPane scrollPane;
    private JTextField searchField;
    private List<UserAccountDTO> userAccountData = new ArrayList<>();
    private List<UserAccountDTO> filteredUserAccountData = new ArrayList<>();
    private UserAccountBUS userAccountBUS;
    private RoleBUS roleBUS;

    public UserAccount() {
        userAccountBUS = new UserAccountBUS();
        roleBUS = new RoleBUS();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        controlPanel.setBackground(PANEL_BG);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JButton addButton = new JButton("Thêm tài khoản");
        addButton.setPreferredSize(new Dimension(160, 35));
        addButton.setIcon(new ImageIcon(getClass().getResource("/images/add-button.png")));
        addButton.setBackground(PRIMARY_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setFont(new Font("Arial", Font.BOLD, 13));
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e -> showAddUserAccountDialog());
        controlPanel.add(addButton);

        controlPanel.add(Box.createHorizontalStrut(20));

        searchField = new JTextField(15);
        searchField.setPreferredSize(new Dimension(200, 35));
        searchField.setFont(new Font("Arial", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch();
                }
            }
        });
        controlPanel.add(searchField);

        JButton searchButton = createIconButton("/images/search.png");
        searchButton.addActionListener(e -> performSearch());
        controlPanel.add(searchButton);

        JButton filterButton = createIconButton("/images/filter.png");
        filterButton.addActionListener(e -> showFilterDialog());
        controlPanel.add(filterButton);

        add(controlPanel, BorderLayout.NORTH);

        scrollPane = new JScrollPane();
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        add(scrollPane, BorderLayout.CENTER);

        updateTableView();
    }

    private JButton createIconButton(String iconPath) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(35, 35));
        button.setIcon(new ImageIcon(getClass().getResource(iconPath)));
        button.setBackground(PANEL_BG);
        button.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void loadData() {
        try {
            userAccountData = userAccountBUS.getAllUserAccounts();
            filteredUserAccountData = new ArrayList<>(userAccountData);
            updateTableView();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu tài khoản: " + e.getMessage());
        }
    }

    private void performSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            filteredUserAccountData = new ArrayList<>(userAccountData);
        } else {
            filteredUserAccountData = userAccountData.stream()
                    .filter(user -> {
                        String username = user.getUsername() != null ? user.getUsername().toLowerCase() : "";
                        String fullName = user.getFullName() != null ? user.getFullName().toLowerCase() : "";
                        String phone = user.getPhone() != null ? user.getPhone().toLowerCase() : "";
                        String email = user.getEmail() != null ? user.getEmail().toLowerCase() : "";
                        String status = user.getStatus() != null ? user.getStatus().toLowerCase() : "";
                        return username.contains(query) || fullName.contains(query) || phone.contains(query) ||
                                email.contains(query) || status.contains(query);
                    })
                    .collect(Collectors.toList());
        }
        updateTableView();
    }

    private void showFilterDialog() {
        JDialog filterDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Lọc tài khoản", true);
        filterDialog.setLayout(new BorderLayout());
        filterDialog.setSize(350, 180);

        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 15, 15));
        contentPanel.setBackground(PANEL_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel statusLabel = new JLabel("Trạng thái:");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Tất cả", "Active", "Inactive", "Suspended"});
        statusCombo.setPreferredSize(new Dimension(150, 30));
        contentPanel.add(statusLabel);
        contentPanel.add(statusCombo);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(PANEL_BG);
        JButton confirmButton = new JButton("Xác nhận");
        confirmButton.setPreferredSize(new Dimension(100, 35));
        confirmButton.setBackground(PRIMARY_COLOR);
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        confirmButton.setBorderPainted(false);
        confirmButton.setFont(new Font("Arial", Font.BOLD, 13));
        confirmButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmButton.addActionListener(e -> {
            String selectedStatus = (String) statusCombo.getSelectedItem();
            filterUserAccounts(selectedStatus);
            filterDialog.dispose();
        });
        buttonPanel.add(confirmButton);

        filterDialog.add(contentPanel, BorderLayout.CENTER);
        filterDialog.add(buttonPanel, BorderLayout.SOUTH);
        filterDialog.setLocationRelativeTo(this);
        filterDialog.setVisible(true);
    }

    private void filterUserAccounts(String status) {
        filteredUserAccountData = userAccountData.stream()
                .filter(user -> {
                    String userStatus = user.getStatus();
                    return status.equals("Tất cả") || (userStatus != null && userStatus.equals(status));
                })
                .collect(Collectors.toList());
        updateTableView();
    }

    private void showAddUserAccountDialog() {
        JDialog addDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm tài khoản", true);
        addDialog.setLayout(new BorderLayout());
        addDialog.setSize(500, 450);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(PANEL_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel usernameLabel = new JLabel("Tên đăng nhập:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        contentPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField usernameField = new JTextField();
        contentPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 13));
        contentPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField();
        contentPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel fullNameLabel = new JLabel("Họ tên:");
        fullNameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        contentPanel.add(fullNameLabel, gbc);

        gbc.gridx = 1;
        JTextField fullNameField = new JTextField();
        contentPanel.add(fullNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel phoneLabel = new JLabel("Số điện thoại:");
        phoneLabel.setFont(new Font("Arial", Font.BOLD, 13));
        contentPanel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        JTextField phoneField = new JTextField();
        contentPanel.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 13));
        contentPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        JTextField emailField = new JTextField();
        contentPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel roleLabel = new JLabel("Vai trò:");
        roleLabel.setFont(new Font("Arial", Font.BOLD, 13));
        contentPanel.add(roleLabel, gbc);

        gbc.gridx = 1;
        List<RoleDTO> allRoles = roleBUS.getAllRoles();
        JComboBox<String> roleCombo = new JComboBox<>();
        for (RoleDTO role : allRoles) {
            roleCombo.addItem(role.getName());
        }
        contentPanel.add(roleCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel statusLabel = new JLabel("Trạng thái:");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));
        contentPanel.add(statusLabel, gbc);

        gbc.gridx = 1;
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Active", "Inactive", "Suspended"});
        contentPanel.add(statusCombo, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(PANEL_BG);
        JButton confirmButton = new JButton("Xác nhận");
        confirmButton.setPreferredSize(new Dimension(100, 35));
        confirmButton.setBackground(PRIMARY_COLOR);
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        confirmButton.setBorderPainted(false);
        confirmButton.setFont(new Font("Arial", Font.BOLD, 13));
        confirmButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmButton.addActionListener(e -> {
            try {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                String fullName = fullNameField.getText().trim();
                String phone = phoneField.getText().trim();
                String email = emailField.getText().trim();
                String selectedRoleName = (String) roleCombo.getSelectedItem();
                String status = (String) statusCombo.getSelectedItem();

                if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
                    JOptionPane.showMessageDialog(addDialog, "Vui lòng điền đầy đủ các trường bắt buộc!");
                    return;
                }

                int roleId = allRoles.stream()
                        .filter(role -> role.getName().equals(selectedRoleName))
                        .findFirst()
                        .map(RoleDTO::getRoleId)
                        .orElse(0);

                UserAccountDTO newUser = new UserAccountDTO();
                newUser.setUsername(username);
                newUser.setPasswordHash(password); // Giả sử hash ở BUS
                newUser.setFullName(fullName);
                newUser.setPhone(phone.isEmpty() ? null : phone);
                newUser.setEmail(email.isEmpty() ? null : email);
                newUser.setRoleId(roleId);
                newUser.setStatus(status);

                boolean success = userAccountBUS.addUserAccount(newUser);

                if (success) {
                    JOptionPane.showMessageDialog(addDialog, "Thêm tài khoản thành công!");
                    loadData();
                    addDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(addDialog, "Không thể thêm tài khoản!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(addDialog, "Lỗi khi thêm tài khoản: " + ex.getMessage());
            }
        });
        buttonPanel.add(confirmButton);

        addDialog.add(contentPanel, BorderLayout.CENTER);
        addDialog.add(buttonPanel, BorderLayout.SOUTH);
        addDialog.setLocationRelativeTo(this);
        addDialog.setVisible(true);
    }

    private void showEditUserAccountDialog(UserAccountDTO user) {
        JDialog editDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa tài khoản", true);
        editDialog.setLayout(new BorderLayout());
        editDialog.setSize(500, 450);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(PANEL_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel usernameLabel = new JLabel("Tên đăng nhập:");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        contentPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField usernameField = new JTextField(user.getUsername());
        contentPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel passwordLabel = new JLabel("Mật khẩu:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 13));
        contentPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(user.getPasswordHash());
        contentPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel fullNameLabel = new JLabel("Họ tên:");
        fullNameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        contentPanel.add(fullNameLabel, gbc);

        gbc.gridx = 1;
        JTextField fullNameField = new JTextField(user.getFullName());
        contentPanel.add(fullNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel phoneLabel = new JLabel("Số điện thoại:");
        phoneLabel.setFont(new Font("Arial", Font.BOLD, 13));
        contentPanel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        JTextField phoneField = new JTextField(user.getPhone());
        contentPanel.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 13));
        contentPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        JTextField emailField = new JTextField(user.getEmail());
        contentPanel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel roleLabel = new JLabel("Vai trò:");
        roleLabel.setFont(new Font("Arial", Font.BOLD, 13));
        contentPanel.add(roleLabel, gbc);

        gbc.gridx = 1;
        List<RoleDTO> allRoles = roleBUS.getAllRoles();
        JComboBox<String> roleCombo = new JComboBox<>();
        for (RoleDTO role : allRoles) {
            roleCombo.addItem(role.getName());
        }
        String currentRoleName = allRoles.stream()
                .filter(role -> role.getRoleId() == user.getRoleId())
                .findFirst()
                .map(RoleDTO::getName)
                .orElse("");
        roleCombo.setSelectedItem(currentRoleName);
        contentPanel.add(roleCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel statusLabel = new JLabel("Trạng thái:");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));
        contentPanel.add(statusLabel, gbc);

        gbc.gridx = 1;
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Active", "Inactive", "Suspended"});
        statusCombo.setSelectedItem(user.getStatus());
        contentPanel.add(statusCombo, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(PANEL_BG);
        JButton confirmButton = new JButton("Xác nhận");
        confirmButton.setPreferredSize(new Dimension(100, 35));
        confirmButton.setBackground(PRIMARY_COLOR);
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        confirmButton.setBorderPainted(false);
        confirmButton.setFont(new Font("Arial", Font.BOLD, 13));
        confirmButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmButton.addActionListener(e -> {
            try {
                String newUsername = usernameField.getText().trim();
                String newPassword = new String(passwordField.getPassword());
                String newFullName = fullNameField.getText().trim();
                String newPhone = phoneField.getText().trim();
                String newEmail = emailField.getText().trim();
                String selectedRoleName = (String) roleCombo.getSelectedItem();
                String newStatus = (String) statusCombo.getSelectedItem();

                if (newUsername.isEmpty() || newFullName.isEmpty()) {
                    JOptionPane.showMessageDialog(editDialog, "Vui lòng điền đầy đủ các trường bắt buộc!");
                    return;
                }

                int newRoleId = allRoles.stream()
                        .filter(role -> role.getName().equals(selectedRoleName))
                        .findFirst()
                        .map(RoleDTO::getRoleId)
                        .orElse(0);

                UserAccountDTO updatedUser = new UserAccountDTO();
                updatedUser.setUserId(user.getUserId());
                updatedUser.setUsername(newUsername);
                updatedUser.setPasswordHash(newPassword); // Giả sử hash ở BUS nếu thay đổi
                updatedUser.setFullName(newFullName);
                updatedUser.setPhone(newPhone.isEmpty() ? null : newPhone);
                updatedUser.setEmail(newEmail.isEmpty() ? null : newEmail);
                updatedUser.setRoleId(newRoleId);
                updatedUser.setStatus(newStatus);
                updatedUser.setCreatedAt(user.getCreatedAt()); // Giữ nguyên

                boolean success = userAccountBUS.updateUserAccount(updatedUser);

                if (success) {
                    JOptionPane.showMessageDialog(editDialog, "Sửa tài khoản thành công!");
                    loadData();
                    editDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(editDialog, "Không thể sửa tài khoản!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(editDialog, "Lỗi khi sửa tài khoản: " + ex.getMessage());
            }
        });
        buttonPanel.add(confirmButton);

        editDialog.add(contentPanel, BorderLayout.CENTER);
        editDialog.add(buttonPanel, BorderLayout.SOUTH);
        editDialog.setLocationRelativeTo(this);
        editDialog.setVisible(true);
    }

    private void deleteUserAccount(int userId) {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa tài khoản ID: " + userId + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = userAccountBUS.deleteUserAccount(userId);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Xóa tài khoản thành công!");
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa tài khoản!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa tài khoản: " + ex.getMessage());
            }
        }
    }

    private void updateTableView() {
        String[] columnNames = {"ID", "Tên đăng nhập", "Họ tên", "SĐT", "Email", "Vai trò", "Trạng thái", "Ngày tạo"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        List<RoleDTO> allRoles = roleBUS.getAllRoles();

        for (UserAccountDTO user : filteredUserAccountData) {
            String roleName = allRoles.stream()
                    .filter(role -> role.getRoleId() == user.getRoleId())
                    .findFirst()
                    .map(RoleDTO::getName)
                    .orElse("Unknown");

            Object[] row = {
                    user.getUserId(),
                    user.getUsername(),
                    user.getFullName(),
                    user.getPhone(),
                    user.getEmail(),
                    roleName,
                    user.getStatus(),
                    user.getCreatedAt()
            };
            model.addRow(row);
        }

        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(TEXT_COLOR);
        table.setShowVerticalLines(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(PANEL_BG);
        table.getTableHeader().setForeground(TEXT_COLOR);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR));
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                }
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return c;
            }
        });

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Sửa");
        JMenuItem deleteItem = new JMenuItem("Xóa");
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger() && table.getSelectedRow() != -1) {
                    int rowIndex = table.getSelectedRow();
                    int modelRow = table.convertRowIndexToModel(rowIndex);
                    UserAccountDTO user = filteredUserAccountData.get(modelRow);

                    editItem.addActionListener(e1 -> showEditUserAccountDialog(user));
                    deleteItem.addActionListener(e1 -> deleteUserAccount(user.getUserId()));

                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        scrollPane.setViewportView(table);
    }
}