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

    private static final String SAVE_GROUP = "INSERT INTO groups(group_name) VALUES(?)";
    private static final String FIND_ALL_GROUPS = "SELECT * FROM groups";
    private static final String DELETE_ALL_GROUPS = "DELETE FROM groups";
    private static final String FIND_ALL_GROUPS_WITH_LESS_ORE_EQUAL_STUDENTS =
            "SELECT groups.group_name, COUNT(*) AS count_students\n" +
                    "FROM groups JOIN students ON groups.id = students.group_id\n" +
                    "GROUP BY groups.group_name\n" +
                    "HAVING COUNT(*) <= (?)";
    private static final String UPDATE_GROUP = "UPDATE groups SET group_name = ? " +
            " WHERE groups.id = ? ";
    private static final String DELETE_GROUP = "DELETE FROM groups WHERE groups.id = ?";

    @Override
    public void save(Shift shift) {
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(SAVE_GROUP)) {
            ps.setString(1, shift.getShiftName());
            ps.execute();
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void saveMany(List<Shift> shifts) {
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(SAVE_GROUP)) {

            for (var currentGroup : shifts) {
                ps.setString(1, currentGroup.getShiftName());
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
             PreparedStatement ps = conn.prepareStatement(UPDATE_GROUP)) {
            ps.setString(1, shift.getShiftName());
            ps.setLong(2, shift.getId());
            ps.execute();
        } catch (ConnectionDBException |SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void delete(Shift shift) {
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_GROUP)) {
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
             PreparedStatement ps = conn.prepareStatement(FIND_ALL_GROUPS)) {

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
             PreparedStatement ps = conn.prepareStatement(DELETE_ALL_GROUPS)) {
            ps.execute();
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public List<String> findAllShiftsWithLessOrEqualStaffNumber(int numberStudents) {
        List<String> resultGroupNames = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL_GROUPS_WITH_LESS_ORE_EQUAL_STUDENTS)) {
            ps.setInt(1, numberStudents);
            ResultSet result = ps.executeQuery();

            while (result.next()) {
                resultGroupNames.add(result.getString(1));
            }
            return resultGroupNames;
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
        return resultGroupNames;
    }
}
