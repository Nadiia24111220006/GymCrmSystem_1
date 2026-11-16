package com.gym.crm.storage;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class StorageTest {

    @Test
    public void testStorageBeansCreation() {
        Storage storage = new Storage();

        Map<?, ?> traineeMap = storage.getTraineeMap();
        Map<?, ?> trainerMap = storage.getTrainerMap();
        Map<?, ?> trainingMap = storage.getTrainingMap();

        assertNotNull(traineeMap);
        assertNotNull(trainerMap);
        assertNotNull(trainingMap);
    }
}