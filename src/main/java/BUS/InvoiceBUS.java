package BUS;

import DAO.InvoiceDAO;
import DTO.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class InvoiceBUS {
    private InvoiceDAO invoiceDAO;
    private BookingRoomBUS bookingRoomBUS;
    private ServiceOrderBUS serviceOrderBUS;
    private DiscountBUS discountBUS;

    public InvoiceBUS() {
        this.invoiceDAO = new InvoiceDAO();
        this.bookingRoomBUS = new BookingRoomBUS();
        this.serviceOrderBUS = new ServiceOrderBUS();
        this.discountBUS = new DiscountBUS();
    }

    /**
     * Tạo hóa đơn cho một phòng cụ thể
     */
    public InvoiceDTO createInvoiceForRoom(int bookingRoomId, int createdBy) {
        BookingRoomDTO bookingRoom = bookingRoomBUS.getBookingRoomById(bookingRoomId);
        if (bookingRoom == null) {
            System.err.println("Lỗi: Không tìm thấy phòng đặt");
            return null;
        }

        if (!"CHECKED_OUT".equals(bookingRoom.getStatus())) {
            System.err.println("Lỗi: Phòng chưa checkout");
            return null;
        }

        // Tính tiền phòng
        BigDecimal roomTotal = bookingRoomBUS.calculateTotalPrice(bookingRoom);

        // Tính tiền dịch vụ
        BigDecimal serviceTotal = BigDecimal.ZERO;
        List<ServiceOrderDTO> services = serviceOrderBUS.getAll().stream()
                .filter(so -> so.getBookingRoomId() == bookingRoomId)
                .toList();

        for (ServiceOrderDTO service : services) {
            // Ép kiểu unitPrice (double) → BigDecimal
            BigDecimal unitPrice = new BigDecimal(service.getUnitPrice());
            BigDecimal quantity = BigDecimal.valueOf(service.getQuantity());
            BigDecimal serviceAmount = unitPrice.multiply(quantity);
            serviceTotal = serviceTotal.add(serviceAmount);
        }

        BigDecimal subtotal = roomTotal.add(serviceTotal);

        // Ép kiểu taxRate (double) → BigDecimal
        BigDecimal taxRate = bookingRoom.getTaxRate() != null
                ? new BigDecimal(String.valueOf(bookingRoom.getTaxRate()))
                : BigDecimal.ZERO;

        BigDecimal taxTotal = subtotal.multiply(taxRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal grandTotal = subtotal.add(taxTotal);

        // Tạo hóa đơn
        InvoiceDTO invoice = new InvoiceDTO();
        invoice.setBookingId(bookingRoom.getBookingId());
        invoice.setInvoiceNo(generateInvoiceNo());
        invoice.setSubtotal(subtotal.doubleValue());
        invoice.setDiscountTotal(0.0);
        invoice.setTaxTotal(taxTotal.doubleValue());
        invoice.setGrandTotal(grandTotal.doubleValue());
        invoice.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        invoice.setCreatedBy(createdBy);
        invoice.setStatus("UNPAID");

        if (invoiceDAO.addInvoice(invoice)) {
            List<InvoiceDTO> invoices = invoiceDAO.getInvoicesByBooking(bookingRoom.getBookingId());
            return invoices.get(invoices.size() - 1);
        }

        return null;
    }

    public InvoiceDTO createInvoiceOnCheckout(int bookingRoomId, int createdBy) {
        BookingRoomDTO br = bookingRoomBUS.getBookingRoomById(bookingRoomId);
        if (br == null || !"CHECKED_OUT".equals(br.getStatus())) {
            System.err.println("Lỗi: Phòng chưa check-out");
            return null;
        }

        // Kiểm tra xem đã có hóa đơn cho phòng này chưa
        List<InvoiceDTO> existing = invoiceDAO.getInvoicesByBookingRoom(bookingRoomId);
        if (!existing.isEmpty()) {
            return existing.get(0); // Tránh tạo trùng
        }

        return createInvoiceForRoom(bookingRoomId, createdBy);
    }

    /**
     * Tạo hóa đơn cho tất cả phòng trong booking
     */
    public InvoiceDTO createInvoiceForAllRooms(int bookingId, int createdBy) {
        List<BookingRoomDTO> bookingRooms = bookingRoomBUS.getBookingRoomsByBooking(bookingId);

        if (bookingRooms == null || bookingRooms.isEmpty()) {
            System.err.println("Lỗi: Không tìm thấy phòng nào trong booking");
            return null;
        }

        boolean allCheckedOut = bookingRooms.stream()
                .allMatch(br -> "CHECKED_OUT".equals(br.getStatus()));

        if (!allCheckedOut) {
            System.err.println("Lỗi: Chưa checkout tất cả phòng");
            return null;
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal taxTotal = BigDecimal.ZERO;

        for (BookingRoomDTO br : bookingRooms) {
            BigDecimal roomTotal = bookingRoomBUS.calculateTotalPrice(br);
            subtotal = subtotal.add(roomTotal);

            // Tiền dịch vụ
            List<ServiceOrderDTO> services = serviceOrderBUS.getAll().stream()
                    .filter(so -> so.getBookingRoomId() == br.getBookingRoomId())
                    .toList();

            for (ServiceOrderDTO service : services) {
                BigDecimal unitPrice = new BigDecimal(service.getUnitPrice());
                BigDecimal quantity = BigDecimal.valueOf(service.getQuantity());
                BigDecimal serviceAmount = unitPrice.multiply(quantity);
                subtotal = subtotal.add(serviceAmount);
            }

            // Thuế
            BigDecimal taxRate = br.getTaxRate() != null
                    ? new BigDecimal(String.valueOf(br.getTaxRate()))
                    : BigDecimal.ZERO;
            BigDecimal roomTax = roomTotal.multiply(taxRate)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            taxTotal = taxTotal.add(roomTax);
        }

        BigDecimal grandTotal = subtotal.add(taxTotal);

        InvoiceDTO invoice = new InvoiceDTO();
        invoice.setBookingId(bookingId);
        invoice.setInvoiceNo(generateInvoiceNo());
        invoice.setSubtotal(subtotal.doubleValue());
        invoice.setDiscountTotal(0.0);
        invoice.setTaxTotal(taxTotal.doubleValue());
        invoice.setGrandTotal(grandTotal.doubleValue());
        invoice.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        invoice.setCreatedBy(createdBy);
        invoice.setStatus("UNPAID");

        if (invoiceDAO.addInvoice(invoice)) {
            List<InvoiceDTO> invoices = invoiceDAO.getInvoicesByBooking(bookingId);
            return invoices.get(invoices.size() - 1);
        }

        return null;
    }

    public InvoiceDTO createInvoiceOnFullCheckout(int bookingId, int createdBy) {
        List<BookingRoomDTO> rooms = bookingRoomBUS.getBookingRoomsByBooking(bookingId);
        if (rooms.stream().anyMatch(r -> !"CHECKED_OUT".equals(r.getStatus()))) {
            System.err.println("Lỗi: Chưa check-out hết phòng");
            return null;
        }

        // Kiểm tra đã có hóa đơn tổng chưa
        List<InvoiceDTO> existing = invoiceDAO.getInvoicesByBooking(bookingId);
        if (!existing.isEmpty()) {
            return existing.get(0);
        }

        return createInvoiceForAllRooms(bookingId, createdBy);
    }

    /**
     * Áp dụng discount vào invoice
     */
    public boolean applyDiscount(int invoiceId, int discountId) {
        InvoiceDTO invoice = invoiceDAO.getInvoiceById(invoiceId);
        if (invoice == null) {
            System.err.println("Lỗi: Không tìm thấy hóa đơn");
            return false;
        }

        DiscountDTO discount = discountBUS.getDiscountById(discountId);
        if (discount == null) {
            System.err.println("Lỗi: Không tìm thấy mã giảm giá");
            return false;
        }

        if (!"ACTIVE".equals(discount.getStatus())) {
            System.err.println("Lỗi: Mã giảm giá không còn hiệu lực");
            return false;
        }

        LocalDateTime now = LocalDateTime.now();

        // Ép kiểu LocalDate nếu cần
        LocalDate startDate = discount.getStartDate().toLocalDate();
        LocalDate expiryDate = discount.getExpiryDate().toLocalDate();

        if (startDate != null && now.isBefore(startDate.atStartOfDay())) {
            System.err.println("Lỗi: Mã giảm giá chưa đến thời gian sử dụng");
            return false;
        }
        if (expiryDate != null && now.isAfter(expiryDate.atTime(LocalTime.MAX))) {
            System.err.println("Lỗi: Mã giảm giá đã hết hạn");
            return false;
        }

        if (discount.getMinSpend() > 0 && invoice.getSubtotal() < discount.getMinSpend()) {
            System.err.println("Lỗi: Tổng tiền chưa đủ điều kiện (Tối thiểu: " + discount.getMinSpend() + ")");
            return false;
        }

        double discountAmount = 0;
        if ("PERCENTAGE".equals(discount.getDiscountType())) {
            discountAmount = invoice.getSubtotal() * (discount.getDiscountValue() / 100);
            if (discount.getMaxDiscountAmount() > 0 && discountAmount > discount.getMaxDiscountAmount()) {
                discountAmount = discount.getMaxDiscountAmount();
            }
        } else {
            discountAmount = discount.getDiscountValue();
        }

        // Cập nhật với BigDecimal để chính xác
        BigDecimal currentDiscount = new BigDecimal(invoice.getDiscountTotal());
        BigDecimal newDiscount = currentDiscount.add(new BigDecimal(discountAmount));
        BigDecimal subtotalBD = new BigDecimal(invoice.getSubtotal());
        BigDecimal taxTotalBD = new BigDecimal(invoice.getTaxTotal());

        invoice.setDiscountTotal(newDiscount.doubleValue());
        invoice.setGrandTotal(subtotalBD.subtract(newDiscount).add(taxTotalBD).doubleValue());

        return invoiceDAO.updateInvoice(invoice);
    }

    /**
     * Xóa discount khỏi invoice
     */
    public boolean removeDiscount(int invoiceId, double discountAmount) {
        InvoiceDTO invoice = invoiceDAO.getInvoiceById(invoiceId);
        if (invoice == null) {
            return false;
        }

        BigDecimal currentDiscount = new BigDecimal(invoice.getDiscountTotal());
        BigDecimal subtract = new BigDecimal(discountAmount);
        BigDecimal newDiscount = currentDiscount.subtract(subtract);
        if (newDiscount.compareTo(BigDecimal.ZERO) < 0) newDiscount = BigDecimal.ZERO;

        BigDecimal subtotalBD = new BigDecimal(invoice.getSubtotal());
        BigDecimal taxTotalBD = new BigDecimal(invoice.getTaxTotal());

        invoice.setDiscountTotal(newDiscount.doubleValue());
        invoice.setGrandTotal(subtotalBD.subtract(newDiscount).add(taxTotalBD).doubleValue());

        return invoiceDAO.updateInvoice(invoice);
    }

    /**
     * Sinh mã hóa đơn
     */
    private String generateInvoiceNo() {
        return "INV" + System.currentTimeMillis();
    }

    // === Các phương thức khác giữ nguyên ===
    public List<InvoiceDTO> getAllInvoices() {
        return invoiceDAO.getAllInvoices();
    }

    public InvoiceDTO getInvoiceById(int invoiceId) {
        if (invoiceId <= 0) {
            System.err.println("ID hóa đơn không hợp lệ");
            return null;
        }
        return invoiceDAO.getInvoiceById(invoiceId);
    }

    public List<InvoiceDTO> getUnpaidInvoices() {
        return invoiceDAO.getUnpaidInvoices();
    }

    public boolean addInvoice(InvoiceDTO invoice) {
        if (!validateInvoice(invoice)) {
            return false;
        }
        return invoiceDAO.addInvoice(invoice);
    }

    public boolean updateInvoice(InvoiceDTO invoice) {
        if (invoice.getInvoiceId() <= 0) {
            System.err.println("ID hóa đơn không hợp lệ");
            return false;
        }
        if (!validateInvoice(invoice)) {
            return false;
        }
        return invoiceDAO.updateInvoice(invoice);
    }

    public boolean deleteInvoice(int invoiceId) {
        if (invoiceId <= 0) {
            System.err.println("ID hóa đơn không hợp lệ");
            return false;
        }
        return invoiceDAO.deleteInvoice(invoiceId);
    }

    public List<InvoiceDTO> searchInvoices(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllInvoices();
        }
        return invoiceDAO.searchInvoices(keyword.trim());
    }

    public List<InvoiceDTO> filterInvoicesByStatus(String status) {
        if (status == null || status.trim().isEmpty() || status.equals("Tất cả")) {
            return getAllInvoices();
        }
        return invoiceDAO.filterInvoicesByStatus(status);
    }

    public List<InvoiceDTO> getInvoicesByBooking(int bookingId) {
        if (bookingId <= 0) {
            return getAllInvoices();
        }
        return invoiceDAO.getInvoicesByBooking(bookingId);
    }

    public InvoiceDTO getInvoiceByBookingId(int bookingId){
        return invoiceDAO.getInvoiceByBookingId(bookingId);
    }

    private boolean validateInvoice(InvoiceDTO invoice) {
        if (invoice == null) {
            System.err.println("Dữ liệu hóa đơn không được null");
            return false;
        }
        if (invoice.getBookingId() <= 0) {
            System.err.println("ID đặt phòng không hợp lệ");
            return false;
        }
        if (invoice.getInvoiceNo() == null || invoice.getInvoiceNo().trim().isEmpty()) {
            System.err.println("Số hóa đơn không được để trống");
            return false;
        }
        if (invoice.getGrandTotal() < 0) {
            System.err.println("Tổng tiền không được âm");
            return false;
        }
        if (invoice.getStatus() == null || invoice.getStatus().trim().isEmpty()) {
            System.err.println("Trạng thái không được để trống");
            return false;
        }
        return true;
    }
}