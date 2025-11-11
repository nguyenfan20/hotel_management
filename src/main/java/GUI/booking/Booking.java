package GUI.booking;

import BUS.*;
import DTO.*;
import GUI.dashboard.Form_Home;
import util.SimpleDocumentListener;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Booking extends javax.swing.JPanel {
    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    private static final Color AVAILABLE_COLOR = new Color(232, 245, 233);
    private static final Color SELECTED_COLOR = new Color(200, 220, 255);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color PANEL_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(224, 224, 224);
    private static final Color TEXT_COLOR = new Color(33, 33, 33);

    private BookingBUS bookingBUS;
    private BookingRoomBUS bookingRoomBUS;
    private CustomerBUS customerBUS;
    private RoomBUS roomBUS;
    private RoomTypeBUS roomTypeBUS;

    private Map<Integer, String> roomTypes = new HashMap<>();
    private List<Integer> selectedRoomIds = new ArrayList<>();
    private Map<Integer, JButton> roomButtonMap = new HashMap<>();

    private CustomerDTO currentCustomer;
    private JPanel customerInfoPanel;
    private JComboBox<String> customerCombo;
    private JTextField phoneField;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField idCardField;
    private JTextField nationalityField;
    private JTextField genderField;

    private JTextField dateCheckinField;
    private JTextField dateCheckoutField;
    private JSpinner guestAdultsSpinner;
    private JSpinner guestChildrenSpinner;
    private JTextField noteField;

    public Booking() {
        bookingBUS = new BookingBUS(new DAO.BookingDAO());
        bookingRoomBUS = new BookingRoomBUS(new DAO.BookingRoomDAO());
        customerBUS = new CustomerBUS();
        roomBUS = new RoomBUS();
        roomTypeBUS = new RoomTypeBUS();
        initComponents();
        loadRoomTypes();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        JScrollPane mainScroll = new JScrollPane();
        mainScroll.setBorder(null);
        mainScroll.getViewport().setBackground(BACKGROUND_COLOR);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // === PHẦN THÔNG TIN KHÁCH HÀNG ===
        customerInfoPanel = createCustomerSection();
        mainPanel.add(customerInfoPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // === PHẦN NGÀY ===
        JPanel dateSection = createDateSection();
        mainPanel.add(dateSection);
        mainPanel.add(Box.createVerticalStrut(20));

        // === PHẦN SỐ NGƯỜI ===
        JPanel guestSection = createGuestSection();
        mainPanel.add(guestSection);
        mainPanel.add(Box.createVerticalStrut(20));

        // === PHẦN CHỌN PHÒNG ===
        JPanel roomSelectionSection = createRoomSelectionSection();
        mainPanel.add(roomSelectionSection);
        mainPanel.add(Box.createVerticalStrut(20));

        // === PHẦN GHI CHÚ ===
        JPanel noteSection = createNoteSection();
        mainPanel.add(noteSection);
        mainPanel.add(Box.createVerticalGlue());

        mainScroll.setViewportView(mainPanel);
        add(mainScroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(PANEL_BG);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));

        JButton submitButton = new JButton("Tạo đơn đặt");
        submitButton.setPreferredSize(new Dimension(130, 35));
        submitButton.setBackground(PRIMARY_COLOR);
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.setBorderPainted(false);
        submitButton.setFont(new Font("Arial", Font.BOLD, 13));
        submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitButton.addActionListener(e -> submitBooking());
        buttonPanel.add(submitButton);

        JButton resetButton = new JButton("Đặt lại");
        resetButton.setPreferredSize(new Dimension(100, 35));
        resetButton.setBackground(BORDER_COLOR);
        resetButton.setForeground(TEXT_COLOR);
        resetButton.setFocusPainted(false);
        resetButton.setBorderPainted(false);
        resetButton.setFont(new Font("Arial", Font.BOLD, 13));
        resetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetButton.addActionListener(e -> resetForm());
        buttonPanel.add(resetButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createCustomerSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(PANEL_BG);
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel("Thông tin khách hàng");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(PRIMARY_COLOR);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(PANEL_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Search panel
        JPanel searchPanel = new JPanel(new GridLayout(1, 2, 15, 15));
        searchPanel.setBackground(PANEL_BG);

        JLabel phoneLabel = new JLabel("Số điện thoại:");
        phoneLabel.setFont(new Font("Arial", Font.BOLD, 12));
        phoneField = new JTextField();
        phoneField.setPreferredSize(new Dimension(200, 35));
        phoneField.setFont(new Font("Arial", Font.PLAIN, 12));
        phoneField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JButton searchCustomerButton = new JButton("Tìm kiếm");
        searchCustomerButton.setPreferredSize(new Dimension(100, 35));
        searchCustomerButton.setBackground(PRIMARY_COLOR);
        searchCustomerButton.setForeground(Color.WHITE);
        searchCustomerButton.setFocusPainted(false);
        searchCustomerButton.setBorderPainted(false);
        searchCustomerButton.setFont(new Font("Arial", Font.BOLD, 12));
        searchCustomerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel phonePanel = new JPanel(new BorderLayout(5, 0));
        phonePanel.setBackground(PANEL_BG);
        phonePanel.add(phoneField, BorderLayout.CENTER);
        phonePanel.add(searchCustomerButton, BorderLayout.EAST);

        searchPanel.add(phoneLabel);
        searchPanel.add(phonePanel);

        contentPanel.add(searchPanel, BorderLayout.NORTH);

        // Customer info panel (initially hidden)
        JPanel customerDetailsPanel = new JPanel(new GridLayout(6, 2, 15, 15));
        customerDetailsPanel.setBackground(PANEL_BG);
        customerDetailsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        customerDetailsPanel.setVisible(false);

        nameField = createReadOnlyTextField("");
        emailField = createReadOnlyTextField("");
        idCardField = createReadOnlyTextField("");
        nationalityField = createReadOnlyTextField("");
        genderField = createReadOnlyTextField("");
        JTextField noteField = createReadOnlyTextField("");

        customerDetailsPanel.add(new JLabel("Họ tên:")).setFont(new Font("Arial", Font.BOLD, 12));
        customerDetailsPanel.add(nameField);
        customerDetailsPanel.add(new JLabel("Email:")).setFont(new Font("Arial", Font.BOLD, 12));
        customerDetailsPanel.add(emailField);
        customerDetailsPanel.add(new JLabel("CMND/CCCD:")).setFont(new Font("Arial", Font.BOLD, 12));
        customerDetailsPanel.add(idCardField);
        customerDetailsPanel.add(new JLabel("Quốc tịch:")).setFont(new Font("Arial", Font.BOLD, 12));
        customerDetailsPanel.add(nationalityField);
        customerDetailsPanel.add(new JLabel("Giới tính:")).setFont(new Font("Arial", Font.BOLD, 12));
        customerDetailsPanel.add(genderField);
        customerDetailsPanel.add(new JLabel("Ghi chú:")).setFont(new Font("Arial", Font.BOLD, 12));
        customerDetailsPanel.add(noteField);

        contentPanel.add(customerDetailsPanel, BorderLayout.CENTER);

        searchCustomerButton.addActionListener(e -> {
            String phone = phoneField.getText().trim();
            if (phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại!");
                return;
            }

            try {

                CustomerDTO foundCustomer = customerBUS.getCustomerByPhone(phone);

                if (foundCustomer != null) {
                    currentCustomer = foundCustomer;
                    nameField.setText(foundCustomer.getFull_name());
                    emailField.setText(foundCustomer.getEmail() != null ? foundCustomer.getEmail() : "");
                    idCardField.setText(foundCustomer.getId_card());
                    nationalityField.setText(foundCustomer.getNationality());
                    genderField.setText(foundCustomer.getGender());
                    customerDetailsPanel.setVisible(true);
                    JOptionPane.showMessageDialog(this, "Tìm thấy khách hàng: " + foundCustomer.getFull_name());
                } else {
                    currentCustomer = null;
                    nameField.setText("");
                    emailField.setText("");
                    idCardField.setText("");
                    nationalityField.setText("");
                    genderField.setText("");
                    customerDetailsPanel.setVisible(false);
                    JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng. Vui lòng nhập thông tin khách hàng mới bên dưới.");
                }
                customerInfoPanel.revalidate();
                customerInfoPanel.repaint();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
            }
        });

        section.add(titleLabel, BorderLayout.NORTH);
        section.add(contentPanel, BorderLayout.CENTER);
        return section;
    }

    private JTextField createReadOnlyTextField(String text) {
        JTextField field = new JTextField(text);
        field.setEditable(false);
        field.setFont(new Font("Arial", Font.PLAIN, 12));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setBackground(new Color(240, 240, 240));
        field.setForeground(TEXT_COLOR);
        return field;
    }

    private JPanel createDateSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(PANEL_BG);
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel("Ngày nhận và trả phòng");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(PRIMARY_COLOR);

        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 15, 15));
        contentPanel.setBackground(PANEL_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel checkinLabel = new JLabel("Ngày nhận phòng (yyyy-MM-dd):");
        checkinLabel.setFont(new Font("Arial", Font.BOLD, 12));
        dateCheckinField = new JTextField();
        dateCheckinField.setText(LocalDate.now().toString());
        dateCheckinField.setFont(new Font("Arial", Font.PLAIN, 12));
        dateCheckinField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JPanel checkinPanel = new JPanel(new BorderLayout(0, 5));
        checkinPanel.setBackground(PANEL_BG);
        checkinPanel.add(checkinLabel, BorderLayout.NORTH);
        checkinPanel.add(dateCheckinField, BorderLayout.CENTER);

        JLabel checkoutLabel = new JLabel("Ngày trả phòng (yyyy-MM-dd):");
        checkoutLabel.setFont(new Font("Arial", Font.BOLD, 12));
        dateCheckoutField = new JTextField();
        dateCheckoutField.setText(LocalDate.now().plusDays(1).toString());
        dateCheckoutField.setFont(new Font("Arial", Font.PLAIN, 12));
        dateCheckoutField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JPanel checkoutPanel = new JPanel(new BorderLayout(0, 5));
        checkoutPanel.setBackground(PANEL_BG);
        checkoutPanel.add(checkoutLabel, BorderLayout.NORTH);
        checkoutPanel.add(dateCheckoutField, BorderLayout.CENTER);

        contentPanel.add(checkinPanel);
        contentPanel.add(checkoutPanel);

        section.add(titleLabel, BorderLayout.NORTH);
        section.add(contentPanel, BorderLayout.CENTER);
        return section;
    }

    private JPanel createGuestSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(PANEL_BG);
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel("Số lượng khách");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(PRIMARY_COLOR);

        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 15, 15));
        contentPanel.setBackground(PANEL_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel adultsLabel = new JLabel("Số người lớn:");
        adultsLabel.setFont(new Font("Arial", Font.BOLD, 12));
        guestAdultsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        guestAdultsSpinner.setFont(new Font("Arial", Font.PLAIN, 12));

        JPanel adultsPanel = new JPanel(new BorderLayout(0, 5));
        adultsPanel.setBackground(PANEL_BG);
        adultsPanel.add(adultsLabel, BorderLayout.NORTH);
        adultsPanel.add(guestAdultsSpinner, BorderLayout.CENTER);

        JLabel childrenLabel = new JLabel("Số trẻ em:");
        childrenLabel.setFont(new Font("Arial", Font.BOLD, 12));
        guestChildrenSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        guestChildrenSpinner.setFont(new Font("Arial", Font.PLAIN, 12));

        JPanel childrenPanel = new JPanel(new BorderLayout(0, 5));
        childrenPanel.setBackground(PANEL_BG);
        childrenPanel.add(childrenLabel, BorderLayout.NORTH);
        childrenPanel.add(guestChildrenSpinner, BorderLayout.CENTER);

        contentPanel.add(adultsPanel);
        contentPanel.add(childrenPanel);

        section.add(titleLabel, BorderLayout.NORTH);
        section.add(contentPanel, BorderLayout.CENTER);
        return section;
    }

    private JPanel createRoomSelectionSection() {
        JPanel section = new JPanel(new BorderLayout(0, 10));
        section.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Chọn phòng trống");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 15));
        titleLabel.setForeground(PRIMARY_COLOR);

        JPanel roomsPanel = new JPanel();
        roomsPanel.setLayout(new BoxLayout(roomsPanel, BoxLayout.Y_AXIS));
        roomsPanel.setBackground(BACKGROUND_COLOR);

        JScrollPane roomsScroll = new JScrollPane(roomsPanel);
        roomsScroll.setBorder(null);
        roomsScroll.getViewport().setBackground(BACKGROUND_COLOR);
        roomsScroll.setPreferredSize(new Dimension(0, 420));

        // Load phòng trống theo ngày đã chọn
        loadAvailableRoomsByDateRange(roomsPanel);

        // Lắng nghe thay đổi ngày để tự động refresh phòng
        dateCheckinField.getDocument().addDocumentListener((SimpleDocumentListener) e -> loadAvailableRoomsByDateRange(roomsPanel));
        dateCheckoutField.getDocument().addDocumentListener((SimpleDocumentListener) e -> loadAvailableRoomsByDateRange(roomsPanel));

        section.add(titleLabel, BorderLayout.NORTH);
        section.add(roomsScroll, BorderLayout.CENTER);
        return section;
    }

    private void reloadAvailableRoomsOnly(JPanel roomsPanel) {
        roomsPanel.removeAll();
        roomButtonMap.clear();
        selectedRoomIds.clear();

        try {
            List<RoomDTO> allRooms = roomBUS.getAllRooms();
            List<BookingRoomDTO> allBookingRooms = bookingRoomBUS.getAllBookingRooms();
            LocalDateTime now = LocalDateTime.now();

            // Lấy danh sách phòng đang bận (RESERVED hoặc OCCUPIED)
            java.util.Set<Integer> busyRoomIds = new java.util.HashSet<>();
            for (BookingRoomDTO br : allBookingRooms) {
                if (br.getCheckOutPlan() != null && now.isBefore(br.getCheckOutPlan())) {
                    BookingDTO booking = bookingBUS.getBookingById(br.getBookingId());
                    if (booking != null && !"CANCELED".equals(booking.getStatus())) {
                        busyRoomIds.add(br.getRoomId());
                    }
                }
            }

            int currentFloor = -1;
            JPanel rowPanel = null;

            for (RoomDTO room : allRooms) {
                // === CHỈ HIỆN PHÒNG TRỐNG ===
                if (busyRoomIds.contains(room.getRoomId())) {
                    continue;
                }

                int floorNo = room.getFloorNo();

                // Tạo tiêu đề tầng
                if (floorNo != currentFloor) {
                    currentFloor = floorNo;

                    JPanel floorHeader = new JPanel(new FlowLayout(FlowLayout.CENTER));
                    floorHeader.setBackground(BACKGROUND_COLOR);
                    JLabel floorLabel = new JLabel("Tầng " + floorNo, SwingConstants.CENTER);
                    floorLabel.setFont(new Font("Arial", Font.BOLD, 18));
                    floorLabel.setForeground(TEXT_COLOR);
                    floorLabel.setOpaque(true);
                    floorLabel.setBackground(PANEL_BG);
                    floorLabel.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(BORDER_COLOR, 1),
                            BorderFactory.createEmptyBorder(12, 60, 12, 60)
                    ));
                    floorHeader.add(floorLabel);
                    roomsPanel.add(floorHeader);

                    rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 22, 18));
                    rowPanel.setBackground(BACKGROUND_COLOR);
                    roomsPanel.add(rowPanel);
                }

                // Tạo nút phòng
                JButton roomBtn = new JButton();
                roomBtn.setLayout(new BorderLayout(0, 6));
                roomBtn.setPreferredSize(new Dimension(140, 150));
                roomBtn.setBackground(AVAILABLE_COLOR);
                roomBtn.setOpaque(true);
                roomBtn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR, 2),
                        BorderFactory.createEmptyBorder(14, 14, 14, 14)
                ));
                roomBtn.setFocusPainted(false);
                roomBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

                // Icon nhà
                ImageIcon icon = new ImageIcon(getClass().getResource("/images/house.png"));
                JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
                roomBtn.add(iconLabel, BorderLayout.CENTER);

                // Tên + loại phòng
                JPanel textPanel = new JPanel(new BorderLayout());
                textPanel.setOpaque(false);
                JLabel nameLbl = new JLabel("Phòng " + room.getRoomNo(), SwingConstants.CENTER);
                nameLbl.setFont(new Font("Arial", Font.BOLD, 14));
                JLabel typeLbl = new JLabel(roomTypes.getOrDefault(room.getRoomTypeId(), ""), SwingConstants.CENTER);
                typeLbl.setFont(new Font("Arial", Font.ITALIC, 11));
                typeLbl.setForeground(new Color(80, 80, 80));
                textPanel.add(nameLbl, BorderLayout.NORTH);
                textPanel.add(typeLbl, BorderLayout.SOUTH);
                roomBtn.add(textPanel, BorderLayout.SOUTH);

                // Xử lý chọn phòng
                int roomId = room.getRoomId();
                roomBtn.addActionListener(e -> {
                    if (selectedRoomIds.contains(roomId)) {
                        selectedRoomIds.remove(Integer.valueOf(roomId));
                        roomBtn.setBackground(AVAILABLE_COLOR);
                    } else {
                        selectedRoomIds.add(roomId);
                        roomBtn.setBackground(SELECTED_COLOR);
                    }
                });

                roomButtonMap.put(roomId, roomBtn);
                rowPanel.add(roomBtn);

                // Xuống dòng sau 4 phòng
                if (rowPanel.getComponentCount() == 4) {
                    rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 22, 18));
                    rowPanel.setBackground(BACKGROUND_COLOR);
                    roomsPanel.add(rowPanel);
                }
            }

            roomsPanel.revalidate();
            roomsPanel.repaint();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải phòng: " + e.getMessage());
        }
    }

    private JPanel createNoteSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(PANEL_BG);
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel("Ghi chú");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(PRIMARY_COLOR);

        noteField = new JTextField();
        noteField.setFont(new Font("Arial", Font.PLAIN, 12));
        noteField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        noteField.setPreferredSize(new Dimension(0, 60));

        section.add(titleLabel, BorderLayout.NORTH);
        section.add(noteField, BorderLayout.CENTER);
        return section;
    }



    private void loadRoomTypes() {
        try {
            List<RoomTypeDTO> roomTypeList = roomTypeBUS.getAllRoomTypes();
            roomTypes.clear();
            for (RoomTypeDTO roomType : roomTypeList) {
                roomTypes.put(roomType.getRoomTypeId(), roomType.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void submitBooking() {
        try {
            if (currentCustomer == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng tìm kiếm khách hàng trước!");
                return;
            }

            if (selectedRoomIds.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất một phòng!");
                return;
            }

            // Validate and parse dates
            LocalDate checkInDate;
            LocalDate checkOutDate;
            try {
                checkInDate = LocalDate.parse(dateCheckinField.getText().trim());
                checkOutDate = LocalDate.parse(dateCheckoutField.getText().trim());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ngày không hợp lệ! Định dạng: yyyy-MM-dd");
                return;
            }

            if (checkInDate.isAfter(checkOutDate) || checkInDate.isEqual(checkOutDate)) {
                JOptionPane.showMessageDialog(this, "Ngày trả phòng phải sau ngày nhận phòng!");
                return;
            }

            int adultsCount = (Integer) guestAdultsSpinner.getValue();
            int childrenCount = (Integer) guestChildrenSpinner.getValue();
            String notes = noteField.getText().trim();

            // Create booking
            BookingDTO booking = new BookingDTO();
            booking.setCustomerId(currentCustomer.getCustomer_id());
            booking.setBookingDate(LocalDateTime.now());
            booking.setStatus("PENDING");
            booking.setSource("MANUAL");
            booking.setNote(notes.isEmpty() ? null : notes);
            booking.setCreatedBy(1); // Default user ID, should be from session

            // Add booking to database
            boolean bookingAdded = bookingBUS.addBooking(booking);
            if (!bookingAdded) {
                JOptionPane.showMessageDialog(this, "Lỗi khi tạo đơn đặt!");
                return;
            }

            // Get the created booking to get its ID (get last booking for this customer)
            List<BookingDTO> bookings = bookingBUS.getBookingsByCustomer(currentCustomer.getCustomer_id());
            BookingDTO createdBooking = bookings.get(bookings.size() - 1);

            // Add booking rooms for each selected room
            for (int roomId : selectedRoomIds) {
                RoomDTO room = roomBUS.getRoomById(roomId);
                RoomTypeDTO roomType = roomTypeBUS.getRoomTypeById(room.getRoomTypeId());
                BigDecimal ratePerNight = roomType.getBasePrice() != null ? roomType.getBasePrice() : BigDecimal.ZERO;

                BookingRoomDTO bookingRoom = new BookingRoomDTO();
                bookingRoom.setBookingId(createdBooking.getBookingId());
                bookingRoom.setRoomId(roomId);
                bookingRoom.setCheckInPlan(checkInDate.atStartOfDay());
                bookingRoom.setCheckOutPlan(checkOutDate.atStartOfDay());
                bookingRoom.setAdults(adultsCount);
                bookingRoom.setChildren(childrenCount);
                bookingRoom.setRatePerNight(ratePerNight);
                bookingRoom.setTaxRate(new BigDecimal(10));
                bookingRoom.setStatus("BOOKED");

                boolean roomAdded = bookingRoomBUS.addBookingRoom(bookingRoom);
                if (!roomAdded) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi thêm phòng vào đơn đặt!");
                    return;
                }
            }

            JOptionPane.showMessageDialog(this, "Tạo đơn đặt thành công!\nMã đơn: " + createdBooking.getCode());
            SwingUtilities.windowForComponent(this).dispose();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }

    private void resetForm() {
        currentCustomer = null;
        phoneField.setText("");
        nameField.setText("");
        emailField.setText("");
        idCardField.setText("");
        nationalityField.setText("");
        genderField.setText("");
        dateCheckinField.setText(LocalDate.now().toString());
        dateCheckoutField.setText(LocalDate.now().plusDays(1).toString());
        guestAdultsSpinner.setValue(1);
        guestChildrenSpinner.setValue(0);
        noteField.setText("");
        selectedRoomIds.clear();

        // Refresh lại danh sách phòng trống
        Container parent = this;
        while (parent != null && !(parent instanceof JScrollPane)) {
            parent = parent.getParent();
        }
        if (parent instanceof JScrollPane) {
            Component view = ((JScrollPane) parent).getViewport().getView();
            if (view instanceof JPanel) {
                reloadAvailableRoomsOnly((JPanel) view);
            }
        }
    }

    private boolean isRoomAvailableInDateRange(int roomId, LocalDateTime checkIn, LocalDateTime checkOut) {
        try {
            List<BookingRoomDTO> allBookings = bookingRoomBUS.getAllBookingRooms();
            for (BookingRoomDTO br : allBookings) {
                if (br.getRoomId() != roomId) continue;

                // Bỏ qua các booking đã hủy hoặc đã trả phòng
                BookingDTO booking = bookingBUS.getBookingById(br.getBookingId());
                if (booking == null || "CANCELED".equals(booking.getStatus())) continue;
                if (br.getCheckOutActual() != null) continue; // đã trả phòng

                LocalDateTime inPlan = br.getCheckInPlan();
                LocalDateTime outPlan = br.getCheckOutPlan();

                // Kiểm tra trùng lịch: [checkIn - checkOut] vs [inPlan - outPlan]
                boolean overlap = !checkOut.isBefore(inPlan) && !outPlan.isBefore(checkIn);
                if (overlap) {
                    return false; // có khách đặt rồi
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true; // trống
    }

    private void loadAvailableRoomsByDateRange(JPanel roomsPanel) {
        roomsPanel.removeAll();
        roomButtonMap.clear();
        selectedRoomIds.clear();

        LocalDateTime checkIn = null;
        LocalDateTime checkOut = null;

        try {
            checkIn = LocalDate.parse(dateCheckinField.getText().trim()).atStartOfDay();
            checkOut = LocalDate.parse(dateCheckoutField.getText().trim()).atStartOfDay();
        } catch (Exception ex) {
            // Nếu ngày sai định dạng → không load gì cả
            roomsPanel.revalidate();
            roomsPanel.repaint();
            return;
        }

        if (checkIn.isAfter(checkOut) || checkIn.isEqual(checkOut)) {
            roomsPanel.revalidate();
            roomsPanel.repaint();
            return;
        }

        try {
            List<RoomDTO> allRooms = roomBUS.getAllRooms();

            int currentFloor = -1;
            JPanel rowPanel = null;

            for (RoomDTO room : allRooms) {
                // KIỂM TRA XEM PHÒNG CÓ TRỐNG TRONG KHOẢNG THỜI GIAN KHÔNG
                if (!isRoomAvailableInDateRange(room.getRoomId(), checkIn, checkOut)) {
                    continue; // bỏ qua nếu đã có người đặt
                }

                int floorNo = room.getFloorNo();

                if (floorNo != currentFloor) {
                    currentFloor = floorNo;

                    JPanel floorHeader = new JPanel(new FlowLayout(FlowLayout.CENTER));
                    floorHeader.setBackground(BACKGROUND_COLOR);
                    JLabel floorLabel = new JLabel("Tầng " + floorNo, SwingConstants.CENTER);
                    floorLabel.setFont(new Font("Arial", Font.BOLD, 18));
                    floorLabel.setForeground(TEXT_COLOR);
                    floorLabel.setOpaque(true);
                    floorLabel.setBackground(PANEL_BG);
                    floorLabel.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(BORDER_COLOR, 1),
                            BorderFactory.createEmptyBorder(12, 60, 12, 60)
                    ));
                    floorHeader.add(floorLabel);
                    roomsPanel.add(floorHeader);

                    rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 22, 18));
                    rowPanel.setBackground(BACKGROUND_COLOR);
                    roomsPanel.add(rowPanel);
                }

                JButton roomBtn = createRoomButton(room);
                int roomId = room.getRoomId();
                roomBtn.addActionListener(e -> {
                    if (selectedRoomIds.contains(roomId)) {
                        selectedRoomIds.remove(Integer.valueOf(roomId));
                        roomBtn.setBackground(AVAILABLE_COLOR);
                    } else {
                        selectedRoomIds.add(roomId);
                        roomBtn.setBackground(SELECTED_COLOR);
                    }
                });

                roomButtonMap.put(roomId, roomBtn);
                rowPanel.add(roomBtn);

                if (rowPanel.getComponentCount() == 4) {
                    rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 22, 18));
                    rowPanel.setBackground(BACKGROUND_COLOR);
                    roomsPanel.add(rowPanel);
                }
            }

            roomsPanel.revalidate();
            roomsPanel.repaint();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tải phòng: " + e.getMessage());
        }
    }

    private JButton createRoomButton(RoomDTO room) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout(0, 6));
        btn.setPreferredSize(new Dimension(140, 150));
        btn.setBackground(AVAILABLE_COLOR);
        btn.setOpaque(true);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 2),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        ImageIcon icon = new ImageIcon(getClass().getResource("/images/house.png"));
        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        btn.add(iconLabel, BorderLayout.CENTER);

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        JLabel nameLbl = new JLabel("Phòng " + room.getRoomNo(), SwingConstants.CENTER);
        nameLbl.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel typeLbl = new JLabel(roomTypes.getOrDefault(room.getRoomTypeId(), ""), SwingConstants.CENTER);
        typeLbl.setFont(new Font("Arial", Font.ITALIC, 11));
        typeLbl.setForeground(new Color(80, 80, 80));
        textPanel.add(nameLbl, BorderLayout.NORTH);
        textPanel.add(typeLbl, BorderLayout.SOUTH);
        btn.add(textPanel, BorderLayout.SOUTH);

        return btn;
    }
}
