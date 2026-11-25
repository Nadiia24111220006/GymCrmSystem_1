package com.gym.crm.service;

import com.gym.crm.dao.TrainerDao;
import com.gym.crm.dao.TraineeDao;
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

    @Mock
    private TraineeDao traineeDao;

    @InjectMocks
    private TrainerService trainerService;

    @Test
    public void testCreateTrainer() {
        String firstName = "John";
        String lastName = "Doe";
        TrainingType specialization = new TrainingType("Fitness");

        when(trainerDao.findAll()).thenReturn(Collections.emptyList());
        when(traineeDao.findAll()).thenReturn(Collections.emptyList());
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

    @Test
    public void testAuthenticateAndChangePasswordAndActivation() {
        Trainer trainer = new Trainer("John", "Doe", new TrainingType("Fitness"));
        trainer.setId(1L);
        trainer.setUsername("John.Doe");
        trainer.setPassword("OldPass1234");
        trainer.setActive(true);

        when(trainerDao.findByUsername("John.Doe")).thenReturn(trainer);
        when(trainerDao.update(eq(1L), any(Trainer.class))).thenReturn(trainer);

        assertTrue(trainerService.authenticate("John.Doe", "OldPass1234"));
        trainerService.changePassword("John.Doe", "OldPass1234", "NewPass1234");
        assertTrue(PasswordUtils.matches("NewPass1234", trainer.getPassword()));
        assertFalse(PasswordUtils.matches("OldPass1234", trainer.getPassword()));

        trainerService.deactivate("John.Doe");
        assertFalse(trainer.isActive());
        trainerService.activate("John.Doe");
        assertTrue(trainer.isActive());
    }

    @Test
    public void testValidationAndNegativeBranches() {
        assertThrows(IllegalArgumentException.class,
                () -> trainerService.createTrainer("", "Doe", new TrainingType("Fitness")));
        assertThrows(IllegalArgumentException.class,
                () -> trainerService.createTrainer("John", "Doe", null));
        assertThrows(IllegalArgumentException.class,
                () -> trainerService.createTrainer("John", "Doe", new TrainingType("Unsupported")));

        Trainer trainer = new Trainer("John", "Doe", new TrainingType("Fitness"));
        trainer.setId(1L);
        trainer.setUsername("John.Doe");
        trainer.setPassword("OldPass1234");
        trainer.setActive(false);

        when(trainerDao.findByUsername("John.Doe")).thenReturn(trainer);
        assertFalse(trainerService.authenticate("John.Doe", "wrong-password"));
        assertThrows(SecurityException.class,
                () -> trainerService.changePassword("John.Doe", "wrong-password", "NewPass1234"));
        assertThrows(IllegalArgumentException.class,
                () -> trainerService.changePassword("John.Doe", "OldPass1234", "short"));

        when(trainerDao.update(eq(1L), any(Trainer.class))).thenReturn(trainer);
        trainerService.activate("John.Doe");
        assertThrows(IllegalStateException.class, () -> trainerService.activate("John.Doe"));
        trainerService.deactivate("John.Doe");
        assertThrows(IllegalStateException.class, () -> trainerService.deactivate("John.Doe"));

        assertThrows(IllegalArgumentException.class,
                () -> trainerService.updateByUsername("missing", "John", "Doe", new TrainingType("Fitness")));
    }
}