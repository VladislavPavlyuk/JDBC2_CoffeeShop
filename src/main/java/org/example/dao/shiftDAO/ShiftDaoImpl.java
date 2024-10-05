package org.example.dao.shiftDAO;

import org.example.dao.ConnectionFactory;
import org.example.exception.ConnectionDBException;
import org.example.model.Shift;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ShiftDaoImpl implements ShiftDao {

    private static final String SAVE_SHIFT = "INSERT INTO shifts(shift_title) VALUES(?)";
    private static final String FIND_ALL_SHIFTS = "SELECT * FROM shifts";
    private static final String DELETE_ALL_SHIFTS = "DELETE FROM shifts";
    private static final String FIND_ALL_SHIFTS_WITH_LESS_ORE_EQUAL_STAFF =
            "SELECT shifts.shift_title, COUNT(*) AS count_staff\n" +
                    "FROM shifts JOIN staff ON shifts.id = staff.shift_id\n" +
                    "GROUP BY shifts.shift_title\n" +
                    "HAVING COUNT(*) <= (?)";
    private static final String UPDATE_SHIFT = "UPDATE shifts SET shift_title = ? " +
            " WHERE shifts.id = ? ";
    private static final String DELETE_SHIFT = "DELETE FROM shifts WHERE shifts.id = ?";

    @Override
    public void save(Shift shift) {
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(SAVE_SHIFT)) {
            ps.setString(1, shift.getShiftTitle());
            ps.execute();
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void saveMany(List<Shift> shifts) {
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(SAVE_SHIFT)) {

            for (var currentShift : shifts) {
                ps.setString(1, currentShift.getShiftTitle());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void update(Shift shift) {
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SHIFT)) {
            ps.setString(1, shift.getShiftTitle());
            ps.setLong(2, shift.getId());
            ps.execute();
        } catch (ConnectionDBException |SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void delete(Shift shift) {
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_SHIFT)) {
            ps.setLong(1, shift.getId());
            ps.execute();
        } catch (ConnectionDBException |SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public List<Shift> findAll() {
        List<Shift> resultShifts = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL_SHIFTS)) {

            ResultSet result = ps.executeQuery();

            while (result.next()) {
                Shift addShift = new Shift();
                addShift.setId(result.getLong(1));
                addShift.setShiftTitle(result.getString(2));
                resultShifts.add(addShift);
            }
            return resultShifts;
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
        return resultShifts;
    }

    @Override
    public void deleteAll() {
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_ALL_SHIFTS)) {
            ps.execute();
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public List<String> findAllShiftsWithLessOrEqualStaffNumber(int numberStaff) {
        List<String> resultShiftTitles = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL_SHIFTS_WITH_LESS_ORE_EQUAL_STAFF)) {
            ps.setInt(1, numberStaff);
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                resultShiftTitles.add(result.getString(1));
            }
            return resultShiftTitles;
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
        return resultShiftTitles;
    }
}
