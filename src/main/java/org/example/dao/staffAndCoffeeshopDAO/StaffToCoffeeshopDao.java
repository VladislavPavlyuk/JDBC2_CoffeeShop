package org.example.dao.staffAndCoffeeshopDAO;

import org.example.model.StaffToCoffeeshop;

public interface StaffToCoffeeshopDao {

    void save(StaffToCoffeeshop staffToCoffeeshop) ;

    void assignStaffToCoffeeshop(long staffId, String coffeshop_title);

    void deleteCoffeeshopFromStaff(long staffId, String coffeshop_title);

    void deleteAll() ;
}
