package com.gym.crm.dao;

import com.gym.crm.model.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class TrainingDao {
    private Map<Long, Training> trainingMap;
    private AtomicLong trainingIdCounter;

    @Autowired
    public void setStorage(@Qualifier("trainingStorage") Map<Long, Training> trainingMap, AtomicLong trainingIdCounter) {
        this.trainingMap = trainingMap;
        this.trainingIdCounter = trainingIdCounter;
    }

    public Training save(Training training) {
        long id = trainingIdCounter.getAndIncrement();
        trainingMap.put(id, training);
        return training;
    }

    public Training findById(long id) {
        return trainingMap.get(id);
    }

    public Training update(long id, Training updatedTraining) {
        if (trainingMap.containsKey(id)) {
            trainingMap.put(id, updatedTraining);
            return updatedTraining;
        }
        return null;
    }

    public void delete(long id) {
        trainingMap.remove(id);
    }

    public Collection<Training> findAll() {
        return trainingMap.values();
    }
}