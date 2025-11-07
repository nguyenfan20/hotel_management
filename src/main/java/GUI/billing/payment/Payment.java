package GUI.billing.payment;

import BUS.PaymentBUS;
import BUS.InvoiceBUS;
import DTO.PaymentDTO;
import DTO.InvoiceDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class Payment extends JPanel {
    private JTable paymentTable;
    private DefaultTableModel tableModel;
    private PaymentBUS paymentBUS;
    private InvoiceBUS invoiceBUS;
    private JTextField searchField;
    private JComboBox<String> statusFilterCombo;
    private JComboBox<String> invoiceCombo;
    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);

    public Payment() {
        paymentBUS = new PaymentBUS();
        invoiceBUS = new InvoiceBUS();
        initComponents();
        loadPaymentData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 240, 245));

        JPanel invoicePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        invoicePanel.setBackground(Color.WHITE);
        invoicePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        invoicePanel.add(new JLabel("Hóa đơn chưa thanh toán:"));
        invoiceCombo = new JComboBox<>();
        loadUnpaidInvoices();
        invoiceCombo.setPreferredSize(new Dimension(350, 30));
        invoicePanel.add(invoiceCombo);

        JButton payButton = new JButton("Thanh toán");
        payButton.setBackground(PRIMARY_COLOR);
        payButton.setForeground(Color.WHITE);
        payButton.setPreferredSize(new Dimension(100, 30));
        payButton.addActionListener(e -> openPaymentDialog());
        invoicePanel.add(payButton);

        JButton refreshInvoiceBtn = new JButton("Làm mới");
        refreshInvoiceBtn.setBackground(new Color(149, 165, 166));
        refreshInvoiceBtn.setForeground(Color.WHITE);
        refreshInvoiceBtn.addActionListener(e -> loadUnpaidInvoices());
        invoicePanel.add(refreshInvoiceBtn);

        add(invoicePanel, BorderLayout.NORTH);

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createTitledBorder("Lịch sử thanh toán"));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(Color.WHITE);

        searchField = new JTextField(20);
        JButton searchButton = new JButton("Tìm kiếm");
        searchButton.setBackground(PRIMARY_COLOR);
        searchButton.setForeground(Color.WHITE);
        searchButton.addActionListener(e -> searchPayments());

        statusFilterCombo = new JComboBox<>(new String[]{"Tất cả", "Pending", "Completed", "Failed", "Cancelled"});
        statusFilterCombo.addActionListener(e -> filterByStatus());

        JButton refreshButton = new JButton("Làm mới");
        refreshButton.setBackground(new Color(149, 165, 166));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(e -> loadPaymentData());

        searchPanel.add(new JLabel("Tìm kiếm:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(new JLabel("Trạng thái:"));
        searchPanel.add(statusFilterCombo);
        searchPanel.add(refreshButton);

        topPanel.add(searchPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.SOUTH);

        String[] columnNames = {"ID", "Mã đặt phòng", "Phiếu thu", "Số tiền", "Phương thức", "Ngày thanh toán", "Ghi chú", "Trạng thái"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        paymentTable = new JTable(tableModel);
        paymentTable.setRowHeight(30);
        paymentTable.getTableHeader().setReorderingAllowed(false);
        paymentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        paymentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewPaymentDetail();
                }
            }
        });

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem viewItem = new JMenuItem("Xem chi tiết");
        JMenuItem editItem = new JMenuItem("Sửa");
        JMenuItem deleteItem = new JMenuItem("Xóa");

        viewItem.addActionListener(e -> viewPaymentDetail());
        editItem.addActionListener(e -> editPayment());
        deleteItem.addActionListener(e -> deletePayment());

        popupMenu.add(viewItem);
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);

        paymentTable.setComponentPopupMenu(popupMenu);

        JScrollPane scrollPane = new JScrollPane(paymentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadUnpaidInvoices() {
        invoiceCombo.removeAllItems();
        List<InvoiceDTO> unpaidInvoices = invoiceBUS.getUnpaidInvoices();

        if (unpaidInvoices.isEmpty()) {
            invoiceCombo.addItem("Không có hóa đơn chưa thanh toán");
        } else {
            for (InvoiceDTO invoice : unpaidInvoices) {
                invoiceCombo.addItem(invoice.getInvoiceId() + " | " + invoice.getInvoiceNo() +
                        " | " + String.format("%.2f", invoice.getGrandTotal()) + " VNĐ");
            }
        }
    }

    private void openPaymentDialog() {
        String selected = (String) invoiceCombo.getSelectedItem();
        if (selected == null || selected.contains("Không có")) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần thanh toán!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int invoiceId = Integer.parseInt(selected.split("\\|")[0].trim());
        InvoiceDTO invoice = invoiceBUS.getInvoiceById(invoiceId);

        if (invoice != null) {
            PaymentDetail detailDialog = new PaymentDetail(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    null,
                    paymentBUS,
                    invoice
            );
            detailDialog.setVisible(true);
            loadPaymentData();
            loadUnpaidInvoices();
        }
    }

    private void loadPaymentData() {
        tableModel.setRowCount(0);
        List<PaymentDTO> payments = paymentBUS.getAllPayments();

        for (PaymentDTO payment : payments) {
            Object[] row = {
                    payment.getPaymentId(),
                    payment.getBookingId(),
                    payment.getReferenceNo() != null ? payment.getReferenceNo() : "N/A",
                    String.format("%.2f", payment.getAmount()),
                    payment.getMethod() != null ? payment.getMethod() : "N/A",
                    payment.getPaidAt() != null ? payment.getPaidAt().toString() : "N/A",
                    payment.getNote() != null ? payment.getNote() : "",
                    payment.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void searchPayments() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadPaymentData();
            return;
        }

        tableModel.setRowCount(0);
        List<PaymentDTO> payments = paymentBUS.searchPayments(keyword);

        for (PaymentDTO payment : payments) {
            Object[] row = {
                    payment.getPaymentId(),
                    payment.getBookingId(),
                    payment.getReferenceNo() != null ? payment.getReferenceNo() : "N/A",
                    String.format("%.2f", payment.getAmount()),
                    payment.getMethod() != null ? payment.getMethod() : "N/A",
                    payment.getPaidAt() != null ? payment.getPaidAt().toString() : "N/A",
                    payment.getNote() != null ? payment.getNote() : "",
                    payment.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void filterByStatus() {
        String status = (String) statusFilterCombo.getSelectedItem();
        if (status.equals("Tất cả")) {
            loadPaymentData();
            return;
        }

        tableModel.setRowCount(0);
        List<PaymentDTO> payments = paymentBUS.filterPaymentsByStatus(status);

        for (PaymentDTO payment : payments) {
            Object[] row = {
                    payment.getPaymentId(),
                    payment.getBookingId(),
                    payment.getReferenceNo() != null ? payment.getReferenceNo() : "N/A",
                    String.format("%.2f", payment.getAmount()),
                    payment.getMethod() != null ? payment.getMethod() : "N/A",
                    payment.getPaidAt() != null ? payment.getPaidAt().toString() : "N/A",
                    payment.getNote() != null ? payment.getNote() : "",
                    payment.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void viewPaymentDetail() {
        int selectedRow = paymentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thanh toán cần xem!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int paymentId = (int) tableModel.getValueAt(selectedRow, 0);
        PaymentDTO payment = paymentBUS.getPaymentById(paymentId);

        if (payment != null) {
            PaymentDetail detailDialog = new PaymentDetail(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    payment,
                    paymentBUS,
                    null
            );
            detailDialog.setVisible(true);
            loadPaymentData();
        }
    }

    private void editPayment() {
        int selectedRow = paymentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thanh toán cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int paymentId = (int) tableModel.getValueAt(selectedRow, 0);
        PaymentDTO payment = paymentBUS.getPaymentById(paymentId);

        if (payment != null) {
            PaymentDetail detailDialog = new PaymentDetail(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    payment,
                    paymentBUS,
                    null
            );
            detailDialog.setVisible(true);
            loadPaymentData();
        }
    }

    private void deletePayment() {
        int selectedRow = paymentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thanh toán cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa thanh toán này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int paymentId = (int) tableModel.getValueAt(selectedRow, 0);
            if (paymentBUS.deletePayment(paymentId)) {
                JOptionPane.showMessageDialog(this, "Xóa thanh toán thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadPaymentData();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thanh toán thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
