package GUI.statistics;

import BUS.StatisticsBUS;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Panel thống kê đặt phòng với giao diện hiện đại - Biểu đồ cột
 */
public class BookingStatisticsPanel extends JPanel {
    private StatisticsBUS statisticsBUS;
    private JComboBox<String> periodTypeCombo;
    private JComboBox<Integer> yearCombo;
    private JPanel chartPanel;
    private JPanel legendPanel;
    private JLabel totalLabel;

    private Map<String, Double> bookingData;
    private Map<String, Color> colorMap; // Lưu màu cho từng loại phòng

    // Modern color palette
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    private static final Color TEXT_PRIMARY = new Color(31, 41, 55);
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);

    private List<Color> chartColors = Arrays.asList(
            new Color(59, 130, 246),   // Blue
            new Color(16, 185, 129),   // Green
            new Color(249, 115, 22),   // Orange
            new Color(239, 68, 68),    // Red
            new Color(139, 92, 246),   // Purple
            new Color(236, 72, 153),   // Pink
            new Color(14, 165, 233),   // Sky
            new Color(34, 197, 94),    // Emerald
            new Color(251, 146, 60),   // Amber
            new Color(168, 85, 247),   // Violet
            new Color(244, 63, 94),    // Rose
            new Color(6, 182, 212)     // Cyan
    );

    public BookingStatisticsPanel(StatisticsBUS statisticsBUS) {
        this.statisticsBUS = statisticsBUS;
        this.bookingData = new LinkedHashMap<>();
        this.colorMap = new LinkedHashMap<>();
        initComponents();
        loadStatistics();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BACKGROUND_COLOR);

        // Top Panel - Filter Options
        JPanel topPanel = createFilterPanel();
        add(topPanel, BorderLayout.NORTH);

        // Center Panel
        JPanel centerPanel = new JPanel(new BorderLayout(20, 0));
        centerPanel.setBackground(BACKGROUND_COLOR);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Chart Container
        JPanel chartContainer = createCardPanel();
        chartContainer.setLayout(new BorderLayout());
        chartContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBarChart(g);
            }
        };
        chartPanel.setBackground(CARD_BACKGROUND);
        chartPanel.setPreferredSize(new Dimension(650, 400));

        chartContainer.add(chartPanel, BorderLayout.CENTER);

        // Legend Container
        JPanel legendContainer = createCardPanel();
        legendContainer.setLayout(new BorderLayout());
        legendContainer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel legendTitle = new JLabel("Chi tiết theo loại phòng");
        legendTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        legendTitle.setForeground(TEXT_PRIMARY);
        legendTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        legendPanel = new JPanel();
        legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
        legendPanel.setBackground(CARD_BACKGROUND);

        JScrollPane legendScroll = new JScrollPane(legendPanel);
        legendScroll.setBorder(BorderFactory.createEmptyBorder());
        legendScroll.setPreferredSize(new Dimension(280, 400));
        legendScroll.getVerticalScrollBar().setUnitIncrement(16);

        legendContainer.add(legendTitle, BorderLayout.NORTH);
        legendContainer.add(legendScroll, BorderLayout.CENTER);

        centerPanel.add(chartContainer, BorderLayout.CENTER);
        centerPanel.add(legendContainer, BorderLayout.EAST);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel - Summary
        JPanel bottomPanel = createSummaryPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createFilterPanel() {
        JPanel panel = createCardPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        periodTypeCombo = new JComboBox<>(new String[]{"Tất cả thời gian", "Theo năm"});
        periodTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        periodTypeCombo.setPreferredSize(new Dimension(150, 35));
        periodTypeCombo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        periodTypeCombo.addActionListener(e -> {
            updateYearComboState();
            loadStatistics();
        });

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Integer[] years = new Integer[5];
        for (int i = 0; i < 5; i++) {
            years[i] = currentYear - i;
        }
        yearCombo = new JComboBox<>(years);
        yearCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        yearCombo.setPreferredSize(new Dimension(100, 35));
        yearCombo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        yearCombo.setEnabled(false);
        yearCombo.addActionListener(e -> loadStatistics());

        JButton refreshButton = createModernButton("Làm mới");
        refreshButton.addActionListener(e -> loadStatistics());
        refreshButton.setIcon(new ImageIcon(getClass().getResource("/icon/refresh.png")));

        panel.add(createLabel("Loại thống kê:"));
        panel.add(periodTypeCombo);
        panel.add(createLabel("Năm:"));
        panel.add(yearCombo);
        panel.add(refreshButton);

        return panel;
    }

    private JPanel createSummaryPanel() {
        JPanel panel = createCardPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR));

        totalLabel = new JLabel("Tổng số lượt đặt: 0");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        totalLabel.setForeground(PRIMARY_COLOR);

        panel.add(totalLabel);
        return panel;
    }

    private JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD_BACKGROUND);
        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(TEXT_SECONDARY);
        return label;
    }

    private JButton createModernButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 35));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });

        return button;
    }

    private void updateYearComboState() {
        String periodType = (String) periodTypeCombo.getSelectedItem();
        yearCombo.setEnabled(periodType.equals("Theo năm"));
    }

    private void loadStatistics() {
        bookingData.clear();
        colorMap.clear();

        String periodType = (String) periodTypeCombo.getSelectedItem();

        if (periodType.equals("Tất cả thời gian")) {
            bookingData = statisticsBUS.getBookingByRoomType();
        } else {
            int year = (int) yearCombo.getSelectedItem();
            bookingData = statisticsBUS.getBookingByRoomType(year);
        }

        // Gán màu cho từng loại phòng
        int colorIndex = 0;
        for (String key : bookingData.keySet()) {
            colorMap.put(key, chartColors.get(colorIndex % chartColors.size()));
            colorIndex++;
        }

        updateTotalLabel();
        updateLegend();
        chartPanel.repaint();
    }

    private void updateTotalLabel() {
        double total = statisticsBUS.calculateTotal(bookingData);
        totalLabel.setText(String.format("Tổng số lượt đặt: %.0f", total));
    }

    private void drawBarChart(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = chartPanel.getWidth();
        int height = chartPanel.getHeight();

        // Margins
        int marginLeft = 70;
        int marginRight = 40;
        int marginTop = 60;
        int marginBottom = 100;

        int chartWidth = width - marginLeft - marginRight;
        int chartHeight = height - marginTop - marginBottom;

        if (bookingData.isEmpty()) {
            g2d.setColor(TEXT_SECONDARY);
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            String message = "Không có dữ liệu";
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(message, (width - fm.stringWidth(message)) / 2, height / 2);
            return;
        }

        // Draw chart title
        g2d.setColor(TEXT_PRIMARY);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 18));
        String title = "Số lượt đặt phòng theo loại";
        FontMetrics titleFm = g2d.getFontMetrics();
        g2d.drawString(title, (width - titleFm.stringWidth(title)) / 2, 30);

        // Find max value
        double maxValue = bookingData.values().stream().mapToDouble(Double::doubleValue).max().orElse(0);
        if (maxValue == 0) maxValue = 1;
        maxValue = maxValue * 1.1; // Add 10% padding

        // Draw axes
        g2d.setColor(BORDER_COLOR);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(marginLeft, marginTop, marginLeft, marginTop + chartHeight); // Y-axis
        g2d.drawLine(marginLeft, marginTop + chartHeight, marginLeft + chartWidth, marginTop + chartHeight); // X-axis

        // Draw grid lines and Y-axis labels
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        int gridLines = 5;
        for (int i = 0; i <= gridLines; i++) {
            int y = marginTop + (chartHeight * i / gridLines);

            // Grid line
            g2d.setColor(new Color(229, 231, 235, 150));
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0));
            g2d.drawLine(marginLeft, y, marginLeft + chartWidth, y);

            // Y-axis label
            double value = maxValue * (gridLines - i) / gridLines;
            String label = String.format("%.0f", value);
            FontMetrics fm = g2d.getFontMetrics();
            g2d.setColor(TEXT_SECONDARY);
            g2d.drawString(label, marginLeft - fm.stringWidth(label) - 10, y + 5);
        }

        // Draw bars
        int barCount = bookingData.size();
        int barSpacing = 25;
        int totalBarWidth = chartWidth - (barSpacing * (barCount + 1));
        int barWidth = Math.max(40, Math.min(80, totalBarWidth / barCount));

        int x = marginLeft + barSpacing;

        for (Map.Entry<String, Double> entry : bookingData.entrySet()) {
            double value = entry.getValue();
            int barHeight = (int) ((value / maxValue) * chartHeight);
            int barY = marginTop + chartHeight - barHeight;

            Color barColor = colorMap.get(entry.getKey());

            // Draw bar shadow
            g2d.setColor(new Color(0, 0, 0, 20));
            g2d.fillRoundRect(x + 3, barY + 3, barWidth, barHeight, 8, 8);

            // Draw bar with gradient
            GradientPaint gradient = new GradientPaint(
                    x, barY, barColor,
                    x, barY + barHeight, barColor.darker()
            );
            g2d.setPaint(gradient);
            g2d.fillRoundRect(x, barY, barWidth, barHeight, 8, 8);

            // Draw bar border
            g2d.setColor(barColor.darker());
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(x, barY, barWidth, barHeight, 8, 8);

            // Draw value on top of bar
            if (value > 0) {
                g2d.setColor(TEXT_PRIMARY);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
                String valueLabel = String.format("%.0f", value);
                FontMetrics fm = g2d.getFontMetrics();
                int labelX = x + (barWidth - fm.stringWidth(valueLabel)) / 2;
                g2d.drawString(valueLabel, labelX, barY - 8);
            }

            // Draw X-axis label (room type name)
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2d.setColor(TEXT_SECONDARY);
            String label = entry.getKey();

            // Rotate label
            Graphics2D g2dRotated = (Graphics2D) g2d.create();
            g2dRotated.translate(x + barWidth / 2, marginTop + chartHeight + 20);
            g2dRotated.rotate(Math.toRadians(-45));
            g2dRotated.drawString(label, 0, 0);
            g2dRotated.dispose();

            x += barWidth + barSpacing;
        }
    }

    private void updateLegend() {
        legendPanel.removeAll();
        double total = statisticsBUS.calculateTotal(bookingData);

        for (Map.Entry<String, Double> entry : bookingData.entrySet()) {
            JPanel itemPanel = new JPanel(new BorderLayout(12, 0));
            itemPanel.setBackground(CARD_BACKGROUND);
            itemPanel.setMaximumSize(new Dimension(260, 65));
            itemPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

            // Color indicator
            JPanel colorBox = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(colorMap.get(entry.getKey()));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                }
            };
            colorBox.setPreferredSize(new Dimension(8, 45));
            colorBox.setOpaque(false);

            // Info panel
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setBackground(CARD_BACKGROUND);

            double percentage = statisticsBUS.calculatePercentage(entry.getValue(), total);

            JLabel nameLabel = new JLabel(entry.getKey());
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            nameLabel.setForeground(TEXT_PRIMARY);

            JLabel valueLabel = new JLabel(String.format("%.0f lượt đặt", entry.getValue()));
            valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            valueLabel.setForeground(TEXT_SECONDARY);

            JLabel percentLabel = new JLabel(String.format("%.1f%%", percentage));
            percentLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            percentLabel.setForeground(colorMap.get(entry.getKey()));

            infoPanel.add(nameLabel);
            infoPanel.add(Box.createVerticalStrut(3));
            infoPanel.add(valueLabel);
            infoPanel.add(Box.createVerticalStrut(2));
            infoPanel.add(percentLabel);

            itemPanel.add(colorBox, BorderLayout.WEST);
            itemPanel.add(infoPanel, BorderLayout.CENTER);

            legendPanel.add(itemPanel);
            legendPanel.add(Box.createVerticalStrut(5));
        }

        legendPanel.revalidate();
        legendPanel.repaint();
    }

    public void refreshData() {
        loadStatistics();
    }
}