package DAO;

import DTO.ServiceOrderDTO;
import util.DatabaseConnection;
import util.ResultSetMapper;
import java.sql.Timestamp;
import java.util.List;

public class ServiceOrderDAO {

    private static final String SELECT_ALL = "SELECT * FROM ServiceOrder ORDER BY service_order_id DESC";
    private static final String SELECT_BY_ID = "SELECT * FROM ServiceOrder WHERE service_order_id = ?";
    private static final String INSERT_SQL = "INSERT INTO ServiceOrder (booking_room_id, service_id, qty, unit_price, ordered_at, ordered_by, note) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE ServiceOrder SET booking_room_id=?, service_id=?, qty=?, unit_price=?, ordered_at=?, ordered_by=?, note=? WHERE service_order_id=?";
    private static final String DELETE_SQL = "DELETE FROM ServiceOrder WHERE service_order_id=?";
    private static final String SEARCH_SERVICE = "SELECT so.* FROM ServiceOrder so JOIN service s ON so.service_id = s.service_id WHERE s.name LIKE ? ORDER BY so.ordered_at DESC";
    private static final String SELECT_BY_BOOKING = "SELECT * FROM ServiceOrder WHERE booking_room_id = ?";

    public List<ServiceOrderDTO> getAll() { return DatabaseConnection.executeQueryList(SELECT_ALL, this::mapToDTO); }
    public ServiceOrderDTO getById(int id) { return DatabaseConnection.executeQuerySingle(SELECT_BY_ID, this::mapToDTO, id); }

    public int insert(ServiceOrderDTO s) {
        return DatabaseConnection.executeInsert(INSERT_SQL,
                s.getBookingRoomId(), s.getServiceId(), s.getQuantity(), s.getUnitPrice(),
                s.getOrderedAt() != null ? Timestamp.valueOf(s.getOrderedAt()) : null,
                s.getOrderedBy(), s.getNote()
        );
    }

    public boolean update(ServiceOrderDTO s) {
        return DatabaseConnection.executeUpdate(UPDATE_SQL,
                s.getBookingRoomId(), s.getServiceId(), s.getQuantity(), s.getUnitPrice(),
                s.getOrderedAt() != null ? Timestamp.valueOf(s.getOrderedAt()) : null,
                s.getOrderedBy(), s.getNote(), s.getServiceOrderId()
        );
    }

    public boolean delete(int id) { return DatabaseConnection.executeUpdate(DELETE_SQL, id); }

    public List<ServiceOrderDTO> searchByServiceName(String kw) {
        return DatabaseConnection.executeQueryList(SEARCH_SERVICE, this::mapToDTO, "%"+kw.trim()+"%");
    }

    public List<ServiceOrderDTO> getByBookingId(int bookingRoomId) {
        return DatabaseConnection.executeQueryList(SELECT_BY_BOOKING, this::mapToDTO, bookingRoomId);
    }

    private ServiceOrderDTO mapToDTO(java.sql.ResultSet rs) throws java.sql.SQLException {
        Timestamp ts = rs.getTimestamp("ordered_at");
        return new ServiceOrderDTO(
                rs.getInt("service_order_id"),
                rs.getInt("booking_room_id"),
                rs.getInt("service_id"),
                rs.getInt("qty"),
                rs.getDouble("unit_price"),
                ts != null ? ts.toLocalDateTime() : null,
                rs.getString("ordered_by"),
                rs.getString("note")
        );
    }
}