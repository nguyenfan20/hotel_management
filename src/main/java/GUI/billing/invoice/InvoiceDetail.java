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

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private JComboBox<String> statusCombo;

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
        setSize(1200, 800);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.WHITE);

        // Tab 1: Invoice Info
        JPanel invoicePanel = createInvoicePanel();
        tabbedPane.addTab("Thông tin hóa đơn", invoicePanel);

        // Tab 2: Customer Info
        JPanel customerPanel = createCustomerPanel();
        tabbedPane.addTab("Thông tin khách hàng", customerPanel);

        // Tab 3: Booking Info
        JPanel bookingPanel = createBookingPanel();
        tabbedPane.addTab("Thông tin đặt phòng", bookingPanel);

        // Tab 4: Room Info
        JPanel roomPanel = createRoomPanel();
        tabbedPane.addTab("Phòng đặt", roomPanel);

        // Tab 5: Service Info
        JPanel servicePanel = createServicePanel();
        tabbedPane.addTab("Dịch vụ sử dụng", servicePanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton saveButton = new JButton("Lưu");
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> saveInvoice());

        JButton closeButton = new JButton("Đóng");
        closeButton.setBackground(new Color(231, 76, 60));
        closeButton.setForeground(Color.WHITE);
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createInvoicePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Số hóa đơn
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Số hóa đơn:"), gbc);
        invoiceNoField = new JTextField(25);
        invoiceNoField.setEditable(false);
        gbc.gridx = 1;
        panel.add(invoiceNoField, gbc);

        // Đặt phòng
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("ID Đặt phòng:"), gbc);
        bookingIdField = new JTextField(25);
        bookingIdField.setEditable(false);
        gbc.gridx = 1;
        panel.add(bookingIdField, gbc);

        // Tổng phụ
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Tổng phụ:"), gbc);
        subtotalField = new JTextField(25);
        subtotalField.setEditable(false);
        gbc.gridx = 1;
        panel.add(subtotalField, gbc);

        // Chiết khấu
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Chiết khấu:"), gbc);
        discountTotalField = new JTextField(25);
        discountTotalField.setEditable(false);
        gbc.gridx = 1;
        panel.add(discountTotalField, gbc);

        // Thuế
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Thuế:"), gbc);
        taxTotalField = new JTextField(25);
        taxTotalField.setEditable(false);
        gbc.gridx = 1;
        panel.add(taxTotalField, gbc);

        // Tổng cộng
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Tổng cộng:"), gbc);
        grandTotalField = new JTextField(25);
        grandTotalField.setEditable(false);
        gbc.gridx = 1;
        panel.add(grandTotalField, gbc);

        // Trạng thái
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Trạng thái:"), gbc);
        statusCombo = new JComboBox<>(new String[]{"Draft", "Issued", "Paid", "Cancelled"});
        gbc.gridx = 1;
        panel.add(statusCombo, gbc);

        return panel;
    }

    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Tên khách hàng
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Tên khách hàng:"), gbc);
        customerNameField = new JTextField(25);
        customerNameField.setEditable(false);
        gbc.gridx = 1;
        panel.add(customerNameField, gbc);

        // Số điện thoại
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Số điện thoại:"), gbc);
        customerPhoneField = new JTextField(25);
        customerPhoneField.setEditable(false);
        gbc.gridx = 1;
        panel.add(customerPhoneField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Email:"), gbc);
        customerEmailField = new JTextField(25);
        customerEmailField.setEditable(false);
        gbc.gridx = 1;
        panel.add(customerEmailField, gbc);

        // CMND/CCCD
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("CMND/CCCD:"), gbc);
        customerIdCardField = new JTextField(25);
        customerIdCardField.setEditable(false);
        gbc.gridx = 1;
        panel.add(customerIdCardField, gbc);

        // Quốc tịch
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Quốc tịch:"), gbc);
        customerNationalityField = new JTextField(25);
        customerNationalityField.setEditable(false);
        gbc.gridx = 1;
        panel.add(customerNationalityField, gbc);

        return panel;
    }

    private JPanel createBookingPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Mã đặt phòng
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Mã đặt phòng:"), gbc);
        bookingCodeField = new JTextField(25);
        bookingCodeField.setEditable(false);
        gbc.gridx = 1;
        panel.add(bookingCodeField, gbc);

        // Ngày đặt
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Ngày đặt:"), gbc);
        bookingDateField = new JTextField(25);
        bookingDateField.setEditable(false);
        gbc.gridx = 1;
        panel.add(bookingDateField, gbc);

        // Nguồn đặt
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Nguồn đặt:"), gbc);
        bookingSourceField = new JTextField(25);
        bookingSourceField.setEditable(false);
        gbc.gridx = 1;
        panel.add(bookingSourceField, gbc);

        return panel;
    }

    private JPanel createRoomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] columnNames = {"STT", "Số phòng", "Ngày nhận", "Ngày trả", "Giá/đêm", "Tổng tiền"};
        roomTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookingRoomTable = new JTable(roomTableModel);
        bookingRoomTable.setRowHeight(25);
        bookingRoomTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(bookingRoomTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createServicePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] columnNames = {"STT", "Tên dịch vụ", "Đơn vị", "Đơn giá", "Số lượng", "Thành tiền"};
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
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadInvoiceData() {
        invoiceNoField.setText(invoice.getInvoiceNo());
        bookingIdField.setText(String.valueOf(invoice.getBookingId()));
        subtotalField.setText(String.format("%.2f", invoice.getSubtotal()));
        discountTotalField.setText(String.format("%.2f", invoice.getDiscountTotal()));
        taxTotalField.setText(String.format("%.2f", invoice.getTaxTotal()));
        grandTotalField.setText(String.format("%.2f", invoice.getGrandTotal()));
        statusCombo.setSelectedItem(invoice.getStatus());

        loadCustomerInfo();
        loadBookingInfo();
        loadRoomInfo();
        loadServiceInfo();
    }

    private void loadCustomerInfo() {
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
    }

    private void loadBookingInfo() {
        BookingDTO booking = bookingBUS.getBookingById(invoice.getBookingId());
        if (booking != null) {
            bookingCodeField.setText(booking.getCode());
            bookingDateField.setText(booking.getBookingDate().toString());
            bookingSourceField.setText(booking.getSource());
        }
    }

    private void loadRoomInfo() {
        roomTableModel.setRowCount(0);
        List<BookingRoomDTO> bookingRooms = bookingRoomBUS.getBookingRoomsByBooking(invoice.getBookingId());

        int stt = 1;
        for (BookingRoomDTO br : bookingRooms) {
            RoomDTO room = roomBUS.getRoomById(br.getRoomId());
            String roomNo = room != null ? room.getRoomNo() : "N/A";

            Object[] row = {
                    stt++,
                    roomNo,
                    br.getCheckInPlan().toLocalDate(),
                    br.getCheckOutPlan().toLocalDate(),
                    String.format("%.2f", br.getRatePerNight()),
                    String.format("%.2f", br.getRatePerNight().multiply(new java.math.BigDecimal(
                            java.time.temporal.ChronoUnit.DAYS.between(br.getCheckInPlan(), br.getCheckOutPlan())
                    )))
            };
            roomTableModel.addRow(row);
        }
    }

    private void loadServiceInfo() {
        serviceTableModel.setRowCount(0);
        List<BookingRoomDTO> bookingRooms = bookingRoomBUS.getBookingRoomsByBooking(invoice.getBookingId());

        int stt = 1;
        for (BookingRoomDTO br : bookingRooms) {
            List<ServiceOrderDTO> services = serviceOrderBUS.getServiceOrdersByBooking(invoice.getBookingId());

            for (ServiceOrderDTO so : services) {
                ServiceDTO service = serviceBUS.getById(so.getServiceId());
                if (service != null) {
                    double total = so.getUnitPrice() * so.getQuantity();

                    Object[] row = {
                            stt++,
                            service.getName(),
                            service.getUnit(),
                            String.format("%.2f", so.getUnitPrice()),
                            so.getQuantity(),
                            String.format("%.2f", total)
                    };
                    serviceTableModel.addRow(row);
                }
            }
        }
    }

    private void saveInvoice() {
        try {
            if (invoiceNoField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Số hóa đơn không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            invoice.setStatus((String) statusCombo.getSelectedItem());

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
