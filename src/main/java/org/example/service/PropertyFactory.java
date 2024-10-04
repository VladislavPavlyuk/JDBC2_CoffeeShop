package org.example.service;

import org.example.exception.PropertyFileException;

import java.util.Properties;

public class PropertyFactory {

    private static PropertyFactory propertyFactory;
    private static Properties properties;
    private static final PropertyReader PROPERTY_READER;

    static {
        PROPERTY_READER = new PropertyReader();
        try {
            properties = PROPERTY_READER.readProperties();
        } catch (PropertyFileException e) {
            e.printStackTrace();
        }
    }

    public static PropertyFactory getInstance() {
        if (propertyFactory == null) {
            propertyFactory = new PropertyFactory();
        }
        return propertyFactory;
    }

    public Properties getProperty() {
        return properties;
    }
}
