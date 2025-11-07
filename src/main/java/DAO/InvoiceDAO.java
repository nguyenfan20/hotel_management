package DAO;

import DTO.InvoiceDTO;
import util.DatabaseConnection;
import util.ResultSetMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class InvoiceDAO {

    private static final String SELECT_ALL = "SELECT i.*, ua.full_name as created_by_name, b.booking_id FROM Invoice i JOIN UserAccount ua ON i.created_by = ua.user_id JOIN Booking b ON i.booking_id = b.booking_id ORDER BY i.created_at DESC";
    private static final String FILTER_BY_STATUS = "SELECT i.*, ua.full_name as created_by_name, b.booking_id FROM Invoice i JOIN UserAccount ua ON i.created_by = ua.user_id JOIN Booking b ON i.booking_id = b.booking_id WHERE i.status = ? ORDER BY i.created_at DESC";
    private static final String SELECT_BY_ID = "SELECT i.*, ua.full_name as created_by_name, b.booking_id FROM Invoice i JOIN UserAccount ua ON i.created_by = ua.user_id JOIN Booking b ON i.booking_id = b.booking_id WHERE i.invoice_id = ?";
    private static final String INSERT_SQL = "INSERT INTO Invoice (booking_id, invoice_no, subtotal, discount_total, tax_total, grand_total, created_at, created_by, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE Invoice SET subtotal = ?, discount_total = ?, tax_total = ?, grand_total = ?, status = ? WHERE invoice_id = ?";
    private static final String DELETE_SQL = "DELETE FROM Invoice WHERE invoice_id = ?";
    private static final String SEARCH_SQL = "SELECT i.*, ua.full_name as created_by_name, b.booking_id FROM Invoice i JOIN UserAccount ua ON i.created_by = ua.user_id JOIN Booking b ON i.booking_id = b.booking_id WHERE i.invoice_no LIKE ? OR i.status LIKE ? ORDER BY i.created_at DESC";
    private static final String SELECT_BY_BOOKING = "SELECT i.*, ua.full_name as created_by_name, b.booking_id FROM Invoice i JOIN UserAccount ua ON i.created_by = ua.user_id JOIN Booking b ON i.booking_id = b.booking_id WHERE i.booking_id = ? ORDER BY i.created_at DESC";
    private static final String SELECT_UNPAID = "SELECT i.*, ua.full_name as created_by_name, b.booking_id FROM Invoice i JOIN UserAccount ua ON i.created_by = ua.user_id JOIN Booking b ON i.booking_id = b.booking_id WHERE i.status = 'Unpaid' ORDER BY i.created_at DESC";

    public List<InvoiceDTO> getAllInvoices() {
        return DatabaseConnection.executeQueryList(SELECT_ALL, this::mapToDTO);
    }

    public List<InvoiceDTO> filterInvoicesByStatus(String status) {
        return DatabaseConnection.executeQueryList(FILTER_BY_STATUS, this::mapToDTO, status);
    }

    public InvoiceDTO getInvoiceById(int invoiceId) {
        return DatabaseConnection.executeQuerySingle(SELECT_BY_ID, this::mapToDTO, invoiceId);
    }

    public List<InvoiceDTO> getUnpaidInvoices() {
        return DatabaseConnection.executeQueryList(SELECT_UNPAID, this::mapToDTO);
    }

    public boolean addInvoice(InvoiceDTO invoice) {
        return DatabaseConnection.executeUpdate(INSERT_SQL,
                invoice.getBookingId(),
                invoice.getInvoiceNo(),
                invoice.getSubtotal(),
                invoice.getDiscountTotal(),
                invoice.getTaxTotal(),
                invoice.getGrandTotal(),
                invoice.getCreatedAt(),
                invoice.getCreatedBy(),
                invoice.getStatus()
        );
    }

    public boolean updateInvoice(InvoiceDTO invoice) {
        return DatabaseConnection.executeUpdate(UPDATE_SQL,
                invoice.getSubtotal(),
                invoice.getDiscountTotal(),
                invoice.getTaxTotal(),
                invoice.getGrandTotal(),
                invoice.getStatus(),
                invoice.getInvoiceId()
        );
    }

    public boolean deleteInvoice(int invoiceId) {
        return DatabaseConnection.executeUpdate(DELETE_SQL, invoiceId);
    }

    public List<InvoiceDTO> searchInvoices(String keyword) {
        String pattern = "%" + keyword + "%";
        return DatabaseConnection.executeQueryList(SEARCH_SQL, this::mapToDTO, pattern, pattern);
    }

    public List<InvoiceDTO> getInvoicesByBooking(int bookingId) {
        return DatabaseConnection.executeQueryList(SELECT_BY_BOOKING, this::mapToDTO, bookingId);
    }

    public InvoiceDTO getInvoiceByBookingId(int bookingId) {
        return DatabaseConnection.executeQuerySingle(SELECT_BY_BOOKING, this::mapToDTO, bookingId);
    }

    private InvoiceDTO mapToDTO(ResultSet rs) throws SQLException {
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