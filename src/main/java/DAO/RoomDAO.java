package DAO;

import DTO.RoomDTO;
import util.DatabaseConnection;
import util.ResultSetMapper;
import java.util.List;

public class RoomDAO {

    private static final String SELECT_ALL = "SELECT r.*, rt.name as room_type_name, rt.description as room_type_description FROM Room r LEFT JOIN RoomType rt ON r.room_type_id = rt.room_type_id ORDER BY r.floor_no, r.room_no";
    private static final String SELECT_BY_ID = "SELECT r.*, rt.name as room_type_name, rt.description as room_type_description FROM Room r LEFT JOIN RoomType rt ON r.room_type_id = rt.room_type_id WHERE r.room_id = ?";
    private static final String INSERT_SQL = "INSERT INTO Room (room_no, floor_no, room_type_id, status, note) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE Room SET room_no = ?, floor_no = ?, room_type_id = ?, status = ?, note = ? WHERE room_id = ?";
    private static final String DELETE_SQL = "DELETE FROM Room WHERE room_id = ?";
    private static final String SEARCH = "SELECT r.*, rt.name as room_type_name, rt.description as room_type_description FROM Room r LEFT JOIN RoomType rt ON r.room_type_id = rt.room_type_id WHERE r.room_no LIKE ? OR r.note LIKE ? ORDER BY r.floor_no, r.room_no";
    private static final String FILTER_FLOOR = "SELECT r.*, rt.name as room_type_name, rt.description as room_type_description FROM Room r LEFT JOIN RoomType rt ON r.room_type_id = rt.room_type_id WHERE r.floor_no = ? ORDER BY r.floor_no, r.room_no";
    private static final String FILTER_STATUS = "SELECT r.*, rt.name as room_type_name, rt.description as room_type_description FROM Room r LEFT JOIN RoomType rt ON r.room_type_id = rt.room_type_id WHERE r.status = ? ORDER BY r.floor_no, r.room_no";
    private static final String FILTER_ROOM_TYPE = "SELECT r.*, rt.name as room_type_name, rt.description as room_type_description FROM Room r LEFT JOIN RoomType rt ON r.room_type_id = rt.room_type_id WHERE r.room_type_id = ? ORDER BY r.floor_no, r.room_no";

    public List<RoomDTO> getAllRooms() { return DatabaseConnection.executeQueryList(SELECT_ALL, this::mapToDTO); }
    public RoomDTO getRoomById(int id) { return DatabaseConnection.executeQuerySingle(SELECT_BY_ID, this::mapToDTO, id); }

    public boolean addRoom(RoomDTO r) {
        return DatabaseConnection.executeUpdate(INSERT_SQL, r.getRoomNo(), r.getFloorNo(), r.getRoomTypeId(), r.getStatus(), r.getNote());
    }

    public boolean updateRoom(RoomDTO r) {
        return DatabaseConnection.executeUpdate(UPDATE_SQL, r.getRoomNo(), r.getFloorNo(), r.getRoomTypeId(), r.getStatus(), r.getNote(), r.getRoomId());
    }

    public boolean deleteRoom(int id) { return DatabaseConnection.executeUpdate(DELETE_SQL, id); }

    public List<RoomDTO> searchRooms(String kw) { return DatabaseConnection.executeQueryList(SEARCH, this::mapToDTO, "%"+kw+"%", "%"+kw+"%"); }
    public List<RoomDTO> filterRoomsByFloor(int floor) { return DatabaseConnection.executeQueryList(FILTER_FLOOR, this::mapToDTO, floor); }
    public List<RoomDTO> filterRoomsByStatus(String status) { return DatabaseConnection.executeQueryList(FILTER_STATUS, this::mapToDTO, status); }
    public List<RoomDTO> filterRoomsByRoomType(int typeId) { return DatabaseConnection.executeQueryList(FILTER_ROOM_TYPE, this::mapToDTO, typeId); }

    private RoomDTO mapToDTO(java.sql.ResultSet rs) throws java.sql.SQLException {
        RoomDTO r = new RoomDTO(
                rs.getInt("room_id"),
                rs.getString("room_no"),
                rs.getByte("floor_no"),
                rs.getInt("room_type_id"),
                rs.getString("status"),
                rs.getString("note")
        );
        r.setRoomTypeName(rs.getString("room_type_name"));
        r.setRoomTypeDescription(rs.getString("room_type_description"));
        return r;
    }
}