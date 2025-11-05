package GUI.billing.payment;

import BUS.PaymentBUS;
import DTO.PaymentDTO;

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
    private JTextField searchField;
    private JComboBox<String> statusFilterCombo;
    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);

    public Payment() {
        paymentBUS = new PaymentBUS();
        initComponents();
        loadPaymentData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 240, 245));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(Color.WHITE);

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

        topPanel.add(new JLabel("Tìm kiếm:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(new JLabel("Trạng thái:"));
        topPanel.add(statusFilterCombo);
        topPanel.add(refreshButton);

        add(topPanel, BorderLayout.NORTH);

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
                    editPayment();
                }
            }
        });

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Sửa");
        JMenuItem deleteItem = new JMenuItem("Xóa");

        editItem.addActionListener(e -> editPayment());
        deleteItem.addActionListener(e -> deletePayment());

        popupMenu.add(editItem);
        popupMenu.add(deleteItem);

        paymentTable.setComponentPopupMenu(popupMenu);

        JScrollPane scrollPane = new JScrollPane(paymentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setBackground(Color.WHITE);

        JButton addButton = new JButton("Thêm thanh toán");
        addButton.setBackground(PRIMARY_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.setPreferredSize(new Dimension(150, 35));
        addButton.addActionListener(e -> addPayment());

        JButton qrPaymentButton = new JButton("Thanh toán QR");
        qrPaymentButton.setBackground(SUCCESS_COLOR);
        qrPaymentButton.setForeground(Color.WHITE);
        qrPaymentButton.setPreferredSize(new Dimension(150, 35));
        qrPaymentButton.addActionListener(e -> openQRPaymentDialog());

        JButton exportButton = new JButton("Xuất");
        exportButton.setBackground(new Color(149, 165, 166));
        exportButton.setForeground(Color.WHITE);
        exportButton.addActionListener(e -> exportData());

        JButton printButton = new JButton("In");
        printButton.setBackground(new Color(149, 165, 166));
        printButton.setForeground(Color.WHITE);
        printButton.addActionListener(e -> printData());

        bottomPanel.add(addButton);
        bottomPanel.add(qrPaymentButton);
        bottomPanel.add(exportButton);
        bottomPanel.add(printButton);
        add(bottomPanel, BorderLayout.SOUTH);
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
                    payment.getPaymentMethod() != null ? payment.getPaymentMethod() : "Tiền mặt", // Hiển thị phương thức thanh toán
                    payment.getPaymentDate() != null ? payment.getPaymentDate().toString() : "N/A",
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
                    payment.getPaymentMethod() != null ? payment.getPaymentMethod() : "Tiền mặt",
                    payment.getPaymentDate() != null ? payment.getPaymentDate().toString() : "N/A",
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
                    payment.getPaymentMethod() != null ? payment.getPaymentMethod() : "Tiền mặt",
                    payment.getPaymentDate() != null ? payment.getPaymentDate().toString() : "N/A",
                    payment.getNote() != null ? payment.getNote() : "",
                    payment.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void addPayment() {
        PaymentDetail detailDialog = new PaymentDetail(
                (Frame) SwingUtilities.getWindowAncestor(this),
                null,
                paymentBUS
        );
        detailDialog.setVisible(true);
        loadPaymentData();
    }

    private void openQRPaymentDialog() {
        JDialog qrDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thanh toán bằng QR", true);
        qrDialog.setSize(500, 450);
        qrDialog.setLayout(new BorderLayout(10, 10));

        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Quét mã QR để thanh toán");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        // QR Code placeholder
        JPanel qrPanel = new JPanel();
        qrPanel.setBackground(Color.WHITE);
        qrPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
        JLabel qrLabel = new JLabel("📱 QR Code");
        qrLabel.setHorizontalAlignment(SwingConstants.CENTER);
        qrLabel.setFont(new Font("Arial", Font.PLAIN, 50));
        qrPanel.add(qrLabel);
        contentPanel.add(qrPanel, BorderLayout.CENTER);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        statusPanel.setBackground(Color.WHITE);
        JLabel statusLabel = new JLabel("⏳ Chờ quét...");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12));
        statusLabel.setForeground(new Color(241, 196, 15));

        JButton confirmButton = new JButton("✓ Quét thành công");
        confirmButton.setBackground(SUCCESS_COLOR);
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setPreferredSize(new Dimension(150, 35));
        confirmButton.setFocusPainted(false);
        confirmButton.setBorderPainted(false);
        confirmButton.setFont(new Font("Arial", Font.BOLD, 12));
        confirmButton.addActionListener(e -> {
            statusLabel.setText("✓ Thanh toán thành công!");
            statusLabel.setForeground(SUCCESS_COLOR);
            JOptionPane.showMessageDialog(qrDialog, "Thanh toán QR thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            qrDialog.dispose();
            loadPaymentData();
        });

        statusPanel.add(statusLabel);
        statusPanel.add(confirmButton);

        qrDialog.add(contentPanel, BorderLayout.CENTER);
        qrDialog.add(statusPanel, BorderLayout.SOUTH);
        qrDialog.setLocationRelativeTo(this);
        qrDialog.setVisible(true);
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
                    paymentBUS
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

    private void exportData() {
        JOptionPane.showMessageDialog(this, "Chức năng xuất dữ liệu đang được phát triển!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void printData() {
        JOptionPane.showMessageDialog(this, "Chức năng in dữ liệu đang được phát triển!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
}
