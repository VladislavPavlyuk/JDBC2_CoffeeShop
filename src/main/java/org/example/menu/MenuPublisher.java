package org.example.menu;


import org.example.dao.coffeeshopDAO.CoffeeshopDao;
import org.example.dao.menuDAO.MenuDao;
import org.example.dao.shiftDAO.ShiftDao;
import org.example.dao.staffDAO.StaffDao;
import org.example.model.Coffeeshop;
import org.example.model.MenuItem;
import org.example.model.Shift;
import org.example.model.Staff;

import java.util.ArrayList;
import java.util.List;

public class MenuPublisher {

    private static final String  ACTION_STRING = "To do action press the number";
    private static final String  FIND_ALL_SHIFTS = "Find all shifts with less or equal staffs number";
    private static final String  FIND_ALL_STAFF = "Find all staff related to the coffeshop with the given name";
    private static final String  ADD_STAFF = "Add a new staff";
    private static final String  DELETE_STAFF = "Delete a staff by the STAFF_ID";
    private static final String  ADD_STAFF_TO_COFFESHOP = "Add a staff to the coffeshop (from a list)";
    private static final String  REMOVE_STAFF_FROM_COFFEESHOP = "Remove the staff from one of coffeshops.";
    private static final String  SHOW_ALL_STAFF = "Show all staff";
    private static final String  SHOW_ALL_DESERTS = "Show all deserts";
    private static final String  SHOW_ALL_DRINKS = "Show all drinks and beverages";
    private static final String  TEST_ALL_DAO_METHODS = "Test all DAO methods";
    private static final String  EXIT = "Exit";
    private static final String  INVATION_STRING = "Please enter the number";
    private static final String  SEPARATOR = "-";
    private static final String  DOT_SPACE = ".  ";
    private static final String  END_LINE = "\n";

    private static final String  LIST_OF_SHIFTS = "List of shifts";
    private static final String  LIST_OF_COFFEESHOPS = "List of coffeshops";
    private static final String  LIST_OF_STAFF = "List of staff";
    private static final String  LIST_OF_DESERTS = "List of deserts";
    private static final String  LIST_OF_DRINKS = "List of drinks and beverages";

    public static void showMenu() {

        int menuLine = 1;
        StringBuilder resultString = new StringBuilder();
        resultString.append(ACTION_STRING)
                .append(END_LINE)
                .append(SEPARATOR.repeat(60))
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(FIND_ALL_SHIFTS)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(FIND_ALL_STAFF)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(ADD_STAFF)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(DELETE_STAFF)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(ADD_STAFF_TO_COFFESHOP)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(REMOVE_STAFF_FROM_COFFEESHOP)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(SHOW_ALL_STAFF)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(SHOW_ALL_DESERTS)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(SHOW_ALL_DRINKS)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(TEST_ALL_DAO_METHODS)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(EXIT)
                .append(END_LINE)
                .append(SEPARATOR.repeat(60))
                .append(END_LINE)
                .append(INVATION_STRING);

        System.out.println(resultString.toString());
    }

    public static  void showShiftList(ShiftDao shiftDao) {
        List<Shift> shifts = shiftDao.findAll();

        int menuLine = 1;
        StringBuilder resultString = new StringBuilder();

        resultString.append(LIST_OF_SHIFTS)
                .append(END_LINE)
                .append(SEPARATOR.repeat(60))
                .append(END_LINE);

        for (var currentShift : shifts) {
            resultString.append(menuLine++)
                    .append(DOT_SPACE)
                    .append(currentShift.getShiftTitle())
                    .append(END_LINE);
        }
        resultString.append(SEPARATOR.repeat(60))
                .append(END_LINE);

        System.out.println(resultString.toString());
    }

    public static  void showCoffeeshopList(CoffeeshopDao coffeeShopDao) {
        List<Coffeeshop> coffeeshops = coffeeShopDao.findAll();

        int menuLine = 1;
        StringBuilder resultString = new StringBuilder();

        resultString.append(LIST_OF_COFFEESHOPS)
                .append(END_LINE)
                .append(SEPARATOR.repeat(60))
                .append(END_LINE);

        for (var currentCoffeeshop : coffeeshops) {
            resultString.append(menuLine++)
                    .append(DOT_SPACE)
                    .append(currentCoffeeshop.getCoffeeshopTitle())
                    .append(DOT_SPACE)
                    .append(currentCoffeeshop.getCoffeeshopDescription())
                    .append(END_LINE);
        }
        resultString.append(SEPARATOR.repeat(60))
                .append(END_LINE);

        System.out.println(resultString.toString());
    }

    public static void showCoffeeshopListStaff(CoffeeshopDao coffeeShopDao, long staffId) {
        List<Coffeeshop> coffeeshops = coffeeShopDao.findAllCoffeeshopsFromStaff(staffId);

        int menuLine = 1;
        StringBuilder resultString = new StringBuilder();

        resultString.append(LIST_OF_COFFEESHOPS)
                .append(END_LINE)
                .append(SEPARATOR.repeat(60))
                .append(END_LINE);

        for (var currentCoffeshop : coffeeshops) {
            resultString.append(menuLine++)
                    .append(DOT_SPACE)
                    .append(currentCoffeshop.getCoffeeshopTitle())
                    .append(DOT_SPACE)
                    .append(currentCoffeshop.getCoffeeshopDescription())
                    .append(END_LINE);
        }
        resultString.append(SEPARATOR.repeat(60))
                .append(END_LINE);

        System.out.println(resultString.toString());
    }

    public static void showStringList(List<String> sourceStringList) {
        int menuLine = 1;
        StringBuilder resultString = new StringBuilder();

        resultString.append(SEPARATOR.repeat(60))
                .append(END_LINE);

        for (var currentString : sourceStringList) {
            resultString.append(menuLine++)
                    .append(DOT_SPACE)
                    .append(currentString)
                    .append(END_LINE);
        }
        resultString.append(SEPARATOR.repeat(60))
                .append(END_LINE);

        System.out.println(resultString.toString());
    }

    public static void showStaffList(StaffDao staffDao) {
        List<Staff> staff = staffDao.findAll();

        int menuLine = 1;
        StringBuilder resultString = new StringBuilder();

        resultString.append(LIST_OF_STAFF)
                .append(END_LINE)
                .append(SEPARATOR.repeat(60))
                .append(END_LINE);

        for (var currentStaff : staff) {
            resultString.append(menuLine++)
                    .append(DOT_SPACE)
                    .append("ID: ").append(currentStaff.getId())
                    .append(", Name: ").append(currentStaff.getFirstName())
                    .append(" ").append(currentStaff.getLastName())
                    .append(", Position ID: ").append(currentStaff.getPositionId())
                    .append(", Shift ID: ").append(currentStaff.getShift_Id())
                    .append(END_LINE);
        }
        resultString.append(SEPARATOR.repeat(60))
                .append(END_LINE);

        System.out.println(resultString.toString());
    }

    public static void showDesertsList(MenuDao menuDao) {
        List<MenuItem> desserts = menuDao.findAllDesserts();
        
        int menuLine = 1;
        StringBuilder resultString = new StringBuilder();
        
        resultString.append(LIST_OF_DESERTS)
                .append(END_LINE)
                .append(SEPARATOR.repeat(60))
                .append(END_LINE);
        
        if (desserts.isEmpty()) {
            resultString.append("No deserts found.")
                    .append(END_LINE);
        } else {
            for (var dessert : desserts) {
                resultString.append(menuLine++)
                        .append(DOT_SPACE)
                        .append(dessert.toString())
                        .append(END_LINE);
            }
        }
        
        resultString.append(SEPARATOR.repeat(60))
                .append(END_LINE);
        
        System.out.println(resultString.toString());
    }

    public static void showDrinksList(MenuDao menuDao) {
        List<MenuItem> drinks = menuDao.findAllDrinks();
        
        int menuLine = 1;
        StringBuilder resultString = new StringBuilder();
        
        resultString.append(LIST_OF_DRINKS)
                .append(END_LINE)
                .append(SEPARATOR.repeat(60))
                .append(END_LINE);
        
        if (drinks.isEmpty()) {
            resultString.append("No drinks found.")
                    .append(END_LINE);
        } else {
            for (var drink : drinks) {
                resultString.append(menuLine++)
                        .append(DOT_SPACE)
                        .append(drink.toString())
                        .append(END_LINE);
            }
        }
        
        resultString.append(SEPARATOR.repeat(60))
                .append(END_LINE);
        
        System.out.println(resultString.toString());
    }

   // private MenuPublisher() {    }

}
