package org.example.menu;


import org.example.dao.coffeeshopDAO.CoffeeshopDao;
import org.example.dao.customerDAO.CustomerDao;
import org.example.dao.customerDiscountDAO.CustomerDiscountDao;
import org.example.dao.menuDAO.MenuDao;
import org.example.dao.orderDAO.OrderDao;
import org.example.dao.scheduleDAO.ScheduleDao;
import org.example.dao.shiftDAO.ShiftDao;
import org.example.dao.staffDAO.StaffDao;
import org.example.model.*;

import java.math.BigDecimal;
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
    private static final String  SHOW_ALL_BARISTAS = "Show all baristas";
    private static final String  SHOW_ALL_WAITERS = "Show all waiters";
    private static final String  SHOW_MIN_DISCOUNT = "Show minimum discount for customer";
    private static final String  SHOW_MAX_DISCOUNT = "Show maximum discount for customer";
    private static final String  SHOW_CUSTOMERS_MIN_DISCOUNT = "Show customers with minimum discount";
    private static final String  SHOW_CUSTOMERS_MAX_DISCOUNT = "Show customers with maximum discount";
    private static final String  SHOW_AVG_DISCOUNT = "Show average discount value";
    private static final String  SHOW_YOUNGEST_CUSTOMER = "Show youngest customer";
    private static final String  SHOW_OLDEST_CUSTOMER = "Show oldest customer";
    private static final String  SHOW_CUSTOMERS_BIRTHDAY_TODAY = "Show customers with birthday today";
    private static final String  SHOW_CUSTOMERS_WITHOUT_EMAIL = "Show customers without email";
    private static final String  UPDATE_COFFEE_PRICE = "Update coffee price";
    private static final String  UPDATE_PASTRY_CHEF_ADDRESS = "Update pastry chef address";
    private static final String  UPDATE_BARISTA_PHONE = "Update barista phone";
    private static final String  UPDATE_CUSTOMER_DISCOUNT = "Update customer discount";
    private static final String  DELETE_DESSERT = "Delete dessert";
    private static final String  DELETE_WAITER = "Delete waiter";
    private static final String  DELETE_BARISTA = "Delete barista";
    private static final String  DELETE_CUSTOMER = "Delete customer";
    private static final String  SHOW_ORDERS_BY_DATE = "Show orders by date";
    private static final String  SHOW_ORDERS_BY_DATE_RANGE = "Show orders by date range";
    private static final String  SHOW_DESSERT_ORDERS_COUNT_BY_DATE = "Show dessert orders count by date";
    private static final String  SHOW_DRINK_ORDERS_COUNT_BY_DATE = "Show drink orders count by date";
    private static final String  SHOW_CUSTOMERS_WITH_DRINKS_TODAY = "Show customers with drinks today";
    private static final String  SHOW_AVERAGE_ORDER_AMOUNT_BY_DATE = "Show average order amount by date";
    private static final String  SHOW_MAX_ORDER_AMOUNT_BY_DATE = "Show max order amount by date";
    private static final String  SHOW_CUSTOMER_WITH_MAX_ORDER_BY_DATE = "Show customer with max order by date";
    private static final String  SHOW_BARISTA_SCHEDULE_FOR_WEEK = "Show barista schedule for week";
    private static final String  SHOW_ALL_BARISTAS_SCHEDULE_FOR_WEEK = "Show all baristas schedule for week";
    private static final String  SHOW_ALL_STAFF_SCHEDULE_FOR_WEEK = "Show all staff schedule for week";
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
                .append(SHOW_ALL_BARISTAS)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(SHOW_ALL_WAITERS)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(SHOW_MIN_DISCOUNT)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(SHOW_MAX_DISCOUNT)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(SHOW_CUSTOMERS_MIN_DISCOUNT)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(SHOW_CUSTOMERS_MAX_DISCOUNT)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(SHOW_AVG_DISCOUNT)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(SHOW_YOUNGEST_CUSTOMER)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(SHOW_OLDEST_CUSTOMER)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(SHOW_CUSTOMERS_BIRTHDAY_TODAY)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(SHOW_CUSTOMERS_WITHOUT_EMAIL)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(UPDATE_COFFEE_PRICE)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(UPDATE_PASTRY_CHEF_ADDRESS)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(UPDATE_BARISTA_PHONE)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(UPDATE_CUSTOMER_DISCOUNT)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(DELETE_DESSERT)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(DELETE_WAITER)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(DELETE_BARISTA)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(DELETE_CUSTOMER)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(SHOW_ORDERS_BY_DATE)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(SHOW_ORDERS_BY_DATE_RANGE)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(SHOW_DESSERT_ORDERS_COUNT_BY_DATE)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(SHOW_DRINK_ORDERS_COUNT_BY_DATE)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(SHOW_CUSTOMERS_WITH_DRINKS_TODAY)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(SHOW_AVERAGE_ORDER_AMOUNT_BY_DATE)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(SHOW_MAX_ORDER_AMOUNT_BY_DATE)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(SHOW_CUSTOMER_WITH_MAX_ORDER_BY_DATE)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(SHOW_BARISTA_SCHEDULE_FOR_WEEK)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(SHOW_ALL_BARISTAS_SCHEDULE_FOR_WEEK)
                .append(END_LINE)
                .append(menuLine++)
                .append(DOT_SPACE)
                .append(SHOW_ALL_STAFF_SCHEDULE_FOR_WEEK)
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

    public static void showMinDiscount(CustomerDiscountDao customerDiscountDao) {
        BigDecimal minDiscount = customerDiscountDao.getMinDiscountValue();
        System.out.println("============================================================");
        System.out.println("Minimum discount for customer: " + minDiscount);
        System.out.println("============================================================");
    }

    public static void showMaxDiscount(CustomerDiscountDao customerDiscountDao) {
        BigDecimal maxDiscount = customerDiscountDao.getMaxDiscountValue();
        System.out.println("============================================================");
        System.out.println("Maximum discount for customer: " + maxDiscount);
        System.out.println("============================================================");
    }

    public static void showCustomersWithMinDiscount(CustomerDiscountDao customerDiscountDao) {
        List<CustomerDiscount> customers = customerDiscountDao.getCustomersWithMinDiscount();
        
        System.out.println("============================================================");
        System.out.println("Customers with minimum discount:");
        System.out.println("============================================================");
        
        if (customers.isEmpty()) {
            System.out.println("No customers found with minimum discount.");
        } else {
            int menuLine = 1;
            for (var customer : customers) {
                System.out.println(menuLine++ + ".  " + customer.toString());
            }
        }
        
        System.out.println("============================================================");
    }

    public static void showCustomersWithMaxDiscount(CustomerDiscountDao customerDiscountDao) {
        List<CustomerDiscount> customers = customerDiscountDao.getCustomersWithMaxDiscount();
        
        System.out.println("============================================================");
        System.out.println("Customers with maximum discount:");
        System.out.println("============================================================");
        
        if (customers.isEmpty()) {
            System.out.println("No customers found with maximum discount.");
        } else {
            int menuLine = 1;
            for (var customer : customers) {
                System.out.println(menuLine++ + ".  " + customer.toString());
            }
        }
        
        System.out.println("============================================================");
    }

    public static void showAverageDiscount(CustomerDiscountDao customerDiscountDao) {
        BigDecimal avgDiscount = customerDiscountDao.getAverageDiscountValue();
        System.out.println("============================================================");
        System.out.println("Average discount value: " + avgDiscount);
        System.out.println("============================================================");
    }

    public static void showYoungestCustomers(CustomerDao customerDao) {
        List<Customer> customers = customerDao.getYoungestCustomers();
        
        System.out.println("============================================================");
        System.out.println("Youngest customers:");
        System.out.println("============================================================");
        
        if (customers.isEmpty()) {
            System.out.println("No customers found.");
        } else {
            int menuLine = 1;
            for (var customer : customers) {
                System.out.println(menuLine++ + ".  " + customer.toString());
            }
        }
        
        System.out.println("============================================================");
    }

    public static void showOldestCustomers(CustomerDao customerDao) {
        List<Customer> customers = customerDao.getOldestCustomers();
        
        System.out.println("============================================================");
        System.out.println("Oldest customers:");
        System.out.println("============================================================");
        
        if (customers.isEmpty()) {
            System.out.println("No customers found.");
        } else {
            int menuLine = 1;
            for (var customer : customers) {
                System.out.println(menuLine++ + ".  " + customer.toString());
            }
        }
        
        System.out.println("============================================================");
    }

    public static void showCustomersWithBirthdayToday(CustomerDao customerDao) {
        List<Customer> customers = customerDao.getCustomersWithBirthdayToday();
        
        System.out.println("============================================================");
        System.out.println("Customers with birthday today:");
        System.out.println("============================================================");
        
        if (customers.isEmpty()) {
            System.out.println("No customers have birthday today.");
        } else {
            int menuLine = 1;
            for (var customer : customers) {
                System.out.println(menuLine++ + ".  " + customer.toString());
            }
        }
        
        System.out.println("============================================================");
    }

    public static void showCustomersWithoutEmail(CustomerDao customerDao) {
        List<Customer> customers = customerDao.getCustomersWithoutEmail();
        
        System.out.println("============================================================");
        System.out.println("Customers without email:");
        System.out.println("============================================================");
        
        if (customers.isEmpty()) {
            System.out.println("All customers have email addresses.");
        } else {
            int menuLine = 1;
            for (var customer : customers) {
                System.out.println(menuLine++ + ".  " + customer.toString());
            }
        }
        
        System.out.println("============================================================");
    }

    public static void showAllBaristas(StaffDao staffDao) {
        List<Staff> baristas = staffDao.findAllBaristas();
        
        System.out.println("============================================================");
        System.out.println("All baristas:");
        System.out.println("============================================================");
        
        if (baristas.isEmpty()) {
            System.out.println("No baristas found.");
        } else {
            int menuLine = 1;
            for (var barista : baristas) {
                System.out.println(menuLine++ + ".  ID: " + barista.getId() + 
                    ", Name: " + barista.getFirstName() + " " + barista.getLastName() +
                    ", Position ID: " + barista.getPositionId() +
                    ", Shift ID: " + barista.getShift_Id());
            }
        }
        
        System.out.println("============================================================");
    }

    public static void showAllWaiters(StaffDao staffDao) {
        List<Staff> waiters = staffDao.findAllWaiters();
        
        System.out.println("============================================================");
        System.out.println("All waiters:");
        System.out.println("============================================================");
        
        if (waiters.isEmpty()) {
            System.out.println("No waiters found.");
        } else {
            int menuLine = 1;
            for (var waiter : waiters) {
                System.out.println(menuLine++ + ".  ID: " + waiter.getId() + 
                    ", Name: " + waiter.getFirstName() + " " + waiter.getLastName() +
                    ", Position ID: " + waiter.getPositionId() +
                    ", Shift ID: " + waiter.getShift_Id());
            }
        }
        
        System.out.println("============================================================");
    }

    public static void showOrdersByDate(OrderDao orderDao, java.sql.Date date) {
        List<Order> orders = orderDao.getOrdersByDate(date);
        
        System.out.println("============================================================");
        System.out.println("Orders for date: " + date);
        System.out.println("============================================================");
        
        if (orders.isEmpty()) {
            System.out.println("No orders found for this date.");
        } else {
            int menuLine = 1;
            for (var order : orders) {
                System.out.println(menuLine++ + ".  " + order.toString());
            }
        }
        
        System.out.println("============================================================");
    }

    public static void showOrdersByDateRange(OrderDao orderDao, java.sql.Date startDate, java.sql.Date endDate) {
        List<Order> orders = orderDao.getOrdersByDateRange(startDate, endDate);
        
        System.out.println("============================================================");
        System.out.println("Orders from " + startDate + " to " + endDate + ":");
        System.out.println("============================================================");
        
        if (orders.isEmpty()) {
            System.out.println("No orders found for this date range.");
        } else {
            int menuLine = 1;
            for (var order : orders) {
                System.out.println(menuLine++ + ".  " + order.toString());
            }
        }
        
        System.out.println("============================================================");
    }

    public static void showDessertOrdersCountByDate(OrderDao orderDao, java.sql.Date date) {
        int count = orderDao.getDessertOrdersCountByDate(date);
        
        System.out.println("============================================================");
        System.out.println("Dessert orders count for date " + date + ": " + count);
        System.out.println("============================================================");
    }

    public static void showDrinkOrdersCountByDate(OrderDao orderDao, java.sql.Date date) {
        int count = orderDao.getDrinkOrdersCountByDate(date);
        
        System.out.println("============================================================");
        System.out.println("Drink orders count for date " + date + ": " + count);
        System.out.println("============================================================");
    }

    public static void showCustomersWithDrinksToday(OrderDao orderDao) {
        List<CustomerBaristaInfo> customers = orderDao.getCustomersWithDrinksToday();
        
        System.out.println("============================================================");
        System.out.println("Customers who ordered drinks today:");
        System.out.println("============================================================");
        
        if (customers.isEmpty()) {
            System.out.println("No customers found who ordered drinks today.");
        } else {
            int menuLine = 1;
            for (var info : customers) {
                System.out.println(menuLine++ + ".  " + info.toString());
            }
        }
        
        System.out.println("============================================================");
    }

    public static void showAverageOrderAmountByDate(OrderDao orderDao, java.sql.Date date) {
        BigDecimal avgAmount = orderDao.getAverageOrderAmountByDate(date);
        
        System.out.println("============================================================");
        System.out.println("Average order amount for date " + date + ": " + avgAmount);
        System.out.println("============================================================");
    }

    public static void showMaxOrderAmountByDate(OrderDao orderDao, java.sql.Date date) {
        BigDecimal maxAmount = orderDao.getMaxOrderAmountByDate(date);
        
        System.out.println("============================================================");
        System.out.println("Maximum order amount for date " + date + ": " + maxAmount);
        System.out.println("============================================================");
    }

    public static void showCustomerWithMaxOrderByDate(OrderDao orderDao, java.sql.Date date) {
        CustomerBaristaInfo customer = orderDao.getCustomerWithMaxOrderAmountByDate(date);
        
        System.out.println("============================================================");
        System.out.println("Customer with maximum order amount for date " + date + ":");
        System.out.println("============================================================");
        
        if (customer == null) {
            System.out.println("No customer found with orders on this date.");
        } else {
            System.out.println(customer.toString());
        }
        
        System.out.println("============================================================");
    }

    public static void showBaristaScheduleForWeek(ScheduleDao scheduleDao, String firstName, String lastName) {
        List<StaffSchedule> schedules = scheduleDao.getBaristaScheduleForWeek(firstName, lastName);
        
        System.out.println("============================================================");
        System.out.println("Barista " + firstName + " " + lastName + " schedule for this week:");
        System.out.println("============================================================");
        
        if (schedules.isEmpty()) {
            System.out.println("No schedule found for this barista for this week.");
        } else {
            int menuLine = 1;
            for (var schedule : schedules) {
                System.out.println(menuLine++ + ".  " + schedule.toString());
            }
        }
        
        System.out.println("============================================================");
    }

    public static void showAllBaristasScheduleForWeek(ScheduleDao scheduleDao) {
        List<StaffSchedule> schedules = scheduleDao.getAllBaristasScheduleForWeek();
        
        System.out.println("============================================================");
        System.out.println("All baristas schedule for this week:");
        System.out.println("============================================================");
        
        if (schedules.isEmpty()) {
            System.out.println("No schedule found for baristas for this week.");
        } else {
            int menuLine = 1;
            for (var schedule : schedules) {
                System.out.println(menuLine++ + ".  " + schedule.toString());
            }
        }
        
        System.out.println("============================================================");
    }

    public static void showAllStaffScheduleForWeek(ScheduleDao scheduleDao) {
        List<StaffSchedule> schedules = scheduleDao.getAllStaffScheduleForWeek();
        
        System.out.println("============================================================");
        System.out.println("All staff schedule for this week:");
        System.out.println("============================================================");
        
        if (schedules.isEmpty()) {
            System.out.println("No schedule found for staff for this week.");
        } else {
            int menuLine = 1;
            for (var schedule : schedules) {
                System.out.println(menuLine++ + ".  " + schedule.toString());
            }
        }
        
        System.out.println("============================================================");
    }

   // private MenuPublisher() {    }

}
