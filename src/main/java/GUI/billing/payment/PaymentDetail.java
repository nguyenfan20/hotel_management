package GUI.billing.payment;

import BUS.PaymentBUS;
import BUS.ServiceOrderBUS;
import BUS.InvoiceBUS;
import DTO.PaymentDTO;
import DTO.ServiceOrderDTO;
import DTO.InvoiceDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public class PaymentDetail extends JDialog {
    private PaymentDTO payment;
    private PaymentBUS paymentBUS;
    private ServiceOrderBUS serviceOrderBUS;
    private InvoiceBUS invoiceBUS;

    private JComboBox<String> bookingCombo;
    private JTable serviceTable;
    private DefaultTableModel serviceTableModel;

    private JTextField totalAmountField;
    private JTextField paidAmountField;
    private JTextField remainingAmountField;
    private JTextField discountField;
    private JTextField customerPayField;
    private JComboBox<String> accountCombo;

    private JLabel qrCodeLabel;

    public PaymentDetail(Frame parent, PaymentDTO payment, PaymentBUS paymentBUS) {
        super(parent, "Chi tiết thanh toán", true);
        this.payment = payment;
        this.paymentBUS = paymentBUS;
        this.serviceOrderBUS = new ServiceOrderBUS();
        this.invoiceBUS = new InvoiceBUS();
        initComponents();
        if (payment != null) {
            loadPaymentData();
        }
    }

    private void initComponents() {
        setSize(1000, 700);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        topPanel.add(new JLabel("Hợp đồng:"), gbc);
        bookingCombo = new JComboBox<>();
        bookingCombo.addActionListener(e -> loadServicesByBooking());
        gbc.gridx = 1;
        topPanel.add(bookingCombo, gbc);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel middlePanel = new JPanel(new BorderLayout(10, 10));
        middlePanel.setBackground(Color.WHITE);

        String[] columnNames = {"STT", "Mô tả", "Đơn vị", "Đơn giá", "Số lượng", "VAT", "Thành tiền"};
        serviceTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        serviceTable = new JTable(serviceTableModel);
        serviceTable.setRowHeight(25);
        serviceTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(serviceTable);
        middlePanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(middlePanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Thông tin thanh toán"));
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1: Amount fields
        gbc.gridx = 0;
        gbc.gridy = 0;
        bottomPanel.add(new JLabel("Số tiền phải thanh toán:"), gbc);
        totalAmountField = new JTextField(15);
        totalAmountField.setEditable(false);
        gbc.gridx = 1;
        bottomPanel.add(totalAmountField, gbc);

        gbc.gridx = 2;
        bottomPanel.add(new JLabel("Số tiền đã thanh toán:"), gbc);
        paidAmountField = new JTextField(15);
        paidAmountField.setEditable(false);
        gbc.gridx = 3;
        bottomPanel.add(paidAmountField, gbc);

        // Row 2: Remaining and discount
        gbc.gridx = 0;
        gbc.gridy = 1;
        bottomPanel.add(new JLabel("Số còn lại:"), gbc);
        remainingAmountField = new JTextField(15);
        remainingAmountField.setEditable(false);
        gbc.gridx = 1;
        bottomPanel.add(remainingAmountField, gbc);

        gbc.gridx = 2;
        bottomPanel.add(new JLabel("Giảm giá:"), gbc);
        discountField = new JTextField(15);
        discountField.setText("0");
        gbc.gridx = 3;
        bottomPanel.add(discountField, gbc);

        // Row 3: Customer payment and account
        gbc.gridx = 0;
        gbc.gridy = 2;
        bottomPanel.add(new JLabel("Số tiền khách trả:"), gbc);
        customerPayField = new JTextField(15);
        gbc.gridx = 1;
        bottomPanel.add(customerPayField, gbc);

        gbc.gridx = 2;
        bottomPanel.add(new JLabel("Tài khoản thu:"), gbc);
        accountCombo = new JComboBox<>(new String[]{"---", "Agribank", "Vietcombank", "Techcombank"});
        gbc.gridx = 3;
        bottomPanel.add(accountCombo, gbc);

        // Row 4: QR Code section
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        JPanel qrPanel = new JPanel(new BorderLayout());
        qrPanel.setBorder(BorderFactory.createTitledBorder("Thanh toán QR Code"));
        qrCodeLabel = new JLabel("Chưa có mã QR");
        qrCodeLabel.setHorizontalAlignment(JLabel.CENTER);
        qrCodeLabel.setPreferredSize(new Dimension(150, 150));
        qrCodeLabel.setBackground(Color.LIGHT_GRAY);
        qrCodeLabel.setOpaque(true);
        qrPanel.add(qrCodeLabel, BorderLayout.CENTER);

        JButton scanQRButton = new JButton("Quét mã QR");
        scanQRButton.setBackground(new Color(155, 89, 182));
        scanQRButton.setForeground(Color.WHITE);
        scanQRButton.addActionListener(e -> scanQRCode());
        qrPanel.add(scanQRButton, BorderLayout.SOUTH);

        bottomPanel.add(qrPanel, gbc);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton paymentButton = new JButton("Thanh toán");
        paymentButton.setBackground(new Color(52, 152, 219));
        paymentButton.setForeground(Color.WHITE);
        paymentButton.setPreferredSize(new Dimension(100, 35));
        paymentButton.addActionListener(e -> savePayment());

        JButton cancelButton = new JButton("Hủy");
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(paymentButton);
        buttonPanel.add(cancelButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadServicesByBooking() {
        String selectedBooking = (String) bookingCombo.getSelectedItem();
        if (selectedBooking == null || selectedBooking.isEmpty()) {
            return;
        }

        serviceTableModel.setRowCount(0);
        int bookingId = Integer.parseInt(selectedBooking.split(" - ")[0]);
        List<ServiceOrderDTO> serviceOrders = serviceOrderBUS.getServiceOrdersByBooking(bookingId);

        double totalAmount = 0;
        int stt = 1;
        for (ServiceOrderDTO order : serviceOrders) {
            double lineTotal = order.getUnitPrice() * order.getQuantity();
            totalAmount += lineTotal;

            // Note: getServiceName() and getUnit() are not available in ServiceOrderDTO.
            // Assuming these are fetched via a join or ServiceDAO in a real implementation.
            // Using placeholders for now.
            String serviceName = "Service " + order.getServiceId(); // Placeholder
            String unit = "Unit"; // Placeholder

            Object[] row = {
                    stt++,
                    serviceName,
                    unit,
                    String.format("%.2f", order.getUnitPrice()),
                    order.getQuantity(),
                    "0",
                    String.format("%.2f", lineTotal)
            };
            serviceTableModel.addRow(row);
        }

        totalAmountField.setText(String.format("%.2f", totalAmount));
        remainingAmountField.setText(String.format("%.2f", totalAmount));
    }

    private void loadPaymentData() {
        if (payment != null) {
            bookingCombo.setSelectedItem(String.valueOf(payment.getBookingId()));
            customerPayField.setText(String.format("%.2f", payment.getAmount()));
            accountCombo.setSelectedItem(payment.getMethod() != null ? payment.getMethod() : "---");
        }
    }

    private void scanQRCode() {
        String qrData = JOptionPane.showInputDialog(this, "Nhập dữ liệu QR Code:", "");
        if (qrData != null && !qrData.isEmpty()) {
            qrCodeLabel.setText("✓ QR Code đã quét thành công");
            qrCodeLabel.setBackground(new Color(46, 204, 113));
            qrCodeLabel.setForeground(Color.WHITE);
            JOptionPane.showMessageDialog(this, "Thanh toán QR Code thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void savePayment() {
        try {
            if (bookingCombo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn hợp đồng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (customerPayField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Số tiền khách trả không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (payment == null) {
                payment = new PaymentDTO();
            }

            int bookingId = Integer.parseInt(((String) bookingCombo.getSelectedItem()).split(" - ")[0]);
            payment.setBookingId(bookingId);
            payment.setAmount(Double.parseDouble(customerPayField.getText()));
            payment.setMethod((String) accountCombo.getSelectedItem());
            payment.setStatus("Completed");
            payment.setPaidAt(java.sql.Timestamp.valueOf(LocalDateTime.now()));

            boolean success;
            if (payment.getPaymentId() == 0) {
                success = paymentBUS.addPayment(payment);
            } else {
                success = paymentBUS.updatePayment(payment);
            }

            if (success) {
                JOptionPane.showMessageDialog(this, "Thanh toán thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Thanh toán thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}