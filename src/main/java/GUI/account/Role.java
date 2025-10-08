package GUI.account;

import BUS.RoleBUS;
import DTO.RoleDTO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Role extends JPanel {
    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color PANEL_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(224, 224, 224);
    private static final Color TEXT_COLOR = new Color(33, 33, 33);

    private JScrollPane scrollPane;
    private JTextField searchField;
    private List<RoleDTO> roleData = new ArrayList<>();
    private List<RoleDTO> filteredRoleData = new ArrayList<>();

    private RoleBUS roleBUS;

    public Role() {
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

        JButton addButton = new JButton("Thêm vai trò");
        addButton.setPreferredSize(new Dimension(150, 35));
        addButton.setIcon(new ImageIcon(getClass().getResource("/images/add-button.png")));
        addButton.setBackground(PRIMARY_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setFont(new Font("Arial", Font.BOLD, 13));
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e -> showAddRoleDialog());
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
            roleData = roleBUS.getAllRoles();
            filteredRoleData = roleData.stream().collect(Collectors.toList());
            updateTableView();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu vai trò: " + e.getMessage());
        }
    }

    private void performSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            filteredRoleData = roleData.stream().collect(Collectors.toList());
        } else {
            filteredRoleData = roleData.stream()
                    .filter(role -> {
                        String name = role.getName() != null ? role.getName().toLowerCase() : "";
                        String description = role.getDescription() != null ? role.getDescription().toLowerCase() : "";
                        return name.contains(query) || description.contains(query);
                    })
                    .collect(Collectors.toList());
        }
        updateTableView();
    }

    private void showFilterDialog() {
        JDialog filterDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Lọc vai trò", true);
        filterDialog.setLayout(new BorderLayout());
        filterDialog.setSize(350, 180);

        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 15, 15));
        contentPanel.setBackground(PANEL_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("Tên vai trò:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JComboBox<String> nameCombo = new JComboBox<>(new String[]{"Tất cả", "Admin", "Manager", "Staff"}); // Có thể điều chỉnh dựa trên dữ liệu
        nameCombo.setPreferredSize(new Dimension(150, 30));
        contentPanel.add(nameLabel);
        contentPanel.add(nameCombo);

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
            String selectedName = (String) nameCombo.getSelectedItem();
            filterRoles(selectedName);
            filterDialog.dispose();
        });
        buttonPanel.add(confirmButton);

        filterDialog.add(contentPanel, BorderLayout.CENTER);
        filterDialog.add(buttonPanel, BorderLayout.SOUTH);
        filterDialog.setLocationRelativeTo(this);
        filterDialog.setVisible(true);
    }

    private void filterRoles(String name) {
        filteredRoleData = roleData.stream()
                .filter(role -> {
                    String roleName = role.getName();
                    return name.equals("Tất cả") || (roleName != null && roleName.equals(name));
                })
                .collect(Collectors.toList());
        updateTableView();
    }

    private void showAddRoleDialog() {
        JDialog addDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm vai trò", true);
        addDialog.setLayout(new BorderLayout());
        addDialog.setSize(400, 250);

        JPanel contentPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        contentPanel.setBackground(PANEL_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("Tên vai trò:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField nameField = new JTextField();

        JLabel descriptionLabel = new JLabel("Mô tả:");
        descriptionLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField descriptionField = new JTextField();

        contentPanel.add(nameLabel);
        contentPanel.add(nameField);
        contentPanel.add(descriptionLabel);
        contentPanel.add(descriptionField);

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
                String name = nameField.getText().trim();
                String description = descriptionField.getText().trim();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(addDialog, "Vui lòng điền tên vai trò!");
                    return;
                }

                RoleDTO newRole = new RoleDTO(0, name, description.isEmpty() ? null : description);
                boolean success = roleBUS.addRole(newRole);

                if (success) {
                    JOptionPane.showMessageDialog(addDialog, "Thêm vai trò thành công!");
                    loadData();
                    addDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(addDialog, "Không thể thêm vai trò!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(addDialog, "Lỗi khi thêm vai trò: " + ex.getMessage());
            }
        });
        buttonPanel.add(confirmButton);

        addDialog.add(contentPanel, BorderLayout.CENTER);
        addDialog.add(buttonPanel, BorderLayout.SOUTH);
        addDialog.setLocationRelativeTo(this);
        addDialog.setVisible(true);
    }

    private void showEditRoleDialog(RoleDTO role) {
        JDialog editDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa vai trò", true);
        editDialog.setLayout(new BorderLayout());
        editDialog.setSize(400, 250);

        JPanel contentPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        contentPanel.setBackground(PANEL_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("Tên vai trò:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField nameField = new JTextField(role.getName());

        JLabel descriptionLabel = new JLabel("Mô tả:");
        descriptionLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField descriptionField = new JTextField(role.getDescription() != null ? role.getDescription() : "");

        contentPanel.add(nameLabel);
        contentPanel.add(nameField);
        contentPanel.add(descriptionLabel);
        contentPanel.add(descriptionField);

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
                String newName = nameField.getText().trim();
                String newDescription = descriptionField.getText().trim();

                if (newName.isEmpty()) {
                    JOptionPane.showMessageDialog(editDialog, "Vui lòng điền tên vai trò!");
                    return;
                }

                RoleDTO updatedRole = new RoleDTO(role.getRoleId(), newName, newDescription.isEmpty() ? null : newDescription);
                boolean success = roleBUS.updateRole(updatedRole);

                if (success) {
                    JOptionPane.showMessageDialog(editDialog, "Sửa vai trò thành công!");
                    loadData();
                    editDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(editDialog, "Không thể sửa vai trò!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(editDialog, "Lỗi khi sửa vai trò: " + ex.getMessage());
            }
        });
        buttonPanel.add(confirmButton);

        editDialog.add(contentPanel, BorderLayout.CENTER);
        editDialog.add(buttonPanel, BorderLayout.SOUTH);
        editDialog.setLocationRelativeTo(this);
        editDialog.setVisible(true);
    }

    private void deleteRole(int roleId) {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa vai trò ID: " + roleId + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = roleBUS.deleteRole(roleId);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Xóa vai trò thành công!");
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa vai trò!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa vai trò: " + ex.getMessage());
            }
        }
    }

    private void updateTableView() {
        String[] columnNames = {"ID", "Tên", "Mô tả"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (RoleDTO role : filteredRoleData) {
            Object[] row = {
                    role.getRoleId(),
                    role.getName(),
                    role.getDescription()
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
                    RoleDTO role = filteredRoleData.get(modelRow);

                    editItem.addActionListener(e1 -> showEditRoleDialog(role));
                    deleteItem.addActionListener(e1 -> deleteRole(role.getRoleId()));

                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        scrollPane.setViewportView(table);
    }
}