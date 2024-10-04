package org.example.dao;

import org.example.exception.ConnectionDBException;
import org.example.service.PropertyFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import static java.lang.Class.forName;

public class ConnectionFactory {

    private static final String DRIVER;
    private static final String DBURL;
    private static final String USER;
    private static final String PASSWORD;

    private static ConnectionFactory factory;

    private Connection conn;

    static {
        Properties prop = PropertyFactory.getInstance().getProperty();
        DRIVER = prop.getProperty("db.driver");
        DBURL = prop.getProperty("db.dburl");
        USER = prop.getProperty("db.user");
        PASSWORD = prop.getProperty("db.password");
    }

    public Connection makeConnection() throws ConnectionDBException {
        try {
            forName(DRIVER);
            conn = DriverManager.getConnection(DBURL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            throw new ConnectionDBException("Error with connect to DataBase");
        }
    return conn;
    }

    public static ConnectionFactory getInstance () {
        if (factory == null) {
            factory = new ConnectionFactory();
        }
        return factory;
    }

    private ConnectionFactory() {
    }
}
