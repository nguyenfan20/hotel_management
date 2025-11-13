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

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.kernel.geom.PageSize;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

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

    // Colors for PDF
    private static final com.itextpdf.kernel.colors.Color PRIMARY_COLOR_ITEXT =
            new DeviceRgb(41, 98, 255);
    private static final com.itextpdf.kernel.colors.Color SUCCESS_COLOR_ITEXT =
            new DeviceRgb(46, 204, 113);
    private static final com.itextpdf.kernel.colors.Color DANGER_COLOR_ITEXT =
            new DeviceRgb(231, 76, 60);
    private static final com.itextpdf.kernel.colors.Color LIGHT_GRAY_ITEXT =
            new DeviceRgb(248, 249, 250);
    private static final com.itextpdf.kernel.colors.Color HEADER_BG_ITEXT =
            new DeviceRgb(230, 240, 255);

    // Colors for UI
    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color SECTION_BG = new Color(248, 249, 250);
    private static final Color INFO_COLOR = new Color(52, 152, 219);

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

        // Nút In PDF
        JButton printButton = new JButton("In PDF");
        printButton.setBackground(INFO_COLOR);
        printButton.setForeground(Color.WHITE);
        printButton.setFocusPainted(false);
        printButton.setPreferredSize(new Dimension(100, 35));
        printButton.addActionListener(e -> exportToPDF());

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

        buttonPanel.add(printButton);
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

    private void exportToPDF() {
        // Kiểm tra trạng thái thanh toán
        if (!"PAID".equalsIgnoreCase(invoice.getStatus())) {
            JOptionPane.showMessageDialog(this,
                    "Chỉ có thể in hóa đơn đã thanh toán!\nTrạng thái hiện tại: " + invoice.getStatus(),
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Chọn nơi lưu file
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu hóa đơn PDF");

        // Tạo tên file mặc định
        String defaultFileName = "HoaDon_" + invoice.getInvoiceNo().replaceAll("[^a-zA-Z0-9]", "_") + ".pdf";
        fileChooser.setSelectedFile(new File(defaultFileName));

        // Lọc chỉ file PDF
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Files", "pdf"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();

            // Đảm bảo file có đuôi .pdf
            if (!filePath.toLowerCase().endsWith(".pdf")) {
                filePath += ".pdf";
            }

            try {
                createPDF(filePath);

                int choice = JOptionPane.showOptionDialog(this,
                        "Xuất PDF thành công!\nFile được lưu tại:\n" + filePath + "\n\nBạn có muốn mở file?",
                        "Thành công",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        new String[]{"Mở file", "Đóng"},
                        "Mở file");

                if (choice == 0) {
                    // Kiểm tra xem Desktop có được hỗ trợ không
                    if (Desktop.isDesktopSupported()) {
                        Desktop desktop = Desktop.getDesktop();
                        File pdfFile = new File(filePath);
                        if (pdfFile.exists()) {
                            desktop.open(pdfFile);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Không thể mở file tự động.\nVui lòng mở file thủ công tại:\n" + filePath,
                                "Thông báo",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Lỗi khi xuất PDF:\n" + ex.getMessage() + "\n\nVui lòng kiểm tra:\n" +
                                "1. Quyền ghi file\n" +
                                "2. Đường dẫn hợp lệ\n" +
                                "3. File không đang được mở bởi chương trình khác",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void createPDF(String dest) throws IOException {
        PdfWriter writer = null;
        PdfDocument pdf = null;
        Document document = null;

        try {
            // Kiểm tra xem file có thể ghi được không
            File destFile = new File(dest);
            if (destFile.exists()) {
                // Nếu file đã tồn tại, thử xóa nó
                if (!destFile.delete()) {
                    throw new IOException("Không thể ghi đè file. File có thể đang được mở bởi chương trình khác.");
                }
            }

            // Tạo thư mục nếu chưa tồn tại
            File parentDir = destFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            // Khởi tạo PDF và Document
            writer = new PdfWriter(dest);
            writer.setCloseStream(true);

            pdf = new PdfDocument(writer);

            document = new Document(pdf, PageSize.A4);
            document.setMargins(30, 30, 30, 30);


            // Load Font cho Tiếng Việt - TRY MULTIPLE PATHS
            PdfFont vietnameseFont = loadVietnameseFont();

            if (vietnameseFont == null) {
                throw new IOException("Cannot load any font for PDF generation");
            }

            // Định nghĩa font sizes
            float titleSize = 20f;
            float headerSize = 13f;
            float normalSize = 11f;
            float smallSize = 9f;

            // Lấy thông tin
            BookingDTO booking = bookingBUS.getBookingById(invoice.getBookingId());
            CustomerDTO customer = (booking != null) ? customerBUS.getCustomerById(booking.getCustomerId()) : null;
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            // Chuyển đổi Timestamp sang LocalDateTime để format
            String invoiceDate = "";
            if (invoice.getCreatedAt() != null) {
                // Chuyển Timestamp -> LocalDateTime -> format
                invoiceDate = invoice.getCreatedAt().toLocalDateTime().format(dateFormatter);
            }

            // --- HEADER: LOGO & THÔNG TIN KHÁCH SẠN ---
            Table headerTable = new Table(new float[]{1, 2})
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginBottom(20);

            // Logo/Tên khách sạn
            Cell hotelInfo = new Cell()
                    .add(new Paragraph("KHÁCH SẠN SGU")
                            .setFont(vietnameseFont)
                            .setFontSize(16f)
                            .setBold()
                            .setFontColor(PRIMARY_COLOR_ITEXT))
                    .add(new Paragraph("273 An Dương Vương, Phường, Chợ Quán, Thành phố Hồ Chí Minh")
                            .setFont(vietnameseFont)
                            .setFontSize(smallSize))
                    .add(new Paragraph("ĐT: (028) 1234 5678 | Email: info@sgu.com")
                            .setFont(vietnameseFont)
                            .setFontSize(smallSize))
                    .setBorder(Border.NO_BORDER)
                    .setPadding(0);
            headerTable.addCell(hotelInfo);

            // Thông tin hóa đơn bên phải
            Cell invoiceInfo = new Cell()
                    .add(new Paragraph("HÓA ĐƠN THANH TOÁN")
                            .setFont(vietnameseFont)
                            .setFontSize(titleSize)
                            .setBold()
                            .setFontColor(PRIMARY_COLOR_ITEXT)
                            .setTextAlignment(TextAlignment.RIGHT))
                    .add(new Paragraph("Số: " + invoice.getInvoiceNo())
                            .setFont(vietnameseFont)
                            .setFontSize(normalSize)
                            .setTextAlignment(TextAlignment.RIGHT))
                    .add(new Paragraph("Ngày: " + invoiceDate)
                            .setFont(vietnameseFont)
                            .setFontSize(normalSize)
                            .setTextAlignment(TextAlignment.RIGHT))
                    .setBorder(Border.NO_BORDER)
                    .setPadding(0);
            headerTable.addCell(invoiceInfo);
            document.add(headerTable);

            // --- THÔNG TIN KHÁCH HÀNG ---
            document.add(new Paragraph("THÔNG TIN KHÁCH HÀNG")
                    .setFont(vietnameseFont)
                    .setFontSize(headerSize)
                    .setBold()
                    .setFontColor(PRIMARY_COLOR_ITEXT)
                    .setMarginTop(10)
                    .setMarginBottom(8));

            Table customerTable = new Table(new float[]{1.2f, 2.8f, 1.2f, 2.8f})
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginBottom(15)
                    .setFont(vietnameseFont)
                    .setFontSize(normalSize);

            addInfoCell(customerTable, "Họ và tên:", customer != null ? customer.getFull_name() : "N/A", vietnameseFont);
            addInfoCell(customerTable, "Số điện thoại:", customer != null ? customer.getPhone() : "N/A", vietnameseFont);
            addInfoCell(customerTable, "CMND/CCCD:", customer != null ? customer.getId_card() : "N/A", vietnameseFont);
            addInfoCell(customerTable, "Email:", customer != null && customer.getEmail() != null ? customer.getEmail() : "N/A", vietnameseFont);
            addInfoCell(customerTable, "Quốc tịch:", customer != null ? customer.getNationality() : "N/A", vietnameseFont);
            addInfoCell(customerTable, "Mã Booking:", booking != null ? booking.getCode() : "N/A", vietnameseFont);

            document.add(customerTable);

            // Đường kẻ ngăn cách
            document.add(new Paragraph().setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 1)).setMarginBottom(15));

            // --- CHI TIẾT PHÒNG ---
            document.add(new Paragraph("CHI TIẾT PHÒNG ĐẶT")
                    .setFont(vietnameseFont)
                    .setFontSize(headerSize)
                    .setBold()
                    .setFontColor(PRIMARY_COLOR_ITEXT)
                    .setMarginBottom(10));

            Table roomTable = new Table(new float[]{0.5f, 1.5f, 1.5f, 1.5f, 1.5f, 2f})
                    .setWidth(UnitValue.createPercentValue(100))
                    .setFont(vietnameseFont)
                    .setMarginBottom(15);

            // Header bảng phòng
            addTableHeader(roomTable, "STT", vietnameseFont);
            addTableHeader(roomTable, "Số phòng", vietnameseFont);
            addTableHeader(roomTable, "Ngày nhận", vietnameseFont);
            addTableHeader(roomTable, "Ngày trả", vietnameseFont);
            addTableHeader(roomTable, "Giá/đêm", vietnameseFont);
            addTableHeader(roomTable, "Tổng tiền", vietnameseFont);

            // Dữ liệu phòng
            for (int i = 0; i < roomTableModel.getRowCount(); i++) {
                addTableCell(roomTable, roomTableModel.getValueAt(i, 0).toString(), vietnameseFont, TextAlignment.CENTER);
                addTableCell(roomTable, roomTableModel.getValueAt(i, 1).toString(), vietnameseFont, TextAlignment.LEFT);
                addTableCell(roomTable, roomTableModel.getValueAt(i, 2).toString(), vietnameseFont, TextAlignment.CENTER);
                addTableCell(roomTable, roomTableModel.getValueAt(i, 3).toString(), vietnameseFont, TextAlignment.CENTER);
                addTableCell(roomTable, roomTableModel.getValueAt(i, 4).toString(), vietnameseFont, TextAlignment.RIGHT);
                addTableCell(roomTable, roomTableModel.getValueAt(i, 5).toString(), vietnameseFont, TextAlignment.RIGHT);
            }
            document.add(roomTable);

            // --- CHI TIẾT DỊCH VỤ (nếu có) ---
            if (serviceTableModel.getRowCount() > 0) {
                document.add(new Paragraph("CHI TIẾT DỊCH VỤ SỬ DỤNG")
                        .setFont(vietnameseFont)
                        .setFontSize(headerSize)
                        .setBold()
                        .setFontColor(PRIMARY_COLOR_ITEXT)
                        .setMarginBottom(10));

                Table serviceTablePdf = new Table(new float[]{0.5f, 2f, 1f, 1.5f, 1f, 1.5f})
                        .setWidth(UnitValue.createPercentValue(100))
                        .setFont(vietnameseFont)
                        .setMarginBottom(15);

                // Header bảng dịch vụ
                addTableHeader(serviceTablePdf, "STT", vietnameseFont);
                addTableHeader(serviceTablePdf, "Tên dịch vụ", vietnameseFont);
                addTableHeader(serviceTablePdf, "Đơn vị", vietnameseFont);
                addTableHeader(serviceTablePdf, "Đơn giá", vietnameseFont);
                addTableHeader(serviceTablePdf, "SL", vietnameseFont);
                addTableHeader(serviceTablePdf, "Thành tiền", vietnameseFont);

                // Dữ liệu dịch vụ
                for (int i = 0; i < serviceTableModel.getRowCount(); i++) {
                    addTableCell(serviceTablePdf, serviceTableModel.getValueAt(i, 0).toString(), vietnameseFont, TextAlignment.CENTER);
                    addTableCell(serviceTablePdf, serviceTableModel.getValueAt(i, 1).toString(), vietnameseFont, TextAlignment.LEFT);
                    addTableCell(serviceTablePdf, serviceTableModel.getValueAt(i, 2).toString(), vietnameseFont, TextAlignment.CENTER);
                    addTableCell(serviceTablePdf, serviceTableModel.getValueAt(i, 3).toString(), vietnameseFont, TextAlignment.RIGHT);
                    addTableCell(serviceTablePdf, serviceTableModel.getValueAt(i, 4).toString(), vietnameseFont, TextAlignment.CENTER);
                    addTableCell(serviceTablePdf, serviceTableModel.getValueAt(i, 5).toString(), vietnameseFont, TextAlignment.RIGHT);
                }
                document.add(serviceTablePdf);
            }

            // --- TỔNG KẾT ---
            Table summaryTable = new Table(new float[]{3f, 2f})
                    .setWidth(UnitValue.createPercentValue(50))
                    .setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.RIGHT)
                    .setMarginTop(20)
                    .setFont(vietnameseFont)
                    .setFontSize(normalSize);

            // Tổng phụ
            addSummaryRow(summaryTable, "Tổng phụ:", String.format("%.0f VNĐ", invoice.getSubtotal()),
                    vietnameseFont, normalSize, false);

            // Chiết khấu
            if (invoice.getDiscountTotal() > 0) {
                addSummaryRow(summaryTable, "Chiết khấu:", String.format("-%.0f VNĐ", invoice.getDiscountTotal()),
                        vietnameseFont, normalSize, false);
            }

            // Thuế
            if (invoice.getTaxTotal() > 0) {
                addSummaryRow(summaryTable, "Thuế (VAT):", String.format("%.0f VNĐ", invoice.getTaxTotal()),
                        vietnameseFont, normalSize, false);
            }

            // Đường kẻ trước tổng
            Cell separatorCell1 = new Cell(1, 2)
                    .setBorder(Border.NO_BORDER)
                    .setBorderTop(new SolidBorder(ColorConstants.GRAY, 1))
                    .setPadding(5);
            summaryTable.addCell(separatorCell1);

            // Tổng thanh toán
            Cell totalLabelCell = new Cell()
                    .add(new Paragraph("TỔNG THANH TOÁN:")
                            .setBold()
                            .setFontSize(14f)
                            .setFontColor(DANGER_COLOR_ITEXT))
                    .setBorder(Border.NO_BORDER)
                    .setPaddingTop(5)
                    .setBackgroundColor(LIGHT_GRAY_ITEXT);
            summaryTable.addCell(totalLabelCell);

            Cell totalValueCell = new Cell()
                    .add(new Paragraph(String.format("%.0f VNĐ", invoice.getGrandTotal()))
                            .setBold()
                            .setFontSize(14f)
                            .setFontColor(DANGER_COLOR_ITEXT))
                    .setBorder(Border.NO_BORDER)
                    .setPaddingTop(5)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setBackgroundColor(LIGHT_GRAY_ITEXT);
            summaryTable.addCell(totalValueCell);

            document.add(summaryTable);

            // --- TRẠNG THÁI THANH TOÁN ---
            Paragraph statusPara = new Paragraph()
                    .add(new Paragraph("Trạng thái: ")
                            .setFont(vietnameseFont)
                            .setFontSize(normalSize)
                            .setBold())
                    .add(new Paragraph(invoice.getStatus().equals("PAID") ? "ĐÃ THANH TOÁN" : invoice.getStatus())
                            .setFont(vietnameseFont)
                            .setFontSize(normalSize)
                            .setBold()
                            .setFontColor(SUCCESS_COLOR_ITEXT))
                    .setMarginTop(15)
                    .setTextAlignment(TextAlignment.RIGHT);
            document.add(statusPara);

            // --- CHỮ KÝ ---
            Table signatureTable = new Table(new float[]{1, 1})
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginTop(40)
                    .setFont(vietnameseFont)
                    .setFontSize(normalSize);

            Cell customerSign = new Cell()
                    .add(new Paragraph("Khách hàng")
                            .setBold()
                            .setTextAlignment(TextAlignment.CENTER))
                    .add(new Paragraph("\n\n\n")
                            .setFontSize(smallSize))
                    .add(new Paragraph("(Ký và ghi rõ họ tên)")
                            .setFontSize(smallSize)
                            .setTextAlignment(TextAlignment.CENTER))
                    .setBorder(Border.NO_BORDER);
            signatureTable.addCell(customerSign);

            Cell hotelSign = new Cell()
                    .add(new Paragraph("Đại diện khách sạn")
                            .setBold()
                            .setTextAlignment(TextAlignment.CENTER))
                    .add(new Paragraph("\n\n\n")
                            .setFontSize(smallSize))
                    .add(new Paragraph("(Ký và đóng dấu)")
                            .setFontSize(smallSize)
                            .setTextAlignment(TextAlignment.CENTER))
                    .setBorder(Border.NO_BORDER);
            signatureTable.addCell(hotelSign);

            document.add(signatureTable);

            // --- FOOTER ---
            Paragraph footer = new Paragraph("Cảm ơn quý khách đã sử dụng dịch vụ của chúng tôi!")
                    .setFont(vietnameseFont)
                    .setFontSize(smallSize)
                    .setItalic()
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(10)
                    .setFontColor(ColorConstants.GRAY);
            document.add(footer);

            // QUAN TRỌNG: Đóng document để flush nội dung
            document.close();

            // Verify file was created
            File verifyFile = new File(dest);
            if (verifyFile.exists() && verifyFile.length() > 0) {
            } else {
                throw new IOException("PDF file was not created properly");
            }

        } catch (Exception e) {
            System.err.println("Error during PDF creation: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Lỗi khi tạo PDF: " + e.getMessage(), e);
        }
        // Document.close() đã tự động close pdf và writer, không cần finally block
    }

    private PdfFont loadVietnameseFont() throws IOException {
        String[] fontPaths = {
                "c:\\windows\\fonts\\arial.ttf",           // Windows
                "c:\\windows\\fonts\\times.ttf",           // Windows fallback
                "/System/Library/Fonts/Supplemental/Arial.ttf", // macOS
                "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf", // Linux
                "fonts/arial.ttf"                          // Relative path in project
        };

        for (String fontPath : fontPaths) {
            try {
                File fontFile = new File(fontPath);
                if (fontFile.exists()) {
                    return PdfFontFactory.createFont(fontPath, "Identity-H",
                            PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
                }
            } catch (IOException e) {
                // Try next font
                continue;
            }
        }

        // Nếu không tìm thấy font nào, sử dụng font mặc định
        return PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA);
    }

    // Hàm hỗ trợ: Thêm thông tin khách hàng (2 cột)
    private void addInfoCell(Table table, String label, String value, PdfFont font) {
        Cell labelCell = new Cell()
                .add(new Paragraph(label)
                        .setFont(font)
                        .setBold())
                .setBorder(Border.NO_BORDER)
                .setPadding(5)
                .setBackgroundColor(LIGHT_GRAY_ITEXT);
        table.addCell(labelCell);

        Cell valueCell = new Cell()
                .add(new Paragraph(value)
                        .setFont(font))
                .setBorder(Border.NO_BORDER)
                .setPadding(5);
        table.addCell(valueCell);
    }

    // Hàm hỗ trợ: Header bảng
    private void addTableHeader(Table table, String text, PdfFont font) {
        Cell headerCell = new Cell()
                .add(new Paragraph(text)
                        .setFont(font)
                        .setBold()
                        .setFontSize(11f))
                .setBackgroundColor(HEADER_BG_ITEXT)
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 1))
                .setPadding(8)
                .setTextAlignment(TextAlignment.CENTER);
        table.addCell(headerCell);
    }

    // Hàm hỗ trợ: Cell bảng
    private void addTableCell(Table table, String text, PdfFont font, TextAlignment alignment) {
        Cell cell = new Cell()
                .add(new Paragraph(text)
                        .setFont(font)
                        .setFontSize(10f))
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 0.5f))
                .setPadding(6)
                .setTextAlignment(alignment);
        table.addCell(cell);
    }

    // Hàm hỗ trợ: Dòng tổng kết
    private void addSummaryRow(Table table, String label, String value, PdfFont font,
                               float fontSize, boolean isBold) {
        Cell labelCell = new Cell()
                .add(new Paragraph(label)
                        .setFont(font)
                        .setFontSize(fontSize)
                        .setBold())
                .setBorder(Border.NO_BORDER)
                .setPaddingTop(4)
                .setPaddingBottom(4);
        table.addCell(labelCell);

        Paragraph valuePara = new Paragraph(value)
                .setFont(font)
                .setFontSize(fontSize);

        if (isBold) {
            valuePara.setBold();
        }

        Cell valueCell = new Cell()
                .add(valuePara)
                .setBorder(Border.NO_BORDER)
                .setPaddingTop(4)
                .setPaddingBottom(4)
                .setTextAlignment(TextAlignment.RIGHT);
        table.addCell(valueCell);
    }
}