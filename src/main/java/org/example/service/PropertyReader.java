package org.example.service;

import org.example.exception.PropertyFileException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static java.lang.System.getProperty;

// Reads config from properties files
public class PropertyReader {

    private static final String MAIN_CONFIG_PATH = "src/main/resources/config.properties";
    private static final String TEST_CONFIG_PATH = "src/test/resources/application-test.properties";
    private static final String TEST_PROPERTY_KEY = "test";

    // chooses config file based on "test" property
    public Properties readProperties() throws PropertyFileException {
        String testProperty = getProperty(TEST_PROPERTY_KEY);
        String configPath = "false".equals(testProperty) ? MAIN_CONFIG_PATH : TEST_CONFIG_PATH;
        
        return loadPropertiesFromPath(configPath);
    }

    private Properties loadPropertiesFromPath(String configPath) throws PropertyFileException {
        Properties properties = new Properties();
        
        try (InputStream inputStream = new FileInputStream(configPath)) {
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new PropertyFileException("Error opening property file: " + configPath, e);
        }
    }

    public Properties readPropertiesFromPath(String configPath) throws PropertyFileException {
        return loadPropertiesFromPath(configPath);
    }
}
