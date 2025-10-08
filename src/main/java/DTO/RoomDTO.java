package DTO;

public class RoomDTO {
    private int roomId;
    private String roomNo;
    private byte floorNo;
    private int roomTypeId;
    private String status;
    private String note;

    private String roomTypeName;
    private String roomTypeDescription;

    public RoomDTO() {
    }

    public RoomDTO(int roomId, String roomNo, byte floorNo,
                   int roomTypeId, String status, String note) {
        this.roomId = roomId;
        this.roomNo = roomNo;
        this.floorNo = floorNo;
        this.roomTypeId = roomTypeId;
        this.status = status;
        this.note = note;
    }

    public RoomDTO(String roomNo, byte floorNo,
                   int roomTypeId, String status, String note) {
        this.roomNo = roomNo;
        this.floorNo = floorNo;
        this.roomTypeId = roomTypeId;
        this.status = status;
        this.note = note;
    }

    // Getters and Setters
    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public byte getFloorNo() {
        return floorNo;
    }

    public void setFloorNo(byte floorNo) {
        this.floorNo = floorNo;
    }

    public int getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(int roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getRoomTypeName() {
        return roomTypeName;
    }

    public void setRoomTypeName(String roomTypeName) {
        this.roomTypeName = roomTypeName;
    }

    public String getRoomTypeDescription() {
        return roomTypeDescription;
    }

    public void setRoomTypeDescription(String roomTypeDescription) {
        this.roomTypeDescription = roomTypeDescription;
    }

    @Override
    public String toString() {
        return "RoomDTO{" +
                "roomId=" + roomId +
                ", roomNo='" + roomNo + '\'' +
                ", floorNo=" + floorNo +
                ", roomTypeId=" + roomTypeId +
                ", status='" + status + '\'' +
                ", note='" + note + '\'' +
                ", roomTypeName='" + roomTypeName + '\'' +
                '}';
    }
}
