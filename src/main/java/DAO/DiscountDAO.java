package DAO;

import DTO.DiscountDTO;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiscountDAO {

    public List<DiscountDTO> getAllDiscounts() {
        return getAll();
    }

    public List<DiscountDTO> getAll() {
        List<DiscountDTO> discounts = new ArrayList<>();
        String sql = "SELECT * FROM Discount ORDER BY code";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                discounts.add(mapResultSetToDTO(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách chiết khấu: " + e.getMessage());
            e.printStackTrace();
        }
        return discounts;
    }

    public List<DiscountDTO> getActiveDiscounts() {
        return getByStatus("Active");
    }

    public List<DiscountDTO> getByStatus(String status) {
        List<DiscountDTO> discounts = new ArrayList<>();
        String sql = "SELECT * FROM Discount WHERE status = ? ORDER BY code";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    discounts.add(mapResultSetToDTO(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy chiết khấu theo trạng thái: " + e.getMessage());
            e.printStackTrace();
        }
        return discounts;
    }

    public DiscountDTO getDiscountById(int discountId) {
        return getById(discountId);
    }

    public DiscountDTO getById(int discountId) {
        String sql = "SELECT * FROM Discount WHERE discount_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, discountId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDTO(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy chiết khấu: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public DiscountDTO getDiscountByCode(String code) {
        return getByCode(code);
    }

    public DiscountDTO getByCode(String code) {
        String sql = "SELECT * FROM Discount WHERE code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, code);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDTO(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy chiết khấu theo mã: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean addDiscount(DiscountDTO discount) {
        return insert(discount);
    }

    public boolean insert(DiscountDTO discount) {
        String sql = "INSERT INTO Discount (code, discount_type, discount_value, min_spend, max_discount_amount, start_date, expiry_date, usage_limit, per_user_limit, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, discount.getCode());
            pstmt.setString(2, discount.getDiscountType());
            pstmt.setDouble(3, discount.getDiscountValue());
            pstmt.setDouble(4, discount.getMinSpend());
            pstmt.setDouble(5, discount.getMaxDiscountAmount());
            pstmt.setDate(6, discount.getStartDate());
            pstmt.setDate(7, discount.getExpiryDate());
            pstmt.setInt(8, discount.getUsageLimit());
            pstmt.setInt(9, discount.getPerUserLimit());
            pstmt.setString(10, discount.getStatus());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm chiết khấu: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateDiscount(DiscountDTO discount) {
        return update(discount);
    }

    public boolean update(DiscountDTO discount) {
        String sql = "UPDATE Discount SET code = ?, discount_type = ?, discount_value = ?, min_spend = ?, max_discount_amount = ?, start_date = ?, expiry_date = ?, usage_limit = ?, per_user_limit = ?, status = ? WHERE discount_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, discount.getCode());
            pstmt.setString(2, discount.getDiscountType());
            pstmt.setDouble(3, discount.getDiscountValue());
            pstmt.setDouble(4, discount.getMinSpend());
            pstmt.setDouble(5, discount.getMaxDiscountAmount());
            pstmt.setDate(6, discount.getStartDate());
            pstmt.setDate(7, discount.getExpiryDate());
            pstmt.setInt(8, discount.getUsageLimit());
            pstmt.setInt(9, discount.getPerUserLimit());
            pstmt.setString(10, discount.getStatus());
            pstmt.setInt(11, discount.getDiscountId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật chiết khấu: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteDiscount(int discountId) {
        return delete(discountId);
    }

    public boolean delete(int discountId) {
        String sql = "DELETE FROM Discount WHERE discount_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, discountId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa chiết khấu: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<DiscountDTO> searchDiscounts(String keyword) {
        List<DiscountDTO> discounts = new ArrayList<>();
        String sql = "SELECT * FROM Discount WHERE code LIKE ? OR discount_type LIKE ? ORDER BY code";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String searchTerm = "%" + keyword + "%";
            pstmt.setString(1, searchTerm);
            pstmt.setString(2, searchTerm);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    discounts.add(mapResultSetToDTO(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm chiết khấu: " + e.getMessage());
            e.printStackTrace();
        }
        return discounts;
    }

    private DiscountDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        return new DiscountDTO(
                rs.getInt("discount_id"),
                rs.getString("code"),
                rs.getString("discount_type"),
                rs.getDouble("discount_value"),
                rs.getDouble("min_spend"),
                rs.getDouble("max_discount_amount"),
                rs.getDate("start_date"),
                rs.getDate("expiry_date"),
                rs.getInt("usage_limit"),
                rs.getInt("per_user_limit"),
                rs.getString("status")
        );
    }
}
