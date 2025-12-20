package org.example;

import org.example.service.CoffeeshopInitializer;

public class App {
    public static void main(String[] args) {
        System.setProperty("test", "false");

        // check command line arguments
        boolean skipInit = false;
        boolean showHelp = false;
        
        for (String arg : args) {
            if (arg.equals("--skip-init") || arg.equals("-s")) {
                skipInit = true;
            } else if (arg.equals("--help") || arg.equals("-h")) {
                showHelp = true;
            }
        }

        if (showHelp) {
            showHelp();
            return;
        }

        try {
            if (!skipInit) {
                CoffeeshopInitializer coffeeshop = new CoffeeshopInitializer();
                coffeeshop.coffeeshopInitialize();
            } else {
                System.out.println("Skipping database initialization (--skip-init flag)");
            }
            
            // start menu if init was successful or skipped
            org.example.menu.MenuExecutor.startMenu();
        } catch (Exception e) {
            System.err.println("Application failed to start: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void showHelp() {
        System.out.println("Coffee Shop Application");
        System.out.println("\nUsage: java -jar app.jar [options]");
        System.out.println("\nOptions:");
        System.out.println("  --skip-init, -s    Skip database initialization");
        System.out.println("  --help, -h         Show this help message");
        System.out.println("\nExamples:");
        System.out.println("  java -jar app.jar              # Normal startup with DB init");
        System.out.println("  java -jar app.jar --skip-init # Skip DB initialization");
    }
}
