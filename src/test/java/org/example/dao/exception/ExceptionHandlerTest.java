package org.example.dao.exception;

import org.example.exception.ConnectionDBException;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionHandlerTest {

    @Test
    void handleException_WhenConnectionDBExceptionProvided_ShouldReturnDaoExceptionWithDatabaseConnectionError() {
        // Given
        ConnectionDBException exception = new ConnectionDBException("Connection error");

        // When
        DaoException actualResult = ExceptionHandler.handleException(exception);

        // Then
        String expectedMessage = "Database connection error";
        assertEquals(expectedMessage, actualResult.getMessage());
        assertEquals(exception, actualResult.getCause());
        assertNotNull(actualResult);
    }

    @Test
    void handleException_WhenSQLExceptionProvided_ShouldReturnDaoExceptionWithSQLError() {
        // Given
        SQLException exception = new SQLException("SQL error");

        // When
        DaoException actualResult = ExceptionHandler.handleException(exception);

        // Then
        String expectedMessage = "SQL error occurred";
        assertEquals(expectedMessage, actualResult.getMessage());
        assertEquals(exception, actualResult.getCause());
        assertNotNull(actualResult);
    }

    @Test
    void handleException_WhenGenericExceptionProvided_ShouldReturnDaoExceptionWithUnexpectedError() {
        // Given
        RuntimeException exception = new RuntimeException("Generic error");

        // When
        DaoException actualResult = ExceptionHandler.handleException(exception);

        // Then
        String expectedMessage = "Unexpected error in DAO layer";
        assertEquals(expectedMessage, actualResult.getMessage());
        assertEquals(exception, actualResult.getCause());
        assertNotNull(actualResult);
    }

    @Test
    void handleAndLog_WhenExceptionOccurs_ShouldNotThrowException() {
        // Given
        SQLException exception = new SQLException("Test error");
        String operation = "test operation";

        // When
        Exception actualResult = null;
        try {
            ExceptionHandler.handleAndLog(exception, operation);
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }
}










