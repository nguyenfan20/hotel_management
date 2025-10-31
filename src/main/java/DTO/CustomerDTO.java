package DTO;

import java.util.Date;

public class CustomerDTO{
    private int customer_id;
    private String full_name;
    private int phone;
    private String email;
    private String id_card;
    private String nationality;
    private Date dob;
    private String gender;
    private String note;

    public CustomerDTO(){
    }

    public CustomerDTO(int customer_id, String full_name, int phone, 
    String email, String id_card, String nationality, Date dob, String genger, String note){

        this.customer_id=customer_id;
        this.full_name=full_name;
        this.nationality=nationality;
        this.dob=dob;
        this.phone=phone;
        this.email=email;
        this.id_card=id_card;
        this.nationality=nationality;
        this.dob=dob;
        this.gender=genger;
        this.note=note;
    }
    public CustomerDTO (String full_name, int phone, 
    String email, String id_card, String nationality, Date dob, String genger, String note){
        this.full_name=full_name;
        this.nationality=nationality;
        this.dob=dob;
        this.phone=phone;
        this.email=email;
        this.id_card=id_card;
        this.nationality=nationality;
        this.dob=dob;
        this.gender=genger;
        this.note=note;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId_card() {
        return id_card;
    }

    public void setId_card(String id_card) {
        this.id_card = id_card;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}