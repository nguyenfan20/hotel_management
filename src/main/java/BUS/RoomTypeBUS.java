package BUS;

import DAO.RoomTypeDAO;
import DTO.RoomTypeDTO;
import java.util.List;

public class RoomTypeBUS {
    private RoomTypeDAO roomTypeDAO;

    public RoomTypeBUS() {
        this.roomTypeDAO = new RoomTypeDAO();
    }

    // Lấy tất cả loại phòng
    public List<RoomTypeDTO> getAllRoomTypes() {
        return roomTypeDAO.getAllRoomTypes();
    }

    // Lấy loại phòng theo ID
    public RoomTypeDTO getRoomTypeById(int roomTypeId) {
        if (roomTypeId <= 0) {
            System.err.println("ID loại phòng không hợp lệ");
            return null;
        }
        return roomTypeDAO.getRoomTypeById(roomTypeId);
    }

    // Thêm loại phòng mới
    public boolean addRoomType(RoomTypeDTO roomType) {
        // Validate dữ liệu
        if (!validateRoomType(roomType)) {
            return false;
        }

        return roomTypeDAO.addRoomType(roomType);
    }

    // Cập nhật loại phòng
    public boolean updateRoomType(RoomTypeDTO roomType) {
        // Validate dữ liệu
        if (roomType.getRoomTypeId() <= 0) {
            System.err.println("ID loại phòng không hợp lệ");
            return false;
        }

        if (!validateRoomType(roomType)) {
            return false;
        }

        return roomTypeDAO.updateRoomType(roomType);
    }

    // Xóa loại phòng
    public boolean deleteRoomType(int roomTypeId) {
        if (roomTypeId <= 0) {
            System.err.println("ID loại phòng không hợp lệ");
            return false;
        }

        // Kiểm tra xem có phòng nào đang sử dụng loại phòng này không
        // TODO: Thêm logic kiểm tra ràng buộc

        return roomTypeDAO.deleteRoomType(roomTypeId);
    }

    // Tìm kiếm loại phòng theo tên
    public List<RoomTypeDTO> searchRoomTypesByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllRoomTypes();
        }
        return roomTypeDAO.searchRoomTypesByName(keyword.trim());
    }

    // Validate dữ liệu loại phòng
    private boolean validateRoomType(RoomTypeDTO roomType) {
        if (roomType == null) {
            System.err.println("Dữ liệu loại phòng không được null");
            return false;
        }

        if (roomType.getName() == null || roomType.getName().trim().isEmpty()) {
            System.err.println("Tên loại phòng không được để trống");
            return false;
        }

        if (roomType.getBasePrice() == null || roomType.getBasePrice().doubleValue() <= 0) {
            System.err.println("Giá cơ bản phải lớn hơn 0");
            return false;
        }

        if (roomType.getCapacityAdults() <= 0) {
            System.err.println("Số lượng người lớn phải lớn hơn 0");
            return false;
        }

        if (roomType.getBedCount() <= 0) {
            System.err.println("Số lượng giường phải lớn hơn 0");
            return false;
        }

        if (roomType.getArea() == null || roomType.getArea().doubleValue() <= 0) {
            System.err.println("Diện tích phải lớn hơn 0");
            return false;
        }

        return true;
    }
}
