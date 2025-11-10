package DAO;

import DTO.PaymentDTO;
import util.DatabaseConnection;
import util.ResultSetMapper;
import java.util.List;

public class PaymentDAO {

    private static final String SELECT_ALL = "SELECT * FROM Payment ORDER BY paid_at DESC";
    private static final String FILTER_STATUS = "SELECT * FROM Payment WHERE status = ? ORDER BY paid_at DESC";
    private static final String SELECT_BY_BOOKING = "SELECT * FROM Payment WHERE booking_id = ? ORDER BY paid_at DESC";
    private static final String SELECT_BY_ID = "SELECT * FROM Payment WHERE payment_id = ?";
    private static final String INSERT_SQL = "INSERT INTO Payment (booking_id, invoice_id, amount, method, paid_at, reference_no, status, note) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE Payment SET amount = ?, method = ?, paid_at = ?, reference_no = ?, status = ?, note = ? WHERE payment_id = ?";
    private static final String DELETE_SQL = "DELETE FROM Payment WHERE payment_id = ?";
    private static final String SEARCH_SQL = "SELECT * FROM Payment WHERE reference_no LIKE ? OR note LIKE ? ORDER BY paid_at DESC";

    public List<PaymentDTO> getAllPayments() {
        return DatabaseConnection.executeQueryList(SELECT_ALL, this::mapToDTO);
    }

    public List<PaymentDTO> filterPaymentsByStatus(String status) {
        return DatabaseConnection.executeQueryList(FILTER_STATUS, this::mapToDTO, status);
    }

    public List<PaymentDTO> getPaymentsByBooking(int bookingId) {
        return DatabaseConnection.executeQueryList(SELECT_BY_BOOKING, this::mapToDTO, bookingId);
    }

    public PaymentDTO getPaymentById(int paymentId) {
        return DatabaseConnection.executeQuerySingle(SELECT_BY_ID, this::mapToDTO, paymentId);
    }

    public boolean addPayment(PaymentDTO payment) {
        return DatabaseConnection.executeUpdate(INSERT_SQL,
                payment.getBookingId(), payment.getInvoiceId(), payment.getAmount(), payment.getMethod(),
                payment.getPaidAt(), payment.getReferenceNo(), payment.getStatus(), payment.getNote()
        );
    }

    public boolean updatePayment(PaymentDTO payment) {
        return DatabaseConnection.executeUpdate(UPDATE_SQL,
                payment.getAmount(), payment.getMethod(), payment.getPaidAt(),
                payment.getReferenceNo(), payment.getStatus(), payment.getNote(), payment.getPaymentId()
        );
    }

    public boolean deletePayment(int paymentId) {
        return DatabaseConnection.executeUpdate(DELETE_SQL, paymentId);
    }

    public List<PaymentDTO> searchPayments(String keyword) {
        return DatabaseConnection.executeQueryList(SEARCH_SQL, this::mapToDTO, "%" + keyword + "%", "%" + keyword + "%");
    }

    private PaymentDTO mapToDTO(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new PaymentDTO(
                rs.getInt("payment_id"),
                rs.getInt("booking_id"),
                rs.getInt("invoice_id"),
                rs.getDouble("amount"),
                rs.getString("method"),
                rs.getTimestamp("paid_at"),
                rs.getString("reference_no"),
                rs.getString("status"),
                rs.getString("note")
        );
    }
}