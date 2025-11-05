package DAO;

import DTO.GuestDTO;
import util.DatabaseConnection;
import util.ResultSetMapper;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class GuestDAO {

    private static final String INSERT_SQL = "INSERT INTO Guest (booking_room_id, full_name, gender, dob, id_card, nationality) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE Guest SET booking_room_id = ?, full_name = ?, gender = ?, dob = ?, id_card = ?, nationality = ? WHERE guest_id = ?";
    private static final String DELETE_SQL = "UPDATE Guest SET is_hide = 1 WHERE guest_id = ?";
    private static final String SELECT_BY_ID = "SELECT * FROM Guest WHERE guest_id = ? AND is_hide = 0";
    private static final String SELECT_BY_BOOKING_ROOM_ID = "SELECT * FROM Guest WHERE booking_room_id = ? AND is_hide = 0";
    private static final String SEARCH_SQL = "SELECT * FROM Guest WHERE (full_name LIKE ? OR id_card LIKE ?) AND is_hide = 0";
    private static final String SELECT_ALL = "SELECT * FROM Guest WHERE is_hide = 0";

    public boolean insert(GuestDTO guest) {
        return DatabaseConnection.executeUpdate(INSERT_SQL,
                guest.getBookingRoomId(),
                guest.getFullName(),
                guest.getGender(),
                Date.valueOf(guest.getDob()),
                guest.getIdCard(),
                guest.getNationality()
        );
    }

    public boolean update(GuestDTO guest) {
        return DatabaseConnection.executeUpdate(UPDATE_SQL,
                guest.getBookingRoomId(),
                guest.getFullName(),
                guest.getGender(),
                Date.valueOf(guest.getDob()),
                guest.getIdCard(),
                guest.getNationality(),
                guest.getGuestId()
        );
    }

    public boolean delete(int guestId) {
        return DatabaseConnection.executeUpdate(DELETE_SQL, guestId);
    }

    public GuestDTO getById(int guestId) {
        return DatabaseConnection.executeQuerySingle(SELECT_BY_ID, this::mapToDTO, guestId);
    }

    public List<GuestDTO> getByBookingRoomId(int bookingRoomId) {
        return DatabaseConnection.executeQueryList(SELECT_BY_BOOKING_ROOM_ID, this::mapToDTO, bookingRoomId);
    }

    public List<GuestDTO> search(String keyword) {
        String pattern = "%" + keyword + "%";
        return DatabaseConnection.executeQueryList(SEARCH_SQL, this::mapToDTO, pattern, pattern);
    }

    public List<GuestDTO> getAll() {
        return DatabaseConnection.executeQueryList(SELECT_ALL, this::mapToDTO);
    }

    private GuestDTO mapToDTO(ResultSet rs) throws SQLException {
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