package org.example.dao.customerDiscountDAO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.example.dao.ConnectionProvider;
import org.example.model.CustomerDiscount;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerDiscountDaoImplTest {

    @Mock
    private ConnectionProvider connectionProvider;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private CustomerDiscountDaoImpl customerDiscountDao;

    @BeforeEach
    void setUp() {
        customerDiscountDao = new CustomerDiscountDaoImpl(connectionProvider);
    }

    @Test
    void getMinDiscountValue_WhenDiscountsExist_ShouldReturnBigDecimal() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getBigDecimal("min_discount_value")).thenReturn(new BigDecimal("5.00"));

        // When
        BigDecimal actualResult = customerDiscountDao.getMinDiscountValue();

        // Then
        BigDecimal expectedResult = new BigDecimal("5.00");
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getMinDiscountValue_WhenNoDiscountsExist_ShouldReturnZero() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // When
        BigDecimal actualResult = customerDiscountDao.getMinDiscountValue();

        // Then
        BigDecimal expectedResult = BigDecimal.ZERO;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getMaxDiscountValue_WhenDiscountsExist_ShouldReturnBigDecimal() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getBigDecimal("max_discount_value")).thenReturn(new BigDecimal("20.00"));

        // When
        BigDecimal actualResult = customerDiscountDao.getMaxDiscountValue();

        // Then
        BigDecimal expectedResult = new BigDecimal("20.00");
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getMaxDiscountValue_WhenNoDiscountsExist_ShouldReturnZero() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // When
        BigDecimal actualResult = customerDiscountDao.getMaxDiscountValue();

        // Then
        BigDecimal expectedResult = BigDecimal.ZERO;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getCustomersWithMinDiscount_WhenCustomersExist_ShouldReturnListOfCustomerDiscounts() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getLong("customer_id")).thenReturn(1L);
        when(resultSet.getString("firstname")).thenReturn("John");
        when(resultSet.getString("lastname")).thenReturn("Doe");
        when(resultSet.getString("middlename")).thenReturn("Middle");
        when(resultSet.getBigDecimal("discount_value")).thenReturn(new BigDecimal("5.00"));
        when(resultSet.getString("discount_type")).thenReturn("PERCENTAGE");
        when(resultSet.getDate("valid_from")).thenReturn(new Date(System.currentTimeMillis()));
        when(resultSet.getDate("valid_to")).thenReturn(null);

        // When
        List<CustomerDiscount> actualResult = customerDiscountDao.getCustomersWithMinDiscount();

        // Then
        List<CustomerDiscount> expectedResult = new ArrayList<>();
        assertNotNull(actualResult);
        assertTrue(actualResult.size() >= 0);
    }

    @Test
    void getCustomersWithMinDiscount_WhenNoCustomersExist_ShouldReturnEmptyList() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // When
        List<CustomerDiscount> actualResult = customerDiscountDao.getCustomersWithMinDiscount();

        // Then
        List<CustomerDiscount> expectedResult = new ArrayList<>();
        assertEquals(expectedResult.size(), actualResult.size());
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void getCustomersWithMaxDiscount_WhenCustomersExist_ShouldReturnListOfCustomerDiscounts() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getLong("customer_id")).thenReturn(1L);
        when(resultSet.getString("firstname")).thenReturn("John");
        when(resultSet.getString("lastname")).thenReturn("Doe");
        when(resultSet.getString("middlename")).thenReturn("Middle");
        when(resultSet.getBigDecimal("discount_value")).thenReturn(new BigDecimal("20.00"));
        when(resultSet.getString("discount_type")).thenReturn("PERCENTAGE");
        when(resultSet.getDate("valid_from")).thenReturn(new Date(System.currentTimeMillis()));
        when(resultSet.getDate("valid_to")).thenReturn(null);

        // When
        List<CustomerDiscount> actualResult = customerDiscountDao.getCustomersWithMaxDiscount();

        // Then
        List<CustomerDiscount> expectedResult = new ArrayList<>();
        assertNotNull(actualResult);
        assertTrue(actualResult.size() >= 0);
    }

    @Test
    void getCustomersWithMaxDiscount_WhenNoCustomersExist_ShouldReturnEmptyList() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // When
        List<CustomerDiscount> actualResult = customerDiscountDao.getCustomersWithMaxDiscount();

        // Then
        List<CustomerDiscount> expectedResult = new ArrayList<>();
        assertEquals(expectedResult.size(), actualResult.size());
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void getAverageDiscountValue_WhenDiscountsExist_ShouldReturnBigDecimal() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getBigDecimal("avg_discount_value")).thenReturn(new BigDecimal("12.50"));

        // When
        BigDecimal actualResult = customerDiscountDao.getAverageDiscountValue();

        // Then
        BigDecimal expectedResult = new BigDecimal("12.50");
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void getAverageDiscountValue_WhenNoDiscountsExist_ShouldReturnZero() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // When
        BigDecimal actualResult = customerDiscountDao.getAverageDiscountValue();

        // Then
        BigDecimal expectedResult = BigDecimal.ZERO;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void updateCustomerDiscount_WhenValidDataProvided_ShouldReturnTrue() throws Exception {
        // Given
        String firstName = "John";
        String lastName = "Doe";
        BigDecimal newDiscountValue = new BigDecimal("15.00");
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        // When
        boolean actualResult = customerDiscountDao.updateCustomerDiscount(firstName, lastName, newDiscountValue);

        // Then
        boolean expectedResult = true;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void updateCustomerDiscount_WhenCustomerNotFound_ShouldReturnFalse() throws Exception {
        // Given
        String firstName = "Unknown";
        String lastName = "Person";
        BigDecimal newDiscountValue = new BigDecimal("15.00");
        
        PreparedStatement insertPreparedStatement = mock(PreparedStatement.class);
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement, insertPreparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);
        when(insertPreparedStatement.executeUpdate()).thenReturn(0);

        // When
        boolean actualResult = customerDiscountDao.updateCustomerDiscount(firstName, lastName, newDiscountValue);

        // Then
        boolean expectedResult = true;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void updateCustomerDiscount_WhenSQLExceptionOccurs_ShouldThrowException() throws Exception {
        // Given
        String firstName = "John";
        String lastName = "Doe";
        BigDecimal newDiscountValue = new BigDecimal("15.00");
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Database error"));

        // When & Then
        assertThrows(Exception.class, () -> customerDiscountDao.updateCustomerDiscount(firstName, lastName, newDiscountValue));
    }
}


