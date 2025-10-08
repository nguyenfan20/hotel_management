package GUI.room;

import util.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.Vector;
import java.util.stream.Collectors;

public class RoomType extends JPanel {
    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color PANEL_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(224, 224, 224);
    private static final Color TEXT_COLOR = new Color(33, 33, 33);

    private JScrollPane scrollPane;
    private JTextField searchField;
    private Vector<Vector<Object>> roomTypeData = new Vector<>();
    private Vector<Vector<Object>> filteredRoomTypeData = new Vector<>();

    public RoomType() {
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        controlPanel.setBackground(PANEL_BG);
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JButton addButton = new JButton("Thêm loại phòng");
        addButton.setPreferredSize(new Dimension(160, 35));
        addButton.setIcon(new ImageIcon(getClass().getResource("/images/add-button.png")));
        addButton.setBackground(PRIMARY_COLOR);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setFont(new Font("Arial", Font.BOLD, 13));
        addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addButton.addActionListener(e -> showAddRoomTypeDialog());
        controlPanel.add(addButton);

        controlPanel.add(Box.createHorizontalStrut(20));

        searchField = new JTextField(15);
        searchField.setPreferredSize(new Dimension(200, 35));
        searchField.setFont(new Font("Arial", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch();
                }
            }
        });
        controlPanel.add(searchField);

        JButton searchButton = createIconButton("/images/search.png");
        searchButton.addActionListener(e -> performSearch());
        controlPanel.add(searchButton);

        JButton filterButton = createIconButton("/images/filter.png");
        filterButton.addActionListener(e -> showFilterDialog());
        controlPanel.add(filterButton);

        add(controlPanel, BorderLayout.NORTH);

        scrollPane = new JScrollPane();
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        add(scrollPane, BorderLayout.CENTER);

        updateTableView();
    }

    private JButton createIconButton(String iconPath) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(35, 35));
        button.setIcon(new ImageIcon(getClass().getResource(iconPath)));
        button.setBackground(PANEL_BG);
        button.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void loadData() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT room_type_id, name, base_price, capacity_adults, capacity_children, bed_count, area, description FROM RoomType");

            roomTypeData.clear();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("room_type_id"));
                row.add(rs.getString("name"));
                row.add(rs.getDouble("base_price"));
                row.add(rs.getInt("capacity_adults"));
                row.add(rs.getInt("capacity_children"));
                row.add(rs.getInt("bed_count"));
                row.add(rs.getDouble("area"));
                row.add(rs.getString("description"));
                roomTypeData.add(row);
            }

            rs.close();
            st.close();
            conn.close();

            filteredRoomTypeData = new Vector<>(roomTypeData);
            updateTableView();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void performSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            filteredRoomTypeData = new Vector<>(roomTypeData);
        } else {
            filteredRoomTypeData = roomTypeData.stream()
                    .filter(row -> {
                        String name = row.get(1) != null ? ((String) row.get(1)).toLowerCase() : "";
                        String basePrice = String.valueOf(row.get(2));
                        String capacityAdults = String.valueOf(row.get(3));
                        String capacityChildren = String.valueOf(row.get(4));
                        String bedCount = String.valueOf(row.get(5));
                        String area = String.valueOf(row.get(6));
                        String description = row.get(7) != null ? ((String) row.get(7)).toLowerCase() : "";
                        return name.contains(query) || basePrice.contains(query) || capacityAdults.contains(query) ||
                                capacityChildren.contains(query) || bedCount.contains(query) || area.contains(query) ||
                                description.contains(query);
                    })
                    .collect(Collectors.toCollection(Vector::new));
        }
        updateTableView();
    }

    private void showFilterDialog() {
        JDialog filterDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Lọc loại phòng", true);
        filterDialog.setLayout(new BorderLayout());
        filterDialog.setSize(350, 180);

        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 15, 15));
        contentPanel.setBackground(PANEL_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel capacityLabel = new JLabel("Sức chứa người lớn:");
        capacityLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JComboBox<String> capacityCombo = new JComboBox<>(new String[]{"Tất cả", "1", "2", "3", "4", "5+"});
        capacityCombo.setPreferredSize(new Dimension(150, 30));
        contentPanel.add(capacityLabel);
        contentPanel.add(capacityCombo);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(PANEL_BG);
        JButton confirmButton = new JButton("Xác nhận");
        confirmButton.setPreferredSize(new Dimension(100, 35));
        confirmButton.setBackground(PRIMARY_COLOR);
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        confirmButton.setBorderPainted(false);
        confirmButton.setFont(new Font("Arial", Font.BOLD, 13));
        confirmButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmButton.addActionListener(e -> {
            String selectedCapacity = (String) capacityCombo.getSelectedItem();
            filterRoomTypes(selectedCapacity);
            filterDialog.dispose();
        });
        buttonPanel.add(confirmButton);

        filterDialog.add(contentPanel, BorderLayout.CENTER);
        filterDialog.add(buttonPanel, BorderLayout.SOUTH);
        filterDialog.setLocationRelativeTo(this);
        filterDialog.setVisible(true);
    }

    private void filterRoomTypes(String capacity) {
        filteredRoomTypeData = roomTypeData.stream()
                .filter(row -> {
                    int capacityAdults = (int) row.get(3);
                    if (capacity.equals("Tất cả")) return true;
                    if (capacity.equals("5+")) return capacityAdults >= 5;
                    return String.valueOf(capacityAdults).equals(capacity);
                })
                .collect(Collectors.toCollection(Vector::new));
        updateTableView();
    }

    private void showAddRoomTypeDialog() {
        JDialog addDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm loại phòng", true);
        addDialog.setLayout(new BorderLayout());
        addDialog.setSize(450, 450);

        JPanel contentPanel = new JPanel(new GridLayout(7, 2, 15, 15));
        contentPanel.setBackground(PANEL_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("Tên loại phòng:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField nameField = new JTextField();

        JLabel basePriceLabel = new JLabel("Giá cơ bản:");
        basePriceLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField basePriceField = new JTextField();

        JLabel capacityAdultsLabel = new JLabel("Người lớn:");
        capacityAdultsLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField capacityAdultsField = new JTextField();

        JLabel capacityChildrenLabel = new JLabel("Trẻ em:");
        capacityChildrenLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField capacityChildrenField = new JTextField();

        JLabel bedCountLabel = new JLabel("Số giường:");
        bedCountLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField bedCountField = new JTextField();

        JLabel areaLabel = new JLabel("Diện tích (m²):");
        areaLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField areaField = new JTextField();

        JLabel descriptionLabel = new JLabel("Mô tả:");
        descriptionLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField descriptionField = new JTextField();

        contentPanel.add(nameLabel);
        contentPanel.add(nameField);
        contentPanel.add(basePriceLabel);
        contentPanel.add(basePriceField);
        contentPanel.add(capacityAdultsLabel);
        contentPanel.add(capacityAdultsField);
        contentPanel.add(capacityChildrenLabel);
        contentPanel.add(capacityChildrenField);
        contentPanel.add(bedCountLabel);
        contentPanel.add(bedCountField);
        contentPanel.add(areaLabel);
        contentPanel.add(areaField);
        contentPanel.add(descriptionLabel);
        contentPanel.add(descriptionField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(PANEL_BG);
        JButton confirmButton = new JButton("Xác nhận");
        confirmButton.setPreferredSize(new Dimension(100, 35));
        confirmButton.setBackground(PRIMARY_COLOR);
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        confirmButton.setBorderPainted(false);
        confirmButton.setFont(new Font("Arial", Font.BOLD, 13));
        confirmButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String basePriceText = basePriceField.getText().trim();
                String capacityAdultsText = capacityAdultsField.getText().trim();
                String capacityChildrenText = capacityChildrenField.getText().trim();
                String bedCountText = bedCountField.getText().trim();
                String areaText = areaField.getText().trim();
                String description = descriptionField.getText().trim();

                if (name.isEmpty() || basePriceText.isEmpty() || capacityAdultsText.isEmpty() ||
                        capacityChildrenText.isEmpty() || bedCountText.isEmpty() || areaText.isEmpty()) {
                    JOptionPane.showMessageDialog(addDialog, "Vui lòng điền đầy đủ các trường bắt buộc!");
                    return;
                }

                double basePrice;
                int capacityAdults, capacityChildren, bedCount;
                double area;
                try {
                    basePrice = Double.parseDouble(basePriceText);
                    capacityAdults = Integer.parseInt(capacityAdultsText);
                    capacityChildren = Integer.parseInt(capacityChildrenText);
                    bedCount = Integer.parseInt(bedCountText);
                    area = Double.parseDouble(areaText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(addDialog, "Giá, sức chứa, số giường, và diện tích phải là số!");
                    return;
                }

                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO RoomType (name, base_price, capacity_adults, capacity_children, bed_count, area, description) VALUES (?, ?, ?, ?, ?, ?, ?)");
                ps.setString(1, name);
                ps.setDouble(2, basePrice);
                ps.setInt(3, capacityAdults);
                ps.setInt(4, capacityChildren);
                ps.setInt(5, bedCount);
                ps.setDouble(6, area);
                ps.setString(7, description.isEmpty() ? null : description);
                ps.executeUpdate();

                ps.close();
                conn.close();

                JOptionPane.showMessageDialog(addDialog, "Thêm loại phòng thành công!");
                loadData();
                addDialog.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(addDialog, "Lỗi khi thêm loại phòng: " + ex.getMessage());
            }
        });
        buttonPanel.add(confirmButton);

        addDialog.add(contentPanel, BorderLayout.CENTER);
        addDialog.add(buttonPanel, BorderLayout.SOUTH);
        addDialog.setLocationRelativeTo(this);
        addDialog.setVisible(true);
    }

    private void showEditRoomTypeDialog(int roomTypeId, String name, double basePrice, int capacityAdults, int capacityChildren, int bedCount, double area, String description) {
        JDialog editDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa loại phòng", true);
        editDialog.setLayout(new BorderLayout());
        editDialog.setSize(450, 450);

        JPanel contentPanel = new JPanel(new GridLayout(7, 2, 15, 15));
        contentPanel.setBackground(PANEL_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel("Tên loại phòng:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField nameField = new JTextField(name);

        JLabel basePriceLabel = new JLabel("Giá cơ bản:");
        basePriceLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField basePriceField = new JTextField(String.valueOf(basePrice));

        JLabel capacityAdultsLabel = new JLabel("Người lớn:");
        capacityAdultsLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField capacityAdultsField = new JTextField(String.valueOf(capacityAdults));

        JLabel capacityChildrenLabel = new JLabel("Sức chứa trẻ em:");
        capacityChildrenLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField capacityChildrenField = new JTextField(String.valueOf(capacityChildren));

        JLabel bedCountLabel = new JLabel("Số giường:");
        bedCountLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField bedCountField = new JTextField(String.valueOf(bedCount));

        JLabel areaLabel = new JLabel("Diện tích (m²):");
        areaLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField areaField = new JTextField(String.valueOf(area));

        JLabel descriptionLabel = new JLabel("Mô tả:");
        descriptionLabel.setFont(new Font("Arial", Font.BOLD, 13));
        JTextField descriptionField = new JTextField(description != null ? description : "");

        contentPanel.add(nameLabel);
        contentPanel.add(nameField);
        contentPanel.add(basePriceLabel);
        contentPanel.add(basePriceField);
        contentPanel.add(capacityAdultsLabel);
        contentPanel.add(capacityAdultsField);
        contentPanel.add(capacityChildrenLabel);
        contentPanel.add(capacityChildrenField);
        contentPanel.add(bedCountLabel);
        contentPanel.add(bedCountField);
        contentPanel.add(areaLabel);
        contentPanel.add(areaField);
        contentPanel.add(descriptionLabel);
        contentPanel.add(descriptionField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(PANEL_BG);
        JButton confirmButton = new JButton("Xác nhận");
        confirmButton.setPreferredSize(new Dimension(100, 35));
        confirmButton.setBackground(PRIMARY_COLOR);
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        confirmButton.setBorderPainted(false);
        confirmButton.setFont(new Font("Arial", Font.BOLD, 13));
        confirmButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmButton.addActionListener(e -> {
            try {
                String newName = nameField.getText().trim();
                String newBasePriceText = basePriceField.getText().trim();
                String newCapacityAdultsText = capacityAdultsField.getText().trim();
                String newCapacityChildrenText = capacityChildrenField.getText().trim();
                String newBedCountText = bedCountField.getText().trim();
                String newAreaText = areaField.getText().trim();
                String newDescription = descriptionField.getText().trim();

                if (newName.isEmpty() || newBasePriceText.isEmpty() || newCapacityAdultsText.isEmpty() ||
                        newCapacityChildrenText.isEmpty() || newBedCountText.isEmpty() || newAreaText.isEmpty()) {
                    JOptionPane.showMessageDialog(editDialog, "Vui lòng điền đầy đủ các trường bắt buộc!");
                    return;
                }

                double newBasePrice;
                int newCapacityAdults, newCapacityChildren, newBedCount;
                double newArea;
                try {
                    newBasePrice = Double.parseDouble(newBasePriceText);
                    newCapacityAdults = Integer.parseInt(newCapacityAdultsText);
                    newCapacityChildren = Integer.parseInt(newCapacityChildrenText);
                    newBedCount = Integer.parseInt(newBedCountText);
                    newArea = Double.parseDouble(newAreaText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(editDialog, "Giá, sức chứa, số giường, và diện tích phải là số!");
                    return;
                }

                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE RoomType SET name = ?, base_price = ?, capacity_adults = ?, capacity_children = ?, bed_count = ?, area = ?, description = ? WHERE room_type_id = ?");
                ps.setString(1, newName);
                ps.setDouble(2, newBasePrice);
                ps.setInt(3, newCapacityAdults);
                ps.setInt(4, newCapacityChildren);
                ps.setInt(5, newBedCount);
                ps.setDouble(6, newArea);
                ps.setString(7, newDescription.isEmpty() ? null : newDescription);
                ps.setInt(8, roomTypeId);
                ps.executeUpdate();

                ps.close();
                conn.close();

                JOptionPane.showMessageDialog(editDialog, "Sửa loại phòng thành công!");
                loadData();
                editDialog.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(editDialog, "Lỗi khi sửa loại phòng: " + ex.getMessage());
            }
        });
        buttonPanel.add(confirmButton);

        editDialog.add(contentPanel, BorderLayout.CENTER);
        editDialog.add(buttonPanel, BorderLayout.SOUTH);
        editDialog.setLocationRelativeTo(this);
        editDialog.setVisible(true);
    }

    private void deleteRoomType(int roomTypeId) {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa loại phòng ID: " + roomTypeId + "?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement("DELETE FROM RoomType WHERE room_type_id = ?");
                ps.setInt(1, roomTypeId);
                ps.executeUpdate();

                ps.close();
                conn.close();

                JOptionPane.showMessageDialog(this, "Xóa loại phòng thành công!");
                loadData();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa loại phòng: " + ex.getMessage());
            }
        }
    }

    private void updateTableView() {
        String[] columnNames = {"ID", "Tên", "Giá", "Người lớn", "Trẻ em", "Số giường", "Diện tích", "Mô tả"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Vector<Object> row : filteredRoomTypeData) {
            model.addRow(row);
        }

        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(TEXT_COLOR);
        table.setShowVerticalLines(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(PANEL_BG);
        table.getTableHeader().setForeground(TEXT_COLOR);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR));
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 250));
                }
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return c;
            }
        });

        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Sửa");
        JMenuItem deleteItem = new JMenuItem("Xóa");
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger() && table.getSelectedRow() != -1) {
                    int rowIndex = table.getSelectedRow();
                    int modelRow = table.convertRowIndexToModel(rowIndex);
                    Vector<Object> row = filteredRoomTypeData.get(modelRow);
                    int roomTypeId = (int) row.get(0);
                    String name = (String) row.get(1);
                    double basePrice = (double) row.get(2);
                    int capacityAdults = (int) row.get(3);
                    int capacityChildren = (int) row.get(4);
                    int bedCount = (int) row.get(5);
                    double area = (double) row.get(6);
                    String description = row.get(7) != null ? (String) row.get(7) : "";

                    editItem.addActionListener(e1 -> showEditRoomTypeDialog(roomTypeId, name, basePrice, capacityAdults, capacityChildren, bedCount, area, description));
                    deleteItem.addActionListener(e1 -> deleteRoomType(roomTypeId));

                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        scrollPane.setViewportView(table);
    }
}
