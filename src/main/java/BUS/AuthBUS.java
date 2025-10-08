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
}