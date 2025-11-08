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
    private JTextField statusField;  // Thay thế JComboBox bằng JTextField (read-only để hiển thị trạng thái hiện tại)

    public PaymentDetail(Frame parent, PaymentDTO payment, PaymentBUS paymentBUS, InvoiceDTO invoice) {
        super(parent, "Chi tiết thanh toán", true);
        this.payment = payment;
        this.invoice = invoice;
        this.paymentBUS = paymentBUS;
        this.invoiceBUS = new InvoiceBUS();
        initComponents();
        loadData();  // Gọi hàm load thống nhất
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

        // Status (thay bằng JTextField read-only)
        gbc.gridx = 0;
        gbc.gridy = 7;
        mainPanel.add(new JLabel("Trạng thái:"), gbc);
        statusField = new JTextField(20);
        statusField.setEditable(false);  // Không cho chỉnh sửa, chỉ hiển thị trạng thái hiện tại
        gbc.gridx = 1;
        mainPanel.add(statusField, gbc);

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

        // Tùy chỉnh button và fields dựa trên mode (new hoặc edit)
        if (payment != null) {
            saveButton.setText("Cập nhật");  // Khi edit, đổi thành "Cập nhật"
        }
    }

    private void loadData() {
        if (payment != null) {
            // Load từ payment (edit mode)
            bookingIdField.setText(String.valueOf(payment.getBookingId()));
            amountField.setText(String.format("%.2f", payment.getAmount()));
            methodField.setText(payment.getMethod() != null ? payment.getMethod() : "");
            referenceNoField.setText(payment.getReferenceNo() != null ? payment.getReferenceNo() : "");
            noteField.setText(payment.getNote() != null ? payment.getNote() : "");
            statusField.setText(payment.getStatus() != null ? payment.getStatus() : "");  // Lấy đúng trạng thái hiện tại

            // Truy xuất số hóa đơn và tổng tiền dựa trên invoice_id mà không load các thông tin khác
            int invoiceId = payment.getInvoiceId();
            if (invoiceId > 0) {
                InvoiceDTO tempInvoice = invoiceBUS.getInvoiceById(invoiceId);
                if (tempInvoice != null) {
                    invoiceNoField.setText(tempInvoice.getInvoiceNo());
                    invoiceAmountField.setText(String.format("%.2f", tempInvoice.getGrandTotal()));
                } else {
                    invoiceNoField.setText("Không tìm thấy");
                    invoiceAmountField.setText("0.00");
                }
            } else {
                // Fallback nếu không có invoice_id
                InvoiceDTO tempInvoice = invoiceBUS.getInvoiceByBookingId(payment.getBookingId());
                if (tempInvoice != null) {
                    invoiceNoField.setText(tempInvoice.getInvoiceNo());
                    invoiceAmountField.setText(String.format("%.2f", tempInvoice.getGrandTotal()));
                } else {
                    invoiceNoField.setText("Không tìm thấy");
                    invoiceAmountField.setText("0.00");
                }
            }
        } else if (invoice != null) {
            // Load từ invoice (new mode)
            bookingIdField.setText(String.valueOf(invoice.getBookingId()));
            amountField.setText(String.format("%.2f", invoice.getGrandTotal()));
            statusField.setText("Pending");  // Default cho new payment

            // Load thông tin invoice
            invoiceNoField.setText(invoice.getInvoiceNo());
            invoiceAmountField.setText(String.format("%.2f", invoice.getGrandTotal()));
        } else {
            invoiceNoField.setText("Không tìm thấy");
            invoiceAmountField.setText("0.00");
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
                payment.setStatus("Completed");  // Default cho new payment (vì không có combo để chọn)
            }

            payment.setBookingId(Integer.parseInt(bookingIdField.getText()));
            payment.setAmount(Double.parseDouble(amountField.getText()));
            payment.setMethod(methodField.getText());
            payment.setReferenceNo(referenceNoField.getText());
            payment.setNote(noteField.getText());
            // Status giữ nguyên từ hiện tại (vì field read-only)
            payment.setPaidAt(java.sql.Timestamp.valueOf(LocalDateTime.now()));

            boolean success;
            if (payment.getPaymentId() == 0) {
                success = paymentBUS.addPayment(payment);
            } else {
                success = paymentBUS.updatePayment(payment);
            }

            if (success) {
                // Load invoice chỉ khi cần cập nhật status
                if ("Completed".equals(payment.getStatus())) {
                    int invoiceId = payment.getInvoiceId();
                    if (invoice == null && invoiceId > 0) {
                        invoice = invoiceBUS.getInvoiceById(invoiceId);
                    } else if (invoice == null) {
                        invoice = invoiceBUS.getInvoiceByBookingId(payment.getBookingId());
                    }
                    if (invoice != null) {
                        invoice.setStatus("Paid");
                        invoiceBUS.updateInvoice(invoice);
                    }
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