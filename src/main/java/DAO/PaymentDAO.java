package DAO;

import DTO.PaymentDTO;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    public List<PaymentDTO> getAllPayments() {
        List<PaymentDTO> payments = new ArrayList<>();
        String sql = "SELECT * FROM Payment ORDER BY paid_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                payments.add(mapResultSetToDTO(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách thanh toán: " + e.getMessage());
            e.printStackTrace();
        }
        return payments;
    }

    public List<PaymentDTO> filterPaymentsByStatus(String status) {
        List<PaymentDTO> payments = new ArrayList<>();
        String sql = "SELECT * FROM Payment WHERE status = ? ORDER BY paid_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapResultSetToDTO(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy thanh toán theo trạng thái: " + e.getMessage());
            e.printStackTrace();
        }
        return payments;
    }

    public List<PaymentDTO> getPaymentsByBooking(int bookingId) {
        List<PaymentDTO> payments = new ArrayList<>();
        String sql = "SELECT * FROM Payment WHERE booking_id = ? ORDER BY paid_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapResultSetToDTO(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy thanh toán theo đặt phòng: " + e.getMessage());
            e.printStackTrace();
        }
        return payments;
    }

    public PaymentDTO getPaymentById(int paymentId) {
        String sql = "SELECT * FROM Payment WHERE payment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, paymentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDTO(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy thanh toán: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean addPayment(PaymentDTO payment) {
        String sql = "INSERT INTO Payment (booking_id, amount, method, paid_at, reference_no, status, note) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, payment.getBookingId());
            pstmt.setDouble(2, payment.getAmount());
            pstmt.setString(3, payment.getMethod());
            pstmt.setTimestamp(4, payment.getPaidAt());
            pstmt.setString(5, payment.getReferenceNo());
            pstmt.setString(6, payment.getStatus());
            pstmt.setString(7, payment.getNote());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm thanh toán: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePayment(PaymentDTO payment) {
        String sql = "UPDATE Payment SET amount = ?, method = ?, paid_at = ?, reference_no = ?, status = ?, note = ? WHERE payment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, payment.getAmount());
            pstmt.setString(2, payment.getMethod());
            pstmt.setTimestamp(3, payment.getPaidAt());
            pstmt.setString(4, payment.getReferenceNo());
            pstmt.setString(5, payment.getStatus());
            pstmt.setString(6, payment.getNote());
            pstmt.setInt(7, payment.getPaymentId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật thanh toán: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean deletePayment(int paymentId) {
        String sql = "DELETE FROM Payment WHERE payment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, paymentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa thanh toán: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<PaymentDTO> searchPayments(String keyword) {
        List<PaymentDTO> payments = new ArrayList<>();
        String sql = "SELECT * FROM Payment WHERE reference_no LIKE ? OR note LIKE ? ORDER BY paid_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String searchTerm = "%" + keyword + "%";
            pstmt.setString(1, searchTerm);
            pstmt.setString(2, searchTerm);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapResultSetToDTO(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm thanh toán: " + e.getMessage());
            e.printStackTrace();
        }
        return payments;
    }

    private PaymentDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        return new PaymentDTO(
                rs.getInt("payment_id"),
                rs.getInt("booking_id"),
                rs.getDouble("amount"),
                rs.getString("method"),
                rs.getTimestamp("paid_at"),
                rs.getString("reference_no"),
                rs.getString("status"),
                rs.getString("note")
        );
    }
}
