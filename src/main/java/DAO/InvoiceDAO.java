package DAO;

import DTO.InvoiceDTO;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {

    public List<InvoiceDTO> getAllInvoices() {
        List<InvoiceDTO> invoices = new ArrayList<>();
        String sql = "SELECT i.*, ua.full_name as created_by_name, b.booking_id " +
                "FROM Invoice i " +
                "JOIN UserAccount ua ON i.created_by = ua.user_id " +
                "JOIN Booking b ON i.booking_id = b.booking_id " +
                "ORDER BY i.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                invoices.add(mapResultSetToDTO(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
        return invoices;
    }

    public List<InvoiceDTO> filterInvoicesByStatus(String status) {
        List<InvoiceDTO> invoices = new ArrayList<>();
        String sql = "SELECT i.*, ua.full_name as created_by_name, b.booking_id " +
                "FROM Invoice i " +
                "JOIN UserAccount ua ON i.created_by = ua.user_id " +
                "JOIN Booking b ON i.booking_id = b.booking_id " +
                "WHERE i.status = ? ORDER BY i.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    invoices.add(mapResultSetToDTO(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm hóa đơn theo trạng thái: " + e.getMessage());
            e.printStackTrace();
        }
        return invoices;
    }

    public InvoiceDTO getInvoiceById(int invoiceId) {
        String sql = "SELECT i.*, ua.full_name as created_by_name, b.booking_id " +
                "FROM Invoice i " +
                "JOIN UserAccount ua ON i.created_by = ua.user_id " +
                "JOIN Booking b ON i.booking_id = b.booking_id " +
                "WHERE i.invoice_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, invoiceId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDTO(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean addInvoice(InvoiceDTO invoice) {
        String sql = "INSERT INTO Invoice (booking_id, invoice_no, subtotal, discount_total, tax_total, grand_total, created_at, created_by, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, invoice.getBookingId());
            pstmt.setString(2, invoice.getInvoiceNo());
            pstmt.setDouble(3, invoice.getSubtotal());
            pstmt.setDouble(4, invoice.getDiscountTotal());
            pstmt.setDouble(5, invoice.getTaxTotal());
            pstmt.setDouble(6, invoice.getGrandTotal());
            pstmt.setTimestamp(7, invoice.getCreatedAt());
            pstmt.setInt(8, invoice.getCreatedBy());
            pstmt.setString(9, invoice.getStatus());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateInvoice(InvoiceDTO invoice) {
        String sql = "UPDATE Invoice SET subtotal = ?, discount_total = ?, tax_total = ?, grand_total = ?, status = ? WHERE invoice_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, invoice.getSubtotal());
            pstmt.setDouble(2, invoice.getDiscountTotal());
            pstmt.setDouble(3, invoice.getTaxTotal());
            pstmt.setDouble(4, invoice.getGrandTotal());
            pstmt.setString(5, invoice.getStatus());
            pstmt.setInt(6, invoice.getInvoiceId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteInvoice(int invoiceId) {
        String sql = "DELETE FROM Invoice WHERE invoice_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, invoiceId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<InvoiceDTO> searchInvoices(String keyword) {
        List<InvoiceDTO> invoices = new ArrayList<>();
        String sql = "SELECT i.*, ua.full_name as created_by_name, b.booking_id " +
                "FROM Invoice i " +
                "JOIN UserAccount ua ON i.created_by = ua.user_id " +
                "JOIN Booking b ON i.booking_id = b.booking_id " +
                "WHERE i.invoice_no LIKE ? OR i.status LIKE ? " +
                "ORDER BY i.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    invoices.add(mapResultSetToDTO(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
        return invoices;
    }

    public List<InvoiceDTO> getInvoicesByBooking(int bookingId) {
        List<InvoiceDTO> invoices = new ArrayList<>();
        String sql = "SELECT i.*, ua.full_name as created_by_name, b.booking_id " +
                "FROM Invoice i " +
                "JOIN UserAccount ua ON i.created_by = ua.user_id " +
                "JOIN Booking b ON i.booking_id = b.booking_id " +
                "WHERE i.booking_id = ? " +
                "ORDER BY i.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    invoices.add(mapResultSetToDTO(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy hóa đơn theo đặt phòng: " + e.getMessage());
            e.printStackTrace();
        }
        return invoices;
    }

    private InvoiceDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        InvoiceDTO dto = new InvoiceDTO(
                rs.getInt("invoice_id"),
                rs.getInt("booking_id"),
                rs.getString("invoice_no"),
                rs.getDouble("subtotal"),
                rs.getDouble("discount_total"),
                rs.getDouble("tax_total"),
                rs.getDouble("grand_total"),
                rs.getTimestamp("created_at"),
                rs.getInt("created_by"),
                rs.getString("status")
        );
        dto.setCreatedByName(rs.getString("created_by_name"));
        return dto;
    }
}
