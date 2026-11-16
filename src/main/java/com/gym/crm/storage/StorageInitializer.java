package com.gym.crm.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

@Component
public class StorageInitializer implements BeanPostProcessor {
    private static final Logger log = LoggerFactory.getLogger(StorageInitializer.class);

    @Value("${storage.init.file}")
    private String initFileName;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof Storage) {
            log.info("Data storage initialization has begun using file via BeanPostProcessor: {}", initFileName);
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(initFileName)) {
                if (inputStream != null) {
                    log.info("Initialization file successfully loaded: {}", initFileName);
                } else {
                    log.warn("Warning: Initialization file '{}' not found in classpath.", initFileName);
                }
            } catch (Exception e) {
                log.error("Error reading initialization file: {}", e.getMessage(), e);
            }
            log.info("Storage successfully initialized");
        }
        return bean;
    }
}