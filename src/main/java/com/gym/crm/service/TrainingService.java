package com.gym.crm.service;

import com.gym.crm.dao.TrainingDao;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Date;

@Service
public class TrainingService {
    private static final Logger log = LoggerFactory.getLogger(TrainingService.class);
    private TrainingDao trainingDao;

    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    public Training createTraining(Trainee trainee, TrainingType trainingType, Trainer trainer, String trainingName, Date trainingDate) {
        log.info("Attempting to create training: '{}' for trainee: {} {} with trainer: {} {}",
                trainingName, trainee.getFirstName(), trainee.getLastName(), trainer.getFirstName(), trainer.getLastName());

        Training training = new Training(trainingName, trainingType, trainer, trainee, trainingDate);
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
}