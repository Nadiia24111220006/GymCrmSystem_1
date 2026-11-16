package com.gym.crm.facade;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class GymFacade {
    private final TrainerService trainerService;
    private final TraineeService traineeService;
    private final TrainingService trainingService;

    @Autowired
    public GymFacade(TrainerService trainerService, TraineeService traineeService, TrainingService trainingService) {
        this.trainerService = trainerService;
        this.traineeService = traineeService;
        this.trainingService = trainingService;
    }

    public Trainee addTrainee(String firstName, String lastName, java.util.Date dateOfBirth, String address) {
        return traineeService.createTrainee(firstName, lastName, dateOfBirth, address);
    }

    public Trainee getTraineeById(long id) {
        return traineeService.findTraineeById(id);
    }

    public Collection <Trainee> getAllTrainees() {
        return traineeService.findAllTrainees();
    }

    public Trainer addTrainer(String firstName, String lastName, TrainingType specialization) {
        return trainerService.createTrainer(firstName, lastName, specialization);
    }

    public Trainer getTrainerById(long id) {
        return trainerService.findTrainerById(id);
    }

    public Collection<Trainer> getAllTrainers() {
        return trainerService.findAllTrainers();
    }

    public Training addTraining(Trainee trainee, TrainingType trainingType, Trainer trainer, String trainingName, java.util.Date trainingDate) {
        return trainingService.createTraining(trainee, trainingType, trainer, trainingName, trainingDate);
    }

    public Collection <Training> getAllTrainings() {
        return trainingService.findAllTrainings();
    }

}
