package GUI.service;

import BUS.ServiceOrderBUS;
import BUS.ServiceBUS;
import DTO.ServiceDTO;
import DTO.ServiceOrderDTO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ServiceOrdersform extends javax.swing.JPanel {
    private final ServiceOrderBUS serviceOrderBUS = new ServiceOrderBUS();
    private final ServiceBUS serviceBUS = new ServiceBUS();

    public ServiceOrdersform() {
        initComponents();
        loadTableData();
    }

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
                    order.getOrderedAt() != null ? order.getOrderedAt().toString() : "",
                    order.getNote()
            });
        }
    }

    private void addServiceOrder() {
        ServiceOrderDialog dialog = new ServiceOrderDialog(SwingUtilities.getWindowAncestor(this), true);
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

        ServiceOrderDialog dialog = new ServiceOrderDialog(SwingUtilities.getWindowAncestor(this), true);
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

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả!");
            loadTableData();
            return;
        }

        for (ServiceOrderDTO s : results) {
            ServiceDTO service = serviceBUS.getById(s.getServiceId());
            String serviceName = (service != null) ? service.getName() : "Không rõ";
            double total = s.getUnitPrice() * s.getQuantity();

            model.addRow(new Object[]{
                    s.getServiceOrderId(),
                    s.getBookingRoomId(),
                    serviceName,
                    s.getQuantity(),
                    s.getUnitPrice(),
                    total,
                    s.getOrderedAt() != null ? s.getOrderedAt().toString() : "",
                    s.getNote()
            });
        }
    }

    @SuppressWarnings("unchecked")
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

        lblTitleorder.setFont(new java.awt.Font("Arial", 1, 20));
        lblTitleorder.setText("QUẢN LÝ ĐƠN DỊCH VỤ");

        tfSearchorder.setColumns(25);

        btnSearchorder.setText("Tìm");
        btnSearchorder.addActionListener(evt -> searchServiceOrder());

        btnRefreshorder.setText("Làm mới");
        btnRefreshorder.addActionListener(evt -> loadTableData());

        tblServiceorder.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {},
                new String [] {
                        "ID", "Mã đặt phòng", "Tên dịch vụ", "Số lượng", "Giá", "Tổng", "Thời gian đặt", "Ghi chú"
                }
        ) {
            boolean[] canEdit = new boolean[] { false, false, false, false, false, false, false, false };
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        JScrollPaneorder.setViewportView(tblServiceorder);

        btnAddorder.setText("Thêm");
        btnAddorder.addActionListener(evt -> addServiceOrder());

        btnEditorder.setText("Sửa");
        btnEditorder.addActionListener(evt -> editServiceOrder());

        btnDeleteorder.setText("Xoá");
        btnDeleteorder.addActionListener(evt -> deleteServiceOrder());

        cbsearch.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Mã đặt phòng", "Tên dịch vụ", "Số lượng", "Ghi chú" }));

        javax.swing.GroupLayout jPanelorderLayout = new javax.swing.GroupLayout(jPanelorder);
        jPanelorder.setLayout(jPanelorderLayout);
        jPanelorderLayout.setHorizontalGroup(
                jPanelorderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanelorderLayout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(jPanelorderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(JScrollPaneorder)
                                        .addGroup(jPanelorderLayout.createSequentialGroup()
                                                .addGroup(jPanelorderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(lblTitleorder)
                                                        .addComponent(tfSearchorder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnSearchorder)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(cbsearch, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnRefreshorder)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(jPanelorderLayout.createSequentialGroup()
                                                .addComponent(btnAddorder)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnEditorder)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnDeleteorder)
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap())
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
                                .addContainerGap(33, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanelorder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanelorder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }

    // Variables declaration
    private javax.swing.JButton btnAddorder;
    private javax.swing.JButton btnDeleteorder;
    private javax.swing.JButton btnEditorder;
    private javax.swing.JButton btnRefreshorder;
    private javax.swing.JButton btnSearchorder;
    private javax.swing.JComboBox<String> cbsearch;
    private javax.swing.JPanel jPanelorder;
    private javax.swing.JLabel lblTitleorder;
    private javax.swing.JScrollPane JScrollPaneorder;
    private javax.swing.JTable tblServiceorder;
    private javax.swing.JTextField tfSearchorder;
}