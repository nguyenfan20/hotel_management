package GUI.statistics;

import BUS.StatisticsBUS;

import javax.swing.*;
import java.awt.*;

/**
 * Panel chính cho thống kê với giao diện hiện đại
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

    // Modern color palette
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private static final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(229, 231, 235);

    public Statistics() {
        statisticsBUS = new StatisticsBUS();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Tạo Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Tạo Content Panel với CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

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

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_BACKGROUND);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        // Title
        JLabel titleLabel = new JLabel("Dashboard Thống Kê");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(31, 41, 55));
        titleLabel.setIcon(new ImageIcon(getClass().getResource("/icon/graph.png")));

        // Tab Panel
        JPanel tabPanel = createTabPanel();

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(tabPanel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createTabPanel() {
        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        tabPanel.setBackground(CARD_BACKGROUND);

        String[] tabs = {"Doanh thu", "Đặt phòng", "Dịch vụ"};
        String[] cards = {REVENUE_CARD, BOOKING_CARD, SERVICE_CARD};
        String[] iconPaths = {
                "/icon/money.png",
                "/icon/comp.png",
                "/icon/hotelbell.png"
        };

        ButtonGroup buttonGroup = new ButtonGroup();

        for (int i = 0; i < tabs.length; i++) {
            JToggleButton tabButton = createTabButton(tabs[i], iconPaths[i], cards[i]);
            buttonGroup.add(tabButton);
            tabPanel.add(tabButton);

            if (i == 0) {
                tabButton.setSelected(true);
            }
        }

        return tabPanel;
    }

    private JToggleButton createTabButton(String text, String iconPath, String cardName) {
        JToggleButton button = new JToggleButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));

        try {
            ImageIcon icons = new ImageIcon(getClass().getResource(iconPath));
            Image img = icons.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            button.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            System.err.println("Không tìm thấy icon: " + iconPath);
        }

        button.addActionListener(e -> {
            cardLayout.show(contentPanel, cardName);
            refreshCurrentPanel(cardName);
        });

        button.addChangeListener(e -> {
            if (button.isSelected()) {
                button.setForeground(PRIMARY_COLOR);
                button.setOpaque(true);
                button.setBackground(new Color(219, 234, 254));
            } else {
                button.setForeground(new Color(107, 114, 128));
                button.setOpaque(false);
                button.setBackground(null);
            }
        });

        return button;
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

    public void refreshAll() {
        revenuePanel.refreshData();
        bookingPanel.refreshData();
        servicePanel.refreshData();
    }
}