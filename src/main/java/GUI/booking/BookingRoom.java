package GUI.booking;

import BUS.BookingRoomBUS;
import DAO.BookingRoomDAO;
import DTO.BookingRoomDTO;
import GUI.booking.Guest;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;
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

    public BookingRoom() {
        bookingRoomBUS = new BookingRoomBUS(new BookingRoomDAO());
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

        JButton reloadButton = new JButton("⟳ Reload");
        reloadButton.setPreferredSize(new Dimension(100, 35));
        reloadButton.setBackground(SUCCESS_COLOR);
        reloadButton.setForeground(Color.WHITE);
        reloadButton.setFocusPainted(false);
        reloadButton.setBorderPainted(false);
        reloadButton.setFont(new Font("Arial", Font.BOLD, 13));
        reloadButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        reloadButton.addActionListener(e -> loadBookingRoomData());
        controlPanel.add(reloadButton);

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
            bookingRoomData = bookingRoomBUS.getAllBookingRooms();
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
            List<BookingRoomDTO> results = bookingRoomBUS.getAllBookingRooms();
            List<BookingRoomDTO> filtered = results.stream()
                    .filter(br -> String.valueOf(br.getBookingRoomId()).contains(query) ||
                            String.valueOf(br.getBookingId()).contains(query) ||
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
        String[] columnNames = {"Mã phòng đặt", "Mã đặt phòng", "Mã phòng", "Ngày nhận", "Ngày trả", "Thuế", "Trạng thái"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (BookingRoomDTO br : data) {
            Object[] row = {
                    "PD00" + br.getBookingRoomId(),
                    "DP00" + br.getBookingId(),
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

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Sửa");
        JMenuItem deleteItem = new JMenuItem("Xóa");
        JMenuItem guestItem = new JMenuItem("Xem khách hàng");
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);
        popupMenu.addSeparator();
        popupMenu.add(guestItem);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger() && table.getSelectedRow() != -1) {
                    int rowIndex = table.getSelectedRow();
                    BookingRoomDTO br = data.get(rowIndex);

                    editItem.addActionListener(e1 -> editBookingRoom(br));
                    deleteItem.addActionListener(e1 -> deleteBookingRoom(br));
                    guestItem.addActionListener(e1 -> openGuestGUI());

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
        JDialog addDialog = new JDialog(this, "Thêm phòng đặt mới", true);
        addDialog.setLayout(new BorderLayout());
        addDialog.setSize(450, 380);

        JPanel contentPanel = new JPanel(new GridLayout(6, 2, 15, 15));
        contentPanel.setBackground(PANEL_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel bookingIdLabel = new JLabel("Mã đặt phòng (*):");
        bookingIdLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField bookingIdField = new JTextField();
        bookingIdField.setFont(new Font("Arial", Font.PLAIN, 13));
        contentPanel.add(bookingIdLabel);
        contentPanel.add(bookingIdField);

        JLabel roomIdLabel = new JLabel("Mã phòng (*):");
        roomIdLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField roomIdField = new JTextField();
        roomIdField.setFont(new Font("Arial", Font.PLAIN, 13));
        contentPanel.add(roomIdLabel);
        contentPanel.add(roomIdField);

        JLabel adultsLabel = new JLabel("Số người lớn (*):");
        adultsLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField adultsField = new JTextField("1");
        adultsField.setFont(new Font("Arial", Font.PLAIN, 13));
        contentPanel.add(adultsLabel);
        contentPanel.add(adultsField);

        JLabel childrenLabel = new JLabel("Số trẻ em:");
        childrenLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField childrenField = new JTextField("0");
        childrenField.setFont(new Font("Arial", Font.PLAIN, 13));
        contentPanel.add(childrenLabel);
        contentPanel.add(childrenField);

        JLabel rateLabel = new JLabel("Giá phòng/đêm (*):");
        rateLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField rateField = new JTextField();
        rateField.setFont(new Font("Arial", Font.PLAIN, 13));
        contentPanel.add(rateLabel);
        contentPanel.add(rateField);

        JLabel daysLabel = new JLabel("Số đêm ở:");
        daysLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField daysField = new JTextField("1");
        daysField.setFont(new Font("Arial", Font.PLAIN, 13));
        contentPanel.add(daysLabel);
        contentPanel.add(daysField);

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
                String bookingIdStr = bookingIdField.getText().trim();
                String roomIdStr = roomIdField.getText().trim();
                String adultsStr = adultsField.getText().trim();
                String childrenStr = childrenField.getText().trim();
                String rateStr = rateField.getText().trim();
                String daysStr = daysField.getText().trim();

                if (bookingIdStr.isEmpty() || roomIdStr.isEmpty() || rateStr.isEmpty()) {
                    JOptionPane.showMessageDialog(addDialog, "Vui lòng nhập đầy đủ thông tin (*)!",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int bookingId = Integer.parseInt(bookingIdStr);
                int roomId = Integer.parseInt(roomIdStr);
                int adults = Integer.parseInt(adultsStr);
                int children = Integer.parseInt(childrenStr);
                java.math.BigDecimal rate = new java.math.BigDecimal(rateStr);
                long days = Long.parseLong(daysStr);

                java.time.LocalDateTime checkIn = java.time.LocalDateTime.now();
                java.time.LocalDateTime checkOut = checkIn.plusDays(days);

                BookingRoomDTO newBookingRoom = new BookingRoomDTO(0, bookingId, roomId,
                        checkIn, checkOut, null, null, adults, children, rate, null, null, "Đã đặt");

                if (bookingRoomBUS.addBookingRoom(newBookingRoom)) {
                    JOptionPane.showMessageDialog(addDialog, "Thêm phòng đặt thành công!");
                    addDialog.dispose();
                    loadBookingRoomData();
                } else {
                    JOptionPane.showMessageDialog(addDialog, "Thêm phòng đặt thất bại!",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(addDialog, "Vui lòng nhập đúng định dạng số!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(addDialog, "Lỗi: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(confirmButton);

        addDialog.add(contentPanel, BorderLayout.CENTER);
        addDialog.add(buttonPanel, BorderLayout.SOUTH);
        addDialog.setLocationRelativeTo(this);
        addDialog.setVisible(true);
    }
}
