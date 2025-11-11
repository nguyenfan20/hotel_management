package GUI.statistics;

import BUS.StatisticsBUS;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Panel thống kê dịch vụ với giao diện hiện đại - Biểu đồ đường
 */
public class ServiceStatisticsPanel extends JPanel {
    private StatisticsBUS statisticsBUS;
    private JComboBox<String> periodTypeCombo;
    private JComboBox<Integer> yearCombo;
    private JPanel chartPanel;
    private JPanel legendPanel;
    private JLabel totalLabel;

    private Map<String, Double> serviceData;
    private Map<String, Color> colorMap; // Lưu màu cho từng dịch vụ

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

    public ServiceStatisticsPanel(StatisticsBUS statisticsBUS) {
        this.statisticsBUS = statisticsBUS;
        this.serviceData = new LinkedHashMap<>();
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
                drawLineChart(g);
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

        JLabel legendTitle = new JLabel("Chi tiết theo dịch vụ");
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

        totalLabel = new JLabel("Tổng doanh thu dịch vụ: 0 VNĐ");
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
        serviceData.clear();
        colorMap.clear();

        String periodType = (String) periodTypeCombo.getSelectedItem();

        if (periodType.equals("Tất cả thời gian")) {
            serviceData = statisticsBUS.getRevenueByService();
        } else {
            int year = (int) yearCombo.getSelectedItem();
            serviceData = statisticsBUS.getRevenueByService(year);
        }

        // Gán màu cho từng dịch vụ
        int colorIndex = 0;
        for (String key : serviceData.keySet()) {
            colorMap.put(key, chartColors.get(colorIndex % chartColors.size()));
            colorIndex++;
        }

        updateTotalLabel();
        updateLegend();
        chartPanel.repaint();
    }

    private void updateTotalLabel() {
        double total = statisticsBUS.calculateTotal(serviceData);
        totalLabel.setText(String.format("Tổng doanh thu dịch vụ: %,.0f VNĐ", total));
    }

    private void drawLineChart(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = chartPanel.getWidth();
        int height = chartPanel.getHeight();

        // Margins
        int marginLeft = 80;
        int marginRight = 40;
        int marginTop = 60;
        int marginBottom = 100;

        int chartWidth = width - marginLeft - marginRight;
        int chartHeight = height - marginTop - marginBottom;

        if (serviceData.isEmpty()) {
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
        String title = "Doanh thu dịch vụ";
        FontMetrics titleFm = g2d.getFontMetrics();
        g2d.drawString(title, (width - titleFm.stringWidth(title)) / 2, 30);

        // Find max value
        double maxValue = serviceData.values().stream().mapToDouble(Double::doubleValue).max().orElse(0);
        if (maxValue == 0) maxValue = 1;
        maxValue = maxValue * 1.15; // Add 15% padding

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
            String label = String.format("%,.0f", value);
            FontMetrics fm = g2d.getFontMetrics();
            g2d.setColor(TEXT_SECONDARY);
            g2d.drawString(label, marginLeft - fm.stringWidth(label) - 10, y + 5);
        }

        // Calculate points
        int dataCount = serviceData.size();
        if (dataCount < 1) return;

        if (dataCount == 1) {
            // Draw single point
            Map.Entry<String, Double> entry = serviceData.entrySet().iterator().next();
            int x = marginLeft + chartWidth / 2;
            int y = marginTop + chartHeight - (int) ((entry.getValue() / maxValue) * chartHeight);

            Color pointColor = colorMap.get(entry.getKey());

            // Draw shadow
            g2d.setColor(new Color(0, 0, 0, 30));
            g2d.fillOval(x - 7, y - 5, 14, 14);

            // Draw point
            g2d.setColor(pointColor);
            g2d.fillOval(x - 8, y - 8, 16, 16);
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawOval(x - 8, y - 8, 16, 16);

            // Draw label
            g2d.setColor(TEXT_SECONDARY);
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            String label = entry.getKey();
            Graphics2D g2dRotated = (Graphics2D) g2d.create();
            g2dRotated.translate(x, marginTop + chartHeight + 20);
            g2dRotated.rotate(Math.toRadians(-45));
            g2dRotated.drawString(label, 0, 0);
            g2dRotated.dispose();

            return;
        }

        int[] xPoints = new int[dataCount];
        int[] yPoints = new int[dataCount];

        int segmentWidth = chartWidth / (dataCount - 1);
        int index = 0;

        List<Map.Entry<String, Double>> entries = new ArrayList<>(serviceData.entrySet());
        for (Map.Entry<String, Double> entry : entries) {
            double value = entry.getValue();
            xPoints[index] = marginLeft + (index * segmentWidth);
            yPoints[index] = marginTop + chartHeight - (int) ((value / maxValue) * chartHeight);
            index++;
        }

        // Draw area under line with gradient
        int[] areaXPoints = new int[dataCount + 2];
        int[] areaYPoints = new int[dataCount + 2];

        System.arraycopy(xPoints, 0, areaXPoints, 0, dataCount);
        System.arraycopy(yPoints, 0, areaYPoints, 0, dataCount);
        areaXPoints[dataCount] = xPoints[dataCount - 1];
        areaYPoints[dataCount] = marginTop + chartHeight;
        areaXPoints[dataCount + 1] = xPoints[0];
        areaYPoints[dataCount + 1] = marginTop + chartHeight;

        GradientPaint gradient = new GradientPaint(
                0, marginTop, new Color(59, 130, 246, 100),
                0, marginTop + chartHeight, new Color(59, 130, 246, 10)
        );
        g2d.setPaint(gradient);
        g2d.fillPolygon(areaXPoints, areaYPoints, dataCount + 2);

        // Draw lines between points
        g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < dataCount - 1; i++) {
            Color lineColor = colorMap.get(entries.get(i).getKey());
            g2d.setColor(lineColor);
            g2d.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
        }

        // Draw points and labels
        index = 0;
        for (Map.Entry<String, Double> entry : entries) {
            int x = xPoints[index];
            int y = yPoints[index];
            Color pointColor = colorMap.get(entry.getKey());

            // Draw point shadow
            g2d.setColor(new Color(0, 0, 0, 30));
            g2d.fillOval(x - 7, y - 5, 14, 14);

            // Draw point
            g2d.setColor(pointColor);
            g2d.fillOval(x - 8, y - 8, 16, 16);
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawOval(x - 8, y - 8, 16, 16);

            // Draw value label above point
            if (entry.getValue() > 0) {
                g2d.setColor(TEXT_PRIMARY);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 11));
                String valueLabel = String.format("%,.0f", entry.getValue());
                FontMetrics fm = g2d.getFontMetrics();

                // Background for value
                int labelWidth = fm.stringWidth(valueLabel);
                int labelHeight = fm.getHeight();
                g2d.setColor(new Color(255, 255, 255, 230));
                g2d.fillRoundRect(x - labelWidth/2 - 4, y - labelHeight - 8,
                        labelWidth + 8, labelHeight + 4, 6, 6);
                g2d.setColor(BORDER_COLOR);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(x - labelWidth/2 - 4, y - labelHeight - 8,
                        labelWidth + 8, labelHeight + 4, 6, 6);

                g2d.setColor(TEXT_PRIMARY);
                g2d.drawString(valueLabel, x - labelWidth / 2, y - 10);
            }

            // Draw X-axis label (service name)
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2d.setColor(TEXT_SECONDARY);
            String label = entry.getKey();

            Graphics2D g2dRotated = (Graphics2D) g2d.create();
            g2dRotated.translate(x, marginTop + chartHeight + 20);
            g2dRotated.rotate(Math.toRadians(-45));
            g2dRotated.drawString(label, 0, 0);
            g2dRotated.dispose();

            index++;
        }
    }

    private void updateLegend() {
        legendPanel.removeAll();
        double total = statisticsBUS.calculateTotal(serviceData);

        for (Map.Entry<String, Double> entry : serviceData.entrySet()) {
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

            JLabel valueLabel = new JLabel(String.format("%,.0f VNĐ", entry.getValue()));
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