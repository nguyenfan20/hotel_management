package DAO;

import DTO.AmenityDTO;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomTypeAmenityDAO {

    public List<AmenityDTO> getAmenitiesByRoomType(int roomTypeId) {
        List<AmenityDTO> amenities = new ArrayList<>();
        String sql = "SELECT a.* FROM Amenity a " +
                "INNER JOIN RoomTypeAmenity rta ON a.amenity_id = rta.amenity_id " +
                "WHERE rta.room_type_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomTypeId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                AmenityDTO amenity = new AmenityDTO(
                        rs.getInt("amenity_id"),
                        rs.getString("name"),
                        rs.getString("charge_type"),
                        rs.getBigDecimal("price"),
                        rs.getString("description")
                );
                amenities.add(amenity);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy amenities của room type: " + e.getMessage());
            e.printStackTrace();
        }

        return amenities;
    }

    // Thêm amenity vào room type
    public boolean addAmenityToRoomType(int roomTypeId, int amenityId) {
        String sql = "INSERT INTO RoomTypeAmenity (room_type_id, amenity_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomTypeId);
            pstmt.setInt(2, amenityId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm amenity vào room type: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Xóa amenity khỏi room type
    public boolean removeAmenityFromRoomType(int roomTypeId, int amenityId) {
        String sql = "DELETE FROM RoomTypeAmenity WHERE room_type_id = ? AND amenity_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomTypeId);
            pstmt.setInt(2, amenityId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa amenity khỏi room type: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Xóa tất cả amenities của một room type
    public boolean removeAllAmenitiesFromRoomType(int roomTypeId) {
        String sql = "DELETE FROM RoomTypeAmenity WHERE room_type_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomTypeId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa tất cả amenities của room type: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật amenities cho room type (xóa hết rồi thêm mới)
    public boolean updateAmenitiesForRoomType(int roomTypeId, List<Integer> amenityIds) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Xóa tất cả amenities cũ
            String deleteSql = "DELETE FROM RoomTypeAmenity WHERE room_type_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                pstmt.setInt(1, roomTypeId);
                pstmt.executeUpdate();
            }

            // Thêm amenities mới
            if (amenityIds != null && !amenityIds.isEmpty()) {
                String insertSql = "INSERT INTO RoomTypeAmenity (room_type_id, amenity_id) VALUES (?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    for (Integer amenityId : amenityIds) {
                        pstmt.setInt(1, roomTypeId);
                        pstmt.setInt(2, amenityId);
                        pstmt.addBatch();
                    }
                    pstmt.executeBatch();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.err.println("Lỗi khi cập nhật amenities cho room type: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
