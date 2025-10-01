package test;

import util.DatabaseConnection;
import config.SqlServerConfig;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class để test kết nối database
 */
public class DatabaseTest {

    public static void main(String[] args) {
        System.out.println("=== Test Kết Nối Database ===");

        // Hiển thị cấu hình
        SqlServerConfig.printConfig();

        // Test kết nối
        boolean isConnected = DatabaseConnection.testConnectionWithDetails();

        if (isConnected) {
            System.out.println("\n✅ Kết nối database thành công!");
            System.out.println("Bạn có thể bắt đầu sử dụng ứng dụng.");

            // Thực hiện truy vấn mẫu vào bảng users
            try (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT TOP 5 user_id, full_name, email FROM UserAccount")) {
                System.out.println("\n=== Kết quả truy vấn mẫu từ bảng users ===");
                while (rs.next()) {
                    int id = rs.getInt("user_id");
                    String username = rs.getString("full_name");
                    String email = rs.getString("email");
                    System.out.printf("ID: %d, Username: %s, Email: %s%n", id, username, email);
                }
            } catch (SQLException e) {
                System.out.println("\n❌ Lỗi khi thực hiện truy vấn mẫu: " + e.getMessage());
                System.out.println("Vui lòng kiểm tra xem bảng 'users' có tồn tại và có các cột id, username, email");
            }
        } else {
            System.out.println("\n❌ Không thể kết nối database!");
            System.out.println("Vui lòng kiểm tra lại cấu hình trong file database.properties");
        }
    }
}