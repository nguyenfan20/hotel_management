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
        searchButton.setBackground(new Color(52, 152, 219));
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

        String[] columnNames = {"Số hợp đồng", "Phiếu thu", "Số tiền phân bổ", "Ghi chú", "Quản lý dễ liễu"};
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
        addButton.setBackground(new Color(52, 152, 219));
        addButton.setForeground(Color.WHITE);
        addButton.setPreferredSize(new Dimension(150, 35));
        addButton.addActionListener(e -> addPayment());

        JButton exportButton = new JButton("Xuất");
        exportButton.setBackground(new Color(149, 165, 166));
        exportButton.setForeground(Color.WHITE);
        exportButton.addActionListener(e -> exportData());

        JButton printButton = new JButton("In");
        printButton.setBackground(new Color(149, 165, 166));
        printButton.setForeground(Color.WHITE);
        printButton.addActionListener(e -> printData());

        bottomPanel.add(addButton);
        bottomPanel.add(exportButton);
        bottomPanel.add(printButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadPaymentData() {
        tableModel.setRowCount(0);
        List<PaymentDTO> payments = paymentBUS.getAllPayments();

        for (PaymentDTO payment : payments) {
            Object[] row = {
                    payment.getBookingId(),
                    payment.getReferenceNo() != null ? payment.getReferenceNo() : "N/A",
                    String.format("%.2f", payment.getAmount()),
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
                    payment.getBookingId(),
                    payment.getReferenceNo() != null ? payment.getReferenceNo() : "N/A",
                    String.format("%.2f", payment.getAmount()),
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
                    payment.getBookingId(),
                    payment.getReferenceNo() != null ? payment.getReferenceNo() : "N/A",
                    String.format("%.2f", payment.getAmount()),
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

    private void editPayment() {
        int selectedRow = paymentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn thanh toán cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookingId = (int) tableModel.getValueAt(selectedRow, 0);
        PaymentDTO payment = paymentBUS.getPaymentsByBooking(bookingId).stream().findFirst().orElse(null);

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
            int bookingId = (int) tableModel.getValueAt(selectedRow, 0);
            PaymentDTO payment = paymentBUS.getPaymentsByBooking(bookingId).stream().findFirst().orElse(null);

            if (payment != null && paymentBUS.deletePayment(payment.getPaymentId())) {
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
