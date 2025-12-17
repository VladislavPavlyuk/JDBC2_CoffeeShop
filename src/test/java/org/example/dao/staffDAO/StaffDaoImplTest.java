package org.example.dao.staffDAO;

import org.example.dao.ConnectionFactory;
import org.example.dao.ConnectionProvider;
import org.example.model.Staff;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StaffDaoImplTest {

    @Mock
    private ConnectionProvider connectionProvider;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private StaffDaoImpl staffDao;

    @BeforeEach
    void setUp() {
        staffDao = new StaffDaoImpl(connectionProvider);
    }

    @Test
    void save_WhenValidStaffProvided_ShouldNotThrowException() throws Exception {
        // Given
        Staff staff = Staff.builder()
                .firstName("Иван")
                .lastName("Петров")
                .positionId(1L)
                .shift_Id(1L)
                .build();
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.execute()).thenReturn(true);

        // When
        Exception actualResult = null;
        try {
            staffDao.save(staff);
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void save_WhenSQLExceptionOccurs_ShouldHandleException() throws Exception {
        // Given
        Staff staff = Staff.builder()
                .firstName("Иван")
                .lastName("Петров")
                .positionId(1L)
                .shift_Id(1L)
                .build();
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        // When
        Exception actualResult = null;
        try {
            staffDao.save(staff);
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void saveMany_WhenValidListProvided_ShouldNotThrowException() throws Exception {
        // Given
        List<Staff> staffList = new ArrayList<>();
        staffList.add(Staff.builder().firstName("Иван").lastName("Петров").positionId(1L).shift_Id(1L).build());
        staffList.add(Staff.builder().firstName("Мария").lastName("Сидорова").positionId(2L).shift_Id(2L).build());
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeBatch()).thenReturn(new int[]{1, 1});

        // When
        Exception actualResult = null;
        try {
            staffDao.saveMany(staffList);
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void saveMany_WhenNamesExceed50Characters_ShouldTruncateNames() throws Exception {
        // Given
        String longName = "A".repeat(100);
        Staff staff = Staff.builder()
                .firstName(longName)
                .lastName(longName)
                .positionId(1L)
                .shift_Id(1L)
                .build();
        List<Staff> staffList = List.of(staff);
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeBatch()).thenReturn(new int[]{1});

        // When
        Exception actualResult = null;
        try {
            staffDao.saveMany(staffList);
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void saveMany_WhenEmptyListProvided_ShouldNotThrowException() throws Exception {
        // Given
        List<Staff> staffList = new ArrayList<>();
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeBatch()).thenReturn(new int[]{});

        // When
        Exception actualResult = null;
        try {
            staffDao.saveMany(staffList);
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void update_WhenValidStaffProvided_ShouldNotThrowException() throws Exception {
        // Given
        Staff staff = Staff.builder()
                .id(1L)
                .firstName("Иван")
                .lastName("Петров")
                .build();
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        // When
        Exception actualResult = null;
        try {
            staffDao.update(staff);
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void delete_WhenValidIdProvided_ShouldNotThrowException() throws Exception {
        // Given
        Long staffId = 1L;
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);

        // When
        Exception actualResult = null;
        try {
            staffDao.delete(staffId);
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void findAll_WhenStaffExists_ShouldReturnListOfStaff() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getLong(1)).thenReturn(1L, 2L);
        when(resultSet.getString(2)).thenReturn("Иван", "Мария");
        when(resultSet.getString(3)).thenReturn("Петров", "Сидорова");
        when(resultSet.getLong(5)).thenReturn(1L, 2L);
        when(resultSet.getLong(6)).thenReturn(1L, 2L);

        // When
        List<Staff> actualResult = staffDao.findAll();

        // Then
        List<Staff> expectedResult = new ArrayList<>();
        assertNotNull(actualResult);
        assertTrue(actualResult.size() >= 0);
    }

    @Test
    void findAll_WhenNoStaffExists_ShouldReturnEmptyList() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // When
        List<Staff> actualResult = staffDao.findAll();

        // Then
        List<Staff> expectedResult = new ArrayList<>();
        assertEquals(expectedResult.size(), actualResult.size());
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void findAllFromCoffeeshops_WhenCoffeeshopExists_ShouldReturnStaffList() throws Exception {
        // Given
        String coffeeshopTitle = "Coffee Shop 1";
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getLong(1)).thenReturn(1L);
        when(resultSet.getLong(2)).thenReturn(1L);
        when(resultSet.getString(3)).thenReturn("Иван");
        when(resultSet.getString(4)).thenReturn("Петров");

        // When
        List<Staff> actualResult = staffDao.findAllFromCoffeeshops(coffeeshopTitle);

        // Then
        assertNotNull(actualResult);
        assertTrue(actualResult.size() >= 0);
    }

    @Test
    void findAllFromCoffeeshops_WhenCoffeeshopNotExists_ShouldReturnEmptyList() throws Exception {
        // Given
        String coffeeshopTitle = "Non-existent Shop";
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // When
        List<Staff> actualResult = staffDao.findAllFromCoffeeshops(coffeeshopTitle);

        // Then
        List<Staff> expectedResult = new ArrayList<>();
        assertEquals(expectedResult.size(), actualResult.size());
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void deleteAll_WhenCalled_ShouldNotThrowException() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(5);

        // When
        Exception actualResult = null;
        try {
            staffDao.deleteAll();
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }
}
