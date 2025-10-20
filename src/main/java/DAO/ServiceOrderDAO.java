package DAO;

import DTO.ServiceOrderDTO;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceOrderDAO {

    private ServiceOrderDTO mapRow(ResultSet rs) throws SQLException {
        return new ServiceOrderDTO(
                rs.getInt("service_order_id"),
                rs.getInt("booking_room_id"),
                rs.getInt("service_id"),
                rs.getInt("qty"),
                rs.getDouble("unit_price"),
                rs.getTimestamp("ordered_at") != null ? rs.getTimestamp("ordered_at").toLocalDateTime() : null,
                rs.getString("ordered_by"),
                rs.getString("note")
        );
    }

    
    public List<ServiceOrderDTO> getAll() {
        String sql = "SELECT * FROM ServiceOrder ORDER BY service_order_id DESC";
        List<ServiceOrderDTO> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

   
    public ServiceOrderDTO getById(int id) {
        String sql = "SELECT * FROM ServiceOrder WHERE service_order_id = ?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    
    public int insert(ServiceOrderDTO s) {
        String sql = """
            INSERT INTO ServiceOrder 
                (booking_room_id, service_id, qty, unit_price, ordered_at, ordered_by, note)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, s.getBookingRoomId());
            ps.setInt(2, s.getServiceId());
            ps.setInt(3, s.getQuantity());
            ps.setDouble(4, s.getUnitPrice());
            ps.setTimestamp(5, s.getOrderedAt() != null ? Timestamp.valueOf(s.getOrderedAt()) : null);
            ps.setString(6, s.getOrderedBy());
            ps.setString(7, s.getNote());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) return keys.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

  
    public boolean update(ServiceOrderDTO s) {
        String sql = """
            UPDATE ServiceOrder
            SET booking_room_id=?, service_id=?, qty=?, unit_price=?, ordered_at=?, ordered_by=?, note=?
            WHERE service_order_id=?
        """;
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, s.getBookingRoomId());
            ps.setInt(2, s.getServiceId());
            ps.setInt(3, s.getQuantity());
            ps.setDouble(4, s.getUnitPrice());
            ps.setTimestamp(5, s.getOrderedAt() != null ? Timestamp.valueOf(s.getOrderedAt()) : null);
            ps.setString(6, s.getOrderedBy());
            ps.setString(7, s.getNote());
            ps.setInt(8, s.getServiceOrderId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    
    public boolean delete(int id) {
        String sql = "DELETE FROM ServiceOrder WHERE service_order_id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<ServiceOrderDTO> searchByServiceName(String keyword) {
        String sql = """
            SELECT so.* 
            FROM ServiceOrder so
            JOIN service s ON so.service_id = s.service_id
            WHERE s.name LIKE ?
            ORDER BY so.ordered_at DESC
        """;
        List<ServiceOrderDTO> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword.trim() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ServiceOrderDTO> getByBookingId(int bookingId) {
        String sql = "SELECT * FROM ServiceOrder WHERE booking_room_id = ? ORDER BY ordered_at DESC";
        List<ServiceOrderDTO> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
