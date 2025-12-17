package org.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class PropertyFactoryTest {

    @BeforeEach
    void setUp() {
        System.setProperty("test", "false");
    }

    @Test
    void getInstance_WhenCalledMultipleTimes_ShouldReturnSameInstance() {
        // When
        PropertyFactory actualResult1 = PropertyFactory.getInstance();
        PropertyFactory actualResult2 = PropertyFactory.getInstance();

        // Then
        PropertyFactory expectedResult = actualResult1;
        assertNotNull(actualResult1);
        assertNotNull(actualResult2);
        assertSame(expectedResult, actualResult2);
    }

    @Test
    void getProperties_WhenCalled_ShouldReturnProperties() {
        // Given
        PropertyFactory factory = PropertyFactory.getInstance();

        // When
        Properties actualResult = factory.getProperties();

        // Then
        assertNotNull(actualResult);
        assertTrue(actualResult instanceof Properties);
    }

    @Test
    void getProperties_WhenCalledMultipleTimes_ShouldReturnDifferentObjects() {
        // Given
        PropertyFactory factory = PropertyFactory.getInstance();

        // When
        Properties actualResult1 = factory.getProperties();
        Properties actualResult2 = factory.getProperties();

        // Then
        assertNotSame(actualResult1, actualResult2);
    }

    @Test
    void getProperty_WhenCalled_ShouldReturnProperties() {
        // Given
        PropertyFactory factory = PropertyFactory.getInstance();

        // When
        Properties actualResult = factory.getProperty();

        // Then
        assertNotNull(actualResult);
        assertTrue(actualResult instanceof Properties);
    }
}
