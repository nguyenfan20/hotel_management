/*
 * Click nbfs://SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package GUI.room;

import BUS.*;
import DTO.*;
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

/**
 * @author Acer
 */
public class Room extends javax.swing.JPanel {
    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    private static final Color AVAILABLE_COLOR = new Color(232, 245, 233);
    private static final Color RESERVED_COLOR = new Color(255, 243, 224);
    private static final Color OCCUPIED_COLOR = new Color(255, 235, 238);
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

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        controlPanel.setBackground(PANEL_BG);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Nút Thêm phòng
        JButton addButton = new JButton("Thêm phòng");
        addButton.setPreferredSize(new Dimension(140, 35));
        addButton.setIcon(new ImageIcon(getClass().getResource("/images/add-button.png")));
        addButton.setBackground(PRIMARY_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setFont(new Font("Arial", Font.BOLD, 13));
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e -> showAddRoomDialog());
        controlPanel.add(addButton);

        // Nút Đặt phòng
        JButton bookButton = new JButton("Đặt phòng");
        bookButton.setPreferredSize(new Dimension(140, 35));
        bookButton.setBackground(PRIMARY_COLOR);
        bookButton.setForeground(Color.WHITE);
        bookButton.setFocusPainted(false);
        bookButton.setBorderPainted(false);
        bookButton.setFont(new Font("Arial", Font.BOLD, 13));
        bookButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bookButton.setToolTipText("Mở form đặt phòng");
        bookButton.addActionListener(e -> showBookingDialog());
        controlPanel.add(bookButton);

        controlPanel.add(Box.createHorizontalStrut(20));

        // Tìm kiếm
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

        JButton filterButton = createIconButton("/images/filter.png");
        filterButton.addActionListener(e -> showFilterDialog());
        controlPanel.add(filterButton);

        toggleButton = createIconButton("/images/grid.png");
        toggleButton.addActionListener(e -> {
            isGridView = !isGridView;
            toggleButton.setIcon(new ImageIcon(getClass().getResource(isGridView ? "/images/grid.png" : "/images/cells.png")));
            updateView();
        });
        controlPanel.add(toggleButton);

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
            button.setText(iconPath.contains("grid") ? "Grid" : iconPath.contains("cells") ? "Table" : "Icon");
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
            JOptionPane.showMessageDialog(this, "Lỗi khi tải loại phòng: " + e.getMessage());
        }
    }

    private void loadData() {
        try {
            roomData = roomBUS.getAllRooms();
            filteredRoomData = new ArrayList<>(roomData);
            updateView();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu phòng: " + e.getMessage());
        }
    }

    private void performSearch() {
        String query = searchField.getText().trim().toLowerCase();
        filteredRoomData = query.isEmpty() ? new ArrayList<>(roomData) : roomData.stream()
                .filter(room -> {
                    String roomNo = room.getRoomNo().toLowerCase();
                    String floorNo = String.valueOf(room.getFloorNo());
                    String status = room.getStatus().toLowerCase();
                    String roomType = roomTypes.getOrDefault(room.getRoomTypeId(), "").toLowerCase();
                    String note = room.getNote() != null ? room.getNote().toLowerCase() : "";
                    return roomNo.contains(query) || floorNo.contains(query) || status.contains(query) ||
                            roomType.contains(query) || note.contains(query);
                })
                .collect(Collectors.toList());
        updateView();
    }

    private void showFilterDialog() {
        JDialog filterDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Lọc phòng", true);
        filterDialog.setLayout(new BorderLayout());
        filterDialog.setSize(350, 200);

        JPanel contentPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        contentPanel.setBackground(PANEL_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel roomTypeLabel = new JLabel("Loại phòng:");
        JComboBox<String> roomTypeCombo = new JComboBox<>();
        roomTypeCombo.addItem("Tất cả");
        roomTypes.values().forEach(roomTypeCombo::addItem);

        JLabel statusLabel = new JLabel("Trạng thái:");
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Tất cả", "AVAILABLE", "RESERVED", "OCCUPIED"});

        contentPanel.add(roomTypeLabel); contentPanel.add(roomTypeCombo);
        contentPanel.add(statusLabel); contentPanel.add(statusCombo);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton confirmButton = new JButton("Xác nhận");
        confirmButton.setPreferredSize(new Dimension(100, 35));
        confirmButton.setBackground(PRIMARY_COLOR);
        confirmButton.setForeground(Color.WHITE);
        confirmButton.addActionListener(e -> {
            filterRooms((String) roomTypeCombo.getSelectedItem(), (String) statusCombo.getSelectedItem());
            filterDialog.dispose();
        });
        buttonPanel.add(confirmButton);
        buttonPanel.setBackground(PANEL_BG);

        filterDialog.add(contentPanel, BorderLayout.CENTER);
        filterDialog.add(buttonPanel, BorderLayout.SOUTH);
        filterDialog.setLocationRelativeTo(this);
        filterDialog.setVisible(true);
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

        // Room basic info
        addDetailRow(contentPanel, "Số phòng:", room.getRoomNo());
        addDetailRow(contentPanel, "ID phòng:", String.valueOf(room.getRoomId()));
        addDetailRow(contentPanel, "Loại phòng:", roomTypes.getOrDefault(room.getRoomTypeId(), "Unknown"));
        addDetailRow(contentPanel, "Tầng:", String.valueOf(room.getFloorNo()));
        addDetailRow(contentPanel, "Trạng thái:", room.getStatus());
        addDetailRow(contentPanel, "Ghi chú:", room.getNote() != null ? room.getNote() : "Không có");

        // Try to get room type details
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

        // Close button
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
        JLabel l = new JLabel(label); l.setFont(new Font("Arial", Font.BOLD, 12)); l.setPreferredSize(new Dimension(150, 30));
        JLabel v = new JLabel(value); v.setFont(new Font("Arial", Font.PLAIN, 12)); v.setForeground(TEXT_COLOR);
        row.add(l, BorderLayout.WEST); row.add(v, BorderLayout.CENTER);
        panel.add(row); panel.add(Box.createVerticalStrut(8));
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

    // ===== CHECK-IN NHANH =====
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
                // Tạo khách hàng
                CustomerDTO customer = new CustomerDTO();
                customer.setFull_name(name);
                customer.setPhone(Integer.parseInt(phone));
                int customerId = customerBUS.addCustomerReturnId(customer);
                if (customerId <= 0) throw new Exception("Không tạo được khách hàng");

                // Tạo booking
                String code = "BOOK-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" +
                        String.format("%03d", bookingBUS.getAllBookings().size() + 1);
                BookingDTO booking = new BookingDTO();
                booking.setCode(code);
                booking.setCustomerId(customerId);
                booking.setBookingDate(LocalDateTime.now());
                booking.setStatus("CHECKED_IN");
                booking.setSource("QUICK_CHECKIN");
                boolean bookingId = bookingBUS.addBooking(booking);
                if (bookingId) throw new Exception("Không tạo được booking");

                // Tạo booking room
                BookingRoomDTO br = new BookingRoomDTO();
                br.setBookingId(bookingId);
                br.setRoomId(room.getRoomId());
                br.setCheckInPlan(LocalDateTime.parse(LocalDate.now().toString()));
                br.setCheckOutPlan(null);
                if (!bookingRoomBUS.addBookingRoom(br)) throw new Exception("Không thêm phòng vào booking");

                // Cập nhật trạng thái phòng
                room.setStatus("OCCUPIED");
                if (!roomBUS.updateRoom(room)) throw new Exception("Không cập nhật trạng thái phòng");

                JOptionPane.showMessageDialog(dialog, "Check-in thành công!\nMã: " + code);
                loadData();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage());
            }
        });
        JButton cancel = new JButton("Hủy");
        cancel.addActionListener(e -> dialog.dispose());
        btnPanel.add(ok); btnPanel.add(cancel);
        btnPanel.setBackground(PANEL_BG);

        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // ===== GRID VIEW + MENU CHUỘT PHẢI =====
    private void showGridView() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_COLOR);

        int currentFloor = -1;
        JPanel rowPanel = null;

        for (RoomDTO room : filteredRoomData) {
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

            switch (room.getStatus()) {
                case "AVAILABLE": btn.setBackground(AVAILABLE_COLOR); break;
                case "RESERVED": btn.setBackground(RESERVED_COLOR); break;
                case "OCCUPIED": btn.setBackground(OCCUPIED_COLOR); break;
            }
            btn.setOpaque(true);

            // Click trái: xem chi tiết
            btn.addActionListener(e -> showRoomDetailDialog(room));

            // Click phải: menu
            JPopupMenu popup = new JPopupMenu();
            JMenuItem detail = new JMenuItem("Xem chi tiết");
            JMenuItem book = new JMenuItem("Đặt phòng");
            JMenuItem checkin = new JMenuItem("Check-in nhanh");

            detail.addActionListener(e -> showRoomDetailDialog(room));
            book.addActionListener(e -> showBookingDialog());
            checkin.addActionListener(e -> quickCheckin(room));
            checkin.setEnabled(room.getStatus().equals("AVAILABLE"));

            popup.add(detail);
            popup.add(book);
            popup.add(checkin);

            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) showPopup(e);
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e.isPopupTrigger()) showPopup(e);
                }
                private void showPopup(MouseEvent e) {
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            });

            rowPanel.add(btn);
            if (rowPanel.getComponentCount() == 4) {
                rowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
                rowPanel.setBackground(BACKGROUND_COLOR);
                mainPanel.add(rowPanel);
            }
        }

        scrollPane.setViewportView(mainPanel);
    }

    private void showTableView() {
        String[] columnNames = {"ID", "Số phòng", "Tầng", "Trạng thái", "Loại phòng", "Ghi chú"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (RoomDTO room : filteredRoomData) {
            Object[] row = {
                    room.getRoomId(),
                    room.getRoomNo(),
                    room.getFloorNo(),
                    room.getStatus(),
                    roomTypes.getOrDefault(room.getRoomTypeId(), "Unknown"),
                    room.getNote()
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

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Sửa");
        JMenuItem deleteItem = new JMenuItem("Xóa");
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger() && table.getSelectedRow() != -1) {
                    int rowIndex = table.getSelectedRow();
                    int modelRow = table.convertRowIndexToModel(rowIndex);
                    RoomDTO room = filteredRoomData.get(modelRow);

                    editItem.addActionListener(e1 -> showEditRoomDialog(room));
                    deleteItem.addActionListener(e1 -> deleteRoom(room.getRoomId(), room.getStatus()));

                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        scrollPane.setViewportView(table);
    }
}
