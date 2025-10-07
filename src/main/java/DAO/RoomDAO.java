package DAO;

import DTO.RoomDTO;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    // Lấy tất cả phòng
    public List<RoomDTO> getAllRooms() {
        List<RoomDTO> rooms = new ArrayList<>();
        String sql = "SELECT r.*, rt.name as room_type_name, rt.description as room_type_description " +
                "FROM Room r " +
                "LEFT JOIN RoomType rt ON r.room_type_id = rt.room_type_id " +
                "ORDER BY r.floor_no, r.room_no";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                RoomDTO room = new RoomDTO(
                        rs.getInt("room_id"),
                        rs.getString("room_no"),
                        rs.getByte("floor_no"),
                        rs.getInt("room_type_id"),
                        rs.getString("status"),
                        rs.getString("note")
                );
                room.setRoomTypeName(rs.getString("room_type_name"));
                room.setRoomTypeDescription(rs.getString("room_type_description"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách phòng: " + e.getMessage());
            e.printStackTrace();
        }

        return rooms;
    }

    // Lấy phòng theo ID
    public RoomDTO getRoomById(int roomId) {
        String sql = "SELECT r.*, rt.name as room_type_name, rt.description as room_type_description " +
                "FROM Room r " +
                "LEFT JOIN RoomType rt ON r.room_type_id = rt.room_type_id " +
                "WHERE r.room_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                RoomDTO room = new RoomDTO(
                        rs.getInt("room_id"),
                        rs.getString("room_no"),
                        rs.getByte("floor_no"),
                        rs.getInt("room_type_id"),
                        rs.getString("status"),
                        rs.getString("note")
                );
                room.setRoomTypeName(rs.getString("room_type_name"));
                room.setRoomTypeDescription(rs.getString("room_type_description"));
                return room;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy phòng: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // Thêm phòng mới
    public boolean addRoom(RoomDTO room) {
        String sql = "INSERT INTO Room (room_no, floor_no, room_type_id, status, note) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, room.getRoomNo());
            pstmt.setByte(2, room.getFloorNo());
            pstmt.setInt(3, room.getRoomTypeId());
            pstmt.setString(4, room.getStatus());
            pstmt.setString(5, room.getNote());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm phòng: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật phòng
    public boolean updateRoom(RoomDTO room) {
        String sql = "UPDATE Room SET room_no = ?, floor_no = ?, room_type_id = ?, status = ?, note = ? WHERE room_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, room.getRoomNo());
            pstmt.setByte(2, room.getFloorNo());
            pstmt.setInt(3, room.getRoomTypeId());
            pstmt.setString(4, room.getStatus());
            pstmt.setString(5, room.getNote());
            pstmt.setInt(6, room.getRoomId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật phòng: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Xóa phòng
    public boolean deleteRoom(int roomId) {
        String sql = "DELETE FROM Room WHERE room_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa phòng: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Tìm kiếm phòng theo số phòng
    public List<RoomDTO> searchRoomsByRoomNo(String keyword) {
        List<RoomDTO> rooms = new ArrayList<>();
        String sql = "SELECT r.*, rt.name as room_type_name, rt.description as room_type_description " +
                "FROM Room r " +
                "LEFT JOIN RoomType rt ON r.room_type_id = rt.room_type_id " +
                "WHERE r.room_no LIKE ? " +
                "ORDER BY r.floor_no, r.room_no";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                RoomDTO room = new RoomDTO(
                        rs.getInt("room_id"),
                        rs.getString("room_no"),
                        rs.getByte("floor_no"),
                        rs.getInt("room_type_id"),
                        rs.getString("status"),
                        rs.getString("note")
                );
                room.setRoomTypeName(rs.getString("room_type_name"));
                room.setRoomTypeDescription(rs.getString("room_type_description"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm phòng: " + e.getMessage());
            e.printStackTrace();
        }

        return rooms;
    }

    // Lọc phòng theo tầng
    public List<RoomDTO> filterRoomsByFloor(byte floorNo) {
        List<RoomDTO> rooms = new ArrayList<>();
        String sql = "SELECT r.*, rt.name as room_type_name, rt.description as room_type_description " +
                "FROM Room r " +
                "LEFT JOIN RoomType rt ON r.room_type_id = rt.room_type_id " +
                "WHERE r.floor_no = ? " +
                "ORDER BY r.room_no";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setByte(1, floorNo);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                RoomDTO room = new RoomDTO(
                        rs.getInt("room_id"),
                        rs.getString("room_no"),
                        rs.getByte("floor_no"),
                        rs.getInt("room_type_id"),
                        rs.getString("status"),
                        rs.getString("note")
                );
                room.setRoomTypeName(rs.getString("room_type_name"));
                room.setRoomTypeDescription(rs.getString("room_type_description"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lọc phòng theo tầng: " + e.getMessage());
            e.printStackTrace();
        }

        return rooms;
    }

    // Lọc phòng theo trạng thái
    public List<RoomDTO> filterRoomsByStatus(String status) {
        List<RoomDTO> rooms = new ArrayList<>();
        String sql = "SELECT r.*, rt.name as room_type_name, rt.description as room_type_description " +
                "FROM Room r " +
                "LEFT JOIN RoomType rt ON r.room_type_id = rt.room_type_id " +
                "WHERE r.status = ? " +
                "ORDER BY r.floor_no, r.room_no";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                RoomDTO room = new RoomDTO(
                        rs.getInt("room_id"),
                        rs.getString("room_no"),
                        rs.getByte("floor_no"),
                        rs.getInt("room_type_id"),
                        rs.getString("status"),
                        rs.getString("note")
                );
                room.setRoomTypeName(rs.getString("room_type_name"));
                room.setRoomTypeDescription(rs.getString("room_type_description"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lọc phòng theo trạng thái: " + e.getMessage());
            e.printStackTrace();
        }

        return rooms;
    }

    // Lọc phòng theo loại phòng
    public List<RoomDTO> filterRoomsByRoomType(int roomTypeId) {
        List<RoomDTO> rooms = new ArrayList<>();
        String sql = "SELECT r.*, rt.name as room_type_name, rt.description as room_type_description " +
                "FROM Room r " +
                "LEFT JOIN RoomType rt ON r.room_type_id = rt.room_type_id " +
                "WHERE r.room_type_id = ? " +
                "ORDER BY r.floor_no, r.room_no";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomTypeId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                RoomDTO room = new RoomDTO(
                        rs.getInt("room_id"),
                        rs.getString("room_no"),
                        rs.getByte("floor_no"),
                        rs.getInt("room_type_id"),
                        rs.getString("status"),
                        rs.getString("note")
                );
                room.setRoomTypeName(rs.getString("room_type_name"));
                room.setRoomTypeDescription(rs.getString("room_type_description"));
                rooms.add(room);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lọc phòng theo loại: " + e.getMessage());
            e.printStackTrace();
        }

        return rooms;
    }
}
