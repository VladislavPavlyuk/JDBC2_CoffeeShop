package org.example.service;

import org.example.dao.ConnectionFactory;
import org.example.dao.coffeeShopDAO.CoffeeShopDao;
import org.example.dao.coffeeShopDAO.CoffeeShopDaoImpl;
import org.example.dao.shiftDAO.ShiftDao;
import org.example.dao.shiftDAO.ShiftDaoImpl;
import org.example.dao.staffDAO.StaffDao;
import org.example.dao.staffDAO.StaffDaoImpl;
import org.example.dao.staffAndCoffeeShopDAO.StaffToCoffeeShopDao;
import org.example.dao.staffAndCoffeeShopDAO.StaffToCoffeeShopDaoImpl;
import org.example.exception.ConnectionDBException;
import org.example.exception.FileException;
import org.example.model.CoffeeShop;
import org.example.model.Shift;
import org.example.model.Staff;
import org.example.model.StaffToCoffeeShop;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CoffeeShopDbInitializer {

    private static final Random RANDOM_GENERATOR = new Random();
    private static final List<String> TABLES_NAME_ARRAY;
    private static final String SQL_SCRIPT_CREATE_TABLES;

    static {
        SQL_SCRIPT_CREATE_TABLES = PropertyFactory.getInstance().getProperty().getProperty("db.sqlScriptCreateTables");

        String tablesNames = PropertyFactory.getInstance().getProperty().getProperty("db.tablesNames");
        TABLES_NAME_ARRAY = Arrays.stream(tablesNames.split(",")).collect(Collectors.toList());
    }

    public static void createTables() {
        try {
            Connection conn = ConnectionFactory.getInstance().makeConnection();

            for (var tableName : TABLES_NAME_ARRAY) {
                if (!tableExists(tableName)) {

                    try (Stream<String> lineStream = Files.lines(Paths.get(SQL_SCRIPT_CREATE_TABLES))) {
                        StringBuilder createTablesQuery = new StringBuilder();

                        for (var currentString : lineStream.collect(Collectors.toList())) {
                            createTablesQuery.append(currentString).append(" ");
                        }

                        try (PreparedStatement ps = conn.prepareStatement(createTablesQuery.toString())) {
                            ps.execute();
                        }

                    } catch (IOException exception) {
                        throw new FileException("Error with createTables.sql");
                    } catch (SQLException e) {
                        System.err.println(e.getMessage());
                    }
                }
            }

        } catch (ConnectionDBException | FileException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void createTablesForTests() {
        try {
            try (Connection conn = ConnectionFactory.getInstance().makeConnection()) {

                try (Stream<String> lineStream = Files.lines(Paths.get(SQL_SCRIPT_CREATE_TABLES))) {
                    StringBuilder createTablesQuery = new StringBuilder();

                    for (var currentString : lineStream.collect(Collectors.toList())) {
                        createTablesQuery.append(currentString).append("\n");
                    }

                    try (PreparedStatement ps = conn.prepareStatement(createTablesQuery.toString())) {
                        ps.execute();
                    }

                } catch (IOException exception) {
                    throw new FileException("Error with createTables.sql");
                } catch (SQLException e) {
                    System.err.println(e.getMessage());
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (ConnectionDBException | FileException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void deleteAllRowsInDB() {
        CoffeeShopDao coffeeShopDao = new CoffeeShopDaoImpl();
        ShiftDao shiftDao = new ShiftDaoImpl();
        StaffDao staffDao = new StaffDaoImpl();
        StaffToCoffeeShopDao staffToCoffeeShopDao = new StaffToCoffeeShopDaoImpl();
        staffToCoffeeShopDao.deleteAll();
        coffeeShopDao.deleteAll();
        staffDao.deleteAll();
        shiftDao.deleteAll();
    }

    public static void createRandomShifts() {
        ShiftDao shiftDao = new ShiftDaoImpl();

        List<Shift> shifts = new ArrayList<>();
        for (int count = 0; count < 10; count++) {
            StringBuilder shiftTitle = new StringBuilder();

            shiftTitle.append((char)(RANDOM_GENERATOR.nextInt(26) + 'a'))
                    .append((char)(RANDOM_GENERATOR.nextInt(26) + 'a'))
                    .append("-")
                    .append((char)(RANDOM_GENERATOR.nextInt(10) + '0'))
                    .append((char)(RANDOM_GENERATOR.nextInt(10) + '0'));
            Shift shift = new Shift();
            shift.setShiftTitle(shiftTitle.toString());

            shifts.add(shift);
        }
        shiftDao.saveMany(shifts);
    }

    public static void createCoffeeShops() throws FileException {
        String coursesFileName = PropertyFactory.getInstance().getProperty().getProperty("data.courses");
        CoffeeShopDao coffeeShopDao = new CoffeeShopDaoImpl();

        List<CoffeeShop> courses = new ArrayList<>();
        try (Stream<String> lineStream = Files.lines(Paths.get(coursesFileName))) {
            for (var currentString : lineStream.collect(Collectors.toList())) {
                CoffeeShop course = new CoffeeShop();
                course.setCoffeeshopName(currentString);
                course.setCoffeeshopDescription("This is " + currentString + " faculty");

                courses.add(course);
            }
            coffeeShopDao.saveMany(courses);
        } catch (IOException exception) {
            throw new FileException("Error with createTables.sql");
        }
    }

    public static void createRandomStaff() throws FileException {
        TxtFileReader txtFileReaderNames = new TxtFileReader("data.names");
        List<String> randomNames = txtFileReaderNames.readFile();
        TxtFileReader txtFileReaderLastNames = new TxtFileReader("data.lastnames");
        List<String> randomLastNames = txtFileReaderLastNames.readFile();

        ShiftDao shiftDao = new ShiftDaoImpl();
        List<Shift> shifts = shiftDao.findAll();

        StaffDao staffDAO = new StaffDaoImpl();

        List<Staff> staffToAdd = new ArrayList<>();
        for (int count = 0; count < 200; count++) {
            Staff addStaff = new Staff();
            addStaff.setFirstName(randomNames.get(RANDOM_GENERATOR.nextInt(randomNames.size())));
            addStaff.setLastName(randomLastNames.get(RANDOM_GENERATOR.nextInt(randomLastNames.size())));
            addStaff.setShift_Id(shifts.get(RANDOM_GENERATOR.nextInt(shifts.size())).getId());

            staffToAdd.add(addStaff);
        }
        staffDAO.saveMany(staffToAdd);
    }

    public static void assignStaffToCoffeeshops() {
        CoffeeShopDao coffeeShopDao = new CoffeeShopDaoImpl();
        List<CoffeeShop> courses = coffeeShopDao.findAll();
        StaffDao staffDao = new StaffDaoImpl();
        List<Staff> staff = staffDao.findAll();

        StaffToCoffeeShopDao staffToCoffeeShopDao = new StaffToCoffeeShopDaoImpl();
        for (var currentStudent : staff) {
            for (int count = 0; count < (RANDOM_GENERATOR.nextInt(2) + 1); count++) {
                StaffToCoffeeShop staffToCoffeeShop = new StaffToCoffeeShop();
                staffToCoffeeShop.setStaff_Id(currentStudent.getId());
                staffToCoffeeShop.setCoffeeshop_Id(courses.get(RANDOM_GENERATOR.nextInt(courses.size())).getId());
                staffToCoffeeShopDao.save(staffToCoffeeShop);
            }
        }
    }

    private static boolean tableExists(String tableName) throws ConnectionDBException {
        try (Connection connection = ConnectionFactory.getInstance().makeConnection()){
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet resultSet = meta.getTables(null, null, tableName, new String[]{"TABLE"});
            return resultSet.next();
        } catch (SQLException exception) {
            throw new ConnectionDBException("error connection to DB");
        }
    }

    private CoffeeShopDbInitializer() {
    }

}
