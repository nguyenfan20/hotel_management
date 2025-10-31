package GUI.booking;

import BUS.BookingBUS;
import DAO.BookingDAO;
import DTO.BookingDTO;
import com.toedter.calendar.JDateChooser;
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

public class BookingGUI extends javax.swing.JPanel {
    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color PANEL_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(224, 224, 224);
    private static final Color TEXT_COLOR = new Color(33, 33, 33);
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);
    private static final Color DANGER_COLOR = new Color(239, 68, 68);
    private static final Color WARNING_COLOR = new Color(241, 196, 15);

    private BookingBUS bookingBUS;
    private javax.swing.JDialog jDialog1;
    private JScrollPane scrollPane;
    private JTextField searchField;
    private List<BookingDTO> bookingData;

    public BookingGUI() {
        bookingBUS = new BookingBUS(new BookingDAO());
        bookingData = new java.util.ArrayList<>();
        initComponents();
        loadBookingData();
    }

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

    private void loadBookingData() {
        try {
            bookingData = bookingBUS.getAllBookings();
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
            BookingDTO booking = bookingBUS.getBookingByCode(query);
            if (booking != null) {
                List<BookingDTO> results = List.of(booking);
                updateTableViewWithData(results);
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy đặt phòng: " + query,
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                updateTableView();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showFilterDialog() {
        JDialog filterDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Lọc đặt phòng", true);
        filterDialog.setLayout(new BorderLayout());
        filterDialog.setSize(350, 220);

        JPanel contentPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        contentPanel.setBackground(PANEL_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel statusLabel = new JLabel("Trạng thái:");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Tất cả", "Đã đặt", "Đã nhận phòng", "Đã trả phòng", "Đã hủy"});
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

    private void filterBookings(String status) {
        try {
            List<BookingDTO> results;
            if ("Tất cả".equals(status)) {
                results = bookingData;
            } else {
                results = bookingBUS.getBookingsByStatus(status);
            }
            updateTableViewWithData(results);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi lọc: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTableView() {
        if (bookingData == null) {
            bookingData = new java.util.ArrayList<>();
        }
        updateTableViewWithData(bookingData);
    }

    private void updateTableViewWithData(List<BookingDTO> data) {
        if (data == null || data.isEmpty()) {
            String[] columnNames = {"Mã đặt phòng", "Mã phòng", "Mã khách", "Ngày đặt", "Trạng thái", "Nguồn đặt", "Ghi chú"};
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

        String[] columnNames = {"Mã đặt phòng", "Mã phòng", "Mã khách", "Ngày đặt", "Trạng thái", "Nguồn đặt", "Ghi chú"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (BookingDTO booking : data) {
            Object[] row = {
                    booking.getCode(),
                    "P00" + booking.getBookingId(),
                    "KH00" + booking.getCustomerId(),
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

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Sửa");
        JMenuItem deleteItem = new JMenuItem("Xóa");
        JMenuItem bookingRoomItem = new JMenuItem("Danh sách phòng đặt");
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);
        popupMenu.addSeparator();
        popupMenu.add(bookingRoomItem);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger() && table.getSelectedRow() != -1) {
                    int rowIndex = table.getSelectedRow();
                    BookingDTO booking = data.get(rowIndex);

                    editItem.addActionListener(e1 -> editBooking(booking));
                    deleteItem.addActionListener(e1 -> deleteBooking(booking));
                    bookingRoomItem.addActionListener(e1 -> openBookingRoomGUI(booking));

                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        scrollPane.setViewportView(table);
    }

    private void editBooking(BookingDTO booking) {
        JOptionPane.showMessageDialog(this, "Sửa đặt phòng: " + booking.getCode(),
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

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

    private void openBookingRoomGUI(BookingDTO booking) {
        try {
            BookingRoom bookingRoomFrame = new BookingRoom();
            bookingRoomFrame.setTitle("Danh sách phòng đặt - Mã: " + booking.getCode());
            bookingRoomFrame.setLocationRelativeTo(this);
            bookingRoomFrame.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi mở giao diện: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
