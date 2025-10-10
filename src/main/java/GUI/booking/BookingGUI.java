package GUI.booking;

import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Optimized BookingGUI with modern colors and styling
 * @author daoho
 */
public class BookingGUI extends javax.swing.JPanel {

    // Color scheme
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);      // Blue
    private static final Color PRIMARY_DARK = new Color(31, 97, 141);        // Dark Blue
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);    // Light Blue
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);      // Green
    private static final Color DANGER_COLOR = new Color(231, 76, 60);        // Red
    private static final Color WARNING_COLOR = new Color(241, 196, 15);      // Yellow
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);  // Light Gray
    private static final Color TEXT_COLOR = new Color(44, 62, 80);           // Dark Gray
    private static final Color BORDER_COLOR = new Color(189, 195, 199);      // Gray

    public BookingGUI() {
        initComponents();
        styleComponents();
        initPopupMenu();
        jDialog1 = new javax.swing.JDialog((java.awt.Frame) null, "Bộ lọc tìm kiếm", true);
        initFilterDialog();
        setVisible(true);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        tfTimKiem = new javax.swing.JTextField();
        btnTimKiem = new javax.swing.JButton();
        btnLoc = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbDatPhong = new javax.swing.JTable();
        cbTieuChi = new javax.swing.JComboBox<>();

        tfTimKiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfTimKiemActionPerformed(evt);
            }
        });

        btnTimKiem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search.png")));
        btnTimKiem.setText("Tìm kiếm");

        btnLoc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/filter.png")));
        btnLoc.setText("Lọc");
        btnLoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLocActionPerformed(evt);
            }
        });

        tbDatPhong.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                        {"DP001", "231", "KH001", "20/08/2025", "Trống", "Online", "Không"},
                        {"DP002", "232", "KH002", "21/08/2025", "Đã đặt", "Trực tiếp", "VIP"},
                        {"DP003", "233", "KH003", "22/08/2025", "Đã nhận phòng", "Điện thoại", "Không"},
                        {"DP004", "234", "KH004", "23/08/2025", "Đã trả phòng", "Đại lý", "Không"}
                },
                new String [] {
                        "Mã đặt phòng", "Mã Phòng", "Mã khách hàng", "Ngày đặt", "Tình trạng", "Nguồn đặt", "Ghi chú"
                }
        ) {
            Class[] types = new Class [] {
                    java.lang.String.class, java.lang.String.class, java.lang.String.class,
                    java.lang.String.class, java.lang.String.class, java.lang.String.class,
                    java.lang.String.class
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

        jScrollPane1.setViewportView(tbDatPhong);
        if (tbDatPhong.getColumnModel().getColumnCount() > 0) {
            tbDatPhong.getColumnModel().getColumn(0).setResizable(false);
            tbDatPhong.getColumnModel().getColumn(1).setResizable(false);
            tbDatPhong.getColumnModel().getColumn(2).setResizable(false);
            tbDatPhong.getColumnModel().getColumn(3).setResizable(false);
            tbDatPhong.getColumnModel().getColumn(4).setResizable(false);
            tbDatPhong.getColumnModel().getColumn(5).setResizable(false);
            tbDatPhong.getColumnModel().getColumn(6).setResizable(false);
        }

        cbTieuChi.setModel(new javax.swing.DefaultComboBoxModel<>(
                new String[] { "Mã đặt phòng", "Mã phòng", "Mã khách hàng" }
        ));
        cbTieuChi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbTieuChiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(tfTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(10, 10, 10)
                                                .addComponent(btnTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(10, 10, 10)
                                                .addComponent(cbTieuChi, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(btnLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE))
                                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(tfTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cbTieuChi, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(20, 20, 20)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                                .addGap(20, 20, 20))
        );
    }// </editor-fold>

    private void styleComponents() {
        // Set background color
        this.setBackground(BACKGROUND_COLOR);

        // Style search text field
        tfTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tfTimKiem.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        tfTimKiem.setBackground(Color.WHITE);
        tfTimKiem.setForeground(TEXT_COLOR);

        // Style combo box
        cbTieuChi.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbTieuChi.setBackground(Color.WHITE);
        cbTieuChi.setForeground(TEXT_COLOR);
        cbTieuChi.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        // Style search button
        styleButton(btnTimKiem, PRIMARY_COLOR, Color.WHITE);

        // Style filter button
        styleButton(btnLoc, SECONDARY_COLOR, Color.WHITE);

        // Style table
        styleTable();

        // Style scroll pane
        jScrollPane1.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        jScrollPane1.getViewport().setBackground(Color.WHITE);
    }

    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }

    private void styleTable() {
        // Table font and colors
        tbDatPhong.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbDatPhong.setRowHeight(35);
        tbDatPhong.setGridColor(BORDER_COLOR);
        tbDatPhong.setSelectionBackground(SECONDARY_COLOR);
        tbDatPhong.setSelectionForeground(Color.WHITE);
        tbDatPhong.setShowGrid(true);
        tbDatPhong.setIntercellSpacing(new Dimension(1, 1));

        // Style table header
        JTableHeader header = tbDatPhong.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setBorder(BorderFactory.createLineBorder(PRIMARY_DARK, 1));

        // Center align header text
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        // Center align cell content
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tbDatPhong.getColumnCount(); i++) {
            tbDatPhong.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Alternating row colors
        tbDatPhong.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(new Color(248, 249, 250));
                    }
                    c.setForeground(TEXT_COLOR);
                }

                // Center align
                ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);

                // Color code status column
                if (column == 4 && value != null) {
                    String status = value.toString();
                    if (status.equals("Trống")) {
                        c.setForeground(new Color(149, 165, 166));
                    } else if (status.equals("Đã đặt")) {
                        c.setForeground(WARNING_COLOR);
                    } else if (status.equals("Đã nhận phòng")) {
                        c.setForeground(SUCCESS_COLOR);
                    } else if (status.equals("Đã trả phòng")) {
                        c.setForeground(PRIMARY_COLOR);
                    } else if (status.equals("Đã hủy")) {
                        c.setForeground(DANGER_COLOR);
                    }
                }

                return c;
            }
        });
    }

    private void initPopupMenu() {
        // Style popup menu
        jPopupMenu1.setBackground(Color.WHITE);
        jPopupMenu1.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        // Create menu items with icons and colors
        javax.swing.JMenuItem menuSua = new javax.swing.JMenuItem("Sửa");
        javax.swing.JMenuItem menuXoa = new javax.swing.JMenuItem("Xóa");
        javax.swing.JMenuItem menuDanhSachPhongDat = new javax.swing.JMenuItem("Danh sách phòng đặt");

        // Style menu items
        styleMenuItem(menuSua, PRIMARY_COLOR);
        styleMenuItem(menuXoa, DANGER_COLOR);
        styleMenuItem(menuDanhSachPhongDat, SUCCESS_COLOR);

        // Add to popup
        jPopupMenu1.add(menuSua);
        jPopupMenu1.add(menuXoa);
        jPopupMenu1.add(menuDanhSachPhongDat);

        // Event handlers
        menuSua.addActionListener(e -> suaDongDangChon());
        menuXoa.addActionListener(e -> xoaDongDangChon());
        menuDanhSachPhongDat.addActionListener(e -> hienThiDanhSachPhongDat());

        // Add mouse listener to table
        tbDatPhong.addMouseListener(new java.awt.event.MouseAdapter() {
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
                    int row = tbDatPhong.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < tbDatPhong.getRowCount()) {
                        tbDatPhong.setRowSelectionInterval(row, row);
                    } else {
                        tbDatPhong.clearSelection();
                    }
                    jPopupMenu1.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    private void styleMenuItem(JMenuItem item, Color color) {
        item.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        item.setForeground(color);
        item.setBackground(Color.WHITE);
        item.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        item.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                item.setBackground(new Color(248, 249, 250));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                item.setBackground(Color.WHITE);
            }
        });
    }

    private void tfTimKiemActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO: Implement search functionality
    }

    private void btnLocActionPerformed(java.awt.event.ActionEvent evt) {
        jDialog1.setTitle("Bộ lọc tìm kiếm");
        jDialog1.setSize(450, 350);
        jDialog1.setLocationRelativeTo(this);
        jDialog1.setModal(true);
        jDialog1.setVisible(true);
    }

    private void cbTieuChiActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO: Handle criteria change
    }

    private void suaDongDangChon() {
        int row = tbDatPhong.getSelectedRow();
        if (row != -1) {
            Object maDatPhong = tbDatPhong.getValueAt(row, 0);
            JOptionPane.showMessageDialog(this,
                    "Sửa dòng có Mã đặt phòng: " + maDatPhong,
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void xoaDongDangChon() {
        int row = tbDatPhong.getSelectedRow();
        if (row != -1) {
            Object maDatPhong = tbDatPhong.getValueAt(row, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn xóa Mã đặt phòng: " + maDatPhong + "?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                ((javax.swing.table.DefaultTableModel) tbDatPhong.getModel()).removeRow(row);
            }
        }
    }

    private void hienThiDanhSachPhongDat() {
        int row = tbDatPhong.getSelectedRow();
        if (row != -1) {
            Object maDatPhong = tbDatPhong.getValueAt(row, 0);
            BookingRoom bookingRoomFrame = new BookingRoom();
            bookingRoomFrame.setTitle("Danh sách phòng đặt - Mã: " + maDatPhong);
            bookingRoomFrame.setLocationRelativeTo(this);
            bookingRoomFrame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một dòng đặt phòng trước!",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void initFilterDialog() {
        jDialog1.setLayout(null);
        jDialog1.getContentPane().setBackground(BACKGROUND_COLOR);

        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(PRIMARY_COLOR);
        titlePanel.setBounds(0, 0, 450, 50);
        JLabel titleLabel = new JLabel("BỘ LỌC TÌM KIẾM");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        jDialog1.add(titlePanel);

        // Ngày đặt
        JLabel lblNgayDat = new JLabel("Ngày đặt:");
        lblNgayDat.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblNgayDat.setForeground(TEXT_COLOR);
        lblNgayDat.setBounds(40, 80, 120, 30);
        jDialog1.add(lblNgayDat);

        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateChooser.setBounds(170, 80, 240, 35);
        dateChooser.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        jDialog1.add(dateChooser);

        // Tình trạng
        JLabel lblTinhTrang = new JLabel("Tình trạng:");
        lblTinhTrang.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTinhTrang.setForeground(TEXT_COLOR);
        lblTinhTrang.setBounds(40, 140, 120, 30);
        jDialog1.add(lblTinhTrang);

        cbTinhTrang = new JComboBox<>(new String[]{
                "Tất cả", "Trống", "Đã đặt", "Đã nhận phòng", "Đã trả phòng", "Đã hủy"
        });
        cbTinhTrang.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbTinhTrang.setBackground(Color.WHITE);
        cbTinhTrang.setBounds(170, 140, 240, 35);
        cbTinhTrang.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        jDialog1.add(cbTinhTrang);

        // Nguồn đặt
        JLabel lblNguonDat = new JLabel("Nguồn đặt:");
        lblNguonDat.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblNguonDat.setForeground(TEXT_COLOR);
        lblNguonDat.setBounds(40, 200, 120, 30);
        jDialog1.add(lblNguonDat);

        cbNguonDat = new JComboBox<>(new String[]{
                "Tất cả", "Online", "Trực tiếp", "Điện thoại", "Đại lý"
        });
        cbNguonDat.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbNguonDat.setBackground(Color.WHITE);
        cbNguonDat.setBounds(170, 200, 240, 35);
        cbNguonDat.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        jDialog1.add(cbNguonDat);

        // Buttons
        JButton btnApDung = new JButton("Áp dụng");
        btnApDung.setBounds(120, 270, 120, 40);
        styleButton(btnApDung, SUCCESS_COLOR, Color.WHITE);
        btnApDung.addActionListener(e -> applyFilter());
        jDialog1.add(btnApDung);

        JButton btnHuy = new JButton("Hủy");
        btnHuy.setBounds(260, 270, 120, 40);
        styleButton(btnHuy, new Color(149, 165, 166), Color.WHITE);
        btnHuy.addActionListener(e -> jDialog1.setVisible(false));
        jDialog1.add(btnHuy);
    }

    private void applyFilter() {
        String ngayDat = "";
        Date selectedDate = dateChooser.getDate();
        if (selectedDate != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            ngayDat = sdf.format(selectedDate);
        }

        String tinhTrang = (String) cbTinhTrang.getSelectedItem();
        String nguonDat = (String) cbNguonDat.getSelectedItem();

        JOptionPane.showMessageDialog(this,
                "Áp dụng bộ lọc:\nNgày đặt: " + ngayDat +
                        "\nTình trạng: " + tinhTrang +
                        "\nNguồn đặt: " + nguonDat,
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);

        jDialog1.setVisible(false);
    }

    // Variables declaration
    private javax.swing.JButton btnLoc;
    private javax.swing.JButton btnTimKiem;
    private javax.swing.JComboBox<String> cbTieuChi;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbDatPhong;
    private javax.swing.JTextField tfTimKiem;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JComboBox<String> cbTinhTrang;
    private javax.swing.JComboBox<String> cbNguonDat;
    private JDateChooser dateChooser;
}