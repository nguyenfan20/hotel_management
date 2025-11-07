package GUI.booking;

import BUS.BookingRoomBUS;
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
import java.text.SimpleDateFormat;
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
    private JScrollPane scrollPane;
    private JTextField searchField;
    private List<BookingRoomDTO> bookingRoomData;
    private int filterByBookingId = -1;

    public BookingRoom() {
        this(-1);
    }

    public BookingRoom(int bookingId) {
        bookingRoomBUS = new BookingRoomBUS(new BookingRoomDAO());
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
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Tất cả", "Đã đặt", "Đã nhận", "Đã trả", "Đã hủy"});
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
            if ("Tất cả".equals(status)) {
                results = bookingRoomData;
            } else {
                results = bookingRoomBUS.getBookingRoomsByStatus(status);
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
                return false;  // Bỏ tính năng edit table
            }
        };

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (BookingRoomDTO br : data) {
            Object[] row = {
                    "PD00" + br.getBookingRoomId(),
                    "P00" + br.getRoomId(),
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
                    BookingRoomDTO br = data.get(rowIndex);

                    JPopupMenu popupMenu = new JPopupMenu();
                    JMenuItem editItem = new JMenuItem("Sửa");
                    JMenuItem deleteItem = new JMenuItem("Xóa");
                    JMenuItem guestItem = new JMenuItem("Xem khách hàng");

                    editItem.addActionListener(e1 -> editBookingRoom(br));
                    deleteItem.addActionListener(e1 -> deleteBookingRoom(br));
                    guestItem.addActionListener(e1 -> openGuestGUI());

                    popupMenu.add(editItem);
                    popupMenu.add(deleteItem);
                    popupMenu.addSeparator();
                    popupMenu.add(guestItem);

                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        scrollPane.setViewportView(table);
    }

    private void editBookingRoom(BookingRoomDTO br) {
        String newStatus = JOptionPane.showInputDialog(this, "Nhập trạng thái mới:", br.getStatus());
        if (newStatus != null && !newStatus.isEmpty()) {
            try {
                br.setStatus(newStatus);
                if (bookingRoomBUS.updateBookingRoom(br)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                    loadBookingRoomData();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
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

    private void openGuestGUI() {
        try {
            Guest guestWindow = new Guest();
            guestWindow.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi mở giao diện: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addNewBookingRoom() {
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
            // Get all rooms with AVAILABLE status and not currently booked
            RoomBUS roomBUS = new RoomBUS();
            RoomTypeBUS roomTypeBUS = new RoomTypeBUS();

            List<RoomDTO> availableRooms = roomBUS.getAllRooms().stream()
                    .filter(room -> "AVAILABLE".equals(room.getStatus()) &&
                            !bookingRoomBUS.isRoomCurrentlyBooked(room.getRoomId()))
                    .toList();

            Map<Integer, String> roomTypes = new HashMap<>();
            List<RoomTypeDTO> roomTypeList = roomTypeBUS.getAllRoomTypes();
            for (RoomTypeDTO roomType : roomTypeList) {
                roomTypes.put(roomType.getRoomTypeId(), roomType.getName());
            }

            // Group rooms by floor and display in grid
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

                BookingRoomDTO newBookingRoom = new BookingRoomDTO(0, filterByBookingId, room.getRoomId(),
                        checkIn, checkOut, null, null, 1, 0, java.math.BigDecimal.ZERO, null, null, "Đã đặt");

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