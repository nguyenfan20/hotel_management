package BUS;

import DAO.RoomTypeDAO;
import DAO.RoomTypeAmenityDAO;
import DTO.RoomTypeDTO;
import java.util.List;
import java.util.stream.Collectors;

public class RoomTypeBUS {
    private RoomTypeDAO roomTypeDAO;
    private RoomTypeAmenityDAO roomTypeAmenityDAO;

    public RoomTypeBUS() {
        this.roomTypeDAO = new RoomTypeDAO();
        this.roomTypeAmenityDAO = new RoomTypeAmenityDAO();
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
    public boolean addRoomType(RoomTypeDTO roomType, List<Integer> amenityIds) {
        // Validate dữ liệu
        if (!validateRoomType(roomType)) {
            return false;
        }

        // Thêm room type
        boolean success = roomTypeDAO.addRoomType(roomType);

        // Nếu thêm thành công và có amenities, thêm amenities
        if (success && amenityIds != null && !amenityIds.isEmpty()) {
            // Lấy ID của room type vừa thêm (cần update DAO để trả về ID)
            List<RoomTypeDTO> allRoomTypes = roomTypeDAO.getAllRoomTypes();
            if (!allRoomTypes.isEmpty()) {
                RoomTypeDTO lastRoomType = allRoomTypes.get(allRoomTypes.size() - 1);
                roomTypeAmenityDAO.updateAmenitiesForRoomType(lastRoomType.getRoomTypeId(), amenityIds);
            }
        }

        return success;
    }

    // Cập nhật loại phòng
    public boolean updateRoomType(RoomTypeDTO roomType, List<Integer> amenityIds) {
        // Validate dữ liệu
        if (roomType.getRoomTypeId() <= 0) {
            System.err.println("ID loại phòng không hợp lệ");
            return false;
        }

        if (!validateRoomType(roomType)) {
            return false;
        }

        // Cập nhật room type
        boolean success = roomTypeDAO.updateRoomType(roomType);

        // Cập nhật amenities
        if (success) {
            roomTypeAmenityDAO.updateAmenitiesForRoomType(roomType.getRoomTypeId(), amenityIds);
        }

        return success;
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
