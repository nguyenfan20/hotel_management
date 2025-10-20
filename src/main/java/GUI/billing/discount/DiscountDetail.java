package GUI.billing.discount;

import BUS.DiscountBUS;
import DTO.DiscountDTO;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;

public class DiscountDetail extends JDialog {
    private DiscountDTO discount;
    private DiscountBUS discountBUS;
    private JTextField codeField;
    private JComboBox<String> typeCombo;
    private JTextField valueField;
    private JTextField minSpendField;
    private JTextField maxDiscountField;
    private JTextField startDateField;
    private JTextField expiryDateField;
    private JTextField usageLimitField;
    private JComboBox<String> statusCombo;

    public DiscountDetail(Frame parent, DiscountDTO discount, DiscountBUS discountBUS) {
        super(parent, "Chi tiết chiết khấu", true);
        this.discount = discount;
        this.discountBUS = discountBUS;
        initComponents();
        if (discount != null) {
            loadDiscountData();
        }
    }

    private void initComponents() {
        setSize(600, 500);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Mã chiết khấu
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Mã chiết khấu:"), gbc);
        codeField = new JTextField(25);
        gbc.gridx = 1;
        mainPanel.add(codeField, gbc);

        // Loại chiết khấu
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Loại chiết khấu:"), gbc);
        typeCombo = new JComboBox<>(new String[]{"Percentage", "Fixed Amount"});
        gbc.gridx = 1;
        mainPanel.add(typeCombo, gbc);

        // Giá trị
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Giá trị:"), gbc);
        valueField = new JTextField(25);
        gbc.gridx = 1;
        mainPanel.add(valueField, gbc);

        // Chi tiêu tối thiểu
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(new JLabel("Chi tiêu tối thiểu:"), gbc);
        minSpendField = new JTextField(25);
        gbc.gridx = 1;
        mainPanel.add(minSpendField, gbc);

        // Chiết khấu tối đa
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(new JLabel("Chiết khấu tối đa:"), gbc);
        maxDiscountField = new JTextField(25);
        gbc.gridx = 1;
        mainPanel.add(maxDiscountField, gbc);

        // Ngày bắt đầu
        gbc.gridx = 0;
        gbc.gridy = 5;
        mainPanel.add(new JLabel("Ngày bắt đầu (yyyy-MM-dd):"), gbc);
        startDateField = new JTextField(25);
        gbc.gridx = 1;
        mainPanel.add(startDateField, gbc);

        // Ngày kết thúc
        gbc.gridx = 0;
        gbc.gridy = 6;
        mainPanel.add(new JLabel("Ngày kết thúc (yyyy-MM-dd):"), gbc);
        expiryDateField = new JTextField(25);
        gbc.gridx = 1;
        mainPanel.add(expiryDateField, gbc);

        // Giới hạn sử dụng
        gbc.gridx = 0;
        gbc.gridy = 7;
        mainPanel.add(new JLabel("Giới hạn sử dụng:"), gbc);
        usageLimitField = new JTextField(25);
        gbc.gridx = 1;
        mainPanel.add(usageLimitField, gbc);

        // Trạng thái
        gbc.gridx = 0;
        gbc.gridy = 8;
        mainPanel.add(new JLabel("Trạng thái:"), gbc);
        statusCombo = new JComboBox<>(new String[]{"Active", "Inactive"});
        gbc.gridx = 1;
        mainPanel.add(statusCombo, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton saveButton = new JButton("Lưu");
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> saveDiscount());

        JButton cancelButton = new JButton("Hủy");
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadDiscountData() {
        codeField.setText(discount.getCode());
        typeCombo.setSelectedItem(discount.getDiscountType());
        valueField.setText(String.valueOf(discount.getDiscountValue()));
        minSpendField.setText(String.valueOf(discount.getMinSpend()));
        maxDiscountField.setText(String.valueOf(discount.getMaxDiscountAmount()));
        startDateField.setText(discount.getStartDate().toString());
        expiryDateField.setText(discount.getExpiryDate().toString());
        usageLimitField.setText(String.valueOf(discount.getUsageLimit()));
        statusCombo.setSelectedItem(discount.getStatus());
    }

    private void saveDiscount() {
        try {
            if (codeField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mã chiết khấu không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (discount == null) {
                discount = new DiscountDTO();
            }

            discount.setCode(codeField.getText().trim());
            discount.setDiscountType((String) typeCombo.getSelectedItem());
            discount.setDiscountValue(Double.parseDouble(valueField.getText()));
            discount.setMinSpend(Double.parseDouble(minSpendField.getText()));
            discount.setMaxDiscountAmount(Double.parseDouble(maxDiscountField.getText()));
            discount.setStartDate(Date.valueOf(startDateField.getText()));
            discount.setExpiryDate(Date.valueOf(expiryDateField.getText()));
            discount.setUsageLimit(Integer.parseInt(usageLimitField.getText()));
            discount.setStatus((String) statusCombo.getSelectedItem());

            boolean success;
            if (discount.getDiscountId() == 0) {
                success = discountBUS.addDiscount(discount);
            } else {
                success = discountBUS.updateDiscount(discount);
            }

            if (success) {
                JOptionPane.showMessageDialog(this, "Lưu chiết khấu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lưu chiết khấu thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
