package DTO;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Data Transfer Object for Guest entity
 */
public class GuestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int guestId;
    private int bookingRoomId;
    private String fullName;
    private String gender;
    private LocalDate dob;
    private String idCard;
    private String nationality;

    // Constructors
    public GuestDTO() {
    }

    public GuestDTO(String fullName, String gender, LocalDate dob, String idCard, String nationality) {
        this.fullName = fullName;
        this.gender = gender;
        this.dob = dob;
        this.idCard = idCard;
        this.nationality = nationality;
    }

    public GuestDTO(int guestId, int bookingRoomId, String fullName, String gender, LocalDate dob,
                    String idCard, String nationality) {
        this.guestId = guestId;
        this.bookingRoomId = bookingRoomId;
        this.fullName = fullName;
        this.gender = gender;
        this.dob = dob;
        this.idCard = idCard;
        this.nationality = nationality;
    }

    // Getters and Setters
    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public int getBookingRoomId() {
        return bookingRoomId;
    }

    public void setBookingRoomId(int bookingRoomId) {
        this.bookingRoomId = bookingRoomId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    @Override
    public String toString() {
        return "GuestDTO{" +
                "guestId=" + guestId +
                ", bookingRoomId=" + bookingRoomId +
                ", fullName='" + fullName + '\'' +
                ", gender='" + gender + '\'' +
                ", dob=" + dob +
                ", idCard='" + idCard + '\'' +
                ", nationality='" + nationality + '\'' +
                '}';
    }
}
