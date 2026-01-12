package org.example;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.example.service.CoffeeshopInitializer;

public class App {
    public static void main(String[] args) {
        // Set UTF-8 encoding for console output
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
        
        System.setProperty("test", "false");

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
            
            org.example.menu.MenuExecutor.startMenu();
        } catch (Exception e) {
            System.err.println("Application failed to start: " + e.getMessage());
            if (System.getProperty("debug") != null && e.getCause() != null) {
                System.err.println("Cause: " + e.getCause().getMessage());
            }
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
