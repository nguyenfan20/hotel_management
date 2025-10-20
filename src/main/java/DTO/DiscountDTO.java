package DTO;

import java.sql.Date;

public class DiscountDTO {
    private int discountId;
    private String code;
    private String discountType;
    private double discountValue;
    private double minSpend;
    private double maxDiscountAmount;
    private Date startDate;
    private Date expiryDate;
    private int usageLimit;
    private int perUserLimit;
    private String status;

    public DiscountDTO() {}

    public DiscountDTO(int discountId, String code, String discountType, double discountValue,
                       double minSpend, double maxDiscountAmount, Date startDate, Date expiryDate,
                       int usageLimit, int perUserLimit, String status) {
        this.discountId = discountId;
        this.code = code;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minSpend = minSpend;
        this.maxDiscountAmount = maxDiscountAmount;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.usageLimit = usageLimit;
        this.perUserLimit = perUserLimit;
        this.status = status;
    }

    public int getDiscountId() { return discountId; }
    public void setDiscountId(int discountId) { this.discountId = discountId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }

    public double getDiscountValue() { return discountValue; }
    public void setDiscountValue(double discountValue) { this.discountValue = discountValue; }

    public double getMinSpend() { return minSpend; }
    public void setMinSpend(double minSpend) { this.minSpend = minSpend; }

    public double getMaxDiscountAmount() { return maxDiscountAmount; }
    public void setMaxDiscountAmount(double maxDiscountAmount) { this.maxDiscountAmount = maxDiscountAmount; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }

    public int getUsageLimit() { return usageLimit; }
    public void setUsageLimit(int usageLimit) { this.usageLimit = usageLimit; }

    public int getPerUserLimit() { return perUserLimit; }
    public void setPerUserLimit(int perUserLimit) { this.perUserLimit = perUserLimit; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return code + " - " + discountType;
    }
}
