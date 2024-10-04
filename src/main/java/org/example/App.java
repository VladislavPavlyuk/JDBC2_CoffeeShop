package org.example;

import org.example.service.CoffeeShopInitializer;

import static org.example.menu.MenuExecutor.startMenu;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.setProperty("test", "false");

        CoffeeShopInitializer coffeeshop = new CoffeeShopInitializer();
        coffeeshop.coffeeshopInitialize();
        startMenu();
    }
}
