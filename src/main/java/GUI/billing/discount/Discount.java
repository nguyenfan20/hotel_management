package GUI.billing.discount;

import BUS.DiscountBUS;
import DTO.DiscountDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class Discount extends JPanel {
    private JTable discountTable;
    private DefaultTableModel tableModel;
    private DiscountBUS discountBUS;
    private JTextField searchField;

    public Discount() {
        discountBUS = new DiscountBUS();
        initComponents();
        loadDiscountData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 240, 245));

        // Top Panel - Search
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(Color.WHITE);

        searchField = new JTextField(20);
        JButton searchButton = new JButton("Tìm kiếm");
        searchButton.setBackground(new Color(52, 152, 219));
        searchButton.setForeground(Color.WHITE);
        searchButton.addActionListener(e -> searchDiscounts());

        JButton refreshButton = new JButton("Làm mới");
        refreshButton.setBackground(new Color(149, 165, 166));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(e -> loadDiscountData());

        topPanel.add(new JLabel("Tìm kiếm:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(refreshButton);

        add(topPanel, BorderLayout.NORTH);

        // Center Panel - Table
        String[] columnNames = {"ID", "Mã", "Loại", "Giá trị", "Chi tiêu tối thiểu", "Chiết khấu tối đa", "Ngày bắt đầu", "Ngày kết thúc", "Giới hạn sử dụng", "Giới hạn mỗi người dùng", "Trạng thái"};  // Thêm cột mới
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        discountTable = new JTable(tableModel);
        discountTable.setRowHeight(30);
        discountTable.getTableHeader().setReorderingAllowed(false);
        discountTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        discountTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editDiscount();
                }
            }
        });

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Sửa");
        JMenuItem deleteItem = new JMenuItem("Xóa");

        editItem.addActionListener(e -> editDiscount());
        deleteItem.addActionListener(e -> deleteDiscount());

        popupMenu.add(editItem);
        popupMenu.add(deleteItem);

        discountTable.setComponentPopupMenu(popupMenu);

        JScrollPane scrollPane = new JScrollPane(discountTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel - Action Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setBackground(Color.WHITE);

        JButton addButton = new JButton("Thêm chiết khấu");
        addButton.setBackground(new Color(52, 152, 219));
        addButton.setForeground(Color.WHITE);
        addButton.setPreferredSize(new Dimension(150, 35));
        addButton.addActionListener(e -> addDiscount());

        bottomPanel.add(addButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadDiscountData() {
        tableModel.setRowCount(0);
        List<DiscountDTO> discounts = discountBUS.getAllDiscounts();

        for (DiscountDTO discount : discounts) {
            Object[] row = {
                    discount.getDiscountId(),
                    discount.getCode(),
                    discount.getDiscountType(),
                    String.format("%.2f", discount.getDiscountValue()),
                    String.format("%.2f", discount.getMinSpend()),
                    String.format("%.2f", discount.getMaxDiscountAmount()),  // Thêm dữ liệu mới
                    discount.getStartDate(),
                    discount.getExpiryDate(),
                    discount.getUsageLimit(),  // Thêm dữ liệu mới
                    discount.getPerUserLimit(),  // Thêm dữ liệu mới
                    discount.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void searchDiscounts() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadDiscountData();
            return;
        }

        tableModel.setRowCount(0);
        List<DiscountDTO> discounts = discountBUS.searchDiscounts(keyword);

        for (DiscountDTO discount : discounts) {
            Object[] row = {
                    discount.getDiscountId(),
                    discount.getCode(),
                    discount.getDiscountType(),
                    String.format("%.2f", discount.getDiscountValue()),
                    String.format("%.2f", discount.getMinSpend()),
                    String.format("%.2f", discount.getMaxDiscountAmount()),  // Thêm dữ liệu mới
                    discount.getStartDate(),
                    discount.getExpiryDate(),
                    discount.getUsageLimit(),  // Thêm dữ liệu mới
                    discount.getPerUserLimit(),  // Thêm dữ liệu mới
                    discount.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void addDiscount() {
        DiscountDetail detailDialog = new DiscountDetail(
                (Frame) SwingUtilities.getWindowAncestor(this),
                null,
                discountBUS
        );
        detailDialog.setVisible(true);
        loadDiscountData();
    }

    private void editDiscount() {
        int selectedRow = discountTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chiết khấu cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int discountId = (int) tableModel.getValueAt(selectedRow, 0);
        DiscountDTO discount = discountBUS.getDiscountById(discountId);

        if (discount != null) {
            DiscountDetail detailDialog = new DiscountDetail(
                    (Frame) SwingUtilities.getWindowAncestor(this),
                    discount,
                    discountBUS
            );
            detailDialog.setVisible(true);
            loadDiscountData();
        }
    }

    private void deleteDiscount() {
        int selectedRow = discountTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chiết khấu cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa chiết khấu này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int discountId = (int) tableModel.getValueAt(selectedRow, 0);
            if (discountBUS.deleteDiscount(discountId)) {
                JOptionPane.showMessageDialog(this, "Xóa chiết khấu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadDiscountData();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa chiết khấu thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}