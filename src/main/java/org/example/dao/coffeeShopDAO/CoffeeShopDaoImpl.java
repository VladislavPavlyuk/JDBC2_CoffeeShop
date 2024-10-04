package org.example.dao.coffeeShopDAO;

import org.example.dao.ConnectionFactory;
import org.example.exception.ConnectionDBException;
import org.example.model.CoffeeShop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CoffeeShopDaoImpl implements CoffeeShopDao {

    private static final String SAVE_COFFEESHOP = "INSERT INTO coffeeshops(coffeeShop_Title, coffeeShop_description) VALUES(?,?)";
    private static final String FIND_ALL_COFFEESHOPS = "SELECT * FROM coffeeshops";
    private static final String FIND_ALL_COFFEESHOPS_FROM_STAFF = "SELECT coffeeshops.id, coffeeshops.coffeeShop_Title, coffeeshops.coffeeShop_description\n" +
            "FROM coffeeshops JOIN staffandcoffeeshops ON coffeeshops.id = staffandcoffeeshops.coffeeshops_id\n" +
            "JOIN staff ON staffandcoffeeshops.staff_id = staff.id\n" +
            "WHERE staff.id = ? ";

    private static final String DELETE_ALL_COFFEESHOPS = "DELETE FROM coffeeshops";
    private static final String UPDATE_COFFEESHOPS = "UPDATE coffeeshops SET coffeeShop_Title = ?, coffeeShop_description = ? " +
            " WHERE coffeeshops.id = ? ";
    private static final String DELETE_COURSE = "DELETE FROM coffeeshops WHERE coffeeshops.id = ?";

    @Override
    public void save(CoffeeShop coffeeShop) {
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(SAVE_COFFEESHOP)) {
            ps.setString(1, coffeeShop.getCoffeeshopName());
            ps.setString(2, coffeeShop.getCoffeeshopDescription());
            ps.execute();
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void saveMany(List<CoffeeShop> coffeeShops) {
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(SAVE_COFFEESHOP)) {

            for (var currentCoffeeShop : coffeeShops) {
                ps.setString(1, currentCoffeeShop.getCoffeeshopName());
                ps.setString(2, currentCoffeeShop.getCoffeeshopDescription());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void update(CoffeeShop coffeeShop) {
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_COFFEESHOPS)) {
            ps.setString(1, coffeeShop.getCoffeeshopName());
            ps.setString(2, coffeeShop.getCoffeeshopDescription());
            ps.setLong(3, coffeeShop.getId());
            ps.execute();
        } catch (ConnectionDBException |SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void delete(CoffeeShop coffeeShop) {
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_COURSE)) {
            ps.setLong(1, coffeeShop.getId());
            ps.execute();
        } catch (ConnectionDBException |SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    @Override
    public List<CoffeeShop> findAll() {
        List<CoffeeShop> resultAddCoffeeShops = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL_COFFEESHOPS);
             ResultSet result = ps.executeQuery()) {

            while (result.next()) {
                CoffeeShop addCoffeeShop = new CoffeeShop();
                addCoffeeShop.setId(result.getLong(1));
                addCoffeeShop.setCoffeeshopName(result.getString(2));
                addCoffeeShop.setCoffeeshopDescription(result.getString(3));
                resultAddCoffeeShops.add(addCoffeeShop);
            }
            return resultAddCoffeeShops;
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
        return resultAddCoffeeShops;
    }

    @Override
    public List<CoffeeShop> findAllCoffeeShopsFromStaff(long coffeeShop_Id) {
        List<CoffeeShop> resultCoffeeShops = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL_COFFEESHOPS_FROM_STAFF)) {

            ps.setLong(1,coffeeShop_Id);

            try (ResultSet result = ps.executeQuery()) {
                while (result.next()) {
                    CoffeeShop addCoffeeShop = new CoffeeShop();
                    addCoffeeShop.setId(result.getLong(1));
                    addCoffeeShop.setCoffeeshopName(result.getString(2));
                    addCoffeeShop.setCoffeeshopDescription(result.getString(3));
                    resultCoffeeShops.add(addCoffeeShop);
                }
                return resultCoffeeShops;
            }
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
        return resultCoffeeShops;
    }

    @Override
    public void deleteAll() {
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_ALL_COFFEESHOPS)) {
            ps.execute();
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }
}
