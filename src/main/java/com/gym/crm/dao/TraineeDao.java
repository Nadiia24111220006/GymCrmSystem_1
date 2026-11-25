package com.gym.crm.dao;

import com.gym.crm.model.Trainee;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class TraineeDao {
    @PersistenceContext
    private EntityManager entityManager;

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Trainee save(Trainee trainee) {
        entityManager.persist(trainee);
        return trainee;
    }

    public Trainee findById(long id) {
        return entityManager.find(Trainee.class, id);
    }

    public Trainee update(long id, Trainee updatedTrainee) {
        Trainee existing = entityManager.find(Trainee.class, id);
        if (existing == null) {
            return null;
        }
        updatedTrainee.setId(id);
        return entityManager.merge(updatedTrainee);
    }

    public void delete(long id) {
        Trainee existing = entityManager.find(Trainee.class, id);
        if (existing != null) {
            entityManager.remove(existing);
        }
    }

    public Collection<Trainee> findAll() {
        return entityManager.createQuery("select t from Trainee t", Trainee.class).getResultList();
    }

    public Trainee findByUsername(String username) {
        return entityManager.createQuery("select t from Trainee t where t.username = :username", Trainee.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public void deleteByUsername(String username) {
        Trainee trainee = findByUsername(username);
        if (trainee != null) {
            entityManager.remove(trainee);
        }
    }
}