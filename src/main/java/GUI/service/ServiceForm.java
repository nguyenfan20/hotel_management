package GUI.service;

import BUS.ServiceBUS;
import DTO.ServiceDTO;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class ServiceForm extends javax.swing.JPanel {
    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color PANEL_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(224, 224, 224);
    private static final Color TEXT_COLOR = new Color(33, 33, 33);
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);
    private static final Color DANGER_COLOR = new Color(239, 68, 68);

    private final ServiceBUS serviceBus = new ServiceBUS();
    private JScrollPane scrollPane;
    private JTextField tfSearch;
    private JComboBox<String> cbSearch;

    public ServiceForm() {
        initComponents();
        loadTableData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Control Panel
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBackground(PANEL_BG);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Left panel with search controls
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setBackground(PANEL_BG);

        // Title
        JLabel lblTitle = new JLabel("QUẢN LÝ DỊCH VỤ");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(TEXT_COLOR);

        // Search field
        tfSearch = new JTextField(20);
        tfSearch.setPreferredSize(new Dimension(250, 35));
        tfSearch.setFont(new Font("Arial", Font.PLAIN, 13));
        tfSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        tfSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchService();
                }
            }
        });

        // Search button
        JButton btnSearch = createIconButton("Tìm");
        btnSearch.setPreferredSize(new Dimension(35, 35));
        btnSearch.addActionListener(e -> searchService());

        // Filter button
        JButton btnFilter = createIconButton("Lọc");
        btnFilter.setPreferredSize(new Dimension(35, 35));
        btnFilter.addActionListener(e -> showFilterDialog());

        // Refresh button
        JButton btnRefresh = createIconButton("⟳");
        btnRefresh.setPreferredSize(new Dimension(35, 35));
        btnRefresh.addActionListener(e -> loadTableData());

        leftPanel.add(lblTitle);
        leftPanel.add(new JSeparator(JSeparator.VERTICAL));
        leftPanel.add(tfSearch);
        leftPanel.add(btnSearch);
        leftPanel.add(btnFilter);
        leftPanel.add(btnRefresh);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(PANEL_BG);

        JButton btnAdd = createActionButton("+ Thêm", SUCCESS_COLOR);
        btnAdd.setPreferredSize(new Dimension(100, 35));
        btnAdd.addActionListener(e -> addService());
        rightPanel.add(btnAdd);

        controlPanel.add(leftPanel, BorderLayout.WEST);
        controlPanel.add(rightPanel, BorderLayout.EAST);

        add(controlPanel, BorderLayout.NORTH);

        // Table
        scrollPane = new JScrollPane();
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        add(scrollPane, BorderLayout.CENTER);

        updateTableView(new ArrayList<>());
    }

    private JButton createIconButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(90, 35));
        button.setBackground(PANEL_BG);
        button.setForeground(TEXT_COLOR);
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createActionButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(80, 35));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setBorder(null);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void loadTableData() {
        List<ServiceDTO> services = serviceBus.getAll();
        updateTableView(services);
    }

    private void searchService() {
        String keyword = tfSearch.getText().trim().toLowerCase();
        String searchType = (String) cbSearch.getSelectedItem();

        if (keyword.isEmpty()) {
            loadTableData();
            return;
        }

        List<ServiceDTO> allServices = serviceBus.getAll();
        List<ServiceDTO> results = new ArrayList<>();

        for (ServiceDTO s : allServices) {
            if (s == null) continue;

            switch (searchType) {
                case "Tên dịch vụ":
                    if (s.getName() != null && s.getName().toLowerCase().contains(keyword)) {
                        results.add(s);
                    }
                    break;

                case "Đơn vị":
                    if (s.getUnit() != null && s.getUnit().toLowerCase().contains(keyword)) {
                        results.add(s);
                    }
                    break;

                case "Giá":
                    try {
                        double price = Double.parseDouble(keyword);
                        if (s.getUnitPrice() == price) {
                            results.add(s);
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Giá phải là số!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    break;
            }
        }

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả phù hợp!");
            loadTableData();
            return;
        }

        updateTableView(results);
    }

    private void updateTableView(List<ServiceDTO> services) {
        String[] columnNames = {"ID", "Tên dịch vụ", "Đơn vị", "Giá", "Loại tính phí", "Trạng thái"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (ServiceDTO s : services) {
            model.addRow(new Object[]{
                    s.getServiceId(),
                    s.getName(),
                    s.getUnit(),
                    String.format("%.0f", s.getUnitPrice()),
                    s.getChargeType(),
                    s.isActive() ? "Hoạt động" : "Ngừng"
            });
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

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger() && table.getSelectedRow() != -1) {
                    int rowIndex = table.getSelectedRow();
                    int serviceId = (int) model.getValueAt(rowIndex, 0);
                    ServiceDTO service = serviceBus.getById(serviceId);

                    JPopupMenu contextMenu = new JPopupMenu();
                    JMenuItem editMenuItem = new JMenuItem("Sửa");
                    JMenuItem deleteMenuItem = new JMenuItem("Xóa");

                    editMenuItem.addActionListener(e1 -> {
                        table.setRowSelectionInterval(rowIndex, rowIndex);
                        editService();
                    });
                    deleteMenuItem.addActionListener(e1 -> {
                        table.setRowSelectionInterval(rowIndex, rowIndex);
                        deleteService();
                    });

                    contextMenu.add(editMenuItem);
                    contextMenu.add(deleteMenuItem);
                    contextMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        scrollPane.setViewportView(table);
    }

    private void addService() {
        ServiceDialog dialog = new ServiceDialog(SwingUtilities.getWindowAncestor(this), true);
        ServiceDTO newService = dialog.openForCreate();

        if (newService != null) {
            serviceBus.add(newService);
            loadTableData();
            JOptionPane.showMessageDialog(this, "Thêm dịch vụ thành công!");
        }
    }

    private void editService() {
        JTable table = (JTable) scrollPane.getViewport().getView();
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dịch vụ cần sửa!");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int id = (int) model.getValueAt(row, 0);
        ServiceDTO s = serviceBus.getById(id);

        ServiceDialog dialog = new ServiceDialog(SwingUtilities.getWindowAncestor(this), true);
        ServiceDTO updated = dialog.openForEdit(s);

        if (updated != null) {
            serviceBus.update(updated);
            loadTableData();
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
        }
    }

    private void deleteService() {
        JTable table = (JTable) scrollPane.getViewport().getView();
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn dịch vụ cần xóa!");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa dịch vụ này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            serviceBus.delete(id);
            loadTableData();
        }
    }

    private void showFilterDialog() {
        JDialog filterDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Lọc dịch vụ", true);
        filterDialog.setLayout(new BorderLayout());
        filterDialog.setSize(350, 180);

        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 15, 15));
        contentPanel.setBackground(PANEL_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel statusLabel = new JLabel("Trạng thái:");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Tất cả", "Hoạt động", "Ngừng"});
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
            filterServices(selectedStatus);
            filterDialog.dispose();
        });
        buttonPanel.add(confirmButton);

        filterDialog.add(contentPanel, BorderLayout.CENTER);
        filterDialog.add(buttonPanel, BorderLayout.SOUTH);
        filterDialog.setLocationRelativeTo(this);
        filterDialog.setVisible(true);
    }

    private void filterServices(String status) {
        List<ServiceDTO> allServices = serviceBus.getAll();
        List<ServiceDTO> results = new ArrayList<>();

        if ("Tất cả".equals(status)) {
            results = allServices;
        } else {
            boolean isActive = "Hoạt động".equals(status);
            for (ServiceDTO s : allServices) {
                if (s.isActive() == isActive) {
                    results.add(s);
                }
            }
        }

        updateTableView(results);
    }
}
