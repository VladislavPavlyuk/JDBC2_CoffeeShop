package org.example.menu;

import org.example.dao.coffeeshopDAO.CoffeeshopDao;
import org.example.dao.menuDAO.MenuDao;
import org.example.dao.shiftDAO.ShiftDao;
import org.example.dao.staffDAO.StaffDao;
import org.example.model.Coffeeshop;
import org.example.model.MenuItem;
import org.example.model.Shift;
import org.example.model.Staff;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuPublisherTest {

    @Mock
    private ShiftDao shiftDao;

    @Mock
    private CoffeeshopDao coffeeshopDao;

    @Mock
    private StaffDao staffDao;

    @Mock
    private MenuDao menuDao;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @org.junit.jupiter.api.AfterEach
    void restoreSystemOut() {
        System.setOut(originalOut);
    }

    @Test
    void showMenu_WhenCalled_ShouldPrintMenuToConsole() {
        // When
        MenuPublisher.showMenu();

        // Then
        String actualResult = outContent.toString();
        assertNotNull(actualResult);
        assertFalse(actualResult.isEmpty());
    }

    @Test
    void showShiftList_WhenShiftsExist_ShouldDisplayShifts() {
        // Given
        List<Shift> shifts = new ArrayList<>();
        Shift shift1 = new Shift();
        shift1.setId(1L);
        shift1.setShiftTitle("Morning Shift");
        shifts.add(shift1);
        
        when(shiftDao.findAll()).thenReturn(shifts);

        // When
        MenuPublisher.showShiftList(shiftDao);

        // Then
        String actualResult = outContent.toString();
        assertNotNull(actualResult);
        assertFalse(actualResult.isEmpty());
    }

    @Test
    void showShiftList_WhenNoShiftsExist_ShouldDisplayEmptyList() {
        // Given
        List<Shift> shifts = new ArrayList<>();
        when(shiftDao.findAll()).thenReturn(shifts);

        // When
        MenuPublisher.showShiftList(shiftDao);

        // Then
        String actualResult = outContent.toString();
        assertNotNull(actualResult);
        assertFalse(actualResult.isEmpty());
    }

    @Test
    void showCoffeeshopList_WhenCoffeeshopsExist_ShouldDisplayCoffeeshops() {
        // Given
        List<Coffeeshop> coffeeshops = new ArrayList<>();
        Coffeeshop shop1 = new Coffeeshop();
        shop1.setId(1L);
        shop1.setCoffeeshopTitle("Coffee Shop 1");
        shop1.setCoffeeshopDescription("Description 1");
        coffeeshops.add(shop1);
        
        when(coffeeshopDao.findAll()).thenReturn(coffeeshops);

        // When
        MenuPublisher.showCoffeeshopList(coffeeshopDao);

        // Then
        String actualResult = outContent.toString();
        assertNotNull(actualResult);
        assertFalse(actualResult.isEmpty());
    }

    @Test
    void showCoffeeshopListStaff_WhenCoffeeshopsExist_ShouldDisplayCoffeeshops() {
        // Given
        long staffId = 1L;
        List<Coffeeshop> coffeeshops = new ArrayList<>();
        Coffeeshop shop1 = new Coffeeshop();
        shop1.setId(1L);
        shop1.setCoffeeshopTitle("Coffee Shop 1");
        shop1.setCoffeeshopDescription("Description 1");
        coffeeshops.add(shop1);
        
        when(coffeeshopDao.findAllCoffeeshopsFromStaff(staffId)).thenReturn(coffeeshops);

        // When
        MenuPublisher.showCoffeeshopListStaff(coffeeshopDao, staffId);

        // Then
        String actualResult = outContent.toString();
        assertNotNull(actualResult);
        assertFalse(actualResult.isEmpty());
    }

    @Test
    void showStaffList_WhenStaffExists_ShouldDisplayStaff() {
        // Given
        List<Staff> staff = new ArrayList<>();
        Staff staff1 = new Staff();
        staff1.setId(1L);
        staff1.setFirstName("Иван");
        staff1.setLastName("Петров");
        staff.add(staff1);
        
        when(staffDao.findAll()).thenReturn(staff);

        // When
        MenuPublisher.showStaffList(staffDao);

        // Then
        String actualResult = outContent.toString();
        assertNotNull(actualResult);
        assertFalse(actualResult.isEmpty());
    }

    @Test
    void showDesertsList_WhenDessertsExist_ShouldDisplayDesserts() {
        // Given
        List<MenuItem> desserts = new ArrayList<>();
        MenuItem dessert1 = new MenuItem();
        dessert1.setId(1L);
        dessert1.setName("Cheesecake");
        dessert1.setBasePrice(5.99);
        dessert1.setStatus("Available");
        desserts.add(dessert1);
        
        when(menuDao.findAllDesserts()).thenReturn(desserts);

        // When
        MenuPublisher.showDesertsList(menuDao);

        // Then
        String actualResult = outContent.toString();
        assertNotNull(actualResult);
        assertFalse(actualResult.isEmpty());
    }

    @Test
    void showDesertsList_WhenNoDessertsExist_ShouldDisplayEmptyMessage() {
        // Given
        List<MenuItem> desserts = new ArrayList<>();
        when(menuDao.findAllDesserts()).thenReturn(desserts);

        // When
        MenuPublisher.showDesertsList(menuDao);

        // Then
        String actualResult = outContent.toString();
        assertNotNull(actualResult);
        assertTrue(actualResult.contains("No deserts found"));
    }

    @Test
    void showDrinksList_WhenDrinksExist_ShouldDisplayDrinks() {
        // Given
        List<MenuItem> drinks = new ArrayList<>();
        MenuItem drink1 = new MenuItem();
        drink1.setId(1L);
        drink1.setName("Cappuccino");
        drink1.setBasePrice(3.99);
        drink1.setStatus("Available");
        drinks.add(drink1);
        
        when(menuDao.findAllDrinks()).thenReturn(drinks);

        // When
        MenuPublisher.showDrinksList(menuDao);

        // Then
        String actualResult = outContent.toString();
        assertNotNull(actualResult);
        assertFalse(actualResult.isEmpty());
    }

    @Test
    void showDrinksList_WhenNoDrinksExist_ShouldDisplayEmptyMessage() {
        // Given
        List<MenuItem> drinks = new ArrayList<>();
        when(menuDao.findAllDrinks()).thenReturn(drinks);

        // When
        MenuPublisher.showDrinksList(menuDao);

        // Then
        String actualResult = outContent.toString();
        assertNotNull(actualResult);
        assertTrue(actualResult.contains("No drinks found"));
    }

    @Test
    void showStringList_WhenListProvided_ShouldDisplayStrings() {
        // Given
        List<String> strings = new ArrayList<>();
        strings.add("Item 1");
        strings.add("Item 2");

        // When
        MenuPublisher.showStringList(strings);

        // Then
        String actualResult = outContent.toString();
        assertNotNull(actualResult);
        assertFalse(actualResult.isEmpty());
    }

    @Test
    void showStringList_WhenEmptyListProvided_ShouldDisplayEmptyList() {
        // Given
        List<String> strings = new ArrayList<>();

        // When
        MenuPublisher.showStringList(strings);

        // Then
        String actualResult = outContent.toString();
        assertNotNull(actualResult);
        assertFalse(actualResult.isEmpty());
    }
}










