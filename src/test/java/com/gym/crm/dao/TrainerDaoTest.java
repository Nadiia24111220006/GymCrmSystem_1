package com.gym.crm.dao;

import com.gym.crm.model.Trainer;
import com.gym.crm.model.TrainingType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class TrainerDaoTest {
    private TrainerDao trainerDao;
    private final Map<Long, Trainer> storage = new HashMap<>();

    @BeforeEach
    public void setUp() {
        trainerDao = new TrainerDao();
        EntityManager entityManager = mock(EntityManager.class);
        AtomicLong counter = new AtomicLong(1L);

        doAnswer(invocation -> {
            Trainer trainer = invocation.getArgument(0);
            if (trainer.getId() == null) trainer.setId(counter.getAndIncrement());
            storage.put(trainer.getId(), trainer);
            return null;
        }).when(entityManager).persist(any(Trainer.class));
        when(entityManager.find(eq(Trainer.class), anyLong())).thenAnswer(invocation -> storage.get(invocation.getArgument(1)));
        when(entityManager.merge(any(Trainer.class))).thenAnswer(invocation -> {
            Trainer trainer = invocation.getArgument(0);
            storage.put(trainer.getId(), trainer);
            return trainer;
        });
        doAnswer(invocation -> {
            Trainer trainer = invocation.getArgument(0);
            storage.remove(trainer.getId());
            return null;
        }).when(entityManager).remove(any(Trainer.class));

        TypedQuery<Trainer> allQuery = mock(TypedQuery.class);
        when(allQuery.getResultList()).thenAnswer(invocation -> new ArrayList<>(storage.values()));
        when(entityManager.createQuery(eq("select t from Trainer t"), eq(Trainer.class))).thenReturn(allQuery);

        trainerDao.setEntityManager(entityManager);
    }

    @Test
    public void testSaveAndFind() {
        Trainer trainer = new Trainer("John", "Doe", new TrainingType("Fitness"));
        trainerDao.save(trainer);
        Trainer found = trainerDao.findById(1L);
        assertNotNull(found);
        assertEquals(1L, found.getId());
    }

    @Test
    public void testTrainerDaoMethods() {
        Trainer trainer = new Trainer("John", "Doe", new TrainingType("Fitness"));
        trainerDao.save(trainer);
        assertFalse(trainerDao.findAll().isEmpty());
        trainerDao.update(1L, trainer);
        assertNotNull(trainerDao.findById(1L));
        trainerDao.delete(1L);
        assertNull(trainerDao.findById(1L));
    }

    @Test
    public void testUsernameLookupAndMissingUpdate() {
        Trainer trainer = new Trainer("John", "Doe", new TrainingType("Fitness"));
        trainer.setUsername("John.Doe");
        trainerDao.save(trainer);

        assertNull(trainerDao.update(999L, new Trainer()));
        trainerDao.delete(999L);
        assertNotNull(trainerDao.findById(1L));
    }

}