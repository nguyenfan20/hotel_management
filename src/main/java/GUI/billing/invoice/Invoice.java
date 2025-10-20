package GUI.billing.invoice;

import BUS.InvoiceBUS;
import DTO.InvoiceDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class Invoice extends JPanel {
    private JTable invoiceTable;
    private DefaultTableModel tableModel;
    private InvoiceBUS invoiceBUS;
    private JTextField searchField;
    private JComboBox<String> statusFilterCombo;

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

        statusFilterCombo = new JComboBox<>(new String[]{"Tất cả", "Draft", "Issued", "Paid", "Cancelled"});
        statusFilterCombo.addActionListener(e -> filterByStatus());

        JButton refreshButton = new JButton("Làm mới");
        refreshButton.setBackground(new Color(149, 165, 166));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(e -> loadInvoiceData());

        topPanel.add(new JLabel("Tìm kiếm:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(new JLabel("Trạng thái:"));
        topPanel.add(statusFilterCombo);
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
        JMenuItem editItem = new JMenuItem("Sửa");
        JMenuItem deleteItem = new JMenuItem("Xóa");

        editItem.addActionListener(e -> editInvoice());
        deleteItem.addActionListener(e -> deleteInvoice());

        popupMenu.add(editItem);
        popupMenu.add(deleteItem);

        invoiceTable.setComponentPopupMenu(popupMenu);

        JScrollPane scrollPane = new JScrollPane(invoiceTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel - Action Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setBackground(Color.WHITE);

        JButton addButton = new JButton("Thêm hóa đơn");
        addButton.setBackground(new Color(52, 152, 219));
        addButton.setForeground(Color.WHITE);
        addButton.setPreferredSize(new Dimension(150, 35));
        addButton.addActionListener(e -> addInvoice());

        bottomPanel.add(addButton);
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

    private void filterByStatus() {
        String status = (String) statusFilterCombo.getSelectedItem();
        if (status.equals("Tất cả")) {
            loadInvoiceData();
            return;
        }

        tableModel.setRowCount(0);
        List<InvoiceDTO> invoices = invoiceBUS.filterInvoicesByStatus(status);

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

    private void addInvoice() {
        InvoiceDetail detailDialog = new InvoiceDetail(
                (Frame) SwingUtilities.getWindowAncestor(this),
                null,
                invoiceBUS
        );
        detailDialog.setVisible(true);
        loadInvoiceData();
    }

    private void editInvoice() {
        int selectedRow = invoiceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
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

