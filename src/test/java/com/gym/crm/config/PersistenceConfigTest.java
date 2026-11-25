package com.gym.crm.config;

import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class PersistenceConfigTest {

    @Test
    public void testDataSourceAndTransactionManager() {
        PersistenceConfig config = new PersistenceConfig();

        DataSource dataSource = config.dataSource("jdbc:h2:mem:test", "sa", "");
        assertNotNull(dataSource);

        EntityManagerFactory entityManagerFactory = mock(EntityManagerFactory.class);
        PlatformTransactionManager transactionManager = config.transactionManager(entityManagerFactory);
        assertInstanceOf(JpaTransactionManager.class, transactionManager);
        assertNotNull(config.trainingTypeInitializer(entityManagerFactory));

        org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean factory =
                config.entityManagerFactory(dataSource, "update", "false", "true");
        assertNotNull(factory);
    }
}
