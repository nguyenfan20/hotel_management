package GUI.billing.invoice;

import BUS.InvoiceBUS;
import BUS.BookingBUS;
import BUS.BookingRoomBUS;
import BUS.ServiceOrderBUS;
import BUS.CustomerBUS;
import BUS.RoomBUS;
import BUS.ServiceBUS;
import DAO.BookingDAO;
import DAO.BookingRoomDAO;
import DTO.InvoiceDTO;
import DTO.BookingDTO;
import DTO.BookingRoomDTO;
import DTO.ServiceOrderDTO;
import DTO.CustomerDTO;
import DTO.RoomDTO;
import DTO.ServiceDTO;
import GUI.dashboard.ModernScrollBarUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InvoiceDetail extends JDialog {
    private InvoiceDTO invoice;
    private InvoiceBUS invoiceBUS;
    private BookingBUS bookingBUS;
    private BookingRoomBUS bookingRoomBUS;
    private ServiceOrderBUS serviceOrderBUS;
    private CustomerBUS customerBUS;
    private RoomBUS roomBUS;
    private ServiceBUS serviceBUS;

    // Invoice fields
    private JTextField invoiceNoField;
    private JTextField bookingIdField;
    private JTextField subtotalField;
    private JTextField discountTotalField;
    private JTextField taxTotalField;
    private JTextField grandTotalField;
    private JTextField status;

    // Customer info fields
    private JTextField customerNameField;
    private JTextField customerPhoneField;
    private JTextField customerEmailField;
    private JTextField customerIdCardField;
    private JTextField customerNationalityField;

    // Booking info fields
    private JTextField bookingCodeField;
    private JTextField bookingDateField;
    private JTextField bookingSourceField;

    // Room info fields
    private JTable bookingRoomTable;
    private DefaultTableModel roomTableModel;

    // Service info fields
    private JTable serviceTable;
    private DefaultTableModel serviceTableModel;

    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color SECTION_BG = new Color(248, 249, 250);

    public InvoiceDetail(Frame parent, InvoiceDTO invoice, InvoiceBUS invoiceBUS) {
        super(parent, "Chi tiết hóa đơn", true);
        this.invoice = invoice;
        this.invoiceBUS = invoiceBUS;
        this.bookingBUS = new BookingBUS(new BookingDAO());
        this.bookingRoomBUS = new BookingRoomBUS(new BookingRoomDAO());
        this.serviceOrderBUS = new ServiceOrderBUS();
        this.customerBUS = new CustomerBUS();
        this.roomBUS = new RoomBUS();
        this.serviceBUS = new ServiceBUS();
        initComponents();
        if (invoice != null) {
            loadInvoiceData();
        }
    }

    private void initComponents() {
        setSize(1000, 700);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(Color.WHITE);

        // Content Panel with Custom ScrollBar
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add all sections
        contentPanel.add(createInvoiceSection());
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(createCustomerSection());
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(createBookingSection());
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(createRoomSection());
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(createServiceSection());
        contentPanel.add(Box.createVerticalStrut(20));

        // Scroll Pane with Modern ScrollBar
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));

        JButton saveButton = new JButton("Lưu");
        saveButton.setBackground(SUCCESS_COLOR);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setPreferredSize(new Dimension(100, 35));
        saveButton.addActionListener(e -> saveInvoice());

        JButton closeButton = new JButton("Đóng");
        closeButton.setBackground(DANGER_COLOR);
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setPreferredSize(new Dimension(100, 35));
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private JPanel createInvoiceSection() {
        JPanel section = createSection("Thông tin hóa đơn");
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        content.add(createLabel("Số hóa đơn:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        invoiceNoField = createTextField(false);
        content.add(invoiceNoField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        content.add(createLabel("ID Đặt phòng:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        bookingIdField = createTextField(false);
        content.add(bookingIdField, gbc);

        // Row 2
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        content.add(createLabel("Tổng phụ:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        subtotalField = createTextField(false);
        content.add(subtotalField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        content.add(createLabel("Chiết khấu:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        discountTotalField = createTextField(false);
        content.add(discountTotalField, gbc);

        // Row 3
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        content.add(createLabel("Thuế:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        taxTotalField = createTextField(false);
        content.add(taxTotalField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        content.add(createLabel("Tổng cộng:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        grandTotalField = createTextField(false);
        grandTotalField.setFont(grandTotalField.getFont().deriveFont(Font.BOLD, 14f));
        content.add(grandTotalField, gbc);

        // Row 4
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        content.add(createLabel("Trạng thái:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        status = createTextField(false);
        content.add(status, gbc);

        section.add(content, BorderLayout.CENTER);
        return section;
    }

    private JPanel createCustomerSection() {
        JPanel section = createSection("Thông tin khách hàng");
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 1
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        content.add(createLabel("Tên khách hàng:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        customerNameField = createTextField(false);
        content.add(customerNameField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        content.add(createLabel("Số điện thoại:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        customerPhoneField = createTextField(false);
        content.add(customerPhoneField, gbc);

        // Row 2
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        content.add(createLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        customerEmailField = createTextField(false);
        content.add(customerEmailField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        content.add(createLabel("CMND/CCCD:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        customerIdCardField = createTextField(false);
        content.add(customerIdCardField, gbc);

        // Row 3
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        content.add(createLabel("Quốc tịch:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        customerNationalityField = createTextField(false);
        content.add(customerNationalityField, gbc);

        section.add(content, BorderLayout.CENTER);
        return section;
    }

    private JPanel createBookingSection() {
        JPanel section = createSection("Thông tin đặt phòng");
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        content.add(createLabel("Mã đặt phòng:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        bookingCodeField = createTextField(false);
        content.add(bookingCodeField, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        content.add(createLabel("Ngày đặt:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        bookingDateField = createTextField(false);
        content.add(bookingDateField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        content.add(createLabel("Nguồn đặt:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        bookingSourceField = createTextField(false);
        content.add(bookingSourceField, gbc);

        section.add(content, BorderLayout.CENTER);
        return section;
    }

    private JPanel createRoomSection() {
        JPanel section = createSection("Phòng đặt");

        String[] columnNames = {"STT", "Số phòng", "Ngày nhận", "Ngày trả", "Giá/đêm", "Tổng tiền"};
        roomTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookingRoomTable = new JTable(roomTableModel);
        bookingRoomTable.setRowHeight(30);
        bookingRoomTable.getTableHeader().setReorderingAllowed(false);
        bookingRoomTable.setShowGrid(true);
        bookingRoomTable.setGridColor(new Color(230, 230, 230));

        JScrollPane scrollPane = new JScrollPane(bookingRoomTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.setPreferredSize(new Dimension(0, 150));

        section.add(scrollPane, BorderLayout.CENTER);
        return section;
    }

    private JPanel createServiceSection() {
        JPanel section = createSection("Dịch vụ sử dụng");

        String[] columnNames = {"STT", "Tên dịch vụ", "Đơn vị", "Đơn giá", "Số lượng", "Thành tiền"};
        serviceTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        serviceTable = new JTable(serviceTableModel);
        serviceTable.setRowHeight(30);
        serviceTable.getTableHeader().setReorderingAllowed(false);
        serviceTable.setShowGrid(true);
        serviceTable.setGridColor(new Color(230, 230, 230));

        JScrollPane scrollPane = new JScrollPane(serviceTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.setPreferredSize(new Dimension(0, 150));

        section.add(scrollPane, BorderLayout.CENTER);
        return section;
    }

    private JPanel createSection(String title) {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(Color.WHITE);
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        titleLabel.setBackground(SECTION_BG);
        titleLabel.setOpaque(true);

        section.add(titleLabel, BorderLayout.NORTH);
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
        field.setPreferredSize(new Dimension(200, 30));
        if (!editable) {
            field.setBackground(new Color(245, 245, 245));
        }
        return field;
    }

    private void loadInvoiceData() {
        invoiceNoField.setText(invoice.getInvoiceNo());
        bookingIdField.setText(String.valueOf(invoice.getBookingId()));
        subtotalField.setText(String.format("%.2f VNĐ", invoice.getSubtotal()));
        discountTotalField.setText(String.format("%.2f VNĐ", invoice.getDiscountTotal()));
        taxTotalField.setText(String.format("%.2f VNĐ", invoice.getTaxTotal()));
        grandTotalField.setText(String.format("%.2f VNĐ", invoice.getGrandTotal()));
        status.setText(invoice.getStatus());

        loadCustomerInfo();
        loadBookingInfo();
        loadRoomInfo();
        loadServiceInfo();
    }

    private void loadCustomerInfo() {
        try {
            BookingDTO booking = bookingBUS.getBookingById(invoice.getBookingId());
            if (booking != null) {
                CustomerDTO customer = customerBUS.getCustomerById(booking.getCustomerId());
                if (customer != null) {
                    customerNameField.setText(customer.getFull_name());
                    customerPhoneField.setText(customer.getPhone());
                    customerEmailField.setText(customer.getEmail() != null ? customer.getEmail() : "");
                    customerIdCardField.setText(customer.getId_card());
                    customerNationalityField.setText(customer.getNationality());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadBookingInfo() {
        try {
            BookingDTO booking = bookingBUS.getBookingById(invoice.getBookingId());
            if (booking != null) {
                bookingCodeField.setText(booking.getCode());
                bookingDateField.setText(booking.getBookingDate().toString());
                bookingSourceField.setText(booking.getSource());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRoomInfo() {
        roomTableModel.setRowCount(0);
        try {
            List<BookingRoomDTO> bookingRooms = bookingRoomBUS.getBookingRoomsByBooking(invoice.getBookingId());

            int stt = 1;
            for (BookingRoomDTO br : bookingRooms) {
                RoomDTO room = roomBUS.getRoomById(br.getRoomId());
                String roomNo = room != null ? room.getRoomNo() : "N/A";

                long nights = java.time.temporal.ChronoUnit.DAYS.between(br.getCheckInPlan(), br.getCheckOutPlan());
                double total = br.getRatePerNight().doubleValue() * nights;

                Object[] row = {
                        stt++,
                        roomNo,
                        br.getCheckInPlan().toLocalDate(),
                        br.getCheckOutPlan().toLocalDate(),
                        String.format("%.2f VNĐ", br.getRatePerNight()),
                        String.format("%.2f VNĐ", total)
                };
                roomTableModel.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadServiceInfo() {
        serviceTableModel.setRowCount(0);
        try {
            List<BookingRoomDTO> bookingRooms = bookingRoomBUS.getBookingRoomsByBooking(invoice.getBookingId());

            int stt = 1;
            for (BookingRoomDTO br : bookingRooms) {
                List<ServiceOrderDTO> services = serviceOrderBUS.getServiceOrdersByBooking(br.getBookingRoomId());
                for (ServiceOrderDTO so : services) {
                    ServiceDTO service = serviceBUS.getById(so.getServiceId());
                    if (service != null) {
                        double total = so.getUnitPrice() * so.getQuantity();

                        Object[] row = {
                                stt++,
                                service.getName(),
                                service.getUnit(),
                                String.format("%.2f VNĐ", so.getUnitPrice()),
                                so.getQuantity(),
                                String.format("%.2f VNĐ", total)
                        };
                        serviceTableModel.addRow(row);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveInvoice() {
        try {
            if (invoiceNoField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Số hóa đơn không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

//            invoice.setStatus((String) statusCombo.getSelectedItem());

            if (invoiceBUS.updateInvoice(invoice)) {
                JOptionPane.showMessageDialog(this, "Cập nhật hóa đơn thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật hóa đơn thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}