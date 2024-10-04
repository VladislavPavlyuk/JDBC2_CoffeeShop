package org.example.dao.staffAndCoffeeShopDAO;

import org.example.dao.ConnectionFactory;
import org.example.exception.ConnectionDBException;
import org.example.model.StaffToCoffeeShop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StaffToCoffeeShopDaoImpl implements StaffToCoffeeShopDao {

    private static final String SAVE_STUDENT_TO_COURSE = "INSERT INTO studentsandcourses(student_id,course_id) VALUES(?,?)";
    private static final String DELETE_ALL_STUDENT_TO_COURSE = "DELETE FROM studentsandcourses";
    private static final String ASSIGN_STUDENT_TO_COURSE = "INSERT INTO studentsandcourses(student_id,course_id) " +
                " VALUES(?, (SELECT courses.id FROM courses WHERE courses.course_name = ?))";
    private static final String DELETE_COURSE_FROM_STUDENT = "DELETE FROM studentsandcourses " +
            " WHERE  studentsandcourses.student_id = ? AND " +
            " studentsandcourses.course_id = (SELECT courses.id FROM courses WHERE courses.course_name = ?) ";

    @Override
    public void save(StaffToCoffeeShop staffToCoffeeShop) {
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(SAVE_STUDENT_TO_COURSE)) {
            ps.setLong(1, staffToCoffeeShop.getStaffId());
            ps.setLong(2, staffToCoffeeShop.getCoffeeshopId());
            ps.execute();
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void assignStudentToCourse(long studentId, String courseName) {
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(ASSIGN_STUDENT_TO_COURSE)) {
            ps.setLong(1, studentId);
            ps.setString(2, courseName);
            ps.execute();
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void deleteCourseFromStudent(long studentId, String courseName) {
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_COURSE_FROM_STUDENT)) {
            ps.setLong(1, studentId);
            ps.setString(2, courseName);
            ps.execute();
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_ALL_STUDENT_TO_COURSE)) {
            ps.execute();
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
