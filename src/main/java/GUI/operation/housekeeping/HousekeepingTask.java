package GUI.operation.housekeeping;

import BUS.HousekeepingTaskBUS;
import DTO.HousekeepingTaskDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class HousekeepingTask extends JPanel {
    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color PANEL_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(224, 224, 224);
    private static final Color TEXT_COLOR = new Color(33, 33, 33);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);

    private JTable taskTable;
    private DefaultTableModel tableModel;
    private HousekeepingTaskBUS taskBUS;
    private JTextField searchField;
    private JComboBox<String> statusFilterCombo;
    private JTextField roomFilterField;

    public HousekeepingTask() {
        taskBUS = new HousekeepingTaskBUS();
        initComponents();
        loadTaskData();
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
                    searchTasks();
                }
            }
        });

        JButton searchButton = createIconButton("/images/search.png");
        searchButton.setToolTipText("Tìm kiếm");
        searchButton.addActionListener(e -> searchTasks());

        // Status filter
        statusFilterCombo = new JComboBox<>(new String[]{"Tất cả", "Pending", "In Progress", "Completed"});
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
        refreshButton.addActionListener(e -> loadTaskData());

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
        String[] columnNames = {"ID", "Phòng", "Ngày nhiệm vụ", "Loại nhiệm vụ", "Người được giao", "Trạng thái", "Ghi chú"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        taskTable = new JTable(tableModel);
        taskTable.setRowHeight(40);
        taskTable.setFont(new Font("Arial", Font.PLAIN, 13));
        taskTable.setGridColor(BORDER_COLOR);
        taskTable.setSelectionBackground(new Color(232, 240, 254));
        taskTable.setSelectionForeground(TEXT_COLOR);
        taskTable.setShowVerticalLines(true);
        taskTable.setIntercellSpacing(new Dimension(1, 1));
        taskTable.getTableHeader().setReorderingAllowed(false);
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        taskTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        taskTable.getTableHeader().setBackground(PANEL_BG);
        taskTable.getTableHeader().setForeground(TEXT_COLOR);
        taskTable.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR));
        taskTable.getTableHeader().setPreferredSize(new Dimension(0, 45));

        taskTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
        taskTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editTask();
                }
            }
        });

        // Right click menu
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Sửa");
        JMenuItem deleteItem = new JMenuItem("Xóa");

        editItem.addActionListener(e -> editTask());
        deleteItem.addActionListener(e -> deleteTask());

        popupMenu.add(editItem);
        popupMenu.add(deleteItem);

        taskTable.setComponentPopupMenu(popupMenu);

        JScrollPane scrollPane = new JScrollPane(taskTable);
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

        JButton addButton = new JButton("Thêm nhiệm vụ");
        addButton.setPreferredSize(new Dimension(150, 35));
        addButton.setIcon(new ImageIcon(getClass().getResource("/images/add-button.png")));
        addButton.setBackground(PRIMARY_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setFont(new Font("Arial", Font.BOLD, 13));
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e -> addTask());

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

    private void loadTaskData() {
        tableModel.setRowCount(0);
        List<HousekeepingTaskDTO> tasks = taskBUS.getAllTasks();

        for (HousekeepingTaskDTO task : tasks) {
            Object[] row = {
                    task.getTaskId(),
                    task.getRoomId(),
                    task.getTaskDate(),
                    task.getTaskType(),
                    task.getAssignedName() != null ? task.getAssignedName() : "Chưa giao",
                    task.getStatus(),
                    task.getNote()
            };
            tableModel.addRow(row);
        }
    }

    private void searchTasks() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadTaskData();
            return;
        }

        tableModel.setRowCount(0);
        List<HousekeepingTaskDTO> tasks = taskBUS.searchTasks(keyword);

        for (HousekeepingTaskDTO task : tasks) {
            Object[] row = {
                    task.getTaskId(),
                    task.getRoomId(),
                    task.getTaskDate(),
                    task.getTaskType(),
                    task.getAssignedName() != null ? task.getAssignedName() : "Chưa giao",
                    task.getStatus(),
                    task.getNote()
            };
            tableModel.addRow(row);
        }
    }

    private void filterByStatus() {
        String status = (String) statusFilterCombo.getSelectedItem();
        if (status.equals("Tất cả")) {
            loadTaskData();
            return;
        }

        tableModel.setRowCount(0);
        List<HousekeepingTaskDTO> tasks = taskBUS.filterTasksByStatus(status);

        for (HousekeepingTaskDTO task : tasks) {
            Object[] row = {
                    task.getTaskId(),
                    task.getRoomId(),
                    task.getTaskDate(),
                    task.getTaskType(),
                    task.getAssignedName() != null ? task.getAssignedName() : "Chưa giao",
                    task.getStatus(),
                    task.getNote()
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
            List<HousekeepingTaskDTO> tasks = taskBUS.filterTasksByRoom(roomId);

            for (HousekeepingTaskDTO task : tasks) {
                Object[] row = {
                        task.getTaskId(),
                        task.getRoomId(),
                        task.getTaskDate(),
                        task.getTaskType(),
                        task.getAssignedName() != null ? task.getAssignedName() : "Chưa giao",
                        task.getStatus(),
                        task.getNote()
                };
                tableModel.addRow(row);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Mã phòng không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addTask() {
        HousekeepingTaskDetail detailDialog = new HousekeepingTaskDetail(
                (Frame) SwingUtilities.getWindowAncestor(this),
                null,
                taskBUS
        );
        detailDialog.setVisible(true);
        loadTaskData();
    }

    private void editTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhiệm vụ cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int taskId = (int) tableModel.getValueAt(selectedRow, 0);
        HousekeepingTaskDTO task = taskBUS.getTaskById(taskId);

        if (task != null) {
            HousekeepingTaskDetail detailDialog = new HousekeepingTaskDetail(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    task,
                    taskBUS
            );
            detailDialog.setVisible(true);
            loadTaskData();
        }
    }

    private void deleteTask() {
        int selectedRow = taskTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhiệm vụ cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa nhiệm vụ này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int taskId = (int) tableModel.getValueAt(selectedRow, 0);
            if (taskBUS.deleteTask(taskId)) {
                JOptionPane.showMessageDialog(this, "Xóa nhiệm vụ thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadTaskData();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa nhiệm vụ thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}