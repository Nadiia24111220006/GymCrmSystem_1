package com.gym.crm.storage;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StorageInitializerTest {

    @Test
    public void testPostProcessAfterInitialization() {
        StorageInitializer initializer = new StorageInitializer();
        Storage storage = new Storage();

        Object result = initializer.postProcessAfterInitialization(storage, "storage");
        assertNotNull(result);
        assertEquals(storage, result);

        String dummyBean = "SomeBean";
        Object dummyResult = initializer.postProcessAfterInitialization(dummyBean, "dummy");
        assertEquals(dummyBean, dummyResult);
    }
}