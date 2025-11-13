package GUI.booking;

import BUS.BookingRoomBUS;
import BUS.InvoiceBUS;
import BUS.RoomBUS;
import BUS.RoomTypeBUS;
import DAO.BookingRoomDAO;
import DTO.BookingRoomDTO;
import DTO.RoomDTO;
import DTO.RoomTypeDTO;
import GUI.booking.Guest;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class BookingRoom extends javax.swing.JFrame {

    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color PANEL_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(224, 224, 224);
    private static final Color TEXT_COLOR = new Color(33, 33, 33);
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);
    private static final Color DANGER_COLOR = new Color(239, 68, 68);

    private BookingRoomBUS bookingRoomBUS;
    private InvoiceBUS invoiceBUS;
    private JScrollPane scrollPane;
    private JTextField searchField;
    private List<BookingRoomDTO> bookingRoomData;
    private int filterByBookingId = -1;

    public BookingRoom() {
        this(-1);
    }

    public BookingRoom(int bookingId) {
        bookingRoomBUS = new BookingRoomBUS(new BookingRoomDAO());
        invoiceBUS = new InvoiceBUS();
        this.filterByBookingId = bookingId;
        initComponents();
        loadBookingRoomData();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quản lý phòng đặt");
        setSize(900, 600);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        controlPanel.setBackground(PANEL_BG);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JButton addButton = new JButton("+ Thêm");
        addButton.setPreferredSize(new Dimension(100, 35));
        addButton.setBackground(PRIMARY_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setFont(new Font("Arial", Font.BOLD, 13));
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e -> addNewBookingRoom());
        controlPanel.add(addButton);

        JButton checkInAllButton = new JButton("Check-in tất cả");
        checkInAllButton.setPreferredSize(new Dimension(140, 35));
        checkInAllButton.setBackground(SUCCESS_COLOR);
        checkInAllButton.setForeground(Color.WHITE);
        checkInAllButton.setFocusPainted(false);
        checkInAllButton.setBorderPainted(false);
        checkInAllButton.setFont(new Font("Arial", Font.BOLD, 13));
        checkInAllButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        checkInAllButton.addActionListener(e -> checkInAllRooms());
        controlPanel.add(checkInAllButton);

        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(250, 35));
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

        JButton filterButton = createIconButton("/images/filter.png");
        filterButton.addActionListener(e -> showFilterDialog());
        controlPanel.add(filterButton);

        JButton reloadIconButton = createIconButton("/icon/reload.png");
        reloadIconButton.addActionListener(e -> loadBookingRoomData());
        controlPanel.add(reloadIconButton);

        mainPanel.add(controlPanel, BorderLayout.NORTH);

        scrollPane = new JScrollPane();
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        setContentPane(mainPanel);
        setLocationRelativeTo(null);
    }

    private JButton createIconButton(String iconPath) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(35, 35));
        try {
            button.setIcon(new ImageIcon(getClass().getResource(iconPath)));
        } catch (Exception e) {
            button.setText("...");
        }
        button.setBackground(PANEL_BG);
        button.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void loadBookingRoomData() {
        try {
            if (filterByBookingId > 0) {
                List<BookingRoomDTO> allRooms = bookingRoomBUS.getAllBookingRooms();
                bookingRoomData = allRooms.stream()
                        .filter(br -> br.getBookingId() == filterByBookingId)
                        .toList();
            } else {
                bookingRoomData = bookingRoomBUS.getAllBookingRooms();
            }
            updateTableView();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            updateTableView();
            return;
        }

        try {
            List<BookingRoomDTO> searchData = bookingRoomData != null ? bookingRoomData : bookingRoomBUS.getAllBookingRooms();
            List<BookingRoomDTO> filtered = searchData.stream()
                    .filter(br -> String.valueOf(br.getBookingRoomId()).contains(query) ||
                            String.valueOf(br.getRoomId()).contains(query))
                    .toList();

            if (filtered.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                updateTableView();
            } else {
                updateTableViewWithData(filtered);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showFilterDialog() {
        JDialog filterDialog = new JDialog(this, "Lọc phòng đặt", true);
        filterDialog.setLayout(new BorderLayout());
        filterDialog.setSize(350, 180);

        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 15, 15));
        contentPanel.setBackground(PANEL_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel statusLabel = new JLabel("Trạng thái:");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"All", "CHECKED_OUT", "CHECKED_IN", "BOOKED" });
        statusCombo.setPreferredSize(new Dimension(150, 30));
        contentPanel.add(statusLabel);
        contentPanel.add(statusCombo);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(PANEL_BG);
        JButton confirmButton = new JButton("Xác nhận");
        confirmButton.setPreferredSize(new Dimension(100, 35));
        confirmButton.setBackground(PRIMARY_COLOR);
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        confirmButton.setBorderPainted(false);
        confirmButton.setFont(new Font("Arial", Font.BOLD, 13));
        confirmButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmButton.addActionListener(e -> {
            String selectedStatus = (String) statusCombo.getSelectedItem();
            filterBookingRooms(selectedStatus);
            filterDialog.dispose();
        });
        buttonPanel.add(confirmButton);

        filterDialog.add(contentPanel, BorderLayout.CENTER);
        filterDialog.add(buttonPanel, BorderLayout.SOUTH);
        filterDialog.setLocationRelativeTo(this);
        filterDialog.setVisible(true);
    }

    private void filterBookingRooms(String status) {
        try {
            List<BookingRoomDTO> results;
            if ("All".equalsIgnoreCase(status)) {
                results = bookingRoomData;
            } else {
                results = bookingRoomData.stream()
                        .filter(br -> status.equals(br.getStatus()))
                        .toList();
            }
            updateTableViewWithData(results);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi lọc: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTableView() {
        updateTableViewWithData(bookingRoomData);
    }

    private void updateTableViewWithData(List<BookingRoomDTO> data) {
        String[] columnNames = {"Mã phòng đặt", "Mã phòng", "Ngày nhận", "Ngày trả", "Thuế", "Trạng thái"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (BookingRoomDTO br : data) {
            Object[] row = {
                    br.getBookingRoomId(),
                    br.getRoomId(),
                    sdf.format(java.sql.Timestamp.valueOf(br.getCheckInPlan())),
                    sdf.format(java.sql.Timestamp.valueOf(br.getCheckOutPlan())),
                    br.getTaxRate() != null ? br.getTaxRate() + "%" : "0%",
                    br.getStatus()
            };
            model.addRow(row);
        }

        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(TEXT_COLOR);
        table.setShowVerticalLines(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(PANEL_BG);
        table.getTableHeader().setForeground(TEXT_COLOR);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR));
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger() && table.getSelectedRow() != -1) {
                    int rowIndex = table.getSelectedRow();
                    BookingRoomDTO bookingRoom = data.get(rowIndex);

                    JPopupMenu contextMenu = new JPopupMenu();
                    JMenuItem editMenuItem = new JMenuItem("Sửa");
                    JMenuItem deleteMenuItem = new JMenuItem("Xóa");
                    JMenuItem checkInMenuItem = new JMenuItem("Check-in");
                    JMenuItem checkOutMenuItem = new JMenuItem("Check-out");
                    JMenuItem guestMenuItem = new JMenuItem("Khách lưu trú");

                    editMenuItem.addActionListener(e1 -> editBookingRoom(bookingRoom));
                    deleteMenuItem.addActionListener(e1 -> deleteBookingRoom(bookingRoom));
                    checkInMenuItem.addActionListener(e1 -> checkInBookingRoom(bookingRoom));
                    checkOutMenuItem.addActionListener(e1 -> checkOutBookingRoom(bookingRoom));
                    guestMenuItem.addActionListener(e1 -> openGuestGUI(bookingRoom));

                    contextMenu.add(editMenuItem);
                    contextMenu.add(deleteMenuItem);
                    contextMenu.addSeparator();
                    contextMenu.add(checkInMenuItem);
                    contextMenu.add(checkOutMenuItem);
                    contextMenu.addSeparator();
                    contextMenu.add(guestMenuItem);

                    contextMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        scrollPane.setViewportView(table);
    }

    private void checkInBookingRoom(BookingRoomDTO bookingRoom) {
        if (!"BOOKED".equals(bookingRoom.getStatus())) {
            JOptionPane.showMessageDialog(this, "Phòng này không ở trạng thái BOOKED!",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận check-in phòng " + bookingRoom.getRoomId() + "?",
                "Xác nhận check-in", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (bookingRoomBUS.checkIn(bookingRoom.getBookingRoomId(), LocalDateTime.now())) {
                    JOptionPane.showMessageDialog(this, "Check-in thành công!");
                    loadBookingRoomData();
                } else {
                    JOptionPane.showMessageDialog(this, "Check-in thất bại!",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void checkInAllRooms() {
        List<BookingRoomDTO> roomsToCheckIn = bookingRoomData.stream()
                .filter(br -> "BOOKED".equals(br.getStatus()))
                .toList();

        if (roomsToCheckIn.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có phòng nào ở trạng thái BOOKED để check-in!",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận check-in " + roomsToCheckIn.size() + " phòng?",
                "Xác nhận check-in tất cả", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int successCount = 0;
                LocalDateTime checkInTime = LocalDateTime.now();

                for (BookingRoomDTO br : roomsToCheckIn) {
                    if (bookingRoomBUS.checkIn(br.getBookingRoomId(), checkInTime)) {
                        successCount++;
                    }
                }

                if (successCount == roomsToCheckIn.size()) {
                    JOptionPane.showMessageDialog(this, "Check-in thành công " + successCount + " phòng!",
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Check-in thành công " + successCount + "/" + roomsToCheckIn.size() + " phòng!",
                            "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                }
                loadBookingRoomData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void checkOutBookingRoom(BookingRoomDTO bookingRoom) {
        if (!"CHECKED_IN".equals(bookingRoom.getStatus())) {
            JOptionPane.showMessageDialog(this, "Phòng này chưa check-in!",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận check-out phòng " + bookingRoom.getRoomId() + "?",
                "Xác nhận check-out", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                LocalDateTime checkOutTime = LocalDateTime.now();
                if (bookingRoomBUS.checkOut(bookingRoom.getBookingRoomId(), checkOutTime)) {
                    // Kiểm tra nếu đây là phòng cuối cùng trong booking
                    if (bookingRoomBUS.areAllRoomsCheckedOut(bookingRoom.getBookingId())) {
                        // Tạo hóa đơn
                        invoiceBUS.createInvoiceOnFullCheckout(bookingRoom.getBookingId(), 1); // Giả sử createdBy = 1
                        JOptionPane.showMessageDialog(this, "Check-out thành công và hóa đơn đã được tạo!");
                    } else {
                        JOptionPane.showMessageDialog(this, "Check-out thành công!");
                    }
                    loadBookingRoomData();
                } else {
                    JOptionPane.showMessageDialog(this, "Check-out thất bại!",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editBookingRoom(BookingRoomDTO br) {
        JDialog editDialog = new JDialog(this, "Sửa phòng đặt", true);
        editDialog.setLayout(new BorderLayout());
        editDialog.setSize(400, 250);

        JPanel contentPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        contentPanel.setBackground(PANEL_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel idLabel = new JLabel("Mã phòng đặt:");
        idLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JLabel idValue = new JLabel("PD00" + br.getBookingRoomId());
        idValue.setFont(new Font("Arial", Font.PLAIN, 13));
        contentPanel.add(idLabel);
        contentPanel.add(idValue);

        JLabel roomLabel = new JLabel("Mã phòng:");
        roomLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JLabel roomValue = new JLabel("P00" + br.getRoomId());
        roomValue.setFont(new Font("Arial", Font.PLAIN, 13));
        contentPanel.add(roomLabel);
        contentPanel.add(roomValue);

        JLabel statusLabel = new JLabel("Trạng thái:");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"BOOKED", "CHECKED_IN", "CHECKED_OUT"});
        statusCombo.setSelectedItem(br.getStatus());
        statusCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        statusCombo.setPreferredSize(new Dimension(150, 30));
        contentPanel.add(statusLabel);
        contentPanel.add(statusCombo);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(PANEL_BG);

        JButton saveButton = new JButton("Lưu");
        saveButton.setPreferredSize(new Dimension(100, 35));
        saveButton.setBackground(PRIMARY_COLOR);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorderPainted(false);
        saveButton.setFont(new Font("Arial", Font.BOLD, 13));
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.addActionListener(e -> {
            try {
                String newStatus = (String) statusCombo.getSelectedItem();
                br.setStatus(newStatus);
                if (bookingRoomBUS.updateBookingRoom(br)) {
                    JOptionPane.showMessageDialog(editDialog, "Cập nhật thành công!");
                    editDialog.dispose();
                    loadBookingRoomData();
                } else {
                    JOptionPane.showMessageDialog(editDialog, "Cập nhật thất bại!",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(editDialog, "Lỗi: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(saveButton);

        JButton cancelButton = new JButton("Hủy");
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.setBackground(new Color(200, 200, 200));
        cancelButton.setForeground(TEXT_COLOR);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 13));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> editDialog.dispose());
        buttonPanel.add(cancelButton);

        editDialog.add(contentPanel, BorderLayout.CENTER);
        editDialog.add(buttonPanel, BorderLayout.SOUTH);
        editDialog.setLocationRelativeTo(this);
        editDialog.setVisible(true);
    }

    private void deleteBookingRoom(BookingRoomDTO br) {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa phòng đặt này?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (bookingRoomBUS.deleteBookingRoom(br.getBookingRoomId())) {
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
                    loadBookingRoomData();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi xóa: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openGuestGUI(BookingRoomDTO br) {
        try {
            Guest guestWindow = new Guest(br.getBookingRoomId());
            guestWindow.setTitle("Danh sách khách lưu trú - Mã phòng đặt: " + br.getBookingRoomId());
            guestWindow.setVisible(true);
            guestWindow.setLocationRelativeTo(this);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi mở giao diện: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            System.out.println(e.getMessage());
        }
    }

    private void addNewBookingRoom() {
        if (filterByBookingId > 0 && bookingRoomBUS.areAllRoomsCheckedOut(filterByBookingId)) {
            JOptionPane.showMessageDialog(this, "Đơn đặt phòng này đã được checkout hoàn tất, không thể thêm phòng mới!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JDialog addDialog = new JDialog(this, "Chọn phòng để thêm", true);
        addDialog.setLayout(new BorderLayout());
        addDialog.setSize(900, 600);

        // Panel to hold available rooms grid
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_COLOR);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);

        try {
            RoomBUS roomBUS = new RoomBUS();
            RoomTypeBUS roomTypeBUS = new RoomTypeBUS();

            // Lấy tất cả phòng và booking rooms
            List<RoomDTO> allRooms = roomBUS.getAllRooms();
            List<BookingRoomDTO> allBookingRooms = bookingRoomBUS.getAllBookingRooms();

            // Logic giống Room.java: Tìm các phòng đang bận
            java.util.Set<Integer> busyRoomIds = new java.util.HashSet<>();
            LocalDateTime now = LocalDateTime.now();

            for (BookingRoomDTO br : allBookingRooms) {
                // Bỏ qua nếu đã checkout thực tế
                if (br.getCheckOutActual() != null) {
                    continue;
                }

                // Kiểm tra booking có bị hủy không
                try {
                    BUS.BookingBUS bookingBUS = new BUS.BookingBUS(new DAO.BookingDAO());
                    DTO.BookingDTO booking = bookingBUS.getBookingById(br.getBookingId());

                    if (booking != null && !"CANCELED".equals(booking.getStatus())) {
                        // Nếu còn trong khoảng thời gian đặt
                        if (br.getCheckOutPlan() != null && now.isBefore(br.getCheckOutPlan())) {
                            busyRoomIds.add(br.getRoomId());
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Lỗi kiểm tra booking: " + e.getMessage());
                }
            }

            // Lọc ra chỉ những phòng KHÔNG nằm trong busyRoomIds
            List<RoomDTO> availableRooms = allRooms.stream()
                    .filter(room -> !busyRoomIds.contains(room.getRoomId()))
                    .toList();

            Map<Integer, String> roomTypes = new HashMap<>();
            List<RoomTypeDTO> roomTypeList = roomTypeBUS.getAllRoomTypes();
            for (RoomTypeDTO roomType : roomTypeList) {
                roomTypes.put(roomType.getRoomTypeId(), roomType.getName());
            }

            // Hiển thị theo tầng
            int currentFloor = -1;
            JPanel rowPanel = null;
            Color AVAILABLE_COLOR = new Color(232, 245, 233);

            for (RoomDTO room : availableRooms) {
                int floorNo = room.getFloorNo();
                if (floorNo != currentFloor) {
                    currentFloor = floorNo;
                    JPanel floorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                    floorPanel.setBackground(BACKGROUND_COLOR);
                    JLabel floorLabel = new JLabel("Tầng " + floorNo, SwingConstants.CENTER);
                    floorLabel.setFont(new Font("Arial", Font.BOLD, 18));
                    floorLabel.setForeground(TEXT_COLOR);
                    floorLabel.setOpaque(true);
                    floorLabel.setBackground(PANEL_BG);
                    floorLabel.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(BORDER_COLOR, 1),
                            BorderFactory.createEmptyBorder(10, 30, 10, 30)
                    ));
                    floorPanel.add(floorLabel);
                    mainPanel.add(floorPanel);

                    rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
                    rowPanel.setBackground(BACKGROUND_COLOR);
                    mainPanel.add(rowPanel);
                }

                JButton btn = new JButton();
                btn.setLayout(new BorderLayout(0, 5));
                btn.setPreferredSize(new Dimension(130, 130));
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR, 2),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                btn.setFocusPainted(false);
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btn.setBackground(AVAILABLE_COLOR);
                btn.setOpaque(true);

                ImageIcon icon = new ImageIcon(getClass().getResource("/images/house.png"));
                JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
                btn.add(iconLabel, BorderLayout.CENTER);

                JPanel labelPanel = new JPanel(new BorderLayout());
                labelPanel.setOpaque(false);
                JLabel nameLabel = new JLabel("Phòng " + room.getRoomNo(), SwingConstants.CENTER);
                nameLabel.setFont(new Font("Arial", Font.BOLD, 13));
                JLabel typeLabel = new JLabel(roomTypes.getOrDefault(room.getRoomTypeId(), ""), SwingConstants.CENTER);
                typeLabel.setFont(new Font("Arial", Font.ITALIC, 11));
                typeLabel.setForeground(new Color(100, 100, 100));
                labelPanel.add(nameLabel, BorderLayout.NORTH);
                labelPanel.add(typeLabel, BorderLayout.SOUTH);
                btn.add(labelPanel, BorderLayout.SOUTH);

                // Click to add this room to booking
                RoomDTO selectedRoom = room;
                btn.addActionListener(e -> {
                    try {
                        addRoomToBooking(selectedRoom);
                        addDialog.dispose();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(addDialog, "Lỗi: " + ex.getMessage(),
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                });

                rowPanel.add(btn);
                if (rowPanel.getComponentCount() == 4) {
                    rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
                    rowPanel.setBackground(BACKGROUND_COLOR);
                    mainPanel.add(rowPanel);
                }
            }

            if (availableRooms.isEmpty()) {
                JLabel noRoomLabel = new JLabel("Không có phòng trống", SwingConstants.CENTER);
                noRoomLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                noRoomLabel.setForeground(TEXT_COLOR);
                mainPanel.add(noRoomLabel);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(addDialog, "Lỗi tải danh sách phòng: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        addDialog.add(scrollPane, BorderLayout.CENTER);

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
        closeButton.addActionListener(e -> addDialog.dispose());
        buttonPanel.add(closeButton);

        addDialog.add(buttonPanel, BorderLayout.SOUTH);
        addDialog.setLocationRelativeTo(this);
        addDialog.setVisible(true);
    }

    private void addRoomToBooking(RoomDTO room) {
        // Kiểm tra phòng đã được thêm vào booking hiện tại chưa
        boolean roomAlreadyAdded = bookingRoomData.stream()
                .anyMatch(br -> br.getRoomId() == room.getRoomId() && br.getBookingId() == filterByBookingId);

        if (roomAlreadyAdded) {
            JOptionPane.showMessageDialog(this, "Phòng này đã được thêm vào đặt phòng rồi!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // KIỂM TRA PHÒNG CÓ ĐANG ĐƯỢC ĐẶT BỞI BOOKING KHÁC KHÔNG
        if (bookingRoomBUS.isRoomCurrentlyBooked(room.getRoomId())) {
            JOptionPane.showMessageDialog(this,
                    "Phòng này đang được đặt bởi khách hàng khác!\n" +
                            "Vui lòng chọn phòng khác.",
                    "Không thể thêm phòng", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog detailDialog = new JDialog(this, "Xác nhận thêm phòng", true);
        detailDialog.setLayout(new BorderLayout());
        detailDialog.setSize(400, 280);

        JPanel contentPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        contentPanel.setBackground(PANEL_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel roomLabel = new JLabel("Phòng:");
        roomLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JLabel roomValue = new JLabel("Phòng " + room.getRoomNo());
        roomValue.setFont(new Font("Arial", Font.PLAIN, 13));
        contentPanel.add(roomLabel);
        contentPanel.add(roomValue);

        JLabel floorLabel = new JLabel("Tầng:");
        floorLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JLabel floorValue = new JLabel("Tầng " + room.getFloorNo());
        floorValue.setFont(new Font("Arial", Font.PLAIN, 13));
        contentPanel.add(floorLabel);
        contentPanel.add(floorValue);

        JLabel statusLabel = new JLabel("Trạng thái:");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JLabel statusValue = new JLabel(room.getStatus());
        statusValue.setFont(new Font("Arial", Font.PLAIN, 13));
        contentPanel.add(statusLabel);
        contentPanel.add(statusValue);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(PANEL_BG);

        JButton confirmButton = new JButton("Xác nhận");
        confirmButton.setPreferredSize(new Dimension(100, 35));
        confirmButton.setBackground(PRIMARY_COLOR);
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        confirmButton.setBorderPainted(false);
        confirmButton.setFont(new Font("Arial", Font.BOLD, 13));
        confirmButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmButton.addActionListener(e -> {
            try {
                if (filterByBookingId <= 0) {
                    JOptionPane.showMessageDialog(detailDialog, "Vui lòng chọn đặt phòng trước!",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!"AVAILABLE".equals(room.getStatus())) {
                    JOptionPane.showMessageDialog(detailDialog, "Phòng này không còn trống!",
                            "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Kiểm tra lại một lần nữa trước khi thêm
                if (bookingRoomBUS.isRoomCurrentlyBooked(room.getRoomId())) {
                    JOptionPane.showMessageDialog(detailDialog,
                            "Phòng này đã được đặt bởi khách hàng khác!",
                            "Không thể thêm phòng", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                java.time.LocalDateTime checkIn = java.time.LocalDateTime.now();
                java.time.LocalDateTime checkOut = checkIn.plusDays(1);

                RoomTypeBUS roomTypeBUS = new RoomTypeBUS();
                RoomTypeDTO roomType = roomTypeBUS.getRoomTypeById(room.getRoomTypeId());
                BigDecimal ratePerNight = roomType.getBasePrice() != null ? roomType.getBasePrice() : BigDecimal.ZERO;

                BookingRoomDTO newBookingRoom = new BookingRoomDTO(0, filterByBookingId, room.getRoomId(),
                        checkIn, checkOut, null, null, 1, 0, ratePerNight, null, new BigDecimal(10), "BOOKED");

                if (bookingRoomBUS.addBookingRoom(newBookingRoom)) {
                    JOptionPane.showMessageDialog(detailDialog, "Thêm phòng thành công!");
                    detailDialog.dispose();
                    loadBookingRoomData();
                } else {
                    JOptionPane.showMessageDialog(detailDialog, "Thêm phòng thất bại!\nPhòng có thể đã được đặt.",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(detailDialog, "Lỗi: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(confirmButton);

        JButton cancelButton = new JButton("Hủy");
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.setBackground(DANGER_COLOR);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 13));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> detailDialog.dispose());
        buttonPanel.add(cancelButton);

        detailDialog.add(contentPanel, BorderLayout.CENTER);
        detailDialog.add(buttonPanel, BorderLayout.SOUTH);
        detailDialog.setLocationRelativeTo(this);
        detailDialog.setVisible(true);
    }
}
