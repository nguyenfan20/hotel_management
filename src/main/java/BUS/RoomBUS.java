package BUS;

import DAO.RoomDAO;
import DTO.RoomDTO;
import java.util.List;

public class RoomBUS {
    private RoomDAO roomDAO;

    public RoomBUS() {
        this.roomDAO = new RoomDAO();
    }

    // Lấy tất cả phòng
    public List<RoomDTO> getAllRooms() {
        return roomDAO.getAllRooms();
    }

    // Lấy phòng theo ID
    public RoomDTO getRoomById(int roomId) {
        if (roomId <= 0) {
            System.err.println("ID phòng không hợp lệ");
            return null;
        }
        return roomDAO.getRoomById(roomId);
    }

    // Thêm phòng mới
    public boolean addRoom(RoomDTO room) {
        // Validate dữ liệu
        if (!validateRoom(room)) {
            return false;
        }

        return roomDAO.addRoom(room);
    }

    // Cập nhật phòng
    public boolean updateRoom(RoomDTO room) {
        // Validate dữ liệu
        if (room.getRoomId() <= 0) {
            System.err.println("ID phòng không hợp lệ");
            return false;
        }

        if (!validateRoom(room)) {
            return false;
        }

        return roomDAO.updateRoom(room);
    }

    // Xóa phòng
    public boolean deleteRoom(int roomId) {
        if (roomId <= 0) {
            System.err.println("ID phòng không hợp lệ");
            return false;
        }

        // Kiểm tra xem phòng có đang được sử dụng không
        RoomDTO room = roomDAO.getRoomById(roomId);
        if (room != null && room.getStatus().equals("Occupied")) {
            System.err.println("Không thể xóa phòng đang có khách");
            return false;
        }

        return roomDAO.deleteRoom(roomId);
    }

    // Tìm kiếm phòng theo số phòng
    public List<RoomDTO> searchRoomsByRoomNo(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllRooms();
        }
        return roomDAO.searchRoomsByRoomNo(keyword.trim());
    }

    // Lọc phòng theo tầng
    public List<RoomDTO> filterRoomsByFloor(byte floorNo) {
        if (floorNo <= 0) {
            return getAllRooms();
        }
        return roomDAO.filterRoomsByFloor(floorNo);
    }

    // Lọc phòng theo trạng thái
    public List<RoomDTO> filterRoomsByStatus(String status) {
        if (status == null || status.trim().isEmpty() || status.equals("Tất cả")) {
            return getAllRooms();
        }
        return roomDAO.filterRoomsByStatus(status);
    }

    // Lọc phòng theo loại phòng
    public List<RoomDTO> filterRoomsByRoomType(int roomTypeId) {
        if (roomTypeId <= 0) {
            return getAllRooms();
        }
        return roomDAO.filterRoomsByRoomType(roomTypeId);
    }

    // Validate dữ liệu phòng
    private boolean validateRoom(RoomDTO room) {
        if (room == null) {
            System.err.println("Dữ liệu phòng không được null");
            return false;
        }

        if (room.getRoomNo() == null || room.getRoomNo().trim().isEmpty()) {
            System.err.println("Số phòng không được để trống");
            return false;
        }

        if (room.getFloorNo() <= 0) {
            System.err.println("Số tầng phải lớn hơn 0");
            return false;
        }

        if (room.getRoomTypeId() <= 0) {
            System.err.println("Loại phòng không hợp lệ");
            return false;
        }

        if (room.getStatus() == null || room.getStatus().trim().isEmpty()) {
            System.err.println("Trạng thái phòng không được để trống");
            return false;
        }

        // Kiểm tra trạng thái hợp lệ
        String status = room.getStatus();
        if (!status.equals("AVAILABLE") && !status.equals("RESERVED") && !status.equals("OCCUPIED")) {
            System.err.println("Trạng thái không hợp lệ. Chỉ chấp nhận: Available, Reserved, Occupied");
            return false;
        }

        return true;
    }
}
