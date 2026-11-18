package com.gym.crm.dao;

import com.gym.crm.model.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class TrainerDao {
    private Map<Long, Trainer> trainerMap;
    private AtomicLong trainerIdCounter;

    @Autowired
    public void setStorage(@Qualifier("trainerStorage") Map<Long, Trainer> trainerMap,
                           @Qualifier("trainerIdCounter") AtomicLong trainerIdCounter) {
        this.trainerMap = trainerMap;
        this.trainerIdCounter = trainerIdCounter;
    }

    public Trainer save(Trainer trainer) {
        long id = trainerIdCounter.getAndIncrement();
        trainer.setId(id);
        trainerMap.put(id, trainer);
        return trainer;
    }

    public Trainer findById(long id) {
        return trainerMap.get(id);
    }

    public Trainer update(long id, Trainer updatedTrainer) {
        if (trainerMap.containsKey(id)) {
            updatedTrainer.setId(id);
            trainerMap.put(id, updatedTrainer);
            return updatedTrainer;
        }
        return null;
    }

    public void delete(long id) {
        trainerMap.remove(id);
    }

    public Collection<Trainer> findAll() {
        return trainerMap.values();
    }
}