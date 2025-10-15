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
        try {
            return userAccountDAO.getAllUserAccounts();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lấy danh sách tài khoản: " + e.getMessage());
        }
    }

    public boolean addUserAccount(UserAccountDTO user) {
        try {
            // Kiểm tra dữ liệu đầu vào
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                throw new IllegalArgumentException("Tên đăng nhập không được để trống!");
            }
            if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
                throw new IllegalArgumentException("Họ tên không được để trống!");
            }
            if (user.getPasswordHash() == null || user.getPasswordHash().trim().isEmpty()) {
                throw new IllegalArgumentException("Mật khẩu không được để trống!");
            }
            if (user.getRoleId() <= 0) {
                throw new IllegalArgumentException("Vai trò không hợp lệ!");
            }
            if (user.getStatus() == null || user.getStatus().trim().isEmpty()) {
                throw new IllegalArgumentException("Trạng thái không được để trống!");
            }
            return userAccountDAO.addUserAccount(user);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi thêm tài khoản: " + e.getMessage());
        }
    }

    public boolean updateUserAccount(UserAccountDTO user) {
        try {
            // Kiểm tra dữ liệu đầu vào
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
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi sửa tài khoản: " + e.getMessage());
        }
    }

    public boolean deleteUserAccount(int userId) {
        try {
            // Kiểm tra userId hợp lệ
            if (userId <= 0) {
                throw new IllegalArgumentException("ID tài khoản không hợp lệ!");
            }
            return userAccountDAO.deleteUserAccount(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi xóa tài khoản: " + e.getMessage());
        }
    }

    public String login(String username, String password) {
        try {
            UserAccountDTO user = userAccountDAO.authenticate(username, password);
            if (user != null) {
                return "Đăng nhập thành công!";
            } else {
                return "Sai tài khoản hoặc mật khẩu!";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

    public String getUserFullName(int userId) {
        try {
            UserAccountDTO user = userAccountDAO.getUserById(userId);
            return user != null ? user.getFullName() : "N/A";
        } catch (SQLException e) {
            e.printStackTrace();
            return "N/A";
        }
    }

    public UserAccountDTO getUserById(int userId) {
        try {
            return userAccountDAO.getUserById(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lấy thông tin người dùng: " + e.getMessage());
        }
    }

    public List<UserAccountDTO> getUsersByRoleId(int roleId) {
        try {
            return userAccountDAO.getUsersByRoleId(roleId);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lấy danh sách người dùng theo vai trò: " + e.getMessage());
        }
    }
}