package DAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import DTO.CustomerDTO;
import util.DatabaseConnection;

public class CustomerDAO {

    // Lấy tất cả khách hàng
    public List<CustomerDTO> getAllCustomers() {
        List<CustomerDTO> customers = new ArrayList<>();
        String sql = "SELECT * FROM Customer WHERE is_hide=0 ORDER BY customer_id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                CustomerDTO customer = mapResultSetToDTO(rs);
                customers.add(customer);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách khách hàng: " + e.getMessage());
            e.printStackTrace();
        }

        return customers;
    }

    // Lấy khách hàng theo ID
    public CustomerDTO getCustomerById(int customerId) {
        String sql = "SELECT * FROM Customer WHERE customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDTO(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy khách hàng theo ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // Thêm khách hàng mới
    public boolean addCustomer(CustomerDTO customer) {
        String sql = "INSERT INTO Customer (full_name, phone, email, id_card, address, nationality, dob, gender, note) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setCustomerParameters(pstmt, customer);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        customer.setCustomer_id(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm khách hàng: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Cập nhật khách hàng
    public boolean updateCustomer(CustomerDTO customer) {
        String sql = "UPDATE Customer SET full_name = ?, phone = ?, email = ?, id_card = ?, " +
                     "address = ?, nationality = ?, dob = ?, gender = ?, note = ? " +
                     "WHERE customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setCustomerParameters(pstmt, customer);
            pstmt.setInt(10, customer.getCustomer_id());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật khách hàng: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Xóa khách hàng
    public boolean deleteCustomer(int customerId) {
        String sql = "UPDATE Customer SET is_hide=1 WHERE customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa khách hàng: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Tìm kiếm khách hàng theo tên, email, phone, id_card
    public List<CustomerDTO> searchCustomers(String keyword) {
        List<CustomerDTO> customers = new ArrayList<>();
        String sql = "SELECT * FROM Customer " +
                     "WHERE is_hide=0 AND full_name LIKE ? OR email LIKE ? OR phone LIKE ? OR id_card LIKE ? " +
                     "ORDER BY full_name";

        String searchPattern = "%" + keyword + "%";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 1; i <= 4; i++) {
                pstmt.setString(i, searchPattern);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapResultSetToDTO(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm khách hàng: " + e.getMessage());
            e.printStackTrace();
        }

        return customers;
    }

    // Lọc theo quốc tịch
    public List<CustomerDTO> filterByNationality(String nationality) {
        List<CustomerDTO> customers = new ArrayList<>();
        String sql = "SELECT * FROM Customer WHERE is_hide=0 AND nationality = ? ORDER BY full_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nationality);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapResultSetToDTO(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lọc theo quốc tịch: " + e.getMessage());
            e.printStackTrace();
        }

        return customers;
    }

    // === Helper Methods ===

    // Ánh xạ ResultSet → CustomerDTO
    private CustomerDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        CustomerDTO customer = new CustomerDTO();

        customer.setCustomer_id(rs.getInt("customer_id"));
        customer.setFull_name(rs.getString("full_name"));

        // Xử lý phone: nếu null → 0, nếu có → parse thành int
        String phoneStr = rs.getString("phone");
        customer.setPhone(phoneStr != null && !phoneStr.isEmpty() ? Integer.parseInt(phoneStr) : 0);

        customer.setEmail(rs.getString("email"));
        customer.setId_card(rs.getString("id_card"));
        customer.setNationality(rs.getString("nationality"));

        Date dob = rs.getDate("dob");
        customer.setDob(dob != null ? new java.util.Date(dob.getTime()) : null);

        customer.setGender(rs.getString("gender"));
        customer.setNote(rs.getString("note"));

        return customer;
    }

    // Thiết lập tham số cho INSERT/UPDATE
    private void setCustomerParameters(PreparedStatement pstmt, CustomerDTO customer) throws SQLException {
        pstmt.setString(1, customer.getFull_name());
        pstmt.setString(2, customer.getPhone() > 0 ? String.valueOf(customer.getPhone()) : null);
        pstmt.setString(3, customer.getEmail());
        pstmt.setString(4, customer.getId_card());
        pstmt.setString(5, null); // address không có trong DTO → để NULL
        pstmt.setString(6, customer.getNationality());
        pstmt.setDate(7, customer.getDob() != null ? new java.sql.Date(customer.getDob().getTime()) : null);
        pstmt.setString(8, customer.getGender());
        pstmt.setString(9, customer.getNote());
    }
}