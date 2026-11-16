package com.gym.crm.service;

import com.gym.crm.dao.TrainerDao;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainerServiceTest {

    @Mock
    private TrainerDao trainerDao;

    @InjectMocks
    private TrainerService trainerService;

    @Test
    public void testCreateTrainer() {
        String firstName = "John";
        String lastName = "Doe";
        TrainingType specialization = new TrainingType("Fitness");

        when(trainerDao.findAll()).thenReturn(Collections.emptyList());
        when(trainerDao.save(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainer trainer = trainerService.createTrainer(firstName, lastName, specialization);

        assertNotNull(trainer);
        assertEquals("John.Doe", trainer.getUsername());
        assertNotNull(trainer.getPassword());
        assertEquals("Fitness", trainer.getSpecialization().getTrainingTypeName());
        assertTrue(trainer.isActive());

        verify(trainerDao, times(1)).save(any(Trainer.class));
    }

    @Test
    public void testTrainerServiceMethods() {
        Trainer trainer = new Trainer("Jane", "Doe", new TrainingType("Yoga"));
        when(trainerDao.findById(1L)).thenReturn(trainer);

        assertNotNull(trainerService.findTrainerById(1L));
        trainerService.findAllTrainers();

        verify(trainerDao, times(1)).findById(1L);
        verify(trainerDao, times(1)).findAll();
    }

    @Test
    public void testFindAndUpdateAndDelete() {
        Trainer trainer = new Trainer("John", "Doe", new TrainingType("Fitness"));
        when(trainerDao.findById(1L)).thenReturn(trainer);

        assertNotNull(trainerService.findTrainerById(1L));

        trainerService.update(1L, trainer);
        trainerService.delete(1L);
    }

    @Test
    public void testUpdateAndDeleteTrainer() {
        Trainer trainer = new Trainer("John", "Doe", new TrainingType("Fitness"));
        when(trainerDao.update(eq(1L), any(Trainer.class))).thenReturn(trainer);

        assertNotNull(trainerService.update(1L, trainer));
        trainerService.delete(1L);

        verify(trainerDao, times(1)).update(eq(1L), any(Trainer.class));
        verify(trainerDao, times(1)).delete(1L);
    }
}