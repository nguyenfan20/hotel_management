package BUS;

import DAO.RoleDAO;
import DTO.RoleDTO;

import java.sql.SQLException;
import java.util.List;

public class RoleBUS {

    private RoleDAO roleDAO;

    public RoleBUS() {
        this.roleDAO = new RoleDAO();
    }

    public List<RoleDTO> getAllRoles() {
        try {
            return roleDAO.getAllRoles();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lấy danh sách vai trò: " + e.getMessage());
        }
    }

    public boolean addRole(RoleDTO role) {
        try {
            // Kiểm tra dữ liệu đầu vào
            if (role.getName() == null || role.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Tên vai trò không được để trống!");
            }
            return roleDAO.addRole(role);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi thêm vai trò: " + e.getMessage());
        }
    }

    public boolean updateRole(RoleDTO role) {
        try {
            // Kiểm tra dữ liệu đầu vào
            if (role.getName() == null || role.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Tên vai trò không được để trống!");
            }
            return roleDAO.updateRole(role);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi sửa vai trò: " + e.getMessage());
        }
    }

    public boolean deleteRole(int roleId) {
        try {
            // Kiểm tra roleId hợp lệ
            if (roleId <= 0) {
                throw new IllegalArgumentException("ID vai trò không hợp lệ!");
            }
            return roleDAO.deleteRole(roleId);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi xóa vai trò: " + e.getMessage());
        }
    }
}