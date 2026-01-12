package org.example.service;

import org.example.dao.ConnectionFactory;
import org.example.dao.coffeeshopDAO.CoffeeshopDao;
import org.example.dao.coffeeshopDAO.CoffeeshopDaoImpl;
import org.example.dao.shiftDAO.ShiftDao;
import org.example.dao.shiftDAO.ShiftDaoImpl;
import org.example.dao.staffDAO.StaffDao;
import org.example.dao.staffDAO.StaffDaoImpl;
import org.example.dao.staffAndCoffeeshopDAO.StaffToCoffeeshopDao;
import org.example.dao.staffAndCoffeeshopDAO.StaffToCoffeeshopDaoImpl;
import org.example.exception.ConnectionDBException;
import org.example.exception.FileException;
import org.example.model.Coffeeshop;
import org.example.model.Shift;
import org.example.model.Staff;
import org.example.model.StaffToCoffeeshop;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CoffeeshopDbInitializer {

    private static final Random RANDOM_GENERATOR = new Random();
    private static final List<String> TABLES_NAME_ARRAY;
    private static final String SQL_SCRIPT_CREATE_TABLES;

    static {
        SQL_SCRIPT_CREATE_TABLES = PropertyFactory.getInstance().getProperty().getProperty("db.sqlScriptCreateTables");

        String tablesNames = PropertyFactory.getInstance().getProperty().getProperty("db.tablesNames");
        TABLES_NAME_ARRAY = Arrays.stream(tablesNames.split(",")).collect(Collectors.toList());
    }

    public static void createTables() {
        try {
            try (Connection conn = ConnectionFactory.getInstance().makeConnection()) {
                String sql = Files.lines(Paths.get(SQL_SCRIPT_CREATE_TABLES))
                    .collect(Collectors.joining("\n"));
                
                List<String> statements = splitSqlStatements(sql);
                
                try (Statement stmt = conn.createStatement()) {
                    int successCount = 0;
                    int errorCount = 0;
                    List<String> failedStatements = new ArrayList<>();
                    
                    for (String statement : statements) {
                        String trimmed = statement.trim();
                        if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                            String tableName = extractTableName(trimmed);
                            boolean isCreateTable = trimmed.toUpperCase().contains("CREATE TABLE");
                            try {
                                stmt.execute(trimmed);
                                successCount++;
                                if (isCreateTable && tableName != null) {
                                    System.out.println("Successfully created table: " + tableName);
                                }
                            } catch (SQLException e) {
                                errorCount++;
                                String errorMsg = e.getMessage();
                                if (errorMsg != null) {
                                    if (errorMsg.contains("already exists") || 
                                        errorMsg.contains("duplicate key") ||
                                        errorMsg.contains("ON CONFLICT")) {
                                        if (tableName != null && trimmed.toUpperCase().contains("CREATE TABLE")) {
                                            System.out.println("Table '" + tableName + "' already exists (skipping)");
                                        }
                                    } else if (errorMsg.contains("does not exist") && 
                                               trimmed.toUpperCase().contains("CREATE TABLE")) {
                                        failedStatements.add(trimmed);
                                        if (tableName != null) {
                                            System.out.println("Deferred table creation (dependency missing): " + tableName);
                                        }
                                    } else {
                                        if (tableName != null) {
                                            System.err.println("SQL Error creating table " + tableName + ": " + errorMsg);
                                        } else {
                                            System.err.println("SQL Error: " + errorMsg);
                                            System.err.println("Statement: " + (trimmed.length() > 100 ? trimmed.substring(0, 100) + "..." : trimmed));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    // second pass - retry failed tables
                    if (!failedStatements.isEmpty()) {
                        System.out.println("Retrying creation of " + failedStatements.size() + " tables with dependencies...");
                        for (String statement : failedStatements) {
                            try {
                                stmt.execute(statement);
                                successCount++;
                                String tableName = extractTableName(statement);
                                if (tableName != null) {
                                    System.out.println("Successfully created table (retry): " + tableName);
                                }
                            } catch (SQLException e) {
                                String errorMsg = e.getMessage();
                                if (errorMsg != null) {
                                    String tableName = extractTableName(statement);
                                    if (errorMsg.contains("already exists")) {
                                        if (tableName != null) {
                                            System.out.println("Table '" + tableName + "' already exists (retry)");
                                        }
                                    } else {
                                        if (tableName != null) {
                                            System.err.println("Warning: Still failed to create table '" + tableName + "': " + errorMsg);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    System.out.println("Tables creation completed. Success: " + successCount + ", Errors: " + errorCount);
                }
            } catch (IOException exception) {
                throw new FileException("Error reading createTables.sql: " + exception.getMessage());
            } catch (SQLException e) {
                System.err.println("SQL Error executing script: " + e.getMessage());
            }
        } catch (ConnectionDBException | FileException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void createTablesForTests() {
        try {
            try (Connection conn = ConnectionFactory.getInstance().makeConnection()) {
                String sql = Files.lines(Paths.get(SQL_SCRIPT_CREATE_TABLES))
                    .collect(Collectors.joining("\n"));
                
                List<String> statements = splitSqlStatements(sql);
                
                try (Statement stmt = conn.createStatement()) {
                    int successCount = 0;
                    int errorCount = 0;
                    List<String> failedStatements = new ArrayList<>();
                    
                    for (String statement : statements) {
                        String trimmed = statement.trim();
                        if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                            String tableName = extractTableName(trimmed);
                            boolean isCreateTable = trimmed.toUpperCase().contains("CREATE TABLE");
                            try {
                                stmt.execute(trimmed);
                                successCount++;
                                if (isCreateTable && tableName != null) {
                                    System.out.println("Successfully created table: " + tableName);
                                }
                            } catch (SQLException e) {
                                errorCount++;
                                String errorMsg = e.getMessage();
                                if (errorMsg != null) {
                                    if (errorMsg.contains("already exists") || 
                                        errorMsg.contains("duplicate key") ||
                                        errorMsg.contains("ON CONFLICT")) {
                                        if (tableName != null && trimmed.toUpperCase().contains("CREATE TABLE")) {
                                            System.out.println("Table '" + tableName + "' already exists (skipping)");
                                        }
                                    } else if (errorMsg.contains("does not exist") && 
                                               trimmed.toUpperCase().contains("CREATE TABLE")) {
                                        failedStatements.add(trimmed);
                                        if (tableName != null) {
                                            System.out.println("Deferred table creation (dependency missing): " + tableName);
                                        }
                                    } else {
                                        if (tableName != null) {
                                            System.err.println("SQL Error creating table " + tableName + ": " + errorMsg);
                                        } else {
                                            System.err.println("SQL Error: " + errorMsg);
                                            System.err.println("Statement: " + (trimmed.length() > 100 ? trimmed.substring(0, 100) + "..." : trimmed));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    // second pass - retry failed tables
                    if (!failedStatements.isEmpty()) {
                        System.out.println("Retrying creation of " + failedStatements.size() + " tables with dependencies...");
                        for (String statement : failedStatements) {
                            try {
                                stmt.execute(statement);
                                successCount++;
                                String tableName = extractTableName(statement);
                                if (tableName != null) {
                                    System.out.println("Successfully created table (retry): " + tableName);
                                }
                            } catch (SQLException e) {
                                String errorMsg = e.getMessage();
                                if (errorMsg != null) {
                                    String tableName = extractTableName(statement);
                                    if (errorMsg.contains("already exists")) {
                                        if (tableName != null) {
                                            System.out.println("Table '" + tableName + "' already exists (retry)");
                                        }
                                    } else {
                                        if (tableName != null) {
                                            System.err.println("Warning: Still failed to create table '" + tableName + "': " + errorMsg);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    System.out.println("Tables creation completed. Success: " + successCount + ", Errors: " + errorCount);
                }
            } catch (IOException exception) {
                throw new FileException("Error reading createTables.sql: " + exception.getMessage());
            } catch (SQLException e) {
                System.err.println("SQL Error executing script: " + e.getMessage());
            }
        } catch (ConnectionDBException | FileException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void deleteAllRowsInDB() throws ConnectionDBException {
        try (Connection conn = ConnectionFactory.getInstance().makeConnection()) {
            conn.setAutoCommit(false);
            try {
                String[] deleteOrder = {
                    "DELETE FROM order_history",
                    "DELETE FROM order_items",
                    "DELETE FROM orders",
                    "DELETE FROM staff_schedule",
                    "DELETE FROM customer_discounts",
                    "DELETE FROM customer_contacts",
                    "DELETE FROM customers",
                    "DELETE FROM price_history",
                    "DELETE FROM menu_item_translations",
                    "DELETE FROM menu_items",
                    "DELETE FROM staff_contacts",
                    "DELETE FROM staffandcoffeeshops",
                    "DELETE FROM coffeeshops",
                    "DELETE FROM staff",
                    "DELETE FROM shift_translations",
                    "DELETE FROM shifts"
                };
                
                try (Statement stmt = conn.createStatement()) {
                    for (String sql : deleteOrder) {
                        try {
                            int rowsDeleted = stmt.executeUpdate(sql);
                            if (rowsDeleted > 0) {
                                System.out.println("Deleted " + rowsDeleted + " rows from " + sql.substring(12));
                            }
                        } catch (SQLException e) {
                            String errorMsg = e.getMessage();
                            if (errorMsg != null) {
                                if (errorMsg.contains("does not exist")) {
                                    // Table doesn't exist, skip
                                    continue;
                                }
                                if (errorMsg.contains("current transaction is aborted")) {
                                    // Transaction aborted, rollback and retry
                                    conn.rollback();
                                    conn.setAutoCommit(false);
                                    // Skip remaining deletes in this transaction
                                    break;
                                }
                            }
                            System.err.println("Warning: Error deleting from table: " + sql + " - " + errorMsg);
                            // Continue with other tables even if one fails
                        }
                    }
                }
                conn.commit();
                System.out.println("All data deleted successfully");
            } catch (SQLException e) {
                conn.rollback();
                throw new ConnectionDBException("Error deleting data: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new ConnectionDBException("Cannot delete data: database connection failed", e);
        }
    }

    public static void createRandomShifts() throws ConnectionDBException {
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO shifts(shift_code, start_time, end_time) VALUES(?, ?, ?)")) {
            
            String[] timeRanges = {
                "08:00:00", "16:00:00",
                "16:00:00", "23:59:59",
                "22:00:00", "23:59:59",
                "09:00:00", "17:00:00",
                "10:00:00", "18:00:00",
                "07:00:00", "15:00:00",
                "12:00:00", "20:00:00",
                "14:00:00", "22:00:00",
                "06:00:00", "14:00:00",
                "20:00:00", "23:59:59"
            };
            
            for (int count = 0; count < 10; count++) {
                StringBuilder shiftTitle = new StringBuilder();
                shiftTitle.append((char)(RANDOM_GENERATOR.nextInt(26) + 'a'))
                        .append((char)(RANDOM_GENERATOR.nextInt(26) + 'a'))
                        .append("-")
                        .append((char)(RANDOM_GENERATOR.nextInt(10) + '0'))
                        .append((char)(RANDOM_GENERATOR.nextInt(10) + '0'));
                
                ps.setString(1, shiftTitle.toString());
                ps.setTime(2, Time.valueOf(timeRanges[count * 2]));
                ps.setTime(3, Time.valueOf(timeRanges[count * 2 + 1]));
                ps.addBatch();
            }
            
            int[] results = ps.executeBatch();
            int successCount = 0;
            for (int result : results) {
                if (result >= 0) successCount++;
            }
            System.out.println("Created " + successCount + " shifts successfully");
        } catch (SQLException e) {
            System.err.println("Error creating shifts: " + e.getMessage());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            
            // check batch exceptions
            SQLException nextException = e.getNextException();
            if (nextException != null) {
                System.err.println("Batch exception: " + nextException.getMessage());
                System.err.println("Batch SQL State: " + nextException.getSQLState());
            }
            
            if (e.getCause() != null) {
                System.err.println("Cause: " + e.getCause().getMessage());
            }
            e.printStackTrace();
            
            if (e.getSQLState() != null && e.getSQLState().startsWith("08")) {
                throw new ConnectionDBException("Cannot create shifts: database connection failed", e);
            } else {
                throw new ConnectionDBException("Cannot create shifts: SQL error (" + e.getSQLState() + ") - " + e.getMessage(), e);
            }
        } catch (Exception e) {
            System.err.println("Unexpected error creating shifts: " + e.getMessage());
            e.printStackTrace();
            throw new ConnectionDBException("Cannot create shifts: unexpected error", e);
        }
    }

    public static void createCoffeeshops() throws FileException, ConnectionDBException {
        String coffeshopFileName = PropertyFactory.getInstance().getProperty().getProperty("data.coffeeshops");
        
        if (coffeshopFileName == null || coffeshopFileName.isEmpty()) {
            throw new FileException("Coffeeshop file path is not configured");
        }
        
        CoffeeshopDao coffeeshopDao = new CoffeeshopDaoImpl();

        List<Coffeeshop> coffeeshops = new ArrayList<>();
        try (Stream<String> lineStream = Files.lines(Paths.get(coffeshopFileName))) {
            for (var currentString : lineStream.collect(Collectors.toList())) {
                if (currentString != null && !currentString.trim().isEmpty()) {
                    Coffeeshop coffeeshop = new Coffeeshop();
                    coffeeshop.setCoffeeshopTitle(currentString.trim());
                    coffeeshop.setCoffeeshopDescription("This is " + currentString + " description");

                    coffeeshops.add(coffeeshop);
                }
            }
            
            if (coffeeshops.isEmpty()) {
                throw new FileException("Coffeeshop file is empty: " + coffeshopFileName);
            }
            
            try {
                coffeeshopDao.saveMany(coffeeshops);
            } catch (Exception e) {
                if (e.getCause() instanceof ConnectionDBException) {
                    throw new ConnectionDBException("Cannot create coffeeshops: database connection failed", e);
                }
                throw e;
            }
        } catch (IOException exception) {
            throw new FileException("Error reading coffeeshop file: " + coffeshopFileName + " - " + exception.getMessage());
        }
    }

    public static void createRandomStaff() throws FileException, ConnectionDBException {
        TxtFileReader txtFileReaderNames = new TxtFileReader("data.names");
        List<String> randomNames = txtFileReaderNames.readFile();
        TxtFileReader txtFileReaderLastNames = new TxtFileReader("data.lastnames");
        List<String> randomLastNames = txtFileReaderLastNames.readFile();

        if (randomNames == null || randomNames.isEmpty()) {
            throw new FileException("Names file is empty or could not be read");
        }
        if (randomLastNames == null || randomLastNames.isEmpty()) {
            throw new FileException("Last names file is empty or could not be read");
        }

        ShiftDao shiftDao = new ShiftDaoImpl();
        List<Shift> shifts = shiftDao.findAll();

        if (shifts == null || shifts.isEmpty()) {
            throw new IllegalStateException("No shifts found. Please create shifts first.");
        }

        List<Long> positionIds = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id FROM positions WHERE is_active = TRUE");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                positionIds.add(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new ConnectionDBException("Cannot get positions: " + e.getMessage(), e);
        }
        
        if (positionIds.isEmpty()) {
            throw new IllegalStateException("No positions found. Please create positions first.");
        }

        StaffDao staffDAO = new StaffDaoImpl();

        int maxStaffCount = RANDOM_GENERATOR.nextInt(5,15);

        List<Staff> staffToAdd = new ArrayList<>();
        for (int count = 0; count < maxStaffCount; count++) {
            Staff addStaff = new Staff();
            // limit names to 50 chars (database field size)
            String firstNameLine = randomNames.get(RANDOM_GENERATOR.nextInt(randomNames.size()));
            String lastNameLine = randomLastNames.get(RANDOM_GENERATOR.nextInt(randomLastNames.size()));
            // extract only the name part (before first dash if exists)
            String firstName = firstNameLine.contains(" - ") ? firstNameLine.split(" - ")[0].trim() : firstNameLine.trim();
            String lastName = lastNameLine.contains(" - ") ? lastNameLine.split(" - ")[0].trim() : lastNameLine.trim();
            addStaff.setFirstName(firstName.length() > 50 ? firstName.substring(0, 50) : firstName);
            addStaff.setLastName(lastName.length() > 50 ? lastName.substring(0, 50) : lastName);
            addStaff.setPositionId(positionIds.get(RANDOM_GENERATOR.nextInt(positionIds.size())));
            addStaff.setShift_Id(shifts.get(RANDOM_GENERATOR.nextInt(shifts.size())).getId());

            staffToAdd.add(addStaff);
        }
        
        try {
            staffDAO.saveMany(staffToAdd);
        } catch (Exception e) {
            if (e.getCause() instanceof ConnectionDBException) {
                throw new ConnectionDBException("Cannot create staff: database connection failed", e);
            }
            throw e;
        }
    }

    public static void assignStaffToCoffeeshops() throws ConnectionDBException {
        try {
            CoffeeshopDao coffeeshopDao = new CoffeeshopDaoImpl();
            List<Coffeeshop> coffeeshops = coffeeshopDao.findAll();
            
            if (coffeeshops == null || coffeeshops.isEmpty()) {
                throw new IllegalStateException("No coffeeshops found. Please create coffeeshops first.");
            }
            
            StaffDao staffDao = new StaffDaoImpl();
            List<Staff> staff = staffDao.findAll();
            
            if (staff == null || staff.isEmpty()) {
                throw new IllegalStateException("No staff found. Please create staff first.");
            }

            StaffToCoffeeshopDao staffToCoffeeshopDao = new StaffToCoffeeshopDaoImpl();
            for (var currentStaff : staff) {
                int numCoffeeshops = RANDOM_GENERATOR.nextInt(2) + 1;
                Set<Long> assignedCoffeeshops = new HashSet<>();
                for (int count = 0; count < numCoffeeshops; count++) {
                    Long coffeeshopId;
                    int attempts = 0;
                    do {
                        coffeeshopId = coffeeshops.get(RANDOM_GENERATOR.nextInt(coffeeshops.size())).getId();
                        attempts++;
                    } while (assignedCoffeeshops.contains(coffeeshopId) && attempts < 10);
                    
                    if (!assignedCoffeeshops.contains(coffeeshopId)) {
                        assignedCoffeeshops.add(coffeeshopId);
                        StaffToCoffeeshop staffToCoffeeshop = new StaffToCoffeeshop();
                        staffToCoffeeshop.setStaff_Id(currentStaff.getId());
                        staffToCoffeeshop.setCoffeeshop_Id(coffeeshopId);
                        staffToCoffeeshopDao.save(staffToCoffeeshop);
                    }
                }
            }
        } catch (Exception e) {
            if (e.getCause() instanceof ConnectionDBException) {
                throw new ConnectionDBException("Cannot assign staff to coffeeshops: database connection failed", e);
            }
            if (e instanceof IllegalStateException) {
                throw e;
            }
            throw new RuntimeException("Error assigning staff to coffeeshops: " + e.getMessage(), e);
        }
    }

    // splits SQL into statements (handles $$ blocks for PostgreSQL functions)
    private static List<String> splitSqlStatements(String sql) {
        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int i = 0;
        boolean inComment = false;
        
        while (i < sql.length()) {
            char c = sql.charAt(i);
            
            if (c == '-' && i + 1 < sql.length() && sql.charAt(i + 1) == '-') {
                while (i < sql.length() && sql.charAt(i) != '\n') {
                    i++;
                }
                if (i < sql.length()) {
                    i++;
                }
                continue;
            }
            
            if (c == '$' && i + 1 < sql.length() && sql.charAt(i + 1) == '$') {
                int start = i;
                i += 2;
                while (i < sql.length()) {
                    if (sql.charAt(i) == '$' && i + 1 < sql.length() && sql.charAt(i + 1) == '$') {
                        current.append(sql.substring(start, i + 2));
                        i += 2;
                        break;
                    }
                    i++;
                }
                continue;
            }
            
            if (c == ';') {
                String statement = current.toString().trim();
                while (statement.startsWith("--")) {
                    int newlinePos = statement.indexOf('\n');
                    if (newlinePos >= 0) {
                        statement = statement.substring(newlinePos + 1).trim();
                    } else {
                        statement = "";
                        break;
                    }
                }
                if (!statement.isEmpty() && !statement.startsWith("--")) {
                    statements.add(statement);
                }
                current = new StringBuilder();
            } else {
                current.append(c);
            }
            i++;
        }
        
        String last = current.toString().trim();
        while (last.startsWith("--")) {
            int newlinePos = last.indexOf('\n');
            if (newlinePos >= 0) {
                last = last.substring(newlinePos + 1).trim();
            } else {
                last = "";
                break;
            }
        }
        if (!last.isEmpty() && !last.startsWith("--")) {
            statements.add(last);
        }
        
        return statements;
    }

    private static String extractTableName(String sql) {
        if (sql == null) return null;
        String upper = sql.toUpperCase().trim();
        if (upper.startsWith("CREATE TABLE")) {
            int start = upper.indexOf("TABLE");
            if (start >= 0) {
                String afterTable = sql.substring(start + 5).trim();
                if (afterTable.toUpperCase().startsWith("IF NOT EXISTS")) {
                    afterTable = afterTable.substring(13).trim();
                }
                int end = afterTable.indexOf(' ');
                if (end > 0) {
                    return afterTable.substring(0, end).trim();
                }
                return afterTable.trim();
            }
        }
        return null;
    }

    private static boolean tableExists(String tableName) throws ConnectionDBException {
        try (Connection connection = ConnectionFactory.getInstance().makeConnection()){
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet resultSet = meta.getTables(null, null, tableName, new String[]{"TABLE"});
            return resultSet.next();
        } catch (SQLException exception) {
            throw new ConnectionDBException("error connection to DB");
        }
    }

    public static void createMenuItems() throws FileException, ConnectionDBException {
        String drinksFile = PropertyFactory.getInstance().getProperty().getProperty("data.drinksAndBeverages");
        String dessertsFile = PropertyFactory.getInstance().getProperty().getProperty("data.deserts");
        
        if (drinksFile == null || drinksFile.isEmpty()) {
            throw new FileException("Drinks file path is not configured");
        }
        if (dessertsFile == null || dessertsFile.isEmpty()) {
            throw new FileException("Desserts file path is not configured");
        }
        
        try (Connection conn = ConnectionFactory.getInstance().makeConnection()) {
            conn.setAutoCommit(false);
            try {
                Long drinkTypeId = getTypeId(conn, "DRINK");
                Long dessertTypeId = getTypeId(conn, "DESSERT");
                Long langEnId = getLanguageId(conn, "en");
                Long langUkId = getLanguageId(conn, "uk");
                
                int itemCodeCounter = 1;
                
                try (Stream<String> lines = Files.lines(Paths.get(drinksFile))) {
                    for (String line : lines.collect(Collectors.toList())) {
                        if (line == null || line.trim().isEmpty()) continue;
                        
                        String[] parts = line.split(" - ");
                        String nameEn = parts[0].trim();
                        String nameUk = parts.length > 1 ? parts[1].trim() : nameEn;
                        
                        String itemCode = "DRINK" + String.format("%03d", itemCodeCounter++);
                        Long menuItemId = insertMenuItem(conn, drinkTypeId, itemCode, 50.00 + RANDOM_GENERATOR.nextDouble() * 200.00);
                        insertMenuItemTranslation(conn, menuItemId, langEnId, nameEn, "Delicious " + nameEn);
                        insertMenuItemTranslation(conn, menuItemId, langUkId, nameUk, "Смачний " + nameUk);
                    }
                }
                
                itemCodeCounter = 1;
                try (Stream<String> lines = Files.lines(Paths.get(dessertsFile))) {
                    for (String line : lines.collect(Collectors.toList())) {
                        if (line == null || line.trim().isEmpty()) continue;
                        
                        String nameEn = line.trim();
                        String nameUk = nameEn;
                        
                        String itemCode = "DESSERT" + String.format("%03d", itemCodeCounter++);
                        Long menuItemId = insertMenuItem(conn, dessertTypeId, itemCode, 30.00 + RANDOM_GENERATOR.nextDouble() * 150.00);
                        insertMenuItemTranslation(conn, menuItemId, langEnId, nameEn, "Delicious " + nameEn);
                        insertMenuItemTranslation(conn, menuItemId, langUkId, nameUk, "Смачний " + nameUk);
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (IOException e) {
            throw new FileException("Error reading menu items files: " + e.getMessage());
        } catch (SQLException e) {
            throw new ConnectionDBException("Error creating menu items: " + e.getMessage() + " (SQL State: " + e.getSQLState() + ")", e);
        }
    }
    
    private static Long getTypeId(Connection conn, String typeCode) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM menu_item_types WHERE type_code = ?")) {
            ps.setString(1, typeCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        throw new SQLException("Type not found: " + typeCode);
    }
    
    private static Long getLanguageId(Connection conn, String langCode) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM languages WHERE code = ?")) {
            ps.setString(1, langCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        throw new SQLException("Language not found: " + langCode);
    }
    
    private static Long insertMenuItem(Connection conn, Long typeId, String itemCode, double price) throws SQLException {
        // Check if item already exists
        String checkSql = "SELECT id FROM menu_items WHERE item_code = ?";
        try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
            checkPs.setString(1, itemCode);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()) {
                    Long existingId = rs.getLong(1);
                    // Temporarily disable trigger to avoid price_history issues during initialization
                    try (Statement disableTrigger = conn.createStatement()) {
                        disableTrigger.execute("ALTER TABLE menu_items DISABLE TRIGGER log_menu_item_price_change");
                    } catch (SQLException e) {
                        // Trigger might not exist, ignore
                    }
                    try {
                        // Update existing item to ensure it's active and has correct price
                        String updateSql = "UPDATE menu_items SET base_price = ?, is_available = TRUE, is_active = TRUE WHERE id = ?";
                        try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                            updatePs.setBigDecimal(1, java.math.BigDecimal.valueOf(price));
                            updatePs.setLong(2, existingId);
                            updatePs.executeUpdate();
                        }
                    } finally {
                        // Re-enable trigger
                        try (Statement enableTrigger = conn.createStatement()) {
                            enableTrigger.execute("ALTER TABLE menu_items ENABLE TRIGGER log_menu_item_price_change");
                        } catch (SQLException e) {
                            // Ignore
                        }
                    }
                    return existingId;
                }
            }
        }
        
        // Item doesn't exist, insert it
        String sql = "INSERT INTO menu_items (type_id, item_code, base_price, is_available, is_active, sort_order) " +
                     "VALUES (?, ?, ?, TRUE, TRUE, 0) RETURNING id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, typeId);
            ps.setString(2, itemCode);
            ps.setBigDecimal(3, java.math.BigDecimal.valueOf(price));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        throw new SQLException("Failed to insert menu item");
    }
    
    private static void insertMenuItemTranslation(Connection conn, Long menuItemId, Long langId, String name, String description) throws SQLException {
        String sql = "INSERT INTO menu_item_translations (menu_item_id, language_id, name, description) " +
                     "VALUES (?, ?, ?, ?) ON CONFLICT (menu_item_id, language_id) DO UPDATE SET name = EXCLUDED.name, description = EXCLUDED.description";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, menuItemId);
            ps.setLong(2, langId);
            ps.setString(3, name.length() > 100 ? name.substring(0, 100) : name);
            ps.setString(4, description);
            ps.executeUpdate();
        }
    }
    
    public static void createRandomCustomers() throws FileException, ConnectionDBException {
        TxtFileReader txtFileReaderNames = new TxtFileReader("data.names");
        List<String> randomNames = txtFileReaderNames.readFile();
        TxtFileReader txtFileReaderLastNames = new TxtFileReader("data.lastnames");
        List<String> randomLastNames = txtFileReaderLastNames.readFile();
        
        if (randomNames == null || randomNames.isEmpty()) {
            throw new FileException("Names file is empty or could not be read");
        }
        if (randomLastNames == null || randomLastNames.isEmpty()) {
            throw new FileException("Last names file is empty or could not be read");
        }
        
        // Ensure customer_contacts table exists
        try (Connection conn = ConnectionFactory.getInstance().makeConnection()) {
            if (!tableExists("customer_contacts")) {
                System.out.println("Creating customer_contacts table...");
                String createTableSql = 
                    "CREATE TABLE IF NOT EXISTS customer_contacts (" +
                    "id SERIAL PRIMARY KEY, " +
                    "customer_id INTEGER NOT NULL, " +
                    "contact_type VARCHAR(20) NOT NULL CHECK (contact_type IN ('PHONE', 'EMAIL', 'ADDRESS')), " +
                    "contact_value VARCHAR(255) NOT NULL, " +
                    "is_primary BOOLEAN DEFAULT FALSE, " +
                    "is_active BOOLEAN DEFAULT TRUE, " +
                    "FOREIGN KEY (customer_id) REFERENCES customers (id) ON DELETE CASCADE" +
                    ")";
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(createTableSql);
                    // Create unique index separately
                    String createIndexSql = 
                        "CREATE UNIQUE INDEX IF NOT EXISTS idx_customer_contacts_unique_active " +
                        "ON customer_contacts (customer_id, contact_type, contact_value) " +
                        "WHERE is_active = TRUE";
                    stmt.execute(createIndexSql);
                    System.out.println("customer_contacts table created successfully");
                }
            }
        } catch (SQLException e) {
            System.err.println("Warning: Could not ensure customer_contacts table exists: " + e.getMessage());
            e.printStackTrace();
        }
        
        int customerCount = RANDOM_GENERATOR.nextInt(10, 25);
        
        try (Connection conn = ConnectionFactory.getInstance().makeConnection()) {
            conn.setAutoCommit(false);
            try {
                for (int i = 0; i < customerCount; i++) {
                    String firstNameLine = randomNames.get(RANDOM_GENERATOR.nextInt(randomNames.size()));
                    String lastNameLine = randomLastNames.get(RANDOM_GENERATOR.nextInt(randomLastNames.size()));
                    String firstName = firstNameLine.contains(" - ") ? firstNameLine.split(" - ")[0].trim() : firstNameLine.trim();
                    String lastName = lastNameLine.contains(" - ") ? lastNameLine.split(" - ")[0].trim() : lastNameLine.trim();
                    firstName = firstName.length() > 50 ? firstName.substring(0, 50) : firstName;
                    lastName = lastName.length() > 50 ? lastName.substring(0, 50) : lastName;
                    
                    java.sql.Date birthDate = java.sql.Date.valueOf(
                        java.time.LocalDate.now().minusYears(18 + RANDOM_GENERATOR.nextInt(50))
                            .minusDays(RANDOM_GENERATOR.nextInt(365))
                    );
                    
                    String sql = "INSERT INTO customers (firstname, lastname, date_of_birth, is_active) VALUES (?, ?, ?, TRUE) RETURNING id";
                    Long customerId;
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, firstName);
                        ps.setString(2, lastName);
                        ps.setDate(3, birthDate);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                customerId = rs.getLong(1);
                            } else {
                                continue;
                            }
                        }
                    }
                    
                    if (RANDOM_GENERATOR.nextDouble() > 0.3) {
                        String phone = "+7 (9" + RANDOM_GENERATOR.nextInt(10) + RANDOM_GENERATOR.nextInt(10) + ") " +
                                      RANDOM_GENERATOR.nextInt(10) + RANDOM_GENERATOR.nextInt(10) + RANDOM_GENERATOR.nextInt(10) + "-" +
                                      RANDOM_GENERATOR.nextInt(10) + RANDOM_GENERATOR.nextInt(10) + "-" +
                                      RANDOM_GENERATOR.nextInt(10) + RANDOM_GENERATOR.nextInt(10);
                        insertCustomerContact(conn, customerId, "PHONE", phone, true);
                    }
                    
                    if (RANDOM_GENERATOR.nextDouble() > 0.4) {
                        String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@email.com";
                        insertCustomerContact(conn, customerId, "EMAIL", email, true);
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new ConnectionDBException("Error creating customers: " + e.getMessage() + " (SQL State: " + e.getSQLState() + ")", e);
        }
    }
    
    private static void insertCustomerContact(Connection conn, Long customerId, String contactType, String contactValue, boolean isPrimary) throws SQLException {
        String checkSql = "SELECT id FROM customer_contacts WHERE customer_id = ? AND contact_type = ? AND contact_value = ? AND is_active = TRUE";
        try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
            checkPs.setLong(1, customerId);
            checkPs.setString(2, contactType);
            checkPs.setString(3, contactValue);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()) {
                    return;
                }
            }
        }
        
        String sql = "INSERT INTO customer_contacts (customer_id, contact_type, contact_value, is_primary, is_active) " +
                     "VALUES (?, ?, ?, ?, TRUE)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, customerId);
            ps.setString(2, contactType);
            ps.setString(3, contactValue);
            ps.setBoolean(4, isPrimary);
            ps.executeUpdate();
        }
    }
    
    public static void createCustomerDiscounts() throws ConnectionDBException {
        try (Connection conn = ConnectionFactory.getInstance().makeConnection()) {
            conn.setAutoCommit(false);
            try {
                Long percentageTypeId = getDiscountTypeId(conn, "PERCENTAGE");
                
                try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM customers WHERE is_active = TRUE")) {
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            Long customerId = rs.getLong(1);
                            if (RANDOM_GENERATOR.nextDouble() > 0.5) {
                                double discountValue = 5.0 + RANDOM_GENERATOR.nextDouble() * 20.0;
                                java.sql.Date validFrom = java.sql.Date.valueOf(java.time.LocalDate.now().minusDays(RANDOM_GENERATOR.nextInt(30)));
                                
                                String sql = "INSERT INTO customer_discounts (customer_id, discount_type_id, discount_value, valid_from, valid_to, is_active) " +
                                             "VALUES (?, ?, ?, ?, NULL, TRUE) ON CONFLICT DO NOTHING";
                                try (PreparedStatement ps2 = conn.prepareStatement(sql)) {
                                    ps2.setLong(1, customerId);
                                    ps2.setLong(2, percentageTypeId);
                                    ps2.setBigDecimal(3, java.math.BigDecimal.valueOf(discountValue));
                                    ps2.setDate(4, validFrom);
                                    ps2.executeUpdate();
                                }
                            }
                        }
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new ConnectionDBException("Error creating customer discounts: " + e.getMessage() + " (SQL State: " + e.getSQLState() + ")", e);
        }
    }
    
    private static Long getDiscountTypeId(Connection conn, String typeCode) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM discount_types WHERE type_code = ?")) {
            ps.setString(1, typeCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        throw new SQLException("Discount type not found: " + typeCode);
    }
    
    public static void createRandomOrders() throws ConnectionDBException {
        try (Connection conn = ConnectionFactory.getInstance().makeConnection()) {
            List<Long> customerIds = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM customers WHERE is_active = TRUE")) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        customerIds.add(rs.getLong(1));
                    }
                }
            }
            
            List<Long> staffIds = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM staff WHERE is_active = TRUE")) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        staffIds.add(rs.getLong(1));
                    }
                }
            }
            
            Long completedStatusId = getOrderStatusId(conn, "COMPLETED");
            
            List<Long> menuItemIds = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM menu_items WHERE is_active = TRUE")) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        menuItemIds.add(rs.getLong(1));
                    }
                }
            }
            
            int orderCount = RANDOM_GENERATOR.nextInt(20, 50);
            for (int i = 0; i < orderCount; i++) {
                Long customerId = customerIds.isEmpty() ? null : customerIds.get(RANDOM_GENERATOR.nextInt(customerIds.size()));
                Long staffId = staffIds.get(RANDOM_GENERATOR.nextInt(staffIds.size()));
                
                java.sql.Timestamp orderDate = java.sql.Timestamp.valueOf(
                    java.time.LocalDateTime.now().minusDays(RANDOM_GENERATOR.nextInt(30))
                        .minusHours(RANDOM_GENERATOR.nextInt(24))
                );
                
                String orderNumber = "ORD" + String.format("%06d", i + 1);
                
                Long orderId = insertOrder(conn, orderNumber, customerId, staffId, completedStatusId, orderDate);
                
                int itemCount = RANDOM_GENERATOR.nextInt(1, 5);
                for (int j = 0; j < itemCount; j++) {
                    Long menuItemId = menuItemIds.get(RANDOM_GENERATOR.nextInt(menuItemIds.size()));
                    int quantity = RANDOM_GENERATOR.nextInt(1, 4);
                    java.math.BigDecimal unitPrice = getMenuItemPrice(conn, menuItemId);
                    java.math.BigDecimal totalPrice = unitPrice.multiply(java.math.BigDecimal.valueOf(quantity));
                    insertOrderItem(conn, orderId, menuItemId, quantity, unitPrice, totalPrice);
                }
                
                updateOrderTotal(conn, orderId);
            }
        } catch (SQLException e) {
            throw new ConnectionDBException("Error creating orders: " + e.getMessage(), e);
        }
    }
    
    private static Long getOrderStatusId(Connection conn, String statusCode) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM order_statuses WHERE status_code = ?")) {
            ps.setString(1, statusCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        throw new SQLException("Order status not found: " + statusCode);
    }
    
    private static Long insertOrder(Connection conn, String orderNumber, Long customerId, Long staffId, Long statusId, java.sql.Timestamp orderDate) throws SQLException {
        String sql = "INSERT INTO orders (order_number, customer_id, staff_id, status_id, order_date, total_amount, discount_amount, final_amount) " +
                     "VALUES (?, ?, ?, ?, ?, 0, 0, 0) RETURNING id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderNumber);
            if (customerId != null) {
                ps.setLong(2, customerId);
            } else {
                ps.setNull(2, java.sql.Types.BIGINT);
            }
            ps.setLong(3, staffId);
            ps.setLong(4, statusId);
            ps.setTimestamp(5, orderDate);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        throw new SQLException("Failed to insert order");
    }
    
    private static java.math.BigDecimal getMenuItemPrice(Connection conn, Long menuItemId) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT base_price FROM menu_items WHERE id = ?")) {
            ps.setLong(1, menuItemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        }
        return java.math.BigDecimal.valueOf(100.0);
    }
    
    private static void insertOrderItem(Connection conn, Long orderId, Long menuItemId, int quantity, java.math.BigDecimal unitPrice, java.math.BigDecimal totalPrice) throws SQLException {
        String sql = "INSERT INTO order_items (order_id, menu_item_id, quantity, unit_price, total_price) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            ps.setLong(2, menuItemId);
            ps.setInt(3, quantity);
            ps.setBigDecimal(4, unitPrice);
            ps.setBigDecimal(5, totalPrice);
            ps.executeUpdate();
        }
    }
    
    private static void updateOrderTotal(Connection conn, Long orderId) throws SQLException {
        String sql = "UPDATE orders SET total_amount = (SELECT COALESCE(SUM(total_price), 0) FROM order_items WHERE order_id = ?), " +
                     "final_amount = (SELECT COALESCE(SUM(total_price), 0) FROM order_items WHERE order_id = ?) WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            ps.setLong(2, orderId);
            ps.setLong(3, orderId);
            ps.executeUpdate();
        }
    }
    
    public static void createStaffSchedules() throws ConnectionDBException {
        try (Connection conn = ConnectionFactory.getInstance().makeConnection()) {
            conn.setAutoCommit(false);
            try {
                List<Long> staffIds = new ArrayList<>();
                try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM staff WHERE is_active = TRUE")) {
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            staffIds.add(rs.getLong(1));
                        }
                    }
                }
                
                List<Long> shiftIds = new ArrayList<>();
                try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM shifts WHERE is_active = TRUE")) {
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            shiftIds.add(rs.getLong(1));
                        }
                    }
                }
                
                if (shiftIds.isEmpty()) {
                    throw new IllegalStateException("No shifts found. Please create shifts first.");
                }
                
                java.time.LocalDate today = java.time.LocalDate.now();
                for (Long staffId : staffIds) {
                    for (int day = 0; day < 7; day++) {
                        if (RANDOM_GENERATOR.nextDouble() > 0.3) {
                            java.sql.Date workDate = java.sql.Date.valueOf(today.plusDays(day));
                            Long shiftId = shiftIds.get(RANDOM_GENERATOR.nextInt(shiftIds.size()));
                            
                            String sql = "INSERT INTO staff_schedule (staff_id, shift_id, work_date) VALUES (?, ?, ?) " +
                                         "ON CONFLICT (staff_id, shift_id, work_date) DO NOTHING";
                            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                                ps.setLong(1, staffId);
                                ps.setLong(2, shiftId);
                                ps.setDate(3, workDate);
                                ps.executeUpdate();
                            }
                        }
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } catch (IllegalStateException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new ConnectionDBException("Error creating staff schedules: " + e.getMessage() + " (SQL State: " + e.getSQLState() + ")", e);
        }
    }

    private CoffeeshopDbInitializer() {
    }
}

