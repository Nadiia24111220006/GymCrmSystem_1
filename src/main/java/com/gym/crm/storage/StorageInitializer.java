package com.gym.crm.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.TrainingTypeCatalog;
import com.gym.crm.service.PasswordUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.io.InputStream;
import java.util.List;

@Configuration
public class StorageInitializer {
    private static final Logger log = LoggerFactory.getLogger(StorageInitializer.class);

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public SmartInitializingSingleton initializeJpaStorage(
            @Value("${storage.init.file}") String initFileName,
            EntityManagerFactory entityManagerFactory) {
        return () -> {
            log.info("JPA data initialization has begun using file: {}", initFileName);
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(initFileName)) {
                if (inputStream == null) {
                    log.warn("Initialization file '{}' not found in classpath.", initFileName);
                    return;
                }

                StorageDataDto data = new ObjectMapper().readValue(inputStream, StorageDataDto.class);
                EntityManager entityManager = entityManagerFactory.createEntityManager();
                entityManager.getTransaction().begin();

                try {
                    for (String typeName : TrainingTypeCatalog.getAllowedTypes()) {
                        persistTrainingType(entityManager, typeName);
                    }

                    if (data.getTrainees() != null) {
                        for (Trainee trainee : data.getTrainees()) {
                            if (findByUsername(entityManager, trainee.getUsername(), Trainee.class) == null) {
                                trainee.setPassword(hashIfNeeded(trainee.getPassword()));
                                entityManager.persist(trainee);
                            }
                        }
                    }

                    if (data.getTrainers() != null) {
                        for (Trainer trainer : data.getTrainers()) {
                            if (findByUsername(entityManager, trainer.getUsername(), Trainer.class) == null) {
                                if (trainer.getSpecialization() != null) {
                                    trainer.setSpecialization(findOrCreateTrainingType(
                                            entityManager, trainer.getSpecialization().getTrainingTypeName()));
                                }
                                trainer.setPassword(hashIfNeeded(trainer.getPassword()));
                                entityManager.persist(trainer);
                            }
                        }
                    }

                    if (data.getTrainings() != null) {
                        for (Training training : data.getTrainings()) {
                            training.setTrainingType(findOrCreateTrainingType(
                                    entityManager, training.getTrainingType().getTrainingTypeName()));
                            entityManager.persist(training);
                        }
                    }
                    entityManager.getTransaction().commit();
                    log.info("JPA initialization file successfully loaded: {}", initFileName);
                } catch (RuntimeException exception) {
                    if (entityManager.getTransaction().isActive()) {
                        entityManager.getTransaction().rollback();
                    }
                    throw exception;
                } finally {
                    entityManager.close();
                }
            } catch (Exception exception) {
                log.error("Error reading JPA initialization file: {}", exception.getMessage(), exception);
            }
        };
    }

    private void persistTrainingType(EntityManager entityManager, String typeName) {
        findOrCreateTrainingType(entityManager, typeName);
    }

    private TrainingType findOrCreateTrainingType(EntityManager entityManager, String typeName) {
        if (typeName == null || typeName.isBlank()) {
            throw new IllegalArgumentException("Training type is required");
        }
        if (!TrainingTypeCatalog.isAllowed(typeName)) {
            throw new IllegalArgumentException("Unsupported training type: " + typeName);
        }
        TrainingType existing = entityManager.createQuery(
                        "select t from TrainingType t where t.trainingTypeName = :name", TrainingType.class)
                .setParameter("name", typeName)
                .getResultStream()
                .findFirst()
                .orElse(null);
        if (existing != null) {
            return existing;
        }
        TrainingType created = new TrainingType(typeName);
        entityManager.persist(created);
        return created;
    }

    private <T> T findByUsername(EntityManager entityManager, String username, Class<T> type) {
        if (username == null || username.isBlank()) {
            return null;
        }
        return entityManager.createQuery("select e from " + type.getSimpleName()
                        + " e where e.username = :username", type)
                .setParameter("username", username)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    private String hashIfNeeded(String password) {
        return password != null && password.startsWith("pbkdf2$")
                ? password
                : PasswordUtils.encode(password);
    }

    private static class StorageDataDto {
        private List<Trainee> trainees;
        private List<Trainer> trainers;
        private List<Training> trainings;

        public List<Trainee> getTrainees() { return trainees; }
        public void setTrainees(List<Trainee> trainees) { this.trainees = trainees; }
        public List<Trainer> getTrainers() { return trainers; }
        public void setTrainers(List<Trainer> trainers) { this.trainers = trainers; }
        public List<Training> getTrainings() { return trainings; }
        public void setTrainings(List<Training> trainings) { this.trainings = trainings; }
    }

}