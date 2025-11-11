package GUI.billing.invoice;

import BUS.InvoiceBUS;
import DTO.InvoiceDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import com.toedter.calendar.JDateChooser;
import java.util.Date;
import java.util.stream.Collectors;

public class Invoice extends JPanel {
    private JTable invoiceTable;
    private DefaultTableModel tableModel;
    private InvoiceBUS invoiceBUS;
    private JTextField searchField;

    public Invoice() {
        invoiceBUS = new InvoiceBUS();
        initComponents();
        loadInvoiceData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 240, 245));

        // Top Panel - Search and Filter
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(Color.WHITE);

        searchField = new JTextField(20);
        JButton searchButton = new JButton("Tìm kiếm");
        searchButton.setBackground(new Color(52, 152, 219));
        searchButton.setForeground(Color.WHITE);
        searchButton.addActionListener(e -> searchInvoices());

        JButton filterButton = new JButton("Lọc");
        filterButton.setBackground(new Color(46, 204, 113)); // Màu xanh lá cây cho lọc
        filterButton.setForeground(Color.WHITE);
        filterButton.addActionListener(e -> showFilterDialog());

        JButton refreshButton = new JButton("Làm mới");
        refreshButton.setBackground(new Color(149, 165, 166));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(e -> loadInvoiceData());

        topPanel.add(new JLabel("Tìm kiếm:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(filterButton);
        topPanel.add(refreshButton);

        add(topPanel, BorderLayout.NORTH);

        // Center Panel - Table
        String[] columnNames = {"ID", "Số HĐ", "Đặt phòng", "Tổng phụ", "Chiết khấu", "Thuế", "Tổng cộng", "Ngày tạo", "Trạng thái"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        invoiceTable = new JTable(tableModel);
        invoiceTable.setRowHeight(30);
        invoiceTable.getTableHeader().setReorderingAllowed(false);
        invoiceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        invoiceTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editInvoice();
                }
            }
        });

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Xem chi tiết");
        JMenuItem deleteItem = new JMenuItem("Xóa");

        editItem.addActionListener(e -> editInvoice());
        deleteItem.addActionListener(e -> deleteInvoice());

        popupMenu.add(editItem);
        popupMenu.add(deleteItem);

        invoiceTable.setComponentPopupMenu(popupMenu);

        JScrollPane scrollPane = new JScrollPane(invoiceTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setBackground(Color.WHITE);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadInvoiceData() {
        tableModel.setRowCount(0);
        List<InvoiceDTO> invoices = invoiceBUS.getAllInvoices();

        for (InvoiceDTO invoice : invoices) {
            Object[] row = {
                    invoice.getInvoiceId(),
                    invoice.getInvoiceNo(),
                    invoice.getBookingId(),
                    String.format("%.2f", invoice.getSubtotal()),
                    String.format("%.2f", invoice.getDiscountTotal()),
                    String.format("%.2f", invoice.getTaxTotal()),
                    String.format("%.2f", invoice.getGrandTotal()),
                    invoice.getCreatedAt(),
                    invoice.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void searchInvoices() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadInvoiceData();
            return;
        }

        tableModel.setRowCount(0);
        List<InvoiceDTO> invoices = invoiceBUS.searchInvoices(keyword);

        for (InvoiceDTO invoice : invoices) {
            Object[] row = {
                    invoice.getInvoiceId(),
                    invoice.getInvoiceNo(),
                    invoice.getBookingId(),
                    String.format("%.2f", invoice.getSubtotal()),
                    String.format("%.2f", invoice.getDiscountTotal()),
                    String.format("%.2f", invoice.getTaxTotal()),
                    String.format("%.2f", invoice.getGrandTotal()),
                    invoice.getCreatedAt(),
                    invoice.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void showFilterDialog() {
        JDialog filterDialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Tiêu chí Lọc Hóa đơn",
                true
        );
        filterDialog.setLayout(new BorderLayout(10, 10));
        filterDialog.setSize(400, 250);
        filterDialog.setLocationRelativeTo(this);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. Lọc theo Trạng thái
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Tất cả", "Draft", "Issued", "Paid", "Cancelled"});

        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; inputPanel.add(statusCombo, gbc);

        // 2. Lọc theo Khoảng thời gian (Ngày tạo hóa đơn)
        JDateChooser dateFromChooser = new JDateChooser();
        dateFromChooser.setDateFormatString("dd/MM/yyyy");

        JDateChooser dateToChooser = new JDateChooser();
        dateToChooser.setDateFormatString("dd/MM/yyyy");

        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("Từ ngày:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; inputPanel.add(dateFromChooser, gbc);

        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(new JLabel("Đến ngày:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; inputPanel.add(dateToChooser, gbc);

        filterDialog.add(inputPanel, BorderLayout.CENTER);

        // Nút Áp dụng và Hủy
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton applyButton = new JButton("Áp dụng");
        applyButton.setBackground(new Color(52, 152, 219));
        applyButton.setForeground(Color.WHITE);

        JButton cancelButton = new JButton("Hủy");

        // --- LOGIC KHI ÁP DỤNG LỌC ---
        applyButton.addActionListener(e -> {
            String selectedStatus = (String) statusCombo.getSelectedItem();
            Date dateFrom = dateFromChooser.getDate();
            Date dateTo = dateToChooser.getDate();

            filterInvoices(selectedStatus, dateFrom, dateTo);
            filterDialog.dispose();
        });

        cancelButton.addActionListener(e -> filterDialog.dispose());

        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);
        filterDialog.add(buttonPanel, BorderLayout.SOUTH);

        filterDialog.setVisible(true);
    }

    private void filterInvoices(String status, Date dateFrom, Date dateTo) {
        tableModel.setRowCount(0);
        List<InvoiceDTO> allInvoices = invoiceBUS.getAllInvoices();

        // Lọc theo Status và Date Range
        List<InvoiceDTO> filteredInvoices = allInvoices.stream()
                .filter(invoice -> {
                    boolean statusMatch = status.equals("Tất cả") || invoice.getStatus().equalsIgnoreCase(status);

                    // Chuyển đổi LocalDateTime (invoice.getCreatedAt()) sang Date để so sánh
                    Date createdAtDate = Date.from(invoice.getCreatedAt().toInstant());

                    boolean dateFromMatch = (dateFrom == null) || createdAtDate.after(dateFrom);
                    boolean dateToMatch = (dateTo == null) || createdAtDate.before(dateTo);

                    // Do DateChooser trả về 00:00:00 của ngày, cần xử lý để bao gồm cả ngày cuối
                    if (dateTo != null) {
                        // Tăng ngày "Đến" lên 1 ngày để bao gồm tất cả hóa đơn trong ngày đó
                        java.util.Calendar cal = java.util.Calendar.getInstance();
                        cal.setTime(dateTo);
                        cal.add(java.util.Calendar.DAY_OF_MONTH, 1);
                        dateToMatch = createdAtDate.before(cal.getTime());
                    }

                    return statusMatch && dateFromMatch && dateToMatch;
                })
                .collect(Collectors.toList());

        for (InvoiceDTO invoice : filteredInvoices) {
            Object[] row = {
                    invoice.getInvoiceId(),
                    invoice.getInvoiceNo(),
                    invoice.getBookingId(),
                    String.format("%.2f", invoice.getSubtotal()),
                    String.format("%.2f", invoice.getDiscountTotal()),
                    String.format("%.2f", invoice.getTaxTotal()),
                    String.format("%.2f", invoice.getGrandTotal()),
                    invoice.getCreatedAt(),
                    invoice.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void editInvoice() {
        int selectedRow = invoiceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần xem!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int invoiceId = (int) tableModel.getValueAt(selectedRow, 0);
        InvoiceDTO invoice = invoiceBUS.getInvoiceById(invoiceId);

        if (invoice != null) {
            InvoiceDetail detailDialog = new InvoiceDetail(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    invoice,
                    invoiceBUS
            );
            detailDialog.setVisible(true);
            loadInvoiceData();
        }
    }

    private void deleteInvoice() {
        int selectedRow = invoiceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa hóa đơn này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int invoiceId = (int) tableModel.getValueAt(selectedRow, 0);
            if (invoiceBUS.deleteInvoice(invoiceId)) {
                JOptionPane.showMessageDialog(this, "Xóa hóa đơn thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadInvoiceData();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa hóa đơn thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}