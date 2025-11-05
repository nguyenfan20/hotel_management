package DAO;

import DTO.RoleDTO;
import util.DatabaseConnection;
import util.ResultSetMapper;
import java.util.List;

public class RoleDAO {

    private static final String SELECT_ALL = "SELECT * FROM Role";
    private static final String INSERT_SQL = "INSERT INTO Role (name, description) VALUES (?, ?)";
    private static final String UPDATE_SQL = "UPDATE Role SET name = ?, description = ? WHERE role_id = ?";
    private static final String DELETE_SQL = "DELETE FROM Role WHERE role_id = ?";

    public List<RoleDTO> getAllRoles() {
        return DatabaseConnection.executeQueryList(SELECT_ALL, this::mapToDTO);
    }

    public boolean addRole(RoleDTO role) {
        return DatabaseConnection.executeUpdate(INSERT_SQL, role.getName(), role.getDescription());
    }

    public boolean updateRole(RoleDTO role) {
        return DatabaseConnection.executeUpdate(UPDATE_SQL, role.getName(), role.getDescription(), role.getRoleId());
    }

    public boolean deleteRole(int roleId) {
        return DatabaseConnection.executeUpdate(DELETE_SQL, roleId);
    }

    private RoleDTO mapToDTO(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new RoleDTO(
                rs.getInt("role_id"),
                rs.getString("name"),
                rs.getString("description")
        );
    }
}