/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package GUI.billing.service;
import BUS.ServiceOrderBUS;
import DTO.ServiceOrderDTO;
import DTO.ServiceDTO;
import BUS.ServiceBUS;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
public class ServiceOrdersform extends javax.swing.JFrame {
private ServiceBUS serviceBUS = new ServiceBUS();

    public ServiceOrdersform() {
    initComponents();
    setLocationRelativeTo(null);
    setTitle("Quản lý Đơn Dịch vụ");
    loadTableData();
    }

    private final ServiceOrderBUS serviceOrderBUS = new ServiceOrderBUS();

    private void loadTableData() {
    DefaultTableModel model = (DefaultTableModel) tblServiceorder.getModel();
    model.setRowCount(0); 
    List<ServiceOrderDTO> orderList = serviceOrderBUS.getAll();

    for (ServiceOrderDTO order : orderList) {
        
        ServiceDTO service = serviceBUS.getById(order.getServiceId());
        String serviceName = (service != null) ? service.getName() : "Không rõ";
        double total = order.getUnitPrice() * order.getQuantity();
        
        model.addRow(new Object[]{
            order.getServiceOrderId(),         
            order.getBookingRoomId(),           
            serviceName,                        
            order.getQuantity(),
            order.getUnitPrice(),
            total,
            order.getOrderedAt() != null 
                ? order.getOrderedAt().toString() 
                : "",                           
            order.getNote()                     
        });
    }
}




    private void addServiceOrder() {
        ServiceOrderDialog dialog = new ServiceOrderDialog(this, true);
        ServiceOrderDTO newOrder = dialog.openForCreate();

        if (newOrder != null) {
            serviceOrderBUS.add(newOrder);
            loadTableData();
            JOptionPane.showMessageDialog(this, "Thêm đơn dịch vụ thành công!");
        }
    }

    private void editServiceOrder() {
    int row = tblServiceorder.getSelectedRow();
    if (row == -1) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn dịch vụ cần sửa!");
        return;
    }

    
    int id = (int) tblServiceorder.getValueAt(row, 0);
    
    
    ServiceOrderDTO order = serviceOrderBUS.getById(id);
    if (order == null) {
        JOptionPane.showMessageDialog(this, "Không tìm thấy đơn dịch vụ!");
        return;
    }

    
    ServiceOrderDialog dialog = new ServiceOrderDialog(this, true);
    ServiceOrderDTO updatedOrder = dialog.openForEdit(order);

    
    if (updatedOrder != null) {
        boolean success = serviceOrderBUS.update(updatedOrder);
        if (success) {
            loadTableData();  
            JOptionPane.showMessageDialog(this, "Cập nhật đơn dịch vụ thành công!");
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
        }
    }
}



    private void deleteServiceOrder() {
        int row = tblServiceorder.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn đơn dịch vụ cần xoá!");
            return;
        }
        int id = (int) tblServiceorder.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Xoá đơn này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            serviceOrderBUS.delete(id);
            loadTableData();
        }
    }
    
    
  private void searchServiceOrder() {
    String keyword = tfSearchorder.getText().trim();
    String searchType = (String) cbsearch.getSelectedItem();

    DefaultTableModel model = (DefaultTableModel) tblServiceorder.getModel();
    model.setRowCount(0);

    
    if (keyword.isEmpty()) {
        loadTableData();
        return;
    }

    List<ServiceOrderDTO> results;

    switch (searchType) {
        case "Mã đặt phòng":
            try {
                int bookingId = Integer.parseInt(keyword);
                results = serviceOrderBUS.getAll().stream()
                        .filter(o -> o.getBookingRoomId() == bookingId)
                        .toList();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ cho Mã đặt phòng!");
                return;
            }
            break;

        case "Tên dịch vụ":
            results = serviceOrderBUS.searchByServiceName(keyword);
            break;

        case "Số lượng":
            try {
                int qty = Integer.parseInt(keyword);
                results = serviceOrderBUS.getAll().stream()
                        .filter(o -> o.getQuantity() == qty)
                        .toList();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ cho Số lượng!");
                return;
            }
            break;

        case "Ghi chú":
            results = serviceOrderBUS.getAll().stream()
                        .filter(o -> o.getNote() != null && o.getNote().toLowerCase().contains(keyword.toLowerCase()))
                        .toList();
            break;

        default:
            results = serviceOrderBUS.getAll();
    }

    
    for (ServiceOrderDTO s : results) {
        ServiceDTO service = serviceBUS.getById(s.getServiceId());
        String serviceName = (service != null) ? service.getName() : "Không rõ";

        model.addRow(new Object[]{
            s.getServiceOrderId(),
            s.getBookingRoomId(),
            serviceName,
            s.getQuantity(),
            s.getOrderedAt(),
            s.getNote()
        });
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

        jPanelorder = new javax.swing.JPanel();
        lblTitleorder = new javax.swing.JLabel();
        tfSearchorder = new javax.swing.JTextField();
        btnSearchorder = new javax.swing.JButton();
        btnRefreshorder = new javax.swing.JButton();
        JScrollPaneorder = new javax.swing.JScrollPane();
        tblServiceorder = new javax.swing.JTable();
        btnAddorder = new javax.swing.JButton();
        btnEditorder = new javax.swing.JButton();
        btnDeleteorder = new javax.swing.JButton();
        cbsearch = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblTitleorder.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N
        lblTitleorder.setText("QUẢN LÝ ĐƠN DỊCH VỤ");

        tfSearchorder.setColumns(25);
        tfSearchorder.setToolTipText("");

        btnSearchorder.setFont(new java.awt.Font("Arial", 0, 13)); // NOI18N
        btnSearchorder.setText("Tìm");
        btnSearchorder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchorderActionPerformed(evt);
            }
        });

        btnRefreshorder.setText("Làm mới");
        btnRefreshorder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshorderActionPerformed(evt);
            }
        });

        tblServiceorder.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Mã đặt phòng", "Tên dịch vụ", "Số lượng", "Giá", "Tổng", "Thời gian đặt", "Ghi chú"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblServiceorder.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblServiceorder.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        JScrollPaneorder.setViewportView(tblServiceorder);

        btnAddorder.setText("Thêm");
        btnAddorder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddorderActionPerformed(evt);
            }
        });

        btnEditorder.setText("Sửa");
        btnEditorder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditorderActionPerformed(evt);
            }
        });

        btnDeleteorder.setText("Xoá");
        btnDeleteorder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteorderActionPerformed(evt);
            }
        });

        cbsearch.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Mã đặt phòng", "Tên dịch vụ", "Số lượng", " " }));
        cbsearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbsearchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanelorderLayout = new javax.swing.GroupLayout(jPanelorder);
        jPanelorder.setLayout(jPanelorderLayout);
        jPanelorderLayout.setHorizontalGroup(
            jPanelorderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelorderLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(JScrollPaneorder))
            .addGroup(jPanelorderLayout.createSequentialGroup()
                .addGroup(jPanelorderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanelorderLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnAddorder)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEditorder)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDeleteorder))
                    .addGroup(jPanelorderLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanelorderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblTitleorder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tfSearchorder))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSearchorder, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbsearch, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRefreshorder, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(186, Short.MAX_VALUE))
        );
        jPanelorderLayout.setVerticalGroup(
            jPanelorderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelorderLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitleorder)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanelorderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfSearchorder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearchorder)
                    .addComponent(btnRefreshorder)
                    .addComponent(cbsearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(JScrollPaneorder, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanelorderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddorder)
                    .addComponent(btnEditorder)
                    .addComponent(btnDeleteorder))
                .addGap(33, 33, 33))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelorder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanelorder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchorderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchorderActionPerformed
        searchServiceOrder();
    }//GEN-LAST:event_btnSearchorderActionPerformed

    private void btnRefreshorderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshorderActionPerformed
        loadTableData();
    }//GEN-LAST:event_btnRefreshorderActionPerformed

    private void btnAddorderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddorderActionPerformed
        addServiceOrder();
    }//GEN-LAST:event_btnAddorderActionPerformed

    private void btnEditorderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditorderActionPerformed
        editServiceOrder();
    }//GEN-LAST:event_btnEditorderActionPerformed

    private void btnDeleteorderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteorderActionPerformed
        deleteServiceOrder();
    }//GEN-LAST:event_btnDeleteorderActionPerformed

    private void cbsearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbsearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cbsearchActionPerformed

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
            java.util.logging.Logger.getLogger(ServiceOrdersform.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ServiceOrdersform.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ServiceOrdersform.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ServiceOrdersform.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ServiceOrdersform().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane JScrollPaneorder;
    private javax.swing.JButton btnAddorder;
    private javax.swing.JButton btnDeleteorder;
    private javax.swing.JButton btnEditorder;
    private javax.swing.JButton btnRefreshorder;
    private javax.swing.JButton btnSearchorder;
    private javax.swing.JComboBox<String> cbsearch;
    private javax.swing.JPanel jPanelorder;
    private javax.swing.JLabel lblTitleorder;
    private javax.swing.JTable tblServiceorder;
    private javax.swing.JTextField tfSearchorder;
    // End of variables declaration//GEN-END:variables
}
