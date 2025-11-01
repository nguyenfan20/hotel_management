package DTO;

import java.io.Serializable;
import java.time.LocalDateTime;

// DTO cho Booking
public class BookingDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int bookingId;
    private String code;
    private int customerId;
    private LocalDateTime bookingDate;
    private String source;
    private String status;
    private int createdBy;
    private String note;

    // Constructors
    public BookingDTO() {
    }

    public BookingDTO(String code, int customerId, LocalDateTime bookingDate, String source, String status) {
        this.code = code;
        this.customerId = customerId;
        this.bookingDate = bookingDate;
        this.source = source;
        this.status = status;
    }

    public BookingDTO(int bookingId, String code, int customerId, LocalDateTime bookingDate,
                      String source, String status, int createdBy, String note) {
        this.bookingId = bookingId;
        this.code = code;
        this.customerId = customerId;
        this.bookingDate = bookingDate;
        this.source = source;
        this.status = status;
        this.createdBy = createdBy;
        this.note = note;
    }

    // Getters and Setters
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "BookingDTO{" +
                "bookingId=" + bookingId +
                ", code='" + code + '\'' +
                ", customerId=" + customerId +
                ", bookingDate=" + bookingDate +
                ", source='" + source + '\'' +
                ", status='" + status + '\'' +
                ", createdBy=" + createdBy +
                ", note='" + note + '\'' +
                '}';
    }
}
