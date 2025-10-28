package GUI.service;

import BUS.ServiceBUS;
import DTO.ServiceDTO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class ServiceForm extends javax.swing.JPanel {
    private final ServiceBUS serviceBus = new ServiceBUS();

    public ServiceForm() {
        initComponents();
        loadTableData();
    }

    private void loadTableData() {
        List<ServiceDTO> services = serviceBus.getAll();
        DefaultTableModel model = (DefaultTableModel) tblService.getModel();
        model.setRowCount(0);
        for (ServiceDTO s : services) {
            model.addRow(new Object[]{
                    s.getServiceId(),
                    s.getName(),
                    s.getUnit(),
                    s.getUnitPrice(),
                    s.getChargeType(),
                    s.isActive() ? "Hoạt động" : "Ngừng"
            });
        }
    }

    private void searchService() {
        String keyword = tfSearch.getText().trim().toLowerCase();
        String searchType = (String) CbserviceSearch.getSelectedItem();
        DefaultTableModel model = (DefaultTableModel) tblService.getModel();
        model.setRowCount(0);

        if (keyword.isEmpty()) {
            loadTableData();
            return;
        }

        List<ServiceDTO> allServices = serviceBus.getAll();
        List<ServiceDTO> results = new ArrayList<>();

        for (ServiceDTO s : allServices) {
            if (s == null) continue;

            switch (searchType) {
                case "Tên dịch vụ":
                    if (s.getName() != null && s.getName().toLowerCase().contains(keyword)) {
                        results.add(s);
                    }
                    break;

                case "Đơn vị":
                    if (s.getUnit() != null && s.getUnit().toLowerCase().contains(keyword)) {
                        results.add(s);
                    }
                    break;

                case "Giá":
                    try {
                        double price = Double.parseDouble(keyword);
                        if (s.getUnitPrice() == price) {
                            results.add(s);
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Giá phải là số!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    break;
            }
        }

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả phù hợp!");
            loadTableData(); // Hiển thị lại dữ liệu gốc nếu không có kết quả
            return;
        }

        for (ServiceDTO s : results) {
            model.addRow(new Object[]{
                    s.getServiceId(),
                    s.getName(),
                    s.getUnit(),
                    s.getUnitPrice(),
                    s.getChargeType(),
                    s.isActive() ? "Hoạt động" : "Ngừng"
            });
        }
    }

    private void addService() {
        ServiceDialog dialog = new ServiceDialog(SwingUtilities.getWindowAncestor(this), true);
        ServiceDTO newService = dialog.openForCreate();

        if (newService != null) {
            serviceBus.add(newService);
            loadTableData();
            JOptionPane.showMessageDialog(this, "Thêm dịch vụ thành công!");
        }
    }

    private void editService() {
        int row = tblService.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dịch vụ cần sửa!");
            return;
        }

        int id = (int) tblService.getValueAt(row, 0);
        ServiceDTO s = serviceBus.getById(id);

        ServiceDialog dialog = new ServiceDialog(SwingUtilities.getWindowAncestor(this), true);
        ServiceDTO updated = dialog.openForEdit(s);

        if (updated != null) {
            serviceBus.update(updated);
            loadTableData();
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
        }
    }

    private void deleteService() {
        int row = tblService.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Chọn dịch vụ cần xóa!");
            return;
        }
        int id = (int) tblService.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa dịch vụ này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            serviceBus.delete(id);
            loadTableData();
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lblTitle = new javax.swing.JLabel();
        tfSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        JScrollPane = new javax.swing.JScrollPane();
        tblService = new javax.swing.JTable();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        CbserviceSearch = new javax.swing.JComboBox<>();

        lblTitle.setFont(new java.awt.Font("Arial", 1, 20));
        lblTitle.setText("QUẢN LÝ DỊCH VỤ");

        tfSearch.setColumns(25);

        btnSearch.setFont(new java.awt.Font("Arial", 0, 13));
        btnSearch.setText("Tìm");
        btnSearch.addActionListener(evt -> searchService());

        btnRefresh.setText("Làm mới");
        btnRefresh.addActionListener(evt -> loadTableData());

        tblService.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {},
                new String [] {
                        "ID", "Tên dịch vụ", "Đơn vị", "Giá", "Loại tính phí", "Trạng thái"
                }
        ) {
            boolean[] canEdit = new boolean[] { false, false, false, false, false, false };
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        tblService.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        JScrollPane.setViewportView(tblService);

        btnAdd.setText("Thêm");
        btnAdd.addActionListener(evt -> addService());

        btnEdit.setText("Sửa");
        btnEdit.addActionListener(evt -> editService());

        btnDelete.setText("Xoá");
        btnDelete.addActionListener(evt -> deleteService());

        CbserviceSearch.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tên dịch vụ", "Đơn vị", "Giá" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(JScrollPane)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(lblTitle)
                                                        .addComponent(tfSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnSearch)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(CbserviceSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnRefresh)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(btnAdd)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnEdit)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnDelete)
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(lblTitle)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(tfSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnSearch)
                                        .addComponent(btnRefresh)
                                        .addComponent(CbserviceSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(JScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnAdd)
                                        .addComponent(btnEdit)
                                        .addComponent(btnDelete))
                                .addContainerGap(33, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }

    // Variables declaration
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSearch;
    private javax.swing.JComboBox<String> CbserviceSearch;
    private javax.swing.JScrollPane JScrollPane;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JTable tblService;
    private javax.swing.JTextField tfSearch;
}