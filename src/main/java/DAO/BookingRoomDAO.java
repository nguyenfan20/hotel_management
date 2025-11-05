package DAO;

import DTO.BookingRoomDTO;
import util.DatabaseConnection;
import util.ResultSetMapper;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class BookingRoomDAO {

    private static final String INSERT_SQL = "INSERT INTO BookingRoom (booking_id, room_id, check_in_plan, check_out_plan, adults, children, rate_per_night, discount_amount, tax_rate, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE BookingRoom SET booking_id = ?, room_id = ?, check_in_plan = ?, check_out_plan = ?, check_in_actual = ?, check_out_actual = ?, adults = ?, children = ?, rate_per_night = ?, discount_amount = ?, tax_rate = ?, status = ? WHERE booking_room_id = ?";
    private static final String DELETE_SQL = "UPDATE BookingRoom SET is_hide = 1 WHERE booking_room_id = ?";
    private static final String SELECT_BY_ID = "SELECT * FROM BookingRoom WHERE booking_room_id = ? AND is_hide = 0";
    private static final String SELECT_BY_BOOKING_ID = "SELECT * FROM BookingRoom WHERE booking_id = ? AND is_hide = 0";
    private static final String SELECT_BY_STATUS = "SELECT * FROM BookingRoom WHERE status = ? AND is_hide = 0";
    private static final String SELECT_ALL = "SELECT * FROM BookingRoom WHERE is_hide = 0";

    public boolean insert(BookingRoomDTO bookingRoom) {
        return DatabaseConnection.executeUpdate(INSERT_SQL,
                bookingRoom.getBookingId(),
                bookingRoom.getRoomId(),
                Timestamp.valueOf(bookingRoom.getCheckInPlan()),
                Timestamp.valueOf(bookingRoom.getCheckOutPlan()),
                bookingRoom.getAdults(),
                bookingRoom.getChildren(),
                bookingRoom.getRatePerNight(),
                bookingRoom.getDiscountAmount(),
                bookingRoom.getTaxRate(),
                bookingRoom.getStatus()
        );
    }

    public boolean update(BookingRoomDTO bookingRoom) {
        return DatabaseConnection.executeUpdate(UPDATE_SQL,
                bookingRoom.getBookingId(),
                bookingRoom.getRoomId(),
                Timestamp.valueOf(bookingRoom.getCheckInPlan()),
                Timestamp.valueOf(bookingRoom.getCheckOutPlan()),
                bookingRoom.getCheckInActual() != null ? Timestamp.valueOf(bookingRoom.getCheckInActual()) : null,
                bookingRoom.getCheckOutActual() != null ? Timestamp.valueOf(bookingRoom.getCheckOutActual()) : null,
                bookingRoom.getAdults(),
                bookingRoom.getChildren(),
                bookingRoom.getRatePerNight(),
                bookingRoom.getDiscountAmount(),
                bookingRoom.getTaxRate(),
                bookingRoom.getStatus(),
                bookingRoom.getBookingRoomId()
        );
    }

    public boolean delete(int bookingRoomId) {
        return DatabaseConnection.executeUpdate(DELETE_SQL, bookingRoomId);
    }

    public BookingRoomDTO getById(int bookingRoomId) {
        return DatabaseConnection.executeQuerySingle(SELECT_BY_ID, this::mapToDTO, bookingRoomId);
    }

    public List<BookingRoomDTO> getByBookingId(int bookingId) {
        return DatabaseConnection.executeQueryList(SELECT_BY_BOOKING_ID, this::mapToDTO, bookingId);
    }

    public List<BookingRoomDTO> getByStatus(String status) {
        return DatabaseConnection.executeQueryList(SELECT_BY_STATUS, this::mapToDTO, status);
    }

    public List<BookingRoomDTO> getAll() {
        return DatabaseConnection.executeQueryList(SELECT_ALL, this::mapToDTO);
    }

    private BookingRoomDTO mapToDTO(ResultSet rs) throws SQLException {
        Timestamp checkInActual = rs.getTimestamp("check_in_actual");
        Timestamp checkOutActual = rs.getTimestamp("check_out_actual");

        return new BookingRoomDTO(
                rs.getInt("booking_room_id"),
                rs.getInt("booking_id"),
                rs.getInt("room_id"),
                rs.getTimestamp("check_in_plan").toLocalDateTime(),
                rs.getTimestamp("check_out_plan").toLocalDateTime(),
                checkInActual != null ? checkInActual.toLocalDateTime() : null,
                checkOutActual != null ? checkOutActual.toLocalDateTime() : null,
                rs.getInt("adults"),
                rs.getInt("children"),
                rs.getBigDecimal("rate_per_night"),
                rs.getBigDecimal("discount_amount"),
                rs.getBigDecimal("tax_rate"),
                rs.getString("status")
        );
    }
}