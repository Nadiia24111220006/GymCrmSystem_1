package com.gym.crm.dao;

import com.gym.crm.model.Training;
import com.gym.crm.model.TrainingType;
import com.gym.crm.model.TrainingTypeCatalog;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Repository
public class TrainingDao {
    @PersistenceContext
    private EntityManager entityManager;

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Training save(Training training) {
        training.setTrainingType(resolveTrainingType(training.getTrainingType()));
        entityManager.persist(training);
        return training;
    }

    public Training findById(long id) {
        return entityManager.find(Training.class, id);
    }

    public Training update(long id, Training updatedTraining) {
        Training existing = entityManager.find(Training.class, id);
        if (existing == null) {
            return null;
        }
        updatedTraining.setTrainingType(resolveTrainingType(updatedTraining.getTrainingType()));
        updatedTraining.setId(id);
        return entityManager.merge(updatedTraining);
    }

    public void delete(long id) {
        Training existing = entityManager.find(Training.class, id);
        if (existing != null) {
            entityManager.remove(existing);
        }
    }

    public Collection<Training> findAll() {
        return entityManager.createQuery("select t from Training t", Training.class).getResultList();
    }

    public void deleteByTraineeUsername(String traineeUsername) {
        entityManager.createQuery("delete from Training t where t.trainee.username = :username")
                .setParameter("username", traineeUsername)
                .executeUpdate();
    }

    public void deleteByTrainerUsername(String trainerUsername) {
        entityManager.createQuery("delete from Training t where t.trainer.username = :username")
                .setParameter("username", trainerUsername)
                .executeUpdate();
    }

    public List<Training> findTraineeTrainingsByCriteria(String traineeUsername, Date fromDate, Date toDate,
                                                          String trainerName, String trainingTypeName) {
        StringBuilder jpql = new StringBuilder("select t from Training t where t.trainee.username = :username");
        if (fromDate != null) jpql.append(" and t.trainingDate >= :fromDate");
        if (toDate != null) jpql.append(" and t.trainingDate <= :toDate");
        if (trainerName != null && !trainerName.isBlank())
            jpql.append(" and concat(concat(t.trainer.firstName, ' '), t.trainer.lastName) = :trainerName");
        if (trainingTypeName != null && !trainingTypeName.isBlank())
            jpql.append(" and lower(t.trainingType.trainingTypeName) = lower(:trainingTypeName)");

        var query = entityManager.createQuery(jpql.toString(), Training.class)
                .setParameter("username", traineeUsername);
        if (fromDate != null) query.setParameter("fromDate", fromDate);
        if (toDate != null) query.setParameter("toDate", toDate);
        if (trainerName != null && !trainerName.isBlank()) query.setParameter("trainerName", trainerName.trim());
        if (trainingTypeName != null && !trainingTypeName.isBlank())
            query.setParameter("trainingTypeName", trainingTypeName.trim());
        return query.getResultList();
    }

    public List<Training> findTrainerTrainingsByCriteria(String trainerUsername, Date fromDate, Date toDate,
                                                          String traineeName) {
        StringBuilder jpql = new StringBuilder("select t from Training t where t.trainer.username = :username");
        if (fromDate != null) jpql.append(" and t.trainingDate >= :fromDate");
        if (toDate != null) jpql.append(" and t.trainingDate <= :toDate");
        if (traineeName != null && !traineeName.isBlank())
            jpql.append(" and concat(concat(t.trainee.firstName, ' '), t.trainee.lastName) = :traineeName");

        var query = entityManager.createQuery(jpql.toString(), Training.class)
                .setParameter("username", trainerUsername);
        if (fromDate != null) query.setParameter("fromDate", fromDate);
        if (toDate != null) query.setParameter("toDate", toDate);
        if (traineeName != null && !traineeName.isBlank()) query.setParameter("traineeName", traineeName.trim());
        return query.getResultList();
    }

    private TrainingType resolveTrainingType(TrainingType requested) {
        if (requested == null || requested.getTrainingTypeName() == null
                || !TrainingTypeCatalog.isAllowed(requested.getTrainingTypeName())) {
            throw new IllegalArgumentException("Unsupported training type");
        }
        String name = requested.getTrainingTypeName();
        return entityManager.createQuery(
                        "select t from TrainingType t where t.trainingTypeName = :name", TrainingType.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Training type is not initialized: " + name));
    }
}