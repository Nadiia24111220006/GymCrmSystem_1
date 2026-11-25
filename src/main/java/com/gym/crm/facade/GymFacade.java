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
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public Trainee createTraineeProfile(String firstName, String lastName, Date dateOfBirth, String address) {
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

    public Trainer createTrainerProfile(String firstName, String lastName, TrainingType specialization) {
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

    public boolean traineeUsernamePasswordMatches(String username, String password) {
        return traineeService.authenticate(username, password);
    }

    public boolean trainerUsernamePasswordMatches(String username, String password) {
        return trainerService.authenticate(username, password);
    }

    public Trainer selectTrainerByUsername(String authUsername, String authPassword, String trainerUsername) {
        ensureTrainerAuthenticated(authUsername, authPassword);
        return trainerService.findByUsername(trainerUsername);
    }

    public Trainee selectTraineeByUsername(String authUsername, String authPassword, String traineeUsername) {
        ensureTraineeAuthenticated(authUsername, authPassword);
        return traineeService.findByUsername(traineeUsername);
    }

    public void changeTraineePassword(String authUsername, String authPassword, String traineeUsername,
                                      String oldPassword, String newPassword) {
        ensureTraineeAuthenticated(authUsername, authPassword);
        Trainee trainee = traineeService.findByUsername(traineeUsername);
        if (trainee == null) {
            throw new IllegalArgumentException("Trainee not found");
        }
        traineeService.changePassword(traineeUsername, oldPassword, newPassword);
    }

    public void changeTrainerPassword(String authUsername, String authPassword, String trainerUsername,
                                      String oldPassword, String newPassword) {
        ensureTrainerAuthenticated(authUsername, authPassword);
        Trainer trainer = trainerService.findByUsername(trainerUsername);
        if (trainer == null) {
            throw new IllegalArgumentException("Trainer not found");
        }
        trainerService.changePassword(trainerUsername, oldPassword, newPassword);
    }

    public Trainer updateTrainerProfile(String authUsername, String authPassword, String trainerUsername, String firstName,
                                        String lastName, TrainingType specialization) {
        ensureTrainerAuthenticated(authUsername, authPassword);
        return trainerService.updateByUsername(trainerUsername, firstName, lastName, specialization);
    }

    public Trainee updateTraineeProfile(String authUsername, String authPassword, String traineeUsername, String firstName,
                                        String lastName, Date dateOfBirth, String address) {
        ensureTraineeAuthenticated(authUsername, authPassword);
        return traineeService.updateByUsername(traineeUsername, firstName, lastName, dateOfBirth, address);
    }

    public void activateTrainee(String authUsername, String authPassword, String traineeUsername) {
        ensureTraineeAuthenticated(authUsername, authPassword);
        traineeService.activate(traineeUsername);
    }

    public void deactivateTrainee(String authUsername, String authPassword, String traineeUsername) {
        ensureTraineeAuthenticated(authUsername, authPassword);
        traineeService.deactivate(traineeUsername);
    }

    public void activateTrainer(String authUsername, String authPassword, String trainerUsername) {
        ensureTrainerAuthenticated(authUsername, authPassword);
        trainerService.activate(trainerUsername);
    }

    public void deactivateTrainer(String authUsername, String authPassword, String trainerUsername) {
        ensureTrainerAuthenticated(authUsername, authPassword);
        trainerService.deactivate(trainerUsername);
    }

    public void deleteTraineeProfile(String authUsername, String authPassword, String traineeUsername) {
        ensureTraineeAuthenticated(authUsername, authPassword);
        trainingService.deleteByTraineeUsername(traineeUsername);
        traineeService.deleteByUsername(traineeUsername);
    }

    public List<Training> getTraineeTrainings(String authUsername, String authPassword, String traineeUsername, Date fromDate,
                                              Date toDate, String trainerName, String trainingType) {
        ensureTraineeAuthenticated(authUsername, authPassword);
        return trainingService.findTraineeTrainingsByCriteria(traineeUsername, fromDate, toDate, trainerName, trainingType);
    }

    public List<Training> getTrainerTrainings(String authUsername, String authPassword, String trainerUsername, Date fromDate,
                                              Date toDate, String traineeName) {
        ensureTrainerAuthenticated(authUsername, authPassword);
        return trainingService.findTrainerTrainingsByCriteria(trainerUsername, fromDate, toDate, traineeName);
    }

    public Training addTraining(String authUsername, String authPassword, String traineeUsername, String trainerUsername,
                                String trainingName, String trainingTypeName, Date trainingDate, int trainingDuration) {
        ensureTraineeAuthenticated(authUsername, authPassword);
        Trainee trainee = traineeService.findByUsername(traineeUsername);
        Trainer trainer = trainerService.findByUsername(trainerUsername);
        if (trainee == null) {
            throw new IllegalArgumentException("Trainee not found");
        }
        if (trainer == null) {
            throw new IllegalArgumentException("Trainer not found");
        }

        Training training = trainingService.createTraining(
                trainee,
                new TrainingType(trainingTypeName),
                trainer,
                trainingName,
                trainingDate,
                trainingDuration
        );

        Set<String> trainerUsernames = trainee.getTrainerUsernames();
        trainerUsernames.add(trainerUsername);
        traineeService.updateTrainerAssignments(traineeUsername, trainerUsernames);
        return training;
    }

    public List<Trainer> getUnassignedTrainersForTrainee(String authUsername, String authPassword, String traineeUsername) {
        ensureTraineeAuthenticated(authUsername, authPassword);
        Trainee trainee = traineeService.findByUsername(traineeUsername);
        if (trainee == null) {
            throw new IllegalArgumentException("Trainee not found");
        }
        Set<String> assigned = trainee.getTrainerUsernames();
        return trainerService.findAllTrainers().stream()
                .filter(trainer -> trainer.getUsername() != null && !assigned.contains(trainer.getUsername()))
                .collect(Collectors.toList());
    }

    public void updateTraineeTrainersList(String authUsername, String authPassword, String traineeUsername,
                                          Set<String> trainerUsernames) {
        ensureTraineeAuthenticated(authUsername, authPassword);
        traineeService.updateTrainerAssignments(traineeUsername, trainerUsernames);
    }

    public Collection <Training> getAllTrainings() {
        return trainingService.findAllTrainings();
    }

    private void ensureAuthenticated(String username, String password) {
        boolean authenticated = traineeService.authenticate(username, password)
                || trainerService.authenticate(username, password);
        if (!authenticated) {
            throw new SecurityException("Authentication failed");
        }
    }

    private void ensureTraineeAuthenticated(String username, String password) {
        if (!traineeService.authenticate(username, password)) {
            throw new SecurityException("Trainee authentication failed");
        }
    }

    private void ensureTrainerAuthenticated(String username, String password) {
        if (!trainerService.authenticate(username, password)) {
            throw new SecurityException("Trainer authentication failed");
        }
    }

}
