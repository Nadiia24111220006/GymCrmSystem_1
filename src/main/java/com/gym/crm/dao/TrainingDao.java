package com.gym.crm.dao;

import com.gym.crm.model.Training;
import com.gym.crm.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class TrainingDao {
    private Storage storage;

    @Autowired
    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public Training save(Training training) {
        long id = storage.generateTrainingId();
        storage.getTrainingMap().put(id, training);
        return training;
    }

    public Training findById(long id) {
        return storage.getTrainingMap().get(id);
    }

    public Training update(long id, Training updatedTraining) {
        if (storage.getTrainingMap().containsKey(id)) {
            storage.getTrainingMap().put(id, updatedTraining);
            return updatedTraining;
        }
        return null;
    }

    public void delete(long id) {
        storage.getTrainingMap().remove(id);
    }

    public Collection<Training> findAll() {
        return storage.getTrainingMap().values();
    }
}