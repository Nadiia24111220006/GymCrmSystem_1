package com.gym.crm.service;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.model.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Random;

@Service
public class TraineeService {
    private static final Logger log = LoggerFactory.getLogger(TraineeService.class);
    private TraineeDao traineeDao;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
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

    private String generateUsername(String firstName, String lastName) {
        String baseUsername = firstName + "." + lastName;
        String username = baseUsername;
        int counter = 1;

        boolean exists;
        do {
            exists = false;
            for (Trainee t : traineeDao.findAll()) {
                if (t.getUsername() != null && t.getUsername().equals(username)) {
                    exists = true;
                    username = baseUsername + counter;
                    counter++;
                    break;
                }
            }
        } while (exists);
        log.debug("Generated unique username: {}", username);
        return username;
    }

    private String generatePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
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
        return traineeDao.update(id, trainee);
    }

    public void delete(long id) {
        traineeDao.delete(id);
    }
}