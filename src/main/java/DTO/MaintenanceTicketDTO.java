package DTO;

import java.sql.Timestamp;

public class MaintenanceTicketDTO {
    private int ticketId;
    private int roomId;
    private String title;
    private String description;
    private String priority;
    private Timestamp openedAt;
    private Timestamp closedAt;
    private String status;
    private Integer assignedTo;
    private String assignedName;

    public MaintenanceTicketDTO() {
    }

    public MaintenanceTicketDTO(int ticketId, int roomId, String title, String description,
                                String priority, Timestamp openedAt, Timestamp closedAt,
                                String status, Integer assignedTo) {
        this.ticketId = ticketId;
        this.roomId = roomId;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.openedAt = openedAt;
        this.closedAt = closedAt;
        this.status = status;
        this.assignedTo = assignedTo;
    }

    public MaintenanceTicketDTO(int roomId, String title, String description,
                                String priority, Timestamp openedAt, Timestamp closedAt,
                                String status, Integer assignedTo) {
        this.roomId = roomId;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.openedAt = openedAt;
        this.closedAt = closedAt;
        this.status = status;
        this.assignedTo = assignedTo;
    }

    // Getters and Setters
    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Timestamp getOpenedAt() {
        return openedAt;
    }

    public void setOpenedAt(Timestamp openedAt) {
        this.openedAt = openedAt;
    }

    public Timestamp getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(Timestamp closedAt) {
        this.closedAt = closedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Integer assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getAssignedName() {
        return assignedName;
    }

    public void setAssignedName(String assignedName) {
        this.assignedName = assignedName;
    }

    @Override
    public String toString() {
        return "MaintenanceTicketDTO{" +
                "ticketId=" + ticketId +
                ", roomId=" + roomId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", priority='" + priority + '\'' +
                ", openedAt=" + openedAt +
                ", closedAt=" + closedAt +
                ", status='" + status + '\'' +
                ", assignedTo=" + assignedTo +
                '}';
    }
}
