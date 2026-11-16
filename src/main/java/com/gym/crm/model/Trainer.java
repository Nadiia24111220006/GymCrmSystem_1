package com.gym.crm.model;

public class Trainer extends User {
    private TrainingType specialization;

    public Trainer() {
        super();
    }
    public Trainer(String firstName, String lastName, TrainingType specialization) {
        super(firstName, lastName);
        this.specialization = specialization;
    }

    public TrainingType getSpecialization() {
        return specialization;
    }

    public void setSpecialization(TrainingType specialization) {
        this.specialization = specialization;
    }
}