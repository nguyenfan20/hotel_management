package util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import config.SqlServerConfig;

public class DatabaseConnection {

    /**
     * Lấy kết nối từ cấu hình SQL Server
     */
    public static Connection getConnection() {
        try {
            String url = SqlServerConfig.getConnectionString();
            String username = SqlServerConfig.getUsername();
            String password = SqlServerConfig.getPassword();

            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            System.err.println("Không thể kết nối database: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // === UPDATE / DELETE / INSERT (return boolean) ===
    public static boolean executeUpdate(String sql, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            setParameters(pst, params);
            return pst.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi executeUpdate: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // === INSERT + RETURN GENERATED ID ===
    public static int executeInsert(String sql, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setParameters(pst, params);
            if (pst.executeUpdate() > 0) {
                try (ResultSet rs = pst.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi executeInsert: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    // === SELECT LIST (với ResultSetMapper) ===
    public static <T> List<T> executeQueryList(String sql, ResultSetMapper<T> mapper, Object... params) {
        List<T> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            setParameters(pst, params);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(mapper.map(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi executeQueryList: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // === SELECT SINGLE (với ResultSetMapper) ===
    public static <T> T executeQuerySingle(String sql, ResultSetMapper<T> mapper, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            setParameters(pst, params);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapper.map(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Lỗi executeQuerySingle: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // === Helper: Set parameters ===
    private static void setParameters(PreparedStatement pst, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            pst.setObject(i + 1, params[i]);
        }
    }
}