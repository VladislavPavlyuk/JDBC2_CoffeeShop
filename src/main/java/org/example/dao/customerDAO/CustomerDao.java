package org.example.dao.customerDAO;

import org.example.model.Customer;

import java.util.List;

public interface CustomerDao {
    
    List<Customer> getYoungestCustomers();
    
    List<Customer> getOldestCustomers();
    
    List<Customer> getCustomersWithBirthdayToday();
    
    List<Customer> getCustomersWithoutEmail();
    
    boolean deleteCustomer(String firstName, String lastName);
}



