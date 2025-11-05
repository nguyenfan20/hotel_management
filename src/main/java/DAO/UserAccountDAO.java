package DAO;

import DTO.UserAccountDTO;
import util.DatabaseConnection;
import util.ResultSetMapper;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class UserAccountDAO {

    private static final String AUTHENTICATE = "SELECT * FROM UserAccount WHERE username = ? AND status = 'Active'";
    private static final String SELECT_ALL = "SELECT * FROM UserAccount";
    private static final String INSERT_SQL = "INSERT INTO UserAccount (username, password_hash, full_name, phone, email, role_id, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE UserAccount SET username = ?, password_hash = ?, full_name = ?, phone = ?, email = ?, role_id = ?, status = ? WHERE user_id = ?";
    private static final String DELETE_SQL = "DELETE FROM UserAccount WHERE user_id = ?";
    private static final String SELECT_BY_EMAIL = "SELECT * FROM UserAccount WHERE email = ? AND status = 'Active'";
    private static final String UPDATE_PASS_EMAIL = "UPDATE UserAccount SET password_hash = ? WHERE email = ? AND status = 'Active'";
    private static final String SELECT_BY_ROLE_ID = "SELECT * FROM UserAccount WHERE role_id = ? AND status = 'Active'";
    private static final String SELECT_BY_ID = "SELECT * FROM UserAccount WHERE user_id = ? AND status = 'Active'";

    public List<UserAccountDTO> getAllUserAccounts() {
        return DatabaseConnection.executeQueryList(SELECT_ALL, this::mapToDTO);
    }

    public boolean addUserAccount(UserAccountDTO u) {
        return DatabaseConnection.executeUpdate(INSERT_SQL,
                u.getUsername(), u.getPasswordHash(), u.getFullName(), u.getPhone(),
                u.getEmail(), u.getRoleId(), u.getStatus(), Timestamp.valueOf(LocalDateTime.now())
        );
    }

    public boolean updateUserAccount(UserAccountDTO u) {
        return DatabaseConnection.executeUpdate(UPDATE_SQL,
                u.getUsername(), u.getPasswordHash(), u.getFullName(), u.getPhone(),
                u.getEmail(), u.getRoleId(), u.getStatus(), u.getUserId()
        );
    }

    public boolean deleteUserAccount(int id) { return DatabaseConnection.executeUpdate(DELETE_SQL, id); }

    public UserAccountDTO authenticate(String username, String passwordHash) {
        return DatabaseConnection.executeQuerySingle(AUTHENTICATE, rs -> {
                UserAccountDTO user = mapToDTO(rs);
                // So sánh password hash
                if (user.getPasswordHash().equals(passwordHash)) {
                    return user;
                }
                return null;
        }, username);
    }

    public UserAccountDTO getUserByEmail(String email) {
        return DatabaseConnection.executeQuerySingle(SELECT_BY_EMAIL, this::mapToDTO, email);
    }

    public boolean updatePasswordByEmail(String email, String hash) {
        return DatabaseConnection.executeUpdate(UPDATE_PASS_EMAIL, hash, email);
    }

    public List<UserAccountDTO> getUsersByRoleId(int roleId) {
        return DatabaseConnection.executeQueryList(SELECT_BY_ROLE_ID, this::mapToDTO, roleId);
    }

    public UserAccountDTO getUserById(int userId) {
        return DatabaseConnection.executeQuerySingle(SELECT_BY_ID, this::mapToDTO, userId);
    }

    private UserAccountDTO mapToDTO(java.sql.ResultSet rs) throws java.sql.SQLException {
        UserAccountDTO u = new UserAccountDTO();
        u.setUserId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setFullName(rs.getString("full_name"));
        u.setPhone(rs.getString("phone"));
        u.setEmail(rs.getString("email"));
        u.setRoleId(rs.getInt("role_id"));
        u.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("created_at");
        u.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);
        return u;
    }
}