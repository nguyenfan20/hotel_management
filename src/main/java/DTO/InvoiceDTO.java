package DTO;

import java.sql.Timestamp;

public class InvoiceDTO {
    private int invoiceId;
    private int bookingId;
    private String invoiceNo;
    private double subtotal;
    private double discountTotal;
    private double taxTotal;
    private double grandTotal;
    private Timestamp createdAt;
    private int createdBy;
    private String status;
    private String createdByName;
    private String bookingInfo;

    public InvoiceDTO() {}

    public InvoiceDTO(int invoiceId, int bookingId, String invoiceNo, double subtotal,
                      double discountTotal, double taxTotal, double grandTotal,
                      Timestamp createdAt, int createdBy, String status) {
        this.invoiceId = invoiceId;
        this.bookingId = bookingId;
        this.invoiceNo = invoiceNo;
        this.subtotal = subtotal;
        this.discountTotal = discountTotal;
        this.taxTotal = taxTotal;
        this.grandTotal = grandTotal;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.status = status;
    }

    public int getInvoiceId() { return invoiceId; }
    public void setInvoiceId(int invoiceId) { this.invoiceId = invoiceId; }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public String getInvoiceNo() { return invoiceNo; }
    public void setInvoiceNo(String invoiceNo) { this.invoiceNo = invoiceNo; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getDiscountTotal() { return discountTotal; }
    public void setDiscountTotal(double discountTotal) { this.discountTotal = discountTotal; }

    public double getTaxTotal() { return taxTotal; }
    public void setTaxTotal(double taxTotal) { this.taxTotal = taxTotal; }

    public double getGrandTotal() { return grandTotal; }
    public void setGrandTotal(double grandTotal) { this.grandTotal = grandTotal; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    public String getBookingInfo() { return bookingInfo; }
    public void setBookingInfo(String bookingInfo) { this.bookingInfo = bookingInfo; }
}
