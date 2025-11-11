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
import java.time.LocalDateTime;
import java.util.List;

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

    private InvoiceDTO selectedInvoice;

    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);
    private static final Color WARNING_COLOR = new Color(245, 158, 11);
    private static final Color DANGER_COLOR = new Color(239, 68, 68);
    private static final Color BG_COLOR = new Color(248, 249, 250);

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

        add(mainContainer);
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
        formPanel.add(methodCombo, gbc);

        // Row 3
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        formPanel.add(createLabel("Số tiền thanh toán:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        amountField = createTextField(true);
        amountField.setFont(amountField.getFont().deriveFont(Font.BOLD, 14f));
        amountField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calculateChange();
            }
        });
        formPanel.add(amountField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        formPanel.add(createLabel("Mã tham chiếu:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        referenceNoField = createTextField(true);
        formPanel.add(referenceNoField, gbc);

        // Row 4
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        formPanel.add(createLabel("Voucher:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        discountCombo = new JComboBox<>();
        discountCombo.setPreferredSize(new Dimension(200, 32));
        loadDiscounts();
        discountCombo.addActionListener(e -> applySelectedDiscount());
        formPanel.add(discountCombo, gbc);

        // Row 5
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
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

        // Row 6 - Change Amount
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0; gbc.gridwidth = 1;
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

    private void loadDiscounts() {
        discountCombo.removeAllItems();
        discountCombo.addItem("Không áp dụng");
        List<DiscountDTO> discounts = discountBUS.getAllDiscounts();
        for (DiscountDTO discount : discounts) {
            discountCombo.addItem(discount.getCode() + " - " + discount.getDiscountValue() + (discount.getDiscountType().equals("PERCENTAGE") ? "%" : " VNĐ"));
        }
    }

    private void applySelectedDiscount() {
        if (selectedInvoice == null) return;

        String selected = (String) discountCombo.getSelectedItem();
        if ("Không áp dụng".equals(selected)) {
            selectedInvoice.setDiscountTotal(0.0);
            invoiceBUS.updateInvoice(selectedInvoice);
            loadInvoiceDetails();
            return;
        }

        // Extract discount code
        String discountCode = selected.split(" - ")[0];
        DiscountDTO discount = discountBUS.getDiscountByCode(discountCode);
        if (discount != null) {
            if (invoiceBUS.applyDiscount(selectedInvoice.getInvoiceId(), discount.getDiscountId())) {
                selectedInvoice = invoiceBUS.getInvoiceById(selectedInvoice.getInvoiceId());
                loadInvoiceDetails();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể áp dụng voucher này!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
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
            }
        } catch (Exception e) {
            e.printStackTrace();
            clearForm();
        }
    }

    private void loadInvoiceDetails() {
        if (selectedInvoice == null) return;

        try {
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
            calculateChange();
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
        noteArea.setText("");
        changeLabel.setText("0.00 VNĐ");
        changeLabel.setForeground(SUCCESS_COLOR);
    }

    private void calculateChange() {
        if (selectedInvoice == null) {
            changeLabel.setText("0.00 VNĐ");
            changeLabel.setForeground(SUCCESS_COLOR);
            return;
        }

        try {
            double invoiceAmount = selectedInvoice.getGrandTotal();
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
                if (change > 0) {
                    message += String.format("Tiền thừa trả lại: %.2f VNĐ", change);
                }

                JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);

                loadPaymentData();
                loadUnpaidInvoices();
                clearForm();
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