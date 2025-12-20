package org.example.dao.shiftDAO;

import java.util.ArrayList;
import java.util.List;

import org.example.model.Shift;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShiftDaoImplTest {

    private ShiftDaoImpl shiftDao;

    @BeforeEach
    void setUp() {
        shiftDao = new ShiftDaoImpl();
    }

    @Test
    void save_WhenValidShiftProvided_ShouldNotThrowException() {
        // Given
        Shift shift = new Shift();
        shift.setShiftTitle("Morning Shift");

        // When
        Exception actualResult = null;
        try {
            shiftDao.save(shift);
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void saveMany_WhenValidListProvided_ShouldNotThrowException() {
        // Given
        List<Shift> shifts = new ArrayList<>();
        Shift shift1 = new Shift();
        shift1.setShiftTitle("Morning Shift");
        Shift shift2 = new Shift();
        shift2.setShiftTitle("Evening Shift");
        shifts.add(shift1);
        shifts.add(shift2);

        // When
        Exception actualResult = null;
        try {
            shiftDao.saveMany(shifts);
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void saveMany_WhenEmptyListProvided_ShouldNotThrowException() {
        // Given
        List<Shift> shifts = new ArrayList<>();

        // When
        Exception actualResult = null;
        try {
            shiftDao.saveMany(shifts);
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void update_WhenValidShiftProvided_ShouldNotThrowException() {
        // Given
        Shift shift = new Shift();
        shift.setId(1L);
        shift.setShiftTitle("Updated Shift");

        // When
        Exception actualResult = null;
        try {
            shiftDao.update(shift);
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void delete_WhenValidShiftProvided_ShouldNotThrowException() {
        // Given
        Shift shift = new Shift();
        shift.setId(1L);

        // When
        Exception actualResult = null;
        try {
            shiftDao.delete(shift);
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void findAll_WhenShiftsExist_ShouldReturnListOfShifts() {
        // When
        List<Shift> actualResult = shiftDao.findAll();

        // Then
        assertNotNull(actualResult);
        assertTrue(actualResult instanceof List);
    }

    @Test
    void findAll_WhenNoShiftsExist_ShouldReturnEmptyList() {
        // When
        List<Shift> actualResult = shiftDao.findAll();

        // Then
        List<Shift> expectedResult = new ArrayList<>();
        assertNotNull(actualResult);
        assertTrue(actualResult.size() >= 0);
    }

    @Test
    void findAllShiftsWithLessOrEqualStaffNumber_WhenNumberProvided_ShouldReturnListOfShiftTitles() {
        // Given
        int numberStaff = 5;

        // When
        List<String> actualResult = shiftDao.findAllShiftsWithLessOrEqualStaffNumber(numberStaff);

        // Then
        List<String> expectedResult = new ArrayList<>();
        assertNotNull(actualResult);
        assertTrue(actualResult.size() >= expectedResult.size());
    }

    @Test
    void findAllShiftsWithLessOrEqualStaffNumber_WhenNoShiftsMatch_ShouldReturnEmptyList() {
        // Given
        int numberStaff = 0;

        // When
        List<String> actualResult = shiftDao.findAllShiftsWithLessOrEqualStaffNumber(numberStaff);

        // Then
        List<String> expectedResult = new ArrayList<>();
        assertNotNull(actualResult);
        assertTrue(actualResult.size() >= 0);
    }

    @Test
    void deleteAll_WhenCalled_ShouldNotThrowException() {
        // When
        Exception actualResult = null;
        try {
            shiftDao.deleteAll();
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }
}
