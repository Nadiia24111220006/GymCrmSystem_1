package com.gym.crm.model;

import java.util.Date;

public class Training {
    private Long id;
    private String trainingName;
    private TrainingType trainingType;
    private Trainer trainer;
    private Trainee trainee;
    private Date trainingDate;

    public Training() {
    }

    public Training(String trainingName, TrainingType trainingType, Trainer trainer, Trainee trainee, Date trainingDate) {
        this.trainingName = trainingName;
        this.trainingType = trainingType;
        this.trainer = trainer;
        this.trainee = trainee;
        this.trainingDate = trainingDate;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrainingName() {
        return trainingName;
    }

    public void setTrainingName(String trainingName) {
        this.trainingName = trainingName;
    }

    public TrainingType getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(TrainingType trainingType) {
        this.trainingType = trainingType;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public Trainee getTrainee() {
        return trainee;
    }

    public void setTrainee(Trainee trainee) {
        this.trainee = trainee;
    }

    public Date getTrainingDate() {
        return trainingDate;
    }

    public void setTrainingDate(Date trainingDate) {
        this.trainingDate = trainingDate;
    }

}
