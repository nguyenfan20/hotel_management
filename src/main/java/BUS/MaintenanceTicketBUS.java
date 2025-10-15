package BUS;

import DAO.MaintenanceTicketDAO;
import DTO.MaintenanceTicketDTO;
import java.util.List;

public class MaintenanceTicketBUS {
    private MaintenanceTicketDAO ticketDAO;

    public MaintenanceTicketBUS() {
        this.ticketDAO = new MaintenanceTicketDAO();
    }

    // Lấy tất cả phiếu bảo trì
    public List<MaintenanceTicketDTO> getAllTickets() {
        return ticketDAO.getAllTickets();
    }

    // Lấy phiếu bảo trì theo ID
    public MaintenanceTicketDTO getTicketById(int ticketId) {
        if (ticketId <= 0) {
            System.err.println("ID phiếu bảo trì không hợp lệ");
            return null;
        }
        return ticketDAO.getTicketById(ticketId);
    }

    // Thêm phiếu bảo trì mới
    public boolean addTicket(MaintenanceTicketDTO ticket) {
        // Validate dữ liệu
        if (!validateTicket(ticket)) {
            return false;
        }

        return ticketDAO.addTicket(ticket);
    }

    // Cập nhật phiếu bảo trì
    public boolean updateTicket(MaintenanceTicketDTO ticket) {
        // Validate dữ liệu
        if (ticket.getTicketId() <= 0) {
            System.err.println("ID phiếu bảo trì không hợp lệ");
            return false;
        }

        if (!validateTicket(ticket)) {
            return false;
        }

        return ticketDAO.updateTicket(ticket);
    }

    // Xóa phiếu bảo trì
    public boolean deleteTicket(int ticketId) {
        if (ticketId <= 0) {
            System.err.println("ID phiếu bảo trì không hợp lệ");
            return false;
        }

        return ticketDAO.deleteTicket(ticketId);
    }

    // Tìm kiếm phiếu bảo trì theo từ khóa
    public List<MaintenanceTicketDTO> searchTickets(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllTickets();
        }
        return ticketDAO.searchTickets(keyword.trim());
    }

    // Lọc phiếu bảo trì theo trạng thái
    public List<MaintenanceTicketDTO> filterTicketsByStatus(String status) {
        if (status == null || status.trim().isEmpty() || status.equals("Tất cả")) {
            return getAllTickets();
        }
        return ticketDAO.filterTicketsByStatus(status);
    }

    // Lọc phiếu bảo trì theo phòng
    public List<MaintenanceTicketDTO> filterTicketsByRoom(int roomId) {
        if (roomId <= 0) {
            return getAllTickets();
        }
        return ticketDAO.filterTicketsByRoom(roomId);
    }

    // Validate dữ liệu phiếu bảo trì
    private boolean validateTicket(MaintenanceTicketDTO ticket) {
        if (ticket == null) {
            System.err.println("Dữ liệu phiếu bảo trì không được null");
            return false;
        }

        if (ticket.getRoomId() <= 0) {
            System.err.println("ID phòng không hợp lệ");
            return false;
        }

        if (ticket.getTitle() == null || ticket.getTitle().trim().isEmpty()) {
            System.err.println("Tiêu đề không được để trống");
            return false;
        }

        if (ticket.getPriority() == null || ticket.getPriority().trim().isEmpty()) {
            System.err.println("Mức độ ưu tiên không được để trống");
            return false;
        }

        // Kiểm tra mức độ ưu tiên hợp lệ
        String priority = ticket.getPriority();
        if (!priority.equals("Low") && !priority.equals("Medium") && !priority.equals("High") && !priority.equals("Critical")) {
            System.err.println("Mức độ ưu tiên không hợp lệ. Chỉ chấp nhận: Low, Medium, High, Critical");
            return false;
        }

        if (ticket.getOpenedAt() == null) {
            System.err.println("Ngày mở không được để trống");
            return false;
        }

        if (ticket.getStatus() == null || ticket.getStatus().trim().isEmpty()) {
            System.err.println("Trạng thái không được để trống");
            return false;
        }

        // Kiểm tra trạng thái hợp lệ
        String status = ticket.getStatus();
        if (!status.equals("Open") && !status.equals("In Progress") && !status.equals("Resolved") && !status.equals("Closed")) {
            System.err.println("Trạng thái không hợp lệ. Chỉ chấp nhận: Open, In Progress, Resolved, Closed");
            return false;
        }

        return true;
    }
}
