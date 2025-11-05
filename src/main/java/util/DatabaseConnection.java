package util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import config.SqlServerConfig;

/**
 * Utility class để quản lý kết nối và thực hiện các thao tác DB chung
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

    // Lấy kết nối
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

    // Đóng kết nối
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Đóng PreparedStatement
    public static void closeStatement(PreparedStatement pstmt) {
        if (pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Đóng ResultSet
    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Đóng tất cả
    public static void close(AutoCloseable... closeables) {
        for (AutoCloseable c : closeables) {
            if (c != null) {
                try {
                    c.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // ================== PHƯƠNG THỨC TIỆN ÍCH CHUNG ==================

    /**
     * Thực thi câu lệnh INSERT/UPDATE/DELETE
     */
    public static boolean executeUpdate(String sql, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setParameters(pstmt, params);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi executeUpdate: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Thực thi câu lệnh INSERT và trả về ID sinh tự động
     */
    public static int executeInsert(String sql, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setParameters(pstmt, params);
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return -1;

        } catch (SQLException e) {
            System.err.println("Lỗi executeInsert: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Thực thi truy vấn SELECT trả về 1 đối tượng
     */
    public static <T> T executeQuerySingle(String sql, Function<ResultSet, T> mapper, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setParameters(pstmt, params);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapper.apply(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi executeQuerySingle: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Thực thi truy vấn SELECT trả về danh sách
     */
    public static <T> List<T> executeQueryList(String sql, Function<ResultSet, T> mapper, Object... params) {
        List<T> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setParameters(pstmt, params);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapper.apply(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi executeQueryList: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Thiết lập tham số cho PreparedStatement
     */
    private static void setParameters(PreparedStatement pstmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            if (param == null) {
                pstmt.setNull(i + 1, Types.NULL);
            } else if (param instanceof String) {
                pstmt.setString(i + 1, (String) param);
            } else if (param instanceof Integer) {
                pstmt.setInt(i + 1, (Integer) param);
            } else if (param instanceof Long) {
                pstmt.setLong(i + 1, (Long) param);
            } else if (param instanceof Double) {
                pstmt.setDouble(i + 1, (Double) param);
            } else if (param instanceof java.sql.Date) {
                pstmt.setDate(i + 1, (java.sql.Date) param);
            } else if (param instanceof java.sql.Timestamp) {
                pstmt.setTimestamp(i + 1, (java.sql.Timestamp) param);
            } else if (param instanceof java.math.BigDecimal) {
                pstmt.setBigDecimal(i + 1, (java.math.BigDecimal) param);
            } else if (param instanceof Boolean) {
                pstmt.setBoolean(i + 1, (Boolean) param);
            } else {
                pstmt.setObject(i + 1, param);
            }
        }
    }

    // ================== PHƯƠNG THỨC TEST KẾT NỐI ==================

    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean testConnectionWithDetails() {
        try {
            System.out.println("Đang test kết nối database...");
            SqlServerConfig.printConfig();

            try (Connection conn = getConnection()) {
                boolean isValid = conn != null && !conn.isClosed();
                System.out.println("Kết nối database: " + (isValid ? "THÀNH CÔNG" : "THẤT BẠI"));
                return isValid;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi kết nối database: " + e.getMessage());
            return false;
        }
    }
}