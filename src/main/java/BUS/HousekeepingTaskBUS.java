package BUS;

import DAO.HousekeepingTaskDAO;
import DTO.HousekeepingTaskDTO;
import java.util.List;

public class HousekeepingTaskBUS {
    private HousekeepingTaskDAO taskDAO;

    public HousekeepingTaskBUS() {
        this.taskDAO = new HousekeepingTaskDAO();
    }

    // Lấy tất cả nhiệm vụ dọn dẹp
    public List<HousekeepingTaskDTO> getAllTasks() {
        return taskDAO.getAllTasks();
    }

    // Lấy nhiệm vụ theo ID
    public HousekeepingTaskDTO getTaskById(int taskId) {
        if (taskId <= 0) {
            System.err.println("ID nhiệm vụ không hợp lệ");
            return null;
        }
        return taskDAO.getTaskById(taskId);
    }

    // Thêm nhiệm vụ mới
    public boolean addTask(HousekeepingTaskDTO task) {
        // Validate dữ liệu
        if (!validateTask(task)) {
            return false;
        }

        return taskDAO.addTask(task);
    }

    // Cập nhật nhiệm vụ
    public boolean updateTask(HousekeepingTaskDTO task) {
        // Validate dữ liệu
        if (task.getTaskId() <= 0) {
            System.err.println("ID nhiệm vụ không hợp lệ");
            return false;
        }

        if (!validateTask(task)) {
            return false;
        }

        return taskDAO.updateTask(task);
    }

    // Xóa nhiệm vụ
    public boolean deleteTask(int taskId) {
        if (taskId <= 0) {
            System.err.println("ID nhiệm vụ không hợp lệ");
            return false;
        }

        return taskDAO.deleteTask(taskId);
    }

    // Tìm kiếm nhiệm vụ theo từ khóa
    public List<HousekeepingTaskDTO> searchTasks(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllTasks();
        }
        return taskDAO.searchTasks(keyword.trim());
    }

    // Lọc nhiệm vụ theo trạng thái
    public List<HousekeepingTaskDTO> filterTasksByStatus(String status) {
        if (status == null || status.trim().isEmpty() || status.equals("Tất cả")) {
            return getAllTasks();
        }
        return taskDAO.filterTasksByStatus(status);
    }

    // Lọc nhiệm vụ theo phòng
    public List<HousekeepingTaskDTO> filterTasksByRoom(int roomId) {
        if (roomId <= 0) {
            return getAllTasks();
        }
        return taskDAO.filterTasksByRoom(roomId);
    }

    // Validate dữ liệu nhiệm vụ
    private boolean validateTask(HousekeepingTaskDTO task) {
        if (task == null) {
            System.err.println("Dữ liệu nhiệm vụ không được null");
            return false;
        }

        if (task.getRoomId() <= 0) {
            System.err.println("ID phòng không hợp lệ");
            return false;
        }

        if (task.getTaskDate() == null) {
            System.err.println("Ngày nhiệm vụ không được để trống");
            return false;
        }

        if (task.getTaskType() == null || task.getTaskType().trim().isEmpty()) {
            System.err.println("Loại nhiệm vụ không được để trống");
            return false;
        }

        if (task.getStatus() == null || task.getStatus().trim().isEmpty()) {
            System.err.println("Trạng thái không được để trống");
            return false;
        }

        // Kiểm tra trạng thái hợp lệ
        String status = task.getStatus();
        if (!status.equals("Pending") && !status.equals("In Progress") && !status.equals("Completed")) {
            System.err.println("Trạng thái không hợp lệ. Chỉ chấp nhận: Pending, In Progress, Completed");
            return false;
        }

        return true;
    }
}
