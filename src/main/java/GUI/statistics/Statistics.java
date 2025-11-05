package GUI.statistics;

import BUS.StatisticsBUS;

import javax.swing.*;
import java.awt.*;

/**
 * Panel chính cho thống kê với JMenuBar để chuyển đổi giữa các loại thống kê
 */
public class Statistics extends JPanel {
    private StatisticsBUS statisticsBUS;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Các panel thống kê con
    private RevenueStatisticsPanel revenuePanel;
    private BookingStatisticsPanel bookingPanel;
    private ServiceStatisticsPanel servicePanel;

    // Tên các card
    private static final String REVENUE_CARD = "REVENUE";
    private static final String BOOKING_CARD = "BOOKING";
    private static final String SERVICE_CARD = "SERVICE";

    public Statistics() {
        statisticsBUS = new StatisticsBUS();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 245));

        // Tạo Menu Bar
        JMenuBar menuBar = createMenuBar();
        add(menuBar, BorderLayout.NORTH);

        // Tạo Content Panel với CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(240, 240, 245));

        // Khởi tạo các panel thống kê
        revenuePanel = new RevenueStatisticsPanel(statisticsBUS);
        bookingPanel = new BookingStatisticsPanel(statisticsBUS);
        servicePanel = new ServiceStatisticsPanel(statisticsBUS);

        // Thêm các panel vào CardLayout
        contentPanel.add(revenuePanel, REVENUE_CARD);
        contentPanel.add(bookingPanel, BOOKING_CARD);
        contentPanel.add(servicePanel, SERVICE_CARD);

        add(contentPanel, BorderLayout.CENTER);

        // Hiển thị panel đầu tiên
        cardLayout.show(contentPanel, REVENUE_CARD);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(52, 152, 219)));

        // Menu Thống kê
        JMenu statisticsMenu = new JMenu("Loại thống kê");
        statisticsMenu.setFont(new Font("Arial", Font.BOLD, 14));
        statisticsMenu.setForeground(new Color(52, 152, 219));

        // Menu items
        JMenuItem revenueItem = createMenuItem("Doanh thu", REVENUE_CARD);
        JMenuItem bookingItem = createMenuItem("Đặt phòng theo loại", BOOKING_CARD);
        JMenuItem serviceItem = createMenuItem("Doanh thu dịch vụ", SERVICE_CARD);

        statisticsMenu.add(revenueItem);
        statisticsMenu.add(bookingItem);
        statisticsMenu.add(serviceItem);

        menuBar.add(statisticsMenu);

        // Thêm label hiển thị panel hiện tại
        JLabel currentPanelLabel = new JLabel("Thống kê: Doanh thu");
        currentPanelLabel.setFont(new Font("Arial", Font.BOLD, 14));
        currentPanelLabel.setForeground(new Color(52, 152, 219));
        currentPanelLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        menuBar.add(Box.createHorizontalStrut(50));
        menuBar.add(currentPanelLabel);

        // Cập nhật label khi chuyển panel
        revenueItem.addActionListener(e -> currentPanelLabel.setText("Thống kê: Doanh thu"));
        bookingItem.addActionListener(e -> currentPanelLabel.setText("Thống kê: Đặt phòng theo loại"));
        serviceItem.addActionListener(e -> currentPanelLabel.setText("Thống kê: Doanh thu dịch vụ"));

        return menuBar;
    }

    private JMenuItem createMenuItem(String text, String cardName) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setFont(new Font("Arial", Font.PLAIN, 13));
        menuItem.addActionListener(e -> {
            cardLayout.show(contentPanel, cardName);
            // Refresh data khi chuyển panel
            refreshCurrentPanel(cardName);
        });
        return menuItem;
    }

    private void refreshCurrentPanel(String cardName) {
        switch (cardName) {
            case REVENUE_CARD:
                revenuePanel.refreshData();
                break;
            case BOOKING_CARD:
                bookingPanel.refreshData();
                break;
            case SERVICE_CARD:
                servicePanel.refreshData();
                break;
        }
    }

    /**
     * Refresh tất cả dữ liệu thống kê
     */
    public void refreshAll() {
        revenuePanel.refreshData();
        bookingPanel.refreshData();
        servicePanel.refreshData();
    }
}