package org.example.dao;

import org.example.exception.ConnectionDBException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionFactoryTest {

    @Test
    void getInstance_WhenCalledMultipleTimes_ShouldReturnSameInstance() {
        // When
        ConnectionFactory actualResult1 = ConnectionFactory.getInstance();
        ConnectionFactory actualResult2 = ConnectionFactory.getInstance();

        // Then
        ConnectionFactory expectedResult = actualResult1;
        assertNotNull(actualResult1);
        assertNotNull(actualResult2);
        assertSame(expectedResult, actualResult2);
    }

    @Test
    void getConnection_WhenDatabaseUnavailable_ShouldThrowConnectionDBException() {
        // Given
        ConnectionFactory factory = ConnectionFactory.getInstance();

        // When
        Exception actualResult = null;
        try {
            factory.getConnection();
        } catch (ConnectionDBException e) {
            actualResult = e;
        }

        // Then
        assertNotNull(actualResult);
        assertTrue(actualResult instanceof ConnectionDBException);
    }

    @Test
    void makeConnection_WhenCalled_ShouldCallGetConnection() {
        // Given
        ConnectionFactory factory = ConnectionFactory.getInstance();

        // When
        Exception actualResult = null;
        try {
            factory.makeConnection();
        } catch (ConnectionDBException e) {
            actualResult = e;
        }

        // Then
        assertNotNull(actualResult);
        assertTrue(actualResult instanceof ConnectionDBException);
    }
}
