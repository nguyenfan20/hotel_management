package BUS;

import DAO.BookingDAO;
import DAO.BookingRoomDAO;
import DTO.BookingDTO;
import DTO.BookingRoomDTO;
import DTO.RoomDTO;
import DTO.RoomTypeDTO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class BookingRoomBUS {
    private BookingRoomDAO bookingRoomDAO;
    private RoomTypeBUS roomTypeBUS;
    private RoomBUS roomBUS;

    public BookingRoomBUS(BookingRoomDAO bookingRoomDAO) {
        this.bookingRoomDAO = bookingRoomDAO;
        this.roomTypeBUS = new RoomTypeBUS();
        this.roomBUS = new RoomBUS();
    }

    public BookingRoomBUS() {
        this.bookingRoomDAO = new BookingRoomDAO();
        this.roomTypeBUS = new RoomTypeBUS();
        this.roomBUS = new RoomBUS();
    }

    /**
     * Kiểm tra xem phòng có đang được đặt trong khoảng thời gian không
     */
    public boolean isRoomOccupied(int roomId, LocalDateTime checkInPlan, LocalDateTime checkOutPlan, int excludeBookingRoomId) {
        List<BookingRoomDTO> allBookingRooms = bookingRoomDAO.getAll();

        for (BookingRoomDTO br : allBookingRooms) {
            if (excludeBookingRoomId > 0 && br.getBookingRoomId() == excludeBookingRoomId) {
                continue;
            }

            if (br.getRoomId() == roomId &&
                    !br.getStatus().equals("CHECKED_OUT") &&
                    !br.getStatus().equals("CANCELLED")) {

                boolean isOverlap = checkInPlan.isBefore(br.getCheckOutPlan()) &&
                        checkOutPlan.isAfter(br.getCheckInPlan());

                if (isOverlap) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Kiểm tra phòng có đang được đặt không
     */
    public boolean isRoomCurrentlyBooked(int roomId) {
        List<BookingRoomDTO> allBookingRooms = bookingRoomDAO.getAll();

        return allBookingRooms.stream()
                .anyMatch(br -> br.getRoomId() == roomId &&
                        !br.getStatus().equals("CHECKED_OUT") &&
                        !br.getStatus().equals("CANCELLED"));
    }

    /**
     * Thêm phòng đặt mới với kiểm tra
     */
    public boolean addBookingRoom(BookingRoomDTO bookingRoom) {
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

        if (isRoomOccupied(bookingRoom.getRoomId(),
                bookingRoom.getCheckInPlan(),
                bookingRoom.getCheckOutPlan(),
                -1)) {
            System.err.println("Lỗi: Phòng này đã được đặt trong khoảng thời gian này");
            return false;
        }

        // Cập nhật trạng thái phòng sang RESERVED
        RoomDTO room = roomBUS.getRoomById(bookingRoom.getRoomId());
        if (room != null) {
            room.setStatus("RESERVED");
            roomBUS.updateRoom(room);
        }

        return bookingRoomDAO.insert(bookingRoom);
    }

    /**
     * Cập nhật phòng đặt
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

        if (isRoomOccupied(bookingRoom.getRoomId(),
                bookingRoom.getCheckInPlan(),
                bookingRoom.getCheckOutPlan(),
                bookingRoom.getBookingRoomId())) {
            System.err.println("Lỗi: Phòng này đã được đặt trong khoảng thời gian này");
            return false;
        }

        return bookingRoomDAO.update(bookingRoom);
    }

    /**
     * Check-in phòng
     */
    public boolean checkIn(int bookingRoomId, LocalDateTime checkInTime) {
        BookingRoomDTO bookingRoom = bookingRoomDAO.getById(bookingRoomId);
        if (bookingRoom == null) {
            System.err.println("Lỗi: Không tìm thấy phòng đặt");
            return false;
        }

        if (!"BOOKED".equals(bookingRoom.getStatus())) {
            System.err.println("Lỗi: Phòng không ở trạng thái BOOKED");
            return false;
        }

        bookingRoom.setCheckInActual(checkInTime);
        bookingRoom.setStatus("CHECKED_IN");

        RoomDTO room = roomBUS.getRoomById(bookingRoom.getRoomId());
        if (room != null) {
            room.setStatus("OCCUPIED");
            roomBUS.updateRoom(room);
        }

        updateBookingStatus(bookingRoom.getBookingId());

        return bookingRoomDAO.update(bookingRoom);
    }

    /**
     * Check-out phòng
     */
    public boolean checkOut(int bookingRoomId, LocalDateTime checkOutTime) {
        BookingRoomDTO bookingRoom = bookingRoomDAO.getById(bookingRoomId);
        if (bookingRoom == null) {
            System.err.println("Lỗi: Không tìm thấy phòng đặt");
            return false;
        }

        if (bookingRoom.getCheckInActual() == null) {
            System.err.println("Lỗi: Phòng chưa được check-in");
            return false;
        }

        bookingRoom.setCheckOutActual(checkOutTime);
        bookingRoom.setStatus("CHECKED_OUT");

        RoomDTO room = roomBUS.getRoomById(bookingRoom.getRoomId());
        if (room != null) {
            room.setStatus("AVAILABLE");
            roomBUS.updateRoom(room);
        }

        boolean success = bookingRoomDAO.update(bookingRoom);

        // Cập nhật trạng thái Booking
        updateBookingStatus(bookingRoom.getBookingId());

        return success;
    }

    // Hàm phụ trợ cập nhật trạng thái Booking
    private void updateBookingStatus(int bookingId) {
        BookingBUS bookingBUS = new BookingBUS(new BookingDAO());
        BookingDTO booking = bookingBUS.getBookingById(bookingId);
        if (booking == null) return;

        List<BookingRoomDTO> rooms = getBookingRoomsByBooking(bookingId);

        boolean allCheckedIn = rooms.stream().allMatch(r -> "CHECKED_IN".equals(r.getStatus()) || "CHECKED_OUT".equals(r.getStatus()));
        boolean anyCheckedIn = rooms.stream().anyMatch(r -> "CHECKED_IN".equals(r.getStatus()));
        boolean allCheckedOut = rooms.stream().allMatch(r -> "CHECKED_OUT".equals(r.getStatus()) || "CANCELLED".equals(r.getStatus()));

        if (allCheckedOut) {
            booking.setStatus("CHECKED_OUT");
        } else if (allCheckedIn) {
            booking.setStatus("CHECKED_IN");
        } else if (anyCheckedIn) {
            booking.setStatus("PARTIAL_CHECKED_IN");
        } else {
            booking.setStatus("BOOKED");
        }

        bookingBUS.updateBooking(booking);
    }

    /**
     * Check-out tất cả phòng trong booking
     */
    public boolean checkOutAllRooms(int bookingId, LocalDateTime checkOutTime) {
        List<BookingRoomDTO> bookingRooms = bookingRoomDAO.getByBookingId(bookingId);

        boolean allSuccess = true;
        for (BookingRoomDTO br : bookingRooms) {
            if ("CHECKED_IN".equals(br.getStatus())) {
                if (!checkOut(br.getBookingRoomId(), checkOutTime)) {
                    allSuccess = false;
                }
            }
        }

        return allSuccess;
    }

    /**
     * Kiểm tra xem tất cả phòng trong booking đã check-out chưa
     */
    public boolean areAllRoomsCheckedOut(int bookingId) {
        List<BookingRoomDTO> bookingRooms = bookingRoomDAO.getByBookingId(bookingId);

        return bookingRooms.stream()
                .allMatch(br -> "CHECKED_OUT".equals(br.getStatus()) || "CANCELLED".equals(br.getStatus()));
    }

    /**
     * Đếm số phòng đã check-out trong booking
     */
    public int countCheckedOutRooms(int bookingId) {
        List<BookingRoomDTO> bookingRooms = bookingRoomDAO.getByBookingId(bookingId);

        return (int) bookingRooms.stream()
                .filter(br -> "CHECKED_OUT".equals(br.getStatus()))
                .count();
    }

    /**
     * Xóa phòng đặt
     */
    public boolean deleteBookingRoom(int bookingRoomId) {
        if (bookingRoomId <= 0) {
            System.err.println("Lỗi: Mã phòng đặt không hợp lệ");
            return false;
        }

        // Lấy thông tin phòng trước khi xóa
        BookingRoomDTO bookingRoom = bookingRoomDAO.getById(bookingRoomId);
        if (bookingRoom != null) {
            // Cập nhật trạng thái phòng về AVAILABLE nếu đang RESERVED
            RoomDTO room = roomBUS.getRoomById(bookingRoom.getRoomId());
            if (room != null && "RESERVED".equals(room.getStatus())) {
                room.setStatus("AVAILABLE");
                roomBUS.updateRoom(room);
            }
        }

        return bookingRoomDAO.delete(bookingRoomId);
    }

    /**
     * Lấy phòng đặt theo mã
     */
    public BookingRoomDTO getBookingRoomById(int bookingRoomId) {
        if (bookingRoomId <= 0) {
            System.err.println("Lỗi: Mã phòng đặt không hợp lệ");
            return null;
        }

        return bookingRoomDAO.getById(bookingRoomId);
    }

    /**
     * Lấy phòng đặt theo booking
     */
    public List<BookingRoomDTO> getBookingRoomsByBooking(int bookingId) {
        if (bookingId <= 0) {
            System.err.println("Lỗi: Mã đặt phòng không hợp lệ");
            return null;
        }

        return bookingRoomDAO.getByBookingId(bookingId);
    }

    /**
     * Lấy phòng đặt theo trạng thái
     */
    public List<BookingRoomDTO> getBookingRoomsByStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            System.err.println("Lỗi: Trạng thái không được trống");
            return null;
        }

        return bookingRoomDAO.getByStatus(status);
    }

    /**
     * Lấy tất cả phòng đặt
     */
    public List<BookingRoomDTO> getAllBookingRooms() {
        return bookingRoomDAO.getAll();
    }

    /**
     * Tính tổng giá phòng
     */
    public BigDecimal calculateTotalPrice(BookingRoomDTO bookingRoom) {
        LocalDateTime checkIn = bookingRoom.getCheckInActual() != null ?
                bookingRoom.getCheckInActual() : bookingRoom.getCheckInPlan();
        LocalDateTime checkOut = bookingRoom.getCheckOutActual() != null ?
                bookingRoom.getCheckOutActual() : bookingRoom.getCheckOutPlan();

        long nights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights < 1) nights = 1;

        BigDecimal total = bookingRoom.getRatePerNight().multiply(BigDecimal.valueOf(nights));

        if (bookingRoom.getDiscountAmount() != null && bookingRoom.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            total = total.subtract(bookingRoom.getDiscountAmount());
        }

//        if (bookingRoom.getTaxRate() != null && bookingRoom.getTaxRate().compareTo(BigDecimal.ZERO) > 0) {
//            BigDecimal taxAmount = total.multiply(bookingRoom.getTaxRate().divide(BigDecimal.valueOf(100)));
//            total = total.add(taxAmount);
//        }

        return total;
    }
}