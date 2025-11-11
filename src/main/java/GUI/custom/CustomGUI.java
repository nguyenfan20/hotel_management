package GUI.custom;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
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

import com.toedter.calendar.JDateChooser;

import BUS.CustomerBUS;
import DTO.CustomerDTO;

public class CustomGUI extends JPanel {

    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color PANEL_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(224, 224, 224);
    private static final Color TEXT_COLOR = new Color(33, 33, 33);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);

    // Components
    private JTextField searchField;
    private JTable customerTable;
    private JScrollPane scrollPane;
    private CustomerBUS customerBUS;
    private DefaultTableModel tableModel;
    private List<CustomerDTO> customerData = new ArrayList<>();
    private List<CustomerDTO> filteredCustomerData = new ArrayList<>();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public CustomGUI() {
        customerBUS = new CustomerBUS();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // === CONTROL PANEL ===
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        controlPanel.setBackground(PANEL_BG);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Nút Thêm
        JButton addButton = new JButton("Thêm khách hàng");
        addButton.setPreferredSize(new Dimension(170, 35));
        addButton.setIcon(new ImageIcon(getClass().getResource("/images/add-button.png")));
        stylePrimaryButton(addButton);
        addButton.addActionListener(e -> showAddCustomerDialog());
        controlPanel.add(addButton);

        controlPanel.add(Box.createHorizontalStrut(20));

        // Ô tìm kiếm
        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(220, 35));
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

        // Nút tìm kiếm
        JButton searchButton = createIconButton("/images/search.png");
        searchButton.addActionListener(e -> performSearch());
        controlPanel.add(searchButton);

        // Nút lọc
        JButton filterButton = createIconButton("/images/filter.png");
        filterButton.addActionListener(e -> showFilterDialog());
        controlPanel.add(filterButton);

        JButton refreshButton = createIconButton("/icon/reload.png");
        refreshButton.addActionListener(e -> loadData());
        controlPanel.add(refreshButton);

        add(controlPanel, BorderLayout.NORTH);

        // === BẢNG DỮ LIỆU ===
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

    private void stylePrimaryButton(JButton btn) {
        btn.setBackground(PRIMARY_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void loadData() {
        try {
            customerData = customerBUS.getAllCustomers();
            filteredCustomerData = new ArrayList<>(customerData);
            updateTableView();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu khách hàng: " + e.getMessage());
        }
    }

    private void performSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            filteredCustomerData = new ArrayList<>(customerData);
        } else {
            filteredCustomerData = customerData.stream()
                    .filter(c -> {
                        String name = c.getFull_name() != null ? c.getFull_name().toLowerCase() : "";
                        String phone = c.getPhone() != null ? c.getPhone().toLowerCase() : "";
                        String idCard = c.getId_card() != null ? c.getId_card().toLowerCase() : "";
                        String nationality = c.getNationality() != null ? c.getNationality().toLowerCase() : "";
                        String genderVn = c.getGender() != null ? toVietnameseGender(c.getGender()).toLowerCase() : "";
                        return name.contains(query) || phone.contains(query) ||
                                idCard.contains(query) || nationality.contains(query) || genderVn.contains(query);
                    })
                    .collect(Collectors.toList());
        }
        updateTableView();
    }

    private void showFilterDialog() {
        JDialog filterDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Lọc khách hàng", true);
        filterDialog.setLayout(new BorderLayout());
        filterDialog.setSize(380, 200);
        filterDialog.setLocationRelativeTo(this);

        JPanel contentPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        contentPanel.setBackground(PANEL_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Quốc tịch
        JLabel natLabel = new JLabel("Quốc tịch:");
        natLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JComboBox<String> natCombo = new JComboBox<>();
        natCombo.addItem("Tất cả");
        List<String> nationalities = customerBUS.getAllNationalities();
        for (String nat : nationalities) natCombo.addItem(nat);
        natCombo.setPreferredSize(new Dimension(150, 30));
        contentPanel.add(natLabel);
        contentPanel.add(natCombo);

        // Giới tính
        JLabel genderLabel = new JLabel("Giới tính:");
        genderLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Tất cả", "Nam", "Nữ", "Khác"});
        genderCombo.setPreferredSize(new Dimension(150, 30));
        contentPanel.add(genderLabel);
        contentPanel.add(genderCombo);

        // Nút xác nhận
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(PANEL_BG);
        JButton confirmButton = new JButton("Áp dụng");
        confirmButton.setPreferredSize(new Dimension(100, 35));
        stylePrimaryButton(confirmButton);
        confirmButton.addActionListener(e -> {
            applyFilter(natCombo.getSelectedItem().toString(), genderCombo.getSelectedItem().toString());
            filterDialog.dispose();
        });
        buttonPanel.add(confirmButton);

        filterDialog.add(contentPanel, BorderLayout.CENTER);
        filterDialog.add(buttonPanel, BorderLayout.SOUTH);
        filterDialog.setVisible(true);
    }

    private void applyFilter(String nationality, String gender) {
        filteredCustomerData = new ArrayList<>(customerData);

        if (!"Tất cả".equals(nationality)) {
            filteredCustomerData = filteredCustomerData.stream()
                    .filter(c -> c.getNationality() != null && c.getNationality().equals(nationality))
                    .collect(Collectors.toList());
        }

        if (!"Tất cả".equals(gender)) {
            String englishGender = toEnglishGender(gender);
            filteredCustomerData = filteredCustomerData.stream()
                    .filter(c -> c.getGender() != null && c.getGender().equals(englishGender))
                    .collect(Collectors.toList());
        }

        updateTableView();
    }

    private void showAddCustomerDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm khách hàng mới", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 650);
        dialog.setLocationRelativeTo(this);

        // === Content Panel ===
        JPanel content = new JPanel(new GridLayout(10, 2, 15, 15));
        content.setBackground(PANEL_BG);
        content.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel nameLabel = new JLabel("Họ và tên:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField nameField = new JTextField();

        JLabel phoneLabel = new JLabel("Số điện thoại:");
        phoneLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField phoneField = new JTextField();

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField emailField = new JTextField();

        JLabel idCardLabel = new JLabel("CMND/CCCD:");
        idCardLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField idCardField = new JTextField();

        JLabel addressLabel = new JLabel("Địa chỉ:");
        addressLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField addressField = new JTextField();

        JLabel nationalityLabel = new JLabel("Quốc tịch:");
        nationalityLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JComboBox<String> nationalityCombo = new JComboBox<>();
        nationalityCombo.addItem("Việt Nam");
        try {
            List<String> nationalities = customerBUS.getAllNationalities();
            for (String nat : nationalities) {
                if (!nat.equals("Việt Nam")) {
                    nationalityCombo.addItem(nat);
                }
            }
        } catch (Exception ex) {
            System.err.println("Lỗi tải quốc tịch: " + ex.getMessage());
        }

        JLabel dobLabel = new JLabel("Ngày sinh:");
        dobLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JDateChooser dobChooser = new JDateChooser();
        dobChooser.setDateFormatString("dd/MM/yyyy");
        dobChooser.setPreferredSize(new Dimension(200, 30));

        JLabel genderLabel = new JLabel("Giới tính:");
        genderLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});

        JLabel noteLabel = new JLabel("Ghi chú:");
        noteLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextArea noteArea = new JTextArea(3, 20);
        noteArea.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        JScrollPane noteScroll = new JScrollPane(noteArea);

        content.add(nameLabel); content.add(nameField);
        content.add(phoneLabel); content.add(phoneField);
        content.add(emailLabel); content.add(emailField);
        content.add(idCardLabel); content.add(idCardField);
        content.add(addressLabel); content.add(addressField);
        content.add(nationalityLabel); content.add(nationalityCombo);
        content.add(dobLabel); content.add(dobChooser);
        content.add(genderLabel); content.add(genderCombo);
        content.add(noteLabel); content.add(noteScroll);

        dialog.add(content, BorderLayout.CENTER);

        // === Button Panel ===
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton saveBtn = new JButton("Lưu");
        saveBtn.setPreferredSize(new Dimension(100, 35));
        stylePrimaryButton(saveBtn);
        saveBtn.addActionListener(e -> {
            try {
                CustomerDTO customer = new CustomerDTO();
                customer.setFull_name(nameField.getText().trim());
                customer.setPhone(phoneField.getText().trim());
                customer.setEmail(emailField.getText().trim().isEmpty() ? null : emailField.getText().trim());
                customer.setId_card(idCardField.getText().trim());
                customer.setAddress(addressField.getText().trim().isEmpty() ? null : addressField.getText().trim());
                customer.setNationality((String) nationalityCombo.getSelectedItem());
                String selectedVn = (String) genderCombo.getSelectedItem();
                customer.setGender(toEnglishGender(selectedVn));
                customer.setNote(noteArea.getText().trim().isEmpty() ? null : noteArea.getText().trim());

                if (dobChooser.getDate() != null) {
                    customer.setDob(dobChooser.getDate());
                }

                boolean success = customerBUS.addCustomer(customer);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Thêm khách hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                    dialog.dispose();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelBtn = new JButton("Hủy");
        cancelBtn.setPreferredSize(new Dimension(100, 35));
        cancelBtn.setBackground(new Color(149, 165, 166));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorderPainted(false);
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 13));
        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void showCustomerDetail(CustomerDTO customer) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chi tiết khách hàng", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 500);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(9, 2, 10, 12));
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        addDetailRow(panel, "Mã KH:", String.valueOf(customer.getCustomer_id()));
        addDetailRow(panel, "Họ tên:", customer.getFull_name());
        addDetailRow(panel, "SĐT:", customer.getPhone() != null ? customer.getPhone() : "Chưa có");
        addDetailRow(panel, "Email:", customer.getEmail() != null ? customer.getEmail() : "Chưa có");
        addDetailRow(panel, "CMND/CCCD:", customer.getId_card());
        addDetailRow(panel, "Địa chỉ:", customer.getAddress());
        addDetailRow(panel, "Quốc tịch:", customer.getNationality());
        addDetailRow(panel, "Ngày sinh:", customer.getDob() != null ? dateFormat.format(customer.getDob()) : "Chưa có");
        addDetailRow(panel, "Giới tính:", toVietnameseGender(customer.getGender()));

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        dialog.add(scroll, BorderLayout.CENTER);

        JButton closeBtn = new JButton("Đóng");
        closeBtn.setPreferredSize(new Dimension(100, 35));
        closeBtn.setBackground(new Color(149, 165, 166));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.addActionListener(e -> dialog.dispose());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        bottom.setBackground(BACKGROUND_COLOR);
        bottom.add(closeBtn);
        dialog.add(bottom, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        JLabel val = new JLabel(value);
        val.setFont(new Font("Arial", Font.PLAIN, 13));
        panel.add(lbl);
        panel.add(val);
    }

    private void editCustomer(CustomerDTO customer) {
        if (customer == null) return;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa khách hàng", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 650);
        dialog.setLocationRelativeTo(this);

        JPanel content = new JPanel(new GridLayout(9, 2, 15, 15));
        content.setBackground(PANEL_BG);
        content.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel nameLabel = new JLabel("Họ và tên:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField nameField = new JTextField(customer.getFull_name());

        JLabel phoneLabel = new JLabel("Số điện thoại:");
        phoneLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField phoneField = new JTextField(customer.getPhone());

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField emailField = new JTextField(customer.getEmail() != null ? customer.getEmail() : "");

        JLabel idCardLabel = new JLabel("CMND/CCCD:");
        idCardLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField idCardField = new JTextField(customer.getId_card());

        JLabel addressLabel = new JLabel("Địa chỉ:");
        addressLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField addressField = new JTextField(customer.getAddress() != null ? customer.getAddress() : "");

        JLabel nationalityLabel = new JLabel("Quốc tịch:");
        nationalityLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JComboBox<String> nationalityCombo = new JComboBox<>();
        try {
            List<String> nationalities = customerBUS.getAllNationalities();
            for (String nat : nationalities) {
                nationalityCombo.addItem(nat);
            }
            nationalityCombo.setSelectedItem(customer.getNationality());
        } catch (Exception ex) {
            nationalityCombo.addItem(customer.getNationality());
        }

        JLabel dobLabel = new JLabel("Ngày sinh:");
        dobLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JDateChooser dobChooser = new JDateChooser();
        dobChooser.setDateFormatString("dd/MM/yyyy");
        dobChooser.setDate(customer.getDob());
        dobChooser.setPreferredSize(new Dimension(200, 30));

        JLabel genderLabel = new JLabel("Giới tính:");
        genderLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});
        genderCombo.setSelectedItem(toVietnameseGender(customer.getGender()));

        JLabel noteLabel = new JLabel("Ghi chú:");
        noteLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextArea noteArea = new JTextArea(customer.getNote() != null ? customer.getNote() : "", 3, 20);
        noteArea.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        JScrollPane noteScroll = new JScrollPane(noteArea);

        content.add(nameLabel); content.add(nameField);
        content.add(phoneLabel); content.add(phoneField);
        content.add(emailLabel); content.add(emailField);
        content.add(idCardLabel); content.add(idCardField);
        content.add(addressLabel); content.add(addressField);
        content.add(nationalityLabel); content.add(nationalityCombo);
        content.add(dobLabel); content.add(dobChooser);
        content.add(genderLabel); content.add(genderCombo);
        content.add(noteLabel); content.add(noteScroll);

        dialog.add(content, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton saveBtn = new JButton("Cập nhật");
        saveBtn.setPreferredSize(new Dimension(110, 35));
        stylePrimaryButton(saveBtn);
        saveBtn.addActionListener(e -> {
            try {
                customer.setFull_name(nameField.getText().trim());
                customer.setPhone(phoneField.getText().trim());
                customer.setId_card(idCardField.getText().trim());
                customer.setEmail(emailField.getText().trim().isEmpty() ? null : emailField.getText().trim());
                customer.setNationality((String) nationalityCombo.getSelectedItem());
                String selectedVn = (String) genderCombo.getSelectedItem();
                customer.setGender(toEnglishGender(selectedVn));
                customer.setNote(noteArea.getText().trim().isEmpty() ? null : noteArea.getText().trim());

                if (dobChooser.getDate() != null) {
                    customer.setDob(dobChooser.getDate());
                } else {
                    customer.setDob(null);
                }

                boolean success = customerBUS.updateCustomer(customer);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Cập nhật thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                    dialog.dispose();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelBtn = new JButton("Hủy");
        cancelBtn.setPreferredSize(new Dimension(100, 35));
        cancelBtn.setBackground(new Color(149, 165, 166));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorderPainted(false);
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 13));
        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void deleteCustomer(int customerId, String name) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Xóa khách hàng:\n" + name + " (ID: " + customerId + ")",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = customerBUS.deleteCustomer(customerId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Xóa thất bại!\nKhách hàng có thể đã đặt phòng hoặc lỗi hệ thống.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateTableView() {
        String[] columnNames = {"Mã KH", "Họ và tên", "Ngày sinh", "SĐT", "Giới tính", "Quốc tịch"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (CustomerDTO c : filteredCustomerData) {
            Object[] row = {
                    c.getCustomer_id(),
                    c.getFull_name(),
                    c.getDob() != null ? dateFormat.format(c.getDob()) : "Chưa có",
                    c.getPhone() != null ? c.getPhone() : "Chưa có",
                    toVietnameseGender(c.getGender()),
                    c.getNationality() != null ? c.getNationality() : "Chưa có"
            };
            tableModel.addRow(row);
        }

        customerTable = new JTable(tableModel);
        customerTable.setRowHeight(40);
        customerTable.setFont(new Font("Arial", Font.PLAIN, 13));
        customerTable.setGridColor(BORDER_COLOR);
        customerTable.setSelectionBackground(new Color(232, 240, 254));
        customerTable.setSelectionForeground(TEXT_COLOR);
        customerTable.setShowVerticalLines(true);
        customerTable.setIntercellSpacing(new Dimension(1, 1));

        JTableHeader header = customerTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(PANEL_BG);
        header.setForeground(TEXT_COLOR);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR));
        header.setPreferredSize(new Dimension(0, 45));

        customerTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(JLabel.CENTER);
                if (!isSelected) {
                    label.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                }
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                if (column == 4 && value != null) {
                    String gender = value.toString();
                    if ("Nam".equals(gender)) label.setForeground(PRIMARY_COLOR);
                    else if ("Nữ".equals(gender)) label.setForeground(new Color(231, 76, 133));
                    else if ("Khác".equals(gender)) label.setForeground(new Color(241, 196, 15));
                    else label.setForeground(new Color(149, 165, 166));
                } else if (!isSelected) {
                    label.setForeground(TEXT_COLOR);
                }

                return label;
            }
        });

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem detailItem = new JMenuItem("Xem chi tiết");
        JMenuItem editItem = new JMenuItem("Sửa");
        JMenuItem deleteItem = new JMenuItem("Xóa");
        popupMenu.add(detailItem);
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);

        customerTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger() && customerTable.getSelectedRow() != -1) {
                    int rowIndex = customerTable.getSelectedRow();
                    int modelRow = customerTable.convertRowIndexToModel(rowIndex);
                    CustomerDTO customer = filteredCustomerData.get(modelRow);

                    detailItem.addActionListener(a -> showCustomerDetail(customer));
                    editItem.addActionListener(a -> editCustomer(customer));
                    deleteItem.addActionListener(a -> deleteCustomer(customer.getCustomer_id(), customer.getFull_name()));

                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        scrollPane.setViewportView(customerTable);
    }

    // === ÁNH XẠ GIỚI TÍNH ===
    private String toVietnameseGender(String englishGender) {
        if (englishGender == null) return "Chưa chọn";
        return switch (englishGender) {
            case "Male" -> "Nam";
            case "Female" -> "Nữ";
            case "Other" -> "Khác";
            default -> englishGender;
        };
    }

    private String toEnglishGender(String vietnameseGender) {
        if (vietnameseGender == null || vietnameseGender.equals("Chưa chọn")) return null;
        return switch (vietnameseGender) {
            case "Nam" -> "Male";
            case "Nữ" -> "Female";
            case "Khác" -> "Other";
            default -> vietnameseGender;
        };
    }
}
