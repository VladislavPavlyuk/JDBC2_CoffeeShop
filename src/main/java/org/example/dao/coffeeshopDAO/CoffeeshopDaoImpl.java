package org.example.dao.coffeeshopDAO;

import org.example.dao.ConnectionFactory;
import org.example.exception.ConnectionDBException;
import org.example.model.Coffeeshop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CoffeeshopDaoImpl implements CoffeeshopDao {

    private static final String SAVE_COFFEESHOP = "INSERT INTO coffeeshops(coffeeshop_Title, coffeeshop_description) VALUES(?,?)";
    private static final String FIND_ALL_COFFEESHOPS = "SELECT * FROM coffeeshops";
    private static final String FIND_ALL_COFFEESHOPS_FROM_STAFF = "SELECT coffeeshops.id, coffeeshops.coffeeshop_Title, coffeeshops.coffeeshop_description\n" +
            "FROM coffeeshops JOIN staffandcoffeeshops ON coffeeshops.id = staffandcoffeeshops.coffeeshops_id\n" +
            "JOIN staff ON staffandcoffeeshops.staff_id = staff.id\n" +
            "WHERE staff.id = ? ";

    private static final String DELETE_ALL_COFFEESHOPS = "DELETE FROM coffeeshops";
    private static final String UPDATE_COFFEESHOPS = "UPDATE coffeeshops SET coffeeshop_Title = ?, coffeeshop_description = ? " +
            " WHERE coffeeshops.id = ? ";
    private static final String DELETE_COFFEESHOP = "DELETE FROM coffeeshops WHERE coffeeshops.id = ?";

    @Override
    public void save(Coffeeshop coffeeshop) {
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(SAVE_COFFEESHOP)) {
            ps.setString(1, coffeeshop.getCoffeeshopTitle());
            ps.setString(2, coffeeshop.getCoffeeshopDescription());
            ps.execute();
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void saveMany(List<Coffeeshop> coffeeshops) {
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(SAVE_COFFEESHOP)) {

            for (var currentCoffeeShop : coffeeshops) {
                ps.setString(1, currentCoffeeShop.getCoffeeshopTitle());
                ps.setString(2, currentCoffeeShop.getCoffeeshopDescription());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void update(Coffeeshop coffeeshop) {
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_COFFEESHOPS)) {
            ps.setString(1, coffeeshop.getCoffeeshopTitle());
            ps.setString(2, coffeeshop.getCoffeeshopDescription());
            ps.setLong(3, coffeeshop.getId());
            ps.execute();
        } catch (ConnectionDBException |SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void delete(Coffeeshop coffeeshop) {
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_COFFEESHOP)) {
            ps.setLong(1, coffeeshop.getId());
            ps.execute();
        } catch (ConnectionDBException |SQLException e) {
            System.err.println(e.getMessage());
        }
    }
    @Override
    public List<Coffeeshop> findAll() {
        List<Coffeeshop> resultAddCoffeeshops = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL_COFFEESHOPS);
             ResultSet result = ps.executeQuery()) {

            while (result.next()) {
                Coffeeshop addCoffeeshop = new Coffeeshop();
                addCoffeeshop.setId(result.getLong(1));
                addCoffeeshop.setCoffeeshopTitle(result.getString(2));
                addCoffeeshop.setCoffeeshopDescription(result.getString(3));
                resultAddCoffeeshops.add(addCoffeeshop);
            }
            return resultAddCoffeeshops;
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
        return resultAddCoffeeshops;
    }

    @Override
    public List<Coffeeshop> findAllCoffeeshopsFromStaff(long coffeeshop_Id) {
        List<Coffeeshop> resultCoffeeshops = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_ALL_COFFEESHOPS_FROM_STAFF)) {

            ps.setLong(1,coffeeshop_Id);

            try (ResultSet result = ps.executeQuery()) {
                while (result.next()) {
                    Coffeeshop addCoffeeshop = new Coffeeshop();
                    addCoffeeshop.setId(result.getLong(1));
                    addCoffeeshop.setCoffeeshopTitle(result.getString(2));
                    addCoffeeshop.setCoffeeshopDescription(result.getString(3));
                    resultCoffeeshops.add(addCoffeeshop);
                }
                return resultCoffeeshops;
            }
        } catch (ConnectionDBException | SQLException e) {
            System.err.println(e.getMessage());
        }
        return resultCoffeeshops;
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
