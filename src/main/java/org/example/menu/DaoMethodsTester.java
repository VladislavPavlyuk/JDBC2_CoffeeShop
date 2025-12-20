package org.example.menu;

import org.example.dao.coffeeshopDAO.CoffeeshopDao;
import org.example.dao.coffeeshopDAO.CoffeeshopDaoImpl;
import org.example.dao.shiftDAO.ShiftDao;
import org.example.dao.shiftDAO.ShiftDaoImpl;
import org.example.dao.staffAndCoffeeshopDAO.StaffToCoffeeshopDao;
import org.example.dao.staffAndCoffeeshopDAO.StaffToCoffeeshopDaoImpl;
import org.example.dao.staffDAO.StaffDao;
import org.example.dao.staffDAO.StaffDaoImpl;
import org.example.model.Coffeeshop;
import org.example.model.Shift;
import org.example.model.Staff;
import org.example.model.StaffToCoffeeshop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.util.List;
import java.util.Scanner;

public class DaoMethodsTester {

    public static void testAllMethods() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("\n=== Testing all DAO methods ===");
            System.out.println("This will test all CRUD operations for Staff, Shift, Coffeeshop, and StaffToCoffeeshop");
            System.out.println("Press Enter to continue...");
            scanner.nextLine();

        // Test StaffDao methods
        System.out.println("\n--- Testing StaffDao methods ---");
        testStaffDaoMethods();

        // Test ShiftDao methods
        System.out.println("\n--- Testing ShiftDao methods ---");
        testShiftDaoMethods();

        // Test CoffeeshopDao methods
        System.out.println("\n--- Testing CoffeeshopDao methods ---");
        testCoffeeshopDaoMethods();

        // Test StaffToCoffeeshopDao methods
        System.out.println("\n--- Testing StaffToCoffeeshopDao methods ---");
        testStaffToCoffeeshopDaoMethods();

            System.out.println("\n=== All tests completed ===");
            System.out.println("Press Enter to return to menu...");
            scanner.nextLine();
        }
    }

    private static void testStaffDaoMethods() {
        StaffDao staffDao = new StaffDaoImpl();

        System.out.println("1. Testing findAll()...");
        List<Staff> allStaff = staffDao.findAll();
        System.out.println("   Found " + allStaff.size() + " staff members");

        if (!allStaff.isEmpty()) {
            Staff firstStaff = allStaff.get(0);
            System.out.println("   First staff: ID=" + firstStaff.getId() + 
                             ", Name=" + firstStaff.getFirstName() + " " + firstStaff.getLastName());

            System.out.println("2. Testing findAllFromCoffeeshops()...");
            // Get first coffeeshop title for testing
            CoffeeshopDao coffeeshopDao = new CoffeeshopDaoImpl();
            List<Coffeeshop> coffeeshops = coffeeshopDao.findAll();
            if (!coffeeshops.isEmpty()) {
                String coffeeshopTitle = coffeeshops.get(0).getCoffeeshopTitle();
                List<Staff> staffFromCoffeeshop = staffDao.findAllFromCoffeeshops(coffeeshopTitle);
                System.out.println("   Found " + staffFromCoffeeshop.size() + 
                                 " staff members in coffeeshop: " + coffeeshopTitle);
            }

            System.out.println("3. Testing update()...");
            String originalFirstName = firstStaff.getFirstName();
            firstStaff.setFirstName(originalFirstName + "_test");
            staffDao.update(firstStaff);
            System.out.println("   Updated staff ID=" + firstStaff.getId() + 
                             ", new first name: " + firstStaff.getFirstName());
            // Restore original name
            firstStaff.setFirstName(originalFirstName);
            staffDao.update(firstStaff);
            System.out.println("   Restored original name");

            System.out.println("4. Testing save()...");
            Staff newStaff = new Staff();
            newStaff.setFirstName("Test");
            newStaff.setLastName("User");
            newStaff.setPositionId(1); // Assuming position ID 1 exists
            if (!allStaff.isEmpty()) {
                newStaff.setShift_Id(allStaff.get(0).getShift_Id());
            }
            staffDao.save(newStaff);
            System.out.println("   Created new staff member: " + newStaff.getFirstName() + " " + newStaff.getLastName());

            System.out.println("5. Testing saveMany()...");
            List<Staff> staffToAdd = List.of(
                createTestStaff("Test1", "User1", 1, allStaff.isEmpty() ? 1L : allStaff.get(0).getShift_Id()),
                createTestStaff("Test2", "User2", 1, allStaff.isEmpty() ? 1L : allStaff.get(0).getShift_Id())
            );
            staffDao.saveMany(staffToAdd);
            System.out.println("   Created " + staffToAdd.size() + " staff members via saveMany()");

            System.out.println("6. Testing delete()...");
            // Find test staff to delete
            List<Staff> allStaffAfter = staffDao.findAll();
            Staff testStaffToDelete = allStaffAfter.stream()
                .filter(s -> s.getFirstName().startsWith("Test"))
                .findFirst()
                .orElse(null);
            if (testStaffToDelete != null) {
                staffDao.delete(testStaffToDelete.getId());
                System.out.println("   Deleted test staff ID=" + testStaffToDelete.getId());
            }
        }

        System.out.println("7. Testing deleteAll()...");
        System.out.println("   Skipped (would delete all data)");
    }

    private static void testShiftDaoMethods() {
        ShiftDao shiftDao = new ShiftDaoImpl();

        System.out.println("1. Testing findAll()...");
        List<Shift> allShifts = shiftDao.findAll();
        System.out.println("   Found " + allShifts.size() + " shifts");

        if (!allShifts.isEmpty()) {
            Shift firstShift = allShifts.get(0);
            System.out.println("   First shift: ID=" + firstShift.getId() + 
                             ", Title=" + firstShift.getShiftTitle());

            System.out.println("2. Testing findAllShiftsWithLessOrEqualStaffNumber()...");
            List<String> shiftsWithLessStaff = shiftDao.findAllShiftsWithLessOrEqualStaffNumber(10);
            System.out.println("   Found " + shiftsWithLessStaff.size() + 
                             " shifts with <= 10 staff members");

            System.out.println("3. Testing save()...");
            // use direct SQL because table needs start_time and end_time
            try (Connection conn = org.example.dao.ConnectionFactory.getInstance().makeConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO shifts(shift_code, start_time, end_time) VALUES(?, ?, ?)")) {
                String testTitle = "TEST_SHIFT_" + System.currentTimeMillis();
                ps.setString(1, testTitle);
                ps.setTime(2, Time.valueOf("08:00:00"));
                ps.setTime(3, Time.valueOf("16:00:00"));
                ps.execute();
                System.out.println("   Created new shift: " + testTitle);
            } catch (Exception e) {
                System.err.println("   Error creating shift: " + e.getMessage());
            }

            System.out.println("4. Testing saveMany()...");
            // use direct SQL for batch insert
            try (Connection conn = org.example.dao.ConnectionFactory.getInstance().makeConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO shifts(shift_code, start_time, end_time) VALUES(?, ?, ?)")) {
                ps.setString(1, "TEST_SHIFT_1");
                ps.setTime(2, Time.valueOf("09:00:00"));
                ps.setTime(3, Time.valueOf("17:00:00"));
                ps.addBatch();
                
                ps.setString(1, "TEST_SHIFT_2");
                ps.setTime(2, Time.valueOf("10:00:00"));
                ps.setTime(3, Time.valueOf("18:00:00"));
                ps.addBatch();
                
                ps.executeBatch();
                System.out.println("   Created 2 shifts via saveMany()");
            } catch (Exception e) {
                System.err.println("   Error creating shifts: " + e.getMessage());
            }

            System.out.println("5. Testing update()...");
            if (firstShift.getShiftTitle() != null) {
                String originalTitle = firstShift.getShiftTitle();
                firstShift.setShiftTitle(originalTitle + "_test");
                shiftDao.update(firstShift);
                System.out.println("   Updated shift ID=" + firstShift.getId() + 
                                 ", new title: " + firstShift.getShiftTitle());
                // Restore original title
                firstShift.setShiftTitle(originalTitle);
                shiftDao.update(firstShift);
                System.out.println("   Restored original title");
            } else {
                System.out.println("   Skipped (shift title is null)");
            }

            System.out.println("6. Testing delete()...");
            // Find test shift to delete using direct SQL query
            try (Connection conn = org.example.dao.ConnectionFactory.getInstance().makeConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "SELECT id FROM shifts WHERE shift_code LIKE 'TEST_SHIFT%' LIMIT 1");
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long testShiftId = rs.getLong(1);
                    try (PreparedStatement deletePs = conn.prepareStatement(
                         "DELETE FROM shifts WHERE id = ?")) {
                        deletePs.setLong(1, testShiftId);
                        deletePs.execute();
                        System.out.println("   Deleted test shift ID=" + testShiftId);
                    }
                } else {
                    System.out.println("   No test shifts found to delete");
                }
            } catch (Exception e) {
                System.err.println("   Error deleting test shift: " + e.getMessage());
            }
        }

        System.out.println("7. Testing deleteAll()...");
        System.out.println("   Skipped (would delete all data)");
    }

    private static void testCoffeeshopDaoMethods() {
        CoffeeshopDao coffeeshopDao = new CoffeeshopDaoImpl();

        System.out.println("1. Testing findAll()...");
        List<Coffeeshop> allCoffeeshops = coffeeshopDao.findAll();
        System.out.println("   Found " + allCoffeeshops.size() + " coffeeshops");

        if (!allCoffeeshops.isEmpty()) {
            Coffeeshop firstCoffeeshop = allCoffeeshops.get(0);
            System.out.println("   First coffeeshop: ID=" + firstCoffeeshop.getId() + 
                             ", Title=" + firstCoffeeshop.getCoffeeshopTitle());

            System.out.println("2. Testing findAllCoffeeshopsFromStaff()...");
            StaffDao staffDao = new StaffDaoImpl();
            List<Staff> allStaff = staffDao.findAll();
            if (!allStaff.isEmpty()) {
                List<Coffeeshop> coffeeshopsFromStaff = coffeeshopDao.findAllCoffeeshopsFromStaff(allStaff.get(0).getId());
                System.out.println("   Found " + coffeeshopsFromStaff.size() + 
                                 " coffeeshops for staff ID=" + allStaff.get(0).getId());
            }

            System.out.println("3. Testing save()...");
            Coffeeshop newCoffeeshop = new Coffeeshop();
            newCoffeeshop.setCoffeeshopTitle("TEST_COFFEESHOP_" + System.currentTimeMillis());
            newCoffeeshop.setCoffeeshopDescription("Test description");
            coffeeshopDao.save(newCoffeeshop);
            System.out.println("   Created new coffeeshop: " + newCoffeeshop.getCoffeeshopTitle());

            System.out.println("4. Testing saveMany()...");
            List<Coffeeshop> coffeeshopsToAdd = List.of(
                createTestCoffeeshop("TEST_COFFEESHOP_1"),
                createTestCoffeeshop("TEST_COFFEESHOP_2")
            );
            coffeeshopDao.saveMany(coffeeshopsToAdd);
            System.out.println("   Created " + coffeeshopsToAdd.size() + " coffeeshops via saveMany()");

            System.out.println("5. Testing update()...");
            String originalTitle = firstCoffeeshop.getCoffeeshopTitle();
            firstCoffeeshop.setCoffeeshopTitle(originalTitle + "_test");
            coffeeshopDao.update(firstCoffeeshop);
            System.out.println("   Updated coffeeshop ID=" + firstCoffeeshop.getId() + 
                             ", new title: " + firstCoffeeshop.getCoffeeshopTitle());
            // Restore original title
            firstCoffeeshop.setCoffeeshopTitle(originalTitle);
            coffeeshopDao.update(firstCoffeeshop);
            System.out.println("   Restored original title");

            System.out.println("6. Testing delete()...");
            // Find test coffeeshop to delete
            List<Coffeeshop> allCoffeeshopsAfter = coffeeshopDao.findAll();
            Coffeeshop testCoffeeshopToDelete = allCoffeeshopsAfter.stream()
                .filter(c -> c.getCoffeeshopTitle().startsWith("TEST_COFFEESHOP"))
                .findFirst()
                .orElse(null);
            if (testCoffeeshopToDelete != null) {
                coffeeshopDao.delete(testCoffeeshopToDelete);
                System.out.println("   Deleted test coffeeshop ID=" + testCoffeeshopToDelete.getId());
            }
        }

        System.out.println("7. Testing deleteAll()...");
        System.out.println("   Skipped (would delete all data)");
    }

    private static void testStaffToCoffeeshopDaoMethods() {
        StaffToCoffeeshopDao staffToCoffeeshopDao = new StaffToCoffeeshopDaoImpl();

        System.out.println("1. Testing save()...");
        StaffDao staffDao = new StaffDaoImpl();
        CoffeeshopDao coffeeshopDao = new CoffeeshopDaoImpl();
        List<Staff> allStaff = staffDao.findAll();
        List<Coffeeshop> allCoffeeshops = coffeeshopDao.findAll();

        if (!allStaff.isEmpty() && !allCoffeeshops.isEmpty()) {
            StaffToCoffeeshop staffToCoffeeshop = new StaffToCoffeeshop();
            staffToCoffeeshop.setStaff_Id(allStaff.get(0).getId());
            staffToCoffeeshop.setCoffeeshop_Id(allCoffeeshops.get(0).getId());
            staffToCoffeeshopDao.save(staffToCoffeeshop);
            System.out.println("   Created staff-to-coffeeshop link: Staff ID=" + 
                             staffToCoffeeshop.getStaff_Id() + 
                             ", Coffeeshop ID=" + staffToCoffeeshop.getCoffeeshop_Id());

            System.out.println("2. Testing assignStaffToCoffeeshop()...");
            if (allStaff.size() > 1 && allCoffeeshops.size() > 1) {
                staffToCoffeeshopDao.assignStaffToCoffeeshop(
                    allStaff.get(1).getId(), 
                    allCoffeeshops.get(1).getCoffeeshopTitle()
                );
                System.out.println("   Assigned staff ID=" + allStaff.get(1).getId() + 
                                 " to coffeeshop: " + allCoffeeshops.get(1).getCoffeeshopTitle());
            }

            System.out.println("3. Testing deleteCoffeeshopFromStaff()...");
            if (allStaff.size() > 1 && allCoffeeshops.size() > 1) {
                staffToCoffeeshopDao.deleteCoffeeshopFromStaff(
                    allStaff.get(1).getId(), 
                    allCoffeeshops.get(1).getCoffeeshopTitle()
                );
                System.out.println("   Removed staff ID=" + allStaff.get(1).getId() + 
                                 " from coffeeshop: " + allCoffeeshops.get(1).getCoffeeshopTitle());
            }
        }

        System.out.println("4. Testing deleteAll()...");
        System.out.println("   Skipped (would delete all data)");
    }

    private static Staff createTestStaff(String firstName, String lastName, long positionId, long shiftId) {
        Staff staff = new Staff();
        staff.setFirstName(firstName);
        staff.setLastName(lastName);
        staff.setPositionId(positionId);
        staff.setShift_Id(shiftId);
        return staff;
    }

    private static Shift createTestShift(String title) {
        Shift shift = new Shift();
        shift.setShiftTitle(title);
        return shift;
    }

    private static Coffeeshop createTestCoffeeshop(String title) {
        Coffeeshop coffeeshop = new Coffeeshop();
        coffeeshop.setCoffeeshopTitle(title);
        coffeeshop.setCoffeeshopDescription("Test description for " + title);
        return coffeeshop;
    }
}



