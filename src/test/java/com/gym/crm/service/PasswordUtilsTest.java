package com.gym.crm.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordUtilsTest {

    @Test
    public void testEncodeAndMatch() {
        String encoded = PasswordUtils.encode("Secret12345");

        assertTrue(encoded.startsWith("pbkdf2$"));
        assertTrue(PasswordUtils.matches("Secret12345", encoded));
        assertFalse(PasswordUtils.matches("WrongPassword", encoded));
    }

    @Test
    public void testPlainTextCompatibilityAndInvalidValues() {
        assertTrue(PasswordUtils.matches("password123", "password123"));
        assertFalse(PasswordUtils.matches(null, "password123"));
        assertFalse(PasswordUtils.matches("password123", null));
        assertFalse(PasswordUtils.matches("password123", "pbkdf2$broken"));
    }
}

