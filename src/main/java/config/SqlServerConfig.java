package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SqlServerConfig {
    
    private static Properties properties = new Properties();
    private static boolean isLoaded = false;
    
    static {
        loadProperties();
    }
    
    /**
     * Load cấu hình từ file database.properties
     */
    private static void loadProperties() {
        try (InputStream input = SqlServerConfig.class.getClassLoader()
                .getResourceAsStream("database.properties")) {
            
            if (input == null) {
                throw new RuntimeException("Không tìm thấy file database.properties");
            }
            
            properties.load(input);
            isLoaded = true;
            
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi đọc file database.properties", e);
        }
    }
    
    public static String getServerName() {
        return properties.getProperty("db.server.name", "localhost");
    }
    
    public static String getPort() {
        return properties.getProperty("db.server.port", "1433");
    }
    
    public static String getDatabaseName() {
        return properties.getProperty("db.database.name", "hotel_management");
    }
    
    public static String getUsername() {
        return properties.getProperty("db.username", "sa");
    }
    
    public static String getPassword() {
        return properties.getProperty("db.password", "123456");
    }
    
    public static boolean isEncrypt() {
        return Boolean.parseBoolean(properties.getProperty("db.encrypt", "false"));
    }
    
    public static boolean isTrustServerCertificate() {
        return Boolean.parseBoolean(properties.getProperty("db.trust.server.certificate", "true"));
    }
    
    public static int getLoginTimeout() {
        return Integer.parseInt(properties.getProperty("db.login.timeout", "30"));
    }
    
    public static int getQueryTimeout() {
        return Integer.parseInt(properties.getProperty("db.query.timeout", "30"));
    }
    
    /**
     * Tạo connection string cho SQL Server
     */
    public static String getConnectionString() {
        return String.format(
            "jdbc:sqlserver://%s:%s;databaseName=%s;encrypt=%s;trustServerCertificate=%s;loginTimeout=%d",
            getServerName(), getPort(), getDatabaseName(), 
            isEncrypt(), isTrustServerCertificate(), getLoginTimeout()
        );
    }
    
    /**
     * Kiểm tra cấu hình kết nối
     */
    public static boolean validateConfig() {
        return isLoaded &&
               getServerName() != null && !getServerName().isEmpty() &&
               getDatabaseName() != null && !getDatabaseName().isEmpty() &&
               getUsername() != null && !getUsername().isEmpty();
    }
    
    /**
     * Hiển thị thông tin cấu hình (ẩn password)
     */
    public static void printConfig() {
        System.out.println("=== SQL Server Configuration ===");
        System.out.println("Server: " + getServerName() + ":" + getPort());
        System.out.println("Database: " + getDatabaseName());
        System.out.println("Username: " + getUsername());
        System.out.println("Password: " + (getPassword().isEmpty() ? "Not set" : "***"));
        System.out.println("Encrypt: " + isEncrypt());
        System.out.println("Trust Certificate: " + isTrustServerCertificate());
        System.out.println("Connection String: " + getConnectionString());
    }
}
