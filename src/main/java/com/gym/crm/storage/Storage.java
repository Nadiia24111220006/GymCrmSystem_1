package com.gym.crm.storage;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class Storage {
    private final Map<Long, Trainee> traineeMap = new HashMap<>();
    private final Map<Long, Trainer> trainerMap = new HashMap<>();
    private final Map<Long, Training> trainingMap = new HashMap<>();

    private final AtomicLong traineeIdCounter = new AtomicLong(1L);
    private final AtomicLong trainerIdCounter = new AtomicLong(1L);
    private final AtomicLong trainingIdCounter = new AtomicLong(1L);

    public Map<Long, Trainee> getTraineeMap() {
        return traineeMap;
    }

    public Map<Long, Trainer> getTrainerMap() {
        return trainerMap;
    }

    public Map<Long, Training> getTrainingMap() {
        return trainingMap;
    }

    public long generateTraineeId() {
        return traineeIdCounter.getAndIncrement();
    }

    public long generateTrainerId() {
        return trainerIdCounter.getAndIncrement();
    }

    public long generateTrainingId() {
        return trainingIdCounter.getAndIncrement();
    }
}