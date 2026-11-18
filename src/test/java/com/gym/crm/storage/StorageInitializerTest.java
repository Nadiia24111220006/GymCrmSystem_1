package com.gym.crm.storage;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class StorageInitializerTest {

    @Test
    public void testPostProcessAfterInitialization() {
        StorageInitializer initializer = new StorageInitializer();

        String dummyBean = "SomeBean";
        Object result = initializer.postProcessAfterInitialization(dummyBean, "dummy");
        assertEquals(dummyBean, result);

        Map<Long, Trainee> traineeMap = new HashMap<>();
        Object traineeResult = initializer.postProcessAfterInitialization(traineeMap, "traineeStorage");
        assertNotNull(traineeResult);

        Map<Long, Trainer> trainerMap = new HashMap<>();
        Object trainerResult = initializer.postProcessAfterInitialization(trainerMap, "trainerStorage");
        assertNotNull(trainerResult);

        Map<Object, Object> trainingMap = new HashMap<>();
        Object trainingResult = initializer.postProcessAfterInitialization(trainingMap, "trainingStorage");
        assertNotNull(trainingResult);

    }
}