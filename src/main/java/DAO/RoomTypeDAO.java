package DAO;

import DTO.RoomTypeDTO;
import util.DatabaseConnection;
import util.ResultSetMapper;
import java.util.List;

public class RoomTypeDAO {

    private final RoomTypeAmenityDAO amenityDAO = new RoomTypeAmenityDAO();

    private static final String SELECT_ALL = "SELECT * FROM RoomType ORDER BY room_type_id";
    private static final String SELECT_BY_ID = "SELECT * FROM RoomType WHERE room_type_id = ?";
    private static final String INSERT_SQL = "INSERT INTO RoomType (name, base_price, capacity_adults, capacity_children, bed_count, area, description) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE RoomType SET name = ?, base_price = ?, capacity_adults = ?, capacity_children = ?, bed_count = ?, area = ?, description = ? WHERE room_type_id = ?";
    private static final String DELETE_SQL = "DELETE FROM RoomType WHERE room_type_id = ?";
    private static final String SEARCH_NAME = "SELECT * FROM RoomType WHERE name LIKE ? ORDER BY room_type_id";

    public List<RoomTypeDTO> getAllRoomTypes() {
        return DatabaseConnection.executeQueryList(SELECT_ALL, rs -> mapToDTO(rs, true));
    }

    public RoomTypeDTO getRoomTypeById(int id) {
        return DatabaseConnection.executeQuerySingle(SELECT_BY_ID, rs -> mapToDTO(rs, true), id);
    }

    public boolean addRoomType(RoomTypeDTO rt) {
        return DatabaseConnection.executeUpdate(INSERT_SQL,
                rt.getName(), rt.getBasePrice(), rt.getCapacityAdults(), rt.getCapacityChildren(),
                rt.getBedCount(), rt.getArea(), rt.getDescription()
        );
    }

    public boolean updateRoomType(RoomTypeDTO rt) {
        return DatabaseConnection.executeUpdate(UPDATE_SQL,
                rt.getName(), rt.getBasePrice(), rt.getCapacityAdults(), rt.getCapacityChildren(),
                rt.getBedCount(), rt.getArea(), rt.getDescription(), rt.getRoomTypeId()
        );
    }

    public boolean deleteRoomType(int id) { return DatabaseConnection.executeUpdate(DELETE_SQL, id); }

    public List<RoomTypeDTO> searchRoomTypesByName(String kw) {
        return DatabaseConnection.executeQueryList(SEARCH_NAME, rs -> mapToDTO(rs, true), "%"+kw+"%");
    }

    private RoomTypeDTO mapToDTO(java.sql.ResultSet rs, boolean loadAmenities) throws java.sql.SQLException {
        RoomTypeDTO rt = new RoomTypeDTO(
                rs.getInt("room_type_id"),
                rs.getString("name"),
                rs.getBigDecimal("base_price"),
                rs.getByte("capacity_adults"),
                rs.getByte("capacity_children"),
                rs.getByte("bed_count"),
                rs.getBigDecimal("area"),
                rs.getString("description")
        );
        if (loadAmenities) {
            rt.setAmenities(amenityDAO.getAmenitiesByRoomType(rt.getRoomTypeId()));
        }
        return rt;
    }
}