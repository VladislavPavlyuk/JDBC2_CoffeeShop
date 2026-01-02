package org.example.dao.base;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import org.example.dao.ConnectionProvider;
import org.example.dao.exception.DaoException;
import org.example.dao.mapper.ResultSetMapper;
import org.example.model.Staff;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AbstractDaoTest {

    @Mock
    private ConnectionProvider connectionProvider;

    @Mock
    private ResultSetMapper<Staff> mapper;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private ResultSet generatedKeys;

    private TestDao testDao;

    private static class TestDao extends AbstractDao<Staff, Long> {
        public TestDao(ConnectionProvider connectionProvider, ResultSetMapper<Staff> mapper) {
            super(connectionProvider, mapper);
        }

        @Override
        protected String getInsertSql() {
            return "INSERT INTO staff(shift_id, firstname, lastname) VALUES(?,?,?)";
        }

        @Override
        protected String getUpdateSql() {
            return "UPDATE staff SET shift_id=?, firstname=?, lastname=? WHERE id=?";
        }

        @Override
        protected String getFindAllSql() {
            return "SELECT * FROM staff";
        }

        @Override
        protected String getFindByIdSql() {
            return "SELECT * FROM staff WHERE id=?";
        }

        @Override
        protected String getDeleteByIdSql() {
            return "DELETE FROM staff WHERE id=?";
        }

        @Override
        protected String getDeleteAllSql() {
            return "DELETE FROM staff";
        }

        @Override
        protected void setInsertParameters(PreparedStatement ps, Staff entity) throws SQLException {
            ps.setLong(1, entity.getShift_Id());
            ps.setString(2, entity.getFirstName());
            ps.setString(3, entity.getLastName());
        }

        @Override
        protected void setUpdateParameters(PreparedStatement ps, Staff entity) throws SQLException {
            ps.setLong(1, entity.getShift_Id());
            ps.setString(2, entity.getFirstName());
            ps.setString(3, entity.getLastName());
            ps.setLong(4, entity.getId());
        }

        @Override
        protected void setId(Staff entity, Long id) {
            entity.setId(id);
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        testDao = new TestDao(connectionProvider, mapper);
        lenient().when(connectionProvider.getConnection()).thenReturn(connection);
        lenient().when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        lenient().when(connection.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS)))
                .thenReturn(preparedStatement);
    }

    @Test
    void save_WhenSaveSuccessful_ShouldReturnStaffWithId() throws Exception {
        // Given
        Long expectedId = 100L;
        Staff staff = Staff.builder()
                .shift_Id(1L)
                .firstName("Иван")
                .lastName("Петров")
                .build();

        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getLong(1)).thenReturn(expectedId);

        // When
        Staff actualResult = testDao.save(staff);

        // Then
        assertNotNull(actualResult);
        assertEquals(expectedId, actualResult.getId());
    }

    @Test
    void save_WhenSQLExceptionOccurs_ShouldThrowDaoException() throws Exception {
        // Given
        Staff staff = Staff.builder()
                .shift_Id(1L)
                .firstName("Иван")
                .lastName("Петров")
                .build();

        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("SQL error"));

        // When
        Exception actualResult = null;
        try {
            testDao.save(staff);
        } catch (DaoException e) {
            actualResult = e;
        }

        // Then
        assertNotNull(actualResult);
        assertTrue(actualResult instanceof DaoException);
    }

    @Test
    void findAll_WhenDataExists_ShouldReturnListOfStaff() throws Exception {
        // Given
        Staff staff1 = Staff.builder().id(1L).firstName("Иван").lastName("Петров").build();
        Staff staff2 = Staff.builder().id(2L).firstName("Мария").lastName("Сидорова").build();

        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(mapper.map(resultSet)).thenReturn(staff1, staff2);

        // When
        List<Staff> actualResult = testDao.findAll();

        // Then
        int expectedSize = 2;
        assertNotNull(actualResult);
        assertEquals(expectedSize, actualResult.size());
    }

    @Test
    void findAll_WhenNoDataExists_ShouldReturnEmptyList() throws Exception {
        // Given
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // When
        List<Staff> actualResult = testDao.findAll();

        // Then
        List<Staff> expectedResult = List.of();
        assertNotNull(actualResult);
        assertEquals(expectedResult.size(), actualResult.size());
        assertTrue(actualResult.isEmpty());
    }

    @Test
    void findById_WhenStaffExists_ShouldReturnOptionalStaff() throws Exception {
        // Given
        Long staffId = 1L;
        Staff expectedStaff = Staff.builder().id(staffId).firstName("Иван").lastName("Петров").build();

        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(mapper.map(resultSet)).thenReturn(expectedStaff);

        // When
        Optional<Staff> actualResult = testDao.findById(staffId);

        // Then
        assertTrue(actualResult.isPresent());
        assertEquals(expectedStaff.getId(), actualResult.get().getId());
    }

    @Test
    void findById_WhenStaffNotExists_ShouldReturnEmptyOptional() throws Exception {
        // Given
        Long staffId = 999L;
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // When
        Optional<Staff> actualResult = testDao.findById(staffId);

        // Then
        Optional<Staff> expectedResult = Optional.empty();
        assertEquals(expectedResult.isPresent(), actualResult.isPresent());
        assertFalse(actualResult.isPresent());
    }

    @Test
    void update_WhenUpdateSuccessful_ShouldNotThrowException() throws Exception {
        // Given
        Staff staff = Staff.builder()
                .id(1L)
                .shift_Id(2L)
                .firstName("Иван")
                .lastName("Петров")
                .build();

        when(preparedStatement.executeUpdate()).thenReturn(1);

        // When
        Exception actualResult = null;
        try {
            testDao.update(staff);
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void deleteById_WhenDeleteSuccessful_ShouldNotThrowException() throws Exception {
        // Given
        Long staffId = 1L;
        when(preparedStatement.executeUpdate()).thenReturn(1);

        // When
        Exception actualResult = null;
        try {
            testDao.deleteById(staffId);
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void deleteAll_WhenDeleteSuccessful_ShouldNotThrowException() throws Exception {
        // Given
        when(preparedStatement.executeUpdate()).thenReturn(10);

        // When
        Exception actualResult = null;
        try {
            testDao.deleteAll();
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void saveAll_WhenSaveSuccessful_ShouldReturnListWithIds() throws Exception {
        // Given
        Long expectedId1 = 100L;
        Long expectedId2 = 101L;
        Staff staff1 = Staff.builder().shift_Id(1L).firstName("Иван").lastName("Петров").build();
        Staff staff2 = Staff.builder().shift_Id(2L).firstName("Мария").lastName("Сидорова").build();
        List<Staff> staffList = List.of(staff1, staff2);

        when(preparedStatement.executeBatch()).thenReturn(new int[]{1, 1});
        when(preparedStatement.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(true, true, false);
        when(generatedKeys.getLong(1)).thenReturn(expectedId1, expectedId2);

        // When
        List<Staff> actualResult = testDao.saveAll(staffList);

        // Then
        int expectedSize = 2;
        assertNotNull(actualResult);
        assertEquals(expectedSize, actualResult.size());
        assertEquals(expectedId1, actualResult.get(0).getId());
        assertEquals(expectedId2, actualResult.get(1).getId());
    }

    @Test
    void saveAll_WhenInputIsEmpty_ShouldReturnEmptyList() {
        // Given
        List<Staff> emptyList = List.of();

        // When
        List<Staff> actualResult = testDao.saveAll(emptyList);

        // Then
        List<Staff> expectedResult = List.of();
        assertNotNull(actualResult);
        assertEquals(expectedResult.size(), actualResult.size());
        assertTrue(actualResult.isEmpty());
    }
}
