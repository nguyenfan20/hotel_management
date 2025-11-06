package GUI.service;

import BUS.ServiceOrderBUS;
import BUS.ServiceBUS;
import DTO.ServiceDTO;
import DTO.ServiceOrderDTO;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class ServiceOrdersform extends javax.swing.JPanel {
    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color PANEL_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(224, 224, 224);
    private static final Color TEXT_COLOR = new Color(33, 33, 33);
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);
    private static final Color DANGER_COLOR = new Color(239, 68, 68);

    private final ServiceOrderBUS serviceOrderBUS = new ServiceOrderBUS();
    private final ServiceBUS serviceBUS = new ServiceBUS();
    private JScrollPane scrollPane;
    private JTextField tfSearch;
    private JComboBox<String> cbSearch;

    public ServiceOrdersform() {
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
        JLabel lblTitle = new JLabel("QUẢN LÝ ĐƠN DỊCH VỤ");
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
                    searchServiceOrder();
                }
            }
        });

        // Search button
        JButton btnSearch = createIconButton("Tìm");
        btnSearch.setPreferredSize(new Dimension(35, 35));
        btnSearch.addActionListener(e -> searchServiceOrder());

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
        btnAdd.addActionListener(e -> addServiceOrder());
        rightPanel.add(btnAdd);

        controlPanel.add(leftPanel, BorderLayout.WEST);
        controlPanel.add(rightPanel, BorderLayout.EAST);

        add(controlPanel, BorderLayout.NORTH);

        // Table
        scrollPane = new JScrollPane();
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        add(scrollPane, BorderLayout.CENTER);

        updateTableView(new java.util.ArrayList<>());
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
        List<ServiceOrderDTO> orderList = serviceOrderBUS.getAll();
        updateTableView(orderList);
    }

    private void updateTableView(List<ServiceOrderDTO> data) {
        String[] columnNames = {"ID", "Mã đặt phòng", "Tên dịch vụ", "Số lượng", "Giá", "Tổng", "Thời gian đặt", "Ghi chú"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (ServiceOrderDTO order : data) {
            ServiceDTO service = serviceBUS.getById(order.getServiceId());
            String serviceName = (service != null) ? service.getName() : "Không rõ";
            double total = order.getUnitPrice() * order.getQuantity();

            model.addRow(new Object[]{
                    order.getServiceOrderId(),
                    order.getBookingRoomId(),
                    serviceName,
                    order.getQuantity(),
                    String.format("%.0f", order.getUnitPrice()),
                    String.format("%.0f", total),
                    order.getOrderedAt() != null ? order.getOrderedAt().toString() : "",
                    order.getNote() != null ? order.getNote() : ""
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
                    int orderId = (int) model.getValueAt(rowIndex, 0);
                    ServiceOrderDTO order = serviceOrderBUS.getById(orderId);

                    JPopupMenu contextMenu = new JPopupMenu();
                    JMenuItem editMenuItem = new JMenuItem("Sửa");
                    JMenuItem deleteMenuItem = new JMenuItem("Xóa");

                    editMenuItem.addActionListener(e1 -> {
                        table.setRowSelectionInterval(rowIndex, rowIndex);
                        editServiceOrder();
                    });
                    deleteMenuItem.addActionListener(e1 -> {
                        table.setRowSelectionInterval(rowIndex, rowIndex);
                        deleteServiceOrder();
                    });

                    contextMenu.add(editMenuItem);
                    contextMenu.add(deleteMenuItem);
                    contextMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        scrollPane.setViewportView(table);
    }

    private void addServiceOrder() {
        ServiceOrderDialog dialog = new ServiceOrderDialog(SwingUtilities.getWindowAncestor(this), true);
        ServiceOrderDTO newOrder = dialog.openForCreate();

        if (newOrder != null) {
            serviceOrderBUS.add(newOrder);
            loadTableData();
            JOptionPane.showMessageDialog(this, "Thêm đơn dịch vụ thành công!");
        }
    }

    private void editServiceOrder() {
        JTable table = (JTable) scrollPane.getViewport().getView();
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn dịch vụ cần sửa!");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int id = (int) model.getValueAt(row, 0);
        ServiceOrderDTO order = serviceOrderBUS.getById(id);
        if (order == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy đơn dịch vụ!");
            return;
        }

        ServiceOrderDialog dialog = new ServiceOrderDialog(SwingUtilities.getWindowAncestor(this), true);
        ServiceOrderDTO updatedOrder = dialog.openForEdit(order);

        if (updatedOrder != null) {
            boolean success = serviceOrderBUS.update(updatedOrder);
            if (success) {
                loadTableData();
                JOptionPane.showMessageDialog(this, "Cập nhật đơn dịch vụ thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
            }
        }
    }

    private void deleteServiceOrder() {
        JTable table = (JTable) scrollPane.getViewport().getView();
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn đơn dịch vụ cần xoá!");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Xoá đơn này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            serviceOrderBUS.delete(id);
            loadTableData();
        }
    }

    private void searchServiceOrder() {
        String keyword = tfSearch.getText().trim();
        String searchType = (String) cbSearch.getSelectedItem();

        if (keyword.isEmpty()) {
            loadTableData();
            return;
        }

        List<ServiceOrderDTO> results;

        try {
            switch (searchType) {
                case "Mã đặt phòng":
                    int bookingId = Integer.parseInt(keyword);
                    results = serviceOrderBUS.getAll().stream()
                            .filter(o -> o.getBookingRoomId() == bookingId)
                            .toList();
                    break;

                case "Tên dịch vụ":
                    results = serviceOrderBUS.searchByServiceName(keyword);
                    break;

                case "Số lượng":
                    int qty = Integer.parseInt(keyword);
                    results = serviceOrderBUS.getAll().stream()
                            .filter(o -> o.getQuantity() == qty)
                            .toList();
                    break;

                case "Ghi chú":
                    results = serviceOrderBUS.getAll().stream()
                            .filter(o -> o.getNote() != null && o.getNote().toLowerCase().contains(keyword.toLowerCase()))
                            .toList();
                    break;

                default:
                    results = serviceOrderBUS.getAll();
            }

            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả!");
                loadTableData();
                return;
            }

            updateTableView(results);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showFilterDialog() {
        JDialog filterDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Lọc đơn dịch vụ", true);
        filterDialog.setLayout(new BorderLayout());
        filterDialog.setSize(380, 220);

        JPanel contentPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        contentPanel.setBackground(PANEL_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel searchTypeLabel = new JLabel("Loại tìm kiếm:");
        searchTypeLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JComboBox<String> searchTypeCombo = new JComboBox<>(new String[]{"Mã đặt phòng", "Tên dịch vụ", "Số lượng", "Ghi chú"});
        searchTypeCombo.setPreferredSize(new Dimension(150, 30));
        contentPanel.add(searchTypeLabel);
        contentPanel.add(searchTypeCombo);

        JLabel keywordLabel = new JLabel("Từ khóa:");
        keywordLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField keywordField = new JTextField();
        keywordField.setFont(new Font("Arial", Font.PLAIN, 13));
        contentPanel.add(keywordLabel);
        contentPanel.add(keywordField);

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
            String searchType = (String) searchTypeCombo.getSelectedItem();
            String keyword = keywordField.getText();
            cbSearch.setSelectedItem(searchType);
            tfSearch.setText(keyword);
            searchServiceOrder();
            filterDialog.dispose();
        });
        buttonPanel.add(confirmButton);

        filterDialog.add(contentPanel, BorderLayout.CENTER);
        filterDialog.add(buttonPanel, BorderLayout.SOUTH);
        filterDialog.setLocationRelativeTo(this);
        filterDialog.setVisible(true);
    }
}
