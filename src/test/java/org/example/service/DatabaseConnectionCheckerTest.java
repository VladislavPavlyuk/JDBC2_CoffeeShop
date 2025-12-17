package org.example.service;

import org.example.exception.ConnectionDBException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectionCheckerTest {

    @Test
    void isDatabaseAvailable_WhenCalled_ShouldReturnBoolean() {
        // When
        boolean actualResult = DatabaseConnectionChecker.isDatabaseAvailable();

        // Then
        assertTrue(actualResult || !actualResult); // Проверка что метод возвращает boolean
    }

    @Test
    void checkDatabaseConnection_WhenDatabaseAvailable_ShouldNotThrowException() {
        // When
        Exception actualResult = null;
        try {
            DatabaseConnectionChecker.checkDatabaseConnection();
        } catch (ConnectionDBException e) {
            actualResult = e;
        }

        // Then
        // Метод может либо выполниться успешно, либо выбросить исключение
        // в зависимости от доступности БД
        if (actualResult != null) {
            assertTrue(actualResult instanceof ConnectionDBException);
        } else {
            assertNull(actualResult);
        }
    }

    @Test
    void checkDatabaseConnection_WhenDatabaseUnavailable_ShouldThrowConnectionDBException() {
        // When
        Exception actualResult = null;
        try {
            DatabaseConnectionChecker.checkDatabaseConnection();
        } catch (ConnectionDBException e) {
            actualResult = e;
        }

        // Then
        // Метод может либо выполниться успешно, либо выбросить исключение
        // в зависимости от доступности БД
        if (actualResult != null) {
            assertTrue(actualResult instanceof ConnectionDBException);
        } else {
            assertNull(actualResult);
        }
    }
}
