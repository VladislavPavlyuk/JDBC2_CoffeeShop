package org.example.service;

import org.example.exception.PropertyFileException;

import java.util.Properties;

// singleton that provides app configuration
public class PropertyFactory {

    private static PropertyFactory propertyFactory;
    private final Properties properties;
    private final PropertyReader propertyReader;

    private PropertyFactory() {
        this.propertyReader = new PropertyReader();
        try {
            this.properties = propertyReader.readProperties();
        } catch (PropertyFileException e) {
            throw new RuntimeException("Failed to initialize properties", e);
        }
    }

    public static PropertyFactory getInstance() {
        if (propertyFactory == null) {
            synchronized (PropertyFactory.class) {
                if (propertyFactory == null) {
                    propertyFactory = new PropertyFactory();
                }
            }
        }
        return propertyFactory;
    }

    public Properties getProperties() {
        return new Properties(properties);
    }

    @Deprecated
    public Properties getProperty() {
        return getProperties();
    }
}
