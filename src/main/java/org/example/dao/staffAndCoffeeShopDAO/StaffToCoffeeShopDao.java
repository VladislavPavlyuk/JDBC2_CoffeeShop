package org.example.dao.staffAndCoffeeShopDAO;


import org.example.model.StaffToCoffeeShop;

public interface StaffToCoffeeShopDao {

    void save(StaffToCoffeeShop staffToCoffeeShop) ;

    void assignStudentToCourse(long studentId, String coureName);

    void deleteCourseFromStudent(long studentId, String coureName);

    void deleteAll() ;
}
