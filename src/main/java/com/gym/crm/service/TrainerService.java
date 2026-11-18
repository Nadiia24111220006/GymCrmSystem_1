package com.gym.crm.service;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.Collection;

@Service
public class TrainerService {
    private static final Logger log = LoggerFactory.getLogger(TrainerService.class);
    private TrainerDao trainerDao;
    private TraineeDao traineeDao;

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    public Trainer createTrainer(String firstName, String lastName, TrainingType specialization) {
        log.info("Attempting to create new trainer: {} {}", firstName, lastName);
        String username = generateUsername(firstName, lastName);
        String password = generatePassword();

        Trainer trainer = new Trainer(firstName, lastName, specialization);
        trainer.setUsername(username);
        trainer.setPassword(password);
        trainer.setActive(true);
        log.info("Successfully created trainer with username: {}", username);
        return trainerDao.save(trainer);
    }

    private boolean isUsernameTaken(String username) {
        for (Trainer t : trainerDao.findAll()) {
            if (t.getUsername() != null && t.getUsername().equals(username)) return true;
        }
        for (Trainee tr : traineeDao.findAll()) {
            if (tr.getUsername() != null && tr.getUsername().equals(username)) return true;
        }
        return false;
    }

    private String generateUsername(String firstName, String lastName) {
        String baseUsername = firstName + "." + lastName;
        String username = baseUsername;
        int counter = 1;

        while (isUsernameTaken(username)) {
            username = baseUsername + counter++;
        }

        return username;
    }

    private String generatePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public Trainer findTrainerById(long id) {
        log.info("Searching for trainer by ID: {}", id);
        return trainerDao.findById(id);
    }

    public Collection<Trainer> findAllTrainers() {
        log.info("Fetching all trainers");
        return trainerDao.findAll();
    }

    public Trainer update(long id, Trainer trainer) {
        if (trainer.getUsername() != null) {
            Trainer existing = trainerDao.findById(id);
            if (existing == null || !existing.getUsername().equals(trainer.getUsername())) {
                if (isUsernameTaken(trainer.getUsername())) {
                    log.warn("Username {} is already taken. Regenerating unique one.", trainer.getUsername());
                    trainer.setUsername(generateUsername(trainer.getFirstName(), trainer.getLastName()));
                }
            }
        }
        return trainerDao.update(id, trainer);
    }

    public void delete(long id) {
        trainerDao.delete(id);
    }
}