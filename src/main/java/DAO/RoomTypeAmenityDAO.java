package DAO;

import DTO.AmenityDTO;
import util.DatabaseConnection;
import util.ResultSetMapper;
import java.util.List;

public class RoomTypeAmenityDAO {

    private static final String SELECT_BY_ROOM_TYPE = "SELECT a.* FROM Amenity a INNER JOIN RoomTypeAmenity rta ON a.amenity_id = rta.amenity_id WHERE rta.room_type_id = ?";
    private static final String INSERT_SQL = "INSERT INTO RoomTypeAmenity (room_type_id, amenity_id) VALUES (?, ?)";
    private static final String DELETE_BY_BOTH = "DELETE FROM RoomTypeAmenity WHERE room_type_id = ? AND amenity_id = ?";
    private static final String DELETE_ALL = "DELETE FROM RoomTypeAmenity WHERE room_type_id = ?";

    public List<AmenityDTO> getAmenitiesByRoomType(int roomTypeId) {
        return DatabaseConnection.executeQueryList(SELECT_BY_ROOM_TYPE, this::mapToDTO, roomTypeId);
    }

    public boolean addAmenityToRoomType(int roomTypeId, int amenityId) {
        return DatabaseConnection.executeUpdate(INSERT_SQL, roomTypeId, amenityId);
    }

    public boolean removeAmenityFromRoomType(int roomTypeId, int amenityId) {
        return DatabaseConnection.executeUpdate(DELETE_BY_BOTH, roomTypeId, amenityId);
    }

    public boolean removeAllAmenitiesFromRoomType(int roomTypeId) {
        return DatabaseConnection.executeUpdate(DELETE_ALL, roomTypeId);
    }

    public boolean updateAmenitiesForRoomType(int roomTypeId, List<Integer> amenityIds) {
        java.sql.Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            DatabaseConnection.executeUpdate(DELETE_ALL, roomTypeId);
            if (amenityIds != null && !amenityIds.isEmpty()) {
                for (Integer id : amenityIds) {
                    DatabaseConnection.executeUpdate(INSERT_SQL, roomTypeId, id);
                }
            }
            conn.commit();
            return true;
        } catch (java.sql.SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (Exception ex) {}
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeConnection(conn);
        }
    }

    private AmenityDTO mapToDTO(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new AmenityDTO(
                rs.getInt("amenity_id"),
                rs.getString("name"),
                rs.getString("charge_type"),
                rs.getBigDecimal("price"),
                rs.getString("description")
        );
    }
}