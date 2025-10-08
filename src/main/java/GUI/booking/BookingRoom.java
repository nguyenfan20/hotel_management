/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package GUI.booking;

/**
 *
 * @author daoho
 */
public class BookingRoom extends javax.swing.JFrame {

    /**
     * Creates new form BookingRoom
     */
    public BookingRoom() {
        initComponents();
        initPopupMenu();
        initFilterDialog();
    }

    private void initPopupMenu() {
        // Tạo các menu item
        javax.swing.JMenuItem menuSua = new javax.swing.JMenuItem("Sửa");
        javax.swing.JMenuItem menuXoa = new javax.swing.JMenuItem("Xóa");

        // Gắn vào popup menu
        jPopupMenu1.add(menuSua);
        jPopupMenu1.add(menuXoa);

        // Sự kiện Sửa
        menuSua.addActionListener(e -> suaDongDangChon());

        // Sự kiện Xóa
        menuXoa.addActionListener(e -> xoaDongDangChon());

        // Gắn MouseListener cho JTable để hiển thị popup
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
                if (e.isPopupTrigger()) { // chuột phải
                    int row = tbPhongDat.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < tbPhongDat.getRowCount()) {
                        tbPhongDat.setRowSelectionInterval(row, row); // chọn đúng dòng
                    } else {
                        tbPhongDat.clearSelection();
                    }
                    jPopupMenu1.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    private void initFilterDialog() {
        jDialog1.setTitle("Lọc phòng đặt");
        jDialog1.setModal(true);
        jDialog1.setSize(400, 250);
        jDialog1.setLocationRelativeTo(this);
        jDialog1.setDefaultCloseOperation(javax.swing.JDialog.HIDE_ON_CLOSE);

        // Main panel
        javax.swing.JPanel mainPanel = new javax.swing.JPanel();
        mainPanel.setLayout(new java.awt.GridBagLayout());
        mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;

        // Check-in date label
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        javax.swing.JLabel lblNgayNhan = new javax.swing.JLabel("Ngày nhận:");
        mainPanel.add(lblNgayNhan, gbc);

        // Check-in date picker
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0.7;
        com.toedter.calendar.JDateChooser dateNgayNhan = new com.toedter.calendar.JDateChooser();
        dateNgayNhan.setDateFormatString("dd/MM/yyyy");
        mainPanel.add(dateNgayNhan, gbc);

        // Check-out date label
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        javax.swing.JLabel lblNgayTra = new javax.swing.JLabel("Ngày trả:");
        mainPanel.add(lblNgayTra, gbc);

        // Check-out date picker
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0.7;
        com.toedter.calendar.JDateChooser dateNgayTra = new com.toedter.calendar.JDateChooser();
        dateNgayTra.setDateFormatString("dd/MM/yyyy");
        mainPanel.add(dateNgayTra, gbc);

        // Status label
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        javax.swing.JLabel lblTrangThai = new javax.swing.JLabel("Trạng thái:");
        mainPanel.add(lblTrangThai, gbc);

        // Status combobox
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0.7;
        javax.swing.JComboBox<String> cbTrangThai = new javax.swing.JComboBox<>(
                new String[]{"Tất cả", "Đã đặt", "Đã nhận", "Đã trả", "Đã hủy"}
        );
        mainPanel.add(cbTrangThai, gbc);

        // Button panel
        javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        javax.swing.JButton btnApDung = new javax.swing.JButton("Áp dụng");
        javax.swing.JButton btnHuy = new javax.swing.JButton("Hủy");

        buttonPanel.add(btnApDung);
        buttonPanel.add(btnHuy);

        // Add panels to dialog
        jDialog1.setLayout(new java.awt.BorderLayout());
        jDialog1.add(mainPanel, java.awt.BorderLayout.CENTER);
        jDialog1.add(buttonPanel, java.awt.BorderLayout.SOUTH);

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

            // Implement filter logic here
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
        // Filter the table based on check-in date, check-out date, and status
        System.out.println("Filtering with:");
        System.out.println("Ngày nhận: " + ngayNhan);
        System.out.println("Ngày trả: " + ngayTra);
        System.out.println("Trạng thái: " + trangThai);
    }

    private void suaDongDangChon() {
        int row = tbPhongDat.getSelectedRow();
        if (row != -1) {
            Object maPhong = tbPhongDat.getValueAt(row, 0);
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Sửa dòng có Mã phòng đặt: " + maPhong);
            // TODO: mở form sửa dữ liệu tại đây
        }
    }

    private void xoaDongDangChon() {
        int row = tbPhongDat.getSelectedRow();
        if (row != -1) {
            Object maPhong = tbPhongDat.getValueAt(row, 0);
            int confirm = javax.swing.JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn xóa Mã phòng đặt: " + maPhong + "?",
                    "Xác nhận", javax.swing.JOptionPane.YES_NO_OPTION);
            if (confirm == javax.swing.JOptionPane.YES_OPTION) {
                ((javax.swing.table.DefaultTableModel) tbPhongDat.getModel()).removeRow(row);
            }
        }
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jDialog1 = new javax.swing.JDialog();
        jPanel1 = new javax.swing.JPanel();
        tfTimKiem = new javax.swing.JTextField();
        btnTimKiem = new javax.swing.JButton();
        btnLoc = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbPhongDat = new javax.swing.JTable();
        cbTieuChi = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

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

        tbPhongDat.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                        {null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null}
                },
                new String [] {
                        "Mã phòng đặt", "Mã đặt phòng", "Mã phòng", "Ngày nhận ", "Ngày trả", "Thuế", "Trạng thái"
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

        cbTieuChi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Mã đặt phòng", "Mã phòng", "Mã phòng đặt" }));
        cbTieuChi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbTieuChiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(tfTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(cbTieuChi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 638, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(tfTimKiem, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(btnTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(btnLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(cbTieuChi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(36, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 650, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 353, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tfTimKiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfTimKiemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfTimKiemActionPerformed

    private void cbTieuChiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbTieuChiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbTieuChiActionPerformed

    private void btnLocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLocActionPerformed
        jDialog1.setVisible(true);
    }//GEN-LAST:event_btnLocActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(BookingRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BookingRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BookingRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BookingRoom.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BookingRoom().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnTimKiem;
    private javax.swing.JButton btnLoc;
    private javax.swing.JComboBox<String> cbTieuChi;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbPhongDat;
    private javax.swing.JTextField tfTimKiem;
    // End of variables declaration//GEN-END:variables
}
