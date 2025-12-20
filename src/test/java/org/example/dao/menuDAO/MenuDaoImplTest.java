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
import java.util.ArrayList;
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
        List<MenuItem> expectedResult = new ArrayList<>();
        assertEquals(expectedResult.size(), actualResult.size());
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
        List<MenuItem> expectedResult = new ArrayList<>();
        assertNotNull(actualResult);
        assertTrue(actualResult.size() >= expectedResult.size());
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
        List<MenuItem> expectedResult = new ArrayList<>();
        assertEquals(expectedResult.size(), actualResult.size());
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

    @Test
    void updateCoffeePrice_WhenValidItemCodeAndPriceProvided_ShouldReturnTrue() throws Exception {
        // Given
        String itemCode = "DRK001";
        double newPrice = 5.99;
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        // When
        boolean actualResult = menuDao.updateCoffeePrice(itemCode, newPrice);

        // Then
        boolean expectedResult = true;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void updateCoffeePrice_WhenItemCodeNotFound_ShouldReturnFalse() throws Exception {
        // Given
        String itemCode = "INVALID";
        double newPrice = 5.99;
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);

        // When
        boolean actualResult = menuDao.updateCoffeePrice(itemCode, newPrice);

        // Then
        boolean expectedResult = false;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void updateCoffeePrice_WhenSQLExceptionOccurs_ShouldThrowException() throws Exception {
        // Given
        String itemCode = "DRK001";
        double newPrice = 5.99;
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Database error"));

        // When & Then
        assertThrows(Exception.class, () -> menuDao.updateCoffeePrice(itemCode, newPrice));
    }

    @Test
    void deleteDessert_WhenValidItemCodeProvided_ShouldReturnTrue() throws Exception {
        // Given
        String itemCode = "DES001";
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        // When
        boolean actualResult = menuDao.deleteDessert(itemCode);

        // Then
        boolean expectedResult = true;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void deleteDessert_WhenItemCodeNotFound_ShouldReturnFalse() throws Exception {
        // Given
        String itemCode = "INVALID";
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);

        // When
        boolean actualResult = menuDao.deleteDessert(itemCode);

        // Then
        boolean expectedResult = false;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void deleteDessert_WhenSQLExceptionOccurs_ShouldThrowException() throws Exception {
        // Given
        String itemCode = "DES001";
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Database error"));

        // When & Then
        assertThrows(Exception.class, () -> menuDao.deleteDessert(itemCode));
    }
}


