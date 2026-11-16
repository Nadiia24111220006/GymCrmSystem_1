package com.gym.crm.model;

import java.util.Date;

public class Trainee extends User {
    private String adress;
    private Date dateofBirth;

    public Trainee() {
        super();
    }
        public Trainee(String firstName, String lastName, String adress, Date dateofBirth) {
        super(firstName, lastName);
        this.adress = adress;
        this.dateofBirth = dateofBirth;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public Date getDateofBirth() {
        return dateofBirth;
    }

    public void setDateofBirth(Date dateofBirth) {
        this.dateofBirth = dateofBirth;
    }
}

