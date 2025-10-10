package GUI.booking;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

public class BookingRoom extends javax.swing.JFrame {

    // Color scheme
    private static final Color PRIMARY_COLOR = new Color(37, 99, 235);      // Blue
    private static final Color PRIMARY_DARK = new Color(29, 78, 216);       // Darker blue
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);      // Green
    private static final Color WARNING_COLOR = new Color(234, 179, 8);      // Yellow
    private static final Color DANGER_COLOR = new Color(239, 68, 68);       // Red
    private static final Color BACKGROUND_COLOR = new Color(249, 250, 251); // Light gray
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(17, 24, 39);        // Dark gray
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);   // Medium gray
    private static final Color BORDER_COLOR = new Color(229, 231, 235);     // Light border

    public BookingRoom() {
        initComponents();
        customizeComponents();
        initPopupMenu();
        initFilterDialog();
    }

    private void customizeComponents() {
        // Set window background
        getContentPane().setBackground(BACKGROUND_COLOR);
        jPanel1.setBackground(CARD_COLOR);
        jPanel1.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Customize search text field
        tfTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tfTimKiem.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        tfTimKiem.setBackground(Color.WHITE);

        // Customize combo box
        cbTieuChi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbTieuChi.setBackground(Color.WHITE);
        cbTieuChi.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        // Customize buttons
        customizeButton(btnTimKiem, PRIMARY_COLOR);
        customizeButton(btnLoc, PRIMARY_COLOR);

        // Customize table
        customizeTable();
    }

    private void customizeButton(JButton button, Color bgColor) {
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_DARK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }

    private void customizeTable() {
        // Table header
        JTableHeader header = tbPhongDat.getTableHeader();
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        // Table body
        tbPhongDat.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbPhongDat.setRowHeight(35);
        tbPhongDat.setShowGrid(true);
        tbPhongDat.setGridColor(BORDER_COLOR);
        tbPhongDat.setSelectionBackground(new Color(219, 234, 254));
        tbPhongDat.setSelectionForeground(TEXT_PRIMARY);
        tbPhongDat.setIntercellSpacing(new Dimension(1, 1));

        // Center align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tbPhongDat.getColumnCount(); i++) {
            tbPhongDat.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Custom renderer for status column with colors
        tbPhongDat.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);

                if (!isSelected && value != null) {
                    String status = value.toString();
                    switch (status) {
                        case "Đã đặt":
                            setForeground(WARNING_COLOR);
                            setFont(new Font("Segoe UI", Font.BOLD, 13));
                            break;
                        case "Đã nhận":
                            setForeground(PRIMARY_COLOR);
                            setFont(new Font("Segoe UI", Font.BOLD, 13));
                            break;
                        case "Đã trả":
                            setForeground(SUCCESS_COLOR);
                            setFont(new Font("Segoe UI", Font.BOLD, 13));
                            break;
                        case "Đã hủy":
                            setForeground(DANGER_COLOR);
                            setFont(new Font("Segoe UI", Font.BOLD, 13));
                            break;
                        default:
                            setForeground(TEXT_PRIMARY);
                            setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    }
                }
                return c;
            }
        });

        // Alternating row colors
        tbPhongDat.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(249, 250, 251));
                    }
                }

                setHorizontalAlignment(JLabel.CENTER);

                // Apply status colors for status column
                if (column == 6 && value != null) {
                    String status = value.toString();
                    switch (status) {
                        case "Đã đặt":
                            setForeground(WARNING_COLOR);
                            setFont(new Font("Segoe UI", Font.BOLD, 13));
                            break;
                        case "Đã nhận":
                            setForeground(PRIMARY_COLOR);
                            setFont(new Font("Segoe UI", Font.BOLD, 13));
                            break;
                        case "Đã trả":
                            setForeground(SUCCESS_COLOR);
                            setFont(new Font("Segoe UI", Font.BOLD, 13));
                            break;
                        case "Đã hủy":
                            setForeground(DANGER_COLOR);
                            setFont(new Font("Segoe UI", Font.BOLD, 13));
                            break;
                        default:
                            setForeground(TEXT_PRIMARY);
                            setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    }
                } else {
                    setForeground(TEXT_PRIMARY);
                    setFont(new Font("Segoe UI", Font.PLAIN, 13));
                }

                return c;
            }
        });

        // Scroll pane styling
        jScrollPane1.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        jScrollPane1.getViewport().setBackground(Color.WHITE);
    }

    private void initPopupMenu() {
        // Style popup menu
        jPopupMenu1.setBackground(CARD_COLOR);
        jPopupMenu1.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(4, 0, 4, 0)
        ));

        // Create menu items
        JMenuItem menuSua = new JMenuItem("Sửa");
        JMenuItem menuXoa = new JMenuItem("Xóa");

        // Style menu items
        styleMenuItem(menuSua, PRIMARY_COLOR);
        styleMenuItem(menuXoa, DANGER_COLOR);

        // Add to popup menu
        jPopupMenu1.add(menuSua);
        jPopupMenu1.add(menuXoa);

        // Event handlers
        menuSua.addActionListener(e -> suaDongDangChon());
        menuXoa.addActionListener(e -> xoaDongDangChon());

        // Attach MouseListener to table
        tbPhongDat.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                showPopup(e);
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                showPopup(e);
            }

            private void showPopup(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = tbPhongDat.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < tbPhongDat.getRowCount()) {
                        tbPhongDat.setRowSelectionInterval(row, row);
                    } else {
                        tbPhongDat.clearSelection();
                    }
                    jPopupMenu1.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    private void styleMenuItem(JMenuItem item, Color hoverColor) {
        item.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        item.setBackground(CARD_COLOR);
        item.setForeground(TEXT_PRIMARY);
        item.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        item.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                item.setBackground(new Color(243, 244, 246));
                item.setForeground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                item.setBackground(CARD_COLOR);
                item.setForeground(TEXT_PRIMARY);
            }
        });
    }

    private void initFilterDialog() {
        jDialog1.setTitle("Lọc phòng đặt");
        jDialog1.setModal(true);
        jDialog1.setSize(450, 300);
        jDialog1.setLocationRelativeTo(this);
        jDialog1.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        jDialog1.getContentPane().setBackground(BACKGROUND_COLOR);

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(CARD_COLOR);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(24, 24, 24, 24)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Check-in date label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel lblNgayNhan = new JLabel("Ngày nhận:");
        lblNgayNhan.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNgayNhan.setForeground(TEXT_PRIMARY);
        mainPanel.add(lblNgayNhan, gbc);

        // Check-in date picker
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0.7;
        com.toedter.calendar.JDateChooser dateNgayNhan = new com.toedter.calendar.JDateChooser();
        dateNgayNhan.setDateFormatString("dd/MM/yyyy");
        dateNgayNhan.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateNgayNhan.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        mainPanel.add(dateNgayNhan, gbc);

        // Check-out date label
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        JLabel lblNgayTra = new JLabel("Ngày trả:");
        lblNgayTra.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblNgayTra.setForeground(TEXT_PRIMARY);
        mainPanel.add(lblNgayTra, gbc);

        // Check-out date picker
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0.7;
        com.toedter.calendar.JDateChooser dateNgayTra = new com.toedter.calendar.JDateChooser();
        dateNgayTra.setDateFormatString("dd/MM/yyyy");
        dateNgayTra.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateNgayTra.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        mainPanel.add(dateNgayTra, gbc);

        // Status label
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        JLabel lblTrangThai = new JLabel("Trạng thái:");
        lblTrangThai.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTrangThai.setForeground(TEXT_PRIMARY);
        mainPanel.add(lblTrangThai, gbc);

        // Status combobox
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0.7;
        JComboBox<String> cbTrangThai = new JComboBox<>(
                new String[]{"Tất cả", "Đã đặt", "Đã nhận", "Đã trả", "Đã hủy"}
        );
        cbTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbTrangThai.setBackground(Color.WHITE);
        cbTrangThai.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        mainPanel.add(cbTrangThai, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 12, 16));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));

        JButton btnApDung = new JButton("Áp dụng");
        JButton btnHuy = new JButton("Hủy");

        // Style dialog buttons
        btnApDung.setBackground(PRIMARY_COLOR);
        btnApDung.setForeground(Color.WHITE);
        btnApDung.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnApDung.setFocusPainted(false);
        btnApDung.setBorderPainted(false);
        btnApDung.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnApDung.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        btnHuy.setBackground(new Color(229, 231, 235));
        btnHuy.setForeground(TEXT_PRIMARY);
        btnHuy.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnHuy.setFocusPainted(false);
        btnHuy.setBorderPainted(false);
        btnHuy.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHuy.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Hover effects
        btnApDung.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnApDung.setBackground(PRIMARY_DARK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnApDung.setBackground(PRIMARY_COLOR);
            }
        });

        btnHuy.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnHuy.setBackground(new Color(209, 213, 219));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnHuy.setBackground(new Color(229, 231, 235));
            }
        });

        buttonPanel.add(btnHuy);
        buttonPanel.add(btnApDung);

        // Add panels to dialog
        jDialog1.setLayout(new BorderLayout(0, 0));
        jDialog1.add(mainPanel, BorderLayout.CENTER);
        jDialog1.add(buttonPanel, BorderLayout.SOUTH);

        // Apply button handler
        btnApDung.addActionListener(e -> {
            String ngayNhan = "";
            String ngayTra = "";

            if (dateNgayNhan.getDate() != null) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                ngayNhan = sdf.format(dateNgayNhan.getDate());
            }

            if (dateNgayTra.getDate() != null) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                ngayTra = sdf.format(dateNgayTra.getDate());
            }

            String trangThai = (String) cbTrangThai.getSelectedItem();

            applyFilter(ngayNhan, ngayTra, trangThai);
            jDialog1.setVisible(false);
        });

        // Cancel button handler
        btnHuy.addActionListener(e -> {
            jDialog1.setVisible(false);
        });
    }

    private void applyFilter(String ngayNhan, String ngayTra, String trangThai) {
        // Implement your filter logic here
        System.out.println("Filtering with:");
        System.out.println("Ngày nhận: " + ngayNhan);
        System.out.println("Ngày trả: " + ngayTra);
        System.out.println("Trạng thái: " + trangThai);
    }

    private void suaDongDangChon() {
        int row = tbPhongDat.getSelectedRow();
        if (row != -1) {
            Object maPhong = tbPhongDat.getValueAt(row, 0);
            JOptionPane.showMessageDialog(this,
                    "Sửa dòng có Mã phòng đặt: " + maPhong,
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void xoaDongDangChon() {
        int row = tbPhongDat.getSelectedRow();
        if (row != -1) {
            Object maPhong = tbPhongDat.getValueAt(row, 0);

            // Custom option pane with styled buttons
            Object[] options = {"Xóa", "Hủy"};
            int confirm = JOptionPane.showOptionDialog(this,
                    "Bạn có chắc muốn xóa Mã phòng đặt: " + maPhong + "?",
                    "Xác nhận xóa",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    options,
                    options[1]);

            if (confirm == JOptionPane.YES_OPTION) {
                ((DefaultTableModel) tbPhongDat.getModel()).removeRow(row);
                JOptionPane.showMessageDialog(this,
                        "Đã xóa thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jPopupMenu1 = new JPopupMenu();
        jDialog1 = new JDialog();
        jPanel1 = new JPanel();
        tfTimKiem = new JTextField();
        btnTimKiem = new JButton();
        btnLoc = new JButton();
        jScrollPane1 = new JScrollPane();
        tbPhongDat = new JTable();
        cbTieuChi = new JComboBox<>();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Quản lý phòng đặt");

        tfTimKiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfTimKiemActionPerformed(evt);
            }
        });

        btnTimKiem.setIcon(new ImageIcon(getClass().getResource("/images/search.png")));
        btnTimKiem.setToolTipText("Tìm kiếm");

        btnLoc.setIcon(new ImageIcon(getClass().getResource("/images/filter.png")));
        btnLoc.setToolTipText("Lọc dữ liệu");
        btnLoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLocActionPerformed(evt);
            }
        });

        tbPhongDat.setModel(new DefaultTableModel(
                new Object [][] {
                        {"PD001", "DP001", "P101", "01/01/2024", "03/01/2024", "10%", "Đã đặt"},
                        {"PD002", "DP002", "P102", "05/01/2024", "07/01/2024", "10%", "Đã nhận"},
                        {"PD003", "DP003", "P103", "10/01/2024", "12/01/2024", "10%", "Đã trả"}
                },
                new String [] {
                        "Mã phòng đặt", "Mã đặt phòng", "Mã phòng", "Ngày nhận", "Ngày trả", "Thuế", "Trạng thái"
                }
        ) {
            Class[] types = new Class [] {
                    String.class, String.class, String.class, String.class, String.class, String.class, String.class
            };
            boolean[] canEdit = new boolean [] {
                    false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tbPhongDat);
        if (tbPhongDat.getColumnModel().getColumnCount() > 0) {
            tbPhongDat.getColumnModel().getColumn(0).setResizable(false);
            tbPhongDat.getColumnModel().getColumn(1).setResizable(false);
            tbPhongDat.getColumnModel().getColumn(2).setResizable(false);
            tbPhongDat.getColumnModel().getColumn(3).setResizable(false);
            tbPhongDat.getColumnModel().getColumn(4).setResizable(false);
            tbPhongDat.getColumnModel().getColumn(5).setResizable(false);
            tbPhongDat.getColumnModel().getColumn(6).setResizable(false);
        }

        cbTieuChi.setModel(new DefaultComboBoxModel<>(new String[] { "Mã đặt phòng", "Mã phòng", "Mã phòng đặt" }));
        cbTieuChi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbTieuChiActionPerformed(evt);
            }
        });

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(tfTimKiem, GroupLayout.PREFERRED_SIZE, 284, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnTimKiem, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnLoc, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(cbTieuChi, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 638, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addComponent(tfTimKiem, GroupLayout.Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(btnTimKiem, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
                                                .addComponent(btnLoc, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(cbTieuChi, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 271, GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(36, Short.MAX_VALUE))
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 650, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGap(0, 353, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE)))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>

    private void tfTimKiemActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO: Implement search functionality
    }

    private void cbTieuChiActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO: Handle criteria selection change
    }

    private void btnLocActionPerformed(java.awt.event.ActionEvent evt) {
        jDialog1.setVisible(true);
    }

    public static void main(String args[]) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BookingRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> {
            new BookingRoom().setVisible(true);
        });
    }

    // Variables declaration
    private JButton btnTimKiem;
    private JButton btnLoc;
    private JComboBox<String> cbTieuChi;
    private JPanel jPanel1;
    private JPopupMenu jPopupMenu1;
    private JDialog jDialog1;
    private JScrollPane jScrollPane1;
    private JTable tbPhongDat;
    private JTextField tfTimKiem;
    // End of variables declaration
}