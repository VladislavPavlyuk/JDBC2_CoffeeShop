package org.example.dao.scheduleDAO;

import org.example.model.StaffSchedule;

import java.util.List;

public interface ScheduleDao {
    
    List<StaffSchedule> getBaristaScheduleForWeek(String firstName, String lastName);
    
    List<StaffSchedule> getAllBaristasScheduleForWeek();
    
    List<StaffSchedule> getAllStaffScheduleForWeek();
}








