package org.example.menu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

public class InteractiveMenu {
    
    private final List<MenuItem> menuItems;
    private int currentSelection;
    private boolean running;
    private final Scanner scanner;
    private boolean rawModeSupported;
    
    public InteractiveMenu() {
        this.menuItems = new ArrayList<>();
        this.currentSelection = 0;
        this.running = true;
        this.scanner = new Scanner(System.in);
        this.rawModeSupported = false;
        try {
            enableRawMode();
            rawModeSupported = true;
        } catch (Exception e) {
            rawModeSupported = false;
        }
    }
    
    private void enableRawMode() {
        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            try {
                ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "mode", "con:", "cols=80", "lines=25");
                pb.start();
            } catch (Exception e) {
            }
        }
    }
    
    public void addMenuItem(String title, Runnable action) {
        menuItems.add(new MenuItem(title, action, null));
    }
    
    public void addMenuItem(String title, Consumer<Scanner> action) {
        menuItems.add(new MenuItem(title, null, action));
    }
    
    public void display() {
        while (running) {
            clearScreen();
            printMenu();
            handleInput();
        }
    }
    
    private void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            System.out.print("\n".repeat(3));
        }
    }
    
    private void printMenu() {
        System.out.println("============================================================");
        System.out.println("                    COFFEESHOP MENU");
        System.out.println("============================================================");
        System.out.println("Navigation: ↑/↓ arrows or u/d to move, Enter to select, q to quit");
        System.out.println("Or enter number (1-" + menuItems.size() + ") to select directly");
        System.out.println("============================================================");
        System.out.println();
        
        for (int i = 0; i < menuItems.size(); i++) {
            if (i == currentSelection) {
                System.out.print(">>> ");
                System.out.print(String.format("%2d. %s", i + 1, menuItems.get(i).getTitle()));
                System.out.println(" <<<");
            } else {
                System.out.println(String.format("     %2d. %s", i + 1, menuItems.get(i).getTitle()));
            }
        }
        
        System.out.println();
        System.out.println("============================================================");
        System.out.print("Your choice: ");
    }
    
    private void handleInput() {
        try {
            if (rawModeSupported && System.in.available() > 0) {
                int key = readKey();
                if (key != -1) {
                    processKey(key);
                    return;
                }
            }
        } catch (IOException e) {
        }
        
        try {
            if (scanner.hasNextLine()) {
                String input = scanner.nextLine().trim().toLowerCase();
                processTextInput(input);
            } else {
                running = false;
            }
        } catch (java.util.NoSuchElementException e) {
            running = false;
        }
    }
    
    private int readKey() {
        try {
            if (System.in.available() > 0) {
                int first = System.in.read();
                if (first == 27) {
                    if (System.in.available() > 0) {
                        int second = System.in.read();
                        if (second == 91) {
                            if (System.in.available() > 0) {
                                int third = System.in.read();
                                return third;
                            }
                        }
                    }
                } else if (first == 13 || first == 10) {
                    return 13;
                } else if (first == 113 || first == 81) {
                    return 113;
                }
            }
        } catch (IOException e) {
        }
        return -1;
    }
    
    private void processKey(int key) {
        switch (key) {
            case 65: // Up arrow
                if (currentSelection > 0) {
                    currentSelection--;
                } else {
                    currentSelection = menuItems.size() - 1;
                }
                break;
            case 66: // Down arrow
                if (currentSelection < menuItems.size() - 1) {
                    currentSelection++;
                } else {
                    currentSelection = 0;
                }
                break;
            case 13: // Enter
                executeSelected();
                break;
            case 113: // Q
            case 81:
                running = false;
                System.out.println("\nExiting application. Goodbye!");
                break;
        }
    }
    
    private void processTextInput(String input) {
        if (input.isEmpty()) {
            executeSelected();
            return;
        }
        
        switch (input) {
            case "q":
            case "quit":
            case "exit":
                running = false;
                System.out.println("\nExiting application. Goodbye!");
                return;
            case "u":
            case "up":
                if (currentSelection > 0) {
                    currentSelection--;
                } else {
                    currentSelection = menuItems.size() - 1;
                }
                return;
            case "d":
            case "down":
                if (currentSelection < menuItems.size() - 1) {
                    currentSelection++;
                } else {
                    currentSelection = 0;
                }
                return;
            case "enter":
                executeSelected();
                return;
            default:
                try {
                    int num = Integer.parseInt(input);
                    if (num >= 1 && num <= menuItems.size()) {
                        currentSelection = num - 1;
                        executeSelected();
                        return;
                    } else {
                        System.out.println("Invalid number. Please enter a number between 1 and " + menuItems.size());
                        waitForEnter();
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Use ↑/↓ or u/d to navigate, Enter to select, q to quit");
                    System.out.println("Or enter number (1-" + menuItems.size() + ") to select directly");
                    waitForEnter();
                }
        }
    }
    
    private void executeSelected() {
        MenuItem item = menuItems.get(currentSelection);
        
        try {
            if (item.getAction() != null) {
                item.getAction().run();
            } else if (item.getActionWithScanner() != null) {
                item.getActionWithScanner().accept(scanner);
            }
            
            waitForEnter();
        } catch (java.util.NoSuchElementException e) {
            System.err.println("Error: Input stream closed or no input available.");
            running = false;
        } catch (Exception e) {
            System.err.println("Error executing menu item: " + e.getMessage());
            e.printStackTrace();
            waitForEnter();
        }
    }
    
    private void waitForEnter() {
        try {
            System.out.println("\nPress Enter to return to menu...");
            try {
                scanner.nextLine();
            } catch (java.util.NoSuchElementException e) {
                if (!scanner.hasNextLine()) {
                    running = false;
                    return;
                }
                scanner.nextLine();
            }
        } catch (java.util.NoSuchElementException e) {
            running = false;
        } catch (Exception e) {
        }
    }
    
    public void stop() {
        this.running = false;
    }
    
    private static class MenuItem {
        private final String title;
        private final Runnable action;
        private final Consumer<Scanner> actionWithScanner;
        
        public MenuItem(String title, Runnable action, Consumer<Scanner> actionWithScanner) {
            this.title = title;
            this.action = action;
            this.actionWithScanner = actionWithScanner;
        }
        
        public String getTitle() {
            return title;
        }
        
        public Runnable getAction() {
            return action;
        }
        
        public Consumer<Scanner> getActionWithScanner() {
            return actionWithScanner;
        }
    }
}


