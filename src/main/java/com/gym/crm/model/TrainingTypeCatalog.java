package com.gym.crm.model;

import java.util.Set;

public final class TrainingTypeCatalog {
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "Fitness",
            "Yoga",
            "Crossfit",
            "Cardio",
            "Strength"
    );

    private TrainingTypeCatalog() {
    }

    public static boolean isAllowed(String typeName) {
        return typeName != null && ALLOWED_TYPES.contains(typeName);
    }

    public static Set<String> getAllowedTypes() {
        return ALLOWED_TYPES;
    }
}
