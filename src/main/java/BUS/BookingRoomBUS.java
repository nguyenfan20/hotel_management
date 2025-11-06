package BUS;

import DAO.BookingRoomDAO;
import DTO.BookingRoomDTO;
import DTO.RoomDTO;
import DTO.RoomTypeDTO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// Lớp xử lý logic nghiệp vụ phòng đặt
public class BookingRoomBUS {
    private BookingRoomDAO bookingRoomDAO;
    private RoomTypeBUS roomTypeBUS;

    public BookingRoomBUS(BookingRoomDAO bookingRoomDAO) {
        this.bookingRoomDAO = bookingRoomDAO;
        this.roomTypeBUS = new RoomTypeBUS();
    }

    // Thêm phòng đặt mới với kiểm tra
    public boolean addBookingRoom(BookingRoomDTO bookingRoom) {
        // Kiểm tra dữ liệu
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

        return bookingRoomDAO.insert(bookingRoom);
    }

    public boolean addBookingRoomFromRoom(int bookingId, RoomDTO room) {
        if (bookingId <= 0) {
            System.err.println("Lỗi: Mã đặt phòng không hợp lệ");
            return false;
        }

        if (room == null || room.getRoomId() <= 0) {
            System.err.println("Lỗi: Mã phòng không hợp lệ");
            return false;
        }

        BigDecimal price = BigDecimal.ZERO;
        try {
            RoomTypeDTO roomType = roomTypeBUS.getRoomTypeById(room.getRoomTypeId());
            if (roomType != null && roomType.getBasePrice() != null) {
                price = roomType.getBasePrice();
            }
        } catch (Exception e) {
            System.err.println("Lỗi lấy giá từ loại phòng: " + e.getMessage());
        }

        // Auto-fill thông tin từ phòng
        LocalDateTime checkIn = LocalDateTime.now();
        LocalDateTime checkOut = checkIn.plusDays(1);

        BookingRoomDTO bookingRoom = new BookingRoomDTO(
                0,
                bookingId,
                room.getRoomId(),
                checkIn,
                checkOut,
                null,
                null,
                1,  // 1 người lớn mặc định
                0,  // 0 trẻ em mặc định
                price,  // Giá từ loại phòng
                BigDecimal.ZERO,  // Không giảm giá mặc định
                BigDecimal.ZERO,  // Không thuế mặc định
                "Đã đặt"
        );

        return bookingRoomDAO.insert(bookingRoom);
    }

    // Cập nhật phòng đặt với kiểm tra
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

    // Cập nhật nhận phòng
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

    // Cập nhật trả phòng
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

    // Xóa phòng đặt
    public boolean deleteBookingRoom(int bookingRoomId) {
        if (bookingRoomId <= 0) {
            System.err.println("Lỗi: Mã phòng đặt không hợp lệ");
            return false;
        }

        return bookingRoomDAO.delete(bookingRoomId);
    }

    // Lấy phòng đặt theo mã
    public BookingRoomDTO getBookingRoomById(int bookingRoomId) {
        if (bookingRoomId <= 0) {
            System.err.println("Lỗi: Mã phòng đặt không hợp lệ");
            return null;
        }

        return bookingRoomDAO.getById(bookingRoomId);
    }

    // Lấy phòng đặt theo đặt phòng
    public List<BookingRoomDTO> getBookingRoomsByBooking(int bookingId) {
        if (bookingId <= 0) {
            System.err.println("Lỗi: Mã đặt phòng không hợp lệ");
            return null;
        }

        return bookingRoomDAO.getByBookingId(bookingId);
    }

    // Lấy phòng đặt theo trạng thái
    public List<BookingRoomDTO> getBookingRoomsByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            System.err.println("Lỗi: Trạng thái không được trống");
            return null;
        }

        return bookingRoomDAO.getByStatus(status);
    }

    // Lấy tất cả phòng đặt
    public List<BookingRoomDTO> getAllBookingRooms() {
        return bookingRoomDAO.getAll();
    }

    // Tính tổng giá phòng
    public BigDecimal calculateTotalPrice(BookingRoomDTO bookingRoom) {
        long nights = java.time.temporal.ChronoUnit.DAYS.between(
                bookingRoom.getCheckInPlan(),
                bookingRoom.getCheckOutPlan()
        );

        BigDecimal total = bookingRoom.getRatePerNight().multiply(BigDecimal.valueOf(nights));

        // Áp dụng giảm giá nếu có
        if (bookingRoom.getDiscountAmount() != null && bookingRoom.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            total = total.subtract(bookingRoom.getDiscountAmount());
        }

        // Áp dụng thuế
        if (bookingRoom.getTaxRate() != null && bookingRoom.getTaxRate().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal taxAmount = total.multiply(bookingRoom.getTaxRate().divide(BigDecimal.valueOf(100)));
            total = total.add(taxAmount);
        }

        return total;
    }
}
