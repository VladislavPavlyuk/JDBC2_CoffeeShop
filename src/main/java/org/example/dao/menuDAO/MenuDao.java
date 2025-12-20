package org.example.dao.menuDAO;

import org.example.model.MenuItem;

import java.util.List;

public interface MenuDao {
    
    List<MenuItem> findAllDesserts();
    
    List<MenuItem> findAllDrinks();
}

