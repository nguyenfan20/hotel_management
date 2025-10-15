package DAO;

import DTO.HousekeepingTaskDTO;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HousekeepingTaskDAO {

    // Lấy tất cả nhiệm vụ dọn dẹp
    public List<HousekeepingTaskDTO> getAllTasks() {
        List<HousekeepingTaskDTO> tasks = new ArrayList<>();
        String sql = "SELECT ht.*, ua.full_name as assigned_name " +
                "FROM HousekeepingTask ht " +
                "LEFT JOIN UserAccount ua ON ht.assigned_to = ua.user_id " +
                "ORDER BY ht.task_id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                HousekeepingTaskDTO task = new HousekeepingTaskDTO(
                        rs.getInt("task_id"),
                        rs.getInt("room_id"),
                        rs.getDate("task_date"),
                        rs.getString("task_type"),
                        (Integer) rs.getObject("assigned_to"),
                        rs.getString("status"),
                        rs.getString("note")
                );
                task.setAssignedName(rs.getString("assigned_name"));
                tasks.add(task);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách nhiệm vụ dọn dẹp: " + e.getMessage());
            e.printStackTrace();
        }

        return tasks;
    }

    // Lấy nhiệm vụ theo ID
    public HousekeepingTaskDTO getTaskById(int taskId) {
        String sql = "SELECT ht.*, ua.full_name as assigned_name " +
                "FROM HousekeepingTask ht " +
                "LEFT JOIN UserAccount ua ON ht.assigned_to = ua.user_id " +
                "WHERE ht.task_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, taskId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                HousekeepingTaskDTO task = new HousekeepingTaskDTO(
                        rs.getInt("task_id"),
                        rs.getInt("room_id"),
                        rs.getDate("task_date"),
                        rs.getString("task_type"),
                        (Integer) rs.getObject("assigned_to"),
                        rs.getString("status"),
                        rs.getString("note")
                );
                task.setAssignedName(rs.getString("assigned_name"));
                return task;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy nhiệm vụ dọn dẹp: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // Thêm nhiệm vụ mới
    public boolean addTask(HousekeepingTaskDTO task) {
        String sql = "INSERT INTO HousekeepingTask (room_id, task_date, task_type, assigned_to, status, note) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, task.getRoomId());
            pstmt.setDate(2, task.getTaskDate());
            pstmt.setString(3, task.getTaskType());
            if (task.getAssignedTo() != null) {
                pstmt.setInt(4, task.getAssignedTo());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }
            pstmt.setString(5, task.getStatus());
            pstmt.setString(6, task.getNote());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm nhiệm vụ dọn dẹp: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật nhiệm vụ
    public boolean updateTask(HousekeepingTaskDTO task) {
        String sql = "UPDATE HousekeepingTask SET room_id = ?, task_date = ?, task_type = ?, assigned_to = ?, status = ?, note = ? WHERE task_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, task.getRoomId());
            pstmt.setDate(2, task.getTaskDate());
            pstmt.setString(3, task.getTaskType());
            if (task.getAssignedTo() != null) {
                pstmt.setInt(4, task.getAssignedTo());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }
            pstmt.setString(5, task.getStatus());
            pstmt.setString(6, task.getNote());
            pstmt.setInt(7, task.getTaskId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật nhiệm vụ dọn dẹp: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Xóa nhiệm vụ
    public boolean deleteTask(int taskId) {
        String sql = "DELETE FROM HousekeepingTask WHERE task_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, taskId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa nhiệm vụ dọn dẹp: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Tìm kiếm nhiệm vụ theo từ khóa
    public List<HousekeepingTaskDTO> searchTasks(String keyword) {
        List<HousekeepingTaskDTO> tasks = new ArrayList<>();
        String sql = "SELECT ht.*, ua.full_name as assigned_name " +
                "FROM HousekeepingTask ht " +
                "LEFT JOIN UserAccount ua ON ht.assigned_to = ua.user_id " +
                "WHERE ht.task_type LIKE ? OR ht.status LIKE ? OR ht.note LIKE ? " +
                "ORDER BY ht.task_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                HousekeepingTaskDTO task = new HousekeepingTaskDTO(
                        rs.getInt("task_id"),
                        rs.getInt("room_id"),
                        rs.getDate("task_date"),
                        rs.getString("task_type"),
                        (Integer) rs.getObject("assigned_to"),
                        rs.getString("status"),
                        rs.getString("note")
                );
                task.setAssignedName(rs.getString("assigned_name"));
                tasks.add(task);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm nhiệm vụ dọn dẹp: " + e.getMessage());
            e.printStackTrace();
        }

        return tasks;
    }

    // Lọc nhiệm vụ theo trạng thái
    public List<HousekeepingTaskDTO> filterTasksByStatus(String status) {
        List<HousekeepingTaskDTO> tasks = new ArrayList<>();
        String sql = "SELECT ht.*, ua.full_name as assigned_name " +
                "FROM HousekeepingTask ht " +
                "LEFT JOIN UserAccount ua ON ht.assigned_to = ua.user_id " +
                "WHERE ht.status = ? ORDER BY ht.task_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                HousekeepingTaskDTO task = new HousekeepingTaskDTO(
                        rs.getInt("task_id"),
                        rs.getInt("room_id"),
                        rs.getDate("task_date"),
                        rs.getString("task_type"),
                        (Integer) rs.getObject("assigned_to"),
                        rs.getString("status"),
                        rs.getString("note")
                );
                task.setAssignedName(rs.getString("assigned_name"));
                tasks.add(task);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lọc nhiệm vụ dọn dẹp theo trạng thái: " + e.getMessage());
            e.printStackTrace();
        }

        return tasks;
    }

    // Lọc nhiệm vụ theo phòng
    public List<HousekeepingTaskDTO> filterTasksByRoom(int roomId) {
        List<HousekeepingTaskDTO> tasks = new ArrayList<>();
        String sql = "SELECT ht.*, ua.full_name as assigned_name " +
                "FROM HousekeepingTask ht " +
                "LEFT JOIN UserAccount ua ON ht.assigned_to = ua.user_id " +
                "WHERE ht.room_id = ? ORDER BY ht.task_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                HousekeepingTaskDTO task = new HousekeepingTaskDTO(
                        rs.getInt("task_id"),
                        rs.getInt("room_id"),
                        rs.getDate("task_date"),
                        rs.getString("task_type"),
                        (Integer) rs.getObject("assigned_to"),
                        rs.getString("status"),
                        rs.getString("note")
                );
                task.setAssignedName(rs.getString("assigned_name"));
                tasks.add(task);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lọc nhiệm vụ dọn dẹp theo phòng: " + e.getMessage());
            e.printStackTrace();
        }

        return tasks;
    }
}
