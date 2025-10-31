package BUS;

import DAO.BookingDAO;
import DTO.BookingDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Business Logic Layer for Booking entity
 * Handles business logic and validation for booking operations
 */
public class BookingBUS {
    private BookingDAO bookingDAO;

    public BookingBUS(BookingDAO bookingDAO) {
        this.bookingDAO = bookingDAO;
    }

    /**
     * Add new booking with validation and auto-generate code
     */
    public boolean addBooking(BookingDTO booking) {
        // Validation
        if (booking.getCustomerId() <= 0) {
            System.err.println("Lỗi: Mã khách hàng không hợp lệ");
            return false;
        }

        if (booking.getBookingDate() == null) {
            System.err.println("Lỗi: Ngày đặt không được trống");
            return false;
        }

        if (booking.getSource() == null || booking.getSource().trim().isEmpty()) {
            System.err.println("Lỗi: Nguồn đặt không được trống");
            return false;
        }

        if (booking.getCreatedBy() <= 0) {
            System.err.println("Lỗi: Người tạo đặt không hợp lệ");
            return false;
        }

        // Auto-generate booking code if not provided
        if (booking.getCode() == null || booking.getCode().trim().isEmpty()) {
            booking.setCode(generateBookingCode());
        }

        // Set default status if not provided
        if (booking.getStatus() == null || booking.getStatus().trim().isEmpty()) {
            booking.setStatus("Đã đặt");
        }

        return bookingDAO.insert(booking);
    }

    /**
     * Update booking with validation
     */
    public boolean updateBooking(BookingDTO booking) {
        if (booking.getBookingId() <= 0) {
            System.err.println("Lỗi: Mã đặt phòng không hợp lệ");
            return false;
        }

        if (booking.getSource() == null || booking.getSource().trim().isEmpty()) {
            System.err.println("Lỗi: Nguồn đặt không được trống");
            return false;
        }

        return bookingDAO.update(booking);
    }

    /**
     * Cancel booking
     */
    public boolean cancelBooking(int bookingId) {
        BookingDTO booking = bookingDAO.getById(bookingId);
        if (booking == null) {
            System.err.println("Lỗi: Không tìm thấy đặt phòng");
            return false;
        }

        booking.setStatus("Đã hủy");
        return bookingDAO.update(booking);
    }

    /**
     * Delete booking
     */
    public boolean deleteBooking(int bookingId) {
        if (bookingId <= 0) {
            System.err.println("Lỗi: Mã đặt phòng không hợp lệ");
            return false;
        }

        return bookingDAO.delete(bookingId);
    }

    /**
     * Get booking by ID
     */
    public BookingDTO getBookingById(int bookingId) {
        if (bookingId <= 0) {
            System.err.println("Lỗi: Mã đặt phòng không hợp lệ");
            return null;
        }

        return bookingDAO.getById(bookingId);
    }

    /**
     * Get bookings by customer ID
     */
    public List<BookingDTO> getBookingsByCustomer(int customerId) {
        if (customerId <= 0) {
            System.err.println("Lỗi: Mã khách hàng không hợp lệ");
            return null;
        }

        return bookingDAO.getByCustomerId(customerId);
    }

    /**
     * Get bookings by status
     */
    public List<BookingDTO> getBookingsByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            System.err.println("Lỗi: Trạng thái không được trống");
            return null;
        }

        return bookingDAO.getByStatus(status);
    }

    /**
     * Search booking by code
     */
    public BookingDTO getBookingByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            System.err.println("Lỗi: Mã đặt phòng không được trống");
            return null;
        }

        return bookingDAO.getByCode(code.trim());
    }

    /**
     * Get all bookings
     */
    public List<BookingDTO> getAllBookings() {
        return bookingDAO.getAll();
    }

    /**
     * Generate unique booking code
     */
    private String generateBookingCode() {
        return "BK" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    /**
     * Check if booking code already exists
     */
    public boolean isBookingCodeExists(String code) {
        return bookingDAO.getByCode(code) != null;
    }
}
