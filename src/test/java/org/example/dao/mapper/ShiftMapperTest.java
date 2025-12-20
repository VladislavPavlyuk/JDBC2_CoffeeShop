package org.example.dao.mapper;

import org.example.model.Shift;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShiftMapperTest {

    private ShiftMapper mapper;
    private ResultSet resultSet;

    @BeforeEach
    void setUp() {
        mapper = new ShiftMapper();
        resultSet = mock(ResultSet.class);
    }

    @Test
    void map_WhenResultSetHasValidData_ShouldReturnShiftWithCorrectValues() throws SQLException {
        // Given
        Long expectedId = 1L;
        String expectedShiftTitle = "Morning Shift";
        
        when(resultSet.getLong("id")).thenReturn(expectedId);
        when(resultSet.getString("shift_title")).thenReturn(expectedShiftTitle);

        // When
        Shift actualResult = mapper.map(resultSet);

        // Then
        assertNotNull(actualResult);
        assertEquals(expectedId, actualResult.getId());
        assertEquals(expectedShiftTitle, actualResult.getShiftTitle());
    }

    @Test
    void map_WhenResultSetThrowsSQLException_ShouldThrowSQLException() throws SQLException {
        // Given
        when(resultSet.getLong("id")).thenThrow(new SQLException("Database error"));

        // When
        Exception actualResult = null;
        try {
            mapper.map(resultSet);
        } catch (SQLException e) {
            actualResult = e;
        }

        // Then
        assertNotNull(actualResult);
        assertTrue(actualResult instanceof SQLException);
    }

    @Test
    void map_WhenResultSetHasNullFields_ShouldReturnShiftWithNullValues() throws SQLException {
        // Given
        Long expectedId = 1L;
        String expectedShiftTitle = null;
        
        when(resultSet.getLong("id")).thenReturn(expectedId);
        when(resultSet.getString("shift_title")).thenReturn(expectedShiftTitle);

        // When
        Shift actualResult = mapper.map(resultSet);

        // Then
        assertNotNull(actualResult);
        assertEquals(expectedId, actualResult.getId());
        assertEquals(expectedShiftTitle, actualResult.getShiftTitle());
    }
}


