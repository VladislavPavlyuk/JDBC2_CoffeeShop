package org.example.dao.exception;

import org.example.exception.ConnectionDBException;

import java.sql.SQLException;

// handles exceptions from DAO layer
public class ExceptionHandler {
    
    private ExceptionHandler() {
    }
    
    // converts different exceptions to DaoException
    public static DaoException handleException(Exception e) {
        if (e instanceof ConnectionDBException) {
            return new DaoException("Database connection error", e);
        } else if (e instanceof SQLException) {
            return new DaoException("SQL error occurred", e);
        } else {
            return new DaoException("Unexpected error in DAO layer", e);
        }
    }
    
    public static void handleAndLog(Exception e, String operation) {
        DaoException daoException = handleException(e);
        System.err.println("Error during " + operation + ": " + daoException.getMessage());
        if (daoException.getCause() != null) {
            daoException.getCause().printStackTrace();
        }
    }
}



