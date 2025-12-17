package org.example.dao.staffDAO;

import org.example.dao.ConnectionFactory;
import org.example.dao.ConnectionProvider;
import org.example.exception.ConnectionDBException;
import org.example.model.Staff;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// handles database operations for staff
public class StaffDaoImpl implements StaffDao {

    private final ConnectionProvider connectionProvider;

    private static final String SAVE_STAFF = "INSERT INTO staff(firstname,lastname,position_id,shift_id) VALUES(?,?,?,?)";
    private static final String UPDATE_STAFF = "UPDATE staff SET firstname=?,lastname=? WHERE id = ?";
    private static final String FIND_ALL_STAFF = "SELECT id, firstname, lastname, middlename, position_id, shift_id, is_active, hired_date, created_at, updated_at FROM staff";
    private static final String FIND_ALL_STAFF_FROM_COFFEESHOP = "SELECT staff.id, staff.shift_id, staff.firstname, " +
            " staff.lastname FROM staff JOIN staffandcoffeeshops ON staff.id = staffandcoffeeshops.staff_id " +
            " JOIN coffeeshops ON coffeeshops.id = staffandcoffeeshops.coffeeshops_id WHERE coffeeshops.coffeeshop_title = ? ";
    private static final String DELETE_ALL_STAFF = "DELETE FROM staff";
    private static final String DELETE_STAFF = "DELETE FROM staff WHERE staff.id = ?";

    public StaffDaoImpl(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Deprecated
    public StaffDaoImpl() {
        this.connectionProvider = ConnectionFactory.getInstance();
    }

    @Override
    public void save(Staff staff){
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(SAVE_STAFF)) {
            ps.setString(1, staff.getFirstName());
            ps.setString(2, staff.getLastName());
            ps.setLong(3, staff.getPositionId());
            ps.setLong(4, staff.getShift_Id());
            ps.execute();
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void saveMany(List<Staff> staff) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(SAVE_STAFF)) {

            for (var currentStaff : staff) {
                // limit names to 50 chars (database field size)
                String firstName = currentStaff.getFirstName();
                String lastName = currentStaff.getLastName();
                if (firstName != null && firstName.length() > 50) {
                    firstName = firstName.substring(0, 50);
                }
                if (lastName != null && lastName.length() > 50) {
                    lastName = lastName.substring(0, 50);
                }
                
                ps.setString(1, firstName);
                ps.setString(2, lastName);
                ps.setLong(3, currentStaff.getPositionId());
                ps.setLong(4, currentStaff.getShift_Id());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (ConnectionDBException | SQLException e) {
            System.err.println("Error saving staff: " + e.getMessage());
            if (e instanceof SQLException) {
                SQLException sqlEx = (SQLException) e;
                System.err.println("SQL State: " + sqlEx.getSQLState());
                System.err.println("Error Code: " + sqlEx.getErrorCode());
                SQLException nextEx = sqlEx.getNextException();
                if (nextEx != null) {
                    System.err.println("Next exception: " + nextEx.getMessage());
                }
            }
            e.printStackTrace();
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
        try (Connection conn = connectionProvider.getConnection();
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
                addStaff.setId(result.getLong(1));  // id
                addStaff.setFirstName(result.getString(2));  // firstname
                addStaff.setLastName(result.getString(3));
                addStaff.setPositionId(result.getLong(5));
                addStaff.setShift_Id(result.getLong(6));
                resultStaffs.add(addStaff);
            }
            return resultStaffs;
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
        return resultStaffs;
    }

    @Override
    public List<Staff> findAllFromCoffeeshops(String coffeeshop_Title) {
        List<Staff> resultStaff = new ArrayList<>();
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL_STAFF_FROM_COFFEESHOP)) {

            ps.setString(1,coffeeshop_Title);
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
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_ALL_STAFF)) {
            ps.execute();
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
