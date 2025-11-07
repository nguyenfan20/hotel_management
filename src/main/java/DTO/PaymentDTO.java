package DTO;

import java.sql.Timestamp;

public class PaymentDTO {
    private int paymentId;
    private int bookingId;
    private int invoiceId;
    private double amount;
    private String method;
    private Timestamp paidAt;
    private String referenceNo;
    private String status;
    private String note;
    private String bookingInfo;

    public PaymentDTO() {}

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public PaymentDTO(int paymentId, int bookingId, int invoiceId, double amount, String method,
                      Timestamp paidAt, String referenceNo, String status, String note) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.invoiceId = invoiceId;
        this.amount = amount;
        this.method = method;
        this.paidAt = paidAt;
        this.referenceNo = referenceNo;
        this.status = status;
        this.note = note;
    }

    public int getPaymentId() { return paymentId; }
    public void setPaymentId(int paymentId) { this.paymentId = paymentId; }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public Timestamp getPaidAt() { return paidAt; }
    public void setPaidAt(Timestamp paidAt) { this.paidAt = paidAt; }

    public String getReferenceNo() { return referenceNo; }
    public void setReferenceNo(String referenceNo) { this.referenceNo = referenceNo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getBookingInfo() { return bookingInfo; }
    public void setBookingInfo(String bookingInfo) { this.bookingInfo = bookingInfo; }
}
