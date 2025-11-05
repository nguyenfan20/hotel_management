package GUI.statistics;

import BUS.StatisticsBUS;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Panel thống kê doanh thu dịch vụ theo loại dịch vụ - Biểu đồ đường
 */
public class ServiceStatisticsPanel extends JPanel {
    private StatisticsBUS statisticsBUS;
    private JComboBox<String> periodTypeCombo;
    private JComboBox<Integer> yearCombo;
    private JPanel chartPanel;
    private JPanel legendPanel;
    private JLabel totalLabel;

    private Map<String, Double> serviceData;
    private List<Color> chartColors = Arrays.asList(
            new Color(52, 152, 219), new Color(46, 204, 113), new Color(241, 196, 15), new Color(231, 76, 60),
            new Color(155, 89, 182), new Color(26, 188, 156), new Color(230, 126, 34), new Color(149, 165, 166),
            new Color(52, 73, 94), new Color(192, 57, 43), new Color(142, 68, 173), new Color(39, 174, 96)
    );

    public ServiceStatisticsPanel(StatisticsBUS statisticsBUS) {
        this.statisticsBUS = statisticsBUS;
        this.serviceData = new LinkedHashMap<>();
        initComponents();
        loadStatistics();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 240, 245));

        // Top Panel - Filter Options
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(Color.WHITE);

        periodTypeCombo = new JComboBox<>(new String[]{"Tất cả thời gian", "Theo năm"});
        periodTypeCombo.addActionListener(e -> {
            updateYearComboState();
            loadStatistics();
        });

        // Year ComboBox
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Integer[] years = new Integer[5];
        for (int i = 0; i < 5; i++) {
            years[i] = currentYear - i;
        }
        yearCombo = new JComboBox<>(years);
        yearCombo.setEnabled(false);
        yearCombo.addActionListener(e -> loadStatistics());

        JButton refreshButton = new JButton("Làm mới");
        refreshButton.setBackground(new Color(149, 165, 166));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.addActionListener(e -> loadStatistics());

        topPanel.add(new JLabel("Loại thống kê:"));
        topPanel.add(periodTypeCombo);
        topPanel.add(new JLabel("Năm:"));
        topPanel.add(yearCombo);
        topPanel.add(refreshButton);

        add(topPanel, BorderLayout.NORTH);

        // Center Panel
        JPanel centerPanel = new JPanel(new BorderLayout(20, 20));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Chart Panel
        chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawLineChart(g);
            }
        };
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setPreferredSize(new Dimension(600, 400));

        // Legend Panel
        legendPanel = new JPanel();
        legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
        legendPanel.setBackground(Color.WHITE);
        legendPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết theo dịch vụ"));

        JScrollPane legendScroll = new JScrollPane(legendPanel);
        legendScroll.setBorder(BorderFactory.createEmptyBorder());
        legendScroll.setPreferredSize(new Dimension(250, 400));

        centerPanel.add(chartPanel, BorderLayout.CENTER);
        centerPanel.add(legendScroll, BorderLayout.EAST);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel - Summary
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        totalLabel = new JLabel("Tổng doanh thu dịch vụ: 0 VNĐ");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setForeground(new Color(52, 152, 219));

        bottomPanel.add(totalLabel);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void updateYearComboState() {
        String periodType = (String) periodTypeCombo.getSelectedItem();
        yearCombo.setEnabled(periodType.equals("Theo năm"));
    }

    private void loadStatistics() {
        serviceData.clear();
        String periodType = (String) periodTypeCombo.getSelectedItem();

        if (periodType.equals("Tất cả thời gian")) {
            serviceData = statisticsBUS.getRevenueByService();
        } else {
            int year = (int) yearCombo.getSelectedItem();
            serviceData = statisticsBUS.getRevenueByService(year);
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

        int width = chartPanel.getWidth();
        int height = chartPanel.getHeight();

        // Margins
        int marginLeft = 80;
        int marginRight = 40;
        int marginTop = 40;
        int marginBottom = 100;

        int chartWidth = width - marginLeft - marginRight;
        int chartHeight = height - marginTop - marginBottom;

        if (serviceData.isEmpty()) {
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            String message = "Không có dữ liệu";
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(message, (width - fm.stringWidth(message)) / 2, height / 2);
            return;
        }

        // Find max value
        double maxValue = serviceData.values().stream().mapToDouble(Double::doubleValue).max().orElse(0);
        if (maxValue == 0) maxValue = 1;

        // Add some padding to max value for better visualization
        maxValue = maxValue * 1.1;

        // Draw axes
        g2d.setColor(Color.GRAY);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(marginLeft, marginTop, marginLeft, marginTop + chartHeight); // Y-axis
        g2d.drawLine(marginLeft, marginTop + chartHeight, marginLeft + chartWidth, marginTop + chartHeight); // X-axis

        // Draw grid lines and Y-axis labels
        g2d.setColor(new Color(220, 220, 220));
        g2d.setStroke(new BasicStroke(1));
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));

        int gridLines = 5;
        for (int i = 0; i <= gridLines; i++) {
            int y = marginTop + (chartHeight * i / gridLines);
            g2d.drawLine(marginLeft, y, marginLeft + chartWidth, y);

            // Y-axis label
            double value = maxValue * (gridLines - i) / gridLines;
            String label = String.format("%,.0f", value);
            FontMetrics fm = g2d.getFontMetrics();
            g2d.setColor(Color.BLACK);
            g2d.drawString(label, marginLeft - fm.stringWidth(label) - 5, y + 5);
            g2d.setColor(new Color(220, 220, 220));
        }

        // Calculate points
        int dataCount = serviceData.size();
        if (dataCount < 2) {
            // If only one data point, draw as a single point with marker
            if (dataCount == 1) {
                Map.Entry<String, Double> entry = serviceData.entrySet().iterator().next();
                int x = marginLeft + chartWidth / 2;
                int y = marginTop + chartHeight - (int) ((entry.getValue() / maxValue) * chartHeight);

                g2d.setColor(chartColors.get(0));
                g2d.fillOval(x - 6, y - 6, 12, 12);
                g2d.setColor(chartColors.get(0).darker());
                g2d.setStroke(new BasicStroke(2));
                g2d.drawOval(x - 6, y - 6, 12, 12);

                // Draw label
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                String label = entry.getKey();
                Graphics2D g2dRotated = (Graphics2D) g2d.create();
                g2dRotated.translate(x, marginTop + chartHeight + 15);
                g2dRotated.rotate(Math.toRadians(-45));
                g2dRotated.drawString(label, 0, 0);
                g2dRotated.dispose();
            }
            return;
        }

        int[] xPoints = new int[dataCount];
        int[] yPoints = new int[dataCount];

        int segmentWidth = chartWidth / (dataCount - 1);
        int index = 0;

        for (Map.Entry<String, Double> entry : serviceData.entrySet()) {
            double value = entry.getValue();

            xPoints[index] = marginLeft + (index * segmentWidth);
            yPoints[index] = marginTop + chartHeight - (int) ((value / maxValue) * chartHeight);

            index++;
        }

        // Draw line
        g2d.setColor(new Color(52, 152, 219));
        g2d.setStroke(new BasicStroke(3));
        for (int i = 0; i < dataCount - 1; i++) {
            g2d.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
        }

        // Draw area under the line (gradient fill)
        int[] areaXPoints = new int[dataCount + 2];
        int[] areaYPoints = new int[dataCount + 2];

        System.arraycopy(xPoints, 0, areaXPoints, 0, dataCount);
        System.arraycopy(yPoints, 0, areaYPoints, 0, dataCount);
        areaXPoints[dataCount] = xPoints[dataCount - 1];
        areaYPoints[dataCount] = marginTop + chartHeight;
        areaXPoints[dataCount + 1] = xPoints[0];
        areaYPoints[dataCount + 1] = marginTop + chartHeight;

        GradientPaint gradient = new GradientPaint(
                0, marginTop, new Color(52, 152, 219, 100),
                0, marginTop + chartHeight, new Color(52, 152, 219, 20)
        );
        g2d.setPaint(gradient);
        g2d.fillPolygon(areaXPoints, areaYPoints, dataCount + 2);

        // Draw points and labels
        index = 0;
        for (Map.Entry<String, Double> entry : serviceData.entrySet()) {
            int x = xPoints[index];
            int y = yPoints[index];

            // Draw point
            g2d.setColor(new Color(52, 152, 219));
            g2d.fillOval(x - 6, y - 6, 12, 12);
            g2d.setColor(new Color(41, 128, 185));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(x - 6, y - 6, 12, 12);

            // Draw value label above point
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 10));
            String valueLabel = String.format("%,.0f", entry.getValue());
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(valueLabel, x - fm.stringWidth(valueLabel) / 2, y - 10);

            // Draw X-axis label (service name)
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            String label = entry.getKey();

            Graphics2D g2dRotated = (Graphics2D) g2d.create();
            g2dRotated.translate(x, marginTop + chartHeight + 15);
            g2dRotated.rotate(Math.toRadians(-45));
            g2dRotated.drawString(label, 0, 0);
            g2dRotated.dispose();

            index++;
        }

        // Draw chart title
        g2d.setColor(new Color(52, 152, 219));
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        String title = "Biểu đồ doanh thu dịch vụ";
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(title, (width - fm.stringWidth(title)) / 2, 25);
    }

    private void updateLegend() {
        legendPanel.removeAll();
        double total = statisticsBUS.calculateTotal(serviceData);

        int colorIndex = 0;
        for (Map.Entry<String, Double> entry : serviceData.entrySet()) {
            JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            itemPanel.setBackground(Color.WHITE);
            itemPanel.setMaximumSize(new Dimension(250, 50));

            JPanel colorBox = new JPanel();
            colorBox.setPreferredSize(new Dimension(20, 20));
            colorBox.setBackground(chartColors.get(colorIndex % chartColors.size()));
            colorBox.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            double percentage = statisticsBUS.calculatePercentage(entry.getValue(), total);
            JLabel label = new JLabel(String.format(
                    "<html><b>%s</b><br/>%,.0f VNĐ (%.1f%%)</html>",
                    entry.getKey(), entry.getValue(), percentage
            ));
            label.setFont(new Font("Arial", Font.PLAIN, 11));

            itemPanel.add(colorBox);
            itemPanel.add(label);
            legendPanel.add(itemPanel);
            colorIndex++;
        }

        legendPanel.revalidate();
        legendPanel.repaint();
    }

    public void refreshData() {
        loadStatistics();
    }
}