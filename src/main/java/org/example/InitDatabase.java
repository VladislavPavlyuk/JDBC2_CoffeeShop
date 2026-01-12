package org.example;

import org.example.service.CoffeeshopInitializer;

public class InitDatabase {
    public static void main(String[] args) {
        System.setProperty("test", "false");
        
        System.out.println("=== Database Initialization Tool ===");
        System.out.println("This will:");
        System.out.println("  1. Create tables (if they don't exist)");
        System.out.println("  2. Delete existing data");
        System.out.println("  3. Create shifts");
        System.out.println("  4. Create coffeeshops");
        System.out.println("  5. Create staff");
        System.out.println("  6. Assign staff to coffeeshops");
        System.out.println("  7. Create menu items (drinks and desserts)");
        System.out.println("  8. Create customers");
        System.out.println("  9. Create customer discounts");
        System.out.println("  10. Create orders");
        System.out.println("  11. Create staff schedules");
        System.out.println();
        
        try {
            CoffeeshopInitializer initializer = new CoffeeshopInitializer();
            initializer.coffeeshopInitialize();
            System.out.println("\n=== Database initialization completed successfully! ===");
            System.out.println("You can now run the application.");
        } catch (Exception e) {
            System.err.println("\n=== Initialization FAILED ===");
            System.err.println("Error: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Cause: " + e.getCause().getMessage());
            }
            System.exit(1);
        }
    }
}


