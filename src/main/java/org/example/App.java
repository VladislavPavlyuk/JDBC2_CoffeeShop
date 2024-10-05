package org.example;

import org.example.service.CoffeeshopInitializer;

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

        CoffeeshopInitializer coffeeshop = new CoffeeshopInitializer();
        coffeeshop.coffeeshopInitialize();
        startMenu();
    }
}
