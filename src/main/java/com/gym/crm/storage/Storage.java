package com.gym.crm.storage;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Configuration
public class Storage {

    @Bean(name = "traineeStorage")
    public Map<Long, Trainee> traineeMap() {
        return new HashMap<>();
    }

    @Bean(name = "trainerStorage")
    public Map<Long, Trainer> trainerMap() {
        return new HashMap<>();
    }

    @Bean(name = "trainingStorage")
    public Map<Long, Training> trainingMap() {
        return new HashMap<>();
    }

    @Bean
    public AtomicLong traineeIdCounter() {
        return new AtomicLong(1L);
    }

    @Bean
    public AtomicLong trainerIdCounter() {
        return new AtomicLong(1L);
    }

    @Bean
    public AtomicLong trainingIdCounter() {
        return new AtomicLong(1L);
    }
}