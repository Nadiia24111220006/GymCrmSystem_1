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
import java.util.List;

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

        Training training = trainingService.createTraining(trainee, trainingType, trainer, trainingName, date, 45);

        assertNotNull(training);
        assertEquals("Morning Yoga", training.getTrainingName());
        assertEquals(trainee, training.getTrainee());
        assertEquals(trainer, training.getTrainer());
        assertEquals(45, training.getTrainingDuration());

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

    @Test
    public void testFindTrainingsByCriteria() {
        Trainee trainee = new Trainee("Alice", "Smith", "Address", new Date());
        trainee.setUsername("Alice.Smith");
        Trainer trainer = new Trainer("Bob", "Builder", new TrainingType("Yoga"));
        trainer.setUsername("Bob.Builder");

        Training training = new Training("Morning Yoga", new TrainingType("Yoga"), trainer, trainee, new Date(), 30);
        when(trainingDao.findTraineeTrainingsByCriteria(
                eq("Alice.Smith"), isNull(), isNull(), eq("Bob Builder"), eq("Yoga")))
                .thenReturn(List.of(training));
        when(trainingDao.findTrainerTrainingsByCriteria(
                eq("Bob.Builder"), isNull(), isNull(), eq("Alice Smith")))
                .thenReturn(List.of(training));

        List<Training> traineeTrainings = trainingService.findTraineeTrainingsByCriteria(
                "Alice.Smith", null, null, "Bob Builder", "Yoga"
        );
        List<Training> trainerTrainings = trainingService.findTrainerTrainingsByCriteria(
                "Bob.Builder", null, null, "Alice Smith"
        );

        assertEquals(1, traineeTrainings.size());
        assertEquals(1, trainerTrainings.size());
    }

    @Test
    public void testAdditionalTrainingBranches() {
        assertThrows(IllegalArgumentException.class,
                () -> trainingService.createTraining(null, new TrainingType("Yoga"), new Trainer(), "Name", new Date(), 30));
        assertThrows(IllegalArgumentException.class,
                () -> trainingService.createTraining(new Trainee(), new TrainingType("Unsupported"), new Trainer(), "Name", new Date(), 30));
        assertThrows(IllegalArgumentException.class,
                () -> trainingService.createTraining(new Trainee(), new TrainingType("Yoga"), new Trainer(), "Name", new Date(), 0));

        Date later = new Date();
        Date earlier = new Date(later.getTime() - 86_400_000L);
        assertThrows(IllegalArgumentException.class,
                () -> trainingService.findTraineeTrainingsByCriteria("Alice.Smith", later, earlier, null, null));
        assertThrows(IllegalArgumentException.class,
                () -> trainingService.findTraineeTrainingsByCriteria("", earlier, later, null, null));
        assertThrows(IllegalArgumentException.class,
                () -> trainingService.findTrainerTrainingsByCriteria("", earlier, later, null));

        Trainee trainee = new Trainee("Alice", "Smith", "Address", new Date());
        trainee.setUsername("Alice.Smith");
        Trainer trainer = new Trainer("Bob", "Builder", new TrainingType("Yoga"));
        trainer.setUsername("Bob.Builder");
        when(trainingDao.save(any(Training.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Training training = trainingService.createTraining(
                trainee, new TrainingType("Yoga"), trainer, "Default Duration", new Date());
        assertEquals(60, training.getTrainingDuration());

        trainingService.deleteByTraineeUsername("Alice.Smith");
        verify(trainingDao).deleteByTraineeUsername("Alice.Smith");

    }

    @Test
    public void testCriteriaValidationAndDeleteBranches() {
        assertThrows(IllegalArgumentException.class,
                () -> trainingService.findTraineeTrainingsByCriteria("", null, null, null, null));
        assertThrows(IllegalArgumentException.class,
                () -> trainingService.findTrainerTrainingsByCriteria("", null, null, null));

        Date later = new Date();
        Date earlier = new Date(later.getTime() - 1000L);
        assertThrows(IllegalArgumentException.class,
                () -> trainingService.findTraineeTrainingsByCriteria("Alice", later, earlier, null, null));

        trainingService.deleteByTraineeUsername("Alice.Smith");
        verify(trainingDao).deleteByTraineeUsername("Alice.Smith");
    }

}