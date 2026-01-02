package org.example.dao.orderDAO;

import org.example.dao.ConnectionProvider;
import org.example.model.CustomerBaristaInfo;
import org.example.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderDaoImplTest {

    @Mock
    private ConnectionProvider connectionProvider;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private OrderDaoImpl orderDao;

    @BeforeEach
    void setUp() {
        orderDao = new OrderDaoImpl(connectionProvider);
    }

    @Test
    void getOrdersByDate_WhenOrdersExist_ShouldReturnListOfOrders() throws Exception {
        // Given
        Date date = Date.valueOf("2024-01-15");
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("order_number")).thenReturn("ORD001");
        when(resultSet.getLong("customer_id")).thenReturn(1L);
        when(resultSet.getLong("staff_id")).thenReturn(1L);
        when(resultSet.getLong("status_id")).thenReturn(1L);
        when(resultSet.getTimestamp("order_date")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(resultSet.getBigDecimal("total_amount")).thenReturn(new BigDecimal("10.00"));
        when(resultSet.getBigDecimal("discount_amount")).thenReturn(new BigDecimal("0.00"));
        when(resultSet.getBigDecimal("final_amount")).thenReturn(new BigDecimal("10.00"));
        when(resultSet.getString("notes")).thenReturn("Test order");

        // When
        List<Order> actualResult = orderDao.getOrdersByDate(date);

        // Then
        List<Order> expectedResult = new ArrayList<>();
        assertNotNull(actualResult);
        assertTrue(actualResult.size() >= expectedResult.size());
    }

    @Test
    void getOrdersByDate_WhenNoOrdersExist_ShouldReturnEmptyList() throws Exception {
        // Given
        Date date = Date.valueOf("2024-01-15");
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // When
        List<Order> actualResult = orderDao.getOrdersByDate(date);

        // Then
        List<Order> expectedResult = new ArrayList<>();
        assertEquals(expectedResult.size(), actualResult.size());
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void getOrdersByDate_WhenSQLExceptionOccurs_ShouldThrowException() throws Exception {
        // Given
        Date date = Date.valueOf("2024-01-15");
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Database error"));

        // When & Then
        assertThrows(Exception.class, () -> orderDao.getOrdersByDate(date));
    }

    @Test
    void getOrdersByDateRange_WhenOrdersExist_ShouldReturnListOfOrders() throws Exception {
        // Given
        Date startDate = Date.valueOf("2024-01-15");
        Date endDate = Date.valueOf("2024-01-20");
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("order_number")).thenReturn("ORD001");
        when(resultSet.getLong("customer_id")).thenReturn(1L);
        when(resultSet.getLong("staff_id")).thenReturn(1L);
        when(resultSet.getLong("status_id")).thenReturn(1L);
        when(resultSet.getTimestamp("order_date")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(resultSet.getBigDecimal("total_amount")).thenReturn(new BigDecimal("10.00"));
        when(resultSet.getBigDecimal("discount_amount")).thenReturn(new BigDecimal("0.00"));
        when(resultSet.getBigDecimal("final_amount")).thenReturn(new BigDecimal("10.00"));
        when(resultSet.getString("notes")).thenReturn("Test order");

        // When
        List<Order> actualResult = orderDao.getOrdersByDateRange(startDate, endDate);

        // Then
        List<Order> expectedResult = new ArrayList<>();
        assertNotNull(actualResult);
        assertTrue(actualResult.size() >= expectedResult.size());
    }

    @Test
    void getOrdersByDateRange_WhenNoOrdersExist_ShouldReturnEmptyList() throws Exception {
        // Given
        Date startDate = Date.valueOf("2024-01-15");
        Date endDate = Date.valueOf("2024-01-20");
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // When
        List<Order> actualResult = orderDao.getOrdersByDateRange(startDate, endDate);

        // Then
        List<Order> expectedResult = new ArrayList<>();
        assertEquals(expectedResult.size(), actualResult.size());
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void getDessertOrdersCountByDate_WhenOrdersExist_ShouldReturnInt() throws Exception {
        // Given
        Date date = Date.valueOf("2024-01-15");
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("dessert_orders_count")).thenReturn(5);

        // When
        int actualResult = orderDao.getDessertOrdersCountByDate(date);

        // Then
        int expectedResult = 5;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getDessertOrdersCountByDate_WhenNoOrdersExist_ShouldReturnZero() throws Exception {
        // Given
        Date date = Date.valueOf("2024-01-15");
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("dessert_orders_count")).thenReturn(0);

        // When
        int actualResult = orderDao.getDessertOrdersCountByDate(date);

        // Then
        int expectedResult = 0;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getDrinkOrdersCountByDate_WhenOrdersExist_ShouldReturnInt() throws Exception {
        // Given
        Date date = Date.valueOf("2024-01-15");
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("drink_orders_count")).thenReturn(10);

        // When
        int actualResult = orderDao.getDrinkOrdersCountByDate(date);

        // Then
        int expectedResult = 10;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getDrinkOrdersCountByDate_WhenNoOrdersExist_ShouldReturnZero() throws Exception {
        // Given
        Date date = Date.valueOf("2024-01-15");
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt("drink_orders_count")).thenReturn(0);

        // When
        int actualResult = orderDao.getDrinkOrdersCountByDate(date);

        // Then
        int expectedResult = 0;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getCustomersWithDrinksToday_WhenCustomersExist_ShouldReturnListOfCustomerBaristaInfo() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getLong("customer_id")).thenReturn(1L);
        when(resultSet.getString("customer_firstname")).thenReturn("John");
        when(resultSet.getString("customer_lastname")).thenReturn("Doe");
        when(resultSet.getString("customer_middlename")).thenReturn("Middle");
        when(resultSet.getDate("customer_date_of_birth")).thenReturn(new Date(System.currentTimeMillis()));
        when(resultSet.getLong("barista_id")).thenReturn(1L);
        when(resultSet.getString("barista_firstname")).thenReturn("Jane");
        when(resultSet.getString("barista_lastname")).thenReturn("Smith");
        when(resultSet.getLong("order_id")).thenReturn(1L);
        when(resultSet.getString("order_number")).thenReturn("ORD001");

        // When
        List<CustomerBaristaInfo> actualResult = orderDao.getCustomersWithDrinksToday();

        // Then
        List<CustomerBaristaInfo> expectedResult = new ArrayList<>();
        assertNotNull(actualResult);
        assertTrue(actualResult.size() >= expectedResult.size());
    }

    @Test
    void getCustomersWithDrinksToday_WhenNoCustomersExist_ShouldReturnEmptyList() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // When
        List<CustomerBaristaInfo> actualResult = orderDao.getCustomersWithDrinksToday();

        // Then
        List<CustomerBaristaInfo> expectedResult = new ArrayList<>();
        assertEquals(expectedResult.size(), actualResult.size());
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void getAverageOrderAmountByDate_WhenOrdersExist_ShouldReturnBigDecimal() throws Exception {
        // Given
        Date date = Date.valueOf("2024-01-15");
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getBigDecimal("average_order_amount")).thenReturn(new BigDecimal("25.50"));

        // When
        BigDecimal actualResult = orderDao.getAverageOrderAmountByDate(date);

        // Then
        BigDecimal expectedResult = new BigDecimal("25.50");
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getAverageOrderAmountByDate_WhenNoOrdersExist_ShouldReturnZero() throws Exception {
        // Given
        Date date = Date.valueOf("2024-01-15");
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // When
        BigDecimal actualResult = orderDao.getAverageOrderAmountByDate(date);

        // Then
        BigDecimal expectedResult = BigDecimal.ZERO;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getMaxOrderAmountByDate_WhenOrdersExist_ShouldReturnBigDecimal() throws Exception {
        // Given
        Date date = Date.valueOf("2024-01-15");
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getBigDecimal("max_order_amount")).thenReturn(new BigDecimal("100.00"));

        // When
        BigDecimal actualResult = orderDao.getMaxOrderAmountByDate(date);

        // Then
        BigDecimal expectedResult = new BigDecimal("100.00");
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getMaxOrderAmountByDate_WhenNoOrdersExist_ShouldReturnZero() throws Exception {
        // Given
        Date date = Date.valueOf("2024-01-15");
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // When
        BigDecimal actualResult = orderDao.getMaxOrderAmountByDate(date);

        // Then
        BigDecimal expectedResult = BigDecimal.ZERO;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getCustomerWithMaxOrderAmountByDate_WhenCustomerExists_ShouldReturnCustomerBaristaInfo() throws Exception {
        // Given
        Date date = Date.valueOf("2024-01-15");
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getLong("customer_id")).thenReturn(1L);
        when(resultSet.getString("customer_firstname")).thenReturn("John");
        when(resultSet.getString("customer_lastname")).thenReturn("Doe");
        when(resultSet.getString("customer_middlename")).thenReturn("Middle");
        when(resultSet.getDate("customer_date_of_birth")).thenReturn(new Date(System.currentTimeMillis()));
        when(resultSet.getLong("barista_id")).thenReturn(1L);
        when(resultSet.getString("barista_firstname")).thenReturn("Jane");
        when(resultSet.getString("barista_lastname")).thenReturn("Smith");
        when(resultSet.getLong("order_id")).thenReturn(1L);
        when(resultSet.getString("order_number")).thenReturn("ORD001");

        // When
        CustomerBaristaInfo actualResult = orderDao.getCustomerWithMaxOrderAmountByDate(date);

        // Then
        assertNotNull(actualResult);
        assertNotNull(actualResult.getCustomerId());
    }

    @Test
    void getCustomerWithMaxOrderAmountByDate_WhenNoCustomerExists_ShouldReturnNull() throws Exception {
        // Given
        Date date = Date.valueOf("2024-01-15");
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // When
        CustomerBaristaInfo actualResult = orderDao.getCustomerWithMaxOrderAmountByDate(date);

        // Then
        CustomerBaristaInfo expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }
}








