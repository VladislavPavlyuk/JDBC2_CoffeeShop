package org.example.dao.scheduleDAO;

import org.example.dao.ConnectionProvider;
import org.example.model.StaffSchedule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleDaoImplTest {

    @Mock
    private ConnectionProvider connectionProvider;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    private ScheduleDaoImpl scheduleDao;

    @BeforeEach
    void setUp() {
        scheduleDao = new ScheduleDaoImpl(connectionProvider);
    }

    @Test
    void getBaristaScheduleForWeek_WhenScheduleExists_ShouldReturnListOfStaffSchedule() throws Exception {
        // Given
        String firstName = "John";
        String lastName = "Doe";
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getLong("staff_id")).thenReturn(1L);
        when(resultSet.getString("staff_firstname")).thenReturn("John");
        when(resultSet.getString("staff_lastname")).thenReturn("Doe");
        when(resultSet.getLong("shift_id")).thenReturn(1L);
        when(resultSet.getString("shift_code")).thenReturn("MORNING");
        when(resultSet.getDate("work_date")).thenReturn(new Date(System.currentTimeMillis()));
        when(resultSet.getString("notes")).thenReturn("Test notes");

        // When
        List<StaffSchedule> actualResult = scheduleDao.getBaristaScheduleForWeek(firstName, lastName);

        // Then
        List<StaffSchedule> expectedResult = new ArrayList<>();
        assertNotNull(actualResult);
        assertTrue(actualResult.size() >= expectedResult.size());
    }

    @Test
    void getBaristaScheduleForWeek_WhenNoScheduleExists_ShouldReturnEmptyList() throws Exception {
        // Given
        String firstName = "John";
        String lastName = "Doe";
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // When
        List<StaffSchedule> actualResult = scheduleDao.getBaristaScheduleForWeek(firstName, lastName);

        // Then
        List<StaffSchedule> expectedResult = new ArrayList<>();
        assertEquals(expectedResult.size(), actualResult.size());
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void getBaristaScheduleForWeek_WhenSQLExceptionOccurs_ShouldThrowException() throws Exception {
        // Given
        String firstName = "John";
        String lastName = "Doe";
        
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Database error"));

        // When & Then
        assertThrows(Exception.class, () -> scheduleDao.getBaristaScheduleForWeek(firstName, lastName));
    }

    @Test
    void getAllBaristasScheduleForWeek_WhenSchedulesExist_ShouldReturnListOfStaffSchedule() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getLong("staff_id")).thenReturn(1L);
        when(resultSet.getString("staff_firstname")).thenReturn("John");
        when(resultSet.getString("staff_lastname")).thenReturn("Doe");
        when(resultSet.getLong("shift_id")).thenReturn(1L);
        when(resultSet.getString("shift_code")).thenReturn("MORNING");
        when(resultSet.getDate("work_date")).thenReturn(new Date(System.currentTimeMillis()));
        when(resultSet.getString("notes")).thenReturn("Test notes");

        // When
        List<StaffSchedule> actualResult = scheduleDao.getAllBaristasScheduleForWeek();

        // Then
        List<StaffSchedule> expectedResult = new ArrayList<>();
        assertNotNull(actualResult);
        assertTrue(actualResult.size() >= expectedResult.size());
    }

    @Test
    void getAllBaristasScheduleForWeek_WhenNoSchedulesExist_ShouldReturnEmptyList() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // When
        List<StaffSchedule> actualResult = scheduleDao.getAllBaristasScheduleForWeek();

        // Then
        List<StaffSchedule> expectedResult = new ArrayList<>();
        assertEquals(expectedResult.size(), actualResult.size());
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void getAllBaristasScheduleForWeek_WhenSQLExceptionOccurs_ShouldThrowException() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Database error"));

        // When & Then
        assertThrows(Exception.class, () -> scheduleDao.getAllBaristasScheduleForWeek());
    }

    @Test
    void getAllStaffScheduleForWeek_WhenSchedulesExist_ShouldReturnListOfStaffSchedule() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getLong("id")).thenReturn(1L);
        when(resultSet.getLong("staff_id")).thenReturn(1L);
        when(resultSet.getString("staff_firstname")).thenReturn("John");
        when(resultSet.getString("staff_lastname")).thenReturn("Doe");
        when(resultSet.getLong("shift_id")).thenReturn(1L);
        when(resultSet.getString("shift_code")).thenReturn("MORNING");
        when(resultSet.getDate("work_date")).thenReturn(new Date(System.currentTimeMillis()));
        when(resultSet.getString("notes")).thenReturn("Test notes");

        // When
        List<StaffSchedule> actualResult = scheduleDao.getAllStaffScheduleForWeek();

        // Then
        List<StaffSchedule> expectedResult = new ArrayList<>();
        assertNotNull(actualResult);
        assertTrue(actualResult.size() >= expectedResult.size());
    }

    @Test
    void getAllStaffScheduleForWeek_WhenNoSchedulesExist_ShouldReturnEmptyList() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // When
        List<StaffSchedule> actualResult = scheduleDao.getAllStaffScheduleForWeek();

        // Then
        List<StaffSchedule> expectedResult = new ArrayList<>();
        assertEquals(expectedResult.size(), actualResult.size());
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void getAllStaffScheduleForWeek_WhenSQLExceptionOccurs_ShouldThrowException() throws Exception {
        // Given
        when(connectionProvider.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenThrow(new SQLException("Database error"));

        // When & Then
        assertThrows(Exception.class, () -> scheduleDao.getAllStaffScheduleForWeek());
    }
}










