package DAO;

import DTO.RoomTypeDTO;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomTypeDAO {

    private RoomTypeAmenityDAO roomTypeAmenityDAO;

    public RoomTypeDAO() {
        this.roomTypeAmenityDAO = new RoomTypeAmenityDAO();
    }

    // Lấy tất cả loại phòng
    public List<RoomTypeDTO> getAllRoomTypes() {
        List<RoomTypeDTO> roomTypes = new ArrayList<>();
        String sql = "SELECT * FROM RoomType ORDER BY room_type_id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                RoomTypeDTO roomType = new RoomTypeDTO(
                        rs.getInt("room_type_id"),
                        rs.getString("name"),
                        rs.getBigDecimal("base_price"),
                        rs.getByte("capacity_adults"),
                        rs.getByte("capacity_children"),
                        rs.getByte("bed_count"),
                        rs.getBigDecimal("area"),
                        rs.getString("description")
                );
                roomType.setAmenities(roomTypeAmenityDAO.getAmenitiesByRoomType(roomType.getRoomTypeId()));
                roomTypes.add(roomType);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách loại phòng: " + e.getMessage());
            e.printStackTrace();
        }

        return roomTypes;
    }

    // Lấy loại phòng theo ID
    public RoomTypeDTO getRoomTypeById(int roomTypeId) {
        String sql = "SELECT * FROM RoomType WHERE room_type_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomTypeId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                RoomTypeDTO roomType = new RoomTypeDTO(
                        rs.getInt("room_type_id"),
                        rs.getString("name"),
                        rs.getBigDecimal("base_price"),
                        rs.getByte("capacity_adults"),
                        rs.getByte("capacity_children"),
                        rs.getByte("bed_count"),
                        rs.getBigDecimal("area"),
                        rs.getString("description")
                );
                roomType.setAmenities(roomTypeAmenityDAO.getAmenitiesByRoomType(roomType.getRoomTypeId()));
                return roomType;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy loại phòng: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // Thêm loại phòng mới
    public boolean addRoomType(RoomTypeDTO roomType) {
        String sql = "INSERT INTO RoomType (name, base_price, capacity_adults, capacity_children, bed_count, area, description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roomType.getName());
            pstmt.setBigDecimal(2, roomType.getBasePrice());
            pstmt.setByte(3, roomType.getCapacityAdults());
            pstmt.setByte(4, roomType.getCapacityChildren());
            pstmt.setByte(5, roomType.getBedCount());
            pstmt.setBigDecimal(6, roomType.getArea());
            pstmt.setString(7, roomType.getDescription());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm loại phòng: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật loại phòng
    public boolean updateRoomType(RoomTypeDTO roomType) {
        String sql = "UPDATE RoomType SET name = ?, base_price = ?, capacity_adults = ?, " +
                "capacity_children = ?, bed_count = ?, area = ?, description = ? " +
                "WHERE room_type_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roomType.getName());
            pstmt.setBigDecimal(2, roomType.getBasePrice());
            pstmt.setByte(3, roomType.getCapacityAdults());
            pstmt.setByte(4, roomType.getCapacityChildren());
            pstmt.setByte(5, roomType.getBedCount());
            pstmt.setBigDecimal(6, roomType.getArea());
            pstmt.setString(7, roomType.getDescription());
            pstmt.setInt(8, roomType.getRoomTypeId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật loại phòng: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Xóa loại phòng
    public boolean deleteRoomType(int roomTypeId) {
        String sql = "DELETE FROM RoomType WHERE room_type_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomTypeId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa loại phòng: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Tìm kiếm loại phòng theo tên
    public List<RoomTypeDTO> searchRoomTypesByName(String keyword) {
        List<RoomTypeDTO> roomTypes = new ArrayList<>();
        String sql = "SELECT * FROM RoomType WHERE name LIKE ? ORDER BY room_type_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                RoomTypeDTO roomType = new RoomTypeDTO(
                        rs.getInt("room_type_id"),
                        rs.getString("name"),
                        rs.getBigDecimal("base_price"),
                        rs.getByte("capacity_adults"),
                        rs.getByte("capacity_children"),
                        rs.getByte("bed_count"),
                        rs.getBigDecimal("area"),
                        rs.getString("description")
                );
                roomType.setAmenities(roomTypeAmenityDAO.getAmenitiesByRoomType(roomType.getRoomTypeId()));
                roomTypes.add(roomType);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm loại phòng: " + e.getMessage());
            e.printStackTrace();
        }

        return roomTypes;
    }
}
