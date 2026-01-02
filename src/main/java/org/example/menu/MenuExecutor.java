package org.example.menu;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.example.dao.ConnectionFactory;
import org.example.dao.coffeeshopDAO.CoffeeshopDao;
import org.example.dao.coffeeshopDAO.CoffeeshopDaoImpl;
import org.example.dao.customerDAO.CustomerDao;
import org.example.dao.customerDAO.CustomerDaoImpl;
import org.example.dao.customerDiscountDAO.CustomerDiscountDao;
import org.example.dao.customerDiscountDAO.CustomerDiscountDaoImpl;
import org.example.dao.menuDAO.MenuDao;
import org.example.dao.menuDAO.MenuDaoImpl;
import org.example.dao.orderDAO.OrderDao;
import org.example.dao.orderDAO.OrderDaoImpl;
import org.example.dao.scheduleDAO.ScheduleDao;
import org.example.dao.scheduleDAO.ScheduleDaoImpl;
import org.example.dao.shiftDAO.ShiftDao;
import org.example.dao.shiftDAO.ShiftDaoImpl;
import org.example.dao.staffAndCoffeeshopDAO.StaffToCoffeeshopDao;
import org.example.dao.staffAndCoffeeshopDAO.StaffToCoffeeshopDaoImpl;
import org.example.dao.staffDAO.StaffDao;
import org.example.dao.staffDAO.StaffDaoImpl;
import static org.example.menu.MenuPublisher.showAverageDiscount;
import static org.example.menu.MenuPublisher.showCoffeeshopList;
import static org.example.menu.MenuPublisher.showCoffeeshopListStaff;
import static org.example.menu.MenuPublisher.showCustomersWithBirthdayToday;
import static org.example.menu.MenuPublisher.showCustomersWithMaxDiscount;
import static org.example.menu.MenuPublisher.showCustomersWithMinDiscount;
import static org.example.menu.MenuPublisher.showCustomersWithoutEmail;
import static org.example.menu.MenuPublisher.showDesertsList;
import static org.example.menu.MenuPublisher.showDrinksList;
import static org.example.menu.MenuPublisher.showMaxDiscount;
import static org.example.menu.MenuPublisher.showMinDiscount;
import static org.example.menu.MenuPublisher.showOldestCustomers;
import static org.example.menu.MenuPublisher.showShiftList;
import static org.example.menu.MenuPublisher.showStaffList;
import static org.example.menu.MenuPublisher.showStringList;
import static org.example.menu.MenuPublisher.showYoungestCustomers;
import org.example.model.Shift;
import org.example.model.Staff;


public class MenuExecutor {
    
    private static final org.example.dao.ConnectionProvider connectionProvider = ConnectionFactory.getInstance();
    private static final ShiftDao shiftDao = new ShiftDaoImpl();
    private static final StaffDao staffDao = new StaffDaoImpl(connectionProvider);
    private static final CoffeeshopDao coffeeshopDao = new CoffeeshopDaoImpl();
    private static final StaffToCoffeeshopDao staffToCoffeeshopDao = new StaffToCoffeeshopDaoImpl();
    private static final MenuDao menuDao = new MenuDaoImpl(connectionProvider);
    private static final CustomerDiscountDao customerDiscountDao = new CustomerDiscountDaoImpl(connectionProvider);
    private static final CustomerDao customerDao = new CustomerDaoImpl(connectionProvider);
    private static final OrderDao orderDao = new OrderDaoImpl(connectionProvider);
    private static final ScheduleDao scheduleDao = new ScheduleDaoImpl(connectionProvider);

    public static void startMenu() {
        startInteractiveMenu();
    }
    
    public static void startInteractiveMenu() {
        InteractiveMenu menu = new InteractiveMenu();
        
        menu.addMenuItem(MenuPublisher.FIND_ALL_SHIFTS, s -> menuItem1Execute(s));
        menu.addMenuItem(MenuPublisher.FIND_ALL_STAFF, s -> menuItem2Execute(s));
        menu.addMenuItem(MenuPublisher.ADD_STAFF, s -> menuItem3Execute(s));
        menu.addMenuItem(MenuPublisher.DELETE_STAFF, s -> menuItem4Execute(s));
        menu.addMenuItem(MenuPublisher.ADD_STAFF_TO_COFFESHOP, s -> menuItem5Execute(s));
        menu.addMenuItem(MenuPublisher.REMOVE_STAFF_FROM_COFFEESHOP, s -> menuItem6Execute(s));
        menu.addMenuItem(MenuPublisher.SHOW_ALL_STAFF, () -> menuItem7Execute());
        menu.addMenuItem(MenuPublisher.SHOW_ALL_DESERTS, () -> menuItem8Execute());
        menu.addMenuItem(MenuPublisher.SHOW_ALL_DRINKS, () -> menuItem9Execute());
        menu.addMenuItem(MenuPublisher.SHOW_ALL_BARISTAS, () -> menuItem10Execute());
        menu.addMenuItem(MenuPublisher.SHOW_ALL_WAITERS, () -> menuItem11Execute());
        menu.addMenuItem(MenuPublisher.SHOW_MIN_DISCOUNT, () -> menuItem12Execute());
        menu.addMenuItem(MenuPublisher.SHOW_MAX_DISCOUNT, () -> menuItem13Execute());
        menu.addMenuItem(MenuPublisher.SHOW_CUSTOMERS_MIN_DISCOUNT, () -> menuItem14Execute());
        menu.addMenuItem(MenuPublisher.SHOW_CUSTOMERS_MAX_DISCOUNT, () -> menuItem15Execute());
        menu.addMenuItem(MenuPublisher.SHOW_AVG_DISCOUNT, () -> menuItem16Execute());
        menu.addMenuItem(MenuPublisher.SHOW_YOUNGEST_CUSTOMER, () -> menuItem17Execute());
        menu.addMenuItem(MenuPublisher.SHOW_OLDEST_CUSTOMER, () -> menuItem18Execute());
        menu.addMenuItem(MenuPublisher.SHOW_CUSTOMERS_BIRTHDAY_TODAY, () -> menuItem19Execute());
        menu.addMenuItem(MenuPublisher.SHOW_CUSTOMERS_WITHOUT_EMAIL, () -> menuItem20Execute());
        menu.addMenuItem(MenuPublisher.UPDATE_COFFEE_PRICE, s -> menuItem21Execute(s));
        menu.addMenuItem(MenuPublisher.UPDATE_PASTRY_CHEF_ADDRESS, s -> menuItem22Execute(s));
        menu.addMenuItem(MenuPublisher.UPDATE_BARISTA_PHONE, s -> menuItem23Execute(s));
        menu.addMenuItem(MenuPublisher.UPDATE_CUSTOMER_DISCOUNT, s -> menuItem24Execute(s));
        menu.addMenuItem(MenuPublisher.DELETE_DESSERT, s -> menuItem25Execute(s));
        menu.addMenuItem(MenuPublisher.DELETE_WAITER, s -> menuItem26Execute(s));
        menu.addMenuItem(MenuPublisher.DELETE_BARISTA, s -> menuItem27Execute(s));
        menu.addMenuItem(MenuPublisher.DELETE_CUSTOMER, s -> menuItem28Execute(s));
        menu.addMenuItem(MenuPublisher.SHOW_ORDERS_BY_DATE, s -> menuItem29Execute(s));
        menu.addMenuItem(MenuPublisher.SHOW_ORDERS_BY_DATE_RANGE, s -> menuItem30Execute(s));
        menu.addMenuItem(MenuPublisher.SHOW_DESSERT_ORDERS_COUNT_BY_DATE, s -> menuItem31Execute(s));
        menu.addMenuItem(MenuPublisher.SHOW_DRINK_ORDERS_COUNT_BY_DATE, s -> menuItem32Execute(s));
        menu.addMenuItem(MenuPublisher.SHOW_CUSTOMERS_WITH_DRINKS_TODAY, () -> menuItem33Execute());
        menu.addMenuItem(MenuPublisher.SHOW_AVERAGE_ORDER_AMOUNT_BY_DATE, s -> menuItem34Execute(s));
        menu.addMenuItem(MenuPublisher.SHOW_MAX_ORDER_AMOUNT_BY_DATE, s -> menuItem35Execute(s));
        menu.addMenuItem(MenuPublisher.SHOW_CUSTOMER_WITH_MAX_ORDER_BY_DATE, s -> menuItem36Execute(s));
        menu.addMenuItem(MenuPublisher.SHOW_BARISTA_SCHEDULE_FOR_WEEK, s -> menuItem37Execute(s));
        menu.addMenuItem(MenuPublisher.SHOW_ALL_BARISTAS_SCHEDULE_FOR_WEEK, () -> menuItem38Execute());
        menu.addMenuItem(MenuPublisher.SHOW_ALL_STAFF_SCHEDULE_FOR_WEEK, () -> menuItem39Execute());
        menu.addMenuItem(MenuPublisher.TEST_ALL_DAO_METHODS, () -> menuItem40Execute());
        menu.addMenuItem(MenuPublisher.EXIT, () -> menu.stop());
        
        menu.display();
    }

    public static void menuItem1Execute(Scanner scanner) {
        System.out.println("Please, enter the number of staff");
        int numberOfStaff = scanner.nextInt();
        scanner.nextLine();

        List<String> shifts = shiftDao.findAllShiftsWithLessOrEqualStaffNumber(numberOfStaff);

        System.out.println("Shifts with less ore equal staff are");
        showStringList(shifts);
    }

    public static void menuItem2Execute(Scanner scanner) {
        showCoffeeshopList(coffeeshopDao);
        System.out.println("Please, enter the coffeshop title");
        String coffeeshop_title = scanner.nextLine();

        List<Staff> staff = staffDao.findAllFromCoffeeshops(coffeeshop_title);

        List<String> staffList = staff.stream().collect(
                ArrayList::new,
                (list,item)-> list.add(item.getFirstName() + " " + item.getLastName()),
                (list1, list2) -> list1.addAll(list2));

        showStringList(staffList);
    }

    public static void menuItem3Execute(Scanner scanner) {
        showShiftList(shiftDao);
        System.out.println("Please, enter the shift to add staff");
        String shiftTitle = scanner.nextLine();
        System.out.println("Please, enter the first name of staff");
        String firstName = scanner.nextLine();
        System.out.println("Please, enter the last name of staff");
        String lastName = scanner.nextLine();

        List<Shift> shifts = shiftDao.findAll();
        try {
            Shift shiftToAdd = shifts.stream().filter(e -> e.getShiftTitle().equals(shiftTitle)).collect(Collectors.toList()).get(0);
            Staff addStaff = new Staff();
            addStaff.setFirstName(firstName);
            addStaff.setLastName(lastName);
            addStaff.setId(shiftToAdd.getId());
            staffDao.save(addStaff);

        } catch(IndexOutOfBoundsException e) {
            System.err.println("Invalid title of shift");
        }
    }

    public static void menuItem4Execute(Scanner scanner) {
        System.out.println("Please, enter the staff id for delete");
        long staffId = scanner.nextLong();

        staffDao.delete(staffId);
    }

    public static void menuItem5Execute(Scanner scanner) {
        showCoffeeshopList(coffeeshopDao);
        System.out.println("Please, enter the coffeeshop titles, that assign to staff");
        String coffeeshop_title = scanner.nextLine();

        System.out.println("Please, enter the staff id to assign");
        long staffId = scanner.nextLong();
        scanner.nextLine();

        staffToCoffeeshopDao.assignStaffToCoffeeshop(staffId,coffeeshop_title);
    }

    public static void menuItem6Execute(Scanner scanner) {
        System.out.println("Please, enter the staff id to assign");
        long staffId = scanner.nextLong();
        scanner.nextLine();

        showCoffeeshopListStaff(coffeeshopDao, staffId);
        System.out.println("Please, enter the coffeeshop title to remove from staff");
        String coffeeshop_title = scanner.nextLine();

        staffToCoffeeshopDao.deleteCoffeeshopFromStaff(staffId, coffeeshop_title);
    }

    public static void menuItem7Execute() {
        showStaffList(staffDao);
    }

    public static void menuItem8Execute() {
        showDesertsList(menuDao);
    }

    public static void menuItem9Execute() {
        showDrinksList(menuDao);
    }

    public static void menuItem10Execute() {
        MenuPublisher.showAllBaristas(staffDao);
    }

    public static void menuItem11Execute() {
        MenuPublisher.showAllWaiters(staffDao);
    }

    public static void menuItem12Execute() {
        showMinDiscount(customerDiscountDao);
    }

    public static void menuItem13Execute() {
        showMaxDiscount(customerDiscountDao);
    }

    public static void menuItem14Execute() {
        showCustomersWithMinDiscount(customerDiscountDao);
    }

    public static void menuItem15Execute() {
        showCustomersWithMaxDiscount(customerDiscountDao);
    }

    public static void menuItem16Execute() {
        showAverageDiscount(customerDiscountDao);
    }

    public static void menuItem17Execute() {
        showYoungestCustomers(customerDao);
    }

    public static void menuItem18Execute() {
        showOldestCustomers(customerDao);
    }

    public static void menuItem19Execute() {
        showCustomersWithBirthdayToday(customerDao);
    }

    public static void menuItem20Execute() {
        showCustomersWithoutEmail(customerDao);
    }

    public static void menuItem21Execute(Scanner scanner) {
        showDrinksList(menuDao);
        System.out.println("Please, enter the coffee item code:");
        String itemCode = scanner.nextLine();
        System.out.println("Please, enter the new price:");
        double newPrice = scanner.nextDouble();
        scanner.nextLine();
        
        boolean success = menuDao.updateCoffeePrice(itemCode, newPrice);
        if (success) {
            System.out.println("Price updated successfully!");
        } else {
            System.out.println("Failed to update price. Coffee not found or invalid item code.");
        }
    }

    public static void menuItem22Execute(Scanner scanner) {
        System.out.println("Please, enter the pastry chef first name:");
        String firstName = scanner.nextLine();
        System.out.println("Please, enter the pastry chef last name:");
        String lastName = scanner.nextLine();
        System.out.println("Please, enter the new address:");
        String newAddress = scanner.nextLine();
        
        boolean success = staffDao.updatePastryChefAddress(firstName, lastName, newAddress);
        if (success) {
            System.out.println("Address updated successfully!");
        } else {
            System.out.println("Failed to update address. Pastry chef not found.");
        }
    }

    public static void menuItem23Execute(Scanner scanner) {
        System.out.println("Please, enter the barista first name:");
        String firstName = scanner.nextLine();
        System.out.println("Please, enter the barista last name:");
        String lastName = scanner.nextLine();
        System.out.println("Please, enter the new phone number:");
        String newPhone = scanner.nextLine();
        
        boolean success = staffDao.updateBaristaPhone(firstName, lastName, newPhone);
        if (success) {
            System.out.println("Phone updated successfully!");
        } else {
            System.out.println("Failed to update phone. Barista not found.");
        }
    }

    public static void menuItem24Execute(Scanner scanner) {
        System.out.println("Please, enter the customer first name:");
        String firstName = scanner.nextLine();
        System.out.println("Please, enter the customer last name:");
        String lastName = scanner.nextLine();
        System.out.println("Please, enter the new discount value:");
        double discountValue = scanner.nextDouble();
        scanner.nextLine();
        
        boolean success = customerDiscountDao.updateCustomerDiscount(
            firstName, lastName, java.math.BigDecimal.valueOf(discountValue));
        if (success) {
            System.out.println("Discount updated successfully!");
        } else {
            System.out.println("Failed to update discount. Customer not found or discount doesn't exist.");
        }
    }

    public static void menuItem25Execute(Scanner scanner) {
        showDesertsList(menuDao);
        System.out.println("Please, enter the dessert item code to delete:");
        String itemCode = scanner.nextLine();
        
        boolean success = menuDao.deleteDessert(itemCode);
        if (success) {
            System.out.println("Dessert deleted successfully!");
        } else {
            System.out.println("Failed to delete dessert. Dessert not found or invalid item code.");
        }
    }

    public static void menuItem26Execute(Scanner scanner) {
        System.out.println("Please, enter the waiter first name:");
        String firstName = scanner.nextLine();
        System.out.println("Please, enter the waiter last name:");
        String lastName = scanner.nextLine();
        
        boolean success = staffDao.deleteWaiter(firstName, lastName);
        if (success) {
            System.out.println("Waiter deleted successfully!");
        } else {
            System.out.println("Failed to delete waiter. Waiter not found.");
        }
    }

    public static void menuItem27Execute(Scanner scanner) {
        System.out.println("Please, enter the barista first name:");
        String firstName = scanner.nextLine();
        System.out.println("Please, enter the barista last name:");
        String lastName = scanner.nextLine();
        
        boolean success = staffDao.deleteBarista(firstName, lastName);
        if (success) {
            System.out.println("Barista deleted successfully!");
        } else {
            System.out.println("Failed to delete barista. Barista not found.");
        }
    }

    public static void menuItem28Execute(Scanner scanner) {
        System.out.println("Please, enter the customer first name:");
        String firstName = scanner.nextLine();
        System.out.println("Please, enter the customer last name:");
        String lastName = scanner.nextLine();
        
        boolean success = customerDao.deleteCustomer(firstName, lastName);
        if (success) {
            System.out.println("Customer deleted successfully!");
        } else {
            System.out.println("Failed to delete customer. Customer not found.");
        }
    }

    public static void menuItem29Execute(Scanner scanner) {
        System.out.println("Please, enter the date (YYYY-MM-DD):");
        String dateStr = scanner.nextLine();
        try {
            java.sql.Date date = java.sql.Date.valueOf(dateStr);
            MenuPublisher.showOrdersByDate(orderDao, date);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD format.");
        }
    }

    public static void menuItem30Execute(Scanner scanner) {
        System.out.println("Please, enter the start date (YYYY-MM-DD):");
        String startDateStr = scanner.nextLine();
        System.out.println("Please, enter the end date (YYYY-MM-DD):");
        String endDateStr = scanner.nextLine();
        try {
            java.sql.Date startDate = java.sql.Date.valueOf(startDateStr);
            java.sql.Date endDate = java.sql.Date.valueOf(endDateStr);
            MenuPublisher.showOrdersByDateRange(orderDao, startDate, endDate);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD format.");
        }
    }

    public static void menuItem31Execute(Scanner scanner) {
        System.out.println("Please, enter the date (YYYY-MM-DD):");
        String dateStr = scanner.nextLine();
        try {
            java.sql.Date date = java.sql.Date.valueOf(dateStr);
            MenuPublisher.showDessertOrdersCountByDate(orderDao, date);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD format.");
        }
    }

    public static void menuItem32Execute(Scanner scanner) {
        System.out.println("Please, enter the date (YYYY-MM-DD):");
        String dateStr = scanner.nextLine();
        try {
            java.sql.Date date = java.sql.Date.valueOf(dateStr);
            MenuPublisher.showDrinkOrdersCountByDate(orderDao, date);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD format.");
        }
    }

    public static void menuItem33Execute() {
        MenuPublisher.showCustomersWithDrinksToday(orderDao);
    }

    public static void menuItem34Execute(Scanner scanner) {
        System.out.println("Please, enter the date (YYYY-MM-DD):");
        String dateStr = scanner.nextLine();
        try {
            java.sql.Date date = java.sql.Date.valueOf(dateStr);
            MenuPublisher.showAverageOrderAmountByDate(orderDao, date);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD format.");
        }
    }

    public static void menuItem35Execute(Scanner scanner) {
        System.out.println("Please, enter the date (YYYY-MM-DD):");
        String dateStr = scanner.nextLine();
        try {
            java.sql.Date date = java.sql.Date.valueOf(dateStr);
            MenuPublisher.showMaxOrderAmountByDate(orderDao, date);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD format.");
        }
    }

    public static void menuItem36Execute(Scanner scanner) {
        System.out.println("Please, enter the date (YYYY-MM-DD):");
        String dateStr = scanner.nextLine();
        try {
            java.sql.Date date = java.sql.Date.valueOf(dateStr);
            MenuPublisher.showCustomerWithMaxOrderByDate(orderDao, date);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date format. Please use YYYY-MM-DD format.");
        }
    }

    public static void menuItem37Execute(Scanner scanner) {
        System.out.println("Please, enter the barista first name:");
        String firstName = scanner.nextLine();
        System.out.println("Please, enter the barista last name:");
        String lastName = scanner.nextLine();
        
        MenuPublisher.showBaristaScheduleForWeek(scheduleDao, firstName, lastName);
    }

    public static void menuItem38Execute() {
        MenuPublisher.showAllBaristasScheduleForWeek(scheduleDao);
    }

    public static void menuItem39Execute() {
        MenuPublisher.showAllStaffScheduleForWeek(scheduleDao);
    }

    public static void menuItem40Execute() {
        DaoMethodsTester.testAllMethods();
    }

    private MenuExecutor() {    }

}
