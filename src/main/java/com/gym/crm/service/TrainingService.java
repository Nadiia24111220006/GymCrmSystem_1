package com.gym.crm.service;

import com.gym.crm.dao.TrainingDao;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.TrainingTypeCatalog;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
public class TrainingService {
    private static final int DEFAULT_DURATION_MINUTES = 60;
    private static final Logger log = LoggerFactory.getLogger(TrainingService.class);
    private final TrainingDao trainingDao;

    public TrainingService(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Transactional
    public Training createTraining(Trainee trainee, TrainingType trainingType, Trainer trainer, String trainingName, Date trainingDate) {
        return createTraining(trainee, trainingType, trainer, trainingName, trainingDate, DEFAULT_DURATION_MINUTES);
    }

    @Transactional
    public Training createTraining(Trainee trainee, TrainingType trainingType, Trainer trainer, String trainingName,
                                   Date trainingDate, int duration) {
        validateTrainingInput(trainee, trainingType, trainer, trainingName, trainingDate, duration);
        log.info("Attempting to create training: '{}' for trainee: {} {} with trainer: {} {}",
                trainingName, trainee.getFirstName(), trainee.getLastName(), trainer.getFirstName(), trainer.getLastName());

        Training training = new Training(trainingName, trainingType, trainer, trainee, trainingDate, duration);
        Training savedTraining = trainingDao.save(training);

        log.info("Successfully created training: '{}'", trainingName);
        return savedTraining;
    }

    public Training findTrainingById(long id) {
        log.info("Searching for training by ID: {}", id);
        return trainingDao.findById(id);
    }

    public Collection<Training> findAllTrainings() {
        log.info("Fetching all trainings");
        return trainingDao.findAll();
    }

    public List<Training> findTraineeTrainingsByCriteria(String traineeUsername, Date fromDate, Date toDate,
                                                          String trainerName, String trainingTypeName) {
        validateDateRange(fromDate, toDate);
        if (traineeUsername == null || traineeUsername.isBlank()) {
            throw new IllegalArgumentException("Trainee username is required");
        }
        return trainingDao.findTraineeTrainingsByCriteria(
                traineeUsername, fromDate, toDate, trainerName, trainingTypeName);
    }

    public List<Training> findTrainerTrainingsByCriteria(String trainerUsername, Date fromDate, Date toDate,
                                                          String traineeName) {
        validateDateRange(fromDate, toDate);
        if (trainerUsername == null || trainerUsername.isBlank()) {
            throw new IllegalArgumentException("Trainer username is required");
        }
        return trainingDao.findTrainerTrainingsByCriteria(trainerUsername, fromDate, toDate, traineeName);
    }

    @Transactional
    public void deleteByTraineeUsername(String traineeUsername) {
        trainingDao.deleteByTraineeUsername(traineeUsername);
    }

    private void validateDateRange(Date fromDate, Date toDate) {
        if (fromDate != null && toDate != null && fromDate.after(toDate)) {
            throw new IllegalArgumentException("'fromDate' cannot be after 'toDate'");
        }
    }


    private void validateTrainingInput(Trainee trainee, TrainingType trainingType, Trainer trainer,
                                       String trainingName, Date trainingDate, int duration) {
        if (trainee == null) {
            throw new IllegalArgumentException("Trainee is required");
        }
        if (trainer == null) {
            throw new IllegalArgumentException("Trainer is required");
        }
        if (trainingType == null || trainingType.getTrainingTypeName() == null || trainingType.getTrainingTypeName().isBlank()) {
            throw new IllegalArgumentException("Training type is required");
        }
        if (!TrainingTypeCatalog.isAllowed(trainingType.getTrainingTypeName())) {
            throw new IllegalArgumentException("Unsupported training type: " + trainingType.getTrainingTypeName());
        }
        if (trainingName == null || trainingName.isBlank()) {
            throw new IllegalArgumentException("Training name is required");
        }
        if (trainingDate == null) {
            throw new IllegalArgumentException("Training date is required");
        }
        if (duration <= 0) {
            throw new IllegalArgumentException("Training duration must be greater than 0");
        }
    }
}