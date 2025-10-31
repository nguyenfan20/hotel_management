package GUI.booking;

import BUS.GuestBUS;
import DAO.GuestDAO;
import DTO.GuestDTO;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class Guest extends javax.swing.JFrame {
    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color PANEL_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(224, 224, 224);
    private static final Color TEXT_COLOR = new Color(33, 33, 33);
    private static final Color DANGER_COLOR = new Color(239, 68, 68);

    private GuestBUS guestBUS;
    private JScrollPane scrollPane;
    private JTextField searchField;
    private List<GuestDTO> guestData;

    public Guest() {
        guestBUS = new GuestBUS(new GuestDAO());
        initComponents();
        loadGuestData();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Quản lý khách hàng");
        setSize(900, 600);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);

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

    private void loadGuestData() {
        try {
            guestData = guestBUS.getAllGuests();
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
            List<GuestDTO> results = guestBUS.searchGuests(query);
            if (results == null || results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy khách: " + query,
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                updateTableView();
            } else {
                updateTableViewWithData(results);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTableView() {
        updateTableViewWithData(guestData);
    }

    private void updateTableViewWithData(List<GuestDTO> data) {
        String[] columnNames = {"Mã khách", "Mã phòng đặt", "Họ tên", "Giới tính", "Ngày sinh", "Số CMND", "Quốc tịch"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (GuestDTO guest : data) {
            Object[] row = {
                    guest.getGuestId(),
                    guest.getBookingRoomId(),
                    guest.getFullName(),
                    guest.getGender(),
                    guest.getDob(),
                    guest.getIdCard(),
                    guest.getNationality()
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
        JMenuItem deleteItem = new JMenuItem("Xóa");
        popupMenu.add(deleteItem);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger() && table.getSelectedRow() != -1) {
                    int rowIndex = table.getSelectedRow();
                    GuestDTO guest = data.get(rowIndex);

                    deleteItem.addActionListener(e1 -> deleteGuest(guest));

                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        scrollPane.setViewportView(table);
    }

    private void deleteGuest(GuestDTO guest) {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa khách: " + guest.getFullName() + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (guestBUS.deleteGuest(guest.getGuestId())) {
                    JOptionPane.showMessageDialog(this, "Xóa thành công!");
                    loadGuestData();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi xóa: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
