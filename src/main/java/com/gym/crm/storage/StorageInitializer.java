package com.gym.crm.storage;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Component
public class StorageInitializer implements BeanPostProcessor {
    private static final Logger log = LoggerFactory.getLogger(StorageInitializer.class);

    @Value("${storage.init.file}")
    private String initFileName;

    @Autowired
    @Qualifier("traineeStorage")
    private Map<Long, Trainee> traineeMap;

    @Autowired
    @Qualifier("trainerStorage")
    private Map<Long, Trainer> trainerMap;

    @Autowired
    @Qualifier("trainingStorage")
    private Map<Long, Training> trainingMap;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof Map && (beanName.equals("traineeStorage") || beanName.equals("trainerStorage") || beanName.equals("trainingStorage"))) {
            log.info("Data storage table '{}' initialization has begun using file via BeanPostProcessor: {}", beanName, initFileName);
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(initFileName)) {
                if (inputStream != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    StorageDataDto data = mapper.readValue(inputStream, StorageDataDto.class);

                    if (data.getTrainees() != null) {
                        long id = 1L;
                        for (Trainee trainee : data.getTrainees()) {
                            trainee.setId(id);
                            traineeMap.put(id++, trainee);
                        }
                    }
                    if (data.getTrainers() != null) {
                        long id = 1L;
                        for (Trainer trainer : data.getTrainers()) {
                            trainer.setId(id);
                            trainerMap.put(id++, trainer);
                        }
                    }
                    log.info("Initialization file successfully loaded: {}", initFileName);
                } else {
                    log.warn("Warning: Initialization file '{}' not found in classpath.", initFileName);
                }
            } catch (Exception e) {
                log.error("Error reading initialization file: {}", e.getMessage(), e);
            }
            log.info("Storage successfully initialized");
        }
        return bean;
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