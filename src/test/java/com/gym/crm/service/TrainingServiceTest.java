package com.gym.crm.service;

import com.gym.crm.dao.TrainingDao;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainingServiceTest {

    @Mock
    private TrainingDao trainingDao;

    @InjectMocks
    private TrainingService trainingService;

    @Test
    public void testCreateTraining() {
        Trainee trainee = new Trainee("Alice", "Smith", "Address", new Date());
        Trainer trainer = new Trainer("Bob", "Builder", new TrainingType("Yoga"));
        TrainingType trainingType = new TrainingType("Yoga");
        String trainingName = "Morning Yoga";
        Date date = new Date();

        when(trainingDao.save(any(Training.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Training training = trainingService.createTraining(trainee, trainingType, trainer, trainingName, date);

        assertNotNull(training);
        assertEquals("Morning Yoga", training.getTrainingName());
        assertEquals(trainee, training.getTrainee());
        assertEquals(trainer, training.getTrainer());

        verify(trainingDao, times(1)).save(any(Training.class));
    }

    @Test
    public void testTrainingServiceMethods() {
        Training training = new Training();
        when(trainingDao.findById(1L)).thenReturn(training);

        assertNotNull(trainingService.findTrainingById(1L));
        trainingService.findAllTrainings();

        verify(trainingDao, times(1)).findById(1L);
        verify(trainingDao, times(1)).findAll();
    }
}