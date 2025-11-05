package GUI.statistics;

import BUS.StatisticsBUS;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.util.*;
import java.util.List;

/**
 * Panel thống kê doanh thu theo quý/tháng hoặc năm
 */
public class RevenueStatisticsPanel extends JPanel {
    private StatisticsBUS statisticsBUS;
    private JComboBox<String> periodTypeCombo;
    private JComboBox<Integer> yearCombo;
    private JPanel chartPanel;
    private JPanel legendPanel;
    private JLabel totalLabel;

    private Map<String, Double> revenueData;
    private List<Color> chartColors = Arrays.asList(
            new Color(52, 152, 219), new Color(46, 204, 113), new Color(241, 196, 15), new Color(231, 76, 60),
            new Color(155, 89, 182), new Color(26, 188, 156), new Color(230, 126, 34), new Color(149, 165, 166),
            new Color(52, 73, 94), new Color(192, 57, 43), new Color(142, 68, 173), new Color(39, 174, 96)
    );

    public RevenueStatisticsPanel(StatisticsBUS statisticsBUS) {
        this.statisticsBUS = statisticsBUS;
        this.revenueData = new LinkedHashMap<>();
        initComponents();
        loadStatistics();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 240, 245));

        // Top Panel - Filter Options
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBackground(Color.WHITE);

        periodTypeCombo = new JComboBox<>(new String[]{"Theo Quý", "Theo Tháng", "Theo Năm"});
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
                drawPieChart(g);
            }
        };
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setPreferredSize(new Dimension(500, 400));

        // Legend Panel
        legendPanel = new JPanel();
        legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
        legendPanel.setBackground(Color.WHITE);
        legendPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết"));

        JScrollPane legendScroll = new JScrollPane(legendPanel);
        legendScroll.setBorder(BorderFactory.createEmptyBorder());
        legendScroll.setPreferredSize(new Dimension(300, 400));

        centerPanel.add(chartPanel, BorderLayout.CENTER);
        centerPanel.add(legendScroll, BorderLayout.EAST);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel - Summary
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        totalLabel = new JLabel("Tổng doanh thu: 0 VNĐ");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setForeground(new Color(52, 152, 219));

        bottomPanel.add(totalLabel);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void updateYearComboState() {
        String periodType = (String) periodTypeCombo.getSelectedItem();
        yearCombo.setEnabled(!periodType.equals("Theo Năm"));
    }

    private void loadStatistics() {
        revenueData.clear();
        String periodType = (String) periodTypeCombo.getSelectedItem();

        if (periodType.equals("Theo Quý")) {
            int year = (int) yearCombo.getSelectedItem();
            revenueData = statisticsBUS.getRevenueByQuarter(year);
        } else if (periodType.equals("Theo Tháng")) {
            int year = (int) yearCombo.getSelectedItem();
            revenueData = statisticsBUS.getRevenueByMonth(year);
        } else {
            revenueData = statisticsBUS.getRevenueByYear();
        }

        updateTotalLabel();
        updateLegend();
        chartPanel.repaint();
    }

    private void updateTotalLabel() {
        double total = statisticsBUS.calculateTotal(revenueData);
        totalLabel.setText(String.format("Tổng doanh thu: %,.0f VNĐ", total));
    }

    private void drawPieChart(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = chartPanel.getWidth();
        int height = chartPanel.getHeight();
        int diameter = Math.min(width, height) - 100;
        int x = (width - diameter) / 2;
        int y = (height - diameter) / 2;

        double total = statisticsBUS.calculateTotal(revenueData);

        if (total == 0) {
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            String message = "Không có dữ liệu";
            FontMetrics fm = g2d.getFontMetrics();
            g2d.drawString(message, (width - fm.stringWidth(message)) / 2, height / 2);
            return;
        }

        double startAngle = 0;
        int colorIndex = 0;

        for (Map.Entry<String, Double> entry : revenueData.entrySet()) {
            double percentage = statisticsBUS.calculatePercentage(entry.getValue(), total);
            double arcAngle = (entry.getValue() / total) * 360;

            if (arcAngle > 0) {
                Color color = chartColors.get(colorIndex % chartColors.size());
                g2d.setColor(color);

                Arc2D.Double arc = new Arc2D.Double(x, y, diameter, diameter, startAngle, arcAngle, Arc2D.PIE);
                g2d.fill(arc);

                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2));
                g2d.draw(arc);

                if (percentage > 3) {
                    double midAngle = Math.toRadians(startAngle + arcAngle / 2);
                    int labelX = x + diameter / 2 + (int) (diameter / 3 * Math.cos(midAngle));
                    int labelY = y + diameter / 2 - (int) (diameter / 3 * Math.sin(midAngle));

                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Arial", Font.BOLD, 12));
                    String label = String.format("%.1f%%", percentage);
                    FontMetrics fm = g2d.getFontMetrics();
                    g2d.drawString(label, labelX - fm.stringWidth(label) / 2, labelY + fm.getHeight() / 4);
                }

                startAngle += arcAngle;
                colorIndex++;
            }
        }
    }

    private void updateLegend() {
        legendPanel.removeAll();
        double total = statisticsBUS.calculateTotal(revenueData);

        int colorIndex = 0;
        for (Map.Entry<String, Double> entry : revenueData.entrySet()) {
            JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
            itemPanel.setBackground(Color.WHITE);
            itemPanel.setMaximumSize(new Dimension(300, 50));

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