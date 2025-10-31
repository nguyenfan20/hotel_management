package BUS;

import DAO.BookingRoomDAO;
import DTO.BookingRoomDTO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Business Logic Layer for BookingRoom entity
 * Handles business logic and validation for booking room operations
 */
public class BookingRoomBUS {
    private BookingRoomDAO bookingRoomDAO;

    public BookingRoomBUS(BookingRoomDAO bookingRoomDAO) {
        this.bookingRoomDAO = bookingRoomDAO;
    }

    /**
     * Add new booking room with validation
     */
    public boolean addBookingRoom(BookingRoomDTO bookingRoom) {
        // Validation
        if (bookingRoom.getBookingId() <= 0) {
            System.err.println("Lỗi: Mã đặt phòng không hợp lệ");
            return false;
        }

        if (bookingRoom.getRoomId() <= 0) {
            System.err.println("Lỗi: Mã phòng không hợp lệ");
            return false;
        }

        if (bookingRoom.getCheckInPlan() == null || bookingRoom.getCheckOutPlan() == null) {
            System.err.println("Lỗi: Ngày nhận và trả phòng không được trống");
            return false;
        }

        if (bookingRoom.getCheckInPlan().isAfter(bookingRoom.getCheckOutPlan())) {
            System.err.println("Lỗi: Ngày nhận phòng phải trước ngày trả phòng");
            return false;
        }

        if (bookingRoom.getAdults() < 1) {
            System.err.println("Lỗi: Phải có ít nhất 1 người lớn");
            return false;
        }

        if (bookingRoom.getRatePerNight() == null || bookingRoom.getRatePerNight().compareTo(BigDecimal.ZERO) <= 0) {
            System.err.println("Lỗi: Giá phòng phải lớn hơn 0");
            return false;
        }

        return bookingRoomDAO.insert(bookingRoom);
    }

    /**
     * Update booking room with validation
     */
    public boolean updateBookingRoom(BookingRoomDTO bookingRoom) {
        if (bookingRoom.getBookingRoomId() <= 0) {
            System.err.println("Lỗi: Mã phòng đặt không hợp lệ");
            return false;
        }

        if (bookingRoom.getCheckInPlan().isAfter(bookingRoom.getCheckOutPlan())) {
            System.err.println("Lỗi: Ngày nhận phòng phải trước ngày trả phòng");
            return false;
        }

        return bookingRoomDAO.update(bookingRoom);
    }

    /**
     * Update check-in information
     */
    public boolean checkIn(int bookingRoomId, LocalDateTime checkInTime) {
        BookingRoomDTO bookingRoom = bookingRoomDAO.getById(bookingRoomId);
        if (bookingRoom == null) {
            System.err.println("Lỗi: Không tìm thấy phòng đặt");
            return false;
        }

        bookingRoom.setCheckInActual(checkInTime);
        bookingRoom.setStatus("Đã nhận");

        return bookingRoomDAO.update(bookingRoom);
    }

    /**
     * Update check-out information
     */
    public boolean checkOut(int bookingRoomId, LocalDateTime checkOutTime) {
        BookingRoomDTO bookingRoom = bookingRoomDAO.getById(bookingRoomId);
        if (bookingRoom == null) {
            System.err.println("Lỗi: Không tìm thấy phòng đặt");
            return false;
        }

        bookingRoom.setCheckOutActual(checkOutTime);
        bookingRoom.setStatus("Đã trả");

        return bookingRoomDAO.update(bookingRoom);
    }

    /**
     * Delete booking room
     */
    public boolean deleteBookingRoom(int bookingRoomId) {
        if (bookingRoomId <= 0) {
            System.err.println("Lỗi: Mã phòng đặt không hợp lệ");
            return false;
        }

        return bookingRoomDAO.delete(bookingRoomId);
    }

    /**
     * Get booking room by ID
     */
    public BookingRoomDTO getBookingRoomById(int bookingRoomId) {
        if (bookingRoomId <= 0) {
            System.err.println("Lỗi: Mã phòng đặt không hợp lệ");
            return null;
        }

        return bookingRoomDAO.getById(bookingRoomId);
    }

    /**
     * Get all booking rooms for a booking
     */
    public List<BookingRoomDTO> getBookingRoomsByBooking(int bookingId) {
        if (bookingId <= 0) {
            System.err.println("Lỗi: Mã đặt phòng không hợp lệ");
            return null;
        }

        return bookingRoomDAO.getByBookingId(bookingId);
    }

    /**
     * Get booking rooms by status
     */
    public List<BookingRoomDTO> getBookingRoomsByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            System.err.println("Lỗi: Trạng thái không được trống");
            return null;
        }

        return bookingRoomDAO.getByStatus(status);
    }

    /**
     * Get all booking rooms
     */
    public List<BookingRoomDTO> getAllBookingRooms() {
        return bookingRoomDAO.getAll();
    }

    /**
     * Calculate total price for a booking room
     */
    public BigDecimal calculateTotalPrice(BookingRoomDTO bookingRoom) {
        long nights = java.time.temporal.ChronoUnit.DAYS.between(
                bookingRoom.getCheckInPlan(),
                bookingRoom.getCheckOutPlan()
        );

        BigDecimal total = bookingRoom.getRatePerNight().multiply(BigDecimal.valueOf(nights));

        // Apply discount if exists
        if (bookingRoom.getDiscountAmount() != null && bookingRoom.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            total = total.subtract(bookingRoom.getDiscountAmount());
        }

        // Apply tax
        if (bookingRoom.getTaxRate() != null && bookingRoom.getTaxRate().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal taxAmount = total.multiply(bookingRoom.getTaxRate().divide(BigDecimal.valueOf(100)));
            total = total.add(taxAmount);
        }

        return total;
    }
}
