package DTO;

import java.math.BigDecimal;

public class AmenityDTO {
    private int amenityId;
    private String name;
    private String chargeType;
    private BigDecimal price;
    private String description;

    public AmenityDTO() {
    }

    public AmenityDTO(int amenityId, String name, String chargeType,
                      BigDecimal price, String description) {
        this.amenityId = amenityId;
        this.name = name;
        this.chargeType = chargeType;
        this.price = price;
        this.description = description;
    }

    public AmenityDTO(String name, String chargeType,
                      BigDecimal price, String description) {
        this.name = name;
        this.chargeType = chargeType;
        this.price = price;
        this.description = description;
    }

    // Getters and Setters
    public int getAmenityId() {
        return amenityId;
    }

    public void setAmenityId(int amenityId) {
        this.amenityId = amenityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChargeType() {
        return chargeType;
    }

    public void setChargeType(String chargeType) {
        this.chargeType = chargeType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "AmenityDTO{" +
                "amenityId=" + amenityId +
                ", name='" + name + '\'' +
                ", chargeType='" + chargeType + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                '}';
    }
}
