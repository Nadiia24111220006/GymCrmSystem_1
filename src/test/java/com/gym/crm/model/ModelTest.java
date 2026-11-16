package com.gym.crm.model;

import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

public class ModelTest {

    @Test
    public void testModels() {
        User user = new User();
        user.setActive(true);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setUsername("Test.User");
        user.setPassword("12345");
        assertTrue(user.isActive());
        assertEquals("Test", user.getFirstName());
        assertEquals("User", user.getLastName());
        assertEquals("Test.User", user.getUsername());
        assertEquals("12345", user.getPassword());

        TrainingType type = new TrainingType();
        type.setTrainingTypeName("Gym");
        assertEquals("Gym", type.getTrainingTypeName());

        Trainee trainee = new Trainee();
        trainee.setAdress("Street");
        trainee.setDateofBirth(new Date());
        assertNotNull(trainee.getAdress());
        assertNotNull(trainee.getDateofBirth());

        Trainer trainer = new Trainer ();
        type = new TrainingType("Fitness");
        trainer.setSpecialization(type);
        assertEquals(type, trainer.getSpecialization());

        Training training = new Training();
        training.setTrainingDate(new Date());
        assertNotNull(training.getTrainingDate());
    }
}