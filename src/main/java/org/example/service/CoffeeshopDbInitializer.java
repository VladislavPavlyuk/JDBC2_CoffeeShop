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
        try (Connection conn = ConnectionFactory.getInstance().makeConnection()) {
            // check if tables need to be created
            boolean needToCreate = false;
            for (var tableName : TABLES_NAME_ARRAY) {
                if (!tableExists(tableName.trim())) {
                    needToCreate = true;
                    break;
                }
            }
            
            if (!needToCreate) {
                System.out.println("All tables already exist, skipping table creation.");
                return;
            }
            
            try (Stream<String> lineStream = Files.lines(Paths.get(SQL_SCRIPT_CREATE_TABLES))) {
                StringBuilder createTablesQuery = new StringBuilder();

                for (var currentString : lineStream.collect(Collectors.toList())) {
                    String trimmed = currentString.trim();
                    if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                        createTablesQuery.append(currentString).append("\n");
                    }
                }

                String sql = createTablesQuery.toString();
                if (!sql.trim().isEmpty()) {
                    // split SQL into separate statements
                    List<String> statements = splitSqlStatements(sql);
                    try (Statement stmt = conn.createStatement()) {
                        for (String statement : statements) {
                            String trimmed = statement.trim();
                            if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                                try {
                                    stmt.execute(trimmed);
                                } catch (SQLException e) {
                                    // ignore "already exists" errors
                                    String errorMsg = e.getMessage();
                                    if (errorMsg != null && 
                                        !errorMsg.contains("already exists") && 
                                        !errorMsg.contains("duplicate key") &&
                                        !errorMsg.contains("ON CONFLICT") &&
                                        !errorMsg.contains("does not exist")) {
                                        if (!errorMsg.contains("syntax error") || trimmed.length() < 200) {
                                            System.err.println("SQL Error: " + errorMsg);
                                            if (trimmed.length() < 200) {
                                                System.err.println("Statement: " + trimmed);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (IOException exception) {
                throw new FileException("Error reading createTables.sql: " + exception.getMessage());
            } catch (SQLException e) {
                System.err.println("SQL Error executing script: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (ConnectionDBException e) {
            System.err.println("Database connection error: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("Cause: " + e.getCause().getMessage());
            }
        } catch (FileException e) {
            System.err.println("File error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
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
                    
                    // first pass - create all tables
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
                                        // ignore these errors
                                        if (tableName != null && trimmed.toUpperCase().contains("CREATE TABLE")) {
                                            System.out.println("Table '" + tableName + "' already exists (skipping)");
                                        }
                                    } else if (errorMsg.contains("does not exist") && 
                                               trimmed.toUpperCase().contains("CREATE TABLE")) {
                                        // table creation failed, save for retry
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
                                // ignore errors on retry if table already exists
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
        try {
            CoffeeshopDao coffeeshopDao = new CoffeeshopDaoImpl();
            ShiftDao shiftDao = new ShiftDaoImpl();
            StaffDao staffDao = new StaffDaoImpl();
            StaffToCoffeeshopDao staffToCoffeeshopDao = new StaffToCoffeeshopDaoImpl();
            staffToCoffeeshopDao.deleteAll();
            coffeeshopDao.deleteAll();
            staffDao.deleteAll();
            shiftDao.deleteAll();
        } catch (Exception e) {
            // if connection error, throw it
            if (e.getCause() instanceof org.example.exception.ConnectionDBException) {
                throw new ConnectionDBException("Cannot delete data: database connection failed", e);
            }
            System.err.println("Error deleting data: " + e.getMessage());
            throw e;
        }
    }

    public static void createRandomShifts() throws ConnectionDBException {
        // use direct SQL because table needs start_time and end_time
        try (Connection conn = ConnectionFactory.getInstance().makeConnection();
             PreparedStatement ps = conn.prepareStatement(
                 "INSERT INTO shifts(shift_code, start_time, end_time) VALUES(?, ?, ?)")) {
            
            // time ranges (end_time must be > start_time for CHECK constraint)
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
            
            // check if connection error (SQL State 08xxx)
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

        // check if name lists are empty
        if (randomNames == null || randomNames.isEmpty()) {
            throw new FileException("Names file is empty or could not be read");
        }
        if (randomLastNames == null || randomLastNames.isEmpty()) {
            throw new FileException("Last names file is empty or could not be read");
        }

        ShiftDao shiftDao = new ShiftDaoImpl();
        List<Shift> shifts = shiftDao.findAll();

        // check if shifts exist
        if (shifts == null || shifts.isEmpty()) {
            throw new IllegalStateException("No shifts found. Please create shifts first.");
        }

        // get positions from database
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

        List<Staff> staffToAdd = new ArrayList<>();
        for (int count = 0; count < 200; count++) {
            Staff addStaff = new Staff();
            // limit names to 50 chars (database field size)
            String firstName = randomNames.get(RANDOM_GENERATOR.nextInt(randomNames.size()));
            String lastName = randomLastNames.get(RANDOM_GENERATOR.nextInt(randomLastNames.size()));
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
                // each staff member works in 1-2 shops
                int numCoffeeshops = RANDOM_GENERATOR.nextInt(2) + 1;
                // use Set to avoid duplicate assignments
                Set<Long> assignedCoffeeshops = new HashSet<>();
                for (int count = 0; count < numCoffeeshops; count++) {
                    Long coffeeshopId;
                    int attempts = 0;
                    // try to find unique shop (max 10 tries)
                    do {
                        coffeeshopId = coffeeshops.get(RANDOM_GENERATOR.nextInt(coffeeshops.size())).getId();
                        attempts++;
                    } while (assignedCoffeeshops.contains(coffeeshopId) && attempts < 10);
                    
                    // if unique shop found, add assignment
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
            
            // handle single-line comments (-- to end of line)
            if (c == '-' && i + 1 < sql.length() && sql.charAt(i + 1) == '-') {
                while (i < sql.length() && sql.charAt(i) != '\n') {
                    i++;
                }
                if (i < sql.length()) {
                    i++;
                }
                continue;
            }
            
            // handle $$ blocks (PostgreSQL functions)
            if (c == '$' && i + 1 < sql.length() && sql.charAt(i + 1) == '$') {
                int start = i;
                i += 2;
                // find closing $$
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
            
            // handle regular statements
            if (c == ';') {
                String statement = current.toString().trim();
                // remove comments from start
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
        
        // add last statement if exists
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

    private CoffeeshopDbInitializer() {
    }
}

