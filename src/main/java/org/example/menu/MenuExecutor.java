package org.example.menu;


import org.example.dao.shiftDAO.ShiftDao;
import org.example.dao.shiftDAO.ShiftDaoImpl;
import org.example.dao.staffAndCoffeeshopDAO.StaffToCoffeeshopDaoImpl;
import org.example.dao.staffDAO.StaffDao;
import org.example.dao.staffDAO.StaffDaoImpl;
import org.example.dao.staffAndCoffeeshopDAO.StaffToCoffeeshopDao;
import org.example.model.Shift;
import org.example.model.Staff;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static org.example.menu.MenuPublisher.*;


public class MenuExecutor {

    public static void startMenu() {
        showMenu();

        Scanner scanner = new Scanner(System.in);
        int choice = scanner.nextInt();

        if (choice == 1) {
            menuItem1Execute();
        }
        if (choice == 2) {
            menuItem2Execute();
        }
        if (choice == 3) {
            menuItem3Execute();
        }
        if (choice == 4) {
            menuItem4Execute();
        }
        if (choice == 5) {
            menuItem5Execute();
        }
        if (choice == 6) {
            menuItem6Execute();
        }
    }

    public static void menuItem1Execute() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please, enter the number of staff");
        int numberOfStaff = scanner.nextInt();

        ShiftDao shiftDao = new ShiftDaoImpl();
        List<String> shifts = shiftDao.findAllShiftsWithLessOrEqualStaffNumber(numberOfStaff);

        System.out.println("Shifts with less ore equal staff are");
        showStringList(shifts);
    }

    public static void menuItem2Execute() {
        showCoffeeshopList();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please, enter the coffeshop title");
        String coffeeshop_title = scanner.nextLine();

        StaffDao staffDao = new StaffDaoImpl();
        List<Staff> staff = staffDao.findAllFromCoffeeshops(coffeeshop_title);

        List<String> staffList = staff.stream().collect(
                ArrayList::new,
                (list,item)-> list.add(item.getFirstName() + " " + item.getLastName()),
                (list1, list2) -> list1.addAll(list2));

        showStringList(staffList);
    }

    public static void menuItem3Execute() {
        Scanner scanner = new Scanner(System.in);
        showShiftList();
        System.out.println("Please, enter the shift to add staff");
        String shiftTitle = scanner.nextLine();
        System.out.println("Please, enter the first name of staff");
        String firstName = scanner.nextLine();
        System.out.println("Please, enter the last name of staff");
        String lastName = scanner.nextLine();

        ShiftDao shiftDao = new ShiftDaoImpl();
        List<Shift> shifts = shiftDao.findAll();
        try {
            Shift shiftToAdd = shifts.stream().filter(e -> e.getShiftTitle().equals(shiftTitle)).collect(Collectors.toList()).get(0);
            StaffDao staffDao = new StaffDaoImpl();
            Staff addStaff = new Staff();
            addStaff.setFirstName(firstName);
            addStaff.setLastName(lastName);
            addStaff.setId(shiftToAdd.getId());
            staffDao.save(addStaff);

        } catch(IndexOutOfBoundsException e) {
            System.err.println("Invalid title of shift");
        }
    }

    public static void menuItem4Execute() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please, enter the staff id for delete");
        long staffId = scanner.nextLong();

        StaffDao staffDao = new StaffDaoImpl();
        staffDao.delete(staffId);
    }

    public static void menuItem5Execute() {
        Scanner scanner = new Scanner(System.in);

        showCoffeeshopList();
        System.out.println("Please, enter the coffeeshop titles, that assign to staff");
        String coffeeshop_title = scanner.nextLine();

        System.out.println("Please, enter the staff id to assign");
        long staffId = scanner.nextLong();

        StaffToCoffeeshopDao staffToCoffeeshopDao = new StaffToCoffeeshopDaoImpl();
        staffToCoffeeshopDao.assignStaffToCoffeeshop(staffId,coffeeshop_title);
    }

    public static void menuItem6Execute() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please, enter the staff id to assign");
        long staffId = scanner.nextLong();
        scanner.nextLine();

        showCoffeeshopListStaff(staffId);
        System.out.println("Please, enter the coffeeshop title to remove from staff");
        String coffeeshop_title = scanner.nextLine();

        StaffToCoffeeshopDao staffToCoffeeShopDao = new StaffToCoffeeshopDaoImpl();
        staffToCoffeeShopDao.deleteCoffeeshopFromStaff(staffId, coffeeshop_title);
    }

    private MenuExecutor() {    }

}
