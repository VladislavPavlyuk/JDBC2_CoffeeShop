package org.example.dao.menuDAO;

import org.example.dao.ConnectionProvider;
import org.example.dao.exception.DaoException;
import org.example.dao.exception.ExceptionHandler;
import org.example.exception.ConnectionDBException;
import org.example.model.MenuItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// handles database operations for menu items
public class MenuDaoImpl implements MenuDao {
    
    private final ConnectionProvider connectionProvider;
    
    private static final String FIND_ALL_DESSERTS_SQL = 
        "SELECT " +
        "mi.id, " +
        "mi.item_code, " +
        "COALESCE(mit_uk.name, mit_en.name, mi.item_code) AS name, " +
        "mi.base_price, " +
        "CASE " +
        "  WHEN mi.is_available AND mi.is_active THEN 'Available' " +
        "  WHEN mi.is_active THEN 'Unavailable' " +
        "  ELSE 'Removed' " +
        "END AS status " +
        "FROM menu_items mi " +
        "JOIN menu_item_types mit_type ON mi.type_id = mit_type.id " +
        "LEFT JOIN menu_item_translations mit_en ON mi.id = mit_en.menu_item_id " +
        "  AND mit_en.language_id = (SELECT id FROM languages WHERE code = 'en') " +
        "LEFT JOIN menu_item_translations mit_uk ON mi.id = mit_uk.menu_item_id " +
        "  AND mit_uk.language_id = (SELECT id FROM languages WHERE code = 'uk') " +
        "WHERE mit_type.type_code = 'DESSERT' " +
        "ORDER BY mi.is_active DESC, mi.sort_order, mi.item_code";
    
    private static final String FIND_ALL_DRINKS_SQL = 
        "SELECT " +
        "mi.id, " +
        "mi.item_code, " +
        "COALESCE(mit_uk.name, mit_en.name, mi.item_code) AS name, " +
        "mi.base_price, " +
        "CASE " +
        "  WHEN mi.is_available AND mi.is_active THEN 'Available' " +
        "  WHEN mi.is_active THEN 'Unavailable' " +
        "  ELSE 'Removed' " +
        "END AS status " +
        "FROM menu_items mi " +
        "JOIN menu_item_types mit_type ON mi.type_id = mit_type.id " +
        "LEFT JOIN menu_item_translations mit_en ON mi.id = mit_en.menu_item_id " +
        "  AND mit_en.language_id = (SELECT id FROM languages WHERE code = 'en') " +
        "LEFT JOIN menu_item_translations mit_uk ON mi.id = mit_uk.menu_item_id " +
        "  AND mit_uk.language_id = (SELECT id FROM languages WHERE code = 'uk') " +
        "WHERE mit_type.type_code = 'DRINK' " +
        "ORDER BY mi.is_active DESC, mi.sort_order, mi.item_code";
    
    public MenuDaoImpl(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }
    
    @Override
    public List<MenuItem> findAllDesserts() {
        List<MenuItem> desserts = new ArrayList<>();
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL_DESSERTS_SQL);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                MenuItem item = mapResultSetToMenuItem(rs);
                desserts.add(item);
            }
        } catch (ConnectionDBException | SQLException e) {
            ExceptionHandler.handleAndLog(e, "findAllDesserts");
            throw ExceptionHandler.handleException(e);
        }
        return desserts;
    }
    
    @Override
    public List<MenuItem> findAllDrinks() {
        List<MenuItem> drinks = new ArrayList<>();
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL_DRINKS_SQL);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                MenuItem item = mapResultSetToMenuItem(rs);
                drinks.add(item);
            }
        } catch (ConnectionDBException | SQLException e) {
            ExceptionHandler.handleAndLog(e, "findAllDrinks");
            throw ExceptionHandler.handleException(e);
        }
        return drinks;
    }
    
    private MenuItem mapResultSetToMenuItem(ResultSet rs) throws SQLException {
        MenuItem item = new MenuItem();
        item.setId(rs.getLong("id"));
        item.setItemCode(rs.getString("item_code"));
        item.setName(rs.getString("name"));
        item.setBasePrice(rs.getDouble("base_price"));
        item.setStatus(rs.getString("status"));
        return item;
    }
}
