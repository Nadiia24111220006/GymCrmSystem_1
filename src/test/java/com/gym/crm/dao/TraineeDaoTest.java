package com.gym.crm.dao;

import com.gym.crm.model.Trainee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class TraineeDaoTest {
    private TraineeDao traineeDao;
    private final Map<Long, Trainee> storage = new HashMap<>();

    @BeforeEach
    public void setUp() {
        traineeDao = new TraineeDao();
        EntityManager entityManager = mock(EntityManager.class);
        AtomicLong counter = new AtomicLong(1L);

        doAnswer(invocation -> {
            Trainee trainee = invocation.getArgument(0);
            if (trainee.getId() == null) trainee.setId(counter.getAndIncrement());
            storage.put(trainee.getId(), trainee);
            return null;
        }).when(entityManager).persist(any(Trainee.class));
        when(entityManager.find(eq(Trainee.class), anyLong())).thenAnswer(invocation -> storage.get(invocation.getArgument(1)));
        when(entityManager.merge(any(Trainee.class))).thenAnswer(invocation -> {
            Trainee trainee = invocation.getArgument(0);
            storage.put(trainee.getId(), trainee);
            return trainee;
        });
        doAnswer(invocation -> {
            Trainee trainee = invocation.getArgument(0);
            storage.values().removeIf(t -> t == trainee);
            return null;
        }).when(entityManager).remove(any(Trainee.class));

        TypedQuery<Trainee> allQuery = mock(TypedQuery.class);
        when(allQuery.getResultList()).thenAnswer(invocation -> new ArrayList<>(storage.values()));
        when(entityManager.createQuery(eq("select t from Trainee t"), eq(Trainee.class))).thenReturn(allQuery);

        // Fix the username lookup mock with a captured username and wire it to EntityManager.
        TypedQuery<Trainee> usernameQuery = mock(TypedQuery.class);
        AtomicReference<String> capturedUsername = new AtomicReference<>();
        when(usernameQuery.setParameter(eq("username"), any())).thenAnswer(invocation -> {
            capturedUsername.set(invocation.getArgument(1));
            return usernameQuery;
        });
        when(usernameQuery.getResultStream()).thenAnswer(invocation -> 
            storage.values().stream()
                .filter(t -> t.getUsername() != null && t.getUsername().equals(capturedUsername.get()))
        );
        when(entityManager.createQuery(eq("select t from Trainee t where t.username = :username"), eq(Trainee.class))).thenReturn(usernameQuery);

        traineeDao.setEntityManager(entityManager);
    }

    @Test
    public void testSaveAndFindById() {
        Trainee trainee = new Trainee("Alice", "Smith", "Lviv", new Date());
        traineeDao.save(trainee);
        Trainee found = traineeDao.findById(1L);
        assertNotNull(found);
        assertEquals("Alice", found.getFirstName());
        assertEquals(1L, found.getId());
    }

    @Test
    public void testUpdateDeleteAndFindAll() {
        Trainee trainee = new Trainee("Alice", "Smith", "Lviv", new Date());
        traineeDao.save(trainee);
        assertFalse(traineeDao.findAll().isEmpty());
        trainee.setAddress("Kyiv");
        traineeDao.update(1L, trainee);
        assertEquals("Kyiv", traineeDao.findById(1L).getAddress());
        traineeDao.delete(1L);
        assertNull(traineeDao.findById(1L));
    }

    @Test
    public void testUsernameLookupAndMissingUpdate() {
        Trainee trainee = new Trainee("Alice", "Smith", "Lviv", new Date());
        trainee.setUsername("Alice.Smith");
        traineeDao.save(trainee);

        assertNull(traineeDao.update(999L, new Trainee()));
        traineeDao.delete(999L);
        traineeDao.deleteByUsername("missing");
        assertNotNull(traineeDao.findById(1L));
    }

}