package com.gym.crm.dao;

import com.gym.crm.model.Trainer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public class TrainerDao {
    @PersistenceContext
    private EntityManager entityManager;

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Trainer save(Trainer trainer) {
        entityManager.persist(trainer);
        return trainer;
    }

    public Trainer findById(long id) {
        return entityManager.find(Trainer.class, id);
    }

    public Trainer update(long id, Trainer updatedTrainer) {
        Trainer existing = entityManager.find(Trainer.class, id);
        if (existing == null) {
            return null;
        }
        updatedTrainer.setId(id);
        return entityManager.merge(updatedTrainer);
    }

    public void delete(long id) {
        Trainer existing = entityManager.find(Trainer.class, id);
        if (existing != null) {
            entityManager.remove(existing);
        }
    }

    public Collection<Trainer> findAll() {
        return entityManager.createQuery("select t from Trainer t", Trainer.class).getResultList();
    }

    public Trainer findByUsername(String username) {
        return entityManager.createQuery("select t from Trainer t where t.username = :username", Trainer.class)
                .setParameter("username", username)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }
}