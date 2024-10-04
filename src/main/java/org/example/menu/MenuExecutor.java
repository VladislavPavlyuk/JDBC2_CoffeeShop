package org.example.menu;


import org.example.dao.shiftDAO.ShiftDao;
import org.example.dao.shiftDAO.ShiftDaoImpl;
import org.example.dao.staffDAO.StaffDao;
import org.example.dao.staffDAO.StaffDaoImpl;
import org.example.dao.staffAndCoffeeShopDAO.StaffToCoffeeShopDao;
import org.example.dao.staffAndCoffeeShopDAO.StaffToCoffeeShopDaoImpl;
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
        System.out.println("Please, enter the number of students");
        int numberOfStudents = scanner.nextInt();

        ShiftDao shiftDao = new ShiftDaoImpl();
        List<String> groups = shiftDao.findAllShiftsWithLessOrEqualStaffNumber(numberOfStudents);

        System.out.println("Groups with less ore equal students nembers are");
        showStringList(groups);
    }

    public static void menuItem2Execute() {
        showCoursesList();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please, enter the courses name");
        String courseName = scanner.nextLine();

        StaffDao staffDao = new StaffDaoImpl();
        List<Staff> staff = staffDao.findAllFromCourse(courseName);

        List<String> studentsList = staff.stream().collect(
                ArrayList::new,
                (list,item)-> list.add(item.getFirstName() + " " + item.getLastName()),
                (list1, list2) -> list1.addAll(list2));

        showStringList(studentsList);
    }

    public static void menuItem3Execute() {
        Scanner scanner = new Scanner(System.in);
        showGroupList();
        System.out.println("Please, enter the group to add student");
        String groupName = scanner.nextLine();
        System.out.println("Please, enter the first name of student");
        String firstName = scanner.nextLine();
        System.out.println("Please, enter the last name of student");
        String lastName = scanner.nextLine();

        ShiftDao shiftDao = new ShiftDaoImpl();
        List<Shift> shifts = shiftDao.findAll();
        try {
            Shift shiftToAdd = shifts.stream().filter(e -> e.getShiftTitle().equals(groupName)).collect(Collectors.toList()).get(0);
            StaffDao staffDao = new StaffDaoImpl();
            Staff addStaff = new Staff();
            addStaff.setFirstName(firstName);
            addStaff.setLastName(lastName);
            addStaff.setId(shiftToAdd.getId());
            staffDao.save(addStaff);

        } catch(IndexOutOfBoundsException e) {
            System.err.println("Invalid name of group");
        }
    }

    public static void menuItem4Execute() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please, enter the student id for delete");
        long studentId = scanner.nextLong();

        StaffDao staffDao = new StaffDaoImpl();
        staffDao.delete(studentId);
    }

    public static void menuItem5Execute() {
        Scanner scanner = new Scanner(System.in);

        showCoursesList();
        System.out.println("Please, enter the courses name, that assign to student");
        String courseName = scanner.nextLine();

        System.out.println("Please, enter the student id to assign");
        long studentId = scanner.nextLong();

        StaffToCoffeeShopDao staffToCoffeeShopDao = new StaffToCoffeeShopDaoImpl();
        staffToCoffeeShopDao.assignStudentToCourse(studentId,courseName);
    }

    public static void menuItem6Execute() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Please, enter the student id to assign");
        long studentId = scanner.nextLong();
        scanner.nextLine();

        showCoursesListStudent(studentId);
        System.out.println("Please, enter the course name to remove from student");
        String courseName = scanner.nextLine();

        StaffToCoffeeShopDao staffToCoffeeShopDao = new StaffToCoffeeShopDaoImpl();
        staffToCoffeeShopDao.deleteCourseFromStudent(studentId, courseName);
    }

    private MenuExecutor() {
    }

}
