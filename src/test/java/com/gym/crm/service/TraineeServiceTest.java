package com.gym.crm.service;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.model.Trainee;
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

        when(traineeDao.save(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainee trainee = traineeService.createTrainee(firstName, lastName, dob, address);

        assertNotNull(trainee);
        assertEquals("Alice.Brown", trainee.getUsername());
        assertNotNull(trainee.getPassword());
        assertTrue(trainee.isActive());

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
}