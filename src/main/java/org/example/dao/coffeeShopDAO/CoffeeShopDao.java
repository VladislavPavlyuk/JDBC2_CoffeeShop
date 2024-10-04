package org.example.dao.coffeeShopDAO;


import org.example.dao.CRUDInterface;
import org.example.model.CoffeeShop;

import java.util.List;

public interface CoffeeShopDao extends CRUDInterface<CoffeeShop> {

   List<CoffeeShop> findAllCoffeeShopsFromStaff(long stafftId);

}
