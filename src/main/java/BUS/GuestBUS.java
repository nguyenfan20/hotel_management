package BUS;

import DAO.GuestDAO;
import DTO.GuestDTO;
import java.util.List;

// Lớp xử lý logic nghiệp vụ khách hàng
public class GuestBUS {
    private GuestDAO guestDAO;

    public GuestBUS(GuestDAO guestDAO) {
        this.guestDAO = guestDAO;
    }

    // Thêm khách hàng mới với kiểm tra
    public boolean addGuest(GuestDTO guest) {
        // Kiểm tra dữ liệu
        if (guest.getFullName() == null || guest.getFullName().trim().isEmpty()) {
            System.err.println("Lỗi: Tên khách hàng không được trống");
            return false;
        }

        if (guest.getIdCard() == null || guest.getIdCard().trim().isEmpty()) {
            System.err.println("Lỗi: Số chứng minh không được trống");
            return false;
        }

        if (guest.getDob() == null) {
            System.err.println("Lỗi: Ngày sinh không được trống");
            return false;
        }

        if (guest.getBookingRoomId() <= 0) {
            System.err.println("Lỗi: Mã phòng đặt không hợp lệ");
            return false;
        }

        return guestDAO.insert(guest);
    }

    // Cập nhật khách hàng với kiểm tra
    public boolean updateGuest(GuestDTO guest) {
        if (guest.getGuestId() <= 0) {
            System.err.println("Lỗi: Mã khách hàng không hợp lệ");
            return false;
        }

        if (guest.getFullName() == null || guest.getFullName().trim().isEmpty()) {
            System.err.println("Lỗi: Tên khách hàng không được trống");
            return false;
        }

        return guestDAO.update(guest);
    }

    // Xóa khách hàng
    public boolean deleteGuest(int guestId) {
        if (guestId <= 0) {
            System.err.println("Lỗi: Mã khách hàng không hợp lệ");
            return false;
        }

        return guestDAO.delete(guestId);
    }

    // Lấy khách hàng theo mã
    public GuestDTO getGuestById(int guestId) {
        if (guestId <= 0) {
            System.err.println("Lỗi: Mã khách hàng không hợp lệ");
            return null;
        }

        return guestDAO.getById(guestId);
    }

    // Lấy khách hàng theo phòng đặt
    public List<GuestDTO> getGuestsByBookingRoom(int bookingRoomId) {
        if (bookingRoomId <= 0) {
            System.err.println("Lỗi: Mã phòng đặt không hợp lệ");
            return null;
        }

        return guestDAO.getByBookingRoomId(bookingRoomId);
    }

    // Tìm khách hàng theo tên hoặc số CMND
    public List<GuestDTO> searchGuests(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            System.err.println("Lỗi: Từ khóa tìm kiếm không được trống");
            return null;
        }

        return guestDAO.search(keyword.trim());
    }

    // Lấy tất cả khách hàng
    public List<GuestDTO> getAllGuests() {
        return guestDAO.getAll();
    }
}
