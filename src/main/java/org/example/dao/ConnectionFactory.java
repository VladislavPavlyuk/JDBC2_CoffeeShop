package org.example.dao;

import static java.lang.Class.forName;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.example.exception.ConnectionDBException;
import org.example.service.PropertyFactory;

public class ConnectionFactory implements ConnectionProvider {

    private static final String DRIVER;
    private static final String DBURL;
    private static final String USER;
    private static final String PASSWORD;

    private static ConnectionFactory factory;

    static {
        Properties prop = PropertyFactory.getInstance().getProperty();
        DRIVER = prop.getProperty("db.driver");
        DBURL = prop.getProperty("db.dburl");
        USER = prop.getProperty("db.user");
        PASSWORD = prop.getProperty("db.password");
    }

    @Override
    public Connection getConnection() throws ConnectionDBException {
        try {
            if (DRIVER == null || DRIVER.isEmpty()) {
                throw new ConnectionDBException("Database driver is not configured");
            }
            if (DBURL == null || DBURL.isEmpty()) {
                throw new ConnectionDBException("Database URL is not configured");
            }
            
            forName(DRIVER);
            Properties props = new Properties();
            props.setProperty("user", USER);
            props.setProperty("password", PASSWORD);
            props.setProperty("ssl", "false");
            props.setProperty("characterEncoding", "UTF-8");
            props.setProperty("useUnicode", "true");
            return DriverManager.getConnection(DBURL, props);
        } catch (ClassNotFoundException e) {
            throw new ConnectionDBException("Database driver not found: " + DRIVER + ". Please check if PostgreSQL driver is in classpath.", e);
        } catch (SQLException e) {
            String errorMsg = String.format("Cannot connect to database at %s. Error: %s", DBURL, e.getMessage());
            throw new ConnectionDBException(errorMsg, e);
        }
    }

    @Deprecated
    public Connection makeConnection() throws ConnectionDBException {
        return getConnection();
    }

    public static ConnectionFactory getInstance() {
        if (factory == null) {
            synchronized (ConnectionFactory.class) {
                if (factory == null) {
                    factory = new ConnectionFactory();
                }
            }
        }
        return factory;
    }

    private ConnectionFactory() {
    }
}
