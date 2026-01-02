package org.example.dao.customerDAO;

import org.example.dao.ConnectionProvider;
import org.example.dao.exception.ExceptionHandler;
import org.example.exception.ConnectionDBException;
import org.example.model.Customer;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerDaoImpl implements CustomerDao {
    
    private final ConnectionProvider connectionProvider;
    
    private static final String GET_YOUNGEST_CUSTOMERS_SQL = 
        "SELECT c.id, c.firstname, c.lastname, c.middlename, c.date_of_birth " +
        "FROM customers c " +
        "WHERE c.is_active = TRUE " +
        "AND c.date_of_birth = (" +
        "  SELECT MAX(c2.date_of_birth) " +
        "  FROM customers c2 " +
        "  WHERE c2.is_active = TRUE" +
        ") " +
        "ORDER BY c.lastname, c.firstname";
    
    private static final String GET_OLDEST_CUSTOMERS_SQL = 
        "SELECT c.id, c.firstname, c.lastname, c.middlename, c.date_of_birth " +
        "FROM customers c " +
        "WHERE c.is_active = TRUE " +
        "AND c.date_of_birth = (" +
        "  SELECT MIN(c2.date_of_birth) " +
        "  FROM customers c2 " +
        "  WHERE c2.is_active = TRUE" +
        ") " +
        "ORDER BY c.lastname, c.firstname";
    
    private static final String GET_CUSTOMERS_WITH_BIRTHDAY_TODAY_SQL = 
        "SELECT c.id, c.firstname, c.lastname, c.middlename, c.date_of_birth " +
        "FROM customers c " +
        "WHERE c.is_active = TRUE " +
        "AND EXTRACT(MONTH FROM c.date_of_birth) = EXTRACT(MONTH FROM CURRENT_DATE) " +
        "AND EXTRACT(DAY FROM c.date_of_birth) = EXTRACT(DAY FROM CURRENT_DATE) " +
        "ORDER BY c.lastname, c.firstname";
    
    private static final String GET_CUSTOMERS_WITHOUT_EMAIL_SQL = 
        "SELECT DISTINCT c.id, c.firstname, c.lastname, c.middlename, c.date_of_birth " +
        "FROM customers c " +
        "WHERE c.is_active = TRUE " +
        "AND c.id NOT IN (" +
        "  SELECT DISTINCT cc.customer_id " +
        "  FROM customer_contacts cc " +
        "  WHERE cc.contact_type = 'EMAIL' " +
        "  AND cc.is_active = TRUE" +
        ") " +
        "ORDER BY c.lastname, c.firstname";
    
    private static final String DELETE_CUSTOMER_SQL = 
        "UPDATE customers " +
        "SET is_active = FALSE, " +
        "    updated_at = CURRENT_TIMESTAMP " +
        "WHERE firstname = ? " +
        "AND lastname = ? " +
        "AND is_active = TRUE";
    
    public CustomerDaoImpl(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }
    
    @Override
    public List<Customer> getYoungestCustomers() {
        List<Customer> customers = new ArrayList<>();
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_YOUNGEST_CUSTOMERS_SQL);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Customer customer = mapResultSetToCustomer(rs);
                customers.add(customer);
            }
        } catch (ConnectionDBException | SQLException e) {
            ExceptionHandler.handleAndLog(e, "getYoungestCustomers");
            throw ExceptionHandler.handleException(e);
        }
        return customers;
    }
    
    @Override
    public List<Customer> getOldestCustomers() {
        List<Customer> customers = new ArrayList<>();
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_OLDEST_CUSTOMERS_SQL);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Customer customer = mapResultSetToCustomer(rs);
                customers.add(customer);
            }
        } catch (ConnectionDBException | SQLException e) {
            ExceptionHandler.handleAndLog(e, "getOldestCustomers");
            throw ExceptionHandler.handleException(e);
        }
        return customers;
    }
    
    @Override
    public List<Customer> getCustomersWithBirthdayToday() {
        List<Customer> customers = new ArrayList<>();
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_CUSTOMERS_WITH_BIRTHDAY_TODAY_SQL);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Customer customer = mapResultSetToCustomer(rs);
                customers.add(customer);
            }
        } catch (ConnectionDBException | SQLException e) {
            ExceptionHandler.handleAndLog(e, "getCustomersWithBirthdayToday");
            throw ExceptionHandler.handleException(e);
        }
        return customers;
    }
    
    @Override
    public List<Customer> getCustomersWithoutEmail() {
        List<Customer> customers = new ArrayList<>();
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_CUSTOMERS_WITHOUT_EMAIL_SQL);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Customer customer = mapResultSetToCustomer(rs);
                customers.add(customer);
            }
        } catch (ConnectionDBException | SQLException e) {
            ExceptionHandler.handleAndLog(e, "getCustomersWithoutEmail");
            throw ExceptionHandler.handleException(e);
        }
        return customers;
    }
    
    @Override
    public boolean deleteCustomer(String firstName, String lastName) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_CUSTOMER_SQL)) {
            
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                try (PreparedStatement contactPs = conn.prepareStatement(
                    "UPDATE customer_contacts " +
                    "SET is_active = FALSE " +
                    "WHERE customer_id = (" +
                    "  SELECT id FROM customers " +
                    "  WHERE firstname = ? " +
                    "  AND lastname = ?" +
                    ") " +
                    "AND is_active = TRUE")) {
                    contactPs.setString(1, firstName);
                    contactPs.setString(2, lastName);
                    contactPs.executeUpdate();
                }
                
                try (PreparedStatement discountPs = conn.prepareStatement(
                    "UPDATE customer_discounts " +
                    "SET is_active = FALSE " +
                    "WHERE customer_id = (" +
                    "  SELECT id FROM customers " +
                    "  WHERE firstname = ? " +
                    "  AND lastname = ?" +
                    ") " +
                    "AND is_active = TRUE")) {
                    discountPs.setString(1, firstName);
                    discountPs.setString(2, lastName);
                    discountPs.executeUpdate();
                }
            }
            
            return rowsAffected > 0;
        } catch (ConnectionDBException | SQLException e) {
            ExceptionHandler.handleAndLog(e, "deleteCustomer");
            throw ExceptionHandler.handleException(e);
        }
    }
    
    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getLong("id"));
        customer.setFirstName(rs.getString("firstname"));
        customer.setLastName(rs.getString("lastname"));
        customer.setMiddleName(rs.getString("middlename"));
        
        Date dateOfBirth = rs.getDate("date_of_birth");
        if (dateOfBirth != null) {
            customer.setDateOfBirth(dateOfBirth);
        }
        
        return customer;
    }
}









