package GUI.booking;

import BUS.BookingBUS;
import BUS.BookingRoomBUS;
import BUS.InvoiceBUS;
import DAO.BookingDAO;
import DAO.BookingRoomDAO;
import DTO.BookingDTO;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class BookingManagement extends javax.swing.JPanel {
    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color PANEL_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(224, 224, 224);
    private static final Color TEXT_COLOR = new Color(33, 33, 33);
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);
    private static final Color DANGER_COLOR = new Color(239, 68, 68);
    private static final Color WARNING_COLOR = new Color(241, 196, 15);

    private BookingBUS bookingBUS;
    private InvoiceBUS invoiceBUS;
    private BookingRoomBUS bookingRoomBUS;
    private javax.swing.JDialog jDialog1;
    private JScrollPane scrollPane;
    private JTextField searchField;
    private List<BookingDTO> bookingData;
    private List<BookingDTO> currentData;

    public BookingManagement() {
        bookingBUS = new BookingBUS(new BookingDAO());
        invoiceBUS = new InvoiceBUS();
        bookingRoomBUS = new BookingRoomBUS(new BookingRoomDAO());
        bookingData = new java.util.ArrayList<>();
        initComponents();
        loadBookingData();
    }

    // Khởi tạo các thành phần UI
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        controlPanel.setBackground(PANEL_BG);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

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

        JButton reloadButton = createIconButton("/icon/reload.png");
        reloadButton.addActionListener(e -> loadBookingData());
        controlPanel.add(reloadButton);

        JButton checkOutAllButton = new JButton("Check-out");
        checkOutAllButton.addActionListener(e -> checkOutAllSelected());
        controlPanel.add(checkOutAllButton);

        add(controlPanel, BorderLayout.NORTH);

        scrollPane = new JScrollPane();
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        add(scrollPane, BorderLayout.CENTER);

        updateTableView();
    }

    private JButton createIconButton(String iconPath) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(35, 35));

        // Try to load as image, if fails use as text
        if (iconPath.startsWith("/")) {
            try {
                button.setIcon(new ImageIcon(getClass().getResource(iconPath)));
            } catch (Exception e) {
                button.setText(iconPath);
            }
        } else {
            // Use text as icon symbol
            button.setText(iconPath);
            button.setFont(new Font("Arial", Font.BOLD, 16));
        }

        button.setBackground(PANEL_BG);
        button.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // Tải dữ liệu đặt phòng
    private void loadBookingData() {
        try {
            bookingData = bookingBUS.getAllBookings();
            currentData = bookingData;
            updateTableView();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Thực hiện tìm kiếm
    private void performSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            currentData = bookingData;
            updateTableView();
            return;
        }

        try {
            List<BookingDTO> results = bookingBUS.getAllBookings();
            List<BookingDTO> filtered = results.stream()
                    .filter(b -> String.valueOf(b.getBookingId()).contains(query) ||
                            b.getCode().toLowerCase().contains(query)) // Giả sử có trường getCode(), điều chỉnh nếu cần
                    .toList();

            if (filtered.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                currentData = bookingData;
                updateTableView();
            } else {
                currentData = filtered;
                updateTableViewWithData(filtered);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Hiển thị dialog lọc
    private void showFilterDialog() {
        JDialog filterDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Lọc đặt phòng", true);
        filterDialog.setLayout(new BorderLayout());
        filterDialog.setSize(350, 220);

        JPanel contentPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        contentPanel.setBackground(PANEL_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel statusLabel = new JLabel("Trạng thái:");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"All", "CHECKED_OUT", "CHECKED_IN", "CONFIRMED", "PENDING"});
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
            filterBookings(selectedStatus);
            filterDialog.dispose();
        });
        buttonPanel.add(confirmButton);

        filterDialog.add(contentPanel, BorderLayout.CENTER);
        filterDialog.add(buttonPanel, BorderLayout.SOUTH);
        filterDialog.setLocationRelativeTo(this);
        filterDialog.setVisible(true);
    }

    // Lọc đặt phòng theo trạng thái
    private void filterBookings(String status) {
        try {
            List<BookingDTO> results;
            if ("All".equals(status)) {
                results = bookingData;
            } else {
                results = bookingBUS.getBookingsByStatus(status);
            }
            currentData = results;
            updateTableViewWithData(results);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi lọc: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void checkOutAllSelected() {
        JTable table = (JTable) scrollPane.getViewport().getView();
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đặt phòng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BookingDTO booking = currentData.get(selectedRow);
        int bookingId = booking.getBookingId();

        if (bookingRoomBUS.checkOutAllRooms(bookingId, LocalDateTime.now())) {
            invoiceBUS.createInvoiceOnFullCheckout(bookingId, 1); // Assume createdBy = 1
            loadBookingData();
            JOptionPane.showMessageDialog(this, "Check-out tất cả phòng thành công và hóa đơn đã tạo!");
        } else {
            JOptionPane.showMessageDialog(this, "Check-out thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Cập nhật bảng hiển thị
    private void updateTableView() {
        if (bookingData == null) {
            bookingData = new java.util.ArrayList<>();
        }
        currentData = bookingData;
        updateTableViewWithData(bookingData);
    }

    // Cập nhật bảng với dữ liệu
    private void updateTableViewWithData(List<BookingDTO> data) {
        currentData = data;
        if (data == null || data.isEmpty()) {
            String[] columnNames = {"Mã đặt phòng", "Mã khách", "Ngày đặt", "Trạng thái", "Nguồn đặt", "Ghi chú"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);
            JTable table = new JTable(model);
            table.setRowHeight(40);
            table.setFont(new Font("Arial", Font.PLAIN, 13));
            table.setGridColor(BORDER_COLOR);
            table.setSelectionBackground(new Color(232, 240, 254));
            table.setSelectionForeground(TEXT_COLOR);
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
            table.getTableHeader().setBackground(PANEL_BG);
            table.getTableHeader().setForeground(TEXT_COLOR);
            table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR));
            table.getTableHeader().setPreferredSize(new Dimension(0, 45));
            scrollPane.setViewportView(table);
            return;
        }

        String[] columnNames = {"Mã đặt phòng", "Mã khách", "Ngày đặt", "Trạng thái", "Nguồn đặt", "Ghi chú"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Bỏ tính năng edit table
            }
        };

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (BookingDTO booking : data) {
            Object[] row = {
                    booking.getCode(),
                    booking.getCustomerId(),
                    sdf.format(java.sql.Timestamp.valueOf(booking.getBookingDate())),
                    booking.getStatus(),
                    booking.getSource(),
                    booking.getNote() != null ? booking.getNote() : "Không"
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
                    BookingDTO booking = data.get(rowIndex);

                    JPopupMenu contextMenu = new JPopupMenu();
                    JMenuItem editMenuItem = new JMenuItem("Sửa");
                    JMenuItem deleteMenuItem = new JMenuItem("Xóa");
                    JMenuItem bookingRoomMenuItem = new JMenuItem("Danh sách phòng đặt");

                    editMenuItem.addActionListener(e1 -> editBooking(booking));
                    deleteMenuItem.addActionListener(e1 -> deleteBooking(booking));
                    bookingRoomMenuItem.addActionListener(e1 -> openBookingRoomGUI(booking));

                    contextMenu.add(editMenuItem);
                    contextMenu.add(deleteMenuItem);
                    contextMenu.addSeparator();
                    contextMenu.add(bookingRoomMenuItem);

                    contextMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        scrollPane.setViewportView(table);
    }

    // Dialog sửa đặt phòng
    private void editBooking(BookingDTO booking) {
        JDialog editDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa đặt phòng", true);
        editDialog.setLayout(new BorderLayout());
        editDialog.setSize(450, 280);

        JPanel contentPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        contentPanel.setBackground(PANEL_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel codeLabel = new JLabel("Mã đặt phòng:");
        codeLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField codeField = new JTextField(booking.getCode());
        codeField.setEditable(false);
        codeField.setFont(new Font("Arial", Font.PLAIN, 13));
        contentPanel.add(codeLabel);
        contentPanel.add(codeField);

//        JLabel statusLabel = new JLabel("Trạng thái:");
//        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));
//        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"BOOKED", "CHECKED_IN", "CANCELED"});
//        statusCombo.setSelectedItem(booking.getStatus());
//        statusCombo.setFont(new Font("Arial", Font.PLAIN, 13));
//        contentPanel.add(statusLabel);
//        contentPanel.add(statusCombo);

        JLabel noteLabel = new JLabel("Ghi chú:");
        noteLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField noteField = new JTextField(booking.getNote() != null ? booking.getNote() : "");
        noteField.setFont(new Font("Arial", Font.PLAIN, 13));
        contentPanel.add(noteLabel);
        contentPanel.add(noteField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(PANEL_BG);
        JButton saveButton = new JButton("Lưu");
        saveButton.setPreferredSize(new Dimension(80, 35));
        saveButton.setBackground(PRIMARY_COLOR);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorderPainted(false);
        saveButton.setFont(new Font("Arial", Font.BOLD, 13));
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveButton.addActionListener(e -> {
            try {
//                booking.setStatus((String) statusCombo.getSelectedItem());
                booking.setNote(noteField.getText());
                if (bookingBUS.updateBooking(booking)) {
                    JOptionPane.showMessageDialog(editDialog, "Cập nhật thành công!");
                    editDialog.dispose();
                    loadBookingData();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(editDialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Hủy");
        cancelButton.setPreferredSize(new Dimension(80, 35));
        cancelButton.setBackground(new Color(200, 200, 200));
        cancelButton.setForeground(TEXT_COLOR);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setFont(new Font("Arial", Font.BOLD, 13));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> editDialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        editDialog.add(contentPanel, BorderLayout.CENTER);
        editDialog.add(buttonPanel, BorderLayout.SOUTH);
        editDialog.setLocationRelativeTo(this);
        editDialog.setVisible(true);
    }

    // Xóa đặt phòng
    private void deleteBooking(BookingDTO booking) {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa đặt phòng: " + booking.getCode() + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (bookingBUS.deleteBooking(booking.getBookingId())) {
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
                    loadBookingData();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi xóa: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Mở giao diện phòng đặt
    private void openBookingRoomGUI(BookingDTO booking) {
        try {
            BookingRoom bookingRoomFrame = new BookingRoom(booking.getBookingId());
            bookingRoomFrame.setTitle("Danh sách phòng đặt - Mã: " + booking.getCode());
            bookingRoomFrame.setLocationRelativeTo(this);
            bookingRoomFrame.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi mở giao diện: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
