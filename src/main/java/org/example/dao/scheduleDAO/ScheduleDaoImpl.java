package org.example.dao.scheduleDAO;

import org.example.dao.ConnectionProvider;
import org.example.dao.exception.ExceptionHandler;
import org.example.exception.ConnectionDBException;
import org.example.model.StaffSchedule;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ScheduleDaoImpl implements ScheduleDao {
    
    private final ConnectionProvider connectionProvider;
    
    private static final String GET_BARISTA_SCHEDULE_FOR_WEEK_SQL = 
        "SELECT " +
        "ss.id, " +
        "ss.staff_id, " +
        "s.firstname AS staff_firstname, " +
        "s.lastname AS staff_lastname, " +
        "ss.shift_id, " +
        "sh.shift_code, " +
        "ss.work_date, " +
        "ss.notes " +
        "FROM staff_schedule ss " +
        "JOIN staff s ON ss.staff_id = s.id " +
        "JOIN positions p ON s.position_id = p.id " +
        "JOIN shifts sh ON ss.shift_id = sh.id " +
        "WHERE s.firstname = ? " +
        "AND s.lastname = ? " +
        "AND p.position_code = 'BARISTA' " +
        "AND ss.work_date >= DATE_TRUNC('week', CURRENT_DATE) " +
        "AND ss.work_date < DATE_TRUNC('week', CURRENT_DATE) + INTERVAL '7 days' " +
        "ORDER BY ss.work_date, sh.shift_code";
    
    private static final String GET_ALL_BARISTAS_SCHEDULE_FOR_WEEK_SQL = 
        "SELECT " +
        "ss.id, " +
        "ss.staff_id, " +
        "s.firstname AS staff_firstname, " +
        "s.lastname AS staff_lastname, " +
        "ss.shift_id, " +
        "sh.shift_code, " +
        "ss.work_date, " +
        "ss.notes " +
        "FROM staff_schedule ss " +
        "JOIN staff s ON ss.staff_id = s.id " +
        "JOIN positions p ON s.position_id = p.id " +
        "JOIN shifts sh ON ss.shift_id = sh.id " +
        "WHERE p.position_code = 'BARISTA' " +
        "AND ss.work_date >= DATE_TRUNC('week', CURRENT_DATE) " +
        "AND ss.work_date < DATE_TRUNC('week', CURRENT_DATE) + INTERVAL '7 days' " +
        "ORDER BY s.lastname, s.firstname, ss.work_date, sh.shift_code";
    
    private static final String GET_ALL_STAFF_SCHEDULE_FOR_WEEK_SQL = 
        "SELECT " +
        "ss.id, " +
        "ss.staff_id, " +
        "s.firstname AS staff_firstname, " +
        "s.lastname AS staff_lastname, " +
        "ss.shift_id, " +
        "sh.shift_code, " +
        "ss.work_date, " +
        "ss.notes " +
        "FROM staff_schedule ss " +
        "JOIN staff s ON ss.staff_id = s.id " +
        "JOIN shifts sh ON ss.shift_id = sh.id " +
        "WHERE ss.work_date >= DATE_TRUNC('week', CURRENT_DATE) " +
        "AND ss.work_date < DATE_TRUNC('week', CURRENT_DATE) + INTERVAL '7 days' " +
        "ORDER BY s.lastname, s.firstname, ss.work_date, sh.shift_code";
    
    public ScheduleDaoImpl(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }
    
    @Override
    public List<StaffSchedule> getBaristaScheduleForWeek(String firstName, String lastName) {
        List<StaffSchedule> schedules = new ArrayList<>();
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_BARISTA_SCHEDULE_FOR_WEEK_SQL)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StaffSchedule schedule = mapResultSetToStaffSchedule(rs);
                    schedules.add(schedule);
                }
            }
        } catch (ConnectionDBException | SQLException e) {
            ExceptionHandler.handleAndLog(e, "getBaristaScheduleForWeek");
            throw ExceptionHandler.handleException(e);
        }
        return schedules;
    }
    
    @Override
    public List<StaffSchedule> getAllBaristasScheduleForWeek() {
        List<StaffSchedule> schedules = new ArrayList<>();
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_ALL_BARISTAS_SCHEDULE_FOR_WEEK_SQL)) {
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StaffSchedule schedule = mapResultSetToStaffSchedule(rs);
                    schedules.add(schedule);
                }
            }
        } catch (ConnectionDBException | SQLException e) {
            ExceptionHandler.handleAndLog(e, "getAllBaristasScheduleForWeek");
            throw ExceptionHandler.handleException(e);
        }
        return schedules;
    }
    
    @Override
    public List<StaffSchedule> getAllStaffScheduleForWeek() {
        List<StaffSchedule> schedules = new ArrayList<>();
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_ALL_STAFF_SCHEDULE_FOR_WEEK_SQL)) {
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StaffSchedule schedule = mapResultSetToStaffSchedule(rs);
                    schedules.add(schedule);
                }
            }
        } catch (ConnectionDBException | SQLException e) {
            ExceptionHandler.handleAndLog(e, "getAllStaffScheduleForWeek");
            throw ExceptionHandler.handleException(e);
        }
        return schedules;
    }
    
    private StaffSchedule mapResultSetToStaffSchedule(ResultSet rs) throws SQLException {
        StaffSchedule schedule = new StaffSchedule();
        
        schedule.setId(rs.getLong("id"));
        schedule.setStaffId(rs.getLong("staff_id"));
        schedule.setStaffFirstName(rs.getString("staff_firstname"));
        schedule.setStaffLastName(rs.getString("staff_lastname"));
        schedule.setShiftId(rs.getLong("shift_id"));
        schedule.setShiftCode(rs.getString("shift_code"));
        
        Date workDate = rs.getDate("work_date");
        if (workDate != null) {
            schedule.setWorkDate(workDate);
        }
        
        schedule.setNotes(rs.getString("notes"));
        
        return schedule;
    }
}










