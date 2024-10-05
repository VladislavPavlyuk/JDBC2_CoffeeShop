package org.example.dao.coffeeshopDAO;

import org.example.dao.CRUDInterface;
import org.example.model.Coffeeshop;

import java.util.List;

public interface CoffeeshopDao extends CRUDInterface<Coffeeshop> {

   List<Coffeeshop> findAllCoffeeshopsFromStaff(long stafftId);

}
