package GUI.room;

import BUS.RoomTypeBUS;
import BUS.AmenityBUS;
import DTO.RoomTypeDTO;
import DTO.AmenityDTO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class RoomType extends JPanel {
    private static final Color PRIMARY_COLOR = new Color(41, 98, 255);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color PANEL_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(224, 224, 224);
    private static final Color TEXT_COLOR = new Color(33, 33, 33);

    private JScrollPane scrollPane;
    private JTextField searchField;
    private List<RoomTypeDTO> roomTypeData = new ArrayList<>();
    private List<RoomTypeDTO> filteredRoomTypeData = new ArrayList<>();
    private RoomTypeBUS roomTypeBUS;
    private AmenityBUS amenityBUS;

    public RoomType() {
        roomTypeBUS = new RoomTypeBUS();
        amenityBUS = new AmenityBUS();
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
            roomTypeData = roomTypeBUS.getAllRoomTypes();
            filteredRoomTypeData = new ArrayList<>(roomTypeData);
            updateTableView();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu loại phòng: " + e.getMessage());
        }
    }

    private void performSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            filteredRoomTypeData = new ArrayList<>(roomTypeData);
        } else {
            filteredRoomTypeData = roomTypeData.stream()
                    .filter(roomType -> {
                        String name = roomType.getName() != null ? roomType.getName().toLowerCase() : "";
                        String basePrice = String.valueOf(roomType.getBasePrice());
                        String capacityAdults = String.valueOf(roomType.getCapacityAdults());
                        String capacityChildren = String.valueOf(roomType.getCapacityChildren());
                        String bedCount = String.valueOf(roomType.getBedCount());
                        String area = String.valueOf(roomType.getArea());
                        String description = roomType.getDescription() != null ? roomType.getDescription().toLowerCase() : "";
                        return name.contains(query) || basePrice.contains(query) || capacityAdults.contains(query) ||
                                capacityChildren.contains(query) || bedCount.contains(query) || area.contains(query) ||
                                description.contains(query);
                    })
                    .collect(Collectors.toList());
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
                .filter(roomType -> {
                    int capacityAdults = roomType.getCapacityAdults();
                    if (capacity.equals("Tất cả")) return true;
                    if (capacity.equals("5+")) return capacityAdults >= 5;
                    return String.valueOf(capacityAdults).equals(capacity);
                })
                .collect(Collectors.toList());
        updateTableView();
    }

    private void showAddRoomTypeDialog() {
        JDialog addDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm loại phòng", true);
        addDialog.setLayout(new BorderLayout());
        addDialog.setSize(500, 550);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(PANEL_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel nameLabel = new JLabel("Tên loại phòng:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        contentPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField nameField = new JTextField();
        contentPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel basePriceLabel = new JLabel("Giá cơ bản:");
        basePriceLabel.setFont(new Font("Arial", Font.BOLD, 13));
        contentPanel.add(basePriceLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField basePriceField = new JTextField();
        contentPanel.add(basePriceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        JLabel capacityAdultsLabel = new JLabel("Người lớn:");
        capacityAdultsLabel.setFont(new Font("Arial", Font.BOLD, 13));
        contentPanel.add(capacityAdultsLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField capacityAdultsField = new JTextField();
        contentPanel.add(capacityAdultsField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        JLabel capacityChildrenLabel = new JLabel("Trẻ em:");
        capacityChildrenLabel.setFont(new Font("Arial", Font.BOLD, 13));
        contentPanel.add(capacityChildrenLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField capacityChildrenField = new JTextField();
        contentPanel.add(capacityChildrenField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.3;
        JLabel bedCountLabel = new JLabel("Số giường:");
        bedCountLabel.setFont(new Font("Arial", Font.BOLD, 13));
        contentPanel.add(bedCountLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField bedCountField = new JTextField();
        contentPanel.add(bedCountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.3;
        JLabel areaLabel = new JLabel("Diện tích (m²):");
        areaLabel.setFont(new Font("Arial", Font.BOLD, 13));
        contentPanel.add(areaLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField areaField = new JTextField();
        contentPanel.add(areaField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.3;
        JLabel descriptionLabel = new JLabel("Mô tả:");
        descriptionLabel.setFont(new Font("Arial", Font.BOLD, 13));
        contentPanel.add(descriptionLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField descriptionField = new JTextField();
        contentPanel.add(descriptionField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 0.3;
        JLabel amenitiesLabel = new JLabel("Tiện nghi:");
        amenitiesLabel.setFont(new Font("Arial", Font.BOLD, 13));
        contentPanel.add(amenitiesLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        List<AmenityDTO> allAmenities = amenityBUS.getAllAmenities();
        JPanel amenitiesPanel = new JPanel();
        amenitiesPanel.setLayout(new BoxLayout(amenitiesPanel, BoxLayout.Y_AXIS));
        amenitiesPanel.setBackground(PANEL_BG);

        List<JCheckBox> amenityCheckboxes = new ArrayList<>();
        for (AmenityDTO amenity : allAmenities) {
            JCheckBox checkbox = new JCheckBox(amenity.getName());
            checkbox.putClientProperty("amenityId", amenity.getAmenityId());
            amenityCheckboxes.add(checkbox);
            amenitiesPanel.add(checkbox);
        }

        JScrollPane amenitiesScroll = new JScrollPane(amenitiesPanel);
        amenitiesScroll.setPreferredSize(new Dimension(250, 100));
        contentPanel.add(amenitiesScroll, gbc);

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

                BigDecimal basePrice;
                byte capacityAdults, capacityChildren, bedCount;
                BigDecimal area;
                try {
                    basePrice = new BigDecimal(basePriceText);
                    capacityAdults = Byte.parseByte(capacityAdultsText);
                    capacityChildren = Byte.parseByte(capacityChildrenText);
                    bedCount = Byte.parseByte(bedCountText);
                    area = new BigDecimal(areaText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(addDialog, "Giá, sức chứa, số giường, và diện tích phải là số hợp lệ!");
                    return;
                }

                List<Integer> selectedAmenityIds = new ArrayList<>();
                for (JCheckBox checkbox : amenityCheckboxes) {
                    if (checkbox.isSelected()) {
                        selectedAmenityIds.add((Integer) checkbox.getClientProperty("amenityId"));
                    }
                }

                RoomTypeDTO newRoomType = new RoomTypeDTO(name, basePrice,
                        capacityAdults, capacityChildren, bedCount,
                        area, description.isEmpty() ? null : description);

                boolean success = roomTypeBUS.addRoomType(newRoomType, selectedAmenityIds);

                if (success) {
                    JOptionPane.showMessageDialog(addDialog, "Thêm loại phòng thành công!");
                    loadData();
                    addDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(addDialog, "Không thể thêm loại phòng!");
                }
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

    private void showEditRoomTypeDialog(RoomTypeDTO roomType) {
        JDialog editDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sửa loại phòng", true);
        editDialog.setLayout(new BorderLayout());
        editDialog.setSize(500, 550);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(PANEL_BG);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel nameLabel = new JLabel("Tên loại phòng:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 13));
        contentPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField nameField = new JTextField(roomType.getName());
        contentPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel basePriceLabel = new JLabel("Giá cơ bản:");
        basePriceLabel.setFont(new Font("Arial", Font.BOLD, 13));
        contentPanel.add(basePriceLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField basePriceField = new JTextField(String.valueOf(roomType.getBasePrice()));
        contentPanel.add(basePriceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        JLabel capacityAdultsLabel = new JLabel("Người lớn:");
        capacityAdultsLabel.setFont(new Font("Arial", Font.BOLD, 13));
        contentPanel.add(capacityAdultsLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField capacityAdultsField = new JTextField(String.valueOf(roomType.getCapacityAdults()));
        contentPanel.add(capacityAdultsField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.3;
        JLabel capacityChildrenLabel = new JLabel("Trẻ em:");
        capacityChildrenLabel.setFont(new Font("Arial", Font.BOLD, 13));
        contentPanel.add(capacityChildrenLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField capacityChildrenField = new JTextField(String.valueOf(roomType.getCapacityChildren()));
        contentPanel.add(capacityChildrenField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.3;
        JLabel bedCountLabel = new JLabel("Số giường:");
        bedCountLabel.setFont(new Font("Arial", Font.BOLD, 13));
        contentPanel.add(bedCountLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField bedCountField = new JTextField(String.valueOf(roomType.getBedCount()));
        contentPanel.add(bedCountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.3;
        JLabel areaLabel = new JLabel("Diện tích (m²):");
        areaLabel.setFont(new Font("Arial", Font.BOLD, 13));
        contentPanel.add(areaLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField areaField = new JTextField(String.valueOf(roomType.getArea()));
        contentPanel.add(areaField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.3;
        JLabel descriptionLabel = new JLabel("Mô tả:");
        descriptionLabel.setFont(new Font("Arial", Font.BOLD, 13));
        contentPanel.add(descriptionLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField descriptionField = new JTextField(roomType.getDescription() != null ? roomType.getDescription() : "");
        contentPanel.add(descriptionField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 0.3;
        JLabel amenitiesLabel = new JLabel("Tiện nghi:");
        amenitiesLabel.setFont(new Font("Arial", Font.BOLD, 13));
        contentPanel.add(amenitiesLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        List<AmenityDTO> allAmenities = amenityBUS.getAllAmenities();
        JPanel amenitiesPanel = new JPanel();
        amenitiesPanel.setLayout(new BoxLayout(amenitiesPanel, BoxLayout.Y_AXIS));
        amenitiesPanel.setBackground(PANEL_BG);

        List<JCheckBox> amenityCheckboxes = new ArrayList<>();
        for (AmenityDTO amenity : allAmenities) {
            JCheckBox checkbox = new JCheckBox(amenity.getName());
            checkbox.putClientProperty("amenityId", amenity.getAmenityId());
            amenityCheckboxes.add(checkbox);
            amenitiesPanel.add(checkbox);
        }

        JScrollPane amenitiesScroll = new JScrollPane(amenitiesPanel);
        amenitiesScroll.setPreferredSize(new Dimension(250, 100));
        contentPanel.add(amenitiesScroll, gbc);

        List<Integer> currentAmenityIds = roomType.getAmenities().stream()
                .map(AmenityDTO::getAmenityId)
                .collect(Collectors.toList());

        for (JCheckBox checkbox : amenityCheckboxes) {
            Integer amenityId = (Integer) checkbox.getClientProperty("amenityId");
            if (currentAmenityIds.contains(amenityId)) {
                checkbox.setSelected(true);
            }
        }

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

                BigDecimal newBasePrice;
                byte newCapacityAdults, newCapacityChildren, newBedCount;
                BigDecimal newArea;
                try {
                    newBasePrice = new BigDecimal(newBasePriceText);
                    newCapacityAdults = Byte.parseByte(newCapacityAdultsText);
                    newCapacityChildren = Byte.parseByte(newCapacityChildrenText);
                    newBedCount = Byte.parseByte(newBedCountText);
                    newArea = new BigDecimal(newAreaText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(editDialog, "Giá, sức chứa, số giường, và diện tích phải là số hợp lệ!");
                    return;
                }

                List<Integer> selectedAmenityIds = new ArrayList<>();
                for (JCheckBox checkbox : amenityCheckboxes) {
                    if (checkbox.isSelected()) {
                        selectedAmenityIds.add((Integer) checkbox.getClientProperty("amenityId"));
                    }
                }

                RoomTypeDTO updatedRoomType = new RoomTypeDTO(roomType.getRoomTypeId(), newName, newBasePrice,
                        newCapacityAdults, newCapacityChildren, newBedCount,
                        newArea, newDescription.isEmpty() ? null : newDescription);

                boolean success = roomTypeBUS.updateRoomType(updatedRoomType, selectedAmenityIds);

                if (success) {
                    JOptionPane.showMessageDialog(editDialog, "Sửa loại phòng thành công!");
                    loadData();
                    editDialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(editDialog, "Không thể sửa loại phòng!");
                }
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
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa loại phòng ID: " + roomTypeId + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = roomTypeBUS.deleteRoomType(roomTypeId);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Xóa loại phòng thành công!");
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa loại phòng!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa loại phòng: " + ex.getMessage());
            }
        }
    }

    private void updateTableView() {
        String[] columnNames = {"ID", "Tên", "Giá", "Người lớn", "Trẻ em", "Số giường", "Diện tích", "Tiện nghi"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (RoomTypeDTO roomType : filteredRoomTypeData) {
            String amenitiesStr = roomType.getAmenities().stream()
                    .map(AmenityDTO::getName)
                    .collect(Collectors.joining(", "));

            Object[] row = {
                    roomType.getRoomTypeId(),
                    roomType.getName(),
                    roomType.getBasePrice(),
                    roomType.getCapacityAdults(),
                    roomType.getCapacityChildren(),
                    roomType.getBedCount(),
                    roomType.getArea(),
                    amenitiesStr.isEmpty() ? "Không có" : amenitiesStr
            };
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
                    RoomTypeDTO roomType = filteredRoomTypeData.get(modelRow);

                    editItem.addActionListener(e1 -> showEditRoomTypeDialog(roomType));
                    deleteItem.addActionListener(e1 -> deleteRoomType(roomType.getRoomTypeId()));

                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        scrollPane.setViewportView(table);
    }
}
