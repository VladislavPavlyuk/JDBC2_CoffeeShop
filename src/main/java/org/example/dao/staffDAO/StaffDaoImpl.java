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
    private static final String UPDATE_PASTRY_CHEF_ADDRESS_SQL = 
        "UPDATE staff_contacts " +
        "SET contact_value = ? " +
        "WHERE staff_id = (" +
        "  SELECT s.id " +
        "  FROM staff s " +
        "  JOIN positions p ON s.position_id = p.id " +
        "  WHERE p.position_code = 'PASTRY_CHEF' " +
        "  AND s.firstname = ? " +
        "  AND s.lastname = ? " +
        "  AND s.is_active = TRUE" +
        ") " +
        "AND contact_type = 'ADDRESS' " +
        "AND is_active = TRUE";
    private static final String UPDATE_BARISTA_PHONE_SQL = 
        "UPDATE staff_contacts " +
        "SET contact_value = ? " +
        "WHERE staff_id = (" +
        "  SELECT s.id " +
        "  FROM staff s " +
        "  JOIN positions p ON s.position_id = p.id " +
        "  WHERE p.position_code = 'BARISTA' " +
        "  AND s.firstname = ? " +
        "  AND s.lastname = ? " +
        "  AND s.is_active = TRUE" +
        ") " +
        "AND contact_type = 'PHONE' " +
        "AND is_active = TRUE";
    private static final String DELETE_WAITER_SQL = 
        "UPDATE staff " +
        "SET is_active = FALSE, " +
        "    updated_at = CURRENT_TIMESTAMP " +
        "WHERE id = (" +
        "  SELECT s.id " +
        "  FROM staff s " +
        "  JOIN positions p ON s.position_id = p.id " +
        "  WHERE p.position_code = 'WAITER' " +
        "  AND s.firstname = ? " +
        "  AND s.lastname = ? " +
        "  AND s.is_active = TRUE" +
        ")";
    private static final String DELETE_BARISTA_SQL = 
        "UPDATE staff " +
        "SET is_active = FALSE, " +
        "    updated_at = CURRENT_TIMESTAMP " +
        "WHERE id = (" +
        "  SELECT s.id " +
        "  FROM staff s " +
        "  JOIN positions p ON s.position_id = p.id " +
        "  WHERE p.position_code = 'BARISTA' " +
        "  AND s.firstname = ? " +
        "  AND s.lastname = ? " +
        "  AND s.is_active = TRUE" +
        ")";
    private static final String FIND_ALL_BARISTAS_SQL = 
        "SELECT " +
        "s.id, " +
        "s.firstname, " +
        "s.lastname, " +
        "s.middlename, " +
        "s.position_id, " +
        "s.shift_id, " +
        "s.hired_date, " +
        "s.is_active, " +
        "MAX(CASE WHEN sc.contact_type = 'PHONE' AND sc.is_primary = TRUE THEN sc.contact_value END) AS phone, " +
        "MAX(CASE WHEN sc.contact_type = 'EMAIL' AND sc.is_primary = TRUE THEN sc.contact_value END) AS email, " +
        "MAX(CASE WHEN sc.contact_type = 'ADDRESS' AND sc.is_primary = TRUE THEN sc.contact_value END) AS address " +
        "FROM staff s " +
        "JOIN positions p ON s.position_id = p.id " +
        "LEFT JOIN staff_contacts sc ON s.id = sc.staff_id AND sc.is_active = TRUE " +
        "WHERE p.position_code = 'BARISTA' " +
        "GROUP BY s.id, s.firstname, s.lastname, s.middlename, s.position_id, s.shift_id, s.hired_date, s.is_active " +
        "ORDER BY s.is_active DESC, s.lastname, s.firstname";
    private static final String FIND_ALL_WAITERS_SQL = 
        "SELECT " +
        "s.id, " +
        "s.firstname, " +
        "s.lastname, " +
        "s.middlename, " +
        "s.position_id, " +
        "s.shift_id, " +
        "s.hired_date, " +
        "s.is_active, " +
        "MAX(CASE WHEN sc.contact_type = 'PHONE' AND sc.is_primary = TRUE THEN sc.contact_value END) AS phone, " +
        "MAX(CASE WHEN sc.contact_type = 'EMAIL' AND sc.is_primary = TRUE THEN sc.contact_value END) AS email, " +
        "MAX(CASE WHEN sc.contact_type = 'ADDRESS' AND sc.is_primary = TRUE THEN sc.contact_value END) AS address " +
        "FROM staff s " +
        "JOIN positions p ON s.position_id = p.id " +
        "LEFT JOIN staff_contacts sc ON s.id = sc.staff_id AND sc.is_active = TRUE " +
        "WHERE p.position_code = 'WAITER' " +
        "GROUP BY s.id, s.firstname, s.lastname, s.middlename, s.position_id, s.shift_id, s.hired_date, s.is_active " +
        "ORDER BY s.is_active DESC, s.lastname, s.firstname";

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
                addStaff.setId(result.getLong(1));
                addStaff.setFirstName(result.getString(2));
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

    @Override
    public boolean updatePastryChefAddress(String firstName, String lastName, String newAddress) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_PASTRY_CHEF_ADDRESS_SQL)) {
            
            ps.setString(1, newAddress);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                // try to insert if doesn't exist
                try (PreparedStatement insertPs = conn.prepareStatement(
                    "INSERT INTO staff_contacts (staff_id, contact_type, contact_value, is_primary, is_active) " +
                    "SELECT s.id, 'ADDRESS', ?, FALSE, TRUE " +
                    "FROM staff s " +
                    "JOIN positions p ON s.position_id = p.id " +
                    "WHERE p.position_code = 'PASTRY_CHEF' " +
                    "AND s.firstname = ? " +
                    "AND s.lastname = ? " +
                    "AND s.is_active = TRUE " +
                    "ON CONFLICT (staff_id, contact_type, contact_value) " +
                    "WHERE is_active = TRUE " +
                    "DO UPDATE SET contact_value = EXCLUDED.contact_value")) {
                    insertPs.setString(1, newAddress);
                    insertPs.setString(2, firstName);
                    insertPs.setString(3, lastName);
                    insertPs.executeUpdate();
                    return true;
                } catch (SQLException e) {
                    return false;
                }
            }
            return rowsAffected > 0;
        } catch (ConnectionDBException | SQLException e) {
            System.err.println("Error updating pastry chef address: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateBaristaPhone(String firstName, String lastName, String newPhone) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_BARISTA_PHONE_SQL)) {
            
            ps.setString(1, newPhone);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                try (PreparedStatement insertPs = conn.prepareStatement(
                    "INSERT INTO staff_contacts (staff_id, contact_type, contact_value, is_primary, is_active) " +
                    "SELECT s.id, 'PHONE', ?, TRUE, TRUE " +
                    "FROM staff s " +
                    "JOIN positions p ON s.position_id = p.id " +
                    "WHERE p.position_code = 'BARISTA' " +
                    "AND s.firstname = ? " +
                    "AND s.lastname = ? " +
                    "AND s.is_active = TRUE " +
                    "ON CONFLICT (staff_id, contact_type, contact_value) " +
                    "WHERE is_active = TRUE " +
                    "DO UPDATE SET contact_value = EXCLUDED.contact_value")) {
                    insertPs.setString(1, newPhone);
                    insertPs.setString(2, firstName);
                    insertPs.setString(3, lastName);
                    insertPs.executeUpdate();
                    return true;
                } catch (SQLException e) {
                    return false;
                }
            }
            return rowsAffected > 0;
        } catch (ConnectionDBException | SQLException e) {
            System.err.println("Error updating barista phone: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteWaiter(String firstName, String lastName) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_WAITER_SQL)) {
            
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                try (PreparedStatement contactPs = conn.prepareStatement(
                    "UPDATE staff_contacts " +
                    "SET is_active = FALSE " +
                    "WHERE staff_id = (" +
                    "  SELECT s.id " +
                    "  FROM staff s " +
                    "  JOIN positions p ON s.position_id = p.id " +
                    "  WHERE p.position_code = 'WAITER' " +
                    "  AND s.firstname = ? " +
                    "  AND s.lastname = ?" +
                    ") " +
                    "AND is_active = TRUE")) {
                    contactPs.setString(1, firstName);
                    contactPs.setString(2, lastName);
                    contactPs.executeUpdate();
                }
            }
            
            return rowsAffected > 0;
        } catch (ConnectionDBException | SQLException e) {
            System.err.println("Error deleting waiter: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteBarista(String firstName, String lastName) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_BARISTA_SQL)) {
            
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                try (PreparedStatement contactPs = conn.prepareStatement(
                    "UPDATE staff_contacts " +
                    "SET is_active = FALSE " +
                    "WHERE staff_id = (" +
                    "  SELECT s.id " +
                    "  FROM staff s " +
                    "  JOIN positions p ON s.position_id = p.id " +
                    "  WHERE p.position_code = 'BARISTA' " +
                    "  AND s.firstname = ? " +
                    "  AND s.lastname = ?" +
                    ") " +
                    "AND is_active = TRUE")) {
                    contactPs.setString(1, firstName);
                    contactPs.setString(2, lastName);
                    contactPs.executeUpdate();
                }
            }
            
            return rowsAffected > 0;
        } catch (ConnectionDBException | SQLException e) {
            System.err.println("Error deleting barista: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Staff> findAllBaristas() {
        List<Staff> baristas = new ArrayList<>();
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL_BARISTAS_SQL);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Staff barista = new Staff();
                barista.setId(rs.getLong("id"));
                barista.setFirstName(rs.getString("firstname"));
                barista.setLastName(rs.getString("lastname"));
                barista.setPositionId(rs.getLong("position_id"));
                barista.setShift_Id(rs.getLong("shift_id"));
                baristas.add(barista);
            }
        } catch (ConnectionDBException | SQLException e) {
            System.err.println("Error finding all baristas: " + e.getMessage());
        }
        return baristas;
    }

    @Override
    public List<Staff> findAllWaiters() {
        List<Staff> waiters = new ArrayList<>();
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL_WAITERS_SQL);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Staff waiter = new Staff();
                waiter.setId(rs.getLong("id"));
                waiter.setFirstName(rs.getString("firstname"));
                waiter.setLastName(rs.getString("lastname"));
                waiter.setPositionId(rs.getLong("position_id"));
                waiter.setShift_Id(rs.getLong("shift_id"));
                waiters.add(waiter);
            }
        } catch (ConnectionDBException | SQLException e) {
            System.err.println("Error finding all waiters: " + e.getMessage());
        }
        return waiters;
    }
}
