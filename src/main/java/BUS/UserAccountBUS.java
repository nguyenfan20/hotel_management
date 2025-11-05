package BUS;

import DAO.UserAccountDAO;
import DTO.UserAccountDTO;

import java.sql.SQLException;
import java.util.List;

public class UserAccountBUS {

    private UserAccountDAO userAccountDAO;

    public UserAccountBUS() {
        this.userAccountDAO = new UserAccountDAO();
    }

    public List<UserAccountDTO> getAllUserAccounts() {
        return userAccountDAO.getAllUserAccounts();
    }

    public boolean addUserAccount(UserAccountDTO user) {
        return userAccountDAO.addUserAccount(user);
    }

    public boolean updateUserAccount(UserAccountDTO user) {
        if (user.getUserId() <= 0) {
            throw new IllegalArgumentException("ID tài khoản không hợp lệ!");
        }
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống!");
        }
        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên không được để trống!");
        }
        if (user.getRoleId() <= 0) {
            throw new IllegalArgumentException("Vai trò không hợp lệ!");
        }
        if (user.getStatus() == null || user.getStatus().trim().isEmpty()) {
            throw new IllegalArgumentException("Trạng thái không được để trống!");
        }
        return userAccountDAO.updateUserAccount(user);
    }

    public boolean deleteUserAccount(int userId) {
        return userAccountDAO.deleteUserAccount(userId);
    }

    public String getUserFullName(int userId) {
        UserAccountDTO user = userAccountDAO.getUserById(userId);
        return user != null ? user.getFullName() : "N/A";
    }

    public UserAccountDTO getUserById(int userId) {
        return userAccountDAO.getUserById(userId);
    }

    public List<UserAccountDTO> getUsersByRoleId(int roleId) {
        return userAccountDAO.getUsersByRoleId(roleId);
    }
}