package com.gym.crm.dao;

import com.gym.crm.model.Trainer;
import com.gym.crm.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class TrainerDao {
    private Storage storage;

    @Autowired
    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public Trainer save(Trainer trainer) {
        long id = storage.generateTrainerId();
        storage.getTrainerMap().put(id, trainer);
        return trainer;
    }

    public Trainer findById(long id) {
        return storage.getTrainerMap().get(id);
    }

    public Trainer update(long id, Trainer updatedTrainer) {
        if (storage.getTrainerMap().containsKey(id)) {
            storage.getTrainerMap().put(id, updatedTrainer);
            return updatedTrainer;
        }
        return null;
    }

    public void delete(long id) {
        storage.getTrainerMap().remove(id);
    }

    public Collection<Trainer> findAll() {
        return storage.getTrainerMap().values();
    }
}