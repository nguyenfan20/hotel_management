package DTO;

import java.sql.Date;

public class HousekeepingTaskDTO {
    private int taskId;
    private int roomId;
    private Date taskDate;
    private String taskType;
    private Integer assignedTo;
    private String status;
    private String note;
    private String assignedName;

    public HousekeepingTaskDTO() {
    }

    public HousekeepingTaskDTO(int taskId, int roomId, Date taskDate, String taskType,
                               Integer assignedTo, String status, String note) {
        this.taskId = taskId;
        this.roomId = roomId;
        this.taskDate = taskDate;
        this.taskType = taskType;
        this.assignedTo = assignedTo;
        this.status = status;
        this.note = note;
    }

    public HousekeepingTaskDTO(int roomId, Date taskDate, String taskType,
                               Integer assignedTo, String status, String note) {
        this.roomId = roomId;
        this.taskDate = taskDate;
        this.taskType = taskType;
        this.assignedTo = assignedTo;
        this.status = status;
        this.note = note;
    }

    // Getters and Setters
    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public Date getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(Date taskDate) {
        this.taskDate = taskDate;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public Integer getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Integer assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getAssignedName() {
        return assignedName;
    }

    public void setAssignedName(String assignedName) {
        this.assignedName = assignedName;
    }

    @Override
    public String toString() {
        return "HousekeepingTaskDTO{" +
                "taskId=" + taskId +
                ", roomId=" + roomId +
                ", taskDate=" + taskDate +
                ", taskType='" + taskType + '\'' +
                ", assignedTo=" + assignedTo +
                ", status='" + status + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}
