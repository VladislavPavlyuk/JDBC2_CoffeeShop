package org.example.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.System.getProperty;
import static java.lang.System.getenv;
import java.util.Properties;

import org.example.exception.PropertyFileException;

public class PropertyReader {

    private static final String MAIN_CONFIG_PATH = "src/main/resources/config.properties";
    private static final String TEST_CONFIG_PATH = "src/test/resources/application-test.properties";
    private static final String TEST_PROPERTY_KEY = "test";

    public Properties readProperties() throws PropertyFileException {
        String testProperty = getProperty(TEST_PROPERTY_KEY);
        String configPath = "false".equals(testProperty) ? MAIN_CONFIG_PATH : TEST_CONFIG_PATH;
        
        Properties properties = loadPropertiesFromPath(configPath);
        overrideWithEnvironmentVariables(properties);
        return properties;
    }

    private void overrideWithEnvironmentVariables(Properties properties) {
        String dbHost = getenv("DB_HOST");
        String dbPort = getenv("DB_PORT");
        String dbName = getenv("DB_NAME");
        String dbUser = getenv("DB_USER");
        String dbPassword = getenv("DB_PASSWORD");
        
        if (dbHost != null && dbPort != null && dbName != null) {
            String dbUrl = String.format("jdbc:postgresql://%s:%s/%s", dbHost, dbPort, dbName);
            properties.setProperty("db.dburl", dbUrl);
        }
        
        if (dbUser != null) {
            properties.setProperty("db.user", dbUser);
        }
        
        if (dbPassword != null) {
            properties.setProperty("db.password", dbPassword);
        }
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
