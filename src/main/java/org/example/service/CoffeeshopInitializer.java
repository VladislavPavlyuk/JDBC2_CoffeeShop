package org.example.service;


import org.example.exception.ConnectionDBException;
import org.example.exception.FileException;

import static java.lang.System.setProperty;

public class CoffeeshopInitializer {

    public void coffeeshopInitialize() {
        setProperty("test", "false");
        
        try {
            // check database connection before init
            System.out.println("Checking database connection...");
            DatabaseConnectionChecker.checkDatabaseConnection();
            
            System.out.println("\n=== Starting database initialization ===");
            System.out.println("Creating database tables...");
            CoffeeshopDbInitializer.createTables();
            
            System.out.println("Deleting existing data...");
            CoffeeshopDbInitializer.deleteAllRowsInDB();
            
            System.out.println("Creating shifts...");
            CoffeeshopDbInitializer.createRandomShifts();
            
            System.out.println("Creating coffeeshops...");
            CoffeeshopDbInitializer.createCoffeeshops();
            
            System.out.println("Creating staff...");
            CoffeeshopDbInitializer.createRandomStaff();
            
            System.out.println("Assigning staff to coffeeshops...");
            try {
                CoffeeshopDbInitializer.assignStaffToCoffeeshops();
            } catch (IllegalStateException e) {
                System.err.println("Warning: " + e.getMessage());
                System.err.println("Skipping staff assignment.");
            }
            
            System.out.println("\n=== Initialization completed successfully! ===");
        } catch (ConnectionDBException e) {
            System.err.println("\n=== Initialization FAILED: Database connection error ===");
            System.err.println("Cannot proceed without database connection.");
            System.err.println("\nPlease:");
            System.err.println("1. Start PostgreSQL server");
            System.err.println("2. Create database 'coffeeshop_db' if it doesn't exist");
            System.err.println("3. Check connection settings in config.properties");
            System.err.println("4. Verify that port 5432 is correct (or change port in config.properties)");
            System.err.println("\nTo start PostgreSQL with Docker:");
            System.err.println("  docker-compose -f src/docker-compose.yml up -d");
            System.err.println("\nExample SQL to create database:");
            System.err.println("  CREATE DATABASE coffeeshop_db;");
            System.err.println("\nTo start PostgreSQL on Windows:");
            System.err.println("  net start postgresql-x64-XX");
            System.err.println("\nTo start PostgreSQL on Linux/Mac:");
            System.err.println("  sudo systemctl start postgresql");
            System.exit(1);
        } catch (FileException e) {
            System.err.println("\n=== Initialization FAILED: File error ===");
            System.err.println("Error: " + e.getMessage());
            System.err.println("Please ensure all required data files exist:");
            System.err.println("  - src/test/resources/names.txt");
            System.err.println("  - src/test/resources/lastname.txt");
            System.err.println("  - src/test/resources/coffeeshops.txt");
            e.printStackTrace();
            System.exit(1);
        } catch (IllegalStateException e) {
            System.err.println("\n=== Initialization FAILED: State error ===");
            System.err.println("Error: " + e.getMessage());
            System.err.println("Please ensure all required data files exist and database is accessible.");
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.err.println("\n=== Initialization FAILED: Unexpected error ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
