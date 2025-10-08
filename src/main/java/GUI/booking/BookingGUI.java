package GUI.booking;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.toedter.calendar.JDateChooser;

/**
 *
 * @author daoho
 */
public class BookingGUI extends javax.swing.JPanel {

    // <CHANGE> Added filter dialog components
    private JDateChooser dateChooser;
    private JTextField tfNgayDat;
    private JComboBox<String> cbTinhTrang;
    private JComboBox<String> cbNguonDat;
    private JButton btnChonNgay;
    private JButton btnApDung;
    private JButton btnHuy;

    /**
     * Creates new form BookingGUI
     */
    public BookingGUI() {
        initComponents();
        initPopupMenu();
        // <CHANGE> Initialize and setup filter dialog
        jDialog1 = new javax.swing.JDialog((java.awt.Frame) null, "Bộ lọc tìm kiếm", true);
        initFilterDialog();
        setVisible(true);
    }

    // <CHANGE> New method to initialize filter dialog content
    private void initFilterDialog() {
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new GridBagLayout());
        filterPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Ngày đặt
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lblNgayDat = new JLabel("Ngày đặt:");
        filterPanel.add(lblNgayDat, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        tfNgayDat = new JTextField(15);
        tfNgayDat.setEditable(false);
        filterPanel.add(tfNgayDat, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        btnChonNgay = new JButton("Chọn ngày");
        btnChonNgay.addActionListener(e -> showDatePicker());
        filterPanel.add(btnChonNgay, gbc);

        // Tình trạng
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblTinhTrang = new JLabel("Tình trạng:");
        filterPanel.add(lblTinhTrang, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        cbTinhTrang = new JComboBox<>(new String[]{"Tất cả", "Trống", "Đã đặt", "Đã nhận phòng", "Đã trả phòng", "Đã hủy"});
        filterPanel.add(cbTinhTrang, gbc);

        // Nguồn đặt
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        JLabel lblNguonDat = new JLabel("Nguồn đặt:");
        filterPanel.add(lblNguonDat, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        cbNguonDat = new JComboBox<>(new String[]{"Tất cả", "Online", "Trực tiếp", "Điện thoại", "Đại lý"});
        filterPanel.add(cbNguonDat, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnApDung = new JButton("Áp dụng");
        btnApDung.addActionListener(e -> applyFilter());
        btnHuy = new JButton("Hủy");
        btnHuy.addActionListener(e -> jDialog1.setVisible(false));

        buttonPanel.add(btnApDung);
        buttonPanel.add(btnHuy);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.EAST;
        filterPanel.add(buttonPanel, gbc);

        jDialog1.add(filterPanel);
        jDialog1.pack();
    }

    // <CHANGE> Method to show date picker dialog
    private void showDatePicker() {
        JDialog dateDialog = new JDialog(jDialog1, "Chọn ngày", true);
        dateDialog.setLayout(new BorderLayout(10, 10));

        JPanel calendarPanel = new JPanel(new BorderLayout());
        calendarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setPreferredSize(new Dimension(250, 30));
        calendarPanel.add(dateChooser, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnXacNhan = new JButton("Xác nhận");
        JButton btnHuyChon = new JButton("Hủy");

        btnXacNhan.addActionListener(e -> {
            Date selectedDate = dateChooser.getDate();
            if (selectedDate != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                tfNgayDat.setText(sdf.format(selectedDate));
            }
            dateDialog.dispose();
        });

        btnHuyChon.addActionListener(e -> dateDialog.dispose());

        btnPanel.add(btnXacNhan);
        btnPanel.add(btnHuyChon);

        dateDialog.add(calendarPanel, BorderLayout.CENTER);
        dateDialog.add(btnPanel, BorderLayout.SOUTH);
        dateDialog.pack();
        dateDialog.setLocationRelativeTo(jDialog1);
        dateDialog.setVisible(true);
    }

    // <CHANGE> Method to apply filter
    private void applyFilter() {
        String ngayDat = tfNgayDat.getText();
        String tinhTrang = (String) cbTinhTrang.getSelectedItem();
        String nguonDat = (String) cbNguonDat.getSelectedItem();

        // TODO: Implement filter logic here
        System.out.println("Lọc theo:");
        System.out.println("Ngày đặt: " + ngayDat);
        System.out.println("Tình trạng: " + tinhTrang);
        System.out.println("Nguồn đặt: " + nguonDat);

        jDialog1.setVisible(false);
    }

    // ... existing code ...

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
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

        btnTimKiem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search.png"))); // NOI18N

        btnLoc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/filter.png"))); // NOI18N
        btnLoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLocActionPerformed(evt);
            }
        });

        tbDatPhong.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                        {null, "231", null, "20/08/2025", "Trống", "Online", "Không"},
                        {null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null}
                },
                new String [] {
                        "Mã đặt phòng", "Mã Phòng", "Mã khách hàng", "Ngày đặt", "Tình trạng", "Nguồn đặt ", "Ghi chú"
                }
        ) {
            Class[] types = new Class [] {
                    java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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

        cbTieuChi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Mã đặt phòng", "Mã phòng", "Mã khách hàng" }));
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
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(tfTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(cbTieuChi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(btnLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 638, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(tfTimKiem, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(btnTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(cbTieuChi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(36, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void initPopupMenu() {
        // Tạo menu item
        javax.swing.JMenuItem menuSua = new javax.swing.JMenuItem("Sửa");
        javax.swing.JMenuItem menuXoa = new javax.swing.JMenuItem("Xóa");
        javax.swing.JMenuItem menuDanhSachPhongDat = new javax.swing.JMenuItem("Danh sách phòng đặt");
        // Gắn vào popup
        jPopupMenu1.add(menuSua);
        jPopupMenu1.add(menuXoa);
        jPopupMenu1.add(menuDanhSachPhongDat);

        // Sự kiện "Sửa"
        menuSua.addActionListener(e -> suaDongDangChon());

        // Sự kiện "Xóa"
        menuXoa.addActionListener(e -> xoaDongDangChon());
        // Danh sách phòn đặt
        //menuDanhSachPhongDat.addActionListener(e -> xoaDongDangChon());
        // Thêm MouseListener cho JTable
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
                if (e.isPopupTrigger()) { // chuột phải
                    int row = tbDatPhong.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < tbDatPhong.getRowCount()) {
                        tbDatPhong.setRowSelectionInterval(row, row); // chọn đúng dòng click
                    } else {
                        tbDatPhong.clearSelection();
                    }
                    jPopupMenu1.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }


    private void tfTimKiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfTimKiemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfTimKiemActionPerformed

    private void btnLocActionPerformed(java.awt.event.ActionEvent evt) {
        // Hiện dialog khi bấm nút Lọc
        jDialog1.setTitle("Bộ lọc tìm kiếm");
        jDialog1.setLocationRelativeTo(this); // canh giữa so với JPanel
        jDialog1.setModal(true); // chỉ cho phép thao tác trên dialog
        jDialog1.setVisible(true);
    }


    private void cbTieuChiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbTieuChiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbTieuChiActionPerformed

    private void suaDongDangChon() {
        int row = tbDatPhong.getSelectedRow();
        if (row != -1) {
            Object maDatPhong = tbDatPhong.getValueAt(row, 0);
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Sửa dòng có Mã đặt phòng: " + maDatPhong);
            // TODO: Thêm code mở form sửa
        }
    }

    private void xoaDongDangChon() {
        int row = tbDatPhong.getSelectedRow();
        if (row != -1) {
            Object maDatPhong = tbDatPhong.getValueAt(row, 0);
            int confirm = javax.swing.JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn xóa Mã đặt phòng: " + maDatPhong + "?",
                    "Xác nhận", javax.swing.JOptionPane.YES_NO_OPTION);
            if (confirm == javax.swing.JOptionPane.YES_OPTION) {
                ((javax.swing.table.DefaultTableModel) tbDatPhong.getModel()).removeRow(row);
            }
        }
    }



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLoc;
    private javax.swing.JButton btnTimKiem;
    private javax.swing.JComboBox<String> cbTieuChi;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbDatPhong;
    private javax.swing.JTextField tfTimKiem;
    private javax.swing.JDialog jDialog1;
    // End of variables declaration//GEN-END:variables
}