package com.gym.crm.facade;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GymFacadeTest {

    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainerService trainerService;
    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private GymFacade gymFacade;

    @Test
    public void testAddTrainee() {
        Date dob = new Date();
        when(traineeService.createTrainee(anyString(), anyString(), any(Date.class), anyString()))
                .thenReturn(new Trainee("John", "Doe", "Kyiv", dob));

        Trainee trainee = gymFacade.addTrainee("John", "Doe", dob, "Kyiv");
        assertNotNull(trainee);
        assertEquals("John", trainee.getFirstName());
    }

    @Test
    public void testGetTraineeById() {
        when(traineeService.findTraineeById(1L)).thenReturn(new Trainee());
        assertNotNull(gymFacade.getTraineeById(1L));
    }

    @Test
    public void testGetAllTrainees() {
        when(traineeService.findAllTrainees()).thenReturn(Collections.emptyList());
        assertNotNull(gymFacade.getAllTrainees());
    }

    @Test
    public void testAddTrainer() {
        TrainingType type = new TrainingType("Fitness");
        when(trainerService.createTrainer(anyString(), anyString(), any(TrainingType.class)))
                .thenReturn(new Trainer("Bob", "Builder", type));

        Trainer trainer = gymFacade.addTrainer("Bob", "Builder", type);
        assertNotNull(trainer);
        assertEquals("Bob", trainer.getFirstName());
    }

    @Test
    public void testGetTrainerById() {
        when(trainerService.findTrainerById(1L)).thenReturn(new Trainer());
        assertNotNull(gymFacade.getTrainerById(1L));
    }

    @Test
    public void testGetAllTrainers() {
        when(trainerService.findAllTrainers()).thenReturn(Collections.emptyList());
        assertNotNull(gymFacade.getAllTrainers());
    }

    @Test
    public void testAddTraining() {
        Trainee trainee = new Trainee();
        Trainer trainer = new Trainer();
        TrainingType type = new TrainingType("Yoga");
        Date date = new Date();

        when(trainingService.createTraining(any(), any(), any(), anyString(), any(Date.class)))
                .thenReturn(new Training("Yoga Class", type, trainer, trainee, date));

        Training training = gymFacade.addTraining(trainee, type, trainer, "Yoga Class", date);
        assertNotNull(training);
        assertEquals("Yoga Class", training.getTrainingName());
    }

    @Test
    public void testGetAllTrainings() {
        when(trainingService.findAllTrainings()).thenReturn(Collections.emptyList());
        assertNotNull(gymFacade.getAllTrainings());
    }
}