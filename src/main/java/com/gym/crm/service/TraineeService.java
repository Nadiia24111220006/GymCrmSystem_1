package com.gym.crm.service;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class TraineeService {
    private static final Logger log = LoggerFactory.getLogger(TraineeService.class);
    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;

    public TraineeService(TraineeDao traineeDao, TrainerDao trainerDao) {
        this.traineeDao = traineeDao;
        this.trainerDao = trainerDao;
    }

    @Transactional
    public Trainee createTrainee(String firstName, String lastName, java.util.Date dateOfBirth, String address) {
        validateName(firstName, "Trainee first name");
        validateName(lastName, "Trainee last name");
        log.info("Attempting to create new trainee with name: {} {}", firstName, lastName);
        String username = generateUsername(firstName, lastName);
        String password = generatePassword();

        Trainee trainee = new Trainee(firstName, lastName, address, dateOfBirth);
        trainee.setUsername(username);
        trainee.setPassword(PasswordUtils.encode(password));
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

    public Trainee findByUsername(String username) {
        return traineeDao.findByUsername(username);
    }

    public Collection<Trainee> findAllTrainees() {
        log.info("Fetching all trainees");
        return traineeDao.findAll();
    }

    @Transactional
    public Trainee update(long id, Trainee trainee) {
        validateName(trainee.getFirstName(), "Trainee first name");
        validateName(trainee.getLastName(), "Trainee last name");
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

    @Transactional
    public Trainee updateByUsername(String username, String firstName, String lastName, Date dateOfBirth, String address) {
        Trainee existing = traineeDao.findByUsername(username);
        if (existing == null) {
            return null;
        }

        validateName(firstName, "Trainee first name");
        validateName(lastName, "Trainee last name");

        existing.setFirstName(firstName);
        existing.setLastName(lastName);
        existing.setDateOfBirth(dateOfBirth);
        existing.setAddress(address);
        return traineeDao.update(existing.getId(), existing);
    }

    public boolean authenticate(String username, String password) {
        Trainee trainee = traineeDao.findByUsername(username);
        return trainee != null && PasswordUtils.matches(password, trainee.getPassword());
    }

    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        Trainee trainee = traineeDao.findByUsername(username);
        if (trainee == null || !PasswordUtils.matches(oldPassword, trainee.getPassword())) {
            throw new SecurityException("Invalid username or password");
        }
        validatePassword(newPassword);
        trainee.setPassword(PasswordUtils.encode(newPassword));
        traineeDao.update(trainee.getId(), trainee);
    }

    @Transactional
    public void activate(String username) {
        Trainee trainee = traineeDao.findByUsername(username);
        if (trainee == null) {
            throw new IllegalArgumentException("Trainee not found");
        }
        if (trainee.isActive()) {
            throw new IllegalStateException("Trainee is already active");
        }
        trainee.setActive(true);
        traineeDao.update(trainee.getId(), trainee);
    }

    @Transactional
    public void deactivate(String username) {
        Trainee trainee = traineeDao.findByUsername(username);
        if (trainee == null) {
            throw new IllegalArgumentException("Trainee not found");
        }
        if (!trainee.isActive()) {
            throw new IllegalStateException("Trainee is already inactive");
        }
        trainee.setActive(false);
        traineeDao.update(trainee.getId(), trainee);
    }

    @Transactional
    public void updateTrainerAssignments(String traineeUsername, Set<String> trainerUsernames) {
        Trainee trainee = traineeDao.findByUsername(traineeUsername);
        if (trainee == null) {
            throw new IllegalArgumentException("Trainee not found");
        }

        Set<Trainer> normalizedTrainers = new HashSet<>();
        if (trainerUsernames != null) {
            for (String username : trainerUsernames) {
                if (username == null || username.isBlank()) {
                    continue;
                }
                Trainer trainer = trainerDao.findByUsername(username);
                if (trainer == null) {
                    throw new IllegalArgumentException("Trainer not found: " + username);
                }
                normalizedTrainers.add(trainer);
            }
        }

        trainee.setTrainers(normalizedTrainers);
        traineeDao.update(trainee.getId(), trainee);
    }

    @Transactional
    public void deleteByUsername(String username) {
        traineeDao.deleteByUsername(username);
    }

    @Transactional
    public void delete(long id) {
        traineeDao.delete(id);
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
