package BUS;

import DAO.GuestDAO;
import DTO.GuestDTO;
import java.util.List;

/**
 * Business Logic Layer for Guest entity
 * Handles business logic and validation for guest operations
 */
public class GuestBUS {
    private GuestDAO guestDAO;

    public GuestBUS(GuestDAO guestDAO) {
        this.guestDAO = guestDAO;
    }

    /**
     * Add new guest with validation
     */
    public boolean addGuest(GuestDTO guest) {
        // Validation
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

    /**
     * Update guest information with validation
     */
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

    /**
     * Delete guest
     */
    public boolean deleteGuest(int guestId) {
        if (guestId <= 0) {
            System.err.println("Lỗi: Mã khách hàng không hợp lệ");
            return false;
        }

        return guestDAO.delete(guestId);
    }

    /**
     * Get guest by ID
     */
    public GuestDTO getGuestById(int guestId) {
        if (guestId <= 0) {
            System.err.println("Lỗi: Mã khách hàng không hợp lệ");
            return null;
        }

        return guestDAO.getById(guestId);
    }

    /**
     * Get all guests for a booking room
     */
    public List<GuestDTO> getGuestsByBookingRoom(int bookingRoomId) {
        if (bookingRoomId <= 0) {
            System.err.println("Lỗi: Mã phòng đặt không hợp lệ");
            return null;
        }

        return guestDAO.getByBookingRoomId(bookingRoomId);
    }

    /**
     * Search guests by name or ID card
     */
    public List<GuestDTO> searchGuests(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            System.err.println("Lỗi: Từ khóa tìm kiếm không được trống");
            return null;
        }

        return guestDAO.search(keyword.trim());
    }

    /**
     * Get all guests
     */
    public List<GuestDTO> getAllGuests() {
        return guestDAO.getAll();
    }
}
