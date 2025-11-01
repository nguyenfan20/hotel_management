package DAO;

import DTO.BookingRoomDTO;
import util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Lớp truy cập cơ sở dữ liệu cho BookingRoom
public class BookingRoomDAO {

    // Thêm phòng đặt mới
    public boolean insert(BookingRoomDTO bookingRoom) {
        String sql = "INSERT INTO BookingRoom (booking_id, room_id, check_in_plan, check_out_plan, " +
                "adults, children, rate_per_night, discount_amount, tax_rate, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, bookingRoom.getBookingId());
                pstmt.setInt(2, bookingRoom.getRoomId());
                pstmt.setTimestamp(3, Timestamp.valueOf(bookingRoom.getCheckInPlan()));
                pstmt.setTimestamp(4, Timestamp.valueOf(bookingRoom.getCheckOutPlan()));
                pstmt.setInt(5, bookingRoom.getAdults());
                pstmt.setInt(6, bookingRoom.getChildren());
                pstmt.setBigDecimal(7, bookingRoom.getRatePerNight());
                pstmt.setBigDecimal(8, bookingRoom.getDiscountAmount());
                pstmt.setBigDecimal(9, bookingRoom.getTaxRate());
                pstmt.setString(10, bookingRoom.getStatus());

                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    // Cập nhật phòng đặt
    public boolean update(BookingRoomDTO bookingRoom) {
        String sql = "UPDATE BookingRoom SET booking_id = ?, room_id = ?, check_in_plan = ?, " +
                "check_out_plan = ?, check_in_actual = ?, check_out_actual = ?, adults = ?, " +
                "children = ?, rate_per_night = ?, discount_amount = ?, tax_rate = ?, status = ? " +
                "WHERE booking_room_id = ?";
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, bookingRoom.getBookingId());
                pstmt.setInt(2, bookingRoom.getRoomId());
                pstmt.setTimestamp(3, Timestamp.valueOf(bookingRoom.getCheckInPlan()));
                pstmt.setTimestamp(4, Timestamp.valueOf(bookingRoom.getCheckOutPlan()));
                pstmt.setTimestamp(5, bookingRoom.getCheckInActual() != null ?
                        Timestamp.valueOf(bookingRoom.getCheckInActual()) : null);
                pstmt.setTimestamp(6, bookingRoom.getCheckOutActual() != null ?
                        Timestamp.valueOf(bookingRoom.getCheckOutActual()) : null);
                pstmt.setInt(7, bookingRoom.getAdults());
                pstmt.setInt(8, bookingRoom.getChildren());
                pstmt.setBigDecimal(9, bookingRoom.getRatePerNight());
                pstmt.setBigDecimal(10, bookingRoom.getDiscountAmount());
                pstmt.setBigDecimal(11, bookingRoom.getTaxRate());
                pstmt.setString(12, bookingRoom.getStatus());
                pstmt.setInt(13, bookingRoom.getBookingRoomId());

                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    // Xóa
    public boolean delete(int bookingRoomId) {
        String sql = "UPDATE BookingRoom SET is_hide = 1 WHERE booking_room_id = ?";
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, bookingRoomId);
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    // Lấy phòng đặt theo mã
    public BookingRoomDTO getById(int bookingRoomId) {
        String sql = "SELECT * FROM BookingRoom WHERE booking_room_id = ? AND is_hide = 0";
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, bookingRoomId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return mapResultSetToDTO(rs);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
        return null;
    }

    // Lấy phòng đặt theo đặt phòng
    public List<BookingRoomDTO> getByBookingId(int bookingId) {
        List<BookingRoomDTO> bookingRooms = new ArrayList<>();
        String sql = "SELECT * FROM BookingRoom WHERE booking_id = ? AND is_hide = 0";
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, bookingId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        bookingRooms.add(mapResultSetToDTO(rs));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
        return bookingRooms;
    }

    // Lấy phòng đặt theo trạng thái
    public List<BookingRoomDTO> getByStatus(String status) {
        List<BookingRoomDTO> bookingRooms = new ArrayList<>();
        String sql = "SELECT * FROM BookingRoom WHERE status = ? AND is_hide = 0";
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, status);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        bookingRooms.add(mapResultSetToDTO(rs));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
        return bookingRooms;
    }

    // Lấy tất cả phòng đặt
    public List<BookingRoomDTO> getAll() {
        List<BookingRoomDTO> bookingRooms = new ArrayList<>();
        String sql = "SELECT * FROM BookingRoom WHERE is_hide = 0";
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = connection.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bookingRooms.add(mapResultSetToDTO(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
        return bookingRooms;
    }

    private BookingRoomDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
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
