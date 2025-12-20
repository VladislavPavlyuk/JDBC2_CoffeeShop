package org.example.dao.customerDiscountDAO;

import org.example.dao.ConnectionProvider;
import org.example.dao.exception.DaoException;
import org.example.dao.exception.ExceptionHandler;
import org.example.exception.ConnectionDBException;
import org.example.model.CustomerDiscount;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// handles database operations for customer discounts
public class CustomerDiscountDaoImpl implements CustomerDiscountDao {
    
    private final ConnectionProvider connectionProvider;
    
    private static final String GET_MIN_DISCOUNT_SQL = 
        "SELECT MIN(cd.discount_value) AS min_discount_value " +
        "FROM customer_discounts cd " +
        "WHERE cd.is_active = TRUE " +
        "AND CURRENT_DATE >= cd.valid_from " +
        "AND (cd.valid_to IS NULL OR CURRENT_DATE <= cd.valid_to)";
    
    private static final String GET_MAX_DISCOUNT_SQL = 
        "SELECT MAX(cd.discount_value) AS max_discount_value " +
        "FROM customer_discounts cd " +
        "WHERE cd.is_active = TRUE " +
        "AND CURRENT_DATE >= cd.valid_from " +
        "AND (cd.valid_to IS NULL OR CURRENT_DATE <= cd.valid_to)";
    
    private static final String GET_CUSTOMERS_WITH_MIN_DISCOUNT_SQL = 
        "SELECT " +
        "c.id AS customer_id, " +
        "c.firstname, " +
        "c.lastname, " +
        "c.middlename, " +
        "cd.discount_value, " +
        "dt.type_code AS discount_type, " +
        "cd.valid_from, " +
        "cd.valid_to " +
        "FROM customers c " +
        "JOIN customer_discounts cd ON c.id = cd.customer_id " +
        "JOIN discount_types dt ON cd.discount_type_id = dt.id " +
        "WHERE cd.is_active = TRUE " +
        "AND c.is_active = TRUE " +
        "AND CURRENT_DATE >= cd.valid_from " +
        "AND (cd.valid_to IS NULL OR CURRENT_DATE <= cd.valid_to) " +
        "AND cd.discount_value = (" +
        "  SELECT MIN(cd2.discount_value) " +
        "  FROM customer_discounts cd2 " +
        "  WHERE cd2.is_active = TRUE " +
        "  AND CURRENT_DATE >= cd2.valid_from " +
        "  AND (cd2.valid_to IS NULL OR CURRENT_DATE <= cd2.valid_to)" +
        ") " +
        "ORDER BY c.lastname, c.firstname";
    
    private static final String GET_CUSTOMERS_WITH_MAX_DISCOUNT_SQL = 
        "SELECT " +
        "c.id AS customer_id, " +
        "c.firstname, " +
        "c.lastname, " +
        "c.middlename, " +
        "cd.discount_value, " +
        "dt.type_code AS discount_type, " +
        "cd.valid_from, " +
        "cd.valid_to " +
        "FROM customers c " +
        "JOIN customer_discounts cd ON c.id = cd.customer_id " +
        "JOIN discount_types dt ON cd.discount_type_id = dt.id " +
        "WHERE cd.is_active = TRUE " +
        "AND c.is_active = TRUE " +
        "AND CURRENT_DATE >= cd.valid_from " +
        "AND (cd.valid_to IS NULL OR CURRENT_DATE <= cd.valid_to) " +
        "AND cd.discount_value = (" +
        "  SELECT MAX(cd2.discount_value) " +
        "  FROM customer_discounts cd2 " +
        "  WHERE cd2.is_active = TRUE " +
        "  AND CURRENT_DATE >= cd2.valid_from " +
        "  AND (cd2.valid_to IS NULL OR CURRENT_DATE <= cd2.valid_to)" +
        ") " +
        "ORDER BY c.lastname, c.firstname";
    
    private static final String GET_AVERAGE_DISCOUNT_SQL = 
        "SELECT " +
        "AVG(cd.discount_value) AS avg_discount_value " +
        "FROM customer_discounts cd " +
        "WHERE cd.is_active = TRUE " +
        "AND CURRENT_DATE >= cd.valid_from " +
        "AND (cd.valid_to IS NULL OR CURRENT_DATE <= cd.valid_to)";
    
    public CustomerDiscountDaoImpl(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }
    
    @Override
    public BigDecimal getMinDiscountValue() {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_MIN_DISCOUNT_SQL);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                BigDecimal value = rs.getBigDecimal("min_discount_value");
                return value != null ? value : BigDecimal.ZERO;
            }
            return BigDecimal.ZERO;
        } catch (ConnectionDBException | SQLException e) {
            ExceptionHandler.handleAndLog(e, "getMinDiscountValue");
            throw ExceptionHandler.handleException(e);
        }
    }
    
    @Override
    public BigDecimal getMaxDiscountValue() {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_MAX_DISCOUNT_SQL);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                BigDecimal value = rs.getBigDecimal("max_discount_value");
                return value != null ? value : BigDecimal.ZERO;
            }
            return BigDecimal.ZERO;
        } catch (ConnectionDBException | SQLException e) {
            ExceptionHandler.handleAndLog(e, "getMaxDiscountValue");
            throw ExceptionHandler.handleException(e);
        }
    }
    
    @Override
    public List<CustomerDiscount> getCustomersWithMinDiscount() {
        List<CustomerDiscount> customers = new ArrayList<>();
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_CUSTOMERS_WITH_MIN_DISCOUNT_SQL);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                CustomerDiscount customer = mapResultSetToCustomerDiscount(rs);
                customers.add(customer);
            }
        } catch (ConnectionDBException | SQLException e) {
            ExceptionHandler.handleAndLog(e, "getCustomersWithMinDiscount");
            throw ExceptionHandler.handleException(e);
        }
        return customers;
    }
    
    @Override
    public List<CustomerDiscount> getCustomersWithMaxDiscount() {
        List<CustomerDiscount> customers = new ArrayList<>();
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_CUSTOMERS_WITH_MAX_DISCOUNT_SQL);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                CustomerDiscount customer = mapResultSetToCustomerDiscount(rs);
                customers.add(customer);
            }
        } catch (ConnectionDBException | SQLException e) {
            ExceptionHandler.handleAndLog(e, "getCustomersWithMaxDiscount");
            throw ExceptionHandler.handleException(e);
        }
        return customers;
    }
    
    @Override
    public BigDecimal getAverageDiscountValue() {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_AVERAGE_DISCOUNT_SQL);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                BigDecimal value = rs.getBigDecimal("avg_discount_value");
                return value != null ? value : BigDecimal.ZERO;
            }
            return BigDecimal.ZERO;
        } catch (ConnectionDBException | SQLException e) {
            ExceptionHandler.handleAndLog(e, "getAverageDiscountValue");
            throw ExceptionHandler.handleException(e);
        }
    }
    
    private CustomerDiscount mapResultSetToCustomerDiscount(ResultSet rs) throws SQLException {
        CustomerDiscount customer = new CustomerDiscount();
        customer.setCustomerId(rs.getLong("customer_id"));
        customer.setFirstName(rs.getString("firstname"));
        customer.setLastName(rs.getString("lastname"));
        customer.setMiddleName(rs.getString("middlename"));
        customer.setDiscountValue(rs.getBigDecimal("discount_value"));
        customer.setDiscountType(rs.getString("discount_type"));
        
        Date validFrom = rs.getDate("valid_from");
        if (validFrom != null) {
            customer.setValidFrom(validFrom);
        }
        
        Date validTo = rs.getDate("valid_to");
        if (validTo != null) {
            customer.setValidTo(validTo);
        }
        
        return customer;
    }
}
