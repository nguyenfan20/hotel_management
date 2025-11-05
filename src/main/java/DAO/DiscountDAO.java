package DAO;

import DTO.DiscountDTO;
import util.DatabaseConnection;
import util.ResultSetMapper;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DiscountDAO {

    private static final String SELECT_ALL = "SELECT * FROM Discount ORDER BY code";
    private static final String SELECT_BY_STATUS = "SELECT * FROM Discount WHERE status = ? ORDER BY code";
    private static final String SELECT_BY_ID = "SELECT * FROM Discount WHERE discount_id = ?";
    private static final String INSERT_SQL = "INSERT INTO Discount (code, discount_type, discount_value, min_spend, max_discount_amount, start_date, expiry_date, usage_limit, per_user_limit, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE Discount SET code = ?, discount_type = ?, discount_value = ?, min_spend = ?, max_discount_amount = ?, start_date = ?, expiry_date = ?, usage_limit = ?, per_user_limit = ?, status = ? WHERE discount_id = ?";
    private static final String DELETE_SQL = "DELETE FROM Discount WHERE discount_id = ?";
    private static final String SEARCH_SQL = "SELECT * FROM Discount WHERE code LIKE ? OR discount_type LIKE ? ORDER BY code";
    private static final String SELECT_BY_CODE = "SELECT * FROM Discount WHERE code = ?";

    public List<DiscountDTO> getAllDiscounts() {
        return DatabaseConnection.executeQueryList(SELECT_ALL, this::mapToDTO);
    }

    public List<DiscountDTO> getActiveDiscounts() {
        return getByStatus("Active");
    }

    public List<DiscountDTO> getByStatus(String status) {
        return DatabaseConnection.executeQueryList(SELECT_BY_STATUS, this::mapToDTO, status);
    }

    public DiscountDTO getDiscountById(int discountId) {
        return DatabaseConnection.executeQuerySingle(SELECT_BY_ID, this::mapToDTO, discountId);
    }

    public boolean addDiscount(DiscountDTO discount) {
        return DatabaseConnection.executeUpdate(INSERT_SQL,
                discount.getCode(),
                discount.getDiscountType(),
                discount.getDiscountValue(),
                discount.getMinSpend(),
                discount.getMaxDiscountAmount(),
                discount.getStartDate(),
                discount.getExpiryDate(),
                discount.getUsageLimit(),
                discount.getPerUserLimit(),
                discount.getStatus()
        );
    }

    public boolean updateDiscount(DiscountDTO discount) {
        return DatabaseConnection.executeUpdate(UPDATE_SQL,
                discount.getCode(),
                discount.getDiscountType(),
                discount.getDiscountValue(),
                discount.getMinSpend(),
                discount.getMaxDiscountAmount(),
                discount.getStartDate(),
                discount.getExpiryDate(),
                discount.getUsageLimit(),
                discount.getPerUserLimit(),
                discount.getStatus(),
                discount.getDiscountId()
        );
    }

    public boolean deleteDiscount(int discountId) {
        return DatabaseConnection.executeUpdate(DELETE_SQL, discountId);
    }

    public List<DiscountDTO> searchDiscounts(String keyword) {
        String pattern = "%" + keyword + "%";
        return DatabaseConnection.executeQueryList(SEARCH_SQL, this::mapToDTO, pattern, pattern);
    }

    public DiscountDTO getDiscountByCode(String code) {
        return DatabaseConnection.executeQuerySingle(SELECT_BY_CODE, this::mapToDTO, code);
    }

    private DiscountDTO mapToDTO(ResultSet rs) throws SQLException {
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