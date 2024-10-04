package org.example;

import org.example.dao.coffeeShopDAO.CoffeeShopDao;
import org.example.dao.coffeeShopDAO.CoffeeShopDaoImpl;
import org.example.model.CoffeeShop;
import org.example.service.CoffeeShopDbInitializer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static java.lang.System.setProperty;

public class CoffeeshopDaoTest {

    @BeforeAll
    static void initTestDB() {
        setProperty("test", "true");
        CoffeeShopDbInitializer.createTablesForTests();
    }

    @Test
    void save_ShouldInsertCourseIntoTable_WhenCalled() {

        CoffeeShopDao coffeeShopDao = new CoffeeShopDaoImpl();

        CoffeeShop addCource = new CoffeeShop();
        addCource.setCourseDescription("description");
        addCource.setId(1L);
        addCource.setCourseName("course1");

        coffeeShopDao.save(addCource);

        List<CoffeeShop> allCourses = coffeeShopDao.findAll();

        int expected = 1;
        int actual = allCourses.size();

        Assertions.assertEquals(expected,actual);

        List<CoffeeShop> expectedList = new ArrayList<>();
        expectedList.add(addCource);

        Assertions.assertEquals(expectedList,allCourses);

    }
}
