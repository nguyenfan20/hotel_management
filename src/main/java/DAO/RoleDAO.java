package DAO;

import DTO.RoleDTO;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO {

    public List<RoleDTO> getAllRoles() throws SQLException {
        List<RoleDTO> roles = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                throw new SQLException("Không thể kết nối đến database!");
            }

            String sql = "SELECT * FROM Role";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            while (rs.next()) {
                RoleDTO role = new RoleDTO();
                role.setRoleId(rs.getInt("role_id"));
                role.setName(rs.getString("name"));
                role.setDescription(rs.getString("description"));
                roles.add(role);
            }
        } finally {
            if (rs != null) rs.close();
            if (pst != null) pst.close();
            if (conn != null) conn.close();
        }

        return roles;
    }

    public boolean addRole(RoleDTO role) throws SQLException {
        Connection conn = null;
        PreparedStatement pst = null;
        boolean success = false;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                throw new SQLException("Không thể kết nối đến database!");
            }

            String sql = "INSERT INTO Role (name, description) VALUES (?, ?)";
            pst = conn.prepareStatement(sql);
            pst.setString(1, role.getName());
            pst.setString(2, role.getDescription());

            success = pst.executeUpdate() > 0;
        } finally {
            if (pst != null) pst.close();
            if (conn != null) conn.close();
        }

        return success;
    }

    public boolean updateRole(RoleDTO role) throws SQLException {
        Connection conn = null;
        PreparedStatement pst = null;
        boolean success = false;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                throw new SQLException("Không thể kết nối đến database!");
            }

            String sql = "UPDATE Role SET name = ?, description = ? WHERE role_id = ?";
            pst = conn.prepareStatement(sql);
            pst.setString(1, role.getName());
            pst.setString(2, role.getDescription());
            pst.setInt(3, role.getRoleId());

            success = pst.executeUpdate() > 0;
        } finally {
            if (pst != null) pst.close();
            if (conn != null) conn.close();
        }

        return success;
    }

    public boolean deleteRole(int roleId) throws SQLException {
        Connection conn = null;
        PreparedStatement pst = null;
        boolean success = false;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                throw new SQLException("Không thể kết nối đến database!");
            }

            String sql = "DELETE FROM Role WHERE role_id = ?";
            pst = conn.prepareStatement(sql);
            pst.setInt(1, roleId);

            success = pst.executeUpdate() > 0;
        } finally {
            if (pst != null) pst.close();
            if (conn != null) conn.close();
        }

        return success;
    }
}