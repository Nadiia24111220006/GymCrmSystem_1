package com.gym.crm.model;

import java.util.Date;

public class Trainee extends User {
    private String address;
    private Date dateOfBirth;

    public Trainee() {
        super();
    }

    public Trainee(String firstName, String lastName, String address, Date dateOfBirth) {
        super(firstName, lastName);
        this.address = address;
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Deprecated
    public String getAdress() {
        return address;
    }

    @Deprecated
    public void setAdress(String address) {
        this.address = address;
    }

    @Deprecated
    public Date getDateofBirth() {
        return dateOfBirth;
    }

    @Deprecated
    public void setDateofBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}

