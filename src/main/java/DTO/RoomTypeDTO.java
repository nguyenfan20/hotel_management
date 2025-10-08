package DTO;

import java.math.BigDecimal;

public class RoomTypeDTO {
    private int roomTypeId;
    private String name;
    private BigDecimal basePrice;
    private byte capacityAdults;
    private byte capacityChildren;
    private byte bedCount;
    private BigDecimal area;
    private String description;

    public RoomTypeDTO() {
    }

    public RoomTypeDTO(int roomTypeId, String name, BigDecimal basePrice,
                       byte capacityAdults, byte capacityChildren,
                       byte bedCount, BigDecimal area, String description) {
        this.roomTypeId = roomTypeId;
        this.name = name;
        this.basePrice = basePrice;
        this.capacityAdults = capacityAdults;
        this.capacityChildren = capacityChildren;
        this.bedCount = bedCount;
        this.area = area;
        this.description = description;
    }

    public RoomTypeDTO(String name, BigDecimal basePrice,
                       byte capacityAdults, byte capacityChildren,
                       byte bedCount, BigDecimal area, String description) {
        this.name = name;
        this.basePrice = basePrice;
        this.capacityAdults = capacityAdults;
        this.capacityChildren = capacityChildren;
        this.bedCount = bedCount;
        this.area = area;
        this.description = description;
    }

    // Getters and Setters
    public int getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(int roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public byte getCapacityAdults() {
        return capacityAdults;
    }

    public void setCapacityAdults(byte capacityAdults) {
        this.capacityAdults = capacityAdults;
    }

    public byte getCapacityChildren() {
        return capacityChildren;
    }

    public void setCapacityChildren(byte capacityChildren) {
        this.capacityChildren = capacityChildren;
    }

    public byte getBedCount() {
        return bedCount;
    }

    public void setBedCount(byte bedCount) {
        this.bedCount = bedCount;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "RoomTypeDTO{" +
                "roomTypeId=" + roomTypeId +
                ", name='" + name + '\'' +
                ", basePrice=" + basePrice +
                ", capacityAdults=" + capacityAdults +
                ", capacityChildren=" + capacityChildren +
                ", bedCount=" + bedCount +
                ", area=" + area +
                ", description='" + description + '\'' +
                '}';
    }
}
