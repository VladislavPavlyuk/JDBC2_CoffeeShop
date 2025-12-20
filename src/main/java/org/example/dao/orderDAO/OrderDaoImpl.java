package org.example.dao.orderDAO;

import org.example.dao.ConnectionProvider;
import org.example.dao.exception.DaoException;
import org.example.dao.exception.ExceptionHandler;
import org.example.exception.ConnectionDBException;
import org.example.model.Order;

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
}
