package com.gym.crm.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TrainingTypeCatalogTest {

    @Test
    public void testCatalogValidation() {
        assertTrue(TrainingTypeCatalog.isAllowed("Yoga"));
        assertTrue(TrainingTypeCatalog.isAllowed("Fitness"));
        assertFalse(TrainingTypeCatalog.isAllowed("Unknown"));
        assertFalse(TrainingTypeCatalog.isAllowed(null));
        assertEquals(5, TrainingTypeCatalog.getAllowedTypes().size());
    }
}

