package org.example.dao.orderDAO;

import org.example.model.CustomerBaristaInfo;
import org.example.model.Order;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public interface OrderDao {
    
    List<Order> getOrdersByDate(Date date);
    
    List<Order> getOrdersByDateRange(Date startDate, Date endDate);
    
    int getDessertOrdersCountByDate(Date date);
    
    int getDrinkOrdersCountByDate(Date date);
    
    List<CustomerBaristaInfo> getCustomersWithDrinksToday();
    
    BigDecimal getAverageOrderAmountByDate(Date date);
    
    BigDecimal getMaxOrderAmountByDate(Date date);
    
    CustomerBaristaInfo getCustomerWithMaxOrderAmountByDate(Date date);
}










