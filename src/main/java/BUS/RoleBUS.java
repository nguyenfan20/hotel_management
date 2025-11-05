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
        return roleDAO.getAllRoles();
    }

    public boolean addRole(RoleDTO role) {
        if (role.getName() == null || role.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên vai trò không được để trống!");
        }
        return roleDAO.addRole(role);
    }

    public boolean updateRole(RoleDTO role) {
        if (role.getName() == null || role.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên vai trò không được để trống!");
        }
        return roleDAO.updateRole(role);
    }

    public boolean deleteRole(int roleId) {
        // Kiểm tra roleId hợp lệ
        if (roleId <= 0) {
            throw new IllegalArgumentException("ID vai trò không hợp lệ!");
        }
        return roleDAO.deleteRole(roleId);
    }
}