package org.example;

import org.example.dao.coffeeshopDAO.CoffeeshopDao;
import org.example.dao.coffeeshopDAO.CoffeeshopDaoImpl;
import org.example.model.Coffeeshop;
import org.example.service.CoffeeshopDbInitializer;
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
        CoffeeshopDbInitializer.createTablesForTests();
    }

    @Test
    void save_ShouldInsertCoffeshopIntoTable_WhenCalled() {

        CoffeeshopDao coffeeshopDao = new CoffeeshopDaoImpl();

        Coffeeshop addCoffeeshop = new Coffeeshop();
        addCoffeeshop.setCoffeeshopDescription("description");
        addCoffeeshop.setId(1L);
        addCoffeeshop.setCoffeeshopTitle("coffeeshop");

        coffeeshopDao.save(addCoffeeshop);

        List<Coffeeshop> allCoffeeshops = coffeeshopDao.findAll();

        int expected = 1;
        int actual = allCoffeeshops.size();

        Assertions.assertEquals(expected,actual);

        List<Coffeeshop> expectedList = new ArrayList<>();
        expectedList.add(addCoffeeshop);

        Assertions.assertEquals(expectedList,allCoffeeshops);

    }
}
