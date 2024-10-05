package org.example.service;


import org.example.exception.FileException;

import static java.lang.System.setProperty;

public class CoffeeShopInitializer {

    public void coffeeshopInitialize() {
        setProperty("test", "false");
        try {
            CoffeeshopDbInitializer.createTables();
            CoffeeshopDbInitializer.deleteAllRowsInDB();
            CoffeeshopDbInitializer.createRandomShifts();
            CoffeeshopDbInitializer.createCoffeeshops();
            CoffeeshopDbInitializer.createRandomStaff();
            CoffeeshopDbInitializer.assignStaffToCoffeeshops();
        } catch (FileException e) {
            System.err.println(e.getMessage());
        }
    }
}
