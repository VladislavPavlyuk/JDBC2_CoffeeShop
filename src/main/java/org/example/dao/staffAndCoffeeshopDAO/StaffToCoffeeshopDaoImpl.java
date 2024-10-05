package org.example.dao.staffAndCoffeeshopDAO;

import org.example.dao.ConnectionFactory;
import org.example.exception.ConnectionDBException;
import org.example.model.StaffToCoffeeshop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StaffToCoffeeshopDaoImpl implements StaffToCoffeeshopDao {

    private static final String SAVE_STAFF_TO_COFFEESHOP = "INSERT INTO staffandcoffeeshops(staff_id,coffeshop_id) VALUES(?,?)";
    private static final String DELETE_ALL_STAFF_TO_COFFEESHOP = "DELETE FROM staffandcoffeeshops";
    private static final String ASSIGN_STAFF_TO_COFFEESHOP = "INSERT INTO staffandcoffeeshops(staff_id,coffeeshop_id) " +
                " VALUES(?, (SELECT coffeeshops.id FROM coffeeshops WHERE coffeeshops.coffeeshop_title = ?))";
    private static final String DELETE_COFFEESHOP_FROM_STAFF = "DELETE FROM staffandcoffeeshops " +
            " WHERE  staffandcoffeeshops.staff_id = ? AND " +
            " staffandcoffeeshops.coffeeshop_id = (SELECT coffeeshops.id FROM coffeeshops WHERE coffeeshops.coffeeshop_title = ?) ";

    @Override
    public void save(StaffToCoffeeshop staffToCoffeeshop) {
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(SAVE_STAFF_TO_COFFEESHOP)) {
            ps.setLong(1, staffToCoffeeshop.getStaff_Id());
            ps.setLong(2, staffToCoffeeshop.getCoffeeshop_Id());
            ps.execute();
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void assignStaffToCoffeeshop(long staffId, String coffeeshop_title) {
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(ASSIGN_STAFF_TO_COFFEESHOP)) {
            ps.setLong(1, staffId);
            ps.setString(2, coffeeshop_title);
            ps.execute();
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }


    @Override
    public void deleteCoffeeshopFromStaff(long staffId, String coffeeshop_title) {
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_COFFEESHOP_FROM_STAFF)) {
            ps.setLong(1, staffId);
            ps.setString(2, coffeeshop_title);
            ps.execute();
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_ALL_STAFF_TO_COFFEESHOP)) {
            ps.execute();
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
