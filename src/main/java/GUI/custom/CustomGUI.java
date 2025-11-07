package GUI.custom;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import BUS.CustomerBUS;
import DTO.CustomerDTO;

public class CustomGUI extends JPanel {

    // === MÀU SẮC HIỆN ĐẠI ===
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color PRIMARY_DARK = new Color(31, 97, 141);
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color WARNING_COLOR = new Color(241, 196, 15);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color BORDER_COLOR = new Color(189, 195, 199);

    // Components
    private JTextField tfTimKiem;
    private JButton btnTimKiem, btnLoc, btnThem;
    private JTable tbDatPhong;
    private JScrollPane jScrollPane1;
    private JPopupMenu popupMenu;
    private JDialog filterDialog;
    private CustomerBUS customerBUS;
    private DefaultTableModel tableModel;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public CustomGUI() {
        initComponents();
        customerBUS = new CustomerBUS();
        initTableModel();
        loadCustomerData();
        styleComponents();
        initPopupMenu();
        initFilterDialog();
        setupSearchAndFilter();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // === TOP PANEL: TÌM KIẾM + LỌC + THÊM ===
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Ô tìm kiếm
        tfTimKiem = new JTextField(20);
        tfTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tfTimKiem.setPreferredSize(new Dimension(300, 38));

        // Nút tìm kiếm
        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setIcon(getIcon("/images/search.png"));
        btnTimKiem.setPreferredSize(new Dimension(110, 38));

        // Nút lọc
        btnLoc = new JButton("Lọc");
        btnLoc.setIcon(getIcon("/images/filter.png"));
        btnLoc.setPreferredSize(new Dimension(90, 38));

        // Nút thêm
        btnThem = new JButton("Thêm");
        btnThem.setIcon(getIcon("/images/add.png"));
        btnThem.setPreferredSize(new Dimension(90, 38));

        // Căn chỉnh
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0;
        topPanel.add(tfTimKiem, gbc);

        gbc.gridx = 1; gbc.weightx = 0;
        topPanel.add(btnTimKiem, gbc);

        gbc.gridx = 2;
        topPanel.add(btnLoc, gbc);

        gbc.gridx = 3;
        topPanel.add(btnThem, gbc);

        // === TABLE ===
        tbDatPhong = new JTable();
        jScrollPane1 = new JScrollPane(tbDatPhong);
        jScrollPane1.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        add(topPanel, BorderLayout.NORTH);
        add(jScrollPane1, BorderLayout.CENTER);
    }

    private ImageIcon getIcon(String path) {
        java.net.URL imgURL = getClass().getResource(path);
        return imgURL != null ? new ImageIcon(imgURL) : null;
    }

    private void initTableModel() {
        tableModel = new DefaultTableModel(
            new Object[][]{},
            new String[]{"Mã KH", "Họ và tên", "Ngày sinh", "Số điện thoại", "Giới tính", "Quốc tịch"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tbDatPhong.setModel(tableModel);
    }

    private void loadCustomerData() {
        tableModel.setRowCount(0);
        List<CustomerDTO> customers = customerBUS.getAllCustomers();
        for (CustomerDTO c : customers) {
            addCustomerToTable(c);
        }
    }

    private void addCustomerToTable(CustomerDTO c) {
        tableModel.addRow(new Object[]{
            c.getCustomer_id(),
            c.getFull_name(),
            c.getDob() != null ? dateFormat.format(c.getDob()) : "Chưa có",
            c.getPhone() != null ? c.getPhone() : "Chưa có",
            c.getGender() != null ? c.getGender() : "Chưa chọn",
            c.getNationality() != null ? c.getNationality() : "Chưa có"
        });
    }

    // === STYLE ===
    private void styleComponents() {
        // TextField
        tfTimKiem.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));
        tfTimKiem.setBackground(Color.WHITE);
        tfTimKiem.setForeground(TEXT_COLOR);

        // Buttons
        styleButton(btnTimKiem, PRIMARY_COLOR, Color.WHITE);
        styleButton(btnLoc, SECONDARY_COLOR, Color.WHITE);
        styleButton(btnThem, SUCCESS_COLOR, Color.WHITE);

        // Table
        styleTable();
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bg); }
        });
    }

    private void styleTable() {
        tbDatPhong.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbDatPhong.setRowHeight(42);
        tbDatPhong.setShowGrid(true);
        tbDatPhong.setGridColor(new Color(230, 230, 230));
        tbDatPhong.setIntercellSpacing(new Dimension(1, 1));
        tbDatPhong.setSelectionBackground(SECONDARY_COLOR);
        tbDatPhong.setSelectionForeground(Color.WHITE);
        tbDatPhong.setBackground(Color.WHITE);

        // Header
        JTableHeader header = tbDatPhong.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 45));
        header.setBorder(BorderFactory.createLineBorder(PRIMARY_DARK, 1));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        // Center cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tbDatPhong.getColumnCount(); i++) {
            tbDatPhong.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Custom renderer: nền trắng + hover + màu giới tính
        tbDatPhong.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(JLabel.CENTER);

                if (!isSelected) {
                    label.setBackground(Color.WHITE);
                    label.setForeground(TEXT_COLOR);
                } else {
                    label.setBackground(SECONDARY_COLOR);
                    label.setForeground(Color.WHITE);
                }

                // Tô màu giới tính
                if (column == 4 && value != null) {
                    String gender = value.toString();
                    if ("Nam".equals(gender)) label.setForeground(PRIMARY_COLOR);
                    else if ("Nữ".equals(gender)) label.setForeground(new Color(231, 76, 133));
                    else if ("Khác".equals(gender)) label.setForeground(WARNING_COLOR);
                    else label.setForeground(new Color(149, 165, 166));
                } else if (!isSelected) {
                    label.setForeground(TEXT_COLOR);
                }

                return label;
            }
        });

        // Hover dòng
        tbDatPhong.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = tbDatPhong.rowAtPoint(e.getPoint());
                if (row >= 0 && !tbDatPhong.isRowSelected(row)) {
                    tbDatPhong.repaint(tbDatPhong.getCellRect(row, 0, true));
                }
            }
        });

        jScrollPane1.setBackground(Color.WHITE);
        jScrollPane1.getViewport().setBackground(Color.WHITE);
    }

    private void initPopupMenu() {
        popupMenu = new JPopupMenu();
        popupMenu.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        popupMenu.setBackground(Color.WHITE);

        JMenuItem itemXem = createMenuItem("Xem chi tiết", PRIMARY_COLOR);
        JMenuItem itemSua = createMenuItem("Sửa", SECONDARY_COLOR);
        JMenuItem itemXoa = createMenuItem("Xóa", DANGER_COLOR);

        itemXem.addActionListener(e -> showDetailAction());
        itemSua.addActionListener(e -> editAction());
        itemXoa.addActionListener(e -> deleteAction());

        popupMenu.add(itemXem);
        popupMenu.add(itemSua);
        popupMenu.add(itemXoa);

        tbDatPhong.setComponentPopupMenu(popupMenu);

        tbDatPhong.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = tbDatPhong.rowAtPoint(e.getPoint());
                    if (row >= 0) tbDatPhong.setRowSelectionInterval(row, row);
                }
            }
        });
    }

    private JMenuItem createMenuItem(String text, Color color) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        item.setForeground(color);
        item.setBackground(Color.WHITE);
        item.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        item.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { item.setBackground(new Color(248, 249, 250)); }
            public void mouseExited(MouseEvent e) { item.setBackground(Color.WHITE); }
        });
        return item;
    }

    private void showDetailAction() {
        int row = tbDatPhong.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một khách hàng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int customerId = (int) tableModel.getValueAt(row, 0);
        CustomerDTO customer = customerBUS.getCustomerById(customerId);
        if (customer != null) showCustomerDetailDialog(customer);
    }

    private void showCustomerDetailDialog(CustomerDTO customer) {
        JDialog dialog = new JDialog((Frame) null, "Chi tiết khách hàng", true);
        dialog.setSize(500, 620);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(BACKGROUND_COLOR);

        JTextArea info = new JTextArea();
        info.setEditable(false);
        info.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        info.setMargin(new Insets(25, 25, 25, 25));
        info.setBackground(Color.WHITE);
        info.setForeground(TEXT_COLOR);
        info.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        info.setLineWrap(true);
        info.setWrapStyleWord(true);

        info.setText(
            "Mã khách hàng: " + customer.getCustomer_id() + "\n\n" +
            "Họ và tên: " + customer.getFull_name() + "\n" +
            "Số điện thoại: " + (customer.getPhone() != null ? customer.getPhone() : "Chưa có") + "\n" +
            "Email: " + (customer.getEmail() != null && !customer.getEmail().isEmpty() ? customer.getEmail() : "Chưa có") + "\n" +
            "CMND/CCCD: " + (customer.getId_card() != null ? customer.getId_card() : "Chưa có") + "\n" +
            "Địa chỉ: Chưa hỗ trợ hiển thị\n" +
            "Quốc tịch: " + (customer.getNationality() != null ? customer.getNationality() : "Chưa có") + "\n" +
            "Ngày sinh: " + (customer.getDob() != null ? dateFormat.format(customer.getDob()) : "Chưa có") + "\n" +
            "Giới tính: " + (customer.getGender() != null ? customer.getGender() : "Chưa chọn") + "\n" +
            "Ghi chú: " + (customer.getNote() != null && !customer.getNote().isEmpty() ? customer.getNote() : "Không có")
        );

        JScrollPane scroll = new JScrollPane(info);
        scroll.setBorder(null);
        dialog.add(scroll, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Đóng");
        styleButton(closeBtn, new Color(149, 165, 166), Color.WHITE);
        closeBtn.addActionListener(e -> dialog.dispose());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        bottom.setBackground(BACKGROUND_COLOR);
        bottom.add(closeBtn);
        dialog.add(bottom, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void editAction() {
        int row = tbDatPhong.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng để sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int customerId = (int) tableModel.getValueAt(row, 0);
        JOptionPane.showMessageDialog(this,
            "Chức năng sửa khách hàng ID: " + customerId + "\n(Chưa triển khai form chỉnh sửa)\nGợi ý: Tạo CustomerEditDialog extends JDialog",
            "Sửa khách hàng", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteAction() {
        int row = tbDatPhong.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = (String) tableModel.getValueAt(row, 1);
        int customerId = (int) tableModel.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(
            this, "Xóa khách hàng:\n" + name + " (ID: " + customerId + ")",
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = customerBUS.deleteCustomer(customerId);
            if (success) {
                tableModel.removeRow(row);
                JOptionPane.showMessageDialog(this, "Xóa thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Xóa thất bại!\nKhách hàng có thể đã đặt phòng hoặc lỗi hệ thống.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void setupSearchAndFilter() {
        tfTimKiem.addActionListener(e -> performSearch());
        btnTimKiem.addActionListener(e -> performSearch());
        btnLoc.addActionListener(e -> filterDialog.setVisible(true));
        btnThem.addActionListener(e -> showAddCustomerDialog());
    }

    // private void showAddCustomerDialog() {
    //     JOptionPane.showMessageDialog(this,
    //         "Mở form thêm khách hàng mới\n(Gợi ý: Tạo CustomerAddDialog extends JDialog)",
    //         "Thêm khách hàng", JOptionPane.INFORMATION_MESSAGE);
    // }

    // private void performSearch() {
    //     String keyword = tfTimKiem.getText().trim();
    //     List<CustomerDTO> results = customerBUS.searchCustomers(keyword);
    //     updateTable(results);
    // }

    private void initFilterDialog() {
        filterDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Bộ lọc khách hàng", true);
        filterDialog.setSize(460, 380);
        filterDialog.setLocationRelativeTo(this);
        filterDialog.setLayout(new BorderLayout());
        filterDialog.getContentPane().setBackground(BACKGROUND_COLOR);

        // Title
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(PRIMARY_COLOR);
        titlePanel.setPreferredSize(new Dimension(460, 55));
        JLabel titleLabel = new JLabel("BỘ LỌC KHÁCH HÀNG");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        filterDialog.add(titlePanel, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 10, 12, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Quốc tịch
        JLabel lblQuocTich = new JLabel("Quốc tịch:");
        lblQuocTich.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JComboBox<String> cbQuocTich = new JComboBox<>();
        cbQuocTich.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbQuocTich.addItem("Tất cả");
        List<String> nationalities = customerBUS.getAllNationalities();
        for (String nat : nationalities) cbQuocTich.addItem(nat);
        cbQuocTich.setBackground(Color.WHITE);
        cbQuocTich.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.35;
        formPanel.add(lblQuocTich, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        formPanel.add(cbQuocTich, gbc);

        // Giới tính
        JLabel lblGioiTinh = new JLabel("Giới tính:");
        lblGioiTinh.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JComboBox<String> cbGioiTinh = new JComboBox<>(new String[]{"Tất cả", "Nam", "Nữ", "Khác"});
        cbGioiTinh.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbGioiTinh.setBackground(Color.WHITE);
        cbGioiTinh.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.35;
        formPanel.add(lblGioiTinh, gbc);
        gbc.gridx = 1; gbc.weightx = 0.65;
        formPanel.add(cbGioiTinh, gbc);

        filterDialog.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton btnApDung = new JButton("Áp dụng");
        styleButton(btnApDung, SUCCESS_COLOR, Color.WHITE);
        btnApDung.addActionListener(e -> {
            applyFilter(cbQuocTich.getSelectedItem().toString(), cbGioiTinh.getSelectedItem().toString());
            filterDialog.dispose();
        });

        JButton btnHuy = new JButton("Hủy");
        styleButton(btnHuy, new Color(149, 165, 166), Color.WHITE);
        btnHuy.addActionListener(e -> filterDialog.dispose());

        JButton btnReset = new JButton("Đặt lại");
        styleButton(btnReset, WARNING_COLOR, Color.WHITE);
        btnReset.addActionListener(e -> {
            cbQuocTich.setSelectedIndex(0);
            cbGioiTinh.setSelectedIndex(0);
        });

        buttonPanel.add(btnApDung);
        buttonPanel.add(btnHuy);
        buttonPanel.add(btnReset);

        filterDialog.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void applyFilter(String nationality, String gender) {
        List<CustomerDTO> results = customerBUS.getAllCustomers();

        if (!"Tất cả".equals(nationality)) {
            results = results.stream()
                .filter(c -> c.getNationality() != null && c.getNationality().equals(nationality))
                .toList();
        }

        if (!"Tất cả".equals(gender)) {
            results = results.stream()
                .filter(c -> c.getGender() != null && c.getGender().equals(gender))
                .toList();
        }

        updateTable(results);
    }

    private void updateTable(List<CustomerDTO> customers) {
        tableModel.setRowCount(0);
        for (CustomerDTO c : customers) {
            addCustomerToTable(c);
        }
    }
}