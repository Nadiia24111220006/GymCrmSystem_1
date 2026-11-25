package com.gym.crm.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Column;

import java.util.Date;

@Entity
@Table(name = "training")
public class Training {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String trainingName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "training_type_id", nullable = false)
    private TrainingType trainingType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trainee_id", nullable = false)
    private Trainee trainee;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date trainingDate;

    @Column(nullable = false)
    private Integer trainingDuration;

    public Training() {
    }

    public Training(String trainingName, TrainingType trainingType, Trainer trainer, Trainee trainee, Date trainingDate) {
        this(trainingName, trainingType, trainer, trainee, trainingDate, 0);
    }

    public Training(String trainingName, TrainingType trainingType, Trainer trainer, Trainee trainee, Date trainingDate,
                    Integer trainingDuration) {
        this.trainingName = trainingName;
        this.trainingType = trainingType;
        this.trainer = trainer;
        this.trainee = trainee;
        this.trainingDate = trainingDate;
        this.trainingDuration = trainingDuration;

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

    public Integer getTrainingDuration() {
        return trainingDuration;
    }

    public void setTrainingDuration(Integer trainingDuration) {
        this.trainingDuration = trainingDuration;
    }

}
