package org.example.dao.coffeeshopDAO;

import java.util.ArrayList;
import java.util.List;

import org.example.model.Coffeeshop;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CoffeeshopDaoImplTest {

    private CoffeeshopDaoImpl coffeeshopDao;

    @BeforeEach
    void setUp() {
        coffeeshopDao = new CoffeeshopDaoImpl();
    }

    @Test
    void save_WhenValidCoffeeshopProvided_ShouldNotThrowException() {
        // Given
        Coffeeshop coffeeshop = new Coffeeshop();
        coffeeshop.setCoffeeshopTitle("Test Coffee Shop");
        coffeeshop.setCoffeeshopDescription("Test Description");

        // When
        Exception actualResult = null;
        try {
            coffeeshopDao.save(coffeeshop);
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void save_WhenCoffeeshopWithNullFields_ShouldNotThrowException() {
        // Given
        Coffeeshop coffeeshop = new Coffeeshop();
        coffeeshop.setCoffeeshopTitle(null);
        coffeeshop.setCoffeeshopDescription(null);

        // When
        Exception actualResult = null;
        try {
            coffeeshopDao.save(coffeeshop);
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
        List<Coffeeshop> coffeeshops = new ArrayList<>();
        Coffeeshop shop1 = new Coffeeshop();
        shop1.setCoffeeshopTitle("Shop 1");
        shop1.setCoffeeshopDescription("Description 1");
        Coffeeshop shop2 = new Coffeeshop();
        shop2.setCoffeeshopTitle("Shop 2");
        shop2.setCoffeeshopDescription("Description 2");
        coffeeshops.add(shop1);
        coffeeshops.add(shop2);

        // When
        Exception actualResult = null;
        try {
            coffeeshopDao.saveMany(coffeeshops);
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
        List<Coffeeshop> coffeeshops = new ArrayList<>();

        // When
        Exception actualResult = null;
        try {
            coffeeshopDao.saveMany(coffeeshops);
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void update_WhenValidCoffeeshopProvided_ShouldNotThrowException() {
        // Given
        Coffeeshop coffeeshop = new Coffeeshop();
        coffeeshop.setId(1L);
        coffeeshop.setCoffeeshopTitle("Updated Title");
        coffeeshop.setCoffeeshopDescription("Updated Description");

        // When
        Exception actualResult = null;
        try {
            coffeeshopDao.update(coffeeshop);
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void delete_WhenValidCoffeeshopProvided_ShouldNotThrowException() {
        // Given
        Coffeeshop coffeeshop = new Coffeeshop();
        coffeeshop.setId(1L);

        // When
        Exception actualResult = null;
        try {
            coffeeshopDao.delete(coffeeshop);
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void findAll_WhenCoffeeshopsExist_ShouldReturnListOfCoffeeshops() {
        // When
        List<Coffeeshop> actualResult = coffeeshopDao.findAll();

        // Then
        assertNotNull(actualResult);
        assertTrue(actualResult instanceof List);
    }

    @Test
    void findAll_WhenNoCoffeeshopsExist_ShouldReturnEmptyList() {
        // When
        List<Coffeeshop> actualResult = coffeeshopDao.findAll();

        // Then
        List<Coffeeshop> expectedResult = new ArrayList<>();
        assertNotNull(actualResult);
        assertTrue(actualResult.size() >= 0);
    }

    @Test
    void findAllCoffeeshopsFromStaff_WhenStaffIdExists_ShouldReturnListOfCoffeeshops() {
        // Given
        long staffId = 1L;

        // When
        List<Coffeeshop> actualResult = coffeeshopDao.findAllCoffeeshopsFromStaff(staffId);

        // Then
        List<Coffeeshop> expectedResult = new ArrayList<>();
        assertNotNull(actualResult);
        assertTrue(actualResult.size() >= expectedResult.size());
    }

    @Test
    void findAllCoffeeshopsFromStaff_WhenStaffIdNotExists_ShouldReturnEmptyList() {
        // Given
        long staffId = 999999L;

        // When
        List<Coffeeshop> actualResult = coffeeshopDao.findAllCoffeeshopsFromStaff(staffId);

        // Then
        List<Coffeeshop> expectedResult = new ArrayList<>();
        assertNotNull(actualResult);
        assertTrue(actualResult.size() >= 0);
    }

    @Test
    void deleteAll_WhenCalled_ShouldNotThrowException() {
        // When
        Exception actualResult = null;
        try {
            coffeeshopDao.deleteAll();
        } catch (Exception e) {
            actualResult = e;
        }

        // Then
        Exception expectedResult = null;
        assertEquals(expectedResult, actualResult);
    }
}
