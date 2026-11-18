package com.gym.crm.service;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.Collection;

@Service
public class TraineeService {
    private static final Logger log = LoggerFactory.getLogger(TraineeService.class);
    private TraineeDao traineeDao;
    private TrainerDao trainerDao;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    public Trainee createTrainee(String firstName, String lastName, java.util.Date dateOfBirth, String address) {
        log.info("Attempting to create new trainee with name: {} {}", firstName, lastName);
        String username = generateUsername(firstName, lastName);
        String password = generatePassword();

        Trainee trainee = new Trainee(firstName, lastName, address, dateOfBirth);
        trainee.setUsername(username);
        trainee.setPassword(password);
        trainee.setActive(true);

        return traineeDao.save(trainee);
    }

    private boolean isUsernameTaken(String username) {
        for (Trainee trainee : traineeDao.findAll()) {
            if (trainee.getUsername() != null && trainee.getUsername().equals(username)) return true;
        }
        for (Trainer trainer : trainerDao.findAll()) {
            if (trainer.getUsername() != null && trainer.getUsername().equals(username)) return true;
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
        log.debug("Generated unique username: {}", username);
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

    public Trainee findTraineeById(long id) {
        log.info("Searching for trainee by ID: {}", id);
        return traineeDao.findById(id);
    }

    public Collection<Trainee> findAllTrainees() {
        log.info("Fetching all trainees");
        return traineeDao.findAll();
    }

    public Trainee update(long id, Trainee trainee) {
        if (trainee.getUsername() != null) {
            Trainee existing = traineeDao.findById(id);
            if (existing == null || !existing.getUsername().equals(trainee.getUsername())) {
                if (isUsernameTaken(trainee.getUsername())) {
                    log.warn("Username {} is already taken. Generating unique one.", trainee.getUsername());
                    trainee.setUsername(generateUsername(trainee.getFirstName(), trainee.getLastName()));
                }
            }
        }
        return traineeDao.update(id, trainee);
    }
        public void delete ( long id){
            traineeDao.delete(id);
        }
}