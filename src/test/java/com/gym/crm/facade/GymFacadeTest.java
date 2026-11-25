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
import java.util.List;
import java.util.Set;

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

    @Test
    public void testAuthenticatedUsernameOperations() {
        Trainee authTrainee = new Trainee("Alice", "Smith", "Kyiv", new Date());
        authTrainee.setUsername("Alice.Smith");
        authTrainee.setPassword("SecurePass1");
        authTrainee.setTrainers(new java.util.HashSet<>());

        Trainer trainer = new Trainer("Bob", "Builder", new TrainingType("Yoga"));
        trainer.setUsername("Bob.Builder");

        when(traineeService.authenticate("Alice.Smith", "SecurePass1")).thenReturn(true);
        when(traineeService.findByUsername("Alice.Smith")).thenReturn(authTrainee);
        when(trainerService.findByUsername("Bob.Builder")).thenReturn(trainer);
        when(trainingService.createTraining(any(), any(), any(), anyString(), any(Date.class), anyInt()))
                .thenReturn(new Training("Yoga Class", new TrainingType("Yoga"), trainer, authTrainee, new Date(), 30));
        when(trainerService.findAllTrainers()).thenReturn(List.of(trainer));

        assertNotNull(gymFacade.selectTraineeByUsername("Alice.Smith", "SecurePass1", "Alice.Smith"));
        assertNotNull(gymFacade.addTraining(
                "Alice.Smith", "SecurePass1", "Alice.Smith", "Bob.Builder", "Yoga Class", "Yoga", new Date(), 30
        ));
        assertEquals(1, gymFacade.getUnassignedTrainersForTrainee("Alice.Smith", "SecurePass1", "Alice.Smith").size());

        gymFacade.updateTraineeTrainersList("Alice.Smith", "SecurePass1", "Alice.Smith", Set.of("Bob.Builder"));
        authTrainee.setTrainers(Set.of(trainer));
        assertEquals(0, gymFacade.getUnassignedTrainersForTrainee("Alice.Smith", "SecurePass1", "Alice.Smith").size());
        verify(traineeService, times(2)).updateTrainerAssignments(eq("Alice.Smith"), anySet());

    }

    @Test
    public void testDeleteTraineeProfileCascade() {
        when(traineeService.authenticate("Alice.Smith", "adminPass")).thenReturn(true);

        gymFacade.deleteTraineeProfile("Alice.Smith", "adminPass", "Alice.Smith");

        verify(trainingService, times(1)).deleteByTraineeUsername("Alice.Smith");
        verify(traineeService, times(1)).deleteByUsername("Alice.Smith");
    }

    @Test
    public void testAdditionalAuthenticatedOperations() {
        Trainee trainee = new Trainee("Alice", "Smith", "Kyiv", new Date());
        trainee.setUsername("Alice.Smith");
        trainee.setPassword("password123");
        Trainer trainer = new Trainer("Bob", "Builder", new TrainingType("Yoga"));
        trainer.setUsername("Bob.Builder");

        when(traineeService.authenticate("Alice.Smith", "password123")).thenReturn(true);
        when(traineeService.findByUsername("Alice.Smith")).thenReturn(trainee);
        when(trainerService.findByUsername("Bob.Builder")).thenReturn(trainer);
        when(trainerService.authenticate("Bob.Builder", "password123")).thenReturn(true);
        when(trainerService.updateByUsername(anyString(), anyString(), anyString(), any())).thenReturn(trainer);
        when(traineeService.updateByUsername(anyString(), anyString(), anyString(), any(), any())).thenReturn(trainee);
        when(trainingService.findTraineeTrainingsByCriteria(anyString(), any(), any(), any(), any())).thenReturn(Collections.emptyList());
        when(trainingService.findTrainerTrainingsByCriteria(anyString(), any(), any(), any())).thenReturn(Collections.emptyList());

        assertEquals(trainer, gymFacade.selectTrainerByUsername("Bob.Builder", "password123", "Bob.Builder"));
        assertEquals(trainee, gymFacade.updateTraineeProfile("Alice.Smith", "password123", "Alice.Smith",
                "Alice", "Smith", new Date(), "Lviv"));
        assertEquals(trainer, gymFacade.updateTrainerProfile("Bob.Builder", "password123", "Bob.Builder",
                "Bob", "Builder", new TrainingType("Yoga")));

        gymFacade.changeTraineePassword("Alice.Smith", "password123", "Alice.Smith", "password123", "newPassword123");
        gymFacade.changeTrainerPassword("Bob.Builder", "password123", "Bob.Builder", "oldPassword", "newPassword123");
        gymFacade.activateTrainee("Alice.Smith", "password123", "Alice.Smith");
        gymFacade.deactivateTrainee("Alice.Smith", "password123", "Alice.Smith");
        gymFacade.activateTrainer("Bob.Builder", "password123", "Bob.Builder");
        gymFacade.deactivateTrainer("Bob.Builder", "password123", "Bob.Builder");

        assertNotNull(gymFacade.getTraineeTrainings("Alice.Smith", "password123", "Alice.Smith",
                null, null, null, null));
        assertNotNull(gymFacade.getTrainerTrainings("Bob.Builder", "password123", "Bob.Builder",
                null, null, null));

        verify(traineeService).changePassword("Alice.Smith", "password123", "newPassword123");
        verify(trainerService).changePassword("Bob.Builder", "oldPassword", "newPassword123");
    }

    @Test
    public void testAuthenticationFailure() {
        when(traineeService.authenticate("bad", "bad")).thenReturn(false);

        assertThrows(SecurityException.class,
                () -> gymFacade.selectTraineeByUsername("bad", "bad", "Alice.Smith"));
    }
}