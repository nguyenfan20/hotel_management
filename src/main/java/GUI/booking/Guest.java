package GUI.booking;

import BUS.GuestBUS;
import DAO.GuestDAO;
import DTO.GuestDTO;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

// Giao diện quản lý khách hàng
public class Guest extends javax.swing.JFrame {
    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color PANEL_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(224, 224, 224);
    private static final Color TEXT_COLOR = new Color(33, 33, 33);
    private static final Color DANGER_COLOR = new Color(239, 68, 68);

    private GuestBUS guestBUS;
    private JScrollPane scrollPane;
    private JTextField searchField;
    private List<GuestDTO> guestData;

    public Guest() {
        guestBUS = new GuestBUS(new GuestDAO());
        initComponents();
        loadGuestData();
    }

    // Khởi tạo các component
    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quản lý khách hàng");
        setSize(900, 600);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        controlPanel.setBackground(PANEL_BG);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(250, 35));
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

        mainPanel.add(controlPanel, BorderLayout.NORTH);

        scrollPane = new JScrollPane();
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        setContentPane(mainPanel);
        setLocationRelativeTo(null);
    }

    // Tạo nút icon
    private JButton createIconButton(String iconPath) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(35, 35));
        try {
            button.setIcon(new ImageIcon(getClass().getResource(iconPath)));
        } catch (Exception e) {
            button.setText("...");
        }
        button.setBackground(PANEL_BG);
        button.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // Tải dữ liệu khách hàng
    private void loadGuestData() {
        try {
            guestData = guestBUS.getAllGuests();
            updateTableView();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Tìm kiếm khách hàng
    private void performSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            updateTableView();
            return;
        }

        try {
            List<GuestDTO> results = guestBUS.searchGuests(query);
            if (results == null || results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy khách: " + query,
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                updateTableView();
            } else {
                updateTableViewWithData(results);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Cập nhật bảng
    private void updateTableView() {
        updateTableViewWithData(guestData);
    }

    // Cập nhật bảng với dữ liệu
    private void updateTableViewWithData(List<GuestDTO> data) {
        String[] columnNames = {"Mã khách", "Mã phòng đặt", "Họ tên", "Giới tính", "Ngày sinh", "Số CMND", "Quốc tịch"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (GuestDTO guest : data) {
            Object[] row = {
                    guest.getGuestId(),
                    guest.getBookingRoomId(),
                    guest.getFullName(),
                    guest.getGender(),
                    guest.getDob(),
                    guest.getIdCard(),
                    guest.getNationality()
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
                    if (data == null || data.size() <= rowIndex || rowIndex < 0) {
                        JOptionPane.showMessageDialog(Guest.this, "Dữ liệu không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    final GuestDTO selectedGuest = data.get(rowIndex);

                    editItem.addActionListener(e1 -> editGuest(selectedGuest));
                    deleteItem.addActionListener(e1 -> deleteGuest(selectedGuest));

                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        scrollPane.setViewportView(table);
    }

    // Xóa khách hàng (soft delete)
    private void deleteGuest(GuestDTO guest) {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa khách: " + guest.getFullName() + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (guestBUS.deleteGuest(guest.getGuestId())) {
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
                    loadGuestData();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi xóa: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Dialog sửa khách hàng
    private void editGuest(GuestDTO guest) {
        if (guest == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng để sửa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog editDialog = new JDialog(this, "Sửa khách hàng", true);
        editDialog.setLayout(new BorderLayout());
        editDialog.setSize(450, 380);

        JPanel contentPanel = new JPanel(new GridLayout(6, 2, 15, 15));
        contentPanel.setBackground(PANEL_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("Họ tên:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField nameField = new JTextField(guest.getFullName() != null ? guest.getFullName() : "");
        nameField.setFont(new Font("Arial", Font.PLAIN, 13));
        contentPanel.add(nameLabel);
        contentPanel.add(nameField);

        JLabel genderLabel = new JLabel("Giới tính:");
        genderLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});
        if (guest.getGender() != null) {
            genderCombo.setSelectedItem(guest.getGender());
        }
        genderCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        contentPanel.add(genderLabel);
        contentPanel.add(genderCombo);

        JLabel dobLabel = new JLabel("Ngày sinh:");
        dobLabel.setFont(new Font("Arial", Font.BOLD, 13));
        String dobString = guest.getDob() != null ? guest.getDob().toString() : "";
        JTextField dobField = new JTextField(dobString);
        dobField.setFont(new Font("Arial", Font.PLAIN, 13));
        contentPanel.add(dobLabel);
        contentPanel.add(dobField);

        JLabel idCardLabel = new JLabel("Số CMND:");
        idCardLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField idCardField = new JTextField(guest.getIdCard() != null ? guest.getIdCard() : "");
        idCardField.setFont(new Font("Arial", Font.PLAIN, 13));
        contentPanel.add(idCardLabel);
        contentPanel.add(idCardField);

        JLabel nationalityLabel = new JLabel("Quốc tịch:");
        nationalityLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField nationalityField = new JTextField(guest.getNationality() != null ? guest.getNationality() : "");
        nationalityField.setFont(new Font("Arial", Font.PLAIN, 13));
        contentPanel.add(nationalityLabel);
        contentPanel.add(nationalityField);

        JButton saveButton = new JButton("Lưu");
        saveButton.setBackground(PRIMARY_COLOR);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorderPainted(false);
        saveButton.setFont(new Font("Arial", Font.BOLD, 13));
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.addActionListener(e -> {
            try {
                guest.setFullName(nameField.getText().trim());
                guest.setGender((String) genderCombo.getSelectedItem());
                try {
                    String dobText = dobField.getText().trim();
                    if (!dobText.isEmpty()) {
                        guest.setDob(java.time.LocalDate.parse(dobText));
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(editDialog, "Ngày sinh không hợp lệ! Dùng định dạng: yyyy-MM-dd",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                guest.setIdCard(idCardField.getText().trim());
                guest.setNationality(nationalityField.getText().trim());
                if (guestBUS.updateGuest(guest)) {
                    JOptionPane.showMessageDialog(editDialog, "Cập nhật thành công!");
                    editDialog.dispose();
                    loadGuestData();
                } else {
                    JOptionPane.showMessageDialog(editDialog, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(editDialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Hủy");
        cancelButton.setBackground(Color.LIGHT_GRAY);
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 13));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> editDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(PANEL_BG);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        editDialog.add(contentPanel, BorderLayout.CENTER);
        editDialog.add(buttonPanel, BorderLayout.SOUTH);
        editDialog.setLocationRelativeTo(this);
        editDialog.setVisible(true);
    }
}
