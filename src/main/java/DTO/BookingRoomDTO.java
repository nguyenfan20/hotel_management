package DTO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for BookingRoom entity
 */
public class BookingRoomDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int bookingRoomId;
    private int bookingId;
    private int roomId;
    private LocalDateTime checkInPlan;
    private LocalDateTime checkOutPlan;
    private LocalDateTime checkInActual;
    private LocalDateTime checkOutActual;
    private int adults;
    private int children;
    private BigDecimal ratePerNight;
    private BigDecimal discountAmount;
    private BigDecimal taxRate;
    private String status;

    // Constructors
    public BookingRoomDTO() {
    }

    public BookingRoomDTO(int bookingId, int roomId, LocalDateTime checkInPlan, LocalDateTime checkOutPlan,
                          int adults, int children, BigDecimal ratePerNight, String status) {
        this.bookingId = bookingId;
        this.roomId = roomId;
        this.checkInPlan = checkInPlan;
        this.checkOutPlan = checkOutPlan;
        this.adults = adults;
        this.children = children;
        this.ratePerNight = ratePerNight;
        this.status = status;
    }

    public BookingRoomDTO(int bookingRoomId, int bookingId, int roomId, LocalDateTime checkInPlan,
                          LocalDateTime checkOutPlan, LocalDateTime checkInActual, LocalDateTime checkOutActual,
                          int adults, int children, BigDecimal ratePerNight, BigDecimal discountAmount,
                          BigDecimal taxRate, String status) {
        this.bookingRoomId = bookingRoomId;
        this.bookingId = bookingId;
        this.roomId = roomId;
        this.checkInPlan = checkInPlan;
        this.checkOutPlan = checkOutPlan;
        this.checkInActual = checkInActual;
        this.checkOutActual = checkOutActual;
        this.adults = adults;
        this.children = children;
        this.ratePerNight = ratePerNight;
        this.discountAmount = discountAmount;
        this.taxRate = taxRate;
        this.status = status;
    }

    // Getters and Setters
    public int getBookingRoomId() {
        return bookingRoomId;
    }

    public void setBookingRoomId(int bookingRoomId) {
        this.bookingRoomId = bookingRoomId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public LocalDateTime getCheckInPlan() {
        return checkInPlan;
    }

    public void setCheckInPlan(LocalDateTime checkInPlan) {
        this.checkInPlan = checkInPlan;
    }

    public LocalDateTime getCheckOutPlan() {
        return checkOutPlan;
    }

    public void setCheckOutPlan(LocalDateTime checkOutPlan) {
        this.checkOutPlan = checkOutPlan;
    }

    public LocalDateTime getCheckInActual() {
        return checkInActual;
    }

    public void setCheckInActual(LocalDateTime checkInActual) {
        this.checkInActual = checkInActual;
    }

    public LocalDateTime getCheckOutActual() {
        return checkOutActual;
    }

    public void setCheckOutActual(LocalDateTime checkOutActual) {
        this.checkOutActual = checkOutActual;
    }

    public int getAdults() {
        return adults;
    }

    public void setAdults(int adults) {
        this.adults = adults;
    }

    public int getChildren() {
        return children;
    }

    public void setChildren(int children) {
        this.children = children;
    }

    public BigDecimal getRatePerNight() {
        return ratePerNight;
    }

    public void setRatePerNight(BigDecimal ratePerNight) {
        this.ratePerNight = ratePerNight;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "BookingRoomDTO{" +
                "bookingRoomId=" + bookingRoomId +
                ", bookingId=" + bookingId +
                ", roomId=" + roomId +
                ", checkInPlan=" + checkInPlan +
                ", checkOutPlan=" + checkOutPlan +
                ", checkInActual=" + checkInActual +
                ", checkOutActual=" + checkOutActual +
                ", adults=" + adults +
                ", children=" + children +
                ", ratePerNight=" + ratePerNight +
                ", discountAmount=" + discountAmount +
                ", taxRate=" + taxRate +
                ", status='" + status + '\'' +
                '}';
    }
}
