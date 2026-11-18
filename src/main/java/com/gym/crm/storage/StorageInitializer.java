package com.gym.crm.storage;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Configuration
public class StorageInitializer{
    private static final Logger log = LoggerFactory.getLogger(StorageInitializer.class);

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public boolean initializeStorage(
            @Value("${storage.init.file}") String initFileName,
            @Qualifier("traineeStorage") Map<Long, Trainee> traineeStorage,
            @Qualifier("trainerStorage") Map<Long, Trainer> trainerStorage,
            @Qualifier("trainingStorage") Map<Long, Training> trainingStorage,
            @Qualifier("traineeIdCounter") AtomicLong traineeIdCounter,
            @Qualifier("trainerIdCounter") AtomicLong trainerIdCounter,
            @Qualifier("trainingIdCounter") AtomicLong trainingIdCounter) {

            log.info("Data storage initialization has begun using file: {}", initFileName);
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(initFileName)) {
                if (inputStream != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    StorageDataDto data = mapper.readValue(inputStream, StorageDataDto.class);

                    if (data.getTrainees() != null) {
                        for (Trainee trainee : data.getTrainees()) {
                            trainee.setId(traineeIdCounter.getAndIncrement());
                            traineeStorage.put(trainee.getId(), trainee);
                        }
                    }
                    if (data.getTrainers() != null) {
                        for (Trainer trainer : data.getTrainers()) {
                            trainer.setId(trainerIdCounter.getAndIncrement());
                            trainerStorage.put(trainer.getId(), trainer);
                        }
                    }

                    if (data.getTrainings() != null) {
                        for (Training training : data.getTrainings()) {
                            long id = trainingIdCounter.getAndIncrement();
                            training.setId(id);
                            trainingStorage.put(id, training);
                        }
                    }
                    log.info("Initialization file successfully loaded: {}", initFileName);
                } else {
                    log.warn("Warning: Initialization file '{}' not found in classpath.", initFileName);
                }
            } catch (Exception e) {
                log.error("Error reading initialization file: {}", e.getMessage(), e);
            }
        return true;
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