package DAO;

import DTO.MaintenanceTicketDTO;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceTicketDAO {

    // Lấy tất cả phiếu bảo trì
    public List<MaintenanceTicketDTO> getAllTickets() {
        List<MaintenanceTicketDTO> tickets = new ArrayList<>();
        String sql = "SELECT mt.*, ua.full_name as assigned_name " +
                "FROM MaintenanceTicket mt " +
                "LEFT JOIN UserAccount ua ON mt.assigned_to = ua.user_id " +
                "ORDER BY mt.ticket_id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
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
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách phiếu bảo trì: " + e.getMessage());
            e.printStackTrace();
        }

        return tickets;
    }

    // Lấy phiếu bảo trì theo ID
    public MaintenanceTicketDTO getTicketById(int ticketId) {
        String sql = "SELECT mt.*, ua.full_name as assigned_name " +
                "FROM MaintenanceTicket mt " +
                "LEFT JOIN UserAccount ua ON mt.assigned_to = ua.user_id " +
                "WHERE mt.ticket_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ticketId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
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
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy phiếu bảo trì: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // Thêm phiếu bảo trì mới
    public boolean addTicket(MaintenanceTicketDTO ticket) {
        String sql = "INSERT INTO MaintenanceTicket (room_id, title, description, priority, opened_at, closed_at, status, assigned_to) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ticket.getRoomId());
            pstmt.setString(2, ticket.getTitle());
            pstmt.setString(3, ticket.getDescription());
            pstmt.setString(4, ticket.getPriority());
            pstmt.setTimestamp(5, ticket.getOpenedAt());
            if (ticket.getClosedAt() != null) {
                pstmt.setTimestamp(6, ticket.getClosedAt());
            } else {
                pstmt.setNull(6, Types.TIMESTAMP);
            }
            pstmt.setString(7, ticket.getStatus());
            if (ticket.getAssignedTo() != null) {
                pstmt.setInt(8, ticket.getAssignedTo());
            } else {
                pstmt.setNull(8, Types.INTEGER);
            }

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm phiếu bảo trì: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật phiếu bảo trì
    public boolean updateTicket(MaintenanceTicketDTO ticket) {
        String sql = "UPDATE MaintenanceTicket SET room_id = ?, title = ?, description = ?, priority = ?, opened_at = ?, closed_at = ?, status = ?, assigned_to = ? WHERE ticket_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ticket.getRoomId());
            pstmt.setString(2, ticket.getTitle());
            pstmt.setString(3, ticket.getDescription());
            pstmt.setString(4, ticket.getPriority());
            pstmt.setTimestamp(5, ticket.getOpenedAt());
            if (ticket.getClosedAt() != null) {
                pstmt.setTimestamp(6, ticket.getClosedAt());
            } else {
                pstmt.setNull(6, Types.TIMESTAMP);
            }
            pstmt.setString(7, ticket.getStatus());
            if (ticket.getAssignedTo() != null) {
                pstmt.setInt(8, ticket.getAssignedTo());
            } else {
                pstmt.setNull(8, Types.INTEGER);
            }
            pstmt.setInt(9, ticket.getTicketId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật phiếu bảo trì: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Xóa phiếu bảo trì
    public boolean deleteTicket(int ticketId) {
        String sql = "DELETE FROM MaintenanceTicket WHERE ticket_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ticketId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa phiếu bảo trì: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Tìm kiếm phiếu bảo trì theo từ khóa
    public List<MaintenanceTicketDTO> searchTickets(String keyword) {
        List<MaintenanceTicketDTO> tickets = new ArrayList<>();
        String sql = "SELECT mt.*, ua.full_name as assigned_name " +
                "FROM MaintenanceTicket mt " +
                "LEFT JOIN UserAccount ua ON mt.assigned_to = ua.user_id " +
                "WHERE mt.title LIKE ? OR mt.description LIKE ? OR mt.status LIKE ? " +
                "ORDER BY mt.ticket_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
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
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm phiếu bảo trì: " + e.getMessage());
            e.printStackTrace();
        }

        return tickets;
    }

    // Lọc phiếu bảo trì theo trạng thái
    public List<MaintenanceTicketDTO> filterTicketsByStatus(String status) {
        List<MaintenanceTicketDTO> tickets = new ArrayList<>();
        String sql = "SELECT mt.*, ua.full_name as assigned_name " +
                "FROM MaintenanceTicket mt " +
                "LEFT JOIN UserAccount ua ON mt.assigned_to = ua.user_id " +
                "WHERE mt.status = ? ORDER BY mt.ticket_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
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
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lọc phiếu bảo trì theo trạng thái: " + e.getMessage());
            e.printStackTrace();
        }

        return tickets;
    }

    // Lọc phiếu bảo trì theo phòng
    public List<MaintenanceTicketDTO> filterTicketsByRoom(int roomId) {
        List<MaintenanceTicketDTO> tickets = new ArrayList<>();
        String sql = "SELECT mt.*, ua.full_name as assigned_name " +
                "FROM MaintenanceTicket mt " +
                "LEFT JOIN UserAccount ua ON mt.assigned_to = ua.user_id " +
                "WHERE mt.room_id = ? ORDER BY mt.ticket_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
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
                tickets.add(ticket);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lọc phiếu bảo trì theo phòng: " + e.getMessage());
            e.printStackTrace();
        }

        return tickets;
    }
}
