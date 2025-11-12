package GUI.billing.payment;

import BUS.*;
import DAO.BookingDAO;
import DTO.*;
import GUI.dashboard.ModernScrollBarUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import javax.imageio.ImageIO;

public class Payment extends JPanel {
    private JTable paymentTable;
    private DefaultTableModel tableModel;
    private PaymentBUS paymentBUS;
    private InvoiceBUS invoiceBUS;
    private BookingBUS bookingBUS;
    private CustomerBUS customerBUS;
    private DiscountBUS discountBUS;

    private JComboBox<String> invoiceCombo;
    private JTextField customerNameField;
    private JTextField customerPhoneField;
    private JTextField invoiceAmountField;
    private JTextField amountField;
    private JComboBox<String> methodCombo;
    private JTextField referenceNoField;
    private JTextArea noteArea;
    private JLabel changeLabel;
    private JComboBox<String> discountCombo;

    // Thêm các label để hiển thị preview giảm giá
    private JLabel originalAmountLabel;
    private JLabel discountAmountLabel;
    private JLabel finalAmountLabel;
    private JPanel discountPreviewPanel;

    // Thêm panel và label cho QR Code
    private JPanel qrCodePanel;
    private JLabel qrCodeLabel;
    private JLabel qrInfoLabel;

    private InvoiceDTO selectedInvoice;
    private double originalInvoiceAmount = 0; // Lưu số tiền gốc trước khi áp discount
    private DiscountDTO selectedDiscount = null; // Add field to track selected discount without applying to DB

    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);
    private static final Color WARNING_COLOR = new Color(245, 158, 11);
    private static final Color DANGER_COLOR = new Color(239, 68, 68);
    private static final Color BG_COLOR = new Color(248, 249, 250);
    private static final Color DISCOUNT_COLOR = new Color(16, 185, 129);

    // Thông tin ngân hàng của bạn
    private static final String BANK_ID = "970436"; // Mã ngân hàng (VD: Vietcombank)
    private static final String ACCOUNT_NO = "9383161867"; // Số tài khoản
    private static final String ACCOUNT_NAME = "TRUONG TAN DAT"; // Tên tài khoản

    public Payment() {
        paymentBUS = new PaymentBUS();
        invoiceBUS = new InvoiceBUS();
        bookingBUS = new BookingBUS(new BookingDAO());
        customerBUS = new CustomerBUS();
        discountBUS = new DiscountBUS();
        initComponents();
        loadPaymentData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BG_COLOR);

        // Main Container
        JPanel mainContainer = new JPanel(new BorderLayout(0, 10));
        mainContainer.setBackground(BG_COLOR);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top Section - Invoice Selection & Payment Form
        JPanel topSection = createPaymentFormSection();

        // Bottom Section - Payment History
        JPanel bottomSection = createPaymentHistorySection();

        mainContainer.add(topSection, BorderLayout.NORTH);
        mainContainer.add(bottomSection, BorderLayout.CENTER);

        // Wrap mainContainer in JScrollPane with modern scrollbar
        JScrollPane mainScrollPane = new JScrollPane(mainContainer);
        mainScrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainScrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        mainScrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainScrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        add(mainScrollPane);
    }

    private JPanel createPaymentFormSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Title
        JLabel titleLabel = new JLabel("Thanh toán hóa đơn");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Invoice Selection Panel
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        selectionPanel.setBackground(Color.WHITE);

        selectionPanel.add(new JLabel("Chọn hóa đơn:"));
        invoiceCombo = new JComboBox<>();
        invoiceCombo.setPreferredSize(new Dimension(400, 32));
        invoiceCombo.addActionListener(e -> onInvoiceSelected());
        selectionPanel.add(invoiceCombo);

        JButton refreshInvoiceBtn = new JButton("Làm mới");
        refreshInvoiceBtn.setBackground(new Color(149, 165, 166));
        refreshInvoiceBtn.setForeground(Color.WHITE);
        refreshInvoiceBtn.setFocusPainted(false);
        refreshInvoiceBtn.setPreferredSize(new Dimension(100, 32));
        refreshInvoiceBtn.addActionListener(e -> loadUnpaidInvoices());
        selectionPanel.add(refreshInvoiceBtn);

        // Payment Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(15, 0, 0, 0)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        formPanel.add(createLabel("Tên khách hàng:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        customerNameField = createTextField(false);
        formPanel.add(customerNameField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(createLabel("Số điện thoại:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        customerPhoneField = createTextField(false);
        formPanel.add(customerPhoneField, gbc);

        // Row 2
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(createLabel("Tổng tiền hóa đơn:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        invoiceAmountField = createTextField(false);
        invoiceAmountField.setFont(invoiceAmountField.getFont().deriveFont(Font.BOLD, 14f));
        invoiceAmountField.setForeground(DANGER_COLOR);
        formPanel.add(invoiceAmountField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(createLabel("Phương thức:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        methodCombo = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản", "Thẻ tín dụng", "Ví điện tử"});
        methodCombo.setPreferredSize(new Dimension(200, 32));
        methodCombo.addActionListener(e -> {
            onPaymentMethodChanged();
            updateReferenceNo();
        });
        formPanel.add(methodCombo, gbc);

        // Row 3 - Voucher
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(createLabel("Voucher:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.gridwidth = 1;
        discountCombo = new JComboBox<>();
        discountCombo.setPreferredSize(new Dimension(200, 32));
        loadDiscounts();
        discountCombo.addActionListener(e -> applySelectedDiscount());
        formPanel.add(discountCombo, gbc);

        // Row 4 - Discount Preview Panel (ẩn mặc định)
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        gbc.weightx = 1; gbc.weighty = 0;
        discountPreviewPanel = createDiscountPreviewPanel();
        discountPreviewPanel.setVisible(false);
        formPanel.add(discountPreviewPanel, gbc);

        // Row 5
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0; gbc.gridwidth = 1;
        formPanel.add(createLabel("Số tiền thanh toán:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        amountField = createTextField(true);
        amountField.setFont(amountField.getFont().deriveFont(Font.BOLD, 14f));
        amountField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateChange();
                updateQRCode();
            }
        });
        formPanel.add(amountField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(createLabel("Mã tham chiếu:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        referenceNoField = createTextField(true);
        formPanel.add(referenceNoField, gbc);

        // Row 6
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(createLabel("Ghi chú:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.gridwidth = 3;
        noteArea = new JTextArea(3, 20);
        noteArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        JScrollPane noteScroll = new JScrollPane(noteArea);
        noteScroll.setPreferredSize(new Dimension(0, 70));
        formPanel.add(noteScroll, gbc);

        // Row 7 - Change Amount
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0; gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(createLabel("Tiền thừa trả lại:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        changeLabel = new JLabel("0.00 VNĐ");
        changeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        changeLabel.setForeground(SUCCESS_COLOR);
        formPanel.add(changeLabel, gbc);

        // Payment Button
        gbc.gridx = 3; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JButton payButton = new JButton("THANH TOÁN");
        payButton.setBackground(PRIMARY_COLOR);
        payButton.setForeground(Color.WHITE);
        payButton.setFocusPainted(false);
        payButton.setFont(new Font("Arial", Font.BOLD, 14));
        payButton.setPreferredSize(new Dimension(150, 40));
        payButton.addActionListener(e -> processPayment());
        formPanel.add(payButton, gbc);

        // Row 8 - QR Code Panel (Ẩn mặc định)
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 4;
        gbc.weightx = 1; gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;

        qrCodePanel = new JPanel(new BorderLayout(10, 10));
        qrCodePanel.setBackground(new Color(248, 249, 250));
        qrCodePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        qrCodePanel.setVisible(false);

        JPanel qrContentPanel = new JPanel(new BorderLayout(10, 10));
        qrContentPanel.setBackground(new Color(248, 249, 250));

        // QR Code Image
        qrCodeLabel = new JLabel("", SwingConstants.CENTER);
        qrCodeLabel.setPreferredSize(new Dimension(250, 250));
        qrCodeLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        qrCodeLabel.setBackground(Color.WHITE);
        qrCodeLabel.setOpaque(true);

        // QR Info
        qrInfoLabel = new JLabel("<html><div style='text-align: center;'>"
                + "<b style='font-size: 14px;'>Quét mã QR để thanh toán</b><br/><br/>"
                + "Ngân hàng: <b>Vietcombank</b><br/>"
                + "Số tài khoản: <b>" + ACCOUNT_NO + "</b><br/>"
                + "Tên tài khoản: <b>" + ACCOUNT_NAME + "</b><br/>"
                + "Số tiền: <b style='color: #EF4444;'>0 VNĐ</b>"
                + "</div></html>", SwingConstants.CENTER);
        qrInfoLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        qrContentPanel.add(qrCodeLabel, BorderLayout.CENTER);
        qrContentPanel.add(qrInfoLabel, BorderLayout.SOUTH);

        qrCodePanel.add(qrContentPanel, BorderLayout.CENTER);

        formPanel.add(qrCodePanel, gbc);

        // Assemble section
        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(selectionPanel, BorderLayout.CENTER);
        contentPanel.add(formPanel, BorderLayout.SOUTH);

        section.add(contentPanel);

        // Load invoices
        loadUnpaidInvoices();

        return section;
    }

    /**
     * Tạo panel hiển thị preview giảm giá
     */
    private JPanel createDiscountPreviewPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(236, 253, 245)); // Light green background
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DISCOUNT_COLOR, 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 10, 3, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tiêu đề
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Chi tiết giảm giá");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 13));
        titleLabel.setForeground(DISCOUNT_COLOR);
        panel.add(titleLabel, gbc);

        // Số tiền gốc
        gbc.gridy = 1; gbc.gridwidth = 1; gbc.weightx = 0;
        JLabel originalLabel = new JLabel("Tổng tiền gốc:");
        originalLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(originalLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        originalAmountLabel = new JLabel("0.00 VNĐ");
        originalAmountLabel.setFont(new Font("Arial", Font.BOLD, 12));
        originalAmountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(originalAmountLabel, gbc);

        // Số tiền giảm
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        JLabel discountLabel = new JLabel("Giảm giá:");
        discountLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        discountLabel.setForeground(DISCOUNT_COLOR);
        panel.add(discountLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        discountAmountLabel = new JLabel("- 0.00 VNĐ");
        discountAmountLabel.setFont(new Font("Arial", Font.BOLD, 12));
        discountAmountLabel.setForeground(DISCOUNT_COLOR);
        discountAmountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(discountAmountLabel, gbc);

        // Đường kẻ ngang
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JSeparator separator = new JSeparator();
        separator.setForeground(DISCOUNT_COLOR);
        panel.add(separator, gbc);

        // Số tiền sau giảm
        gbc.gridy = 4; gbc.gridwidth = 1; gbc.weightx = 0;
        JLabel finalLabel = new JLabel("Tổng tiền thanh toán:");
        finalLabel.setFont(new Font("Arial", Font.BOLD, 13));
        finalLabel.setForeground(DANGER_COLOR);
        panel.add(finalLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1;
        finalAmountLabel = new JLabel("0.00 VNĐ");
        finalAmountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        finalAmountLabel.setForeground(DANGER_COLOR);
        finalAmountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(finalAmountLabel, gbc);

        return panel;
    }

    private void onPaymentMethodChanged() {
        String method = (String) methodCombo.getSelectedItem();
        if ("Chuyển khoản".equals(method)) {
            qrCodePanel.setVisible(true);
            updateQRCode();
        } else {
            qrCodePanel.setVisible(false);
        }
        revalidate();
        repaint();
    }

    private void updateReferenceNo() {
        String method = (String) methodCombo.getSelectedItem();
        String referenceNo = "";

        if (selectedInvoice != null) {
            String invoiceNo = selectedInvoice.getInvoiceNo();

            switch (method) {
                case "Tiền mặt":
                    referenceNo = "TM" + invoiceNo;
                    break;
                case "Chuyển khoản":
                    referenceNo = "FT" + invoiceNo;
                    break;
                case "Thẻ tín dụng":
                    referenceNo = "MC" + invoiceNo;
                    break;
                case "Ví điện tử":
                    referenceNo = "VS" + invoiceNo;
                    break;
                default:
                    referenceNo = invoiceNo;
            }
        } else {
            // Nếu chưa chọn invoice, chỉ dùng prefix
            switch (method) {
                case "Tiền mặt":
                    referenceNo = "TM";
                    break;
                case "Chuyển khoản":
                    referenceNo = "FT";
                    break;
                case "Thẻ tín dụng":
                    referenceNo = "MC";
                    break;
                case "Ví điện tử":
                    referenceNo = "VS";
                    break;
            }
        }

        referenceNoField.setText(referenceNo);
    }

    private void updateQRCode() {
        if (!qrCodePanel.isVisible()) return;

        try {
            double amount = 0;
            if (amountField.getText() != null && !amountField.getText().trim().isEmpty()) {
                amount = Double.parseDouble(amountField.getText().trim());
            }

            // Tạo nội dung chuyển khoản
            String description = "Thanh toan hoa don";
            if (selectedInvoice != null) {
                description = "TT HD " + selectedInvoice.getInvoiceNo();
            }

            // Cập nhật thông tin hiển thị
            qrInfoLabel.setText("<html><div style='text-align: center;'>"
                    + "<b style='font-size: 14px;'>Quét mã QR để thanh toán</b><br/><br/>"
                    + "Ngân hàng: <b>Vietcombank</b><br/>"
                    + "Số tài khoản: <b>" + ACCOUNT_NO + "</b><br/>"
                    + "Tên tài khoản: <b>" + ACCOUNT_NAME + "</b><br/>"
                    + "Số tiền: <b style='color: #EF4444;'>" + String.format("%.0f VNĐ", amount) + "</b><br/>"
                    + "Nội dung: <b>" + description + "</b>"
                    + "</div></html>");

            // Tạo URL QR Code sử dụng API của Vietqr
            String qrUrl = String.format(
                    "https://img.vietqr.io/image/%s-%s-compact2.png?amount=%.0f&addInfo=%s&accountName=%s",
                    BANK_ID,
                    ACCOUNT_NO,
                    amount,
                    URLEncoder.encode(description, StandardCharsets.UTF_8.toString()),
                    URLEncoder.encode(ACCOUNT_NAME, StandardCharsets.UTF_8.toString())
            );

            // Tải và hiển thị QR Code
            loadQRCodeImage(qrUrl);

        } catch (NumberFormatException e) {
            qrCodeLabel.setText("Vui lòng nhập số tiền hợp lệ");
            qrCodeLabel.setIcon(null);
        } catch (Exception e) {
            qrCodeLabel.setText("Lỗi tạo QR Code");
            qrCodeLabel.setIcon(null);
            e.printStackTrace();
        }
    }

    private void loadQRCodeImage(String urlString) {
        new Thread(() -> {
            try {
                URL url = new URL(urlString);
                BufferedImage image = ImageIO.read(url);

                if (image != null) {
                    // Resize image để vừa với label
                    Image scaledImage = image.getScaledInstance(230, 230, Image.SCALE_SMOOTH);
                    ImageIcon icon = new ImageIcon(scaledImage);

                    SwingUtilities.invokeLater(() -> {
                        qrCodeLabel.setIcon(icon);
                        qrCodeLabel.setText("");
                    });
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    qrCodeLabel.setText("Không thể tải QR Code");
                    qrCodeLabel.setIcon(null);
                });
                e.printStackTrace();
            }
        }).start();
    }

    private void loadDiscounts() {
        discountCombo.removeAllItems();
        discountCombo.addItem("Không áp dụng");
        List<DiscountDTO> discounts = discountBUS.getAllDiscounts();
        for (DiscountDTO discount : discounts) {
            discountCombo.addItem(discount.getCode() + " - " + discount.getDiscountValue() + (discount.getDiscountType().equals("PERCENTAGE") ? "%" : " VNĐ"));
        }
    }

    /**
     * Áp dụng discount đã chọn - CHỈ HIỂN THỊ PREVIEW, CHƯA LƯU VÀO DB
     */
    private void applySelectedDiscount() {
        if (selectedInvoice == null) {
            discountPreviewPanel.setVisible(false);
            revalidate();
            repaint();
            return;
        }

        String selected = (String) discountCombo.getSelectedItem();

        // Nếu chọn "Không áp dụng", ẩn preview và reset
        if ("Không áp dụng".equals(selected)) {
            selectedDiscount = null;
            discountPreviewPanel.setVisible(false);

            // Reset về số tiền gốc
            invoiceAmountField.setText(String.format("%.2f VNĐ", originalInvoiceAmount));
            amountField.setText(String.format("%.2f", originalInvoiceAmount));
            calculateChange();
            updateQRCode();

            revalidate();
            repaint();
            return;
        }

        // Extract discount code và load discount info
        String discountCode = selected.split(" - ")[0];
        selectedDiscount = discountBUS.getDiscountByCode(discountCode);

        if (selectedDiscount != null) {
            // Tính toán số tiền giảm
            double discountAmount = 0;

            if ("PERCENTAGE".equals(selectedDiscount.getDiscountType())) {
                // Giảm theo phần trăm
                discountAmount = originalInvoiceAmount * (selectedDiscount.getDiscountValue() / 100.0);
            } else {
                // Giảm theo số tiền cố định
                discountAmount = selectedDiscount.getDiscountValue();
            }

            // Đảm bảo không giảm quá số tiền gốc
            if (discountAmount > originalInvoiceAmount) {
                discountAmount = originalInvoiceAmount;
            }

            double finalAmount = originalInvoiceAmount - discountAmount;

            // Cập nhật preview panel
            originalAmountLabel.setText(String.format("%.2f VNĐ", originalInvoiceAmount));
            discountAmountLabel.setText(String.format("- %.2f VNĐ", discountAmount));
            finalAmountLabel.setText(String.format("%.2f VNĐ", finalAmount));

            // Hiển thị preview panel
            discountPreviewPanel.setVisible(true);

            // Cập nhật số tiền cần thanh toán (CHỈ HIỂN THỊ, CHƯA LƯU DB)
            invoiceAmountField.setText(String.format("%.2f VNĐ", finalAmount));
            amountField.setText(String.format("%.2f", finalAmount));

            calculateChange();
            updateQRCode();

            revalidate();
            repaint();
        }
    }

    private JPanel createPaymentHistorySection() {
        JPanel section = new JPanel(new BorderLayout(0, 10));
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Title
        JLabel titleLabel = new JLabel("Lịch sử thanh toán");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(PRIMARY_COLOR);

        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(Color.WHITE);

        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Tìm kiếm");
        searchButton.setBackground(PRIMARY_COLOR);
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);
        searchButton.addActionListener(e -> searchPayments(searchField.getText()));

        JComboBox<String> statusFilterCombo = new JComboBox<>(new String[]{"Tất cả", "Pending", "Completed", "Failed", "Cancelled"});
        statusFilterCombo.addActionListener(e -> filterByStatus((String) statusFilterCombo.getSelectedItem()));

        JButton refreshButton = new JButton("Làm mới");
        refreshButton.setBackground(new Color(149, 165, 166));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> loadPaymentData());

        filterPanel.add(new JLabel("Tìm kiếm:"));
        filterPanel.add(searchField);
        filterPanel.add(searchButton);
        filterPanel.add(new JLabel("Trạng thái:"));
        filterPanel.add(statusFilterCombo);
        filterPanel.add(refreshButton);

        // Table
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
        paymentTable.setShowGrid(true);
        paymentTable.setGridColor(new Color(230, 230, 230));

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
        JMenuItem deleteItem = new JMenuItem("Xóa");

        viewItem.addActionListener(e -> viewPaymentDetail());
        deleteItem.addActionListener(e -> deletePayment());

        popupMenu.add(viewItem);
        popupMenu.add(deleteItem);

        paymentTable.setComponentPopupMenu(popupMenu);

        JScrollPane scrollPane = new JScrollPane(paymentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());

        // Assemble
        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.CENTER);

        section.add(topPanel, BorderLayout.NORTH);
        section.add(scrollPane, BorderLayout.CENTER);

        return section;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        return label;
    }

    private JTextField createTextField(boolean editable) {
        JTextField field = new JTextField(20);
        field.setEditable(editable);
        field.setPreferredSize(new Dimension(200, 32));
        if (!editable) {
            field.setBackground(new Color(245, 245, 245));
        }
        return field;
    }

    private void loadUnpaidInvoices() {
        invoiceCombo.removeAllItems();
        invoiceCombo.addItem("-- Chọn hóa đơn cần thanh toán --");

        List<InvoiceDTO> unpaidInvoices = invoiceBUS.getUnpaidInvoices();

        for (InvoiceDTO invoice : unpaidInvoices) {
            invoiceCombo.addItem(invoice.getInvoiceId() + " | " + invoice.getInvoiceNo() +
                    " | " + String.format("%.2f VNĐ", invoice.getGrandTotal()));
        }

        clearForm();
    }

    private void onInvoiceSelected() {
        String selected = (String) invoiceCombo.getSelectedItem();
        if (selected == null || selected.startsWith("--")) {
            clearForm();
            selectedInvoice = null;
            return;
        }

        try {
            int invoiceId = Integer.parseInt(selected.split("\\|")[0].trim());
            selectedInvoice = invoiceBUS.getInvoiceById(invoiceId);

            if (selectedInvoice != null) {
                loadInvoiceDetails();
                updateReferenceNo(); // Cập nhật mã tham chiếu khi chọn invoice
            }
        } catch (Exception e) {
            e.printStackTrace();
            clearForm();
        }
    }

    private void loadInvoiceDetails() {
        if (selectedInvoice == null) return;

        try {
            // Lưu số tiền gốc (chưa có discount) khi load invoice lần đầu
            originalInvoiceAmount = selectedInvoice.getSubtotal() + selectedInvoice.getTaxTotal();

            // Load booking info
            BookingDTO booking = bookingBUS.getBookingById(selectedInvoice.getBookingId());
            if (booking != null) {
                CustomerDTO customer = customerBUS.getCustomerById(booking.getCustomerId());
                if (customer != null) {
                    customerNameField.setText(customer.getFull_name());
                    customerPhoneField.setText(customer.getPhone());
                }
            }

            invoiceAmountField.setText(String.format("%.2f VNĐ", selectedInvoice.getGrandTotal()));
            amountField.setText(String.format("%.2f", selectedInvoice.getGrandTotal()));

            // Reset discount selection
            discountCombo.setSelectedIndex(0);
            selectedDiscount = null;
            discountPreviewPanel.setVisible(false);

            calculateChange();
            updateQRCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearForm() {
        customerNameField.setText("");
        customerPhoneField.setText("");
        invoiceAmountField.setText("");
        amountField.setText("");
        referenceNoField.setText("");
        discountCombo.setSelectedIndex(0);
        noteArea.setText("");
        changeLabel.setText("0.00 VNĐ");
        changeLabel.setForeground(SUCCESS_COLOR);
        qrCodePanel.setVisible(false);
        discountPreviewPanel.setVisible(false);
        originalInvoiceAmount = 0;
        selectedDiscount = null;
    }

    private void calculateChange() {
        if (selectedInvoice == null) {
            changeLabel.setText("0.00 VNĐ");
            changeLabel.setForeground(SUCCESS_COLOR);
            return;
        }

        try {
            // Lấy số tiền cần thanh toán (đã tính discount nếu có)
            double invoiceAmount = 0;
            if (selectedDiscount != null) {
                // Tính lại số tiền sau discount
                double discountAmount = 0;
                if ("PERCENTAGE".equals(selectedDiscount.getDiscountType())) {
                    discountAmount = originalInvoiceAmount * (selectedDiscount.getDiscountValue() / 100.0);
                } else {
                    discountAmount = selectedDiscount.getDiscountValue();
                }
                if (discountAmount > originalInvoiceAmount) {
                    discountAmount = originalInvoiceAmount;
                }
                invoiceAmount = originalInvoiceAmount - discountAmount;
            } else {
                invoiceAmount = selectedInvoice.getGrandTotal();
            }

            double paidAmount = Double.parseDouble(amountField.getText().trim());
            double change = paidAmount - invoiceAmount;

            if (change < 0) {
                changeLabel.setText(String.format("Thiếu: %.2f VNĐ", Math.abs(change)));
                changeLabel.setForeground(DANGER_COLOR);
            } else {
                changeLabel.setText(String.format("%.2f VNĐ", change));
                changeLabel.setForeground(SUCCESS_COLOR);
            }
        } catch (NumberFormatException e) {
            changeLabel.setText("0.00 VNĐ");
            changeLabel.setForeground(SUCCESS_COLOR);
        }
    }

    private void processPayment() {
        if (selectedInvoice == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần thanh toán!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double invoiceAmount = selectedInvoice.getGrandTotal();
            double paidAmount = Double.parseDouble(amountField.getText().trim());

            // Nếu có discount được chọn, áp dụng vào DB trước khi thanh toán
            if (selectedDiscount != null) {
                // Get fresh invoice data to ensure discount is applied correctly
                InvoiceDTO freshInvoice = invoiceBUS.getInvoiceById(selectedInvoice.getInvoiceId());

                // Reset discount if one was previously applied
                if (freshInvoice.getDiscountTotal() > 0) {
                    freshInvoice.setDiscountTotal(0.0);
                    double resetGrandTotal = freshInvoice.getSubtotal() + freshInvoice.getTaxTotal();
                    freshInvoice.setGrandTotal(resetGrandTotal);
                    invoiceBUS.updateInvoice(freshInvoice);
                }

                // Apply the selected discount
                if (!invoiceBUS.applyDiscount(selectedInvoice.getInvoiceId(), selectedDiscount.getDiscountId())) {
                    JOptionPane.showMessageDialog(this, "Không thể áp dụng voucher!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Reload invoice with applied discount
                selectedInvoice = invoiceBUS.getInvoiceById(selectedInvoice.getInvoiceId());

                // Update amount with new total after discount
                invoiceAmount = selectedInvoice.getGrandTotal();
            }

            // Kiểm tra số tiền thanh toán
            if (paidAmount < invoiceAmount) {
                JOptionPane.showMessageDialog(this,
                        String.format("Số tiền thanh toán (%.2f VNĐ) không đủ để thanh toán hóa đơn (%.2f VNĐ)!", paidAmount, invoiceAmount),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            PaymentDTO payment = new PaymentDTO();
            payment.setBookingId(selectedInvoice.getBookingId());
            payment.setInvoiceId(selectedInvoice.getInvoiceId());
            payment.setAmount(paidAmount);
            payment.setMethod((String) methodCombo.getSelectedItem());
            payment.setReferenceNo(referenceNoField.getText().trim());
            payment.setNote(noteArea.getText().trim());
            payment.setStatus("SUCCESS");
            payment.setPaidAt(java.sql.Timestamp.valueOf(LocalDateTime.now()));

            if (paymentBUS.addPayment(payment)) {
                // Update invoice status
                selectedInvoice.setStatus("PAID");
                invoiceBUS.updateInvoice(selectedInvoice);

                distributeDiscountToBookingRooms(selectedInvoice);

                double change = paidAmount - invoiceAmount;
                String message = "Thanh toán thành công!\n";
                if (selectedDiscount != null) {
                    message += String.format("Đã áp dụng voucher: %s\n", selectedDiscount.getCode());
                }
                if (change > 0) {
                    message += String.format("Tiền thừa trả lại: %.2f VNĐ", change);
                }

                JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);

                loadPaymentData();
                loadUnpaidInvoices();
                clearForm();
                selectedDiscount = null;
            } else {
                JOptionPane.showMessageDialog(this, "Thanh toán thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Phân bổ discount từ Invoice về các BookingRoom theo tỷ lệ
     */
    private void distributeDiscountToBookingRooms(InvoiceDTO invoice) {
        if (invoice.getDiscountTotal() <= 0) {
            return; // Không có discount thì không cần phân bổ
        }

        try {
            // Lấy tất cả BookingRoom của booking này
            BookingRoomBUS bookingRoomBUS = new BookingRoomBUS();
            List<BookingRoomDTO> bookingRooms = bookingRoomBUS.getBookingRoomsByBooking(invoice.getBookingId());

            if (bookingRooms == null || bookingRooms.isEmpty()) {
                return;
            }

            // Tính tổng giá trị các phòng (trước discount và tax)
            double totalRoomPrice = 0.0;
            for (BookingRoomDTO room : bookingRooms) {
                // Tính số đêm
                LocalDateTime checkIn = room.getCheckInActual() != null ?
                        room.getCheckInActual() : room.getCheckInPlan();
                LocalDateTime checkOut = room.getCheckOutActual() != null ?
                        room.getCheckOutActual() : room.getCheckOutPlan();

                long nights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
                if (nights < 1) nights = 1;

                double roomPrice = room.getRatePerNight().doubleValue() * nights;
                totalRoomPrice += roomPrice;
            }

            // Phân bổ discount theo tỷ lệ giá phòng
            double totalDiscountApplied = 0.0;
            for (int i = 0; i < bookingRooms.size(); i++) {
                BookingRoomDTO room = bookingRooms.get(i);

                // Tính số đêm
                LocalDateTime checkIn = room.getCheckInActual() != null ?
                        room.getCheckInActual() : room.getCheckInPlan();
                LocalDateTime checkOut = room.getCheckOutActual() != null ?
                        room.getCheckOutActual() : room.getCheckOutPlan();

                long nights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
                if (nights < 1) nights = 1;

                double roomPrice = room.getRatePerNight().doubleValue() * nights;

                // Tính discount cho phòng này
                double roomDiscount;
                if (i == bookingRooms.size() - 1) {
                    // Phòng cuối cùng: lấy phần discount còn lại để tránh sai số làm tròn
                    roomDiscount = invoice.getDiscountTotal() - totalDiscountApplied;
                } else {
                    // Tính theo tỷ lệ
                    roomDiscount = (roomPrice / totalRoomPrice) * invoice.getDiscountTotal();
                    totalDiscountApplied += roomDiscount;
                }

                // Cập nhật discount_amount cho BookingRoom
                room.setDiscountAmount(java.math.BigDecimal.valueOf(roomDiscount));
                bookingRoomBUS.updateBookingRoom(room);
            }

        } catch (Exception e) {
            System.err.println("Lỗi khi phân bổ discount: " + e.getMessage());
            e.printStackTrace();
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
                    String.format("%.2f VNĐ", payment.getAmount()),
                    payment.getMethod() != null ? payment.getMethod() : "N/A",
                    payment.getPaidAt() != null ? payment.getPaidAt().toString() : "N/A",
                    payment.getNote() != null ? payment.getNote() : "",
                    payment.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void searchPayments(String keyword) {
        if (keyword.trim().isEmpty()) {
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
                    String.format("%.2f VNĐ", payment.getAmount()),
                    payment.getMethod() != null ? payment.getMethod() : "N/A",
                    payment.getPaidAt() != null ? payment.getPaidAt().toString() : "N/A",
                    payment.getNote() != null ? payment.getNote() : "",
                    payment.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void filterByStatus(String status) {
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
                    String.format("%.2f VNĐ", payment.getAmount()),
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
        if (payment == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thanh toán!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        InvoiceDTO invoice = null;
        if (payment.getInvoiceId() > 0) {
            invoice = invoiceBUS.getInvoiceById(payment.getInvoiceId());
        }

        PaymentDetail detailDialog = new PaymentDetail(
                (Frame) SwingUtilities.getWindowAncestor(this),
                payment,
                paymentBUS,
                invoice
        );
        detailDialog.setVisible(true);
        loadPaymentData();
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