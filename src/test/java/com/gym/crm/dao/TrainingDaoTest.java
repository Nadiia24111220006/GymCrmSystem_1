package com.gym.crm.dao;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import com.gym.crm.storage.Storage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class TrainingDaoTest {
    private TrainingDao trainingDao;

    @BeforeEach
    public void setUp() {
        Storage storage = new Storage();
        trainingDao = new TrainingDao();
        trainingDao.setStorage(storage);
    }

    @Test
    public void testSaveAndFind() {
        Trainee trainee = new Trainee("Alice", "Smith", "Kyiv", new Date());
        Trainer trainer = new Trainer("Bob", "Builder", new TrainingType("Yoga"));
        Training training = new Training("Yoga Class", new TrainingType("Yoga"), trainer, trainee, new Date());

        trainingDao.save(training);
        assertNotNull(trainingDao.findById(1L));
    }

    @Test
    public void testUpdateDeleteAndFindAll() {
        Trainee trainee = new Trainee("Alice", "Smith", "Kyiv", new Date());
        Trainer trainer = new Trainer("Bob", "Builder", new TrainingType("Yoga"));
        Training training = new Training("Yoga Class", new TrainingType("Yoga"), trainer, trainee, new Date());

        trainingDao.save(training);
        assertFalse(trainingDao.findAll().isEmpty());

        training.setTrainingName("Advanced Yoga");
        trainingDao.update(1L, training);
        assertEquals("Advanced Yoga", trainingDao.findById(1L).getTrainingName());

        trainingDao.delete(1L);
        assertNull(trainingDao.findById(1L));
    }
}