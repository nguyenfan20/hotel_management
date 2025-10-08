/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package GUI.custom;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/**
 *
 * @author duhieu
 */
public class CustomGUI extends javax.swing.JPanel {

    /**
     * Creates new form CustomGUI
     */
    public CustomGUI() {
        initComponents();
        addRightClickMenu(); // ✅ Thêm hàm menu chuột phải
    }

    /**
     * Hàm tạo menu chuột phải cho JTable
     */
    private void addRightClickMenu() {
        // Tạo popup menu
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem itemXem = new JMenuItem("Xem chi tiết");
        JMenuItem itemSua = new JMenuItem("Sửa");
        JMenuItem itemXoa = new JMenuItem("Xóa");

        // Gắn sự kiện cho các menu item
        itemXem.addActionListener(e -> {
            int row = tbDatPhong.getSelectedRow();
            if (row != -1) {
                Object id = tbDatPhong.getValueAt(row, 0);
                if (id != null) {
                    showCustomerDetailDialog(id.toString());
                } else {
                    JOptionPane.showMessageDialog(this, "Không có mã khách hàng!");
                }
            }
        });

        itemSua.addActionListener(e -> {
            int row = tbDatPhong.getSelectedRow();
            if (row != -1) {
                Object ten = tbDatPhong.getValueAt(row, 1);
                JOptionPane.showMessageDialog(this, "Sửa thông tin: " + ten);
            }
        });

        itemXoa.addActionListener(e -> {
            int row = tbDatPhong.getSelectedRow();
            if (row != -1) {
                Object ten = tbDatPhong.getValueAt(row, 1);
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Bạn có chắc muốn xóa " + ten + "?",
                        "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    ((DefaultTableModel) tbDatPhong.getModel()).removeRow(row);
                }
            }
        });

        // Thêm item vào popup
        popupMenu.add(itemXem);
        popupMenu.add(itemSua);
        popupMenu.add(itemXoa);

        // Gắn popup menu vào JTable
        tbDatPhong.setComponentPopupMenu(popupMenu);

        // Khi click chuột phải, chọn dòng đó
        tbDatPhong.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = tbDatPhong.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < tbDatPhong.getRowCount()) {
                        tbDatPhong.setRowSelectionInterval(row, row);
                    } else {
                        tbDatPhong.clearSelection();
                    }
                }
            }
        });
    }

    /**
     * ✅ Hàm hiển thị dialog chi tiết khách hàng
     */
    private void showCustomerDetailDialog(String customerId) {
        try (Connection conn = getConnection()) {
            String sql = "SELECT * FROM Customer WHERE customer_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, customerId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Tạo dialog hiển thị thông tin
                JDialog dialog = new JDialog((Frame) null, "Chi tiết khách hàng", true);
                dialog.setSize(400, 400);
                dialog.setLocationRelativeTo(this);
                dialog.setLayout(new BorderLayout());

                JTextArea info = new JTextArea();
                info.setEditable(false);
                info.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                info.setText(
                        "🆔 Mã khách hàng: " + rs.getInt("customer_id") + "\n" +
                        "👤 Họ và tên: " + rs.getString("full_name") + "\n" +
                        "📞 Số điện thoại: " + rs.getString("phone") + "\n" +
                        "📧 Email: " + rs.getString("email") + "\n" +
                        "🪪 CMND/CCCD: " + rs.getString("id_card") + "\n" +
                        "🏠 Địa chỉ: " + rs.getString("address") + "\n" +
                        "🌏 Quốc tịch: " + rs.getString("nationality") + "\n" +
                        "🎂 Ngày sinh: " + rs.getDate("dob") + "\n" +
                        "🚻 Giới tính: " + rs.getString("gender") + "\n" +
                        "📝 Ghi chú: " + rs.getString("note")
                );

                dialog.add(new JScrollPane(info), BorderLayout.CENTER);

                JButton closeBtn = new JButton("Đóng");
                closeBtn.addActionListener(ev -> dialog.dispose());
                JPanel bottom = new JPanel();
                bottom.add(closeBtn);
                dialog.add(bottom, BorderLayout.SOUTH);

                dialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng ID: " + customerId);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi truy vấn CSDL: " + e.getMessage());
        }
    }

    /**
     * ✅ Kết nối tới SQL Server (sửa lại cho đúng thông tin của bạn)
     */
    private Connection getConnection() throws Exception {
        String url = "jdbc:sqlserver://localhost:1433;databaseName=HotelDB;encrypt=false";
        String user = "sa";
        String pass = "123456";
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return DriverManager.getConnection(url, user, pass);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    @SuppressWarnings("unchecked")
    private void initComponents() {

        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu4 = new javax.swing.JMenu();
        jMenu5 = new javax.swing.JMenu();
        jFrame1 = new javax.swing.JFrame();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        jPopupMenu2 = new javax.swing.JPopupMenu();
        btnTimKiem = new javax.swing.JButton();
        tfTimKiem = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbDatPhong = new javax.swing.JTable();
        btnLoc = new javax.swing.JButton();

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        jMenu3.setText("jMenu3");

        jMenu4.setText("File");
        jMenuBar2.add(jMenu4);

        jMenu5.setText("Edit");
        jMenuBar2.add(jMenu5);

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame1Layout.setVerticalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        btnTimKiem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search.png"))); // NOI18N

        tfTimKiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfTimKiemActionPerformed(evt);
            }
        });

        tbDatPhong.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"1", "Nguyễn Văn A", "01/01/2000", "0123456789", "Nam", null},
                {"2", "Trần Thị B", "10/03/1999", "0987654321", "Nữ", null}
            },
            new String [] {
                "Mã khách hàng", "Họ và tên", "Ngày sinh", "Số điện thoại", "Giới tính", "Xem chi tiết"
            }
        ));
        jScrollPane1.setViewportView(tbDatPhong);

        btnLoc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/filter.png"))); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(tfTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnTimKiem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnLoc))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 692, Short.MAX_VALUE))
                .addGap(43, 43, 43))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLoc)
                    .addComponent(tfTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(256, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void tfTimKiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfTimKiemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfTimKiemActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLoc;
    private javax.swing.JButton btnTimKiem;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JPopupMenu jPopupMenu2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tbDatPhong;
    private javax.swing.JTextField tfTimKiem;
    // End of variables declaration//GEN-END:variables
}
