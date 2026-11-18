package com.gym.crm.model;

import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

public class ModelTest {

    @Test
    public void testUserAndEntityModels() {

        User user = new User();
        user.setId(10L);
        user.setActive(true);
        user.setFirstName("Test");
        user.setLastName("User");
        user.setUsername("Test.User");
        user.setPassword("12345");

        assertEquals(10L, user.getId());
        assertTrue(user.isActive());
        assertEquals("Test", user.getFirstName());
        assertEquals("User", user.getLastName());
        assertEquals("Test.User", user.getUsername());
        assertEquals("12345", user.getPassword());

        User userParam = new User("John", "Doe");
        assertEquals("John", userParam.getFirstName());
        assertEquals("Doe", userParam.getLastName());
        assertTrue(userParam.isActive());

        TrainingType type = new TrainingType();
        type.setTrainingTypeName("Gym");
        assertEquals("Gym", type.getTrainingTypeName());

        TrainingType typeParam = new TrainingType("Fitness");
        assertEquals("Fitness", typeParam.getTrainingTypeName());

        Trainee trainee = new Trainee();
        trainee.setAdress("Street");
        trainee.setDateofBirth(new Date());
        assertNotNull(trainee.getAdress());
        assertNotNull(trainee.getDateofBirth());

        Trainee traineeParam = new Trainee("Alice", "Smith", "Kyiv", new Date());
        assertEquals("Alice", traineeParam.getFirstName());
        assertEquals("Smith", traineeParam.getLastName());
        assertEquals("Kyiv", traineeParam.getAdress());
        assertNotNull(traineeParam.getDateofBirth());

        Trainer trainer = new Trainer();
        trainer.setSpecialization(type);
        assertEquals(type, trainer.getSpecialization());

        Trainer trainerParam = new Trainer("Bob", "Builder", typeParam);
        assertEquals("Bob", trainerParam.getFirstName());
        assertEquals("Builder", trainerParam.getLastName());
        assertEquals(typeParam, trainerParam.getSpecialization());

        Training training = new Training();
        training.setTrainingName("Yoga Class");
        training.setTrainingType(type);
        training.setTrainer(trainer);
        training.setTrainee(trainee);
        training.setTrainingDate(new Date());

        assertEquals("Yoga Class", training.getTrainingName());
        assertEquals(type, training.getTrainingType());
        assertEquals(trainer, training.getTrainer());
        assertEquals(trainee, training.getTrainee());
        assertNotNull(training.getTrainingDate());

        Training trainingParam = new Training("Crossfit", type, trainer, trainee, new Date());
        assertEquals("Crossfit", trainingParam.getTrainingName());
        assertEquals(type, trainingParam.getTrainingType());
        assertEquals(trainer, trainingParam.getTrainer());
        assertEquals(trainee, trainingParam.getTrainee());
        assertNotNull(trainingParam.getTrainingDate());
    }
}