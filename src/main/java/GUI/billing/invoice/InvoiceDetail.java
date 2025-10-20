package GUI.billing.invoice;

import BUS.InvoiceBUS;
import DTO.InvoiceDTO;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class InvoiceDetail extends JDialog {
    private InvoiceDTO invoice;
    private InvoiceBUS invoiceBUS;
    private JTextField invoiceNoField;
    private JTextField bookingIdField;
    private JTextField subtotalField;
    private JTextField discountTotalField;
    private JTextField taxTotalField;
    private JTextField grandTotalField;
    private JComboBox<String> statusCombo;

    public InvoiceDetail(Frame parent, InvoiceDTO invoice, InvoiceBUS invoiceBUS) {
        super(parent, "Chi tiết hóa đơn", true);
        this.invoice = invoice;
        this.invoiceBUS = invoiceBUS;
        initComponents();
        if (invoice != null) {
            loadInvoiceData();
        }
    }

    private void initComponents() {
        setSize(600, 450);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Số hóa đơn
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Số hóa đơn:"), gbc);
        invoiceNoField = new JTextField(25);
        gbc.gridx = 1;
        mainPanel.add(invoiceNoField, gbc);

        // Đặt phòng
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("ID Đặt phòng:"), gbc);
        bookingIdField = new JTextField(25);
        gbc.gridx = 1;
        mainPanel.add(bookingIdField, gbc);

        // Tổng phụ
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Tổng phụ:"), gbc);
        subtotalField = new JTextField(25);
        gbc.gridx = 1;
        mainPanel.add(subtotalField, gbc);

        // Chiết khấu
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(new JLabel("Chiết khấu:"), gbc);
        discountTotalField = new JTextField(25);
        gbc.gridx = 1;
        mainPanel.add(discountTotalField, gbc);

        // Thuế
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(new JLabel("Thuế:"), gbc);
        taxTotalField = new JTextField(25);
        gbc.gridx = 1;
        mainPanel.add(taxTotalField, gbc);

        // Tổng cộng
        gbc.gridx = 0;
        gbc.gridy = 5;
        mainPanel.add(new JLabel("Tổng cộng:"), gbc);
        grandTotalField = new JTextField(25);
        gbc.gridx = 1;
        mainPanel.add(grandTotalField, gbc);

        // Trạng thái
        gbc.gridx = 0;
        gbc.gridy = 6;
        mainPanel.add(new JLabel("Trạng thái:"), gbc);
        statusCombo = new JComboBox<>(new String[]{"Draft", "Issued", "Paid", "Cancelled"});
        gbc.gridx = 1;
        mainPanel.add(statusCombo, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton saveButton = new JButton("Lưu");
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> saveInvoice());

        JButton cancelButton = new JButton("Hủy");
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadInvoiceData() {
        invoiceNoField.setText(invoice.getInvoiceNo());
        bookingIdField.setText(String.valueOf(invoice.getBookingId()));
        subtotalField.setText(String.format("%.2f", invoice.getSubtotal()));
        discountTotalField.setText(String.format("%.2f", invoice.getDiscountTotal()));
        taxTotalField.setText(String.format("%.2f", invoice.getTaxTotal()));
        grandTotalField.setText(String.format("%.2f", invoice.getGrandTotal()));
        statusCombo.setSelectedItem(invoice.getStatus());
    }

    private void saveInvoice() {
        try {
            if (invoiceNoField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Số hóa đơn không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (invoice == null) {
                invoice = new InvoiceDTO();
            }

            invoice.setInvoiceNo(invoiceNoField.getText().trim());
            invoice.setBookingId(Integer.parseInt(bookingIdField.getText()));
            invoice.setSubtotal(Double.parseDouble(subtotalField.getText()));
            invoice.setDiscountTotal(Double.parseDouble(discountTotalField.getText()));
            invoice.setTaxTotal(Double.parseDouble(taxTotalField.getText()));
            invoice.setGrandTotal(Double.parseDouble(grandTotalField.getText()));
            invoice.setStatus((String) statusCombo.getSelectedItem());

            boolean success;
            if (invoice.getInvoiceId() == 0) {
                invoice.setCreatedAt(java.sql.Timestamp.valueOf(LocalDateTime.now()));
                success = invoiceBUS.addInvoice(invoice);
            } else {
                success = invoiceBUS.updateInvoice(invoice);
            }

            if (success) {
                JOptionPane.showMessageDialog(this, "Lưu hóa đơn thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lưu hóa đơn thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

