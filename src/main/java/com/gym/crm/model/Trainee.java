package com.gym.crm.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "trainee")
@PrimaryKeyJoinColumn(name = "id")
public class Trainee extends User {
    private String address;
    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "trainee_trainer",
            joinColumns = @JoinColumn(name = "trainee_id"),
            inverseJoinColumns = @JoinColumn(name = "trainer_id"))
    private Set<Trainer> trainers = new HashSet<>();

    @OneToMany(mappedBy = "trainee", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Training> trainings = new HashSet<>();

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

    @JsonAlias("adress")
    public void setAddress(String address) {
        this.address = address;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    @JsonAlias("dateofBirth")
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Set<Trainer> getTrainers() {
        return trainers;
    }

    public void setTrainers(Set<Trainer> trainers) {
        this.trainers = trainers == null ? new HashSet<>() : trainers;
    }

    public Set<Training> getTrainings() {
        return trainings;
    }

    public void setTrainings(Set<Training> trainings) {
        this.trainings = trainings == null ? new HashSet<>() : trainings;
    }

    public Set<String> getTrainerUsernames() {
        Set<String> usernames = new HashSet<>();
        if (trainers != null) {
            for (Trainer trainer : trainers) {
                if (trainer != null && trainer.getUsername() != null) {
                    usernames.add(trainer.getUsername());
                }
            }
        }
        return usernames;
    }
}
