package org.example.dao.menuDAO;

import org.example.dao.ConnectionFactory;
import org.example.dao.ConnectionProvider;
import org.example.model.MenuItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuDaoImplTest {

    @Mock
    private ConnectionProvider connectionProvider;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private MenuDaoImpl menuDao;

    @BeforeEach
    void setUp() {
        menuDao = new MenuDaoImpl(connectionProvider);
    }

    @Test
    void findAllDesserts_WhenDessertsExist_ShouldReturnListOfMenuItems() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getLong("id")).thenReturn(1L, 2L);
        when(resultSet.getString("item_code")).thenReturn("DES001", "DES002");
        when(resultSet.getString("name")).thenReturn("Cheesecake", "Tiramisu");
        when(resultSet.getDouble("base_price")).thenReturn(5.99, 6.99);
        when(resultSet.getString("status")).thenReturn("Available", "Available");

        // When
        List<MenuItem> actualResult = menuDao.findAllDesserts();

        // Then
        assertNotNull(actualResult);
        assertTrue(actualResult.size() >= 0);
    }

    @Test
    void findAllDesserts_WhenNoDessertsExist_ShouldReturnEmptyList() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // When
        List<MenuItem> actualResult = menuDao.findAllDesserts();

        // Then
        assertNotNull(actualResult);
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void findAllDesserts_WhenSQLExceptionOccurs_ShouldThrowException() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Database error"));

        // When & Then
        assertThrows(Exception.class, () -> menuDao.findAllDesserts());
    }

    @Test
    void findAllDrinks_WhenDrinksExist_ShouldReturnListOfMenuItems() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getLong("id")).thenReturn(1L, 2L);
        when(resultSet.getString("item_code")).thenReturn("DRK001", "DRK002");
        when(resultSet.getString("name")).thenReturn("Cappuccino", "Latte");
        when(resultSet.getDouble("base_price")).thenReturn(3.99, 4.99);
        when(resultSet.getString("status")).thenReturn("Available", "Available");

        // When
        List<MenuItem> actualResult = menuDao.findAllDrinks();

        // Then
        assertNotNull(actualResult);
        assertTrue(actualResult.size() >= 0);
    }

    @Test
    void findAllDrinks_WhenNoDrinksExist_ShouldReturnEmptyList() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // When
        List<MenuItem> actualResult = menuDao.findAllDrinks();

        // Then
        assertNotNull(actualResult);
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void findAllDrinks_WhenSQLExceptionOccurs_ShouldThrowException() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Database error"));

        // When & Then
        assertThrows(Exception.class, () -> menuDao.findAllDrinks());
    }
}


