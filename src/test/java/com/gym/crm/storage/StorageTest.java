package com.gym.crm.storage;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class StorageTest {

    @Test
    public void testStorageBeansCreation() {
        Storage storage = new Storage();

        Map<?, ?> traineeMap = storage.traineeMap();
        Map<?, ?> trainerMap = storage.traineeMap();
        Map<?, ?> trainingMap = storage.traineeMap();

        assertNotNull(traineeMap);
        assertNotNull(trainerMap);
        assertNotNull(trainingMap);
        assertNotNull(storage.traineeIdCounter());
        assertNotNull(storage.trainerIdCounter());
        assertNotNull(storage.trainingIdCounter());
        assertEquals(1L, storage.traineeIdCounter().getAndIncrement());
        assertEquals(1L, storage.trainerIdCounter().getAndIncrement());
        assertEquals(1L, storage.trainingIdCounter().getAndIncrement());
    }
}