package com.gym.crm.service;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.TrainingTypeCatalog;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Collection;

@Service
public class TrainerService {
    private static final Logger log = LoggerFactory.getLogger(TrainerService.class);
    private final TrainerDao trainerDao;
    private final TraineeDao traineeDao;

    public TrainerService(TrainerDao trainerDao, TraineeDao traineeDao) {
        this.trainerDao = trainerDao;
        this.traineeDao = traineeDao;
    }

    @Transactional
    public Trainer createTrainer(String firstName, String lastName, TrainingType specialization) {
        validateName(firstName, "Trainer first name");
        validateName(lastName, "Trainer last name");
        if (specialization == null || specialization.getTrainingTypeName() == null || specialization.getTrainingTypeName().isBlank()) {
            throw new IllegalArgumentException("Trainer specialization is required");
        }
        if (!TrainingTypeCatalog.isAllowed(specialization.getTrainingTypeName())) {
            throw new IllegalArgumentException("Unsupported training type: " + specialization.getTrainingTypeName());
        }
        log.info("Attempting to create new trainer: {} {}", firstName, lastName);
        String username = generateUsername(firstName, lastName);
        String password = generatePassword();

        Trainer trainer = new Trainer(firstName, lastName, specialization);
        trainer.setUsername(username);
        trainer.setPassword(PasswordUtils.encode(password));
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

    public Trainer findByUsername(String username) {
        return trainerDao.findByUsername(username);
    }

    public Collection<Trainer> findAllTrainers() {
        log.info("Fetching all trainers");
        return trainerDao.findAll();
    }

    @Transactional
    public Trainer update(long id, Trainer trainer) {
        validateName(trainer.getFirstName(), "Trainer first name");
        validateName(trainer.getLastName(), "Trainer last name");
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

    @Transactional
    public Trainer updateByUsername(String username, String firstName, String lastName, TrainingType specialization) {
        Trainer existing = trainerDao.findByUsername(username);
        if (existing == null) {
            throw new IllegalArgumentException("Trainer not found: " + username);
        }

        validateName(firstName, "Trainer first name");
        validateName(lastName, "Trainer last name");
        if (specialization == null || specialization.getTrainingTypeName() == null || specialization.getTrainingTypeName().isBlank()) {
            throw new IllegalArgumentException("Trainer specialization is required");
        }
        if (!TrainingTypeCatalog.isAllowed(specialization.getTrainingTypeName())) {
            throw new IllegalArgumentException("Unsupported training type: " + specialization.getTrainingTypeName());
        }

        existing.setFirstName(firstName);
        existing.setLastName(lastName);
        existing.setSpecialization(specialization);
        return trainerDao.update(existing.getId(), existing);
    }

    public boolean authenticate(String username, String password) {
        Trainer trainer = trainerDao.findByUsername(username);
        return trainer != null && PasswordUtils.matches(password, trainer.getPassword());
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        Trainer trainer = trainerDao.findByUsername(username);
        if (trainer == null || !PasswordUtils.matches(oldPassword, trainer.getPassword())) {
            throw new SecurityException("Invalid username or password");
        }
        validatePassword(newPassword);
        trainer.setPassword(PasswordUtils.encode(newPassword));
        trainerDao.update(trainer.getId(), trainer);
    }

    @Transactional
    public void activate(String username) {
        Trainer trainer = trainerDao.findByUsername(username);
        if (trainer == null) {
            throw new IllegalArgumentException("Trainer not found");
        }
        if (trainer.isActive()) {
            throw new IllegalStateException("Trainer is already active");
        }
        trainer.setActive(true);
        trainerDao.update(trainer.getId(), trainer);
    }

    @Transactional
    public void deactivate(String username) {
        Trainer trainer = trainerDao.findByUsername(username);
        if (trainer == null) {
            throw new IllegalArgumentException("Trainer not found");
        }
        if (!trainer.isActive()) {
            throw new IllegalStateException("Trainer is already inactive");
        }
        trainer.setActive(false);
        trainerDao.update(trainer.getId(), trainer);
    }

    @Transactional
    public void delete(long id) {
        trainerDao.delete(id);
    }

    private void validateName(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 10) {
            throw new IllegalArgumentException("Password must contain at least 10 characters");
        }
    }
}