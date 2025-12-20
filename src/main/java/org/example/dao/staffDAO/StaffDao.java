package org.example.dao.staffDAO;


import org.example.model.Staff;

import java.util.List;

public interface StaffDao {

    void save(Staff staff) ;

    void saveMany(List<Staff> staff) ;

    void update(Staff staff) ;

    void delete(long staffId) ;

    List<Staff> findAll() ;

    List<Staff> findAllFromCoffeeshops(String coffeeshop_Title) ;

    void deleteAll() ;
    
    boolean updatePastryChefAddress(String firstName, String lastName, String newAddress);
    
    boolean updateBaristaPhone(String firstName, String lastName, String newPhone);
    
    boolean deleteWaiter(String firstName, String lastName);
    
    boolean deleteBarista(String firstName, String lastName);
    
    List<Staff> findAllBaristas();
    
    List<Staff> findAllWaiters();
}
