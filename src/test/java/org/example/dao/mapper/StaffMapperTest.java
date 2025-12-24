package org.example.dao.mapper;

import org.example.model.Staff;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StaffMapperTest {

    private StaffMapper mapper;
    private ResultSet resultSet;

    @BeforeEach
    void setUp() {
        mapper = new StaffMapper();
        resultSet = mock(ResultSet.class);
    }

    @Test
    void map_WhenResultSetHasValidData_ShouldReturnStaffWithCorrectValues() throws SQLException {
        // Given
        Long expectedId = 1L;
        Long expectedShiftId = 2L;
        Long expectedPositionId = 3L;
        String expectedFirstName = "Иван";
        String expectedLastName = "Петров";
        
        when(resultSet.getLong("id")).thenReturn(expectedId);
        when(resultSet.getLong("shift_id")).thenReturn(expectedShiftId);
        when(resultSet.getLong("position_id")).thenReturn(expectedPositionId);
        when(resultSet.getString("firstname")).thenReturn(expectedFirstName);
        when(resultSet.getString("lastname")).thenReturn(expectedLastName);

        // When
        Staff actualResult = mapper.map(resultSet);

        // Then
        assertNotNull(actualResult);
        assertEquals(expectedId, actualResult.getId());
        assertEquals(expectedShiftId, actualResult.getShift_Id());
        assertEquals(expectedPositionId, actualResult.getPositionId());
        assertEquals(expectedFirstName, actualResult.getFirstName());
        assertEquals(expectedLastName, actualResult.getLastName());
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
    void map_WhenResultSetHasNullFields_ShouldReturnStaffWithNullValues() throws SQLException {
        // Given
        Long expectedId = 1L;
        String expectedFirstName = null;
        String expectedLastName = null;
        
        when(resultSet.getLong("id")).thenReturn(expectedId);
        when(resultSet.getLong("shift_id")).thenReturn(0L);
        when(resultSet.getLong("position_id")).thenReturn(0L);
        when(resultSet.getString("firstname")).thenReturn(expectedFirstName);
        when(resultSet.getString("lastname")).thenReturn(expectedLastName);

        // When
        Staff actualResult = mapper.map(resultSet);

        // Then
        assertNotNull(actualResult);
        assertEquals(expectedId, actualResult.getId());
        assertEquals(expectedFirstName, actualResult.getFirstName());
        assertEquals(expectedLastName, actualResult.getLastName());
    }
}




