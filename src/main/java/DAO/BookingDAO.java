package DAO;

import DTO.BookingDTO;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BookingDAO {
    public List<BookingDTO> getBookings() {
        List<BookingDTO> bookings = new ArrayList<>();
        String sql = "SELECT * FROM Booking ORDER BY booking_id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                BookingDTO booking = new BookingDTO(
                        rs.getInt("booking_id"),
                        rs.getString("code"),
                        rs.getInt("customer_id"),
                        rs.getTimestamp("booking_date").toLocalDateTime(),
                        rs.getString("source"),
                        rs.getString("status"),
                        rs.getInt("created_by"),
                        rs.getString("note")

                );
                bookings.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách đặt phòng" + e.getMessage());
            e.printStackTrace();
        }
        return bookings;
    }
}
