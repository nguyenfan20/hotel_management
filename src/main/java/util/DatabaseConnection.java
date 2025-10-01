package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import config.SqlServerConfig;

/**
 * Utility class để quản lý kết nối database SQL Server
 */
public class DatabaseConnection {
    
    private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    
    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("SQL Server JDBC Driver not found", e);
        }
    }
    
    /**
     * Lấy kết nối database
     */
    public static Connection getConnection() throws SQLException {
        if (!SqlServerConfig.validateConfig()) {
            throw new SQLException("Cấu hình database không hợp lệ. Kiểm tra file database.properties");
        }
        
        return DriverManager.getConnection(
            SqlServerConfig.getConnectionString(),
            SqlServerConfig.getUsername(),
            SqlServerConfig.getPassword()
        );
    }
    
    /**
     * Đóng kết nối database
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Test kết nối database
     */
    public static boolean testConnection() {
        try (Connection connection = getConnection()) {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Test kết nối database với thông tin chi tiết
     */
    public static boolean testConnectionWithDetails() {
        try {
            System.out.println("Đang test kết nối database...");
            SqlServerConfig.printConfig();
            
            try (Connection connection = getConnection()) {
                boolean isValid = connection != null && !connection.isClosed();
                System.out.println("Kết nối database: " + (isValid ? "THÀNH CÔNG" : "THẤT BẠI"));
                return isValid;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối database: " + e.getMessage());
            return false;
        }
    }
}
