package GUI.billing.payment;

import BUS.PaymentBUS;
import BUS.InvoiceBUS;
import DTO.PaymentDTO;
import DTO.InvoiceDTO;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;

public class PaymentDetail extends JDialog {
    private PaymentDTO payment;
    private InvoiceDTO invoice;
    private PaymentBUS paymentBUS;
    private InvoiceBUS invoiceBUS;

    private JTextField bookingIdField;
    private JTextField invoiceNoField;
    private JTextField invoiceAmountField;
    private JTextField amountField;
    private JTextField methodField;
    private JTextField referenceNoField;
    private JTextField noteField;
    private JComboBox<String> statusCombo;

    public PaymentDetail(Frame parent, PaymentDTO payment, PaymentBUS paymentBUS, InvoiceDTO invoice) {
        super(parent, "Chi tiết thanh toán", true);
        this.payment = payment;
        this.invoice = invoice;
        this.paymentBUS = paymentBUS;
        this.invoiceBUS = new InvoiceBUS();
        initComponents();
        if (invoice != null) {
            loadInvoiceData();
        } else if (payment != null) {
            loadPaymentData();
        }
    }

    private void initComponents() {
        setSize(600, 550);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Booking ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Mã đặt phòng:"), gbc);
        bookingIdField = new JTextField(20);
        bookingIdField.setEditable(false);
        gbc.gridx = 1;
        mainPanel.add(bookingIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Số hóa đơn:"), gbc);
        invoiceNoField = new JTextField(20);
        invoiceNoField.setEditable(false);
        gbc.gridx = 1;
        mainPanel.add(invoiceNoField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Tổng tiền hóa đơn:"), gbc);
        invoiceAmountField = new JTextField(20);
        invoiceAmountField.setEditable(false);
        gbc.gridx = 1;
        mainPanel.add(invoiceAmountField, gbc);

        // Amount
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(new JLabel("Số tiền thanh toán:"), gbc);
        amountField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(amountField, gbc);

        // Method
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(new JLabel("Phương thức:"), gbc);
        methodField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(methodField, gbc);

        // Reference Number
        gbc.gridx = 0;
        gbc.gridy = 5;
        mainPanel.add(new JLabel("Mã tham chiếu:"), gbc);
        referenceNoField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(referenceNoField, gbc);

        // Note
        gbc.gridx = 0;
        gbc.gridy = 6;
        mainPanel.add(new JLabel("Ghi chú:"), gbc);
        noteField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(noteField, gbc);

        // Status
        gbc.gridx = 0;
        gbc.gridy = 7;
        mainPanel.add(new JLabel("Trạng thái:"), gbc);
        statusCombo = new JComboBox<>(new String[]{"Pending", "Completed", "Failed", "Cancelled"});
        gbc.gridx = 1;
        mainPanel.add(statusCombo, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton saveButton = new JButton("Thanh toán");
        saveButton.setBackground(new Color(52, 152, 219));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> savePayment());

        JButton cancelButton = new JButton("Hủy");
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadInvoiceData() {
        if (invoice != null) {
            bookingIdField.setText(String.valueOf(invoice.getBookingId()));
            invoiceNoField.setText(invoice.getInvoiceNo());
            invoiceAmountField.setText(String.format("%.2f", invoice.getGrandTotal()));
            amountField.setText(String.format("%.2f", invoice.getGrandTotal()));
        }
    }

    private void loadPaymentData() {
        if (payment != null) {
            bookingIdField.setText(String.valueOf(payment.getBookingId()));
            amountField.setText(String.format("%.2f", payment.getAmount()));
            methodField.setText(payment.getMethod() != null ? payment.getMethod() : "z");
            referenceNoField.setText(payment.getReferenceNo() != null ? payment.getReferenceNo() : "z");
            noteField.setText(payment.getNote() != null ? payment.getNote() : "z    ");
            statusCombo.setSelectedItem(payment.getStatus());

            if (payment.getInvoiceId() > 0) {
                invoice = invoiceBUS.getInvoiceById(payment.getInvoiceId());
            }
            // Nếu không có invoice_id → fallback dùng booking_id
            else if (invoice == null) {
                invoice = invoiceBUS.getInvoiceByBookingId(payment.getBookingId());
            }

            if (invoice != null) {
                invoiceNoField.setText(invoice.getInvoiceNo());
                invoiceAmountField.setText(String.format("%.2f", invoice.getGrandTotal()));
            } else {
                invoiceNoField.setText("Không tìm thấy");
                invoiceAmountField.setText("0.00");
            }
        }
    }

    private void savePayment() {
        try {
            if (bookingIdField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mã đặt phòng không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (amountField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Số tiền không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (payment == null) {
                payment = new PaymentDTO();
            }

            payment.setBookingId(Integer.parseInt(bookingIdField.getText()));
            payment.setAmount(Double.parseDouble(amountField.getText()));
            payment.setMethod(methodField.getText());
            payment.setReferenceNo(referenceNoField.getText());
            payment.setNote(noteField.getText());
            payment.setStatus((String) statusCombo.getSelectedItem());
            payment.setPaidAt(java.sql.Timestamp.valueOf(LocalDateTime.now()));

            boolean success;
            if (payment.getPaymentId() == 0) {
                success = paymentBUS.addPayment(payment);
            } else {
                success = paymentBUS.updatePayment(payment);
            }

            if (success) {
                if (invoice != null && "Completed".equals(payment.getStatus())) {
                    invoice.setStatus("Paid");
                    invoiceBUS.updateInvoice(invoice);
                }

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
