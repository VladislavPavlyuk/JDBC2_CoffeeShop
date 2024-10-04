package org.example.dao.staffDAO;

import org.example.dao.ConnectionFactory;
import org.example.exception.ConnectionDBException;
import org.example.model.Staff;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StaffDaoImpl implements StaffDao {

    private static final String SAVE_STAFF = "INSERT INTO staff(shift_id,first_name,last_name) VALUES(?,?,?)";
    private static final String UPDATE_STAFF = "UPDATE staff SET first_name=?,last_name=? WHERE id = ?";
    private static final String FIND_ALL_STAFF = "SELECT * FROM staff";
    private static final String FIND_ALL_STAFF_FROM_COFFEESHOP = "SELECT staff.id, staff.shift_id, staff.first_name, " +
            " staff.last_name FROM staff JOIN staffandshift ON staff.id = staffandcoffeeshops.staff_id " +
            " JOIN shifts ON coffeeshops.id = staffandcoffeeshops.coffeeshop_id WHERE coffeeshop.coffeeshop_title = ? ";
    private static final String DELETE_ALL_STAFF = "DELETE FROM staff";
    private static final String DELETE_STAFF = "DELETE FROM staff WHERE staff.id = ?";

    @Override
    public void save(Staff staff){
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(SAVE_STAFF)) {
            ps.setLong(1, staff.getShift_Id());
            ps.setString(2, staff.getFirstName());
            ps.setString(3, staff.getLastName());
            ps.execute();
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void saveMany(List<Staff> staff) {
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(SAVE_STAFF)) {

            for (var currentStudent : staff) {
                ps.setLong(1, currentStudent.getShift_Id());
                ps.setString(2, currentStudent.getFirstName());
                ps.setString(3, currentStudent.getLastName());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }


    @Override
    public void update(Staff staff) {
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_STAFF)) {
            ps.setString(1, staff.getFirstName());
            ps.setString(2, staff.getLastName());
            ps.setLong(3, staff.getId());
            ps.execute();
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void delete(long staffId) {
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_STAFF)) {
            ps.setLong(1, staffId);
            ps.execute();
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public List<Staff> findAll() {
        List<Staff> resultStaffs = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL_STAFF);
             ResultSet result = ps.executeQuery()) {

            while (result.next()) {
                Staff addStaff = new Staff();
                addStaff.setId(result.getLong(1));
                addStaff.setShift_Id(result.getLong(2));
                addStaff.setFirstName(result.getString(3));
                addStaff.setLastName(result.getString(4));
                resultStaffs.add(addStaff);
            }
            return resultStaffs;
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
        return resultStaffs;
    }

    @Override
    public List<Staff> findAllFromCourse(String courseName) {
        List<Staff> resultStaff = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL_STAFF_FROM_COFFEESHOP)) {

            ps.setString(1,courseName);
            try (ResultSet result = ps.executeQuery()) {
                while (result.next()) {
                    Staff addStaff = new Staff();
                    addStaff.setId(result.getLong(1));
                    addStaff.setShift_Id(result.getLong(2));
                    addStaff.setFirstName(result.getString(3));
                    addStaff.setLastName(result.getString(4));
                    resultStaff.add(addStaff);
                }
                return resultStaff;
            }
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
        return resultStaff;
    }

    @Override
    public void deleteAll() {
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_ALL_STAFF)) {
            ps.execute();
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
