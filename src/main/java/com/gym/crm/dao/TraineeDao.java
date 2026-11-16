package com.gym.crm.dao;

import com.gym.crm.model.Trainee;
import com.gym.crm.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class TraineeDao {
    private Storage storage;

    @Autowired
    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    public Trainee save(Trainee trainee) {
        long id = storage.generateTraineeId();
        storage.getTraineeMap().put(id, trainee);
        return trainee;
    }

    public Trainee findById(long id) {
        return storage.getTraineeMap().get(id);
    }

    public Trainee update(long id, Trainee updatedTrainee) {
        if (storage.getTraineeMap().containsKey(id)) {
            storage.getTraineeMap().put(id, updatedTrainee);
            return updatedTrainee;
        }
        return null;
    }

    public void delete(long id) {
        storage.getTraineeMap().remove(id);
    }

    public Collection<Trainee> findAll() {
        return storage.getTraineeMap().values();
    }
}