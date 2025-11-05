package DAO;

import util.DatabaseConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class StatisticsDAO {

    // Thống kê doanh thu theo quý trong năm
    private static final String REVENUE_BY_QUARTER =
            "SELECT " +
                    "    DATEPART(QUARTER, i.created_at) AS period, " +
                    "    SUM(i.grand_total) AS revenue " +
                    "FROM Invoice i " +
                    "WHERE YEAR(i.created_at) = ? AND i.status = 'Paid' " +
                    "GROUP BY DATEPART(QUARTER, i.created_at) " +
                    "ORDER BY period";

    // Thống kê doanh thu theo tháng trong năm
    private static final String REVENUE_BY_MONTH =
            "SELECT " +
                    "    MONTH(i.created_at) AS period, " +
                    "    SUM(i.grand_total) AS revenue " +
                    "FROM Invoice i " +
                    "WHERE YEAR(i.created_at) = ? AND i.status = 'Paid' " +
                    "GROUP BY MONTH(i.created_at) " +
                    "ORDER BY period";

    // Thống kê doanh thu theo năm
    private static final String REVENUE_BY_YEAR =
            "SELECT " +
                    "    YEAR(i.created_at) AS period, " +
                    "    SUM(i.grand_total) AS revenue " +
                    "FROM Invoice i " +
                    "WHERE i.status = 'Paid' " +
                    "GROUP BY YEAR(i.created_at) " +
                    "ORDER BY period DESC";

    // Thống kê tỷ lệ đặt phòng theo loại phòng
    private static final String BOOKING_BY_ROOM_TYPE =
            "SELECT " +
                    "    rt.name AS room_type, " +
                    "    COUNT(br.booking_room_id) AS booking_count " +
                    "FROM BookingRoom br " +
                    "JOIN Room r ON br.room_id = r.room_id " +
                    "JOIN RoomType rt ON r.room_type_id = rt.room_type_id " +
                    "WHERE br.is_hide = 0 " +
                    "GROUP BY rt.name " +
                    "ORDER BY booking_count DESC";

    // Thống kê tỷ lệ đặt phòng theo loại phòng trong năm
    private static final String BOOKING_BY_ROOM_TYPE_YEAR =
            "SELECT " +
                    "    rt.name AS room_type, " +
                    "    COUNT(br.booking_room_id) AS booking_count " +
                    "FROM BookingRoom br " +
                    "JOIN Room r ON br.room_id = r.room_id " +
                    "JOIN RoomType rt ON r.room_type_id = rt.room_type_id " +
                    "JOIN Booking b ON br.booking_id = b.booking_id " +
                    "WHERE br.is_hide = 0 AND YEAR(b.booking_date) = ? " +
                    "GROUP BY rt.name " +
                    "ORDER BY booking_count DESC";

    // Thống kê doanh thu dịch vụ theo loại dịch vụ
    private static final String REVENUE_BY_SERVICE =
            "SELECT " +
                    "    s.name AS service_name, " +
                    "    SUM(so.qty * so.unit_price) AS revenue " +
                    "FROM ServiceOrder so " +
                    "JOIN Service s ON so.service_id = s.service_id " +
                    "GROUP BY s.name " +
                    "ORDER BY revenue DESC";

    // Thống kê doanh thu dịch vụ theo loại dịch vụ trong năm
    private static final String REVENUE_BY_SERVICE_YEAR =
            "SELECT " +
                    "    s.name AS service_name, " +
                    "    SUM(so.qty * so.unit_price) AS revenue " +
                    "FROM ServiceOrder so " +
                    "JOIN Service s ON so.service_id = s.service_id " +
                    "WHERE YEAR(so.ordered_at) = ? " +
                    "GROUP BY s.name " +
                    "ORDER BY revenue DESC";

    // Lấy doanh thu theo quý
    public Map<String, Double> getRevenueByQuarter(int year) {
        Map<String, Double> result = new LinkedHashMap<>();

        // Khởi tạo 4 quý với giá trị 0
        for (int i = 1; i <= 4; i++) {
            result.put("Quý " + i, 0.0);
        }

        // Lấy dữ liệu từ database
        try {
            var list = DatabaseConnection.executeQueryList(REVENUE_BY_QUARTER, this::mapToRevenueEntry, year);
            for (var entry : list) {
                result.put("Quý " + entry[0], (Double) entry[1]);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy doanh thu theo quý: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    // Lấy doanh thu theo tháng
    public Map<String, Double> getRevenueByMonth(int year) {
        Map<String, Double> result = new LinkedHashMap<>();

        // Khởi tạo 12 tháng với giá trị 0
        String[] monthNames = {"Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4",
                "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8",
                "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"};
        for (String month : monthNames) {
            result.put(month, 0.0);
        }

        // Lấy dữ liệu từ database
        try {
            var list = DatabaseConnection.executeQueryList(REVENUE_BY_MONTH, this::mapToRevenueEntry, year);
            for (var entry : list) {
                int monthNum = Integer.parseInt(entry[0].toString());
                result.put(monthNames[monthNum - 1], (Double) entry[1]);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy doanh thu theo tháng: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    // Lấy doanh thu theo năm
    public Map<String, Double> getRevenueByYear() {
        Map<String, Double> result = new LinkedHashMap<>();

        try {
            var list = DatabaseConnection.executeQueryList(REVENUE_BY_YEAR, this::mapToRevenueEntry);
            for (var entry : list) {
                result.put("Năm " + entry[0], (Double) entry[1]);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy doanh thu theo năm: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    // Lấy tỷ lệ đặt phòng theo loại phòng
    public Map<String, Double> getBookingByRoomType() {
        Map<String, Double> result = new LinkedHashMap<>();

        try {
            var list = DatabaseConnection.executeQueryList(BOOKING_BY_ROOM_TYPE, this::mapToBookingEntry);
            for (var entry : list) {
                result.put((String) entry[0], (Double) entry[1]);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy thống kê đặt phòng: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    // Lấy tỷ lệ đặt phòng theo loại phòng trong năm
    public Map<String, Double> getBookingByRoomType(int year) {
        Map<String, Double> result = new LinkedHashMap<>();

        try {
            var list = DatabaseConnection.executeQueryList(BOOKING_BY_ROOM_TYPE_YEAR, this::mapToBookingEntry, year);
            for (var entry : list) {
                result.put((String) entry[0], (Double) entry[1]);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy thống kê đặt phòng theo năm: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    // Lấy doanh thu dịch vụ theo loại
    public Map<String, Double> getRevenueByService() {
        Map<String, Double> result = new LinkedHashMap<>();

        try {
            var list = DatabaseConnection.executeQueryList(REVENUE_BY_SERVICE, this::mapToServiceEntry);
            for (var entry : list) {
                result.put((String) entry[0], (Double) entry[1]);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy doanh thu dịch vụ: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    // Lấy doanh thu dịch vụ theo loại trong năm
    public Map<String, Double> getRevenueByService(int year) {
        Map<String, Double> result = new LinkedHashMap<>();

        try {
            var list = DatabaseConnection.executeQueryList(REVENUE_BY_SERVICE_YEAR, this::mapToServiceEntry, year);
            for (var entry : list) {
                result.put((String) entry[0], (Double) entry[1]);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy doanh thu dịch vụ theo năm: " + e.getMessage());
            e.printStackTrace();
        }

        return result;
    }

    // Map ResultSet sang Object[] cho revenue by period (period, revenue)
    private Object[] mapToRevenueEntry(ResultSet rs) throws SQLException {
        return new Object[] {
                rs.getString("period"),
                rs.getDouble("revenue")
        };
    }

    // Map ResultSet sang Object[] cho booking count (room_type, count)
    private Object[] mapToBookingEntry(ResultSet rs) throws SQLException {
        return new Object[] {
                rs.getString("room_type"),
                (double) rs.getInt("booking_count")
        };
    }

    // Map ResultSet sang Object[] cho service revenue (service_name, revenue)
    private Object[] mapToServiceEntry(ResultSet rs) throws SQLException {
        return new Object[] {
                rs.getString("service_name"),
                rs.getDouble("revenue")
        };
    }
}