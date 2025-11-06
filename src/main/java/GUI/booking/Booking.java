/*
 * Click nbfs://SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package GUI.booking;

import BUS.BookingBUS;
import BUS.BookingRoomBUS;
import BUS.CustomerBUS;
import BUS.RoomBUS;
import BUS.RoomTypeBUS;
import DTO.BookingDTO;
import DTO.BookingRoomDTO;
import DTO.CustomerDTO;
import DTO.RoomDTO;
import DTO.RoomTypeDTO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Hotel Management System
 */
public class Booking extends javax.swing.JPanel {
    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    private static final Color AVAILABLE_COLOR = new Color(232, 245, 233);
    private static final Color SELECTED_COLOR = new Color(200, 220, 255);
    private static final Color BACKGROUND_COLOR = new Color(245,
            245, 245);
    private static final Color PANEL_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(224, 224, 224);
    private static final Color TEXT_COLOR = new Color(33, 33, 33);

    private JScrollPane scrollPane;
    private JTable bookingTable;
    private JTextField searchField;

    private BookingBUS bookingBUS;
    private BookingRoomBUS bookingRoomBUS;
    private CustomerBUS customerBUS;
    private RoomBUS roomBUS;
    private RoomTypeBUS roomTypeBUS;

    private List<BookingDTO> bookingData = new ArrayList<>();
    private List<BookingDTO> filteredBookingData = new ArrayList<>();
    private Map<Integer, String> roomTypes = new HashMap<>();
    private int currentUserId = 1; // TODO: Get from login session

    /**
     * Creates new form Booking
     */
    public Booking() {
        bookingBUS = new BookingBUS(new DAO.BookingDAO());
        bookingRoomBUS = new BookingRoomBUS(new DAO.BookingRoomDAO());
        customerBUS = new CustomerBUS();
        roomBUS = new RoomBUS();
        roomTypeBUS = new RoomTypeBUS();
        initComponents();
        loadRoomTypes();
        loadData();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        controlPanel.setBackground(PANEL_BG);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JButton addBookingButton = new JButton("Đặt phòng");
        addBookingButton.setPreferredSize(new Dimension(140, 35));
        addBookingButton.setBackground(PRIMARY_COLOR);
        addBookingButton.setForeground(Color.WHITE);
        addBookingButton.setFocusPainted(false);
        addBookingButton.setBorderPainted(false);
        addBookingButton.setFont(new Font("Arial", Font.BOLD, 13));
        addBookingButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addBookingButton.addActionListener(e -> showNewBookingDialog());
        controlPanel.add(addBookingButton);

        controlPanel.add(Box.createHorizontalStrut(20));

        searchField = new JTextField(15);
        searchField.setPreferredSize(new Dimension(200, 35));
        searchField.setFont(new Font("Arial", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch();
                }
            }
        });
        controlPanel.add(searchField);

        JButton searchButton = createIconButton("/images/search.png");
        searchButton.addActionListener(e -> performSearch());
        controlPanel.add(searchButton);

        add(controlPanel, BorderLayout.NORTH);

        scrollPane = new JScrollPane();
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JButton createIconButton(String iconPath) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(35, 35));
        try {
            button.setIcon(new ImageIcon(getClass().getResource(iconPath)));
        } catch (Exception e) {
            // Icon not found, use text
            button.setText("🔍");
        }
        button.setBackground(PANEL_BG);
        button.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
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

    private void loadData() {
        try {
            bookingData = bookingBUS.getAllBookings();
            filteredBookingData = bookingData.stream().collect(Collectors.toList());
            updateTable();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu đặt phòng: " + e.getMessage());
        }
    }

    private void performSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            filteredBookingData = bookingData.stream().collect(Collectors.toList());
        } else {
            filteredBookingData = bookingData.stream()
                    .filter(booking -> {
                        String code = booking.getCode().toLowerCase();
                        String status = booking.getStatus().toLowerCase();
                        return code.contains(query) || status.contains(query);
                    })
                    .collect(Collectors.toList());
        }
        updateTable();
    }

    private void updateTable() {
        String[] columnNames = {"ID", "Mã đặt", "Khách hàng", "Ngày đặt", "Trạng thái", "Ghi chú"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (BookingDTO booking : filteredBookingData) {
            Object[] row = {
                    booking.getBookingId(),
                    booking.getCode(),
                    booking.getCustomerId(),
                    booking.getBookingDate() != null ? booking.getBookingDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "",
                    booking.getStatus(),
                    booking.getNote() != null ? booking.getNote() : ""
            };
            model.addRow(row);
        }

        bookingTable = new JTable(model);
        bookingTable.setRowHeight(40);
        bookingTable.setFont(new Font("Arial", Font.PLAIN, 13));
        bookingTable.setGridColor(BORDER_COLOR);
        bookingTable.setSelectionBackground(new Color(232, 240, 254));
        bookingTable.setSelectionForeground(TEXT_COLOR);

        bookingTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        bookingTable.getTableHeader().setBackground(PANEL_BG);
        bookingTable.getTableHeader().setForeground(TEXT_COLOR);
        bookingTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR));
        bookingTable.getTableHeader().setPreferredSize(new Dimension(0, 45));

        bookingTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                }
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return c;
            }
        });

        bookingTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && bookingTable.getSelectedRow() != -1) {
                    int row = bookingTable.getSelectedRow();
                    int bookingId = (Integer) bookingTable.getValueAt(row, 0);
                    BookingDTO booking = bookingBUS.getBookingById(bookingId);
                    if (booking != null) {
                        showBookingDetailDialog(booking);
                    }
                }
            }
        });

        scrollPane.setViewportView(bookingTable);
    }

    private void showNewBookingDialog() {
        JDialog bookingDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Tạo đơn đặt phòng", true);
        bookingDialog.setLayout(new BorderLayout());
        bookingDialog.setSize(700, 600);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Tab 1: Customer Information
        JPanel customerPanel = createCustomerPanel(bookingDialog);
        tabbedPane.addTab("Thông tin khách hàng", customerPanel);

        // Tab 2: Room Selection
        JPanel roomPanel = createRoomSelectionPanel(bookingDialog);
        tabbedPane.addTab("Chọn phòng", roomPanel);

        bookingDialog.add(tabbedPane, BorderLayout.CENTER);

        // Store reference to access from inner class
        final JDialog dialog = bookingDialog;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(PANEL_BG);

        JButton completeButton = new JButton("Hoàn tất");
        completeButton.setPreferredSize(new Dimension(100, 35));
        completeButton.setBackground(PRIMARY_COLOR);
        completeButton.setForeground(Color.WHITE);
        completeButton.setFocusPainted(false);
        completeButton.setBorderPainted(false);
        completeButton.setFont(new Font("Arial", Font.BOLD, 13));
        completeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton cancelButton = new JButton("Hủy");
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.setBackground(BORDER_COLOR);
        cancelButton.setForeground(TEXT_COLOR);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 13));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(completeButton);
        buttonPanel.add(cancelButton);

        bookingDialog.add(buttonPanel, BorderLayout.SOUTH);
        bookingDialog.setLocationRelativeTo(this);
        bookingDialog.setVisible(true);
    }

    private JPanel createCustomerPanel(JDialog parentDialog) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PANEL_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 15, 15));
        formPanel.setBackground(PANEL_BG);

        JLabel customerLabel = new JLabel("Khách hàng:");
        customerLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JComboBox<String> customerCombo = new JComboBox<>();
        JButton addCustomerButton = new JButton("Thêm mới");
        addCustomerButton.setPreferredSize(new Dimension(100, 30));
        addCustomerButton.setBackground(PRIMARY_COLOR);
        addCustomerButton.setForeground(Color.WHITE);
        addCustomerButton.setFont(new Font("Arial", Font.BOLD, 11));
        addCustomerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Load customers
        try {
            List<CustomerDTO> customers = customerBUS.getAllCustomers();
            for (CustomerDTO c : customers) {
                customerCombo.addItem(c.getFull_name() + " - " + c.getPhone());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel customerSelectPanel = new JPanel(new BorderLayout(5, 0));
        customerSelectPanel.setBackground(PANEL_BG);
        customerSelectPanel.add(customerCombo, BorderLayout.CENTER);
        customerSelectPanel.add(addCustomerButton, BorderLayout.EAST);

        formPanel.add(customerLabel);
        formPanel.add(customerSelectPanel);

        // Check-in date
        JLabel checkinLabel = new JLabel("Ngày nhận phòng:");
        checkinLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField checkinField = new JTextField();
        checkinField.setText("2024-01-01");
        formPanel.add(checkinLabel);
        formPanel.add(checkinField);

        // Check-out date
        JLabel checkoutLabel = new JLabel("Ngày trả phòng:");
        checkoutLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField checkoutField = new JTextField();
        checkoutField.setText("2024-01-02");
        formPanel.add(checkoutLabel);
        formPanel.add(checkoutField);

        // Adults
        JLabel adultsLabel = new JLabel("Số người lớn:");
        adultsLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JSpinner adultsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        formPanel.add(adultsLabel);
        formPanel.add(adultsSpinner);

        // Children
        JLabel childrenLabel = new JLabel("Số trẻ em:");
        childrenLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JSpinner childrenSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        formPanel.add(childrenLabel);
        formPanel.add(childrenSpinner);

        // Note
        JLabel noteLabel = new JLabel("Ghi chú:");
        noteLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField noteField = new JTextField();
        formPanel.add(noteLabel);
        formPanel.add(noteField);

        panel.add(formPanel, BorderLayout.NORTH);

        // Store fields in panel for later access
        panel.putClientProperty("customerCombo", customerCombo);
        panel.putClientProperty("checkinField", checkinField);
        panel.putClientProperty("checkoutField", checkoutField);
        panel.putClientProperty("adultsSpinner", adultsSpinner);
        panel.putClientProperty("childrenSpinner", childrenSpinner);
        panel.putClientProperty("noteField", noteField);

        return panel;
    }

    private JPanel createRoomSelectionPanel(JDialog parentDialog) {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Available rooms display
        JPanel roomsPanel = new JPanel();
        roomsPanel.setLayout(new BoxLayout(roomsPanel, BoxLayout.Y_AXIS));
        roomsPanel.setBackground(BACKGROUND_COLOR);

        JScrollPane roomsScrollPane = new JScrollPane(roomsPanel);
        roomsScrollPane.setBorder(null);
        roomsScrollPane.getViewport().setBackground(BACKGROUND_COLOR);

        // Load and display available rooms
        try {
            List<RoomDTO> availableRooms = roomBUS.filterRoomsByStatus("AVAILABLE");

            int currentFloor = -1;
            JPanel floorPanel = null;
            JPanel rowPanel = null;
            List<JButton> selectedRoomButtons = new ArrayList<>();

            for (RoomDTO room : availableRooms) {
                int roomId = room.getRoomId();
                String roomName = room.getRoomNo();
                int floorNo = room.getFloorNo();
                String roomType = roomTypes.getOrDefault(room.getRoomTypeId(), "Unknown");

                // Create floor separator
                if (floorNo != currentFloor) {
                    currentFloor = floorNo;
                    JPanel separator = new JPanel(new FlowLayout(FlowLayout.CENTER));
                    separator.setBackground(BACKGROUND_COLOR);
                    JLabel floorLabel = new JLabel("Tầng " + floorNo);
                    floorLabel.setFont(new Font("Arial", Font.BOLD, 16));
                    floorLabel.setForeground(TEXT_COLOR);
                    separator.add(floorLabel);
                    roomsPanel.add(separator);

                    rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
                    rowPanel.setBackground(BACKGROUND_COLOR);
                    roomsPanel.add(rowPanel);
                }

                // Create room button
                JButton roomBtn = new JButton();
                roomBtn.setLayout(new BorderLayout(0, 5));
                roomBtn.setPreferredSize(new Dimension(120, 120));
                roomBtn.setBackground(AVAILABLE_COLOR);
                roomBtn.setOpaque(true);
                roomBtn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR, 2),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)
                ));
                roomBtn.setFocusPainted(false);
                roomBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                roomBtn.setFont(new Font("Arial", Font.BOLD, 12));

                JLabel roomLabel = new JLabel("Phòng " + roomName + "\n" + roomType);
                roomLabel.setHorizontalAlignment(SwingConstants.CENTER);
                roomLabel.setFont(new Font("Arial", Font.BOLD, 12));
                roomBtn.add(roomLabel, BorderLayout.CENTER);

                final List<JButton> selectedRooms = selectedRoomButtons;
                final JButton btn = roomBtn;
                roomBtn.addActionListener(e -> {
                    if (selectedRooms.contains(btn)) {
                        selectedRooms.remove(btn);
                        btn.setBackground(AVAILABLE_COLOR);
                    } else {
                        selectedRooms.add(btn);
                        btn.setBackground(SELECTED_COLOR);
                    }
                });

                rowPanel.add(roomBtn);
            }

            panel.putClientProperty("selectedRoomButtons", selectedRoomButtons);

        } catch (Exception e) {
            e.printStackTrace();
        }

        panel.add(roomsScrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void showBookingDetailDialog(BookingDTO booking) {
        JDialog detailDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chi tiết đơn đặt", true);
        detailDialog.setLayout(new BorderLayout());
        detailDialog.setSize(600, 500);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(PANEL_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Booking info
        addDetailRow(contentPanel, "Mã đặt:", booking.getCode());
        addDetailRow(contentPanel, "ID:", String.valueOf(booking.getBookingId()));
        addDetailRow(contentPanel, "Khách hàng ID:", String.valueOf(booking.getCustomerId()));
        addDetailRow(contentPanel, "Ngày đặt:", booking.getBookingDate() != null ?
                booking.getBookingDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A");
        addDetailRow(contentPanel, "Trạng thái:", booking.getStatus());
        addDetailRow(contentPanel, "Nguồn đặt:", booking.getSource());
        addDetailRow(contentPanel, "Ghi chú:", booking.getNote() != null ? booking.getNote() : "Không có");

        contentPanel.add(Box.createVerticalStrut(15));
        JLabel roomsLabel = new JLabel("Phòng đặt:");
        roomsLabel.setFont(new Font("Arial", Font.BOLD, 13));
        roomsLabel.setForeground(PRIMARY_COLOR);
        contentPanel.add(roomsLabel);

        // Booking rooms
        try {
            List<BookingRoomDTO> bookingRooms = bookingRoomBUS.getBookingRoomsByBooking(booking.getBookingId());
            for (BookingRoomDTO br : bookingRooms) {
                RoomDTO room = roomBUS.getRoomById(br.getRoomId());
                String roomInfo = "Phòng " + (room != null ? room.getRoomNo() : "ID: " + br.getRoomId()) +
                        " - " + br.getCheckInPlan() + " đến " + br.getCheckOutPlan();
                addDetailRow(contentPanel, "", roomInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        detailDialog.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(PANEL_BG);
        JButton closeButton = new JButton("Đóng");
        closeButton.setPreferredSize(new Dimension(100, 35));
        closeButton.setBackground(PRIMARY_COLOR);
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setFont(new Font("Arial", Font.BOLD, 13));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> detailDialog.dispose());
        buttonPanel.add(closeButton);

        detailDialog.add(buttonPanel, BorderLayout.SOUTH);
        detailDialog.setLocationRelativeTo(this);
        detailDialog.setVisible(true);
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        JPanel rowPanel = new JPanel(new BorderLayout());
        rowPanel.setBackground(PANEL_BG);
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 12));
        labelComponent.setPreferredSize(new Dimension(120, 30));

        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 12));
        valueComponent.setForeground(TEXT_COLOR);

        rowPanel.add(labelComponent, BorderLayout.WEST);
        rowPanel.add(valueComponent, BorderLayout.CENTER);

        panel.add(rowPanel);
        panel.add(Box.createVerticalStrut(5));
    }
}
