package DAO;

import DTO.UserAccountDTO;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserAccountDAO {

    public List<UserAccountDTO> getAllUserAccounts() throws SQLException {
        List<UserAccountDTO> users = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                throw new SQLException("Không thể kết nối đến database!");
            }

            String sql = "SELECT * FROM UserAccount";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            while (rs.next()) {
                UserAccountDTO user = new UserAccountDTO();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setFullName(rs.getString("full_name"));
                user.setPhone(rs.getString("phone"));
                user.setEmail(rs.getString("email"));
                user.setRoleId(rs.getInt("role_id"));
                user.setStatus(rs.getString("status"));
                user.setCreatedAt(rs.getTimestamp("created_at") != null ?
                        rs.getTimestamp("created_at").toLocalDateTime() : null);
                users.add(user);
            }
        } finally {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
            if (conn != null) conn.close();
        }

        return users;
    }

    public boolean addUserAccount(UserAccountDTO user) throws SQLException {
        Connection conn = null;
        PreparedStatement pst = null;
        boolean success = false;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                throw new SQLException("Không thể kết nối đến database!");
            }

            String sql = "INSERT INTO UserAccount (username, password_hash, full_name, phone, email, role_id, status, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            pst = conn.prepareStatement(sql);
            pst.setString(1, user.getUsername());
            pst.setString(2, user.getPasswordHash());
            pst.setString(3, user.getFullName());
            pst.setString(4, user.getPhone());
            pst.setString(5, user.getEmail());
            pst.setInt(6, user.getRoleId());
            pst.setString(7, user.getStatus());
            pst.setTimestamp(8, java.sql.Timestamp.valueOf(LocalDateTime.now()));

            success = pst.executeUpdate() > 0;
        } finally {
            if (pst != null) pst.close();
            if (conn != null) conn.close();
        }

        return success;
    }

    public boolean updateUserAccount(UserAccountDTO user) throws SQLException {
        Connection conn = null;
        PreparedStatement pst = null;
        boolean success = false;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                throw new SQLException("Không thể kết nối đến database!");
            }

            String sql = "UPDATE UserAccount SET username = ?, password_hash = ?, full_name = ?, phone = ?, email = ?, " +
                    "role_id = ?, status = ? WHERE user_id = ?";
            pst = conn.prepareStatement(sql);
            pst.setString(1, user.getUsername());
            pst.setString(2, user.getPasswordHash());
            pst.setString(3, user.getFullName());
            pst.setString(4, user.getPhone());
            pst.setString(5, user.getEmail());
            pst.setInt(6, user.getRoleId());
            pst.setString(7, user.getStatus());
            pst.setInt(8, user.getUserId());

            success = pst.executeUpdate() > 0;
        } finally {
            if (pst != null) pst.close();
            if (conn != null) conn.close();
        }

        return success;
    }

    public boolean deleteUserAccount(int userId) throws SQLException {
        Connection conn = null;
        PreparedStatement pst = null;
        boolean success = false;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                throw new SQLException("Không thể kết nối đến database!");
            }

            String sql = "DELETE FROM UserAccount WHERE user_id = ?";
            pst = conn.prepareStatement(sql);
            pst.setInt(1, userId);

            success = pst.executeUpdate() > 0;
        } finally {
            if (pst != null) pst.close();
            if (conn != null) conn.close();
        }

        return success;
    }

    public UserAccountDTO authenticate(String username, String password) throws SQLException {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        UserAccountDTO user = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                throw new SQLException("Không thể kết nối đến database!");
            }

            String sql = "SELECT * FROM UserAccount WHERE username = ? AND password_hash = ?";
            pst = conn.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);

            rs = pst.executeQuery();
            if (rs.next()) {
                user = new UserAccountDTO();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setFullName(rs.getString("full_name"));
                user.setPhone(rs.getString("phone"));
                user.setEmail(rs.getString("email"));
                user.setRoleId(rs.getInt("role_id"));
                user.setStatus(rs.getString("status"));
                user.setCreatedAt(rs.getTimestamp("created_at") != null ?
                        rs.getTimestamp("created_at").toLocalDateTime() : null);
            }
        } finally {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
            if (conn != null) conn.close();
        }

        return user;
    }
}