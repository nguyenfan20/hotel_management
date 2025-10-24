package DTO;

import java.time.LocalDateTime;

public class BookingDTO {
    private int bookingId ;
    private String code;
    private int customerID;
    private LocalDateTime booking_date;
    private String source;
    private String status;
    private int created_by;
    private String note;

    public BookingDTO() {
    }

    public BookingDTO(int bookingId, String code, int customerID, LocalDateTime booking_date, String source, String status, int created_by, String note) {
        this.bookingId = bookingId;
        this.code = code;
        this.customerID = customerID;
        this.booking_date = booking_date;
        this.source = source;
        this.status = status;
        this.created_by = created_by;
        this.note = note;
    }

    public BookingDTO(String code, int customerID, LocalDateTime booking_date, String source, String status, int created_by, String note) {
        this.code = code;
        this.customerID = customerID;
        this.booking_date = booking_date;
        this.source = source;
        this.status = status;
        this.created_by = created_by;
        this.note = note;
    }

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

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public LocalDateTime getBooking_date() {
        return booking_date;
    }

    public void setBooking_date(LocalDateTime booking_date) {
        this.booking_date = booking_date;
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

    public int getCreated_by() {
        return created_by;
    }

    public void setCreated_by(int created_by) {
        this.created_by = created_by;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
