package com.gym.crm.service;

import com.gym.crm.dao.TrainerDao;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Random;

@Service
public class TrainerService {
    private static final Logger log = LoggerFactory.getLogger(TrainerService.class);
    private TrainerDao trainerDao;

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
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

    private String generateUsername(String firstName, String lastName) {
        String baseUsername = firstName + "." + lastName;
        String username = baseUsername;
        int counter = 1;

        boolean exists;
        do {
            exists = false;
            for (Trainer t : trainerDao.findAll()) {
                if (t.getUsername() != null && t.getUsername().equals(username)) {
                    exists = true;
                    username = baseUsername + counter;
                    counter++;
                    break;
                }
            }
        } while (exists);

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

    public Trainer findTrainerById(long id) {
        log.info("Searching for trainer by ID: {}", id);
        return trainerDao.findById(id);
    }

    public Collection<Trainer> findAllTrainers() {
        log.info("Fetching all trainers");
        return trainerDao.findAll();
    }

    public Trainer update(long id, Trainer trainer) {
        return trainerDao.update(id, trainer);
    }

    public void delete(long id) {
        trainerDao.delete(id);
    }
}