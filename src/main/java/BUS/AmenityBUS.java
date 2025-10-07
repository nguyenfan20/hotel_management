package BUS;

import DAO.AmenityDAO;
import DTO.AmenityDTO;
import java.util.List;

public class AmenityBUS {
    private AmenityDAO amenityDAO;

    public AmenityBUS() {
        this.amenityDAO = new AmenityDAO();
    }

    // Lấy tất cả tiện nghi
    public List<AmenityDTO> getAllAmenities() {
        return amenityDAO.getAllAmenities();
    }

    // Lấy tiện nghi theo ID
    public AmenityDTO getAmenityById(int amenityId) {
        if (amenityId <= 0) {
            System.err.println("ID tiện nghi không hợp lệ");
            return null;
        }
        return amenityDAO.getAmenityById(amenityId);
    }

    // Thêm tiện nghi mới
    public boolean addAmenity(AmenityDTO amenity) {
        // Validate dữ liệu
        if (!validateAmenity(amenity)) {
            return false;
        }

        return amenityDAO.addAmenity(amenity);
    }

    // Cập nhật tiện nghi
    public boolean updateAmenity(AmenityDTO amenity) {
        // Validate dữ liệu
        if (amenity.getAmenityId() <= 0) {
            System.err.println("ID tiện nghi không hợp lệ");
            return false;
        }

        if (!validateAmenity(amenity)) {
            return false;
        }

        return amenityDAO.updateAmenity(amenity);
    }

    // Xóa tiện nghi
    public boolean deleteAmenity(int amenityId) {
        if (amenityId <= 0) {
            System.err.println("ID tiện nghi không hợp lệ");
            return false;
        }

        return amenityDAO.deleteAmenity(amenityId);
    }

    // Tìm kiếm tiện nghi theo tên
    public List<AmenityDTO> searchAmenitiesByName(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllAmenities();
        }
        return amenityDAO.searchAmenitiesByName(keyword.trim());
    }

    // Lọc tiện nghi theo loại phí
    public List<AmenityDTO> filterAmenitiesByChargeType(String chargeType) {
        if (chargeType == null || chargeType.trim().isEmpty() || chargeType.equals("Tất cả")) {
            return getAllAmenities();
        }
        return amenityDAO.filterAmenitiesByChargeType(chargeType);
    }

    // Validate dữ liệu tiện nghi
    private boolean validateAmenity(AmenityDTO amenity) {
        if (amenity == null) {
            System.err.println("Dữ liệu tiện nghi không được null");
            return false;
        }

        if (amenity.getName() == null || amenity.getName().trim().isEmpty()) {
            System.err.println("Tên tiện nghi không được để trống");
            return false;
        }

        if (amenity.getChargeType() == null || amenity.getChargeType().trim().isEmpty()) {
            System.err.println("Loại phí không được để trống");
            return false;
        }

        // Kiểm tra loại phí hợp lệ
        String chargeType = amenity.getChargeType();
        if (!chargeType.equals("Per Use") && !chargeType.equals("Per Day") && !chargeType.equals("Per Hour")) {
            System.err.println("Loại phí không hợp lệ. Chỉ chấp nhận: Per Use, Per Day, Per Hour");
            return false;
        }

        if (amenity.getPrice() == null || amenity.getPrice().doubleValue() < 0) {
            System.err.println("Giá không được âm");
            return false;
        }

        return true;
    }
}
