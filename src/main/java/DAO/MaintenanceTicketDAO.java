package DAO;

import DTO.MaintenanceTicketDTO;
import util.DatabaseConnection;
import util.ResultSetMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MaintenanceTicketDAO {

    private static final String SELECT_ALL = "SELECT mt.*, ua.full_name as assigned_name FROM MaintenanceTicket mt LEFT JOIN UserAccount ua ON mt.assigned_to = ua.user_id ORDER BY mt.ticket_id";
    private static final String SELECT_BY_ID = "SELECT mt.*, ua.full_name as assigned_name FROM MaintenanceTicket mt LEFT JOIN UserAccount ua ON mt.assigned_to = ua.user_id WHERE mt.ticket_id = ?";
    private static final String INSERT_SQL = "INSERT INTO MaintenanceTicket (room_id, title, description, priority, opened_at, closed_at, status, assigned_to) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE MaintenanceTicket SET room_id = ?, title = ?, description = ?, priority = ?, opened_at = ?, closed_at = ?, status = ?, assigned_to = ? WHERE ticket_id = ?";
    private static final String DELETE_SQL = "DELETE FROM MaintenanceTicket WHERE ticket_id = ?";
    private static final String SEARCH_SQL = "SELECT mt.*, ua.full_name as assigned_name FROM MaintenanceTicket mt LEFT JOIN UserAccount ua ON mt.assigned_to = ua.user_id WHERE mt.title LIKE ? OR mt.description LIKE ? OR mt.status LIKE ? ORDER BY mt.ticket_id";
    private static final String FILTER_BY_STATUS = "SELECT mt.*, ua.full_name as assigned_name FROM MaintenanceTicket mt LEFT JOIN UserAccount ua ON mt.assigned_to = ua.user_id WHERE mt.status = ? ORDER BY mt.ticket_id";
    private static final String FILTER_BY_ROOM = "SELECT mt.*, ua.full_name as assigned_name FROM MaintenanceTicket mt LEFT JOIN UserAccount ua ON mt.assigned_to = ua.user_id WHERE mt.room_id = ? ORDER BY mt.ticket_id";

    public List<MaintenanceTicketDTO> getAllTickets() {
        return DatabaseConnection.executeQueryList(SELECT_ALL, this::mapToDTO);
    }

    public MaintenanceTicketDTO getTicketById(int ticketId) {
        return DatabaseConnection.executeQuerySingle(SELECT_BY_ID, this::mapToDTO, ticketId);
    }

    public boolean addTicket(MaintenanceTicketDTO ticket) {
        return DatabaseConnection.executeUpdate(INSERT_SQL,
                ticket.getRoomId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getPriority(),
                ticket.getOpenedAt(),
                ticket.getClosedAt(),
                ticket.getStatus(),
                ticket.getAssignedTo()
        );
    }

    public boolean updateTicket(MaintenanceTicketDTO ticket) {
        return DatabaseConnection.executeUpdate(UPDATE_SQL,
                ticket.getRoomId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getPriority(),
                ticket.getOpenedAt(),
                ticket.getClosedAt(),
                ticket.getStatus(),
                ticket.getAssignedTo(),
                ticket.getTicketId()
        );
    }

    public boolean deleteTicket(int ticketId) {
        return DatabaseConnection.executeUpdate(DELETE_SQL, ticketId);
    }

    public List<MaintenanceTicketDTO> searchTickets(String keyword) {
        String pattern = "%" + keyword + "%";
        return DatabaseConnection.executeQueryList(SEARCH_SQL, this::mapToDTO, pattern, pattern, pattern);
    }

    public List<MaintenanceTicketDTO> filterTicketsByStatus(String status) {
        return DatabaseConnection.executeQueryList(FILTER_BY_STATUS, this::mapToDTO, status);
    }

    public List<MaintenanceTicketDTO> filterTicketsByRoom(int roomId) {
        return DatabaseConnection.executeQueryList(FILTER_BY_ROOM, this::mapToDTO, roomId);
    }

    private MaintenanceTicketDTO mapToDTO(ResultSet rs) throws SQLException {
        MaintenanceTicketDTO ticket = new MaintenanceTicketDTO(
                rs.getInt("ticket_id"),
                rs.getInt("room_id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("priority"),
                rs.getTimestamp("opened_at"),
                rs.getTimestamp("closed_at"),
                rs.getString("status"),
                (Integer) rs.getObject("assigned_to")
        );
        ticket.setAssignedName(rs.getString("assigned_name"));
        return ticket;
    }
}