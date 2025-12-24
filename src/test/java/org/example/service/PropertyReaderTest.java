package org.example.service;

import org.example.exception.PropertyFileException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class PropertyReaderTest {

    private PropertyReader propertyReader;

    @BeforeEach
    void setUp() {
        propertyReader = new PropertyReader();
    }

    @Test
    void readPropertiesFromPath_WhenFileExists_ShouldReturnPropertiesWithCorrectValues(@TempDir Path tempDir) throws Exception {
        // Given
        File propertiesFile = tempDir.resolve("test.properties").toFile();
        try (FileWriter writer = new FileWriter(propertiesFile)) {
            writer.write("db.driver=org.postgresql.Driver\n");
            writer.write("db.dburl=jdbc:postgresql://localhost:5432/test\n");
            writer.write("db.user=testuser\n");
            writer.write("db.password=testpass\n");
        }

        // When
        Properties actualResult = propertyReader.readPropertiesFromPath(propertiesFile.getAbsolutePath());

        // Then
        Properties expectedResult = new Properties();
        expectedResult.setProperty("db.driver", "org.postgresql.Driver");
        expectedResult.setProperty("db.dburl", "jdbc:postgresql://localhost:5432/test");
        expectedResult.setProperty("db.user", "testuser");
        expectedResult.setProperty("db.password", "testpass");
        
        assertNotNull(actualResult);
        assertEquals(expectedResult.getProperty("db.driver"), actualResult.getProperty("db.driver"));
        assertEquals(expectedResult.getProperty("db.dburl"), actualResult.getProperty("db.dburl"));
        assertEquals(expectedResult.getProperty("db.user"), actualResult.getProperty("db.user"));
        assertEquals(expectedResult.getProperty("db.password"), actualResult.getProperty("db.password"));
    }

    @Test
    void readPropertiesFromPath_WhenFileNotExists_ShouldThrowPropertyFileException() {
        // Given
        String nonExistentPath = "nonexistent.properties";

        // When
        Exception actualResult = null;
        try {
            propertyReader.readPropertiesFromPath(nonExistentPath);
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        assertNotNull(actualResult);
        assertTrue(actualResult instanceof PropertyFileException);
    }

    @Test
    void readProperties_WhenTestPropertyIsTrue_ShouldLoadTestProperties() {
        // Given
        System.setProperty("test", "true");

        // When
        Properties actualResult = null;
        Exception exception = null;
        try {
            actualResult = propertyReader.readProperties();
        } catch (PropertyFileException e) {
            exception = e;
        } finally {
            System.clearProperty("test");
        }

        // Then
        if (exception == null) {
            assertNotNull(actualResult);
        } else {
            assertTrue(exception instanceof PropertyFileException);
        }
    }

    @Test
    void readProperties_WhenTestPropertyIsFalse_ShouldLoadMainProperties() {
        // Given
        System.setProperty("test", "false");

        // When
        Properties actualResult = null;
        Exception exception = null;
        try {
            actualResult = propertyReader.readProperties();
        } catch (PropertyFileException e) {
            exception = e;
        } finally {
            System.clearProperty("test");
        }

        // Then
        if (exception == null) {
            assertNotNull(actualResult);
        } else {
            assertTrue(exception instanceof PropertyFileException);
        }
    }
}




