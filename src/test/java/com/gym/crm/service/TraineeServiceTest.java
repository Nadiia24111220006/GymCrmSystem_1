package com.gym.crm.service;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TraineeServiceTest {

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @InjectMocks
    private TraineeService traineeService;

    @Test
    public void testCreateTrainee() {
        String firstName = "Alice";
        String lastName = "Brown";
        Date dob = new Date();
        String address = "Kyiv";

        when(traineeDao.findAll()).thenReturn(Collections.emptyList());
        when(trainerDao.findAll()).thenReturn(Collections.emptyList());
        when(traineeDao.save(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainee trainee = traineeService.createTrainee(firstName, lastName, dob, address);

        assertNotNull(trainee);
        assertEquals("Alice.Brown", trainee.getUsername());
        assertNotNull(trainee.getPassword());
        assertTrue(trainee.isActive());
        assertNotEquals("Alice.Brown", trainee.getPassword());

        verify(traineeDao, times(1)).save(any(Trainee.class));
    }
    @Test
    public void testFindAndUpdateAndDelete() {
        Trainee trainee = new Trainee("John", "Doe", "Kyiv", new Date());
        when(traineeDao.findById(1L)).thenReturn(trainee);

        assertNotNull(traineeService.findTraineeById(1L));

        traineeService.update(1L, trainee);
        traineeService.delete(1L);
    }

    @Test
    public void testFindAllTrainees() {
        when(traineeDao.findAll()).thenReturn(Collections.emptyList());
        assertNotNull(traineeService.findAllTrainees());
    }

    @Test
    public void testCreateTraineeWithDuplicateUsername() {
        Trainee existingTrainee = new Trainee("Alice", "Brown", "Kyiv", new Date());
        existingTrainee.setUsername("Alice.Brown");

        when(traineeDao.findAll()).thenReturn(java.util.Collections.singletonList(existingTrainee));
        when(trainerDao.findAll()).thenReturn(Collections.emptyList());
        when(traineeDao.save(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainee newTrainee = traineeService.createTrainee("Alice", "Brown", new Date(), "Lviv");

        assertNotNull(newTrainee);
        assertEquals("Alice.Brown1", newTrainee.getUsername());
        verify(traineeDao, times(1)).save(any(Trainee.class));
    }

    @Test
    public void testUpdateAndDeleteTrainee() {
        Trainee trainee = new Trainee("John", "Doe", "Kyiv", new Date());
        when(traineeDao.update(eq(1L), any(Trainee.class))).thenReturn(trainee);

        assertNotNull(traineeService.update(1L, trainee));
        traineeService.delete(1L);

        verify(traineeDao, times(1)).update(eq(1L), any(Trainee.class));
        verify(traineeDao, times(1)).delete(1L);
    }

    @Test
    public void testAuthenticateAndChangePassword() {
        Trainee trainee = new Trainee("Alice", "Brown", "Kyiv", new Date());
        trainee.setId(1L);
        trainee.setUsername("Alice.Brown");
        trainee.setPassword("OldPass1234");

        when(traineeDao.findByUsername("Alice.Brown")).thenReturn(trainee);
        when(traineeDao.update(eq(1L), any(Trainee.class))).thenReturn(trainee);

        assertTrue(traineeService.authenticate("Alice.Brown", "OldPass1234"));
        traineeService.changePassword("Alice.Brown", "OldPass1234", "NewPass1234");
        assertTrue(PasswordUtils.matches("NewPass1234", trainee.getPassword()));
        assertFalse(PasswordUtils.matches("OldPass1234", trainee.getPassword()));
    }

    @Test
    public void testActivateDeactivateAndUpdateTrainers() {
        Trainee trainee = new Trainee("Alice", "Brown", "Kyiv", new Date());
        trainee.setId(1L);
        trainee.setUsername("Alice.Brown");
        trainee.setActive(true);

        when(traineeDao.findByUsername("Alice.Brown")).thenReturn(trainee);
        when(traineeDao.update(eq(1L), any(Trainee.class))).thenReturn(trainee);
        Trainer bob = new com.gym.crm.model.Trainer("Bob", "Builder", null);
        bob.setUsername("Bob.Builder");
        when(trainerDao.findByUsername("Bob.Builder")).thenReturn(bob);

        traineeService.deactivate("Alice.Brown");
        assertFalse(trainee.isActive());
        traineeService.activate("Alice.Brown");
        assertTrue(trainee.isActive());

        traineeService.updateTrainerAssignments("Alice.Brown", new HashSet<>(Collections.singleton("Bob.Builder")));
        assertTrue(trainee.getTrainerUsernames().contains("Bob.Builder"));
    }

    @Test
    public void testValidationAndNegativeBranches() {
        assertThrows(IllegalArgumentException.class,
                () -> traineeService.createTrainee("", "Brown", new Date(), "Kyiv"));
        assertThrows(IllegalArgumentException.class,
                () -> traineeService.createTrainee("Alice", "", new Date(), "Kyiv"));

        Trainee trainee = new Trainee("Alice", "Brown", "Kyiv", new Date());
        trainee.setId(1L);
        trainee.setUsername("Alice.Brown");
        trainee.setPassword("OldPass1234");
        trainee.setActive(false);
        when(traineeDao.findByUsername("Alice.Brown")).thenReturn(trainee);

        assertFalse(traineeService.authenticate("Alice.Brown", "wrong-password"));
        assertThrows(SecurityException.class,
                () -> traineeService.changePassword("Alice.Brown", "wrong-password", "NewPass1234"));
        assertThrows(IllegalArgumentException.class,
                () -> traineeService.changePassword("Alice.Brown", "OldPass1234", "short"));

        when(traineeDao.update(eq(1L), any(Trainee.class))).thenReturn(trainee);
        traineeService.activate("Alice.Brown");
        assertThrows(IllegalStateException.class, () -> traineeService.activate("Alice.Brown"));
        traineeService.deactivate("Alice.Brown");
        assertThrows(IllegalStateException.class, () -> traineeService.deactivate("Alice.Brown"));

        assertThrows(IllegalArgumentException.class,
                () -> traineeService.updateTrainerAssignments("missing", Collections.emptySet()));

        trainee.setActive(false);
        when(traineeDao.findByUsername("Alice.Brown")).thenReturn(trainee);
        assertThrows(IllegalArgumentException.class,
                () -> traineeService.updateTrainerAssignments("Alice.Brown", Collections.singleton("Missing.Trainer")));
    }
}