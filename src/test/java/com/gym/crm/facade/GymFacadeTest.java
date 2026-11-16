package com.gym.crm.facade;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
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
        verify(traineeService, times(1)).createTrainee(anyString(), anyString(), any(Date.class), anyString());
    }

    @Test
    public void testFacadeOtherMethods() {
        gymFacade.getAllTrainees();
        gymFacade.getAllTrainers();
        gymFacade.getAllTrainings();
        gymFacade.getTraineeById(1L);
        gymFacade.getTrainerById(1L);
    }

    @Test
    public void testGymFacadeGettersAndAllMethods() {
        when(traineeService.findAllTrainees()).thenReturn(Collections.emptyList());
        when(trainerService.findAllTrainers()).thenReturn(Collections.emptyList());
        when(trainingService.findAllTrainings()).thenReturn(Collections.emptyList());
        when(traineeService.findTraineeById(1L)).thenReturn(new Trainee());
        when(trainerService.findTrainerById(1L)).thenReturn(new Trainer());

        assertNotNull(gymFacade.getAllTrainees());
        assertNotNull(gymFacade.getAllTrainers());
        assertNotNull(gymFacade.getAllTrainings());
        assertNotNull(gymFacade.getTraineeById(1L));
        assertNotNull(gymFacade.getTrainerById(1L));
    }
}