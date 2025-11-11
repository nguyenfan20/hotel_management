package GUI.billing.discount;

import BUS.DiscountBUS;
import DTO.DiscountDTO;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.util.Calendar;

public class DiscountDetail extends JDialog {
    private DiscountDTO discount;
    private DiscountBUS discountBUS;
    private JTextField codeField;
    private JComboBox<String> typeCombo;
    private JTextField valueField;
    private JTextField minSpendField;
    private JTextField maxDiscountField;
    private JDateChooser startDateChooser;
    private JDateChooser expiryDateChooser;
    private JTextField usageLimitField;
    private JTextField perUserLimitField;
    private JComboBox<String> statusCombo;

    public DiscountDetail(Frame parent, DiscountDTO discount, DiscountBUS discountBUS) {
        super(parent, discount == null ? "Thêm chiết khấu" : "Chi tiết chiết khấu", true);
        this.discount = discount;
        this.discountBUS = discountBUS;
        initComponents();
        if (discount != null) {
            loadDiscountData();
        } else {
            setDefaultValues();
        }
    }

    private void initComponents() {
        setSize(650, 600);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Mã chiết khấu
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        mainPanel.add(new JLabel("Mã chiết khấu:"), gbc);

        codeField = new JTextField();
        codeField.setPreferredSize(new Dimension(300, 30));
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        mainPanel.add(codeField, gbc);

        // Loại chiết khấu
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        mainPanel.add(new JLabel("Loại chiết khấu:"), gbc);

        typeCombo = new JComboBox<>(new String[]{"PERCENTAGE", "FIXED_AMOUNT"});
        typeCombo.setPreferredSize(new Dimension(300, 30));
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        mainPanel.add(typeCombo, gbc);

        // Giá trị
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        mainPanel.add(new JLabel("Giá trị:"), gbc);

        valueField = new JTextField();
        valueField.setPreferredSize(new Dimension(300, 30));
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        mainPanel.add(valueField, gbc);

        // Chi tiêu tối thiểu
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        mainPanel.add(new JLabel("Chi tiêu tối thiểu:"), gbc);

        minSpendField = new JTextField();
        minSpendField.setPreferredSize(new Dimension(300, 30));
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        mainPanel.add(minSpendField, gbc);

        // Chiết khấu tối đa
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.3;
        mainPanel.add(new JLabel("Chiết khấu tối đa:"), gbc);

        maxDiscountField = new JTextField();
        maxDiscountField.setPreferredSize(new Dimension(300, 30));
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        mainPanel.add(maxDiscountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.3;
        mainPanel.add(new JLabel("Ngày bắt đầu:"), gbc);

        startDateChooser = new JDateChooser();
        startDateChooser.setPreferredSize(new Dimension(300, 30));
        startDateChooser.setDateFormatString("yyyy-MM-dd");
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        mainPanel.add(startDateChooser, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.3;
        mainPanel.add(new JLabel("Ngày kết thúc:"), gbc);

        expiryDateChooser = new JDateChooser();
        expiryDateChooser.setPreferredSize(new Dimension(300, 30));
        expiryDateChooser.setDateFormatString("yyyy-MM-dd");
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        mainPanel.add(expiryDateChooser, gbc);

        // Giới hạn sử dụng
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 0.3;
        mainPanel.add(new JLabel("Giới hạn sử dụng:"), gbc);

        usageLimitField = new JTextField();
        usageLimitField.setPreferredSize(new Dimension(300, 30));
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        mainPanel.add(usageLimitField, gbc);

        // Giới hạn mỗi người dùng
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.weightx = 0.3;
        mainPanel.add(new JLabel("Giới hạn mỗi người dùng:"), gbc);

        perUserLimitField = new JTextField();
        perUserLimitField.setPreferredSize(new Dimension(300, 30));
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        mainPanel.add(perUserLimitField, gbc);

        // Trạng thái
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.weightx = 0.3;
        mainPanel.add(new JLabel("Trạng thái:"), gbc);

        statusCombo = new JComboBox<>(new String[]{"ACTIVE", "INACTIVE"});
        statusCombo.setPreferredSize(new Dimension(300, 30));
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        mainPanel.add(statusCombo, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton saveButton = new JButton("Lưu");
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.setPreferredSize(new Dimension(100, 35));
        saveButton.addActionListener(e -> saveDiscount());

        JButton cancelButton = new JButton("Hủy");
        cancelButton.setBackground(new Color(231, 76, 60));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setDefaultValues() {
        codeField.setText("");
        valueField.setText("0");
        minSpendField.setText("0");
        maxDiscountField.setText("0");
        usageLimitField.setText("0");
        perUserLimitField.setText("0");

        long currentTime = System.currentTimeMillis();
        startDateChooser.setDate(new java.util.Date(currentTime));
        expiryDateChooser.setDate(new java.util.Date(currentTime + 30L * 24 * 60 * 60 * 1000));

        typeCombo.setSelectedIndex(0);
        statusCombo.setSelectedIndex(0);
    }

    private void loadDiscountData() {
        codeField.setText(discount.getCode() != null ? discount.getCode() : "");
        typeCombo.setSelectedItem(discount.getDiscountType() != null ? discount.getDiscountType() : "PERCENTAGE");
        valueField.setText(String.valueOf(discount.getDiscountValue()));
        minSpendField.setText(String.valueOf(discount.getMinSpend()));
        maxDiscountField.setText(String.valueOf(discount.getMaxDiscountAmount()));

        if (discount.getStartDate() != null) {
            startDateChooser.setDate(new java.util.Date(discount.getStartDate().getTime()));
        }
        if (discount.getExpiryDate() != null) {
            expiryDateChooser.setDate(new java.util.Date(discount.getExpiryDate().getTime()));
        }

        usageLimitField.setText(String.valueOf(discount.getUsageLimit()));
        perUserLimitField.setText(String.valueOf(discount.getPerUserLimit()));
        statusCombo.setSelectedItem(discount.getStatus() != null ? discount.getStatus() : "ACTIVE");
    }

    private void saveDiscount() {
        try {
            String code = codeField.getText().trim();
            String valueStr = valueField.getText().trim();
            String minSpendStr = minSpendField.getText().trim();
            String maxDiscountStr = maxDiscountField.getText().trim();
            String usageLimitStr = usageLimitField.getText().trim();
            String perUserLimitStr = perUserLimitField.getText().trim();

            if (code.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mã chiết khấu không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (valueStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Giá trị không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (minSpendStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Chi tiêu tối thiểu không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (maxDiscountStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Chiết khấu tối đa không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (startDateChooser.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (expiryDateChooser.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Ngày kết thúc không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (usageLimitStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Giới hạn sử dụng không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (perUserLimitStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Giới hạn mỗi người dùng không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Date startDate = new Date(startDateChooser.getDate().getTime());
            Date expiryDate = new Date(expiryDateChooser.getDate().getTime());

            if (startDate.after(expiryDate)) {
                JOptionPane.showMessageDialog(this, "Ngày bắt đầu phải trước hoặc bằng ngày kết thúc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (discount == null) {
                discount = new DiscountDTO();
            }

            discount.setCode(code);
            discount.setDiscountType((String) typeCombo.getSelectedItem());
            discount.setDiscountValue(Double.parseDouble(valueStr));
            discount.setMinSpend(Double.parseDouble(minSpendStr));
            discount.setMaxDiscountAmount(Double.parseDouble(maxDiscountStr));
            discount.setStartDate(startDate);
            discount.setExpiryDate(expiryDate);
            discount.setUsageLimit(Integer.parseInt(usageLimitStr));
            discount.setPerUserLimit(Integer.parseInt(perUserLimitStr));
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
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi không mong muốn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
