package org.example.menu;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.example.dao.ConnectionFactory;
import org.example.dao.coffeeshopDAO.CoffeeshopDao;
import org.example.dao.coffeeshopDAO.CoffeeshopDaoImpl;
import org.example.dao.customerDiscountDAO.CustomerDiscountDao;
import org.example.dao.customerDiscountDAO.CustomerDiscountDaoImpl;
import org.example.dao.menuDAO.MenuDao;
import org.example.dao.menuDAO.MenuDaoImpl;
import org.example.dao.shiftDAO.ShiftDao;
import org.example.dao.shiftDAO.ShiftDaoImpl;
import org.example.dao.staffAndCoffeeshopDAO.StaffToCoffeeshopDao;
import org.example.dao.staffAndCoffeeshopDAO.StaffToCoffeeshopDaoImpl;
import org.example.dao.staffDAO.StaffDao;
import org.example.dao.staffDAO.StaffDaoImpl;
import static org.example.menu.MenuPublisher.showAverageDiscount;
import static org.example.menu.MenuPublisher.showCoffeeshopList;
import static org.example.menu.MenuPublisher.showCoffeeshopListStaff;
import static org.example.menu.MenuPublisher.showCustomersWithMaxDiscount;
import static org.example.menu.MenuPublisher.showCustomersWithMinDiscount;
import static org.example.menu.MenuPublisher.showCustomersWithBirthdayToday;
import static org.example.menu.MenuPublisher.showCustomersWithoutEmail;
import static org.example.menu.MenuPublisher.showDesertsList;
import static org.example.menu.MenuPublisher.showDrinksList;
import static org.example.menu.MenuPublisher.showMaxDiscount;
import static org.example.menu.MenuPublisher.showMenu;
import static org.example.menu.MenuPublisher.showMinDiscount;
import static org.example.menu.MenuPublisher.showOldestCustomers;
import static org.example.menu.MenuPublisher.showShiftList;
import static org.example.menu.MenuPublisher.showStaffList;
import static org.example.menu.MenuPublisher.showStringList;
import static org.example.menu.MenuPublisher.showYoungestCustomers;
import org.example.menu.DaoMethodsTester;
import org.example.model.Shift;
import org.example.model.Staff;


public class MenuExecutor {
    
    // create DAO objects once to reuse them
    private static final org.example.dao.ConnectionProvider connectionProvider = ConnectionFactory.getInstance();
    private static final ShiftDao shiftDao = new ShiftDaoImpl();
    private static final StaffDao staffDao = new StaffDaoImpl(connectionProvider);
    private static final CoffeeshopDao coffeeshopDao = new CoffeeshopDaoImpl();
    private static final StaffToCoffeeshopDao staffToCoffeeshopDao = new StaffToCoffeeshopDaoImpl();
    private static final MenuDao menuDao = new MenuDaoImpl(connectionProvider);
    private static final CustomerDiscountDao customerDiscountDao = new CustomerDiscountDaoImpl(connectionProvider);
    private static final CustomerDao customerDao = new CustomerDaoImpl(connectionProvider);

    public static void startMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            try {
                showMenu();
                
                if (!scanner.hasNextInt()) {
                    System.out.println("Invalid input. Please enter a number.");
                    scanner.nextLine();
                    continue;
                }
                
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline after number

                if (choice == 1) {
                    menuItem1Execute(scanner);
                } else if (choice == 2) {
                    menuItem2Execute(scanner);
                } else if (choice == 3) {
                    menuItem3Execute(scanner);
                } else if (choice == 4) {
                    menuItem4Execute(scanner);
                } else if (choice == 5) {
                    menuItem5Execute(scanner);
                } else if (choice == 6) {
                    menuItem6Execute(scanner);
                } else if (choice == 7) {
                    menuItem7Execute();
                } else if (choice == 8) {
                    menuItem8Execute();
                } else if (choice == 9) {
                    menuItem9Execute();
                } else if (choice == 10) {
                    menuItem10Execute();
                } else if (choice == 11) {
                    menuItem11Execute();
                } else if (choice == 12) {
                    menuItem12Execute();
                } else if (choice == 13) {
                    menuItem13Execute();
                } else if (choice == 14) {
                    menuItem14Execute();
                } else if (choice == 15) {
                    menuItem15Execute();
                } else if (choice == 16) {
                    running = false;
                    System.out.println("Exiting application. Goodbye!");
                } else {
                    System.out.println("Invalid choice. Please try again.");
                }
            } catch (java.util.NoSuchElementException e) {
                System.out.println("\nInput stream closed. Exiting application.");
                running = false;
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
                scanner.nextLine(); // consume any remaining input
            }
        }
        scanner.close();
    }

    public static void menuItem1Execute(Scanner scanner) {
        System.out.println("Please, enter the number of staff");
        int numberOfStaff = scanner.nextInt();

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
        showMinDiscount(customerDiscountDao);
    }

    public static void menuItem11Execute() {
        showMaxDiscount(customerDiscountDao);
    }

    public static void menuItem12Execute() {
        showCustomersWithMinDiscount(customerDiscountDao);
    }

    public static void menuItem13Execute() {
        showCustomersWithMaxDiscount(customerDiscountDao);
    }

    public static void menuItem14Execute() {
        showAverageDiscount(customerDiscountDao);
    }

    public static void menuItem15Execute() {
        showYoungestCustomers(customerDao);
    }

    public static void menuItem16Execute() {
        showOldestCustomers(customerDao);
    }

    public static void menuItem17Execute() {
        showCustomersWithBirthdayToday(customerDao);
    }

    public static void menuItem18Execute() {
        showCustomersWithoutEmail(customerDao);
    }

    public static void menuItem19Execute() {
        DaoMethodsTester.testAllMethods();
    }

    private MenuExecutor() {    }

}
