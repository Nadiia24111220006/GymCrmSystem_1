package com.gym.crm.storage;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StorageInitializerTest {

    @Test
    public void testInitializeStorageWithMissingFile() {
        StorageInitializer initializer = new StorageInitializer();

        Map<Long, Trainee> traineeMap = new HashMap<>();
        Map<Long, Trainer> trainerMap = new HashMap<>();
        Map<Long, Training> trainingMap = new HashMap<>();

        boolean result = initializer.initializeStorage(
                "missing-data.json",
                traineeMap,
                trainerMap,
                trainingMap,
                new AtomicLong(1L),
                new AtomicLong(1L),
                new AtomicLong(1L));

        assertTrue(result);
    }

    @Test
    public void testInitializeStorageWithDataFile() {
        StorageInitializer initializer = new StorageInitializer();

        Map<Long, Trainee> traineeMap = new HashMap<>();
        Map<Long, Trainer> trainerMap = new HashMap<>();
        Map<Long, Training> trainingMap = new HashMap<>();
        AtomicLong traineeCounter = new AtomicLong(1L);
        AtomicLong trainerCounter = new AtomicLong(1L);
        AtomicLong trainingCounter = new AtomicLong(1L);

        boolean result = initializer.initializeStorage(
                "data.json",
                traineeMap,
                trainerMap,
                trainingMap,
                traineeCounter,
                trainerCounter,
                trainingCounter);

        assertTrue(result);
        assertEquals(1, traineeMap.size());
        assertEquals(1, trainerMap.size());
        assertEquals(1L, traineeMap.keySet().iterator().next());
        assertEquals(1L, trainerMap.keySet().iterator().next());
        assertEquals(2L, traineeCounter.get());
        assertEquals(2L, trainerCounter.get());
        assertEquals(1L, trainingCounter.get());
    }
}

