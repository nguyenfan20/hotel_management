package BUS;

import DAO.UserAccountDAO;
import DTO.UserAccountDTO;

import java.sql.SQLException;

public class AuthBUS {

    private UserAccountDAO userAccountDAO;

    public AuthBUS() {
        this.userAccountDAO = new UserAccountDAO();
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

    public String verifyEmailExists(String email) {
        try {
            UserAccountDTO user = userAccountDAO.getUserByEmail(email);
            if (user != null) {
                return "Email hợp lệ";
            } else {
                return "Email không tồn tại trong hệ thống!";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }

    public String resetPassword(String email, String newPassword) {
        try {
            // Verify email exists first
            UserAccountDTO user = userAccountDAO.getUserByEmail(email);
            if (user == null) {
                return "Email không tồn tại trong hệ thống!";
            }

            // Update password (in production, you should hash the password)
            boolean success = userAccountDAO.updatePasswordByEmail(email, newPassword);
            if (success) {
                return "Đặt lại mật khẩu thành công!";
            } else {
                return "Đặt lại mật khẩu thất bại!";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }
}