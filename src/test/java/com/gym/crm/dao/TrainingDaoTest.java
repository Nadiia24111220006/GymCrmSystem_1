package com.gym.crm.dao;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class TrainingDaoTest {
    private TrainingDao trainingDao;
    private final Map<Long, Training> storage = new HashMap<>();

    @BeforeEach
    public void setUp() {
        trainingDao = new TrainingDao();
        EntityManager entityManager = mock(EntityManager.class);
        AtomicLong counter = new AtomicLong(1L);

        TrainingType yoga = new TrainingType("Yoga");
        yoga.setId(1L);
        TypedQuery<TrainingType> typeQuery = mock(TypedQuery.class);
        when(typeQuery.setParameter(anyString(), any())).thenReturn(typeQuery);
        when(typeQuery.getResultStream()).thenAnswer(invocation -> java.util.stream.Stream.of(yoga));
        when(entityManager.createQuery(eq("select t from TrainingType t where t.trainingTypeName = :name"), eq(TrainingType.class)))
                .thenReturn(typeQuery);

        doAnswer(invocation -> {
            Training training = invocation.getArgument(0);
            if (training.getId() == null) training.setId(counter.getAndIncrement());
            storage.put(training.getId(), training);
            return null;
        }).when(entityManager).persist(any(Training.class));
        when(entityManager.find(eq(Training.class), anyLong())).thenAnswer(invocation -> storage.get(invocation.getArgument(1)));
        when(entityManager.merge(any(Training.class))).thenAnswer(invocation -> {
            Training training = invocation.getArgument(0);
            storage.put(training.getId(), training);
            return training;
        });
        doAnswer(invocation -> {
            Training training = invocation.getArgument(0);
            storage.remove(training.getId());
            return null;
        }).when(entityManager).remove(any(Training.class));

        TypedQuery<Training> allQuery = mock(TypedQuery.class);
        when(allQuery.getResultList()).thenAnswer(invocation -> new ArrayList<>(storage.values()));
        when(entityManager.createQuery(eq("select t from Training t"), eq(Training.class))).thenReturn(allQuery);

        trainingDao.setEntityManager(entityManager);
    }

    @Test
    public void testSaveAndFind() {
        Trainee trainee = new Trainee("Alice", "Smith", "Kyiv", new Date());
        Trainer trainer = new Trainer("Bob", "Builder", new TrainingType("Yoga"));
        Training training = new Training("Yoga Class", new TrainingType("Yoga"), trainer, trainee, new Date(), 30);

        trainingDao.save(training);
        assertNotNull(trainingDao.findById(1L));
    }

    @Test
    public void testUpdateDeleteAndFindAll() {
        Trainee trainee = new Trainee("Alice", "Smith", "Kyiv", new Date());
        Trainer trainer = new Trainer("Bob", "Builder", new TrainingType("Yoga"));
        Training training = new Training("Yoga Class", new TrainingType("Yoga"), trainer, trainee, new Date(), 30);

        trainingDao.save(training);
        assertFalse(trainingDao.findAll().isEmpty());
        training.setTrainingName("Advanced Yoga");
        trainingDao.update(1L, training);
        assertEquals("Advanced Yoga", trainingDao.findById(1L).getTrainingName());
        trainingDao.delete(1L);
        assertNull(trainingDao.findById(1L));
    }

    @Test
    public void testMissingUpdateAndDelete() {
        trainingDao.delete(999L);
        assertNull(trainingDao.update(999L, new Training()));
        assertNull(trainingDao.findById(999L));
    }
}