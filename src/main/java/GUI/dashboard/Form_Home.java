package GUI.dashboard;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import BUS.*;
import DAO.BookingDAO;
import DAO.BookingRoomDAO;
import DTO.BookingDTO;
import DTO.BookingRoomDTO;
import DTO.CustomerDTO;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.awt.Desktop;
import java.net.URI;
import java.util.stream.Collectors;
import javax.swing.JButton;
import javax.swing.BorderFactory;


public class Form_Home extends javax.swing.JPanel {

    private JLabel lblWelcome;
    private JLabel lblDateTime;
    private Timer clockTimer;

    // Stat cards
    private StatCard cardRooms;
    private StatCard cardRevenue;
    private StatCard cardGuests;
    private StatCard cardOccupancy;

    // Chart panels
    private JPanel revenueChartPanel;
    private JPanel bookingChartPanel;

    // Quick stats
    private QuickStatPanel quickStats;

    // Map panel
    private JEditorPane mapPane;

    // BUS instances
    private RoomBUS roomBUS;
    private CustomerBUS customerBUS;
    private StatisticsBUS statisticsBUS;
    private BookingBUS bookingBUS;
    private BookingRoomBUS bookingRoomBUS;

    public Form_Home() {
        roomBUS = new RoomBUS();
        customerBUS = new CustomerBUS();
        statisticsBUS = new StatisticsBUS();
        bookingBUS = new BookingBUS(new BookingDAO());
        bookingRoomBUS = new BookingRoomBUS(new BookingRoomDAO());
        initCustomComponents();
        updateDashboard();
    }

    private ImageIcon loadIcon(String iconName) {
        try {
            String iconPath = "/icon/" + iconName;
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            Image scaledImage = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            System.err.println("Lỗi load icon: " + iconName + " - " + e.getMessage());
        }
        return null;
    }

    private String getTotalRooms() {
        try {
            List<?> rooms = roomBUS.getAllRooms();
            return String.valueOf(rooms != null ? rooms.size() : 0);
        } catch (Exception e) {
            System.err.println("Lỗi lấy tổng phòng: " + e.getMessage());
            return "0";
        }
    }

    private String getMonthlyRevenue() {
        try {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            int currentYear = cal.get(java.util.Calendar.YEAR);
            int currentMonth = cal.get(java.util.Calendar.MONTH) + 1;

            Map<String, Double> revenueByMonth = statisticsBUS.getRevenueByMonth(currentYear);

            if (revenueByMonth == null || revenueByMonth.isEmpty()) {
                System.out.println("Không có dữ liệu doanh thu");
                return "0 VNĐ";
            }

            // Thử nhiều format key khác nhau
            String[] possibleKeys = {
                    "Tháng " + currentMonth,
                    "Thang " + currentMonth,
                    String.valueOf(currentMonth),
                    "T" + currentMonth
            };

            Double revenue = null;
            for (String key : possibleKeys) {
                if (revenueByMonth.containsKey(key)) {
                    revenue = revenueByMonth.get(key);
                    break;
                }
            }

            // Nếu không tìm thấy key nào, log ra các key có sẵn
            if (revenue == null) {
                System.out.println("Các key có sẵn: " + revenueByMonth.keySet());
                System.out.println("Đang tìm tháng: " + currentMonth);

                // Tính tổng tất cả doanh thu trong năm làm fallback
                revenue = statisticsBUS.calculateTotal(revenueByMonth);
                if (revenue > 0) {
                    return String.format("%.1f B VNĐ", revenue / 1_000_000_000);
                }
                return "0 VNĐ";
            }

            // Format số tiền
            if (revenue >= 1_000_000_000) {
                return String.format("%.1f B VNĐ", revenue / 1_000_000_000);
            } else if (revenue >= 1_000_000) {
                return String.format("%.1f M VNĐ", revenue / 1_000_000);
            } else if (revenue >= 1_000) {
                return String.format("%.1f K VNĐ", revenue / 1_000);
            } else {
                return String.format("%.0f VNĐ", revenue);
            }
        } catch (Exception e) {
            System.err.println("Lỗi lấy doanh thu tháng: " + e.getMessage());
            e.printStackTrace();
            return "0 VNĐ";
        }
    }

    private String getTotalCustomers() {
        try {
            List<?> customers = customerBUS.getAllCustomers();
            return String.format("%,d", customers != null ? customers.size() : 0);
        } catch (Exception e) {
            System.err.println("Lỗi lấy tổng khách: " + e.getMessage());
            return "0";
        }
    }

    private String getOccupancyRate() {
        try {
            List<?> allRooms = roomBUS.getAllRooms();
            if (allRooms == null || allRooms.isEmpty()) {
                return "0%";
            }

            int totalRooms = allRooms.size();

            // Đếm số phòng đang được đặt (có booking room active)
            List<BookingRoomDTO> allBookingRooms = bookingRoomBUS.getAllBookingRooms();

            if (allBookingRooms == null || allBookingRooms.isEmpty()) {
                return "0%";
            }

            // Đếm số phòng unique đang được đặt (status không phải "Đã trả" hoặc "Đã hủy")
            long occupiedCount = allBookingRooms.stream()
                    .filter(br -> {
                        String status = br.getStatus();
                        // Chỉ đếm phòng đang active
                        return status != null &&
                                !status.equalsIgnoreCase("Đã trả") &&
                                !status.equalsIgnoreCase("Đã hủy") &&
                                !status.equalsIgnoreCase("CHECKED_OUT");
                    })
                    .map(BookingRoomDTO::getRoomId)  // Lấy room ID
                    .distinct()  // Chỉ đếm mỗi phòng 1 lần
                    .count();

            double occupancyRate = (double) occupiedCount / totalRooms * 100;

//            // Log để debug
//            System.out.println("Tổng phòng: " + totalRooms);
//            System.out.println("Phòng đang đặt: " + occupiedCount);
//            System.out.println("Tỷ lệ: " + occupancyRate + "%");

            return String.format("%.0f%%", occupancyRate);
        } catch (Exception e) {
            System.err.println("Lỗi tính tỷ lệ đặt phòng: " + e.getMessage());
            e.printStackTrace();
            return "0%";
        }
    }

    public void refreshStatCards() {
        try {
            // Cập nhật giá trị cho các card
            if (cardRooms != null) {
                updateStatCard(cardRooms, getTotalRooms());
            }
            if (cardRevenue != null) {
                updateStatCard(cardRevenue, getMonthlyRevenue());
            }
            if (cardGuests != null) {
                updateStatCard(cardGuests, getTotalCustomers());
            }
            if (cardOccupancy != null) {
                updateStatCard(cardOccupancy, getOccupancyRate());
            }
        } catch (Exception e) {
            System.err.println("Lỗi refresh stat cards: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // HELPER METHOD ĐỂ CẬP NHẬT GIÁ TRỊ CARD
    private void updateStatCard(StatCard card, String newValue) {
        if (card != null && newValue != null) {
            // Tìm JLabel value trong card và cập nhật
            for (java.awt.Component comp : card.getComponents()) {
                if (comp instanceof JPanel) {
                    JPanel textPanel = (JPanel) comp;
                    for (java.awt.Component innerComp : textPanel.getComponents()) {
                        if (innerComp instanceof JLabel) {
                            JLabel label = (JLabel) innerComp;
                            // JLabel value có font size 24
                            if (label.getFont().getSize() == 24) {
                                label.setText(newValue);
                                break;
                            }
                        }
                    }
                }
            }
            card.revalidate();
            card.repaint();
        }
    }

    public void updateDashboard() {
        SwingUtilities.invokeLater(() -> {
            refreshStatCards();
        });
    }

    private void initCustomComponents() {
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(240, 242, 245));

        // Main scroll pane
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(new Color(240, 242, 245));
        mainContent.setBorder(new EmptyBorder(25, 25, 25, 25));

        // === HEADER SECTION ===
        mainContent.add(createHeaderSection());
        mainContent.add(Box.createVerticalStrut(25));

        // === STAT CARDS SECTION ===
        mainContent.add(createStatCardsSection());
        mainContent.add(Box.createVerticalStrut(25));

        // === CHARTS AND QUICK STATS SECTION ===
        mainContent.add(createChartsSection());
        mainContent.add(Box.createVerticalStrut(25));

        // === MAP AND ACTIVITY SECTION ===
        mainContent.add(createBottomSection());

        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBar(new ScrollBar());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Left: Welcome message
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        lblWelcome = new JLabel("Chào mừng trở lại!");
        lblWelcome.setIcon(loadIcon("wave.png"));
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblWelcome.setForeground(new Color(30, 30, 30));

        JLabel lblSubtitle = new JLabel("Tổng quan hoạt động khách sạn hôm nay");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(new Color(120, 120, 120));

        leftPanel.add(lblWelcome);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(lblSubtitle);

        // Right: Date time
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);

        lblDateTime = new JLabel();
        lblDateTime.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDateTime.setForeground(new Color(100, 100, 100));
        lblDateTime.setAlignmentX(Component.RIGHT_ALIGNMENT);

        // Refresh button
        JButton btnRefresh = new JButton("Làm mới");
        btnRefresh.setIcon(new ImageIcon(getClass().getResource("/icon/refresh.png")));
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setBackground(new Color(59, 130, 246));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnRefresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRefresh.setAlignmentX(Component.RIGHT_ALIGNMENT);
        btnRefresh.addActionListener(e -> {
            // Animation effect
            btnRefresh.setEnabled(false);
            btnRefresh.setText("Đang tải...");

            new Thread(() -> {
                try {
                    updateDashboard();
                    Thread.sleep(500); // Slight delay for better UX
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                } finally {
                    SwingUtilities.invokeLater(() -> {
                        btnRefresh.setText("Làm mới");
                        btnRefresh.setEnabled(true);
                    });
                }
            }).start();
        });

        rightPanel.add(lblDateTime);
        rightPanel.add(Box.createVerticalStrut(8));
        rightPanel.add(btnRefresh);


        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createStatCardsSection() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 20, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        cardRooms = new StatCard("Tổng số phòng", getTotalRooms(),
                loadIcon("room.png"),
                new Color(99, 102, 241), new Color(139, 92, 246));

        cardRevenue = new StatCard("Doanh thu tháng", getMonthlyRevenue(),
                loadIcon("revenue.png"),
                new Color(236, 72, 153), new Color(239, 68, 68));

        cardGuests = new StatCard("Khách hàng", getTotalCustomers(),
                loadIcon("customer.png"),
                new Color(34, 197, 94), new Color(59, 130, 246));

        cardOccupancy = new StatCard("Tỷ lệ đặt phòng", getOccupancyRate(),
                loadIcon("occupancy.png"),
                new Color(251, 146, 60), new Color(234, 179, 8));

        panel.add(cardRooms);
        panel.add(cardRevenue);
        panel.add(cardGuests);
        panel.add(cardOccupancy);

        return panel;
    }


    private JPanel createChartsSection() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 350));

        // Revenue chart
        revenueChartPanel = createChartPanel("Biểu đồ doanh thu 7 ngày",
                new String[]{"T2", "T3", "T4", "T5", "T6", "T7", "CN"},
                new int[]{320, 450, 380, 520, 490, 610, 580});

        // Booking chart
        bookingChartPanel = createChartPanel("Đặt phòng theo loại",
                new String[]{"Standard", "Deluxe", "Suite", "VIP"},
                new int[]{45, 30, 20, 15});

        panel.add(revenueChartPanel);
        panel.add(bookingChartPanel);

        return panel;
    }

    private JPanel createChartPanel(String title, String[] labels, int[] values) {
        JPanel panel = new RoundedPanel(20);
        panel.setLayout(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(50, 50, 50));
        panel.add(lblTitle, BorderLayout.NORTH);

        // Simple bar chart
        JPanel chartArea = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();
                int barWidth = width / (labels.length * 2);
                int maxValue = 0;
                for (int v : values) if (v > maxValue) maxValue = v;

                Color[] colors = {
                        new Color(99, 102, 241),
                        new Color(236, 72, 153),
                        new Color(34, 197, 94),
                        new Color(251, 146, 60)
                };

                for (int i = 0; i < labels.length; i++) {
                    int barHeight = (int) ((double) values[i] / maxValue * (height - 40));
                    int x = i * width / labels.length + barWidth / 2;
                    int y = height - barHeight - 20;

                    // Draw bar with gradient
                    GradientPaint gradient = new GradientPaint(
                            x, y, colors[i % colors.length],
                            x, y + barHeight, colors[i % colors.length].darker()
                    );
                    g2.setPaint(gradient);
                    g2.fillRoundRect(x, y, barWidth, barHeight, 8, 8);

                    // Draw label
                    g2.setColor(new Color(120, 120, 120));
                    g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    FontMetrics fm = g2.getFontMetrics();
                    int labelWidth = fm.stringWidth(labels[i]);
                    g2.drawString(labels[i], x + (barWidth - labelWidth) / 2, height - 5);
                }
            }
        };
        chartArea.setBackground(Color.WHITE);
        panel.add(chartArea, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomSection() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));

        // Map panel
        JPanel mapPanel = createMapPanel();

        // Activity panel
        JPanel activityPanel = createActivityPanel();

        panel.add(mapPanel);
        panel.add(activityPanel);

        return panel;
    }

    private JPanel createMapPanel() {
        JPanel panel = new RoundedPanel(20);
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Vị trí khách sạn");
        lblTitle.setIcon(new ImageIcon(getClass().getResource("/icon/pin.png")));
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(50, 50, 50));
        panel.add(lblTitle, BorderLayout.NORTH);

        // === DÙNG ẢNH LOCAL ===
        JLabel mapLabel = new JLabel("Đang tải ảnh...", JLabel.CENTER);
        mapLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        mapLabel.setForeground(Color.GRAY);

        // Load ảnh từ resources
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/images/map.png"));
        final Image originalImage = originalIcon.getImage();

        if (originalIcon.getIconWidth() == -1) {
            // Không tìm thấy ảnh
            mapLabel.setText("Không tìm thấy ảnh bản đồ");
            mapLabel.setForeground(Color.RED);
        } else {
            // Load thành công → resize lần đầu
            SwingUtilities.invokeLater(() -> {
                updateMapImage(mapLabel, originalImage, panel.getWidth() - 40);
                mapLabel.setText(null);
            });

            // === TỰ ĐỘNG RESIZE KHI PANEL THAY ĐỔI KÍCH THƯỚC ===
            panel.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    SwingUtilities.invokeLater(() -> {
                        int width = panel.getWidth() - 40;
                        if (width > 100) {
                            updateMapImage(mapLabel, originalImage, width);
                        }
                    });
                }
            });
        }

        // === CLICK MỞ GOOGLE MAPS THẬT ===
        mapLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        mapLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://www.google.com/maps?q=10.7626363,106.6811023"));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Không mở được trình duyệt!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panel.add(mapLabel, BorderLayout.CENTER);

        // === NÚT "CHỈ ĐƯỜNG" ===
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        JLabel lblAddress = new JLabel("<html><b>Địa chỉ:</b> Trường ĐH Sài Gòn, An Dương Vương, Q.5, TP.HCM</html>");
        lblAddress.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblAddress.setForeground(new Color(100, 100, 100));

        JButton btnDirections = new JButton("Chỉ đường");
        btnDirections.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDirections.setForeground(Color.WHITE);
        btnDirections.setBackground(new Color(59, 130, 246));
        btnDirections.setFocusPainted(false);
        btnDirections.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btnDirections.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDirections.addActionListener(e -> {
            try {
                Desktop.getDesktop().browse(new URI("https://www.google.com/maps/dir/?api=1&destination=10.7626363,106.6811023"));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Không mở được bản đồ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        bottomPanel.add(lblAddress, BorderLayout.CENTER);
        bottomPanel.add(btnDirections, BorderLayout.EAST);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void updateMapImage(JLabel label, Image original, int targetWidth) {
        if (original == null || targetWidth <= 0) return;
        int height = (int) (original.getHeight(null) * (targetWidth / (double) original.getWidth(null)));
        Image scaled = original.getScaledInstance(targetWidth, height, Image.SCALE_SMOOTH);
        label.setIcon(new ImageIcon(scaled));
    }

    private JPanel createActivityPanel() {
        JPanel panel = new RoundedPanel(20);
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Hoạt động gần đây");
        lblTitle.setIcon(new ImageIcon(getClass().getResource("/icon/bell.png")));
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(50, 50, 50));
        panel.add(lblTitle, BorderLayout.NORTH);

        JPanel activityList = new JPanel();
        activityList.setLayout(new BoxLayout(activityList, BoxLayout.Y_AXIS));
        activityList.setBackground(Color.WHITE);

        // Lấy 5 hoạt động gần đây nhất
        List<BookingDTO> recentBookings = getRecentActivities();

        if (recentBookings.isEmpty()) {
            JLabel lblEmpty = new JLabel("Chưa có hoạt động nào gần đây");
            lblEmpty.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblEmpty.setForeground(Color.GRAY);
            lblEmpty.setHorizontalAlignment(SwingConstants.CENTER);
            activityList.add(lblEmpty);
        } else {
            for (BookingDTO booking : recentBookings) {
                JPanel item = createActivityItemFromBooking(booking);
                activityList.add(item);
                activityList.add(Box.createVerticalStrut(12));
            }
        }

        JScrollPane scrollPane = new JScrollPane(activityList);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBar(new ScrollBar());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private List<BookingDTO> getRecentActivities() {
        try {
            List<BookingDTO> allBookings = bookingBUS.getAllBookings();
            if (allBookings == null) return new ArrayList<>();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            return allBookings.stream()
                    .filter(b -> b.getBookingDate() != null)
                    .sorted(Comparator.comparing(BookingDTO::getBookingDate).reversed())
                    .limit(5)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Lỗi lấy hoạt động gần đây: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private String getCustomerName(int customerId) {
        try {
            CustomerDTO customer = customerBUS.getCustomerById(customerId);
            return customer != null ? customer.getFull_name() : "Khách #" + customerId;
        } catch (Exception e) {
            return "Khách #" + customerId;
        }
    }

    private JPanel createActivityItemFromBooking(BookingDTO booking) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // ---------- LẤY THÔNG TIN KHÁCH HÀNG ----------
        String customerName = getCustomerName(booking.getCustomerId());

        // ---------- LẤY DANH SÁCH PHÒNG ĐẶT ----------
        List<BookingRoomDTO> bookingRooms = new BookingRoomBUS(new BookingRoomDAO())
                .getBookingRoomsByBooking(booking.getBookingId());

        String roomInfo = "";
        String latestStatus = booking.getStatus();               // mặc định từ Booking
        LocalDateTime latestTime = booking.getBookingDate();    // thời gian mặc định

        if (bookingRooms != null && !bookingRooms.isEmpty()) {
            for (BookingRoomDTO br : bookingRooms) {
                // Cập nhật trạng thái mới nhất
                if (br.getStatus() != null) {
                    latestStatus = switch (br.getStatus()) {
                        case "CHECKED_IN" -> "CHECKED_IN";
                        case "CHECKED_OUT"  -> "CHECKED_OUT";
                        default        -> br.getStatus();
                    };
                }

                // Cập nhật thời gian thực tế (check-in / check-out)
                if (br.getCheckInActual() != null && (latestTime == null || br.getCheckInActual().isAfter(latestTime))) {
                    latestTime = br.getCheckInActual();
                }
                if (br.getCheckOutActual() != null && (latestTime == null || br.getCheckOutActual().isAfter(latestTime))) {
                    latestTime = br.getCheckOutActual();
                }

                // Gộp số phòng
                if (br.getRoomId() > 0) {
                    if (!roomInfo.isEmpty()) roomInfo += ", ";
                    roomInfo += br.getRoomId();
                }
            }
        }

        // ---------- XÁC ĐỊNH ICON ----------
        ImageIcon statusIcon = switch (latestStatus) {
            case "BOOKED"        -> loadIcon("booking_new.png");
            case "CHECKED_IN"   -> loadIcon("checkin.png");
            case "CHECKED_OUT"  -> loadIcon("checkout.png");
            case "PENDING"        -> loadIcon("pending.png");
            case "PAID" -> loadIcon("payment.png");
            default              -> loadIcon("info.png");
        };
        if (statusIcon == null) statusIcon = loadIcon("info.png");

        JLabel lblIcon = new JLabel(statusIcon);
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);

        // ---------- HÀNH ĐỘNG ----------
        String actionText = switch (latestStatus) {
            case "CONFIRMED"        -> customerName + " đặt phòng mới";
            case "CHECKED_IN"   -> customerName + " đã check-in";
            case "CHECKED_OUT"  -> customerName + " đã check-out";
            case "PENDING"        -> customerName + " đang chờ xác nhận";
            case "PAID" -> customerName + " đã thanh toán";
            default              -> customerName + " cập nhật trạng thái";
        };

        // ---------- SUBTITLE ----------
        String subtitle = "Mã: " + booking.getCode();
        if (!roomInfo.isEmpty()) {
            subtitle += " | Phòng: " + roomInfo;
        }

        // ---------- THỜI GIAN ----------
        String timeStr = "N/A";
        if (latestTime != null) {
            timeStr = latestTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        }

        // ---------- TEXT PANEL ----------
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel lblTitle = new JLabel(actionText);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(new Color(50, 50, 50));

        JLabel lblSubtitle = new JLabel(subtitle);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblSubtitle.setForeground(new Color(120, 120, 120));

        textPanel.add(lblTitle);
        textPanel.add(lblSubtitle);

        JLabel lblTime = new JLabel(timeStr);
        lblTime.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTime.setForeground(new Color(150, 150, 150));

        // ---------- LAYOUT ----------
        panel.add(lblIcon, BorderLayout.WEST);
        panel.add(textPanel, BorderLayout.CENTER);
        panel.add(lblTime, BorderLayout.EAST);

        return panel;
    }

    // === CUSTOM COMPONENTS ===

    class StatCard extends JPanel {
        private String title;
        private String value;
        private ImageIcon icon;
        private Color color1;
        private Color color2;

        public StatCard(String title, String value, ImageIcon icon, Color color1, Color color2) {
            this.title = title;
            this.value = value;
            this.icon = icon;
            this.color1 = color1;
            this.color2 = color2;

            setOpaque(false);
            setLayout(new BorderLayout(10, 10));
            setBorder(new EmptyBorder(20, 20, 20, 20));

            JLabel lblIcon = new JLabel(icon);
            lblIcon.setForeground(Color.WHITE);

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setOpaque(false);

            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblTitle.setForeground(new Color(255, 255, 255, 200));

            JLabel lblValue = new JLabel(value);
            lblValue.setFont(new Font("Segoe UI", Font.BOLD, 24));
            lblValue.setForeground(Color.WHITE);

            textPanel.add(lblTitle);
            textPanel.add(Box.createVerticalStrut(5));
            textPanel.add(lblValue);

            add(lblIcon, BorderLayout.WEST);
            add(textPanel, BorderLayout.CENTER);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gradient = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
            g2.setPaint(gradient);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    class RoundedPanel extends JPanel {
        private int radius;

        public RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    class QuickStatPanel extends JPanel {
        // For future expansion
    }
}
