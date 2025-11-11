package GUI.operation.maintenance;

import BUS.MaintenanceTicketBUS;
import BUS.UserAccountBUS;
import DTO.MaintenanceTicketDTO;
import DTO.UserAccountDTO;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class MaintenanceTicketDetail extends JDialog {
    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color PANEL_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(224, 224, 224);

    private MaintenanceTicketDTO ticket;
    private MaintenanceTicketBUS ticketBUS;
    private UserAccountBUS userAccountBUS;
    private boolean isEditMode;

    private JTextField txtRoomId;
    private JTextField txtTitle;
    private JTextArea txtDescription;
    private JComboBox<String> cboPriority;
    private JComboBox<String> cboStatus;
    private JComboBox<UserItem> cboAssignedTo;
    private JDateChooser openedAtChooser;
    private JDateChooser closedAtChooser;

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

    public MaintenanceTicketDetail(Frame parent, MaintenanceTicketDTO ticket, MaintenanceTicketBUS ticketBUS) {
        super(parent, ticket == null ? "Thêm phiếu bảo trì" : "Sửa phiếu bảo trì", true);
        this.ticket = ticket;
        this.ticketBUS = ticketBUS;
        this.userAccountBUS = new UserAccountBUS();
        this.isEditMode = (ticket != null);

        initComponents();
        if (isEditMode) {
            loadTicketData();
        }

        setSize(600, 700);
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Header Panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        headerPanel.setBackground(PANEL_BG);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        JLabel headerLabel = new JLabel(isEditMode ? "Sửa phiếu bảo trì" : "Thêm phiếu bảo trì mới");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerLabel.setForeground(PRIMARY_COLOR);
        headerLabel.setIcon(new ImageIcon(getClass().getResource("/images/maintenance.png")));
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

        // Title
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel lblTitle = new JLabel("Tiêu đề:");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 13));
        formPanel.add(lblTitle, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtTitle = new JTextField(20);
        txtTitle.setPreferredSize(new Dimension(200, 35));
        txtTitle.setFont(new Font("Arial", Font.PLAIN, 13));
        txtTitle.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        formPanel.add(txtTitle, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        JLabel lblDescription = new JLabel("Mô tả:");
        lblDescription.setFont(new Font("Arial", Font.BOLD, 13));
        formPanel.add(lblDescription, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.weighty = 0.3;
        gbc.fill = GridBagConstraints.BOTH;
        txtDescription = new JTextArea(4, 20);
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        txtDescription.setFont(new Font("Arial", Font.PLAIN, 13));
        txtDescription.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JScrollPane scrollDescription = new JScrollPane(txtDescription);
        scrollDescription.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        formPanel.add(scrollDescription, gbc);

        // Priority
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel lblPriority = new JLabel("Mức độ ưu tiên:");
        lblPriority.setFont(new Font("Arial", Font.BOLD, 13));
        formPanel.add(lblPriority, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        cboPriority = new JComboBox<>(new String[]{"Low", "Medium", "High", "Critical"});
        cboPriority.setPreferredSize(new Dimension(200, 35));
        cboPriority.setFont(new Font("Arial", Font.PLAIN, 13));
        formPanel.add(cboPriority, gbc);

        // Status
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.3;
        JLabel lblStatus = new JLabel("Trạng thái:");
        lblStatus.setFont(new Font("Arial", Font.BOLD, 13));
        formPanel.add(lblStatus, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        cboStatus = new JComboBox<>(new String[]{"Open", "In Progress", "Resolved", "Closed"});
        cboStatus.setPreferredSize(new Dimension(200, 35));
        cboStatus.setFont(new Font("Arial", Font.PLAIN, 13));
        formPanel.add(cboStatus, gbc);

        // Assigned To
        gbc.gridx = 0;
        gbc.gridy = 5;
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

        // Opened At - Replace with JDateChooser
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.3;
        JLabel lblOpenedAt = new JLabel("Ngày mở:");
        lblOpenedAt.setFont(new Font("Arial", Font.BOLD, 13));
        formPanel.add(lblOpenedAt, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        openedAtChooser = new JDateChooser();
        openedAtChooser.setDateFormatString("yyyy-MM-dd HH:mm:ss");
        openedAtChooser.setDate(new Date());
        openedAtChooser.setPreferredSize(new Dimension(200, 35));
        formPanel.add(openedAtChooser, gbc);

        // Closed At - Replace with JDateChooser and make editable
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 0.3;
        JLabel lblClosedAt = new JLabel("Ngày đóng:");
        lblClosedAt.setFont(new Font("Arial", Font.BOLD, 13));
        formPanel.add(lblClosedAt, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        closedAtChooser = new JDateChooser();
        closedAtChooser.setDateFormatString("yyyy-MM-dd HH:mm:ss");
        closedAtChooser.setPreferredSize(new Dimension(200, 35));
        formPanel.add(closedAtChooser, gbc);

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
                saveTicket();
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

    private void loadTicketData() {
        txtRoomId.setText(String.valueOf(ticket.getRoomId()));
        txtTitle.setText(ticket.getTitle());
        txtDescription.setText(ticket.getDescription());
        cboPriority.setSelectedItem(ticket.getPriority());
        cboStatus.setSelectedItem(ticket.getStatus());

        if (ticket.getAssignedTo() != null && ticket.getAssignedTo() > 0) {
            for (int i = 0; i < cboAssignedTo.getItemCount(); i++) {
                UserItem item = cboAssignedTo.getItemAt(i);
                if (item.getUserId() == ticket.getAssignedTo()) {
                    cboAssignedTo.setSelectedIndex(i);
                    break;
                }
            }
        }

        if (ticket.getOpenedAt() != null) {
            openedAtChooser.setDate(new Date(ticket.getOpenedAt().getTime()));
        }
        if (ticket.getClosedAt() != null) {
            closedAtChooser.setDate(new Date(ticket.getClosedAt().getTime()));
        }
    }

    private void saveTicket() {
        try {
            // Validate input
            if (txtRoomId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập mã phòng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (txtTitle.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tiêu đề!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validate opened date
            if (openedAtChooser.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày mở!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Create or update ticket
            if (ticket == null) {
                ticket = new MaintenanceTicketDTO();
            }

            ticket.setRoomId(Integer.parseInt(txtRoomId.getText().trim()));
            ticket.setTitle(txtTitle.getText().trim());
            ticket.setDescription(txtDescription.getText().trim());
            ticket.setPriority((String) cboPriority.getSelectedItem());
            ticket.setStatus((String) cboStatus.getSelectedItem());

            UserItem selectedUser = (UserItem) cboAssignedTo.getSelectedItem();
            ticket.setAssignedTo(selectedUser.getUserId() > 0 ? selectedUser.getUserId() : null);

            if (openedAtChooser.getDate() != null) {
                ticket.setOpenedAt(new Timestamp(openedAtChooser.getDate().getTime()));
            }

            // Set closed_at when status is Closed
            if ("Closed".equals(ticket.getStatus())) {
                if (closedAtChooser.getDate() != null) {
                    ticket.setClosedAt(new Timestamp(closedAtChooser.getDate().getTime()));
                } else {
                    ticket.setClosedAt(new Timestamp(System.currentTimeMillis()));
                }
            } else {
                ticket.setClosedAt(closedAtChooser.getDate() != null ?
                        new Timestamp(closedAtChooser.getDate().getTime()) : null);
            }

            boolean success;
            if (isEditMode) {
                success = ticketBUS.updateTicket(ticket);
            } else {
                success = ticketBUS.addTicket(ticket);
            }

            if (success) {
                JOptionPane.showMessageDialog(this,
                        isEditMode ? "Cập nhật phiếu bảo trì thành công!" : "Thêm phiếu bảo trì thành công!",
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        isEditMode ? "Cập nhật phiếu bảo trì thất bại!" : "Thêm phiếu bảo trì thất bại!",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Mã phòng phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
