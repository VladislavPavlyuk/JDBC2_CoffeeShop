package org.example.menu;


import org.example.dao.coffeeshopDAO.CoffeeshopDao;
import org.example.dao.coffeeshopDAO.CoffeeshopDaoImpl;
import org.example.dao.shiftDAO.ShiftDao;
import org.example.dao.shiftDAO.ShiftDaoImpl;
import org.example.model.Coffeeshop;
import org.example.model.Shift;

import java.util.List;

public class MenuPublisher {

    private static final String  ACTION_STRING = "To do action press the number";
    private static final String  FIND_ALL_SHIFTS = "Find all shifts with less or equal staffs’ number";
    private static final String  FIND_ALL_STAFF = "Find all staff related to the coffeshop with the given name";
    private static final String  ADD_STAFF = "Add a new staff";
    private static final String  DELETE_STAFF = "Delete a staff by the STAFF_ID";
    private static final String  ADD_STAFF_TO_COFFESHOP = "Add a staff to the coffeshop (from a list)";
    private static final String  REMOVE_STAFF_FROM_COFFEESHOP = "Remove the staff from one of coffeshops.";
    private static final String  INVATION_STRING = "Please enter the number";
    private static final String  SEPARATOR = "-";
    private static final String  DOT_SPACE = ".  ";
    private static final String  END_LINE = "\n";

    private static final String  LIST_OF_SHIFTS = "List of shifts";
    private static final String  LIST_OF_COFFEESHOPS = "List of coffeshops";

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
                .append(SEPARATOR.repeat(60))
                .append(END_LINE)
                .append(INVATION_STRING);

        System.out.println(resultString.toString());
    }

    public static  void showShiftList() {
        ShiftDao shiftDao = new ShiftDaoImpl();
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

    public static  void showCoffeeshopList() {
        CoffeeshopDao coffeeShopDao = new CoffeeshopDaoImpl();
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

    public static void showCoffeeshopListStaff(long staffId) {
        CoffeeshopDao coffeeShopDao = new CoffeeshopDaoImpl();
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

   // private MenuPublisher() {    }

}
