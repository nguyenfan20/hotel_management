package DAO;

import DTO.BookingDTO;
import util.DatabaseConnection;
import util.ResultSetMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class BookingDAO {

    private static final String INSERT_SQL = "INSERT INTO Booking (code, customer_id, booking_date, source, status, created_by, note) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE Booking SET code = ?, customer_id = ?, booking_date = ?, source = ?, status = ?, created_by = ?, note = ? WHERE booking_id = ?";
    private static final String DELETE_SQL = "UPDATE Booking SET is_hide = 1 WHERE booking_id = ?";
    private static final String SELECT_BY_ID = "SELECT * FROM Booking WHERE booking_id = ? AND is_hide = 0";
    private static final String SELECT_BY_CUSTOMER = "SELECT * FROM Booking WHERE customer_id = ? AND is_hide = 0";
    private static final String SELECT_BY_STATUS = "SELECT * FROM Booking WHERE status = ? AND is_hide = 0";
    private static final String SELECT_BY_CODE = "SELECT * FROM Booking WHERE code LIKE ? AND is_hide = 0";
    private static final String SELECT_ALL = "SELECT * FROM Booking WHERE is_hide = 0";

    public boolean insert(BookingDTO booking) {
        return DatabaseConnection.executeUpdate(INSERT_SQL,
                booking.getCode(),
                booking.getCustomerId(),
                Timestamp.valueOf(booking.getBookingDate()),
                booking.getSource(),
                booking.getStatus(),
                booking.getCreatedBy(),
                booking.getNote()
        );
    }

    public boolean update(BookingDTO booking) {
        return DatabaseConnection.executeUpdate(UPDATE_SQL,
                booking.getCode(),
                booking.getCustomerId(),
                Timestamp.valueOf(booking.getBookingDate()),
                booking.getSource(),
                booking.getStatus(),
                booking.getCreatedBy(),
                booking.getNote(),
                booking.getBookingId()
        );
    }

    public boolean delete(int bookingId) {
        return DatabaseConnection.executeUpdate(DELETE_SQL, bookingId);
    }

    public BookingDTO getById(int bookingId) {
        return DatabaseConnection.executeQuerySingle(SELECT_BY_ID, this::mapToDTO, bookingId);
    }

    public List<BookingDTO> getByCustomerId(int customerId) {
        return DatabaseConnection.executeQueryList(SELECT_BY_CUSTOMER, this::mapToDTO, customerId);
    }

    public List<BookingDTO> getByStatus(String status) {
        return DatabaseConnection.executeQueryList(SELECT_BY_STATUS, this::mapToDTO, status);
    }

    public List<BookingDTO> getByCode(String code) {
        return DatabaseConnection.executeQueryList(SELECT_BY_CODE, this::mapToDTO, "%" + code + "%");
    }

    public List<BookingDTO> getAll() {
        return DatabaseConnection.executeQueryList(SELECT_ALL, this::mapToDTO);
    }

    private BookingDTO mapToDTO(ResultSet rs) throws SQLException {
        return new BookingDTO(
                rs.getInt("booking_id"),
                rs.getString("code"),
                rs.getInt("customer_id"),
                rs.getTimestamp("booking_date").toLocalDateTime(),
                rs.getString("source"),
                rs.getString("status"),
                rs.getInt("created_by"),
                rs.getString("note")
        );
    }
}