package org.example.dao.mapper;

import org.example.model.Coffeeshop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CoffeeshopMapperTest {

    private CoffeeshopMapper mapper;
    private ResultSet resultSet;

    @BeforeEach
    void setUp() {
        mapper = new CoffeeshopMapper();
        resultSet = mock(ResultSet.class);
    }

    @Test
    void map_WhenResultSetHasValidData_ShouldReturnCoffeeshopWithCorrectValues() throws SQLException {
        // Given
        Long expectedId = 1L;
        String expectedTitle = "Coffee Shop 1";
        String expectedDescription = "Description 1";
        
        when(resultSet.getLong("id")).thenReturn(expectedId);
        when(resultSet.getString("coffeeshop_Title")).thenReturn(expectedTitle);
        when(resultSet.getString("coffeeshop_description")).thenReturn(expectedDescription);

        // When
        Coffeeshop actualResult = mapper.map(resultSet);

        // Then
        assertNotNull(actualResult);
        assertEquals(expectedId, actualResult.getId());
        assertEquals(expectedTitle, actualResult.getCoffeeshopTitle());
        assertEquals(expectedDescription, actualResult.getCoffeeshopDescription());
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
    void map_WhenResultSetHasNullFields_ShouldReturnCoffeeshopWithNullValues() throws SQLException {
        // Given
        Long expectedId = 1L;
        String expectedTitle = null;
        String expectedDescription = null;
        
        when(resultSet.getLong("id")).thenReturn(expectedId);
        when(resultSet.getString("coffeeshop_Title")).thenReturn(expectedTitle);
        when(resultSet.getString("coffeeshop_description")).thenReturn(expectedDescription);

        // When
        Coffeeshop actualResult = mapper.map(resultSet);

        // Then
        assertNotNull(actualResult);
        assertEquals(expectedId, actualResult.getId());
        assertEquals(expectedTitle, actualResult.getCoffeeshopTitle());
        assertEquals(expectedDescription, actualResult.getCoffeeshopDescription());
    }
}












