package com.gym.crm.dao;

import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

public class TrainerDaoTest {
    private TrainerDao trainerDao;

    @BeforeEach
    public void setUp() {
        trainerDao = new TrainerDao();
        trainerDao.setStorage(new HashMap<>(), new AtomicLong(1L));
    }

    @Test
    public void testSaveAndFind() {
        Trainer trainer = new Trainer("John", "Doe", new TrainingType("Fitness"));
        trainerDao.save(trainer);
        Trainer found = trainerDao.findById(1L);
        assertNotNull(found);
        assertEquals(1L, found.getId());
    }

    @Test
    public void testTrainerDaoMethods() {
        Trainer trainer = new Trainer("John", "Doe", new TrainingType("Fitness"));
        trainerDao.save(trainer);

        assertFalse(trainerDao.findAll().isEmpty());
        trainerDao.update(1L, trainer);
        assertNotNull(trainerDao.findById(1L));
        trainerDao.delete(1L);
        assertNull(trainerDao.findById(1L));
    }
}