package BUS;

import DAO.StatisticsDAO;
import java.util.Map;

public class StatisticsBUS {
    private StatisticsDAO statisticsDAO;

    public StatisticsBUS() {
        this.statisticsDAO = new StatisticsDAO();
    }

    /**
     * Lấy doanh thu theo quý trong năm
     * @param year Năm cần thống kê
     * @return Map với key là "Quý 1", "Quý 2",... và value là doanh thu
     */
    public Map<String, Double> getRevenueByQuarter(int year) {
        if (year < 2000 || year > 2100) {
            System.err.println("Năm không hợp lệ");
            return Map.of();
        }
        return statisticsDAO.getRevenueByQuarter(year);
    }

    /**
     * Lấy doanh thu theo tháng trong năm
     * @param year Năm cần thống kê
     * @return Map với key là "Tháng 1", "Tháng 2",... và value là doanh thu
     */
    public Map<String, Double> getRevenueByMonth(int year) {
        if (year < 2000 || year > 2100) {
            System.err.println("Năm không hợp lệ");
            return Map.of();
        }
        return statisticsDAO.getRevenueByMonth(year);
    }

    /**
     * Lấy doanh thu theo năm (tất cả các năm có dữ liệu)
     * @return Map với key là "Năm 2023", "Năm 2024",... và value là doanh thu
     */
    public Map<String, Double> getRevenueByYear() {
        return statisticsDAO.getRevenueByYear();
    }

    /**
     * Lấy tỷ lệ đặt phòng theo loại phòng (tất cả thời gian)
     * @return Map với key là tên loại phòng và value là số lượng đặt phòng
     */
    public Map<String, Double> getBookingByRoomType() {
        return statisticsDAO.getBookingByRoomType();
    }

    /**
     * Lấy tỷ lệ đặt phòng theo loại phòng trong năm
     * @param year Năm cần thống kê
     * @return Map với key là tên loại phòng và value là số lượng đặt phòng
     */
    public Map<String, Double> getBookingByRoomType(int year) {
        if (year < 2000 || year > 2100) {
            System.err.println("Năm không hợp lệ");
            return Map.of();
        }
        return statisticsDAO.getBookingByRoomType(year);
    }

    /**
     * Lấy doanh thu dịch vụ theo loại (tất cả thời gian)
     * @return Map với key là tên dịch vụ và value là doanh thu
     */
    public Map<String, Double> getRevenueByService() {
        return statisticsDAO.getRevenueByService();
    }

    /**
     * Lấy doanh thu dịch vụ theo loại trong năm
     * @param year Năm cần thống kê
     * @return Map với key là tên dịch vụ và value là doanh thu
     */
    public Map<String, Double> getRevenueByService(int year) {
        if (year < 2000 || year > 2100) {
            System.err.println("Năm không hợp lệ");
            return Map.of();
        }
        return statisticsDAO.getRevenueByService(year);
    }

    /**
     * Tính tổng doanh thu từ Map
     * @param data Map chứa dữ liệu thống kê
     * @return Tổng giá trị
     */
    public double calculateTotal(Map<String, Double> data) {
        if (data == null || data.isEmpty()) {
            return 0;
        }
        return data.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    /**
     * Tính phần trăm của một giá trị so với tổng
     * @param value Giá trị cần tính phần trăm
     * @param total Tổng giá trị
     * @return Phần trăm (0-100)
     */
    public double calculatePercentage(double value, double total) {
        if (total == 0) {
            return 0;
        }
        return (value / total) * 100;
    }
}