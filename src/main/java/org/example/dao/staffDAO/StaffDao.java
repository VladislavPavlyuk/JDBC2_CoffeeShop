package org.example.dao.staffDAO;


import org.example.model.Staff;

import java.util.List;

public interface StaffDao {

    void save(Staff staff) ;

    void saveMany(List<Staff> staff) ;

    void update(Staff staff) ;

    void delete(long studentId) ;

    List<Staff> findAll() ;

    List<Staff> findAllFromCourse(String courseName) ;

    void deleteAll() ;
}
