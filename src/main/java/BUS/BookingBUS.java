package BUS;

import DAO.BookingDAO;
import DTO.BookingDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

// Lớp xử lý logic nghiệp vụ đặt phòng
public class BookingBUS {
    private BookingDAO bookingDAO;

    public BookingBUS(BookingDAO bookingDAO) {
        this.bookingDAO = bookingDAO;
    }

    // Thêm đặt phòng mới với kiểm tra
    public boolean addBooking(BookingDTO booking) {
        // Kiểm tra dữ liệu
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

        // Tự động sinh mã đặt phòng
        if (booking.getCode() == null || booking.getCode().trim().isEmpty()) {
            booking.setCode(generateBookingCode());
        }

        // Đặt trạng thái mặc định
        if (booking.getStatus() == null || booking.getStatus().trim().isEmpty()) {
            booking.setStatus("Đã đặt");
        }

        return bookingDAO.insert(booking);
    }

    // Cập nhật đặt phòng với kiểm tra
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

    // Hủy đặt phòng
    public boolean cancelBooking(int bookingId) {
        BookingDTO booking = bookingDAO.getById(bookingId);
        if (booking == null) {
            System.err.println("Lỗi: Không tìm thấy đặt phòng");
            return false;
        }

        booking.setStatus("Đã hủy");
        return bookingDAO.update(booking);
    }

    // Xóa đặt phòng
    public boolean deleteBooking(int bookingId) {
        if (bookingId <= 0) {
            System.err.println("Lỗi: Mã đặt phòng không hợp lệ");
            return false;
        }

        return bookingDAO.delete(bookingId);
    }

    // Lấy đặt phòng theo mã
    public BookingDTO getBookingById(int bookingId) {
        if (bookingId <= 0) {
            System.err.println("Lỗi: Mã đặt phòng không hợp lệ");
            return null;
        }

        return bookingDAO.getById(bookingId);
    }

    // Lấy đặt phòng theo khách hàng
    public List<BookingDTO> getBookingsByCustomer(int customerId) {
        if (customerId <= 0) {
            System.err.println("Lỗi: Mã khách hàng không hợp lệ");
            return null;
        }

        return bookingDAO.getByCustomerId(customerId);
    }

    // Lấy đặt phòng theo trạng thái
    public List<BookingDTO> getBookingsByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            System.err.println("Lỗi: Trạng thái không được trống");
            return null;
        }

        return bookingDAO.getByStatus(status);
    }

    // Tìm đặt phòng theo mã
    public List<BookingDTO> getBookingByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            System.err.println("Lỗi: Mã đặt phòng không được trống");
            return null;
        }

        return bookingDAO.getByCode(code.trim());
    }

    // Lấy tất cả đặt phòng
    public List<BookingDTO> getAllBookings() {
        return bookingDAO.getAll();
    }

    // Sinh mã đặt phòng duy nhất
    private String generateBookingCode() {
        return "BK" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    // Kiểm tra mã đặt phòng đã tồn tại
    public boolean isBookingCodeExists(String code) {
        return bookingDAO.getByCode(code) != null;
    }
}
