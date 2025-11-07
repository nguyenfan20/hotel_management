package GUI.room;

import BUS.*;
import DTO.*;
import GUI.dashboard.ModernScrollBarUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Room extends javax.swing.JPanel {
    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    private static final Color AVAILABLE_COLOR = new Color(232, 245, 233);
    private static final Color RESERVED_COLOR = new Color(255, 243, 224);
    private static final Color OCCUPIED_COLOR = new Color(255, 235, 238);
    private static final Color CLEANING_COLOR = new Color(227, 242, 253);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color PANEL_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(224, 224, 224);
    private static final Color TEXT_COLOR = new Color(33, 33, 33);

    private JPanel mainPanel;
    private JScrollPane scrollPane;
    private boolean isGridView = true;
    private JButton toggleButton;
    private JTextField searchField;
    private List<RoomDTO> roomData = new ArrayList<>();
    private List<RoomDTO> filteredRoomData = new ArrayList<>();
    private Map<Integer, String> roomTypes = new HashMap<>();

    private RoomBUS roomBUS;
    private RoomTypeBUS roomTypeBUS;
    private BookingBUS bookingBUS;
    private BookingRoomBUS bookingRoomBUS;
    private CustomerBUS customerBUS;

    public Room() {
        roomBUS = new RoomBUS();
        roomTypeBUS = new RoomTypeBUS();
        bookingBUS = new BookingBUS(new DAO.BookingDAO());
        bookingRoomBUS = new BookingRoomBUS(new DAO.BookingRoomDAO());
        customerBUS = new CustomerBUS();
        initComponents();
        loadRoomTypes();
        loadData();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // === CONTROL PANEL ===
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        controlPanel.setBackground(PANEL_BG);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JButton addButton = new JButton("Thêm phòng");
        addButton.setPreferredSize(new Dimension(140, 35));
        addButton.setIcon(new ImageIcon(getClass().getResource("/images/add-button.png")));
        addButton.setBackground(PRIMARY_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e -> showAddRoomDialog());
        controlPanel.add(addButton);

        JButton bookButton = new JButton("Đặt phòng");
        bookButton.setPreferredSize(new Dimension(140, 35));
        bookButton.setBackground(PRIMARY_COLOR);
        bookButton.setForeground(Color.WHITE);
        bookButton.setFocusPainted(false);
        bookButton.setBorderPainted(false);
        bookButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bookButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bookButton.setToolTipText("Mở form đặt phòng mới");
        bookButton.addActionListener(e -> showBookingDialog());
        controlPanel.add(bookButton);

        controlPanel.add(Box.createHorizontalStrut(20));

        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(250, 35));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
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

        toggleButton = createIconButton("/images/grid.png");
        toggleButton.setToolTipText("Chuyển đổi giữa lưới và bảng");
        toggleButton.addActionListener(e -> {
            isGridView = !isGridView;
            toggleButton.setIcon(new ImageIcon(getClass().getResource(isGridView ? "/images/grid.png" : "/images/cells.png")));
            updateView();
        });
        controlPanel.add(toggleButton);

        add(controlPanel, BorderLayout.NORTH);

        // === SCROLL PANE VỚI MODERN SCROLLBAR ===
        scrollPane = new JScrollPane();
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Áp dụng Modern Scrollbar
        scrollPane.getVerticalScrollBar().setUI(new ModernScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new ModernScrollBarUI());

        add(scrollPane, BorderLayout.CENTER);
    }

    private JButton createIconButton(String iconPath) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(35, 35));
        try {
            button.setIcon(new ImageIcon(getClass().getResource(iconPath)));
        } catch (Exception ex) {
            button.setText(iconPath.contains("grid") ? "Grid" : "Icon");
        }
        button.setBackground(PANEL_BG);
        button.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void loadRoomTypes() {
        try {
            List<RoomTypeDTO> list = roomTypeBUS.getAllRoomTypes();
            roomTypes.clear();
            for (RoomTypeDTO rt : list) {
                roomTypes.put(rt.getRoomTypeId(), rt.getName());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải loại phòng: " + e.getMessage());
        }
    }

    private void loadData() {
        try {
            roomData = roomBUS.getAllRooms();
            filteredRoomData = new ArrayList<>(roomData);
            updateView();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu phòng!");
        }
    }

    private void performSearch() {
        String query = searchField.getText().trim().toLowerCase();
        filteredRoomData = query.isEmpty() ? new ArrayList<>(roomData) :
                roomData.stream()
                        .filter(room -> {
                            String roomNo = room.getRoomNo().toLowerCase();
                            String floor = String.valueOf(room.getFloorNo());
                            String status = room.getStatus().toLowerCase();
                            String type = roomTypes.getOrDefault(room.getRoomTypeId(), "").toLowerCase();
                            String note = room.getNote() != null ? room.getNote().toLowerCase() : "";
                            return roomNo.contains(query) || floor.contains(query) ||
                                    status.contains(query) || type.contains(query) || note.contains(query);
                        })
                        .collect(Collectors.toList());
        updateView();
    }

    private void showFilterDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Lọc phòng", true);
        dialog.setSize(380, 220);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(3, 2, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        panel.setBackground(PANEL_BG);

        panel.add(new JLabel("Loại phòng:"));
        JComboBox<String> typeCombo = new JComboBox<>();
        typeCombo.addItem("Tất cả");
        roomTypes.values().stream().sorted().forEach(typeCombo::addItem);

        panel.add(new JLabel("Trạng thái:"));
        JComboBox<String> statusCombo = new JComboBox<>();
        statusCombo.addItem("Tất cả");
        statusCombo.addItem("AVAILABLE");
        statusCombo.addItem("RESERVED");
        statusCombo.addItem("OCCUPIED");

        panel.add(new JLabel("Tầng:"));
        JComboBox<String> floorCombo = new JComboBox<>();
        floorCombo.addItem("Tất cả");
        roomData.stream()
                .map(r -> r.getFloorNo())
                .distinct()
                .sorted()
                .forEach(f -> floorCombo.addItem(String.valueOf(f)));

        panel.add(typeCombo);
        panel.add(statusCombo);
        panel.add(floorCombo);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(PANEL_BG);
        JButton ok = new JButton("Áp dụng");
        ok.setBackground(PRIMARY_COLOR);
        ok.setForeground(Color.WHITE);
        ok.addActionListener(e -> {
            String type = (String) typeCombo.getSelectedItem();
            String status = (String) statusCombo.getSelectedItem();
            String floor = (String) floorCombo.getSelectedItem();

            filteredRoomData = roomData.stream()
                    .filter(r -> type.equals("Tất cả") || roomTypes.get(r.getRoomTypeId()).equals(type))
                    .filter(r -> status.equals("Tất cả") || r.getStatus().equals(status))
                    .filter(r -> floor.equals("Tất cả") || r.getFloorNo() == Integer.parseInt(floor))
                    .collect(Collectors.toList());
            updateView();
            dialog.dispose();
        });
        btnPanel.add(ok);
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void filterRooms(String roomType, String status) {
        filteredRoomData = roomData.stream()
                .filter(room -> (roomType.equals("Tất cả") || roomTypes.get(room.getRoomTypeId()).equals(roomType))
                        && (status.equals("Tất cả") || room.getStatus().equals(status)))
                .collect(Collectors.toList());
        updateView();
    }

    private void showAddRoomDialog() {
        JDialog addDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm phòng", true);
        addDialog.setLayout(new BorderLayout());
        addDialog.setSize(400, 350);

        JPanel contentPanel = new JPanel(new GridLayout(5, 2, 15, 15));
        contentPanel.setBackground(PANEL_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel roomNoLabel = new JLabel("Số phòng:");
        roomNoLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField roomNoField = new JTextField();
        roomNoField.setPreferredSize(new Dimension(150, 30));

        JLabel floorNoLabel = new JLabel("Tầng:");
        floorNoLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField floorNoField = new JTextField();
        floorNoField.setPreferredSize(new Dimension(150, 30));

        JLabel statusLabel = new JLabel("Trạng thái:");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"AVAILABLE", "RESERVED", "OCCUPIED"});
        statusCombo.setPreferredSize(new Dimension(150, 30));

        JLabel roomTypeLabel = new JLabel("Loại phòng:");
        roomTypeLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JComboBox<String> roomTypeCombo = new JComboBox<>();
        for (String typeName : roomTypes.values()) {
            roomTypeCombo.addItem(typeName);
        }
        roomTypeCombo.setPreferredSize(new Dimension(150, 30));

        JLabel noteLabel = new JLabel("Ghi chú:");
        noteLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField noteField = new JTextField();
        noteField.setPreferredSize(new Dimension(150, 30));

        contentPanel.add(roomNoLabel);
        contentPanel.add(roomNoField);
        contentPanel.add(floorNoLabel);
        contentPanel.add(floorNoField);
        contentPanel.add(statusLabel);
        contentPanel.add(statusCombo);
        contentPanel.add(roomTypeLabel);
        contentPanel.add(roomTypeCombo);
        contentPanel.add(noteLabel);
        contentPanel.add(noteField);

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
                String roomNo = roomNoField.getText().trim();
                String floorNoText = floorNoField.getText().trim();
                String status = (String) statusCombo.getSelectedItem();
                String roomType = (String) roomTypeCombo.getSelectedItem();
                String note = noteField.getText().trim();

                if (roomNo.isEmpty() || floorNoText.isEmpty()) {
                    JOptionPane.showMessageDialog(addDialog, "Vui lòng điền đầy đủ số phòng và tầng!");
                    return;
                }

                byte floorNo;
                try {
                    floorNo = Byte.parseByte(floorNoText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(addDialog, "Tầng phải là số nguyên hợp lệ (0-127)!");
                    return;
                }

                int roomTypeId = roomTypes.entrySet().stream()
                        .filter(entry -> entry.getValue().equals(roomType))
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElse(-1);

                RoomDTO newRoom = new RoomDTO(roomNo, floorNo, roomTypeId, status, note.isEmpty() ? null : note);
                boolean success = roomBUS.addRoom(newRoom);

                if (success) {
                    JOptionPane.showMessageDialog(addDialog, "Thêm phòng thành công!");
                    loadData();
                    addDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(addDialog, "Không thể thêm phòng!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(addDialog, "Lỗi khi thêm phòng: " + ex.getMessage());
            }
        });
        buttonPanel.add(confirmButton);

        addDialog.add(contentPanel, BorderLayout.CENTER);
        addDialog.add(buttonPanel, BorderLayout.SOUTH);
        addDialog.setLocationRelativeTo(this);
        addDialog.setVisible(true);
    }

    private void showEditRoomDialog(RoomDTO room) {
        JDialog editDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa phòng", true);
        editDialog.setLayout(new BorderLayout());
        editDialog.setSize(400, 350);

        JPanel contentPanel = new JPanel(new GridLayout(5, 2, 15, 15));
        contentPanel.setBackground(PANEL_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel roomNoLabel = new JLabel("Số phòng:");
        roomNoLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField roomNoField = new JTextField(room.getRoomNo());
        roomNoField.setPreferredSize(new Dimension(150, 30));

        JLabel floorNoLabel = new JLabel("Tầng:");
        floorNoLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField floorNoField = new JTextField(String.valueOf(room.getFloorNo()));
        floorNoField.setPreferredSize(new Dimension(150, 30));

        JLabel statusLabel = new JLabel("Trạng thái:");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"AVAILABLE", "RESERVED", "OCCUPIED"});
        statusCombo.setSelectedItem(room.getStatus());
        statusCombo.setPreferredSize(new Dimension(150, 30));

        JLabel roomTypeLabel = new JLabel("Loại phòng:");
        roomTypeLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JComboBox<String> roomTypeCombo = new JComboBox<>();
        for (String typeName : roomTypes.values()) {
            roomTypeCombo.addItem(typeName);
        }
        roomTypeCombo.setSelectedItem(roomTypes.get(room.getRoomTypeId()));
        roomTypeCombo.setPreferredSize(new Dimension(150, 30));

        JLabel noteLabel = new JLabel("Ghi chú:");
        noteLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField noteField = new JTextField(room.getNote() != null ? room.getNote() : "");
        noteField.setPreferredSize(new Dimension(150, 30));

        contentPanel.add(roomNoLabel);
        contentPanel.add(roomNoField);
        contentPanel.add(floorNoLabel);
        contentPanel.add(floorNoField);
        contentPanel.add(statusLabel);
        contentPanel.add(statusCombo);
        contentPanel.add(roomTypeLabel);
        contentPanel.add(roomTypeCombo);
        contentPanel.add(noteLabel);
        contentPanel.add(noteField);

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
                String newRoomNo = roomNoField.getText().trim();
                String floorNoText = floorNoField.getText().trim();
                String newStatus = (String) statusCombo.getSelectedItem();
                String newRoomType = (String) roomTypeCombo.getSelectedItem();
                String newNote = noteField.getText().trim();

                if (newRoomNo.isEmpty() || floorNoText.isEmpty()) {
                    JOptionPane.showMessageDialog(editDialog, "Vui lòng điền đầy đủ số phòng và tầng!");
                    return;
                }

                byte newFloorNo;
                try {
                    newFloorNo = Byte.parseByte(floorNoText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(editDialog, "Tầng phải là số nguyên hợp lệ (0-127)!");
                    return;
                }

                int newRoomTypeId = roomTypes.entrySet().stream()
                        .filter(entry -> entry.getValue().equals(newRoomType))
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElse(-1);

                RoomDTO updatedRoom = new RoomDTO(room.getRoomId(), newRoomNo, newFloorNo, newRoomTypeId, newStatus,
                        newNote.isEmpty() ? null : newNote);
                boolean success = roomBUS.updateRoom(updatedRoom);

                if (success) {
                    JOptionPane.showMessageDialog(editDialog, "Sửa phòng thành công!");
                    loadData();
                    editDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(editDialog, "Không thể sửa phòng!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(editDialog, "Lỗi khi sửa phòng: " + ex.getMessage());
            }
        });
        buttonPanel.add(confirmButton);

        editDialog.add(contentPanel, BorderLayout.CENTER);
        editDialog.add(buttonPanel, BorderLayout.SOUTH);
        editDialog.setLocationRelativeTo(this);
        editDialog.setVisible(true);
    }

    private void deleteRoom(int roomId, String status) {
        if (!status.equals("AVAILABLE")) {
            JOptionPane.showMessageDialog(this, "Chỉ có thể xóa phòng trống!");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa phòng ID: " + roomId + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION && roomBUS.deleteRoom(roomId)) {
            JOptionPane.showMessageDialog(this, "Xóa thành công!");
            loadData();
        }
    }

    private void showRoomDetailDialog(RoomDTO room) {
        JDialog detailDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chi tiết phòng", true);
        detailDialog.setLayout(new BorderLayout());
        detailDialog.setSize(500, 450);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(PANEL_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        addDetailRow(contentPanel, "Số phòng:", room.getRoomNo());
        addDetailRow(contentPanel, "ID phòng:", String.valueOf(room.getRoomId()));
        addDetailRow(contentPanel, "Loại phòng:", roomTypes.getOrDefault(room.getRoomTypeId(), "Unknown"));
        addDetailRow(contentPanel, "Tầng:", String.valueOf(room.getFloorNo()));
        addDetailRow(contentPanel, "Trạng thái:", room.getStatus());
        addDetailRow(contentPanel, "Ghi chú:", room.getNote() != null ? room.getNote() : "Không có");

        try {
            RoomTypeDTO roomType = roomTypeBUS.getRoomTypeById(room.getRoomTypeId());
            if (roomType != null) {
                contentPanel.add(Box.createVerticalStrut(15));
                JLabel typeInfoLabel = new JLabel("Thông tin loại phòng:");
                typeInfoLabel.setFont(new Font("Arial", Font.BOLD, 13));
                typeInfoLabel.setForeground(PRIMARY_COLOR);
                contentPanel.add(typeInfoLabel);

                addDetailRow(contentPanel, "Tên loại:", roomType.getName());
                addDetailRow(contentPanel, "Giá cơ bản:", roomType.getBasePrice() != null ?
                        String.valueOf(roomType.getBasePrice()) : "N/A");
                addDetailRow(contentPanel, "Sức chứa người lớn:", String.valueOf(roomType.getCapacityAdults()));
                addDetailRow(contentPanel, "Sức chứa trẻ em:", String.valueOf(roomType.getCapacityChildren()));
                addDetailRow(contentPanel, "Số giường:", String.valueOf(roomType.getBedCount()));
                addDetailRow(contentPanel, "Diện tích:", roomType.getArea() != null ?
                        String.valueOf(roomType.getArea()) + " m²" : "N/A");
                addDetailRow(contentPanel, "Mô tả:", roomType.getDescription() != null ?
                        roomType.getDescription() : "Không có");
            }
        } catch (Exception e) {
            System.err.println("Lỗi lấy chi tiết loại phòng: " + e.getMessage());
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(PANEL_BG);
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
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(PANEL_BG);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        JLabel l = new JLabel(label);
        l.setFont(new Font("Arial", Font.BOLD, 12));
        l.setPreferredSize(new Dimension(150, 30));
        JLabel v = new JLabel(value);
        v.setFont(new Font("Arial", Font.PLAIN, 12));
        v.setForeground(TEXT_COLOR);
        row.add(l, BorderLayout.WEST);
        row.add(v, BorderLayout.CENTER);
        panel.add(row);
        panel.add(Box.createVerticalStrut(8));
    }

    private void updateView() {
        if (isGridView) {
            showGridView();
        } else {
            showTableView();
        }
    }

    private void showBookingDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Tạo đơn đặt phòng", true);
        dialog.setSize(700, 600);
        dialog.setLayout(new BorderLayout());
        dialog.add(new GUI.booking.Booking(), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(PANEL_BG);
        JButton close = new JButton("Đóng");
        close.setPreferredSize(new Dimension(100, 35));
        close.setBackground(PRIMARY_COLOR);
        close.setForeground(Color.WHITE);
        close.addActionListener(e -> dialog.dispose());
        btnPanel.add(close);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void quickCheckin(RoomDTO room) {
        if (!room.getStatus().equals("AVAILABLE")) {
            JOptionPane.showMessageDialog(this, "Phòng không trống!");
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Check-in nhanh - Phòng " + room.getRoomNo(), true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        form.setBackground(PANEL_BG);

        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JSpinner adultsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        JSpinner childrenSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));

        form.add(new JLabel("Họ tên:")); form.add(nameField);
        form.add(new JLabel("SĐT:")); form.add(phoneField);
        form.add(new JLabel("Người lớn:")); form.add(adultsSpinner);
        form.add(new JLabel("Trẻ em:")); form.add(childrenSpinner);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton ok = new JButton("Check-in");
        ok.setBackground(PRIMARY_COLOR);
        ok.setForeground(Color.WHITE);
        ok.addActionListener(e -> {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập tên và SĐT!");
                return;
            }

            try {
                // ... existing code ...
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage());
            }
        });
        JButton cancel = new JButton("Hủy");
        cancel.addActionListener(e -> dialog.dispose());
        btnPanel.add(ok);
        btnPanel.add(cancel);
        btnPanel.setBackground(PANEL_BG);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showGridView() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_COLOR);

        int currentFloor = -1;
        JPanel rowPanel = null;

        for (RoomDTO room : filteredRoomData) {
            if (room.getFloorNo() != currentFloor) {
                currentFloor = room.getFloorNo();

                JPanel floorHeader = new JPanel(new FlowLayout(FlowLayout.CENTER));
                floorHeader.setBackground(BACKGROUND_COLOR);
                JLabel floorLabel = new JLabel("Tầng " + currentFloor);
                floorLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
                floorLabel.setForeground(new Color(50, 50, 50));
                floorLabel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER_COLOR),
                        BorderFactory.createEmptyBorder(12, 40, 12, 40)
                ));
                floorLabel.setBackground(PANEL_BG);
                floorLabel.setOpaque(true);
                floorHeader.add(floorLabel);
                mainPanel.add(floorHeader);
                mainPanel.add(Box.createVerticalStrut(10));

                rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 20));
                rowPanel.setBackground(BACKGROUND_COLOR);
                mainPanel.add(rowPanel);
            }

            JButton btn = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(getBackground());
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20); // Bo góc 20px
                    g2d.dispose();
                    super.paintComponent(g);
                }
            };
            btn.setPreferredSize(new Dimension(140, 140));
            btn.setLayout(new BorderLayout());
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 2),
                    BorderFactory.createEmptyBorder(12, 12, 12, 12)
            ));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Quan trọng: Tắt các thuộc tính mặc định để màu nền hiển thị luôn
            btn.setOpaque(false);  // Đổi sang false để paintComponent xử lý
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(true);  // Giữ viền nếu cần
            btn.setFocusPainted(false);

            // Icon
            JLabel iconLabel = new JLabel(new ImageIcon(getClass().getResource("/images/house.png")), SwingConstants.CENTER);
            btn.add(iconLabel, BorderLayout.CENTER);

            // Thông tin
            JPanel infoPanel = new JPanel(new BorderLayout(0, 4));
            infoPanel.setOpaque(false);
            JLabel roomNoLabel = new JLabel("Phòng " + room.getRoomNo(), SwingConstants.CENTER);
            roomNoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            roomNoLabel.setForeground(TEXT_COLOR);

            JLabel typeLabel = new JLabel(roomTypes.getOrDefault(room.getRoomTypeId(), "N/A"), SwingConstants.CENTER);
            typeLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            typeLabel.setForeground(new Color(100, 100, 100));

            infoPanel.add(roomNoLabel, BorderLayout.NORTH);
            infoPanel.add(typeLabel, BorderLayout.SOUTH);
            btn.add(infoPanel, BorderLayout.SOUTH);

            // Màu nền theo trạng thái
            Color bgColor;
            switch (room.getStatus()) {
                case "AVAILABLE":
                    bgColor = AVAILABLE_COLOR;
                    break;
                case "RESERVED":
                    bgColor = RESERVED_COLOR;
                    break;
                case "OCCUPIED":
                    bgColor = OCCUPIED_COLOR;
                    break;
                default:
                    bgColor = CLEANING_COLOR;
                    break;
            }
            btn.setBackground(bgColor);

            btn.addMouseListener(new MouseAdapter() {
                private Color originalColor = btn.getBackground();
                @Override
                public void mouseEntered(MouseEvent e) {
                    btn.setBackground(originalColor.brighter());
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    btn.setBackground(originalColor);
                }
            });

            btn.addActionListener(e -> showRoomDetailDialog(room));

            // Popup menu
            JPopupMenu popup = new JPopupMenu();
            JMenuItem detail = new JMenuItem("Xem chi tiết");
            JMenuItem book = new JMenuItem("Đặt phòng");
            JMenuItem checkin = new JMenuItem("Check-in nhanh");
            checkin.setEnabled("AVAILABLE".equals(room.getStatus()));

            detail.addActionListener(e -> showRoomDetailDialog(room));
            book.addActionListener(e -> showBookingDialog());
            checkin.addActionListener(e -> quickCheckin(room));

            popup.add(detail);
            popup.add(book);
            popup.add(checkin);

            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) { if (e.isPopupTrigger()) popup.show(e.getComponent(), e.getX(), e.getY()); }
                @Override
                public void mouseReleased(MouseEvent e) { if (e.isPopupTrigger()) popup.show(e.getComponent(), e.getX(), e.getY()); }
            });

            assert rowPanel != null;
            rowPanel.add(btn);

            if (rowPanel.getComponentCount() == 5) {
                rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 20));
                rowPanel.setBackground(BACKGROUND_COLOR);
                mainPanel.add(rowPanel);
            }
        }

        scrollPane.setViewportView(mainPanel);
    }

    private void showTableView() {
        String[] cols = {"ID", "Số phòng", "Tầng", "Trạng thái", "Loại phòng", "Ghi chú"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (RoomDTO r : filteredRoomData) {
            model.addRow(new Object[]{
                    r.getRoomId(),
                    r.getRoomNo(),
                    r.getFloorNo(),
                    r.getStatus(),
                    roomTypes.getOrDefault(r.getRoomTypeId(), ""),
                    r.getNote()
            });
        }

        JTable table = new JTable(model);
        table.setRowHeight(45);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(250, 250, 250));
        table.getTableHeader().setForeground(TEXT_COLOR);
        table.getTableHeader().setPreferredSize(new Dimension(0, 50));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 248, 248));
                }
                return c;
            }
        });

        scrollPane.setViewportView(table);
    }
}