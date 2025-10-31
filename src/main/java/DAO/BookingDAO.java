package DAO;

import DTO.BookingDTO;
import util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Booking entity
 * Handles all database operations for Booking table
 * Updated to use DatabaseConnection instead of constructor-injected connection
 */
public class BookingDAO {

    /**
     * Insert a new booking record
     */
    public boolean insert(BookingDTO booking) {
        String sql = "INSERT INTO Booking (code, customer_id, booking_date, source, status, created_by, note) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, booking.getCode());
                pstmt.setInt(2, booking.getCustomerId());
                pstmt.setTimestamp(3, Timestamp.valueOf(booking.getBookingDate()));
                pstmt.setString(4, booking.getSource());
                pstmt.setString(5, booking.getStatus());
                pstmt.setInt(6, booking.getCreatedBy());
                pstmt.setString(7, booking.getNote());

                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Update an existing booking record
     */
    public boolean update(BookingDTO booking) {
        String sql = "UPDATE Booking SET code = ?, customer_id = ?, booking_date = ?, source = ?, " +
                "status = ?, created_by = ?, note = ? WHERE booking_id = ?";
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, booking.getCode());
                pstmt.setInt(2, booking.getCustomerId());
                pstmt.setTimestamp(3, Timestamp.valueOf(booking.getBookingDate()));
                pstmt.setString(4, booking.getSource());
                pstmt.setString(5, booking.getStatus());
                pstmt.setInt(6, booking.getCreatedBy());
                pstmt.setString(7, booking.getNote());
                pstmt.setInt(8, booking.getBookingId());

                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Delete a booking record (soft delete)
     */
    public boolean delete(int bookingId) {
        String sql = "UPDATE Booking SET is_hide = 1 WHERE booking_id = ?";
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, bookingId);
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * Get booking by ID
     */
    public BookingDTO getById(int bookingId) {
        String sql = "SELECT * FROM Booking WHERE booking_id = ? AND is_hide = 0";
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, bookingId);
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

    /**
     * Get bookings by customer ID
     */
    public List<BookingDTO> getByCustomerId(int customerId) {
        List<BookingDTO> bookings = new ArrayList<>();
        String sql = "SELECT * FROM Booking WHERE customer_id = ? AND is_hide = 0";
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, customerId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        bookings.add(mapResultSetToDTO(rs));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
        return bookings;
    }

    /**
     * Get bookings by status
     */
    public List<BookingDTO> getByStatus(String status) {
        List<BookingDTO> bookings = new ArrayList<>();
        String sql = "SELECT * FROM Booking WHERE status = ? AND is_hide = 0";
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, status);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        bookings.add(mapResultSetToDTO(rs));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
        return bookings;
    }

    /**
     * Search bookings by code
     */
    public BookingDTO getByCode(String code) {
        String sql = "SELECT * FROM Booking WHERE code = ? AND is_hide = 0";
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, code);
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

    /**
     * Get all bookings
     */
    public List<BookingDTO> getAll() {
        List<BookingDTO> bookings = new ArrayList<>();
        String sql = "SELECT * FROM Booking WHERE is_hide = 0";
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = connection.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapResultSetToDTO(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
        return bookings;
    }

    /**
     * Map ResultSet to BookingDTO
     */
    private BookingDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
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
