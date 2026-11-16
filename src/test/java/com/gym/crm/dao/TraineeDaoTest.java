package com.gym.crm.dao;

import com.gym.crm.model.Trainee;
import com.gym.crm.storage.Storage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class TraineeDaoTest {

    private TraineeDao traineeDao;

    @BeforeEach
    public void setUp() {
        Storage storage = new Storage();
        traineeDao = new TraineeDao();
        traineeDao.setStorage(storage);
    }

    @Test
    public void testSaveAndFindById() {
        Trainee trainee = new Trainee("Alice", "Smith", "Lviv", new Date());
        traineeDao.save(trainee);

        Trainee found = traineeDao.findById(1L);
        assertNotNull(found);
        assertEquals("Alice", found.getFirstName());
    }

    @Test
    public void testUpdateDeleteAndFindAll() {
        Trainee trainee = new Trainee("Alice", "Smith", "Lviv", new Date());
        traineeDao.save(trainee);

        assertFalse(traineeDao.findAll().isEmpty());
        trainee.setAdress("Kyiv");
        traineeDao.update(1L, trainee);
        assertEquals("Kyiv", traineeDao.findById(1L).getAdress());

        traineeDao.delete(1L);
        assertNull(traineeDao.findById(1L));
    }
}