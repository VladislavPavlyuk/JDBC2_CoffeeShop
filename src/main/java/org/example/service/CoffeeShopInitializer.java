package org.example.service;


import org.example.exception.FileException;

import static java.lang.System.setProperty;

public class CoffeeShopInitializer {

    public void coffeeshopInitialize() {
        setProperty("test", "false");
        try {
            CoffeeShopDbInitializer.createTables();
            CoffeeShopDbInitializer.deleteAllRowsInDB();
            CoffeeShopDbInitializer.createRandomShifts();
            CoffeeShopDbInitializer.createCoffeeShops();
            CoffeeShopDbInitializer.createRandomStaff();
            CoffeeShopDbInitializer.assignStaffToCoffeeshops();
        } catch (FileException e) {
            System.err.println(e.getMessage());
        }
    }
}
