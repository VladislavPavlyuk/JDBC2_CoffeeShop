package org.example.dao.orderDAO;

import org.example.dao.ConnectionProvider;
import org.example.dao.exception.DaoException;
import org.example.dao.exception.ExceptionHandler;
import org.example.exception.ConnectionDBException;
import org.example.model.CustomerBaristaInfo;
import org.example.model.Order;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class OrderDaoImpl implements OrderDao {
    
    private final ConnectionProvider connectionProvider;
    
    private static final String GET_ORDERS_BY_DATE_SQL = 
        "SELECT " +
        "o.id, " +
        "o.order_number, " +
        "o.customer_id, " +
        "o.staff_id, " +
        "o.status_id, " +
        "o.order_date, " +
        "o.total_amount, " +
        "o.discount_amount, " +
        "o.final_amount, " +
        "o.notes " +
        "FROM orders o " +
        "WHERE DATE(o.order_date) = ? " +
        "ORDER BY o.order_date DESC";
    
    private static final String GET_ORDERS_BY_DATE_RANGE_SQL = 
        "SELECT " +
        "o.id, " +
        "o.order_number, " +
        "o.customer_id, " +
        "o.staff_id, " +
        "o.status_id, " +
        "o.order_date, " +
        "o.total_amount, " +
        "o.discount_amount, " +
        "o.final_amount, " +
        "o.notes " +
        "FROM orders o " +
        "WHERE DATE(o.order_date) >= ? AND DATE(o.order_date) <= ? " +
        "ORDER BY o.order_date DESC";
    
    private static final String GET_DESSERT_ORDERS_COUNT_BY_DATE_SQL = 
        "SELECT COUNT(DISTINCT oi.order_id) AS dessert_orders_count " +
        "FROM order_items oi " +
        "JOIN orders o ON oi.order_id = o.id " +
        "JOIN menu_items mi ON oi.menu_item_id = mi.id " +
        "JOIN menu_item_types mit ON mi.type_id = mit.id " +
        "WHERE DATE(o.order_date) = ? " +
        "AND mit.type_code = 'DESSERT'";
    
    private static final String GET_DRINK_ORDERS_COUNT_BY_DATE_SQL = 
        "SELECT COUNT(DISTINCT oi.order_id) AS drink_orders_count " +
        "FROM order_items oi " +
        "JOIN orders o ON oi.order_id = o.id " +
        "JOIN menu_items mi ON oi.menu_item_id = mi.id " +
        "JOIN menu_item_types mit ON mi.type_id = mit.id " +
        "WHERE DATE(o.order_date) = ? " +
        "AND mit.type_code = 'DRINK'";
    
    private static final String GET_CUSTOMERS_WITH_DRINKS_TODAY_SQL = 
        "SELECT DISTINCT " +
        "c.id AS customer_id, " +
        "c.firstname AS customer_firstname, " +
        "c.lastname AS customer_lastname, " +
        "c.middlename AS customer_middlename, " +
        "c.date_of_birth AS customer_date_of_birth, " +
        "s.id AS barista_id, " +
        "s.firstname AS barista_firstname, " +
        "s.lastname AS barista_lastname, " +
        "o.id AS order_id, " +
        "o.order_number " +
        "FROM orders o " +
        "JOIN order_items oi ON o.id = oi.order_id " +
        "JOIN menu_items mi ON oi.menu_item_id = mi.id " +
        "JOIN menu_item_types mit ON mi.type_id = mit.id " +
        "JOIN staff s ON o.staff_id = s.id " +
        "JOIN positions p ON s.position_id = p.id " +
        "LEFT JOIN customers c ON o.customer_id = c.id " +
        "WHERE DATE(o.order_date) = CURRENT_DATE " +
        "AND mit.type_code = 'DRINK' " +
        "AND p.position_code = 'BARISTA' " +
        "AND c.id IS NOT NULL " +
        "ORDER BY c.lastname, c.firstname";
    
    private static final String GET_AVERAGE_ORDER_AMOUNT_BY_DATE_SQL = 
        "SELECT AVG(o.final_amount) AS average_order_amount " +
        "FROM orders o " +
        "WHERE DATE(o.order_date) = ?";
    
    private static final String GET_MAX_ORDER_AMOUNT_BY_DATE_SQL = 
        "SELECT MAX(o.final_amount) AS max_order_amount " +
        "FROM orders o " +
        "WHERE DATE(o.order_date) = ?";
    
    private static final String GET_CUSTOMER_WITH_MAX_ORDER_AMOUNT_BY_DATE_SQL = 
        "SELECT " +
        "c.id AS customer_id, " +
        "c.firstname AS customer_firstname, " +
        "c.lastname AS customer_lastname, " +
        "c.middlename AS customer_middlename, " +
        "c.date_of_birth AS customer_date_of_birth, " +
        "s.id AS barista_id, " +
        "s.firstname AS barista_firstname, " +
        "s.lastname AS barista_lastname, " +
        "o.id AS order_id, " +
        "o.order_number " +
        "FROM orders o " +
        "JOIN customers c ON o.customer_id = c.id " +
        "JOIN staff s ON o.staff_id = s.id " +
        "WHERE DATE(o.order_date) = ? " +
        "AND o.final_amount = (" +
        "  SELECT MAX(o2.final_amount) " +
        "  FROM orders o2 " +
        "  WHERE DATE(o2.order_date) = ?" +
        ") " +
        "ORDER BY o.id " +
        "LIMIT 1";
    
    public OrderDaoImpl(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }
    
    @Override
    public List<Order> getOrdersByDate(Date date) {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_ORDERS_BY_DATE_SQL)) {
            ps.setDate(1, date);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = mapResultSetToOrder(rs);
                    orders.add(order);
                }
            }
        } catch (ConnectionDBException | SQLException e) {
            ExceptionHandler.handleAndLog(e, "getOrdersByDate");
            throw ExceptionHandler.handleException(e);
        }
        return orders;
    }
    
    @Override
    public List<Order> getOrdersByDateRange(Date startDate, Date endDate) {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_ORDERS_BY_DATE_RANGE_SQL)) {
            ps.setDate(1, startDate);
            ps.setDate(2, endDate);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = mapResultSetToOrder(rs);
                    orders.add(order);
                }
            }
        } catch (ConnectionDBException | SQLException e) {
            ExceptionHandler.handleAndLog(e, "getOrdersByDateRange");
            throw ExceptionHandler.handleException(e);
        }
        return orders;
    }
    
    @Override
    public int getDessertOrdersCountByDate(Date date) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_DESSERT_ORDERS_COUNT_BY_DATE_SQL)) {
            ps.setDate(1, date);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("dessert_orders_count");
                }
            }
        } catch (ConnectionDBException | SQLException e) {
            ExceptionHandler.handleAndLog(e, "getDessertOrdersCountByDate");
            throw ExceptionHandler.handleException(e);
        }
        return 0;
    }
    
    @Override
    public int getDrinkOrdersCountByDate(Date date) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_DRINK_ORDERS_COUNT_BY_DATE_SQL)) {
            ps.setDate(1, date);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("drink_orders_count");
                }
            }
        } catch (ConnectionDBException | SQLException e) {
            ExceptionHandler.handleAndLog(e, "getDrinkOrdersCountByDate");
            throw ExceptionHandler.handleException(e);
        }
        return 0;
    }
    
    @Override
    public List<CustomerBaristaInfo> getCustomersWithDrinksToday() {
        List<CustomerBaristaInfo> results = new ArrayList<>();
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_CUSTOMERS_WITH_DRINKS_TODAY_SQL)) {
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CustomerBaristaInfo info = mapResultSetToCustomerBaristaInfo(rs);
                    results.add(info);
                }
            }
        } catch (ConnectionDBException | SQLException e) {
            ExceptionHandler.handleAndLog(e, "getCustomersWithDrinksToday");
            throw ExceptionHandler.handleException(e);
        }
        return results;
    }
    
    @Override
    public BigDecimal getAverageOrderAmountByDate(Date date) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_AVERAGE_ORDER_AMOUNT_BY_DATE_SQL)) {
            ps.setDate(1, date);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal avg = rs.getBigDecimal("average_order_amount");
                    return avg != null ? avg : BigDecimal.ZERO;
                }
            }
        } catch (ConnectionDBException | SQLException e) {
            ExceptionHandler.handleAndLog(e, "getAverageOrderAmountByDate");
            throw ExceptionHandler.handleException(e);
        }
        return BigDecimal.ZERO;
    }
    
    @Override
    public BigDecimal getMaxOrderAmountByDate(Date date) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_MAX_ORDER_AMOUNT_BY_DATE_SQL)) {
            ps.setDate(1, date);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal max = rs.getBigDecimal("max_order_amount");
                    return max != null ? max : BigDecimal.ZERO;
                }
            }
        } catch (ConnectionDBException | SQLException e) {
            ExceptionHandler.handleAndLog(e, "getMaxOrderAmountByDate");
            throw ExceptionHandler.handleException(e);
        }
        return BigDecimal.ZERO;
    }
    
    @Override
    public CustomerBaristaInfo getCustomerWithMaxOrderAmountByDate(Date date) {
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(GET_CUSTOMER_WITH_MAX_ORDER_AMOUNT_BY_DATE_SQL)) {
            ps.setDate(1, date);
            ps.setDate(2, date);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCustomerBaristaInfo(rs);
                }
            }
        } catch (ConnectionDBException | SQLException e) {
            ExceptionHandler.handleAndLog(e, "getCustomerWithMaxOrderAmountByDate");
            throw ExceptionHandler.handleException(e);
        }
        return null;
    }
    
    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getLong("id"));
        order.setOrderNumber(rs.getString("order_number"));
        
        Long customerId = rs.getLong("customer_id");
        if (!rs.wasNull()) {
            order.setCustomerId(customerId);
        }
        
        order.setStaffId(rs.getLong("staff_id"));
        order.setStatusId(rs.getLong("status_id"));
        
        Timestamp orderDate = rs.getTimestamp("order_date");
        if (orderDate != null) {
            order.setOrderDate(orderDate);
        }
        
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setDiscountAmount(rs.getBigDecimal("discount_amount"));
        order.setFinalAmount(rs.getBigDecimal("final_amount"));
        order.setNotes(rs.getString("notes"));
        
        return order;
    }
    
    private CustomerBaristaInfo mapResultSetToCustomerBaristaInfo(ResultSet rs) throws SQLException {
        CustomerBaristaInfo info = new CustomerBaristaInfo();
        
        info.setCustomerId(rs.getLong("customer_id"));
        info.setCustomerFirstName(rs.getString("customer_firstname"));
        info.setCustomerLastName(rs.getString("customer_lastname"));
        info.setCustomerMiddleName(rs.getString("customer_middlename"));
        
        Date dob = rs.getDate("customer_date_of_birth");
        if (dob != null) {
            info.setCustomerDateOfBirth(dob);
        }
        
        info.setBaristaId(rs.getLong("barista_id"));
        info.setBaristaFirstName(rs.getString("barista_firstname"));
        info.setBaristaLastName(rs.getString("barista_lastname"));
        
        info.setOrderId(rs.getLong("order_id"));
        info.setOrderNumber(rs.getString("order_number"));
        
        return info;
    }
}








