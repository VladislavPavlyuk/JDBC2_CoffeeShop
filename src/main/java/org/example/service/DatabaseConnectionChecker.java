package org.example.service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import org.example.dao.ConnectionFactory;
import org.example.exception.ConnectionDBException;

// checks if database is available
public class DatabaseConnectionChecker {

    private DatabaseConnectionChecker() {
    }

    public static boolean isDatabaseAvailable() {
        try (Connection conn = ConnectionFactory.getInstance().getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            String dbProductName = metaData.getDatabaseProductName();
            String dbProductVersion = metaData.getDatabaseProductVersion();
            System.out.println("[OK] Database connection successful!");
            System.out.println("  Database: " + dbProductName + " " + dbProductVersion);
            return true;
        } catch (ConnectionDBException e) {
            System.err.println("[FAIL] Database connection failed!");
            System.err.println("  Error: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("  Cause: " + e.getCause().getMessage());
            }
            return false;
        } catch (SQLException e) {
            System.err.println("[FAIL] Database connection failed!");
            System.err.println("  SQL Error: " + e.getMessage());
            return false;
        }
    }

    public static void checkDatabaseConnection() throws ConnectionDBException {
        Connection conn = null;
        try {
            conn = ConnectionFactory.getInstance().getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            String dbProductName = metaData.getDatabaseProductName();
            System.out.println("[OK] Database connection successful! (" + dbProductName + ")");
        } catch (ConnectionDBException e) {
            System.err.println("[FAIL] Cannot connect to database!");
            System.err.println("  Error: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("  Cause: " + e.getCause().getMessage());
                System.err.println("  SQL State: " + (e.getCause() instanceof SQLException ? ((SQLException)e.getCause()).getSQLState() : "N/A"));
            }
            System.err.println("  Please ensure:");
            System.err.println("  1. PostgreSQL server is running");
            System.err.println("  2. Database 'coffeeshop_db' exists");
            System.err.println("  3. Connection settings in config.properties are correct");
            System.err.println("  4. Port 5432 is accessible (or change port in config.properties)");
            throw e;
        } catch (SQLException e) {
            throw new ConnectionDBException("SQL error during connection check: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                }
            }
        }
    }
}

