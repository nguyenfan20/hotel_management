package GUI.operation.maintenance;

import BUS.MaintenanceTicketBUS;
import DTO.MaintenanceTicketDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;

public class MaintenanceTicket extends JPanel {
    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color PANEL_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(224, 224, 224);
    private static final Color TEXT_COLOR = new Color(33, 33, 33);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color WARNING_COLOR = new Color(241, 196, 15);

    private JTable ticketTable;
    private DefaultTableModel tableModel;
    private MaintenanceTicketBUS ticketBUS;
    private JTextField searchField;
    private JComboBox<String> statusFilterCombo;
    private JTextField roomFilterField;

    public MaintenanceTicket() {
        ticketBUS = new MaintenanceTicketBUS();
        initComponents();
        loadTicketData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Top Panel - Search and Filter
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.setBackground(PANEL_BG);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Search field
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
                    searchTickets();
                }
            }
        });

        JButton searchButton = createIconButton("/images/search.png");
        searchButton.setToolTipText("Tìm kiếm");
        searchButton.addActionListener(e -> searchTickets());

        // Status filter
        statusFilterCombo = new JComboBox<>(new String[]{"Tất cả", "Open", "In Progress", "Resolved", "Closed"});
        statusFilterCombo.setPreferredSize(new Dimension(130, 35));
        statusFilterCombo.setFont(new Font("Arial", Font.PLAIN, 13));
        statusFilterCombo.addActionListener(e -> filterByStatus());

        // Room filter
        roomFilterField = new JTextField(8);
        roomFilterField.setPreferredSize(new Dimension(100, 35));
        roomFilterField.setFont(new Font("Arial", Font.PLAIN, 13));
        roomFilterField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JButton roomFilterButton = createIconButton("/images/filter.png");
        roomFilterButton.setToolTipText("Lọc theo phòng");
        roomFilterButton.addActionListener(e -> filterByRoom());

        JButton refreshButton = createIconButton("/images/refresh.png");
        refreshButton.setToolTipText("Làm mới");
        refreshButton.addActionListener(e -> loadTicketData());

        topPanel.add(new JLabel("Tìm kiếm:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(Box.createHorizontalStrut(10));
        topPanel.add(new JLabel("Trạng thái:"));
        topPanel.add(statusFilterCombo);
        topPanel.add(Box.createHorizontalStrut(10));
        topPanel.add(new JLabel("Phòng:"));
        topPanel.add(roomFilterField);
        topPanel.add(roomFilterButton);
        topPanel.add(refreshButton);

        add(topPanel, BorderLayout.NORTH);

        // Center Panel - Table
        String[] columnNames = {"ID", "Phòng", "Tiêu đề", "Mô tả", "Ưu tiên", "Ngày mở", "Ngày đóng", "Trạng thái", "Người được giao"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ticketTable = new JTable(tableModel);
        ticketTable.setRowHeight(40);
        ticketTable.setFont(new Font("Arial", Font.PLAIN, 13));
        ticketTable.setGridColor(BORDER_COLOR);
        ticketTable.setSelectionBackground(new Color(232, 240, 254));
        ticketTable.setSelectionForeground(TEXT_COLOR);
        ticketTable.setShowVerticalLines(true);
        ticketTable.setIntercellSpacing(new Dimension(1, 1));
        ticketTable.getTableHeader().setReorderingAllowed(false);
        ticketTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        ticketTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        ticketTable.getTableHeader().setBackground(PANEL_BG);
        ticketTable.getTableHeader().setForeground(TEXT_COLOR);
        ticketTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR));
        ticketTable.getTableHeader().setPreferredSize(new Dimension(0, 45));

        ticketTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

        // Double click to edit
        ticketTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editTicket();
                }
            }
        });

        // Right click menu
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Sửa");
        JMenuItem deleteItem = new JMenuItem("Xóa");

        editItem.addActionListener(e -> editTicket());
        deleteItem.addActionListener(e -> deleteTicket());

        popupMenu.add(editItem);
        popupMenu.add(deleteItem);

        ticketTable.setComponentPopupMenu(popupMenu);

        JScrollPane scrollPane = new JScrollPane(ticketTable);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel - Action Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setBackground(PANEL_BG);
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JButton addButton = new JButton("Thêm phiếu bảo trì");
        addButton.setPreferredSize(new Dimension(170, 35));
        addButton.setIcon(new ImageIcon(getClass().getResource("/images/add-button.png")));
        addButton.setBackground(PRIMARY_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setFont(new Font("Arial", Font.BOLD, 13));
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e -> addTicket());

        bottomPanel.add(addButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JButton createIconButton(String iconPath) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(35, 35));
        button.setIcon(new ImageIcon(getClass().getResource(iconPath)));
        button.setBackground(PANEL_BG);
        button.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void loadTicketData() {
        tableModel.setRowCount(0);
        List<MaintenanceTicketDTO> tickets = ticketBUS.getAllTickets();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (MaintenanceTicketDTO ticket : tickets) {
            Object[] row = {
                    ticket.getTicketId(),
                    ticket.getRoomId(),
                    ticket.getTitle(),
                    ticket.getDescription(),
                    ticket.getPriority(),
                    ticket.getOpenedAt() != null ? dateFormat.format(ticket.getOpenedAt()) : "",
                    ticket.getClosedAt() != null ? dateFormat.format(ticket.getClosedAt()) : "",
                    ticket.getStatus(),
                    ticket.getAssignedName() != null ? ticket.getAssignedName() : "Chưa giao"
            };
            tableModel.addRow(row);
        }
    }

    private void searchTickets() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadTicketData();
            return;
        }

        tableModel.setRowCount(0);
        List<MaintenanceTicketDTO> tickets = ticketBUS.searchTickets(keyword);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (MaintenanceTicketDTO ticket : tickets) {
            Object[] row = {
                    ticket.getTicketId(),
                    ticket.getRoomId(),
                    ticket.getTitle(),
                    ticket.getDescription(),
                    ticket.getPriority(),
                    ticket.getOpenedAt() != null ? dateFormat.format(ticket.getOpenedAt()) : "",
                    ticket.getClosedAt() != null ? dateFormat.format(ticket.getClosedAt()) : "",
                    ticket.getStatus(),
                    ticket.getAssignedName() != null ? ticket.getAssignedName() : "Chưa giao"
            };
            tableModel.addRow(row);
        }
    }

    private void filterByStatus() {
        String status = (String) statusFilterCombo.getSelectedItem();
        if (status.equals("Tất cả")) {
            loadTicketData();
            return;
        }

        tableModel.setRowCount(0);
        List<MaintenanceTicketDTO> tickets = ticketBUS.filterTicketsByStatus(status);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (MaintenanceTicketDTO ticket : tickets) {
            Object[] row = {
                    ticket.getTicketId(),
                    ticket.getRoomId(),
                    ticket.getTitle(),
                    ticket.getDescription(),
                    ticket.getPriority(),
                    ticket.getOpenedAt() != null ? dateFormat.format(ticket.getOpenedAt()) : "",
                    ticket.getClosedAt() != null ? dateFormat.format(ticket.getClosedAt()) : "",
                    ticket.getStatus(),
                    ticket.getAssignedName() != null ? ticket.getAssignedName() : "Chưa giao"
            };
            tableModel.addRow(row);
        }
    }

    private void filterByRoom() {
        String roomIdStr = roomFilterField.getText().trim();
        if (roomIdStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã phòng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int roomId = Integer.parseInt(roomIdStr);
            tableModel.setRowCount(0);
            List<MaintenanceTicketDTO> tickets = ticketBUS.filterTicketsByRoom(roomId);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            for (MaintenanceTicketDTO ticket : tickets) {
                Object[] row = {
                        ticket.getTicketId(),
                        ticket.getRoomId(),
                        ticket.getTitle(),
                        ticket.getDescription(),
                        ticket.getPriority(),
                        ticket.getOpenedAt() != null ? dateFormat.format(ticket.getOpenedAt()) : "",
                        ticket.getClosedAt() != null ? dateFormat.format(ticket.getClosedAt()) : "",
                        ticket.getStatus(),
                        ticket.getAssignedName() != null ? ticket.getAssignedName() : "Chưa giao"
                };
                tableModel.addRow(row);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Mã phòng không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addTicket() {
        MaintenanceTicketDetail detailDialog = new MaintenanceTicketDetail(
                (Frame) SwingUtilities.getWindowAncestor(this),
                null,
                ticketBUS
        );
        detailDialog.setVisible(true);
        loadTicketData();
    }

    private void editTicket() {
        int selectedRow = ticketTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu bảo trì cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int ticketId = (int) tableModel.getValueAt(selectedRow, 0);
        MaintenanceTicketDTO ticket = ticketBUS.getTicketById(ticketId);

        if (ticket != null) {
            MaintenanceTicketDetail detailDialog = new MaintenanceTicketDetail(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    ticket,
                    ticketBUS
            );
            detailDialog.setVisible(true);
            loadTicketData();
        }
    }

    private void deleteTicket() {
        int selectedRow = ticketTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phiếu bảo trì cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa phiếu bảo trì này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int ticketId = (int) tableModel.getValueAt(selectedRow, 0);
            if (ticketBUS.deleteTicket(ticketId)) {
                JOptionPane.showMessageDialog(this, "Xóa phiếu bảo trì thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadTicketData();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa phiếu bảo trì thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}