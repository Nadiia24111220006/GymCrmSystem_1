package com.gym.crm.dao;

import com.gym.crm.model.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class TraineeDao {
    private Map<Long, Trainee> traineeMap;
    private AtomicLong traineeIdCounter;

    @Autowired
    public void setStorage(@Qualifier("traineeStorage") Map<Long, Trainee> traineeMap,
                           @Qualifier("traineeIdCounter") AtomicLong traineeIdCounter) {
        this.traineeMap = traineeMap;
        this.traineeIdCounter = traineeIdCounter;
    }

    public Trainee save(Trainee trainee) {
        long id = traineeIdCounter.getAndIncrement();
        trainee.setId(id);
        traineeMap.put(id, trainee);
        return trainee;
    }

    public Trainee findById(long id) {
        return traineeMap.get(id);
    }

    public Trainee update(long id, Trainee updatedTrainee) {
        if (traineeMap.containsKey(id)) {
            updatedTrainee.setId(id);
            traineeMap.put(id, updatedTrainee);
            return updatedTrainee;
        }
        return null;
    }

    public void delete(long id) {
        traineeMap.remove(id);
    }

    public Collection<Trainee> findAll() {
        return traineeMap.values();
    }
}