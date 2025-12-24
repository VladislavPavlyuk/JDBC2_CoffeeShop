package org.example.dao.customerDiscountDAO;

import org.example.model.CustomerDiscount;

import java.math.BigDecimal;
import java.util.List;

public interface CustomerDiscountDao {
    
    BigDecimal getMinDiscountValue();
    
    BigDecimal getMaxDiscountValue();
    
    List<CustomerDiscount> getCustomersWithMinDiscount();
    
    List<CustomerDiscount> getCustomersWithMaxDiscount();
    
    BigDecimal getAverageDiscountValue();
    
    boolean updateCustomerDiscount(String firstName, String lastName, BigDecimal newDiscountValue);
}



