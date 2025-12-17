package org.example.dao.staffAndCoffeeshopDAO;

import org.example.model.StaffToCoffeeshop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StaffToCoffeeshopDaoImplTest {

    private StaffToCoffeeshopDaoImpl staffToCoffeeshopDao;

    @BeforeEach
    void setUp() {
        staffToCoffeeshopDao = new StaffToCoffeeshopDaoImpl();
    }

    @Test
    void save_WhenValidStaffToCoffeeshopProvided_ShouldNotThrowException() {
        // Given
        StaffToCoffeeshop staffToCoffeeshop = new StaffToCoffeeshop();
        staffToCoffeeshop.setStaff_Id(1L);
        staffToCoffeeshop.setCoffeeshop_Id(1L);

        // When
        Exception actualResult = null;
        try {
            staffToCoffeeshopDao.save(staffToCoffeeshop);
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void save_WhenDuplicateStaffToCoffeeshopProvided_ShouldNotThrowException() {
        // Given
        StaffToCoffeeshop staffToCoffeeshop = new StaffToCoffeeshop();
        staffToCoffeeshop.setStaff_Id(1L);
        staffToCoffeeshop.setCoffeeshop_Id(1L);

        // When
        Exception actualResult = null;
        try {
            staffToCoffeeshopDao.save(staffToCoffeeshop);
            staffToCoffeeshopDao.save(staffToCoffeeshop); // Попытка сохранить дубликат
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void assignStaffToCoffeeshop_WhenValidStaffIdAndTitleProvided_ShouldNotThrowException() {
        // Given
        long staffId = 1L;
        String coffeeshopTitle = "Test Coffee Shop";

        // When
        Exception actualResult = null;
        try {
            staffToCoffeeshopDao.assignStaffToCoffeeshop(staffId, coffeeshopTitle);
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void assignStaffToCoffeeshop_WhenNonExistentCoffeeshopTitleProvided_ShouldNotThrowException() {
        // Given
        long staffId = 1L;
        String coffeeshopTitle = "Non-existent Shop";

        // When
        Exception actualResult = null;
        try {
            staffToCoffeeshopDao.assignStaffToCoffeeshop(staffId, coffeeshopTitle);
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void deleteCoffeeshopFromStaff_WhenValidStaffIdAndTitleProvided_ShouldNotThrowException() {
        // Given
        long staffId = 1L;
        String coffeeshopTitle = "Test Coffee Shop";

        // When
        Exception actualResult = null;
        try {
            staffToCoffeeshopDao.deleteCoffeeshopFromStaff(staffId, coffeeshopTitle);
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void deleteCoffeeshopFromStaff_WhenNonExistentRelationProvided_ShouldNotThrowException() {
        // Given
        long staffId = 999999L;
        String coffeeshopTitle = "Non-existent Shop";

        // When
        Exception actualResult = null;
        try {
            staffToCoffeeshopDao.deleteCoffeeshopFromStaff(staffId, coffeeshopTitle);
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void deleteAll_WhenCalled_ShouldNotThrowException() {
        // When
        Exception actualResult = null;
        try {
            staffToCoffeeshopDao.deleteAll();
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }
}
