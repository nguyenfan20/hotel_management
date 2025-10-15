package GUI.operation.housekeeping;

import BUS.HousekeepingTaskBUS;
import BUS.UserAccountBUS;
import DTO.HousekeepingTaskDTO;
import DTO.UserAccountDTO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HousekeepingTaskDetail extends JDialog {
    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color PANEL_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(224, 224, 224);

    private HousekeepingTaskDTO task;
    private HousekeepingTaskBUS taskBUS;
    private UserAccountBUS userAccountBUS;
    private boolean isEditMode;

    private JTextField txtRoomId;
    private JTextField txtTaskDate;
    private JTextField txtTaskType;
    private JComboBox<UserItem> cboAssignedTo;
    private JComboBox<String> cboStatus;
    private JTextArea txtNote;

    private JButton btnSave;
    private JButton btnCancel;

    private static class UserItem {
        private int userId;
        private String fullName;

        public UserItem(int userId, String fullName) {
            this.userId = userId;
            this.fullName = fullName;
        }

        public int getUserId() {
            return userId;
        }

        @Override
        public String toString() {
            return fullName;
        }
    }

    public HousekeepingTaskDetail(Frame parent, HousekeepingTaskDTO task, HousekeepingTaskBUS taskBUS) {
        super(parent, task == null ? "Thêm nhiệm vụ dọn dẹp" : "Sửa nhiệm vụ dọn dẹp", true);
        this.task = task;
        this.taskBUS = taskBUS;
        this.userAccountBUS = new UserAccountBUS();
        this.isEditMode = (task != null);

        initComponents();
        if (isEditMode) {
            loadTaskData();
        }

        setSize(550, 600);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Header Panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        headerPanel.setBackground(PANEL_BG);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        JLabel headerLabel = new JLabel(isEditMode ? "Sửa nhiệm vụ dọn dẹp" : "Thêm nhiệm vụ dọn dẹp mới");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(PRIMARY_COLOR);
        headerLabel.setIcon(new ImageIcon(getClass().getResource("/images/cleaning.png")));
        headerPanel.add(headerLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(PANEL_BG);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Room ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel lblRoomId = new JLabel("Mã phòng:");
        lblRoomId.setFont(new Font("Arial", Font.BOLD, 13));
        formPanel.add(lblRoomId, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtRoomId = new JTextField(20);
        txtRoomId.setPreferredSize(new Dimension(200, 35));
        txtRoomId.setFont(new Font("Arial", Font.PLAIN, 13));
        txtRoomId.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        formPanel.add(txtRoomId, gbc);

        // Task Date
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel lblTaskDate = new JLabel("Ngày nhiệm vụ:");
        lblTaskDate.setFont(new Font("Arial", Font.BOLD, 13));
        formPanel.add(lblTaskDate, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtTaskDate = new JTextField(20);
        txtTaskDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        txtTaskDate.setPreferredSize(new Dimension(200, 35));
        txtTaskDate.setFont(new Font("Arial", Font.PLAIN, 13));
        txtTaskDate.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        formPanel.add(txtTaskDate, gbc);

        // Task Type
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        JLabel lblTaskType = new JLabel("Loại nhiệm vụ:");
        lblTaskType.setFont(new Font("Arial", Font.BOLD, 13));
        formPanel.add(lblTaskType, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtTaskType = new JTextField(20);
        txtTaskType.setPreferredSize(new Dimension(200, 35));
        txtTaskType.setFont(new Font("Arial", Font.PLAIN, 13));
        txtTaskType.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        formPanel.add(txtTaskType, gbc);

        // Assigned To
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        JLabel lblAssignedTo = new JLabel("Người được giao:");
        lblAssignedTo.setFont(new Font("Arial", Font.BOLD, 13));
        formPanel.add(lblAssignedTo, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        cboAssignedTo = new JComboBox<>();
        cboAssignedTo.addItem(new UserItem(0, "-- Chưa giao --"));
        loadHousekeepingStaff();
        cboAssignedTo.setPreferredSize(new Dimension(200, 35));
        cboAssignedTo.setFont(new Font("Arial", Font.PLAIN, 13));
        formPanel.add(cboAssignedTo, gbc);

        // Status
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.3;
        JLabel lblStatus = new JLabel("Trạng thái:");
        lblStatus.setFont(new Font("Arial", Font.BOLD, 13));
        formPanel.add(lblStatus, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        cboStatus = new JComboBox<>(new String[]{"Pending", "In Progress", "Completed"});
        cboStatus.setPreferredSize(new Dimension(200, 35));
        cboStatus.setFont(new Font("Arial", Font.PLAIN, 13));
        formPanel.add(cboStatus, gbc);

        // Note
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel lblNote = new JLabel("Ghi chú:");
        lblNote.setFont(new Font("Arial", Font.BOLD, 13));
        formPanel.add(lblNote, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        txtNote = new JTextArea(5, 20);
        txtNote.setLineWrap(true);
        txtNote.setWrapStyleWord(true);
        txtNote.setFont(new Font("Arial", Font.PLAIN, 13));
        txtNote.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JScrollPane scrollNote = new JScrollPane(txtNote);
        scrollNote.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        formPanel.add(scrollNote, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        buttonPanel.setBackground(PANEL_BG);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));

        btnSave = new JButton("Lưu");
        btnSave.setPreferredSize(new Dimension(100, 35));
        btnSave.setBackground(SUCCESS_COLOR);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setBorderPainted(false);
        btnSave.setFont(new Font("Arial", Font.BOLD, 13));
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveTask();
            }
        });

        btnCancel = new JButton("Hủy");
        btnCancel.setPreferredSize(new Dimension(100, 35));
        btnCancel.setBackground(DANGER_COLOR);
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnCancel.setBorderPainted(false);
        btnCancel.setFont(new Font("Arial", Font.BOLD, 13));
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadHousekeepingStaff() {
        try {
            List<UserAccountDTO> users = userAccountBUS.getUsersByRoleId(3);
            for (UserAccountDTO user : users) {
                cboAssignedTo.addItem(new UserItem(user.getUserId(), user.getFullName()));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách nhân viên: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTaskData() {
        txtRoomId.setText(String.valueOf(task.getRoomId()));
        txtTaskDate.setText(task.getTaskDate().toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        txtTaskType.setText(task.getTaskType());

        if (task.getAssignedTo() != null && task.getAssignedTo() > 0) {
            for (int i = 0; i < cboAssignedTo.getItemCount(); i++) {
                UserItem item = cboAssignedTo.getItemAt(i);
                if (item.getUserId() == task.getAssignedTo()) {
                    cboAssignedTo.setSelectedIndex(i);
                    break;
                }
            }
        }

        cboStatus.setSelectedItem(task.getStatus());
        txtNote.setText(task.getNote());
    }

    private void saveTask() {
        try {
            // Validate input
            if (txtRoomId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập mã phòng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (txtTaskDate.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày nhiệm vụ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (txtTaskType.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập loại nhiệm vụ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Create or update task
            if (task == null) {
                task = new HousekeepingTaskDTO();
            }

            task.setRoomId(Integer.parseInt(txtRoomId.getText().trim()));
            LocalDate taskDate = LocalDate.parse(txtTaskDate.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            task.setTaskDate(java.sql.Date.valueOf(taskDate));
            task.setTaskType(txtTaskType.getText().trim());

            UserItem selectedUser = (UserItem) cboAssignedTo.getSelectedItem();
            task.setAssignedTo(selectedUser.getUserId() > 0 ? selectedUser.getUserId() : null);

            task.setStatus((String) cboStatus.getSelectedItem());
            task.setNote(txtNote.getText().trim());

            boolean success;
            if (isEditMode) {
                success = taskBUS.updateTask(task);
            } else {
                success = taskBUS.addTask(task);
            }

            if (success) {
                JOptionPane.showMessageDialog(this,
                        isEditMode ? "Cập nhật nhiệm vụ thành công!" : "Thêm nhiệm vụ thành công!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        isEditMode ? "Cập nhật nhiệm vụ thất bại!" : "Thêm nhiệm vụ thất bại!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Mã phòng phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}