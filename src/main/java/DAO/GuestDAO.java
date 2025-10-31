package DAO;

import DTO.GuestDTO;
import util.DatabaseConnection;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Guest entity
 * Handles all database operations for Guest table
 * Updated to use DatabaseConnection instead of constructor-injected connection
 */
public class GuestDAO {

    /**
     * Insert a new guest record
     */
    public boolean insert(GuestDTO guest) {
        String sql = "INSERT INTO Guest (booking_room_id, full_name, gender, dob, id_card, nationality) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, guest.getBookingRoomId());
                pstmt.setString(2, guest.getFullName());
                pstmt.setString(3, guest.getGender());
                pstmt.setDate(4, java.sql.Date.valueOf(guest.getDob()));
                pstmt.setString(5, guest.getIdCard());
                pstmt.setString(6, guest.getNationality());

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
     * Update an existing guest record
     */
    public boolean update(GuestDTO guest) {
        String sql = "UPDATE Guest SET booking_room_id = ?, full_name = ?, gender = ?, dob = ?, " +
                "id_card = ?, nationality = ? WHERE guest_id = ?";
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, guest.getBookingRoomId());
                pstmt.setString(2, guest.getFullName());
                pstmt.setString(3, guest.getGender());
                pstmt.setDate(4, java.sql.Date.valueOf(guest.getDob()));
                pstmt.setString(5, guest.getIdCard());
                pstmt.setString(6, guest.getNationality());
                pstmt.setInt(7, guest.getGuestId());

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
     * Delete a guest record (soft delete)
     */
    public boolean delete(int guestId) {
        String sql = "UPDATE Guest SET is_hide = 1 WHERE guest_id = ?";
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, guestId);
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
     * Get guest by ID
     */
    public GuestDTO getById(int guestId) {
        String sql = "SELECT * FROM Guest WHERE guest_id = ? AND is_hide = 0";
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, guestId);
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
     * Get all guests for a booking room
     */
    public List<GuestDTO> getByBookingRoomId(int bookingRoomId) {
        List<GuestDTO> guests = new ArrayList<>();
        String sql = "SELECT * FROM Guest WHERE booking_room_id = ? AND is_hide = 0";
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setInt(1, bookingRoomId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        guests.add(mapResultSetToDTO(rs));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
        return guests;
    }

    /**
     * Search guests by name or ID card
     */
    public List<GuestDTO> search(String keyword) {
        List<GuestDTO> guests = new ArrayList<>();
        String sql = "SELECT * FROM Guest WHERE (full_name LIKE ? OR id_card LIKE ?) AND is_hide = 0";
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                String searchPattern = "%" + keyword + "%";
                pstmt.setString(1, searchPattern);
                pstmt.setString(2, searchPattern);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        guests.add(mapResultSetToDTO(rs));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
        return guests;
    }

    /**
     * Get all guests
     */
    public List<GuestDTO> getAll() {
        List<GuestDTO> guests = new ArrayList<>();
        String sql = "SELECT * FROM Guest WHERE is_hide = 0";
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            try (PreparedStatement pstmt = connection.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    guests.add(mapResultSetToDTO(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
        return guests;
    }

    /**
     * Map ResultSet to GuestDTO
     */
    private GuestDTO mapResultSetToDTO(ResultSet rs) throws SQLException {
        return new GuestDTO(
                rs.getInt("guest_id"),
                rs.getInt("booking_room_id"),
                rs.getString("full_name"),
                rs.getString("gender"),
                rs.getDate("dob").toLocalDate(),
                rs.getString("id_card"),
                rs.getString("nationality")
        );
    }
}
