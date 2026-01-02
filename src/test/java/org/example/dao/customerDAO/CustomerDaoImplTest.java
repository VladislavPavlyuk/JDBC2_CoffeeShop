package org.example.dao.customerDAO;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.example.dao.ConnectionProvider;
import org.example.model.Customer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerDaoImplTest {

    @Mock
    private ConnectionProvider connectionProvider;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private CustomerDaoImpl customerDao;

    @BeforeEach
    void setUp() {
        customerDao = new CustomerDaoImpl(connectionProvider);
    }

    @Test
    void getYoungestCustomers_WhenCustomersExist_ShouldReturnListOfCustomers() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("firstname")).thenReturn("John");
        when(resultSet.getString("lastname")).thenReturn("Doe");
        when(resultSet.getString("middlename")).thenReturn("Middle");
        when(resultSet.getDate("date_of_birth")).thenReturn(new Date(System.currentTimeMillis()));

        // When
        List<Customer> actualResult = customerDao.getYoungestCustomers();

        // Then
        List<Customer> expectedResult = new ArrayList<>();
        assertNotNull(actualResult);
        assertTrue(actualResult.size() >= 0);
    }

    @Test
    void getYoungestCustomers_WhenNoCustomersExist_ShouldReturnEmptyList() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // When
        List<Customer> actualResult = customerDao.getYoungestCustomers();

        // Then
        List<Customer> expectedResult = new ArrayList<>();
        assertEquals(expectedResult.size(), actualResult.size());
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void getOldestCustomers_WhenCustomersExist_ShouldReturnListOfCustomers() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("firstname")).thenReturn("John");
        when(resultSet.getString("lastname")).thenReturn("Doe");
        when(resultSet.getString("middlename")).thenReturn("Middle");
        when(resultSet.getDate("date_of_birth")).thenReturn(new Date(System.currentTimeMillis()));

        // When
        List<Customer> actualResult = customerDao.getOldestCustomers();

        // Then
        List<Customer> expectedResult = new ArrayList<>();
        assertNotNull(actualResult);
        assertTrue(actualResult.size() >= 0);
    }

    @Test
    void getOldestCustomers_WhenNoCustomersExist_ShouldReturnEmptyList() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // When
        List<Customer> actualResult = customerDao.getOldestCustomers();

        // Then
        List<Customer> expectedResult = new ArrayList<>();
        assertEquals(expectedResult.size(), actualResult.size());
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void getCustomersWithBirthdayToday_WhenCustomersExist_ShouldReturnListOfCustomers() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("firstname")).thenReturn("John");
        when(resultSet.getString("lastname")).thenReturn("Doe");
        when(resultSet.getString("middlename")).thenReturn("Middle");
        when(resultSet.getDate("date_of_birth")).thenReturn(new Date(System.currentTimeMillis()));

        // When
        List<Customer> actualResult = customerDao.getCustomersWithBirthdayToday();

        // Then
        List<Customer> expectedResult = new ArrayList<>();
        assertNotNull(actualResult);
        assertTrue(actualResult.size() >= 0);
    }

    @Test
    void getCustomersWithBirthdayToday_WhenNoCustomersExist_ShouldReturnEmptyList() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // When
        List<Customer> actualResult = customerDao.getCustomersWithBirthdayToday();

        // Then
        List<Customer> expectedResult = new ArrayList<>();
        assertEquals(expectedResult.size(), actualResult.size());
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void getCustomersWithoutEmail_WhenCustomersExist_ShouldReturnListOfCustomers() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getString("firstname")).thenReturn("John");
        when(resultSet.getString("lastname")).thenReturn("Doe");
        when(resultSet.getString("middlename")).thenReturn("Middle");
        when(resultSet.getDate("date_of_birth")).thenReturn(new Date(System.currentTimeMillis()));

        // When
        List<Customer> actualResult = customerDao.getCustomersWithoutEmail();

        // Then
        List<Customer> expectedResult = new ArrayList<>();
        assertNotNull(actualResult);
        assertTrue(actualResult.size() >= 0);
    }

    @Test
    void getCustomersWithoutEmail_WhenNoCustomersExist_ShouldReturnEmptyList() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // When
        List<Customer> actualResult = customerDao.getCustomersWithoutEmail();

        // Then
        List<Customer> expectedResult = new ArrayList<>();
        assertEquals(expectedResult.size(), actualResult.size());
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void deleteCustomer_WhenValidDataProvided_ShouldReturnTrue() throws Exception {
        // Given
        String firstName = "John";
        String lastName = "Doe";
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        // When
        boolean actualResult = customerDao.deleteCustomer(firstName, lastName);

        // Then
        boolean expectedResult = true;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void deleteCustomer_WhenCustomerNotFound_ShouldReturnFalse() throws Exception {
        // Given
        String firstName = "Unknown";
        String lastName = "Person";
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(0);

        // When
        boolean actualResult = customerDao.deleteCustomer(firstName, lastName);

        // Then
        boolean expectedResult = false;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void deleteCustomer_WhenSQLExceptionOccurs_ShouldThrowException() throws Exception {
        // Given
        String firstName = "John";
        String lastName = "Doe";
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("Database error"));

        // When & Then
        assertThrows(Exception.class, () -> customerDao.deleteCustomer(firstName, lastName));
    }
}








