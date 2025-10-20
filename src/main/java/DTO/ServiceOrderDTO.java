package DTO;

import java.time.LocalDateTime;

public class ServiceOrderDTO {
    private int serviceOrderId;   // service_order_id
    private int bookingRoomId;    // booking_room_id
    private int serviceId;        // service_id
    private int quantity;         // qty
    private double unitPrice;     // unit_price
    private LocalDateTime orderedAt; // ordered_at
    private String orderedBy;     // ordered_by
    private String note;          // note

    public ServiceOrderDTO() {}

    public ServiceOrderDTO(int serviceOrderId, int bookingRoomId, int serviceId,
                           int quantity, double unitPrice,
                           LocalDateTime orderedAt, String orderedBy, String note) {
        this.serviceOrderId = serviceOrderId;
        this.bookingRoomId = bookingRoomId;
        this.serviceId = serviceId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.orderedAt = orderedAt;
        this.orderedBy = orderedBy;
        this.note = note;
    }

    // Getters & Setters
    public int getServiceOrderId() { return serviceOrderId; }
    public void setServiceOrderId(int serviceOrderId) { this.serviceOrderId = serviceOrderId; }

    public int getBookingRoomId() { return bookingRoomId; }
    public void setBookingRoomId(int bookingRoomId) { this.bookingRoomId = bookingRoomId; }

    public int getServiceId() { return serviceId; }
    public void setServiceId(int serviceId) { this.serviceId = serviceId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    public LocalDateTime getOrderedAt() { return orderedAt; }
    public void setOrderedAt(LocalDateTime orderedAt) { this.orderedAt = orderedAt; }

    public String getOrderedBy() { return orderedBy; }
    public void setOrderedBy(String orderedBy) { this.orderedBy = orderedBy; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    @Override
    public String toString() {
        return "ServiceOrderDTO{" +
                "serviceOrderId=" + serviceOrderId +
                ", bookingRoomId=" + bookingRoomId +
                ", serviceId=" + serviceId +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", orderedAt=" + orderedAt +
                ", orderedBy='" + orderedBy + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}
