package GUI.service;

import javax.swing.DefaultComboBoxModel;
import BUS.ServiceBUS;
import BUS.BookingRoomBUS;
import DTO.ServiceDTO;
import DTO.ServiceOrderDTO;
import DTO.BookingRoomDTO;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServiceOrderDialog extends javax.swing.JDialog {

    private boolean editMode = false;
    private ServiceOrderDTO currentOrder;
    private ServiceOrderDTO resultOrder;
    private ServiceBUS serviceBUS = new ServiceBUS();
    private BookingRoomBUS bookingRoomBUS = new BookingRoomBUS();
    private List<ServiceDTO> services;
    private List<BookingRoomDTO> checkedInRooms;

    public ServiceOrderDialog(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(parent);
        setTitle("Thông tin Đơn Dịch vụ");
        loadServicesToComboBox();
        loadCheckedInRoomsToComboBox();
    }

    public ServiceOrderDialog(Window owner, boolean modal) {
        super(JOptionPane.getFrameForComponent(owner), modal);
        initComponents();
        setLocationRelativeTo(owner);
        setTitle("Thông tin Đơn Dịch vụ");
        loadServicesToComboBox();
        loadCheckedInRoomsToComboBox();
    }

    public ServiceOrderDTO openForCreate() {
        editMode = false;
        currentOrder = null;
        ordertitle.setText("Thêm đơn dịch vụ");
        clearForm();
        setVisible(true);
        return resultOrder;
    }

    public ServiceOrderDTO openForEdit(ServiceOrderDTO order) {
        editMode = true;
        currentOrder = order;
        ordertitle.setText("Sửa đơn dịch vụ");
        fillForm(order);
        setVisible(true);
        return resultOrder;
    }

    private void clearForm() {
        cbBookingRoom.setSelectedIndex(-1);
        cbServiceorder.setSelectedIndex(-1);
        spnQuantityorder.setValue(1);
        txtNote.setText("");
    }

    private void fillForm(ServiceOrderDTO order) {
        // Tìm và chọn booking room tương ứng
        for (int i = 0; i < cbBookingRoom.getItemCount(); i++) {
            BookingRoomDTO br = cbBookingRoom.getItemAt(i);
            if (br.getBookingRoomId() == order.getBookingRoomId()) {
                cbBookingRoom.setSelectedIndex(i);
                break;
            }
        }

        spnQuantityorder.setValue(order.getQuantity());
        txtNote.setText(order.getNote());

        // Tìm và chọn service tương ứng
        for (int i = 0; i < cbServiceorder.getItemCount(); i++) {
            ServiceDTO s = cbServiceorder.getItemAt(i);
            if (s.getServiceId() == order.getServiceId()) {
                cbServiceorder.setSelectedIndex(i);
                break;
            }
        }
    }

    private void loadCheckedInRoomsToComboBox() {
        cbBookingRoom.removeAllItems();

        // Lấy danh sách phòng đã check-in
        checkedInRooms = bookingRoomBUS.getBookingRoomsByStatus("CHECKED_IN");

        DefaultComboBoxModel<BookingRoomDTO> model = new DefaultComboBoxModel<>();

        if (checkedInRooms != null && !checkedInRooms.isEmpty()) {
            for (BookingRoomDTO br : checkedInRooms) {
                model.addElement(br);
            }
        }

        cbBookingRoom.setModel(model);

        // Tùy chỉnh hiển thị trong ComboBox
        cbBookingRoom.setRenderer(new javax.swing.DefaultListCellRenderer() {
            private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            @Override
            public java.awt.Component getListCellRendererComponent(
                    javax.swing.JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {

                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value != null && value instanceof BookingRoomDTO) {
                    BookingRoomDTO br = (BookingRoomDTO) value;
                    String checkInTime = br.getCheckInActual() != null ?
                            br.getCheckInActual().format(formatter) : "N/A";
                    setText(String.format("ID: %d - Phòng: %d - Check-in: %s",
                            br.getBookingRoomId(),
                            br.getRoomId(),
                            checkInTime));
                } else {
                    setText("");
                }

                return this;
            }
        });
    }

    private void loadServicesToComboBox() {
        cbServiceorder.removeAllItems();

        services = serviceBUS.getAll();

        DefaultComboBoxModel<ServiceDTO> model = new DefaultComboBoxModel<>();

        if (services != null) {
            for (ServiceDTO s : services) {
                if (s != null && s.isActive()) {
                    model.addElement(s);
                }
            }
        }

        cbServiceorder.setModel(model);

        cbServiceorder.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    javax.swing.JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {

                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value != null && value instanceof ServiceDTO) {
                    ServiceDTO s = (ServiceDTO) value;
                    setText(String.format("%s - %.0f VNĐ", s.getName(), s.getUnitPrice()));
                } else {
                    setText("");
                }

                return this;
            }
        });
    }

    private boolean validateForm() {
        if (cbBookingRoom.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng đã check-in!");
            return false;
        }
        if (cbServiceorder.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dịch vụ!");
            return false;
        }
        int quantity = (Integer) spnQuantityorder.getValue();
        if (quantity <= 0) {
            JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0!");
            return false;
        }
        return true;
    }

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {
        resultOrder = null;
        dispose();
    }

    public ServiceOrderDTO getResult() {
        return resultOrder;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        Panel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        btnSaveorder = new javax.swing.JButton();
        btnCancelorder = new javax.swing.JButton();
        txtNote = new javax.swing.JTextField();
        cbServiceorder = new javax.swing.JComboBox<>();
        spnQuantityorder = new javax.swing.JSpinner();
        cbBookingRoom = new javax.swing.JComboBox<>();
        ordertitle = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        Panel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Thông tin", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Arial", 1, 14)));

        jLabel7.setText("Phòng đã check-in");

        jLabel8.setText("Số lượng");

        jLabel5.setText("Tên dịch vụ");

        jLabel6.setText("Ghi chú");

        btnSaveorder.setText("Lưu");
        btnSaveorder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveorderActionPerformed(evt);
            }
        });

        btnCancelorder.setText("Huỷ");
        btnCancelorder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelorderActionPerformed(evt);
            }
        });

        spnQuantityorder.setModel(new javax.swing.SpinnerNumberModel(1, 1, 100, 1));

        javax.swing.GroupLayout PanelLayout = new javax.swing.GroupLayout(Panel);
        Panel.setLayout(PanelLayout);
        PanelLayout.setHorizontalGroup(
                PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(PanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(PanelLayout.createSequentialGroup()
                                                .addComponent(jLabel7)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(cbBookingRoom, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(PanelLayout.createSequentialGroup()
                                                .addComponent(jLabel8)
                                                .addGap(18, 18, 18)
                                                .addComponent(spnQuantityorder, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
                                .addGroup(PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(PanelLayout.createSequentialGroup()
                                                .addComponent(btnSaveorder, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(29, 29, 29)
                                                .addComponent(btnCancelorder))
                                        .addGroup(PanelLayout.createSequentialGroup()
                                                .addGroup(PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel6)
                                                        .addComponent(jLabel5))
                                                .addGap(25, 25, 25)
                                                .addGroup(PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(txtNote)
                                                        .addComponent(cbServiceorder, 0, 250, Short.MAX_VALUE))))
                                .addContainerGap())
        );
        PanelLayout.setVerticalGroup(
                PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(PanelLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel7)
                                        .addComponent(cbBookingRoom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel6)
                                        .addComponent(txtNote, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(32, 32, 32)
                                .addGroup(PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel8)
                                        .addComponent(spnQuantityorder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel5)
                                        .addComponent(cbServiceorder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 101, Short.MAX_VALUE)
                                .addGroup(PanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnSaveorder)
                                        .addComponent(btnCancelorder))
                                .addGap(39, 39, 39))
        );

        ordertitle.setFont(new java.awt.Font("Arial", 1, 18));
        ordertitle.setText("Thêm đơn dịch vụ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(Panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(ordertitle)
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(ordertitle)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>

    private void btnSaveorderActionPerformed(java.awt.event.ActionEvent evt) {
        if (!validateForm()) return;

        try {
            BookingRoomDTO selectedBookingRoom = (BookingRoomDTO) cbBookingRoom.getSelectedItem();
            ServiceDTO selectedService = (ServiceDTO) cbServiceorder.getSelectedItem();
            int quantity = (Integer) spnQuantityorder.getValue();
            String note = txtNote.getText().trim();

            double unitPrice = selectedService.getUnitPrice();
            LocalDateTime now = LocalDateTime.now();

            if (editMode && currentOrder != null) {
                // Chế độ sửa
                currentOrder.setBookingRoomId(selectedBookingRoom.getBookingRoomId());
                currentOrder.setServiceId(selectedService.getServiceId());
                currentOrder.setQuantity(quantity);
                currentOrder.setUnitPrice(unitPrice);
                currentOrder.setOrderedAt(now);
                currentOrder.setNote(note);
                resultOrder = currentOrder;
            } else {
                // Chế độ thêm mới
                resultOrder = new ServiceOrderDTO(
                        0,
                        selectedBookingRoom.getBookingRoomId(),
                        selectedService.getServiceId(),
                        quantity,
                        unitPrice,
                        now,
                        null,
                        note
                );
            }

            dispose();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Đã xảy ra lỗi khi lưu đơn dịch vụ: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void btnCancelorderActionPerformed(java.awt.event.ActionEvent evt) {
        resultOrder = null;
        dispose();
    }

    // Variables declaration
    private javax.swing.JPanel Panel;
    private javax.swing.JButton btnCancelorder;
    private javax.swing.JButton btnSaveorder;
    private javax.swing.JComboBox<BookingRoomDTO> cbBookingRoom;
    private javax.swing.JComboBox<ServiceDTO> cbServiceorder;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel ordertitle;
    private javax.swing.JSpinner spnQuantityorder;
    private javax.swing.JTextField txtNote;
    // End of variables declaration
}