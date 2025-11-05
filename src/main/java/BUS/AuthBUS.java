package BUS;

import DAO.UserAccountDAO;
import DTO.UserAccountDTO;

import java.sql.SQLException;

public class AuthBUS {

    private UserAccountDAO userAccountDAO;

    public AuthBUS() {
        this.userAccountDAO = new UserAccountDAO();
    }

    public UserAccountDTO authenticate(String username, String password) {
            UserAccountDTO user = userAccountDAO.authenticate(username, password);
            return user;
    }

    public String verifyEmailExists(String email) {
        UserAccountDTO user = userAccountDAO.getUserByEmail(email);
        if (user != null) {
            return "Email hợp lệ";
        } else {
            return "Email không tồn tại trong hệ thống!";
        }
    }

    public String resetPassword(String email, String newPassword) {
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
    }
}
