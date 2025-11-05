package DAO;

import DTO.HousekeepingTaskDTO;
import util.DatabaseConnection;
import util.ResultSetMapper;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class HousekeepingTaskDAO {

    private static final String SELECT_ALL = "SELECT ht.*, ua.full_name as assigned_name FROM HousekeepingTask ht LEFT JOIN UserAccount ua ON ht.assigned_to = ua.user_id ORDER BY ht.task_id";
    private static final String SELECT_BY_ID = "SELECT ht.*, ua.full_name as assigned_name FROM HousekeepingTask ht LEFT JOIN UserAccount ua ON ht.assigned_to = ua.user_id WHERE ht.task_id = ?";
    private static final String INSERT_SQL = "INSERT INTO HousekeepingTask (room_id, task_date, task_type, assigned_to, status, note) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE HousekeepingTask SET room_id = ?, task_date = ?, task_type = ?, assigned_to = ?, status = ?, note = ? WHERE task_id = ?";
    private static final String DELETE_SQL = "DELETE FROM HousekeepingTask WHERE task_id = ?";
    private static final String SEARCH_SQL = "SELECT ht.*, ua.full_name as assigned_name FROM HousekeepingTask ht LEFT JOIN UserAccount ua ON ht.assigned_to = ua.user_id WHERE ht.task_type LIKE ? OR ht.status LIKE ? OR ht.note LIKE ? ORDER BY ht.task_id";
    private static final String FILTER_BY_STATUS = "SELECT ht.*, ua.full_name as assigned_name FROM HousekeepingTask ht LEFT JOIN UserAccount ua ON ht.assigned_to = ua.user_id WHERE ht.status = ? ORDER BY ht.task_id";
    private static final String FILTER_BY_ROOM = "SELECT ht.*, ua.full_name as assigned_name FROM HousekeepingTask ht LEFT JOIN UserAccount ua ON ht.assigned_to = ua.user_id WHERE ht.room_id = ? ORDER BY ht.task_id";

    public List<HousekeepingTaskDTO> getAllTasks() {
        return DatabaseConnection.executeQueryList(SELECT_ALL, this::mapToDTO);
    }

    public HousekeepingTaskDTO getTaskById(int taskId) {
        return DatabaseConnection.executeQuerySingle(SELECT_BY_ID, this::mapToDTO, taskId);
    }

    public boolean addTask(HousekeepingTaskDTO task) {
        return DatabaseConnection.executeUpdate(INSERT_SQL,
                task.getRoomId(),
                task.getTaskDate(),
                task.getTaskType(),
                task.getAssignedTo(),
                task.getStatus(),
                task.getNote()
        );
    }

    public boolean updateTask(HousekeepingTaskDTO task) {
        return DatabaseConnection.executeUpdate(UPDATE_SQL,
                task.getRoomId(),
                task.getTaskDate(),
                task.getTaskType(),
                task.getAssignedTo(),
                task.getStatus(),
                task.getNote(),
                task.getTaskId()
        );
    }

    public boolean deleteTask(int taskId) {
        return DatabaseConnection.executeUpdate(DELETE_SQL, taskId);
    }

    public List<HousekeepingTaskDTO> searchTasks(String keyword) {
        String pattern = "%" + keyword + "%";
        return DatabaseConnection.executeQueryList(SEARCH_SQL, this::mapToDTO, pattern, pattern, pattern);
    }

    public List<HousekeepingTaskDTO> filterTasksByStatus(String status) {
        return DatabaseConnection.executeQueryList(FILTER_BY_STATUS, this::mapToDTO, status);
    }

    public List<HousekeepingTaskDTO> filterTasksByRoom(int roomId) {
        return DatabaseConnection.executeQueryList(FILTER_BY_ROOM, this::mapToDTO, roomId);
    }

    private HousekeepingTaskDTO mapToDTO(ResultSet rs) throws SQLException {
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
}